package com.buildmybridge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 会话状态服务
 * 管理会话状态（Redis 存储）
 */
@Slf4j
@Service
public class SessionStateService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String SESSION_KEY_PREFIX = "session:";

    /**
     * 会话数据结构
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionData implements Serializable {
        private String conversationId;
        private List<Message> context;
        private Map<String, Object> metadata;
        private Long createdAt;
        private Long lastUpdated;
        private Long expiresAt;
    }

    /**
     * 消息数据结构
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message implements Serializable {
        private String role;
        private String content;
        private Long timestamp;
    }

    private String getSessionKey(String userId, Long botAccountId) {
        return SESSION_KEY_PREFIX + userId + ":" + botAccountId;
    }

    public SessionData createSession(String userId, Long botAccountId, String conversationId,
                                     Integer sessionTimeoutMinutes, Map<String, Object> metadata) {
        try {
            String sessionKey = getSessionKey(userId, botAccountId);
            Long now = System.currentTimeMillis() / 1000;

            SessionData session = new SessionData();
            session.setConversationId(conversationId);
            session.setContext(new ArrayList<>());
            session.setMetadata(metadata != null ? metadata : new HashMap<>());
            session.setCreatedAt(now);
            session.setLastUpdated(now);
            session.setExpiresAt(now + (sessionTimeoutMinutes * 60));

            redisTemplate.opsForValue().set(sessionKey, session, sessionTimeoutMinutes, TimeUnit.MINUTES);

            log.info("Created session for user {} with bot {}, timeout: {} minutes",
                    userId, botAccountId, sessionTimeoutMinutes);
            return session;
        } catch (Exception e) {
            log.error("Failed to create session", e);
            throw new RuntimeException("Failed to create session", e);
        }
    }

    public SessionData getSession(String userId, Long botAccountId) {
        try {
            String sessionKey = getSessionKey(userId, botAccountId);
            Object value = redisTemplate.opsForValue().get(sessionKey);

            if (value == null) {
                return null;
            }

            if (value instanceof SessionData) {
                return (SessionData) value;
            }

            return null;
        } catch (Exception e) {
            log.error("Failed to get session", e);
            return null;
        }
    }

    public void updateSession(String userId, Long botAccountId, SessionData session, Integer sessionTimeoutMinutes) {
        try {
            String sessionKey = getSessionKey(userId, botAccountId);
            session.setLastUpdated(System.currentTimeMillis() / 1000);
            redisTemplate.opsForValue().set(sessionKey, session, sessionTimeoutMinutes, TimeUnit.MINUTES);

            log.debug("Updated session for user {} with bot {}", userId, botAccountId);
        } catch (Exception e) {
            log.error("Failed to update session", e);
            throw new RuntimeException("Failed to update session", e);
        }
    }

    public void addMessageToSession(String userId, Long botAccountId, String role, String content,
                                    Integer sessionTimeoutMinutes) {
        try {
            SessionData session = getSession(userId, botAccountId);
            if (session == null) {
                return;
            }

            Message message = new Message(role, content, System.currentTimeMillis() / 1000);
            session.getContext().add(message);

            if (session.getContext().size() > 20) {
                session.setContext(new ArrayList<>(session.getContext().subList(
                        session.getContext().size() - 20,
                        session.getContext().size()
                )));
            }

            updateSession(userId, botAccountId, session, sessionTimeoutMinutes);
        } catch (Exception e) {
            log.error("Failed to add message to session", e);
        }
    }

    public void deleteSession(String userId, Long botAccountId) {
        try {
            String sessionKey = getSessionKey(userId, botAccountId);
            redisTemplate.delete(sessionKey);
            log.debug("Deleted session for user {} with bot {}", userId, botAccountId);
        } catch (Exception e) {
            log.error("Failed to delete session", e);
        }
    }

    public boolean hasSession(String userId, Long botAccountId) {
        String sessionKey = getSessionKey(userId, botAccountId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(sessionKey));
    }

    public void extendSession(String userId, Long botAccountId, Integer sessionTimeoutMinutes) {
        try {
            String sessionKey = getSessionKey(userId, botAccountId);
            redisTemplate.expire(sessionKey, sessionTimeoutMinutes, TimeUnit.MINUTES);
            log.debug("Extended session for user {} with bot {} by {} minutes",
                    userId, botAccountId, sessionTimeoutMinutes);
        } catch (Exception e) {
            log.error("Failed to extend session", e);
        }
    }
}
