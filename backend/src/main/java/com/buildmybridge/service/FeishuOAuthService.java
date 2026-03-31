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
import java.time.LocalDateTime;
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
    private RedisTemplate<String, Object> redisTemplate;

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

    public String generateAuthUrl() {
        String state = UUID.randomUUID().toString();
        // 缓存 state 到 Redis，10 分钟过期
        redisTemplate.opsForValue().set("oauth_state:" + state, "pending", 10, TimeUnit.MINUTES);

        return String.format(
                "%s?client_id=%s&redirect_uri=%s&response_type=code&state=%s",
                FEISHU_OAUTH_AUTHORIZE_URL,
                appId,
                redirectUri,
                state
        );
    }

    public String exchangeAccessToken(String code, String state) {
        // 验证 state
        Boolean stateValid = redisTemplate.hasKey("oauth_state:" + state);
        if (!stateValid) {
            throw new RuntimeException("Invalid or expired state parameter");
        }

        // 清理 state
        redisTemplate.delete("oauth_state:" + state);

        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("grant_type", "authorization_code");
            requestBody.put("client_id", appId);
            requestBody.put("client_secret", appSecret);
            requestBody.put("code", code);
            requestBody.put("redirect_uri", redirectUri);

            String response = restTemplate.postForObject(
                    FEISHU_OAUTH_TOKEN_URL,
                    requestBody,
                    String.class
            );

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
                    expiresIn - 300,
                    TimeUnit.SECONDS
            );

            return accessToken;
        } catch (IOException e) {
            log.error("Failed to parse OAuth response", e);
            throw new RuntimeException("Failed to exchange access token", e);
        }
    }

    public Map<String, String> getUserInfo(String accessToken) {
        try {
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(
                    FEISHU_USER_INFO_URL,
                    org.springframework.http.HttpMethod.GET,
                    entity,
                    String.class
            );

            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
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

    public LoginResponse handleOAuthCallback(String code, String state) {
        String accessToken = exchangeAccessToken(code, state);
        Map<String, String> userInfo = getUserInfo(accessToken);

        User user = getUserOrCreate(
                userInfo.get("user_id"),
                userInfo.get("name"),
                userInfo.get("open_id")
        );

        String jwtToken = jwtTokenProvider.generateToken(user.getId(), user.getUsername());

        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .token(jwtToken)
                .build();
    }

    private User getUserOrCreate(String feishuUserId, String name, String feishuOpenId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("feishu_open_id", feishuOpenId);

        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            user = new User();
            user.setUsername(name);
            user.setFeishuOpenId(feishuOpenId);
            user.setCreatedAt(LocalDateTime.now());
            userMapper.insert(user);
            log.info("Created new user: {} with feishu_open_id: {}", name, feishuOpenId);
        } else {
            if (!user.getUsername().equals(name)) {
                user.setUsername(name);
                userMapper.updateById(user);
            }
        }

        return user;
    }
}
