package com.buildmybridge.service;

import com.buildmybridge.dto.BotWithStatus;
import com.buildmybridge.entity.BotAccount;
import com.buildmybridge.mapper.BotAccountMapper;
import com.buildmybridge.utils.MD5Util;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 机器人账户服务
 * 提供机器人账户的 CRUD 操作
 */
@Slf4j
@Service
public class BotAccountService extends ServiceImpl<BotAccountMapper, BotAccount> {

    @Autowired
    private FeishuWSConnectionManager wsConnectionManager;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 创建机器人账户
     *
     * @param botName 机器人名称
     * @param appId 飞书应用 ID
     * @param appSecret 飞书应用密钥（会被 MD5 加密存储）
     * @return 创建的机器人账户
     */
    public BotAccount createBot(String botName, String appId, String appSecret) {
        try {
            BotAccount bot = new BotAccount();
            bot.setBotType("FEISHU");
            bot.setBotName(botName);
            bot.setEnabled(true);
            bot.setCreatedAt(LocalDateTime.now());

            // 保存配置到 JSON
            Map<String, String> config = new HashMap<>();
            config.put("app_id", appId);
            config.put("app_secret", MD5Util.md5(appSecret)); // MD5 加密存储
            bot.setBotConfig(objectMapper.writeValueAsString(config));

            this.save(bot);
            log.info("Created bot account: {} with id: {}", botName, bot.getId());

            // 异步创建 WebSocket 连接
            wsConnectionManager.createConnection(
                    String.valueOf(bot.getId()),
                    appId,
                    appSecret
            );

            return bot;
        } catch (Exception e) {
            log.error("Failed to create bot account", e);
            throw new RuntimeException("Failed to create bot account", e);
        }
    }

    /**
     * 更新机器人账户
     *
     * @param botId 机器人 ID
     * @param botName 机器人名称
     * @param appId 飞书应用 ID
     * @param appSecret 飞书应用密钥
     * @return 更新后的机器人账户
     */
    public BotAccount updateBot(Long botId, String botName, String appId, String appSecret) {
        try {
            BotAccount bot = this.getById(botId);
            if (bot == null) {
                throw new RuntimeException("Bot not found");
            }

            bot.setBotName(botName);

            Map<String, String> config = new HashMap<>();
            config.put("app_id", appId);
            config.put("app_secret", MD5Util.md5(appSecret));
            bot.setBotConfig(objectMapper.writeValueAsString(config));

            this.updateById(bot);
            log.info("Updated bot account: {} with id: {}", botName, botId);

            // 关闭旧连接并创建新连接
            wsConnectionManager.closeConnection(String.valueOf(botId));
            wsConnectionManager.createConnection(
                    String.valueOf(botId),
                    appId,
                    appSecret
            );

            return bot;
        } catch (Exception e) {
            log.error("Failed to update bot account", e);
            throw new RuntimeException("Failed to update bot account", e);
        }
    }

    /**
     * 获取机器人账户
     *
     * @param botId 机器人 ID
     * @return 机器人账户
     */
    public BotAccount getBot(Long botId) {
        return this.getById(botId);
    }

    /**
     * 获取所有启用的机器人账户
     *
     * @return 机器人列表
     */
    public List<BotAccount> getEnabledBots() {
        QueryWrapper<BotAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enabled", true);
        return this.list(queryWrapper);
    }

    /**
     * 删除机器人账户
     *
     * @param botId 机器人 ID
     * @return 是否删除成功
     */
    public boolean deleteBot(Long botId) {
        try {
            // 关闭 WebSocket 连接
            wsConnectionManager.closeConnection(String.valueOf(botId));
            return this.removeById(botId);
        } catch (Exception e) {
            log.error("Failed to delete bot account", e);
            return false;
        }
    }

    /**
     * 启用或禁用机器人
     *
     * @param botId 机器人 ID
     * @param enabled 是否启用
     * @return 是否操作成功
     */
    public boolean toggleBotStatus(Long botId, boolean enabled) {
        BotAccount bot = this.getById(botId);
        if (bot == null) {
            return false;
        }
        bot.setEnabled(enabled);
        return this.updateById(bot);
    }

    /**
     * 获取机器人列表及其连接状态
     *
     * @return 包含状态的机器人列表
     */
    public List<BotWithStatus> getBotsWithStatus() {
        List<BotAccount> bots = this.list();
        return bots.stream().map(bot -> {
            BotWithStatus botStatus = new BotWithStatus();
            botStatus.setBot(bot);
            botStatus.setConnectionStatus(wsConnectionManager.getConnectionStatus(String.valueOf(bot.getId())));
            return botStatus;
        }).collect(Collectors.toList());
    }

    /**
     * 获取连接状态
     *
     * @param botId 机器人 ID
     * @return 连接状态
     */
    public String getConnectionStatus(String botId) {
        return wsConnectionManager.getConnectionStatus(botId);
    }
}
