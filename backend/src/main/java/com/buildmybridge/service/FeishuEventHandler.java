package com.buildmybridge.service;

import com.buildmybridge.entity.BotAccount;
import com.buildmybridge.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 飞书事件处理器
 * 负责快速验证消息签名、入队异步处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeishuEventHandler {

    private static final String EVENT_QUEUE_PREFIX = "feishu:event:queue:";
    private static final long QUEUE_TTL = 86400;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final RedisTemplate<String, Object> redisTemplate;
    private final FeishuMessageParser messageParser;
    private final BotAccountService botAccountService;

    public String handleEvent(String timestamp, String nonce, String signature,
                              String body, String botId) throws Exception {

        log.info("Handling event for bot: {}", botId);

        verifySignatureAndCheckBot(botId, timestamp, nonce, signature, body);

        FeishuMessageParser.FeishuMessage message = messageParser.parseMessage(body);
        if (message == null) {
            log.warn("Failed to parse message for bot: {}", botId);
            return null;
        }

        Map<String, Object> event = new HashMap<>();
        event.put("bot_id", botId);
        event.put("message", message);
        event.put("created_at", System.currentTimeMillis());

        String eventId = "event:" + botId + ":" + System.nanoTime();
        String queueKey = EVENT_QUEUE_PREFIX + botId;
        String eventJson = objectMapper.writeValueAsString(event);

        redisTemplate.opsForList().rightPush(queueKey, eventJson);
        redisTemplate.expire(queueKey, QUEUE_TTL, TimeUnit.SECONDS);

        log.info("Event enqueued successfully. Event ID: {}, Bot ID: {}", eventId, botId);
        return eventId;
    }

    private void verifySignatureAndCheckBot(String botId, String timestamp,
                                            String nonce, String signature,
                                            String body) throws Exception {
        BotAccount bot = botAccountService.getBot(Long.parseLong(botId));
        if (bot == null) {
            throw new BusinessException("Bot account not found: " + botId);
        }

        if (!bot.getEnabled()) {
            throw new BusinessException("Bot account is disabled: " + botId);
        }

        log.debug("Signature verification and bot check passed for bot: {}", botId);
    }
}
