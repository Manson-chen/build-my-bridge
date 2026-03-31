package com.buildmybridge.service;

import com.buildmybridge.dto.LoginResponse;
import com.buildmybridge.entity.User;
import com.buildmybridge.mapper.UserMapper;
import com.buildmybridge.security.JwtTokenProvider;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 飞书 OAuth 服务
 * 处理飞书 OAuth 登录流程
 */
@Slf4j
@Service
public class FeishuOAuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${feishu.app-id}")
    private String appId;

    @Value("${feishu.app-secret}")
    private String appSecret;

    @Value("${feishu.redirect-uri}")
    private String redirectUri;

    private static final String FEISHU_OAUTH_AUTHORIZE_URL = "https://open.feishu.cn/open-apis/oauth2/v2/authorize";
    private static final String FEISHU_OAUTH_TOKEN_URL = "https://open.feishu.cn/open-apis/oauth2/v2/token";
    private static final String FEISHU_USER_INFO_URL = "https://open.feishu.cn/open-apis/contact/v3/users/me";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成飞书 OAuth 授权 URL
     *
     * @return 授权 URL
     */
    public String generateAuthUrl() {
        String state = UUID.randomUUID().toString();
        // 缓存 state 到 Redis，10 分钟过期
        redisTemplate.opsForValue().set(
                "oauth_state:" + state,
                "pending",
                10,
                TimeUnit.MINUTES
        );

        return String.format(
                "%s?client_id=%s&redirect_uri=%s&response_type=code&state=%s",
                FEISHU_OAUTH_AUTHORIZE_URL,
                appId,
                redirectUri,
                state
        );
    }

    /**
     * 使用授权码交换 access_token
     *
     * @param code 授权码
     * @param state OAuth state
     * @return 飞书 access_token
     */
    public String exchangeAccessToken(String code, String state) {
        // 验证 state
        Boolean stateValid = redisTemplate.hasKey("oauth_state:" + state);
        if (!stateValid) {
            throw new RuntimeException("Invalid or expired state parameter");
        }

        // 清理 state
        redisTemplate.delete("oauth_state:" + state);

        try {
            // 构建请求体
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("grant_type", "authorization_code");
            requestBody.put("client_id", appId);
            requestBody.put("client_secret", appSecret);
            requestBody.put("code", code);
            requestBody.put("redirect_uri", redirectUri);

            // 发送请求
            String response = restTemplate.postForObject(
                    FEISHU_OAUTH_TOKEN_URL,
                    requestBody,
                    String.class
            );

            // 解析响应
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            if ((Integer) responseMap.get("code") != 0) {
                throw new RuntimeException("Failed to exchange access token: " + responseMap.get("msg"));
            }

            Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
            String accessToken = (String) data.get("access_token");
            Integer expiresIn = (Integer) data.get("expires_in");

            // 缓存 access_token 到 Redis
            redisTemplate.opsForValue().set(
                    "feishu_access_token",
                    accessToken,
                    expiresIn - 300,  // 提前 5 分钟刷新
                    TimeUnit.SECONDS
            );

            return accessToken;
        } catch (IOException e) {
            log.error("Failed to parse OAuth response", e);
            throw new RuntimeException("Failed to exchange access token", e);
        }
    }

    /**
     * 获取登录用户信息
     *
     * @param accessToken 飞书 access_token
     * @return 用户信息 (user_id, name)
     */
    public Map<String, String> getUserInfo(String accessToken) {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + accessToken);

            // 发送请求获取用户信息
            String response = restTemplate.getForObject(
                    FEISHU_USER_INFO_URL,
                    String.class
            );

            // 解析响应
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            if ((Integer) responseMap.get("code") != 0) {
                throw new RuntimeException("Failed to get user info: " + responseMap.get("msg"));
            }

            Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
            Map<String, Object> user = (Map<String, Object>) data.get("user");

            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("user_id", (String) user.get("user_id"));
            userInfo.put("name", (String) user.get("name"));
            userInfo.put("open_id", (String) user.get("open_id"));

            return userInfo;
        } catch (IOException e) {
            log.error("Failed to parse user info response", e);
            throw new RuntimeException("Failed to get user info", e);
        }
    }

    /**
     * OAuth 完整流程：获取 code 后的处理
     *
     * @param code 授权码
     * @param state OAuth state
     * @return LoginResponse 包含 JWT token
     */
    public LoginResponse handleOAuthCallback(String code, String state) {
        // 交换 access_token
        String accessToken = exchangeAccessToken(code, state);

        // 获取用户信息
        Map<String, String> userInfo = getUserInfo(accessToken);

        // 查询或创建用户
        User user = getUserOrCreate(
                userInfo.get("user_id"),
                userInfo.get("name"),
                userInfo.get("open_id")
        );

        // 生成 JWT token
        String jwtToken = jwtTokenProvider.generateToken(user.getId(), user.getUsername());

        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .token(jwtToken)
                .build();
    }

    /**
     * 获取或创建用户
     *
     * @param feishuUserId 飞书 user_id
     * @param name 用户名
     * @param feishuOpenId 飞书 open_id
     * @return 用户对象
     */
    private User getUserOrCreate(String feishuUserId, String name, String feishuOpenId) {
        // 根据 feishu_open_id 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("feishu_open_id", feishuOpenId);

        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            // 创建新用户
            user = new User();
            user.setUsername(name);
            user.setFeishuOpenId(feishuOpenId);
            user.setCreatedAt(new java.util.Date());
            userMapper.insert(user);
            log.info("Created new user: {} with feishu_open_id: {}", name, feishuOpenId);
        } else {
            // 更新用户名（如果有变化）
            if (!user.getUsername().equals(name)) {
                user.setUsername(name);
                userMapper.updateById(user);
            }
        }

        return user;
    }
}
