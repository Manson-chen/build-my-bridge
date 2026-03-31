package com.buildmybridge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI 应用实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("ai_app")
public class AiApp {

    /**
     * 应用ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 应用类型（DIFY, OPENAI, ANTHROPIC等）
     */
    @TableField("app_type")
    private String appType;

    /**
     * 应用名称
     */
    @TableField("app_name")
    private String appName;

    /**
     * 应用配置（JSON格式）
     * 示例(Dify): {"api_url": "https://dify.example.com", "api_key": "xxx", "app_id": "xxx", "type": "chatflow"}
     */
    @TableField("app_config")
    private String appConfig;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
