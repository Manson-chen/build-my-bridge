package com.buildmybridge.service;

import com.buildmybridge.entity.Subscription;
import com.buildmybridge.mapper.SubscriptionMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 绑定关系服务
 * 提供绑定关系的 CRUD 操作
 */
@Slf4j
@Service
public class SubscriptionService extends ServiceImpl<SubscriptionMapper, Subscription> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 会话超时时间范围：5 - 1440 分钟
    private static final int MIN_SESSION_TIMEOUT = 5;
    private static final int MAX_SESSION_TIMEOUT = 1440;
    private static final int DEFAULT_SESSION_TIMEOUT = 120;

    /**
     * 创建绑定关系
     *
     * @param botAccountId 机器人账户 ID
     * @param aiAppId AI 应用 ID
     * @param sessionTimeoutMinutes 会话超时时间（分钟）
     * @param showErrorToUser 是否向用户展示错误信息
     * @return 创建的绑定关系
     */
    public Subscription createSubscription(Long botAccountId, Long aiAppId,
                                          Integer sessionTimeoutMinutes, Boolean showErrorToUser) {
        try {
            // 验证会话超时时间
            if (sessionTimeoutMinutes == null) {
                sessionTimeoutMinutes = DEFAULT_SESSION_TIMEOUT;
            } else if (sessionTimeoutMinutes < MIN_SESSION_TIMEOUT || sessionTimeoutMinutes > MAX_SESSION_TIMEOUT) {
                throw new RuntimeException("Session timeout must be between " + MIN_SESSION_TIMEOUT + " and " + MAX_SESSION_TIMEOUT + " minutes");
            }

            if (showErrorToUser == null) {
                showErrorToUser = false;
            }

            Subscription subscription = new Subscription();
            subscription.setBotAccountId(botAccountId);
            subscription.setAiAppId(aiAppId);
            subscription.setSessionTimeoutMinutes(sessionTimeoutMinutes);
            subscription.setShowErrorToUser(showErrorToUser);
            subscription.setEnabled(true);
            subscription.setCreatedAt(LocalDateTime.now());
            subscription.setUpdatedAt(LocalDateTime.now());

            this.save(subscription);
            log.info("Created subscription: botId={}, appId={}, sessionTimeout={} minutes",
                    botAccountId, aiAppId, sessionTimeoutMinutes);
            return subscription;
        } catch (Exception e) {
            log.error("Failed to create subscription", e);
            throw new RuntimeException("Failed to create subscription", e);
        }
    }

    /**
     * 更新绑定关系配置
     *
     * @param subscriptionId 绑定关系 ID
     * @param sessionTimeoutMinutes 会话超时时间
     * @param showErrorToUser 是否向用户展示错误信息
     * @return 更新后的绑定关系
     */
    public Subscription updateSubscription(Long subscriptionId, Integer sessionTimeoutMinutes, Boolean showErrorToUser) {
        try {
            Subscription subscription = this.getById(subscriptionId);
            if (subscription == null) {
                throw new RuntimeException("Subscription not found");
            }

            // 验证会话超时时间
            if (sessionTimeoutMinutes != null &&
                    (sessionTimeoutMinutes < MIN_SESSION_TIMEOUT || sessionTimeoutMinutes > MAX_SESSION_TIMEOUT)) {
                throw new RuntimeException("Session timeout must be between " + MIN_SESSION_TIMEOUT + " and " + MAX_SESSION_TIMEOUT + " minutes");
            }

            if (sessionTimeoutMinutes != null) {
                subscription.setSessionTimeoutMinutes(sessionTimeoutMinutes);
            }

            if (showErrorToUser != null) {
                subscription.setShowErrorToUser(showErrorToUser);
            }

            subscription.setUpdatedAt(LocalDateTime.now());
            this.updateById(subscription);
            log.info("Updated subscription {} with sessionTimeout={}, showErrorToUser={}",
                    subscriptionId, sessionTimeoutMinutes, showErrorToUser);
            return subscription;
        } catch (Exception e) {
            log.error("Failed to update subscription", e);
            throw new RuntimeException("Failed to update subscription", e);
        }
    }

    /**
     * 获取绑定关系
     *
     * @param subscriptionId 绑定关系 ID
     * @return 绑定关系
     */
    public Subscription getSubscription(Long subscriptionId) {
        return this.getById(subscriptionId);
    }

    /**
     * 查询机器人的所有绑定关系
     *
     * @param botAccountId 机器人 ID
     * @return 绑定关系列表
     */
    public List<Subscription> getSubscriptionsByBot(Long botAccountId) {
        QueryWrapper<Subscription> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bot_account_id", botAccountId);
        queryWrapper.eq("enabled", true);
        return this.list(queryWrapper);
    }

    /**
     * 查询应用被哪些机器人使用（受影响的机器人列表）
     *
     * @param aiAppId AI 应用 ID
     * @return 绑定关系列表
     */
    public List<Subscription> getAffectedBots(Long aiAppId) {
        QueryWrapper<Subscription> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ai_app_id", aiAppId);
        queryWrapper.eq("enabled", true);
        return this.list(queryWrapper);
    }

    /**
     * 检查机器人是否有绑定关系
     *
     * @param botAccountId 机器人 ID
     * @return 是否有绑定
     */
    public boolean hasBotBinding(Long botAccountId) {
        QueryWrapper<Subscription> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bot_account_id", botAccountId);
        queryWrapper.eq("enabled", true);
        return this.count(queryWrapper) > 0;
    }

    /**
     * 删除绑定关系
     *
     * @param subscriptionId 绑定关系 ID
     * @return 是否删除成功
     */
    public boolean deleteSubscription(Long subscriptionId) {
        return this.removeById(subscriptionId);
    }

    /**
     * 启用或禁用绑定关系
     *
     * @param subscriptionId 绑定关系 ID
     * @param enabled 是否启用
     * @return 是否操作成功
     */
    public boolean toggleSubscription(Long subscriptionId, boolean enabled) {
        Subscription subscription = this.getById(subscriptionId);
        if (subscription == null) {
            return false;
        }
        subscription.setEnabled(enabled);
        subscription.setUpdatedAt(LocalDateTime.now());
        return this.updateById(subscription);
    }

    /**
     * 获取机器人的应用绑定（一对一关系）
     *
     * @param botAccountId 机器人 ID
     * @return 绑定的应用 ID，如果没有绑定返回 null
     */
    public Long getApplicationForBot(Long botAccountId) {
        List<Subscription> subscriptions = getSubscriptionsByBot(botAccountId);
        if (subscriptions.isEmpty()) {
            return null;
        }
        // Phase 1：一个机器人只能绑定一个应用，返回第一个
        return subscriptions.get(0).getAiAppId();
    }
}
