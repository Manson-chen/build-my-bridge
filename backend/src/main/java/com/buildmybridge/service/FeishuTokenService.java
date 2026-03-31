package com.buildmybridge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 飞书 Token 服务
 * 管理 tenant_access_token 并缓存到 Redis
 */
@Slf4j
@Service
public class FeishuTokenService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${feishu.app-id}")
    private String appId;

    @Value("${feishu.app-secret}")
    private String appSecret;

    private static final String FEISHU_TOKEN_URL = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token";
    private static final String TENANT_TOKEN_CACHE_KEY = "feishu:tenant_access_token";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取 tenant_access_token
     * 首先从 Redis 缓存中取，如果不存在或过期则重新获取
     *
     * @return tenant_access_token
     */
    public String getTenantAccessToken() {
        // 尝试从 Redis 缓存中获取
        String cachedToken = redisTemplate.opsForValue().get(TENANT_TOKEN_CACHE_KEY);
        if (cachedToken != null && !cachedToken.isEmpty()) {
            log.debug("Using cached tenant_access_token");
            return cachedToken;
        }

        // 缓存不存在，重新获取
        return refreshTenantAccessToken();
    }

    /**
     * 刷新 tenant_access_token
     * 从飞书 API 获取新的 token，并缓存到 Redis
     *
     * @return 新的 tenant_access_token
     */
    public String refreshTenantAccessToken() {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("app_id", appId);
            requestBody.put("app_secret", appSecret);

            String response = restTemplate.postForObject(
                    FEISHU_TOKEN_URL,
                    requestBody,
                    String.class
            );

            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            Integer code = (Integer) responseMap.get("code");

            if (code != 0) {
                String msg = (String) responseMap.get("msg");
                log.error("Failed to get tenant_access_token: {}", msg);
                throw new RuntimeException("Failed to get tenant_access_token: " + msg);
            }

            String tenantAccessToken = (String) responseMap.get("tenant_access_token");
            Integer expiresIn = (Integer) responseMap.get("expire");

            if (tenantAccessToken == null || tenantAccessToken.isEmpty()) {
                log.error("Empty tenant_access_token in response");
                throw new RuntimeException("Empty tenant_access_token in response");
            }

            // 缓存到 Redis，设置 TTL（提前 5 分钟过期以便刷新）
            long ttl = expiresIn - 300;
            redisTemplate.opsForValue().set(
                    TENANT_TOKEN_CACHE_KEY,
                    tenantAccessToken,
                    ttl,
                    TimeUnit.SECONDS
            );

            log.info("Successfully refreshed tenant_access_token, expires in {} seconds", expiresIn);
            return tenantAccessToken;
        } catch (IOException e) {
            log.error("Failed to parse tenant_access_token response", e);
            throw new RuntimeException("Failed to get tenant_access_token", e);
        }
    }

    /**
     * 检查 token 是否即将过期
     * 用于主动刷新 token
     *
     * @return 是否需要刷新
     */
    public boolean shouldRefreshToken() {
        Long ttl = redisTemplate.getExpire(TENANT_TOKEN_CACHE_KEY, TimeUnit.SECONDS);
        return ttl == null || ttl < 600; // 少于 10 分钟则需要刷新
    }
}
