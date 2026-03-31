-- BuildMyBridge 数据库建表脚本
-- 使用 MySQL 8.0+ 创建

-- 创建数据库
CREATE DATABASE IF NOT EXISTS buildmybridge
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE buildmybridge;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `username` VARCHAR(255) NOT NULL UNIQUE COMMENT '用户名',
    `feishu_open_id` VARCHAR(255) COMMENT '飞书 OpenID',
    `feishu_user_id` VARCHAR(255) COMMENT '飞书 UserID',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_feishu_open_id` (`feishu_open_id`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 机器人账户表
CREATE TABLE IF NOT EXISTS `bot_account` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '机器人ID',
    `bot_type` VARCHAR(50) NOT NULL COMMENT '机器人类型（FEISHU, DINGDING, WECHAT）',
    `bot_name` VARCHAR(255) NOT NULL COMMENT '机器人名称',
    `bot_config` JSON NOT NULL COMMENT '机器人配置（JSON格式）',
    `enabled` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `connection_status` VARCHAR(50) DEFAULT 'INIT' COMMENT '连接状态（INIT, CONNECTING, CONNECTED, FAILED, DISCONNECTED）',
    `last_error_message` TEXT COMMENT '最后一次错误信息',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_bot_type` (`bot_type`),
    INDEX `idx_enabled` (`enabled`),
    INDEX `idx_connection_status` (`connection_status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='机器人账户表';

-- AI 应用表
CREATE TABLE IF NOT EXISTS `ai_app` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '应用ID',
    `app_type` VARCHAR(50) NOT NULL COMMENT '应用类型（DIFY, OPENAI, ANTHROPIC）',
    `app_name` VARCHAR(255) NOT NULL COMMENT '应用名称',
    `app_config` JSON NOT NULL COMMENT '应用配置（JSON格式）',
    `enabled` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_app_type` (`app_type`),
    INDEX `idx_enabled` (`enabled`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI应用表';

-- 绑定关系表
CREATE TABLE IF NOT EXISTS `subscription` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '绑定ID',
    `bot_account_id` BIGINT NOT NULL COMMENT '机器人ID',
    `ai_app_id` BIGINT NOT NULL COMMENT 'AI应用ID',
    `session_timeout_minutes` INT DEFAULT 120 COMMENT '会话超时时间（分钟，5-1440）',
    `show_error_to_user` BOOLEAN DEFAULT FALSE COMMENT '是否向用户展示错误信息',
    `routing_rules` JSON COMMENT '路由规则（JSON格式，Phase 2使用）',
    `enabled` BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_bot_app` (`bot_account_id`, `ai_app_id`),
    FOREIGN KEY (`bot_account_id`) REFERENCES `bot_account`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`ai_app_id`) REFERENCES `ai_app`(`id`) ON DELETE CASCADE,
    INDEX `idx_enabled` (`enabled`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='绑定关系表';

-- 消息日志表
CREATE TABLE IF NOT EXISTS `message_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    `bot_account_id` BIGINT NOT NULL COMMENT '机器人ID',
    `ai_app_id` BIGINT NOT NULL COMMENT 'AI应用ID',
    `user_id` VARCHAR(255) COMMENT '用户ID（飞书 OpenID）',
    `message_content` LONGTEXT COMMENT '原始消息内容',
    `response_content` LONGTEXT COMMENT '应用响应内容',
    `status` VARCHAR(50) NOT NULL COMMENT '状态（success, error）',
    `error_message` TEXT COMMENT '错误信息',
    `response_time_ms` INT COMMENT '响应时间（毫秒）',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`bot_account_id`) REFERENCES `bot_account`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`ai_app_id`) REFERENCES `ai_app`(`id`) ON DELETE CASCADE,
    INDEX `idx_bot_account_id` (`bot_account_id`),
    INDEX `idx_ai_app_id` (`ai_app_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息日志表';

-- Phase 2：消息任务表（异步消息处理队列）
CREATE TABLE IF NOT EXISTS `message_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '任务ID',
    `bot_account_id` BIGINT NOT NULL COMMENT '机器人ID',
    `ai_app_id` BIGINT NOT NULL COMMENT 'AI应用ID',
    `message_content` LONGTEXT NOT NULL COMMENT '消息内容',
    `user_id` VARCHAR(255) COMMENT '用户ID',
    `status` VARCHAR(50) DEFAULT 'PENDING' COMMENT '任务状态（PENDING, PROCESSING, SUCCESS, FAILED）',
    `retry_count` INT DEFAULT 0 COMMENT '重试次数',
    `error_message` TEXT COMMENT '错误信息',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息任务表（Phase 2）';

-- Phase 2：死信队列表（消息处理失败）
CREATE TABLE IF NOT EXISTS `dead_letter_queue` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '死信ID',
    `message_task_id` BIGINT COMMENT '消息任务ID',
    `bot_account_id` BIGINT NOT NULL COMMENT '机器人ID',
    `ai_app_id` BIGINT NOT NULL COMMENT 'AI应用ID',
    `message_content` LONGTEXT COMMENT '消息内容',
    `error_message` TEXT COMMENT '错误原因',
    `retry_count` INT DEFAULT 0 COMMENT '重试次数',
    `max_retry_count` INT DEFAULT 3 COMMENT '最大重试次数',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='死信队列表（Phase 2）';

-- 创建完成提示
SELECT '✓ 数据库建表完成！' AS '数据库初始化状态';
