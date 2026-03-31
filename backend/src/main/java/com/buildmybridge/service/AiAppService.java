package com.buildmybridge.service;

import com.buildmybridge.entity.AiApp;
import com.buildmybridge.mapper.AiAppMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 应用服务
 * 提供 AI 应用账户的 CRUD 操作
 */
@Slf4j
@Service
public class AiAppService extends ServiceImpl<AiAppMapper, AiApp> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 创建 AI 应用
     *
     * @param appType 应用类型（DIFY、OPENAI 等）
     * @param appName 应用名称
     * @param appConfig 应用配置（JSON 对象转为字符串）
     * @return 创建的 AI 应用
     */
    public AiApp createApp(String appType, String appName, Map<String, Object> appConfig) {
        try {
            AiApp app = new AiApp();
            app.setAppType(appType);
            app.setAppName(appName);
            app.setAppConfig(objectMapper.writeValueAsString(appConfig));
            app.setEnabled(true);
            app.setCreatedAt(LocalDateTime.now());
            app.setUpdatedAt(LocalDateTime.now());

            this.save(app);
            log.info("Created AI app: {} (type: {})", appName, appType);
            return app;
        } catch (Exception e) {
            log.error("Failed to create AI app", e);
            throw new RuntimeException("Failed to create AI app", e);
        }
    }

    /**
     * 更新 AI 应用
     *
     * @param appId 应用 ID
     * @param appName 应用名称
     * @param appConfig 应用配置
     * @return 更新后的 AI 应用
     */
    public AiApp updateApp(Long appId, String appName, Map<String, Object> appConfig) {
        try {
            AiApp app = this.getById(appId);
            if (app == null) {
                throw new RuntimeException("AI app not found");
            }

            app.setAppName(appName);
            app.setAppConfig(objectMapper.writeValueAsString(appConfig));
            app.setUpdatedAt(LocalDateTime.now());

            this.updateById(app);
            log.info("Updated AI app: {} with id: {}", appName, appId);
            return app;
        } catch (Exception e) {
            log.error("Failed to update AI app", e);
            throw new RuntimeException("Failed to update AI app", e);
        }
    }

    /**
     * 获取 AI 应用
     *
     * @param appId 应用 ID
     * @return AI 应用
     */
    public AiApp getApp(Long appId) {
        return this.getById(appId);
    }

    /**
     * 获取所有启用的 AI 应用
     *
     * @return AI 应用列表
     */
    public List<AiApp> getEnabledApps() {
        QueryWrapper<AiApp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enabled", true);
        return this.list(queryWrapper);
    }

    /**
     * 按类型获取 AI 应用
     *
     * @param appType 应用类型
     * @return AI 应用列表
     */
    public List<AiApp> getAppsByType(String appType) {
        QueryWrapper<AiApp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_type", appType);
        queryWrapper.eq("enabled", true);
        return this.list(queryWrapper);
    }

    /**
     * 删除 AI 应用
     *
     * @param appId 应用 ID
     * @return 是否删除成功
     */
    public boolean deleteApp(Long appId) {
        return this.removeById(appId);
    }

    /**
     * 禁用 AI 应用
     *
     * @param appId 应用 ID
     * @return 是否禁用成功
     */
    public boolean disableApp(Long appId) {
        AiApp app = this.getById(appId);
        if (app == null) {
            return false;
        }
        app.setEnabled(false);
        app.setUpdatedAt(LocalDateTime.now());
        return this.updateById(app);
    }

    /**
     * 启用 AI 应用
     *
     * @param appId 应用 ID
     * @return 是否启用成功
     */
    public boolean enableApp(Long appId) {
        AiApp app = this.getById(appId);
        if (app == null) {
            return false;
        }
        app.setEnabled(true);
        app.setUpdatedAt(LocalDateTime.now());
        return this.updateById(app);
    }

    /**
     * 获取应用配置
     *
     * @param appId 应用 ID
     * @return 应用配置 Map
     */
    public Map<String, Object> getAppConfig(Long appId) {
        try {
            AiApp app = this.getById(appId);
            if (app == null) {
                return null;
            }
            return objectMapper.readValue(app.getAppConfig(), Map.class);
        } catch (Exception e) {
            log.error("Failed to parse app config for app {}", appId, e);
            return new HashMap<>();
        }
    }
}
