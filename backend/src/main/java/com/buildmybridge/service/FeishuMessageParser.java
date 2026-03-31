package com.buildmybridge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * 飞书消息解析器
 * 解析飞书 WebSocket 事件消息
 */
@Slf4j
@Service
public class FeishuMessageParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 飞书事件消息
     */
    @Data
    public static class FeishuMessage {
        private String messageId;      // 消息 ID
        private String chatId;         // 会话 ID（群 ID 或用户 ID）
        private String senderId;       // 发送者 ID
        private String content;        // 消息内容（文本）
        private String messageType;    // 消息类型（text、image 等）
        private Long timestamp;        // 时间戳
        private String chatType;       // 会话类型（p2p 或 group）
    }

    /**
     * 解析飞书事件消息
     *
     * @param eventData 飞书事件 JSON 数据
     * @return 解析后的飞书消息对象
     */
    public FeishuMessage parseMessage(String eventData) {
        try {
            Map<String, Object> eventMap = objectMapper.readValue(eventData, Map.class);
            Map<String, Object> header = (Map<String, Object>) eventMap.get("header");
            Map<String, Object> event = (Map<String, Object>) eventMap.get("event");

            if (event == null) {
                log.warn("No event data in message");
                return null;
            }

            // 提取消息数据
            Map<String, Object> message = (Map<String, Object>) event.get("message");
            if (message == null) {
                log.warn("No message data in event");
                return null;
            }

            FeishuMessage fsMessage = new FeishuMessage();
            fsMessage.setMessageId((String) message.get("message_id"));
            fsMessage.setChatId((String) message.get("chat_id"));
            fsMessage.setSenderId((String) message.get("sender_id"));
            fsMessage.setMessageType((String) message.get("message_type"));
            fsMessage.setChatType((String) message.get("chat_type"));

            // 时间戳
            Object createTimeObj = message.get("create_time");
            if (createTimeObj != null) {
                fsMessage.setTimestamp(Long.parseLong(createTimeObj.toString()));
            }

            // 解析消息内容
            Map<String, Object> content = (Map<String, Object>) message.get("content");
            if (content != null && "text".equals(fsMessage.getMessageType())) {
                fsMessage.setContent((String) content.get("text"));
            }

            return fsMessage;
        } catch (IOException e) {
            log.error("Failed to parse feishu message", e);
            return null;
        }
    }

    /**
     * 验证飞书消息签名
     * 用于验证事件消息的真实性
     *
     * @param timestamp 消息时间戳
     * @param nonce 随机数
     * @param body 消息体
     * @param signature 签名
     * @param encryptKey 加密密钥
     * @return 是否验证成功
     */
    public boolean verifySignature(String timestamp, String nonce, String body,
                                   String signature, String encryptKey) {
        try {
            // 构建待签名字符串
            String signatureBase = timestamp + nonce + encryptKey + body;

            // 计算 SHA256 哈希
            MessageDigestUtil digest = new MessageDigestUtil();
            String computedSignature = digest.sha256(signatureBase);

            return computedSignature.equals(signature);
        } catch (Exception e) {
            log.error("Failed to verify signature", e);
            return false;
        }
    }

    /**
     * 消息摘要工具类
     */
    public static class MessageDigestUtil {
        public String sha256(String input) throws Exception {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
    }
}
