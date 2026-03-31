package com.buildmybridge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 飞书消息发送器
 * 通过飞书 IM API 发送回复消息
 */
@Slf4j
@Service
public class FeishuMessageSender {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FeishuTokenService feishuTokenService;

    private static final String FEISHU_MESSAGE_SEND_URL = "https://open.feishu.cn/open-apis/im/v1/messages?receive_id_type=chat_id";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 发送文本消息到飞书
     *
     * @param chatId 会话 ID（群 ID 或用户 ID）
     * @param content 消息内容
     * @return 是否发送成功
     */
    public boolean sendTextMessage(String chatId, String content) {
        try {
            String tenantAccessToken = feishuTokenService.getTenantAccessToken();

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("receive_id", chatId);
            requestBody.put("msg_type", "text");

            Map<String, Object> contentMap = new HashMap<>();
            contentMap.put("text", content);
            requestBody.put("content", objectMapper.writeValueAsString(contentMap));

            // 设置请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + tenantAccessToken);
            headers.put("Content-Type", "application/json");

            // 发送请求
            String response = restTemplate.postForObject(
                    FEISHU_MESSAGE_SEND_URL,
                    requestBody,
                    String.class
            );

            // 解析响应
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            Integer code = (Integer) responseMap.get("code");

            if (code != 0) {
                String msg = (String) responseMap.get("msg");
                log.error("Failed to send message to chat {}: {}", chatId, msg);
                return false;
            }

            log.info("Successfully sent text message to chat {}", chatId);
            return true;
        } catch (IOException e) {
            log.error("Failed to send message", e);
            return false;
        } catch (Exception e) {
            log.error("Failed to send message", e);
            return false;
        }
    }

    /**
     * 发送卡片消息到飞书（可选，用于富文本显示）
     *
     * @param chatId 会话 ID
     * @param cardJson 卡片 JSON 内容
     * @return 是否发送成功
     */
    public boolean sendCardMessage(String chatId, String cardJson) {
        try {
            String tenantAccessToken = feishuTokenService.getTenantAccessToken();

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("receive_id", chatId);
            requestBody.put("msg_type", "interactive");
            requestBody.put("content", cardJson);

            // 设置请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + tenantAccessToken);
            headers.put("Content-Type", "application/json");

            // 发送请求
            String response = restTemplate.postForObject(
                    FEISHU_MESSAGE_SEND_URL,
                    requestBody,
                    String.class
            );

            // 解析响应
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            Integer code = (Integer) responseMap.get("code");

            if (code != 0) {
                String msg = (String) responseMap.get("msg");
                log.error("Failed to send card message to chat {}: {}", chatId, msg);
                return false;
            }

            log.info("Successfully sent card message to chat {}", chatId);
            return true;
        } catch (IOException e) {
            log.error("Failed to send card message", e);
            return false;
        } catch (Exception e) {
            log.error("Failed to send card message", e);
            return false;
        }
    }

    /**
     * 回复消息（作为子消息）
     *
     * @param messageId 原消息 ID
     * @param content 回复内容
     * @return 是否发送成功
     */
    public boolean replyToMessage(String messageId, String content) {
        try {
            String tenantAccessToken = feishuTokenService.getTenantAccessToken();

            String replyUrl = String.format(
                    "https://open.feishu.cn/open-apis/im/v1/messages/%s/replies",
                    messageId
            );

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("msg_type", "text");

            Map<String, Object> contentMap = new HashMap<>();
            contentMap.put("text", content);
            requestBody.put("content", objectMapper.writeValueAsString(contentMap));

            // 设置请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + tenantAccessToken);
            headers.put("Content-Type", "application/json");

            // 发送请求
            String response = restTemplate.postForObject(
                    replyUrl,
                    requestBody,
                    String.class
            );

            // 解析响应
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            Integer code = (Integer) responseMap.get("code");

            if (code != 0) {
                String msg = (String) responseMap.get("msg");
                log.error("Failed to reply to message {}: {}", messageId, msg);
                return false;
            }

            log.info("Successfully replied to message {}", messageId);
            return true;
        } catch (IOException e) {
            log.error("Failed to reply to message", e);
            return false;
        } catch (Exception e) {
            log.error("Failed to reply to message", e);
            return false;
        }
    }
}
