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
 * 消息日志实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("message_log")
public class MessageLog {

    /**
     * 日志ID
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
     * 用户ID（飞书 OpenID）
     */
    @TableField("user_id")
    private String userId;

    /**
     * 原始消息内容
     */
    @TableField("message_content")
    private String messageContent;

    /**
     * 应用响应内容
     */
    @TableField("response_content")
    private String responseContent;

    /**
     * 状态（success, error）
     */
    private String status;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 响应时间（毫秒）
     */
    @TableField("response_time_ms")
    private Integer responseTimeMs;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}
