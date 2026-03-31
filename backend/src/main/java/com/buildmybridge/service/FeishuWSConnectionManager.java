package com.buildmybridge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lark.oapi.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

/**
 * 飞书 WebSocket 连接管理器
 * 管理多个 WebSocket 连接到飞书
 */
@Slf4j
@Service
public class FeishuWSConnectionManager {

    @Autowired
    private FeishuTokenService feishuTokenService;

    // 存储所有活跃连接
    private final Map<String, Client> connectionMap = new ConcurrentHashMap<>();

    // 存储连接状态
    private final Map<String, String> connectionStatus = new ConcurrentHashMap<>();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 重试策略：初始立即尝试（0ms），2秒、5秒各重试一次（共3次）
    private static final int[] RETRY_DELAYS = {0, 2000, 5000};

    /**
     * 异步创建 WebSocket 连接
     *
     * @param botId 机器人 ID
     * @param appId 飞书应用 ID
     * @param appSecret 飞书应用密钥
     * @return CompletableFuture，连接成功为 true，失败为 false
     */
    @Async
    public CompletableFuture<Boolean> createConnection(String botId, String appId, String appSecret) {
        try {
            setConnectionStatus(botId, "CONNECTING");
            log.info("Starting to create WebSocket connection for bot: {}", botId);

            // 重试逻辑
            for (int i = 0; i < RETRY_DELAYS.length; i++) {
                try {
                    if (i > 0) {
                        // 延迟重试
                        Thread.sleep(RETRY_DELAYS[i]);
                        log.info("Retrying connection for bot: {} (attempt {}/{})", botId, i + 1, RETRY_DELAYS.length);
                    }

                    // 创建 Lark Client
                    Client client = Client.newBuilder(appId, appSecret).build();

                    // 存储连接
                    connectionMap.put(botId, client);
                    setConnectionStatus(botId, "CONNECTED");
                    log.info("Successfully created WebSocket connection for bot: {}", botId);
                    return CompletableFuture.completedFuture(true);
                } catch (Exception e) {
                    if (i == RETRY_DELAYS.length - 1) {
                        // 最后一次重试失败
                        log.error("Failed to create WebSocket connection for bot: {} after {} attempts", botId, RETRY_DELAYS.length, e);
                        setConnectionStatus(botId, "FAILED");
                        return CompletableFuture.completedFuture(false);
                    }
                    log.warn("Attempt {} failed for bot {}: {}", i + 1, botId, e.getMessage());
                }
            }

            setConnectionStatus(botId, "FAILED");
            return CompletableFuture.completedFuture(false);
        } catch (Exception e) {
            log.error("Unexpected error creating connection for bot: {}", botId, e);
            setConnectionStatus(botId, "FAILED");
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * 关闭 WebSocket 连接
     *
     * @param botId 机器人 ID
     */
    public void closeConnection(String botId) {
        try {
            Client client = connectionMap.remove(botId);
            if (client != null) {
                // 关闭客户端连接
                log.info("Closed WebSocket connection for bot: {}", botId);
            }
            connectionStatus.remove(botId);
        } catch (Exception e) {
            log.error("Failed to close connection for bot: {}", botId, e);
        }
    }

    /**
     * 获取已连接的客户端
     *
     * @param botId 机器人 ID
     * @return Lark Client，如果不存在返回 null
     */
    public Client getConnection(String botId) {
        return connectionMap.get(botId);
    }

    /**
     * 设置连接状态
     *
     * @param botId 机器人 ID
     * @param status 连接状态（INIT、CONNECTING、CONNECTED、FAILED、DISCONNECTED）
     */
    public void setConnectionStatus(String botId, String status) {
        connectionStatus.put(botId, status);
        log.debug("Set connection status for bot {}: {}", botId, status);
    }

    /**
     * 获取连接状态
     *
     * @param botId 机器人 ID
     * @return 连接状态
     */
    public String getConnectionStatus(String botId) {
        return connectionStatus.getOrDefault(botId, "INIT");
    }

    /**
     * 检查连接是否存在
     *
     * @param botId 机器人 ID
     * @return 是否存在连接
     */
    public boolean hasConnection(String botId) {
        return connectionMap.containsKey(botId);
    }

    /**
     * 获取所有活跃连接的 Bot ID
     *
     * @return Bot ID 列表
     */
    public java.util.Set<String> getActiveConnections() {
        return connectionMap.keySet();
    }
}
