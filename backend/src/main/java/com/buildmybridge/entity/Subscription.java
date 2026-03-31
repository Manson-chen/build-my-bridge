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
 * 绑定关系实体类（机器人与AI应用的关系）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("subscription")
public class Subscription {

    /**
     * 绑定ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 机器人ID
     */
    @TableField("bot_account_id")
    private Long botAccountId;

    /**
     * AI应用ID
     */
    @TableField("ai_app_id")
    private Long aiAppId;

    /**
     * 会话超时时间（分钟，范围5-1440，默认120）
     */
    @TableField("session_timeout_minutes")
    private Integer sessionTimeoutMinutes;

    /**
     * 是否向用户展示错误信息（默认false）
     */
    @TableField("show_error_to_user")
    private Boolean showErrorToUser;

    /**
     * 路由规则（JSON格式，Phase 2使用）
     * 示例: {"default_app_id": "app_1", "rules": [...]}
     */
    @TableField("routing_rules")
    private String routingRules;

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
