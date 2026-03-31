package com.buildmybridge.service;

import com.buildmybridge.entity.Subscription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消息路由服务
 * 根据 subscription 关系路由消息
 */
@Slf4j
@Service
public class MessageRouterService {

    @Autowired
    private SubscriptionService subscriptionService;

    /**
     * 路由目标
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteTarget {
        private Long aiAppId;                    // 目标 AI 应用 ID
        private Integer sessionTimeoutMinutes;   // 会话超时时间
        private Boolean showErrorToUser;         // 是否展示错误信息
        private Long subscriptionId;             // 订阅关系 ID
    }

    /**
     * 根据机器人和消息内容获取路由目标
     *
     * @param botAccountId 机器人 ID
     * @param messageContent 消息内容（用于 Phase 2 条件路由）
     * @return 路由目标
     */
    public RouteTarget getRouteTarget(Long botAccountId, String messageContent) {
        try {
            // 获取机器人的绑定关系
            java.util.List<Subscription> subscriptions = subscriptionService.getSubscriptionsByBot(botAccountId);

            if (subscriptions == null || subscriptions.isEmpty()) {
                log.warn("No subscription found for bot {}", botAccountId);
                return null;
            }

            // Phase 1：直接绑定模式，返回第一个（唯一）绑定
            Subscription subscription = subscriptions.get(0);

            // 验证应用是否启用
            if (!subscription.getEnabled()) {
                log.warn("Subscription {} is disabled", subscription.getId());
                return null;
            }

            return new RouteTarget(
                    subscription.getAiAppId(),
                    subscription.getSessionTimeoutMinutes(),
                    subscription.getShowErrorToUser(),
                    subscription.getId()
            );
        } catch (Exception e) {
            log.error("Failed to route message for bot {}", botAccountId, e);
            return null;
        }
    }

    /**
     * 验证路由可用性（检查绑定和应用状态）
     *
     * @param botAccountId 机器人 ID
     * @return 是否可用
     */
    public boolean validateRoute(Long botAccountId) {
        try {
            RouteTarget target = getRouteTarget(botAccountId, "");
            return target != null;
        } catch (Exception e) {
            log.error("Failed to validate route for bot {}", botAccountId, e);
            return false;
        }
    }

    /**
     * 获取机器人绑定的应用 ID
     *
     * @param botAccountId 机器人 ID
     * @return AI 应用 ID
     */
    public Long getTargetAppId(Long botAccountId) {
        try {
            RouteTarget target = getRouteTarget(botAccountId, "");
            return target != null ? target.getAiAppId() : null;
        } catch (Exception e) {
            log.error("Failed to get target app ID for bot {}", botAccountId, e);
            return null;
        }
    }
}
