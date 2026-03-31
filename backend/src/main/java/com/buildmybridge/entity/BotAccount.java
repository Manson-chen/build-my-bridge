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
 * 机器人账户实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bot_account")
public class BotAccount {

    /**
     * 机器人ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 机器人类型（FEISHU, DINGDING, WECHAT等）
     */
    @TableField("bot_type")
    private String botType;

    /**
     * 机器人名称
     */
    @TableField("bot_name")
    private String botName;

    /**
     * 机器人配置（JSON格式）
     * 示例: {"app_id": "cli_xxx", "app_secret": "encrypted_xxx"}
     */
    @TableField("bot_config")
    private String botConfig;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 连接状态（INIT, CONNECTING, CONNECTED, FAILED, DISCONNECTED）
     */
    @TableField("connection_status")
    private String connectionStatus;

    /**
     * 最后一次错误信息
     */
    @TableField("last_error_message")
    private String lastErrorMessage;

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
