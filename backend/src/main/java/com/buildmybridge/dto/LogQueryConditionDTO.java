package com.buildmybridge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息日志查询条件 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogQueryConditionDTO {
    private Long botAccountId;      // 机器人 ID
    private Long aiAppId;           // AI 应用 ID
    private String status;          // 状态（success、error）
    private LocalDateTime startDate; // 开始日期
    private LocalDateTime endDate;   // 结束日期
    private String keyword;         // 关键词搜索
}
