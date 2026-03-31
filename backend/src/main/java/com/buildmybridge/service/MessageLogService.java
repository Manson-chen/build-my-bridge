package com.buildmybridge.service;

import com.buildmybridge.entity.MessageLog;
import com.buildmybridge.mapper.MessageLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息日志服务
 * 记录和查询消息日志
 */
@Slf4j
@Service
public class MessageLogService extends ServiceImpl<MessageLogMapper, MessageLog> {

    /**
     * 日志查询条件
     */
    @Data
    public static class LogQueryCondition {
        private Long botAccountId;      // 机器人 ID
        private Long aiAppId;           // AI 应用 ID
        private String status;          // 状态（success、error）
        private LocalDateTime startDate; // 开始日期
        private LocalDateTime endDate;   // 结束日期
        private String keyword;         // 关键词搜索
    }

    /**
     * 日志统计结果
     */
    @Data
    public static class LogStats {
        private Long totalCount;        // 总消息数
        private Long successCount;      // 成功数
        private Long failureCount;      // 失败数
        private Double successRate;     // 成功率
    }

    /**
     * 记录消息日志
     *
     * @param botAccountId 机器人 ID
     * @param aiAppId AI 应用 ID
     * @param messageContent 消息内容
     * @param responseContent 回复内容
     * @param status 状态（success、error）
     * @param errorMessage 错误信息
     * @return 记录的日志
     */
    public MessageLog recordLog(Long botAccountId, Long aiAppId, String messageContent,
                                String responseContent, String status, String errorMessage) {
        try {
            MessageLog messageLog = new MessageLog();
            messageLog.setBotAccountId(botAccountId);
            messageLog.setAiAppId(aiAppId);
            messageLog.setMessageContent(messageContent);
            messageLog.setResponseContent(responseContent);
            messageLog.setStatus(status);
            messageLog.setErrorMessage(errorMessage);
            messageLog.setCreatedAt(LocalDateTime.now());

            this.save(messageLog);
            log.debug("Recorded message log - bot: {}, app: {}, status: {}", botAccountId, aiAppId, status);
            return messageLog;
        } catch (Exception e) {
            log.error("Failed to record message log", e);
            throw new RuntimeException("Failed to record message log", e);
        }
    }

    /**
     * 获取日志详情
     *
     * @param logId 日志 ID
     * @return 日志
     */
    public MessageLog getLog(Long logId) {
        return this.getById(logId);
    }

    /**
     * 查询日志（支持分页、筛选）
     *
     * @param condition 查询条件
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 日志列表
     */
    public List<MessageLog> queryLogs(LogQueryCondition condition, int pageNum, int pageSize) {
        try {
            QueryWrapper<MessageLog> queryWrapper = new QueryWrapper<>();

            if (condition.getBotAccountId() != null) {
                queryWrapper.eq("bot_account_id", condition.getBotAccountId());
            }

            if (condition.getAiAppId() != null) {
                queryWrapper.eq("ai_app_id", condition.getAiAppId());
            }

            if (condition.getStatus() != null && !condition.getStatus().isEmpty()) {
                queryWrapper.eq("status", condition.getStatus());
            }

            if (condition.getStartDate() != null) {
                queryWrapper.ge("created_at", condition.getStartDate());
            }

            if (condition.getEndDate() != null) {
                queryWrapper.le("created_at", condition.getEndDate());
            }

            if (condition.getKeyword() != null && !condition.getKeyword().isEmpty()) {
                queryWrapper.like("message_content", condition.getKeyword())
                        .or()
                        .like("response_content", condition.getKeyword());
            }

            queryWrapper.orderByDesc("created_at");

            int offset = (pageNum - 1) * pageSize;
            return this.list(queryWrapper.last("LIMIT " + offset + ", " + pageSize));
        } catch (Exception e) {
            log.error("Failed to query logs", e);
            return java.util.Collections.emptyList();
        }
    }

    /**
     * 计算日志统计
     *
     * @param condition 查询条件
     * @return 统计结果
     */
    public LogStats calculateStats(LogQueryCondition condition) {
        try {
            QueryWrapper<MessageLog> queryWrapper = new QueryWrapper<>();

            if (condition.getBotAccountId() != null) {
                queryWrapper.eq("bot_account_id", condition.getBotAccountId());
            }

            if (condition.getAiAppId() != null) {
                queryWrapper.eq("ai_app_id", condition.getAiAppId());
            }

            if (condition.getStartDate() != null) {
                queryWrapper.ge("created_at", condition.getStartDate());
            }

            if (condition.getEndDate() != null) {
                queryWrapper.le("created_at", condition.getEndDate());
            }

            Long totalCount = this.count(queryWrapper);
            Long successCount = this.count(queryWrapper.clone().eq("status", "success"));
            Long failureCount = totalCount - successCount;
            Double successRate = totalCount > 0 ? (double) successCount / totalCount * 100 : 0.0;

            LogStats stats = new LogStats();
            stats.setTotalCount(totalCount);
            stats.setSuccessCount(successCount);
            stats.setFailureCount(failureCount);
            stats.setSuccessRate(Math.round(successRate * 100.0) / 100.0);

            return stats;
        } catch (Exception e) {
            log.error("Failed to calculate stats", e);
            return null;
        }
    }

    /**
     * 查询今日消息数
     *
     * @return 今日消息数
     */
    public Long getTodayMessageCount() {
        try {
            LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
            LocalDateTime tomorrow = today.plusDays(1);

            QueryWrapper<MessageLog> queryWrapper = new QueryWrapper<>();
            queryWrapper.ge("created_at", today)
                    .lt("created_at", tomorrow);

            return this.count(queryWrapper);
        } catch (Exception e) {
            log.error("Failed to get today message count", e);
            return 0L;
        }
    }

    /**
     * 查询特定日期的消息统计
     *
     * @param date 日期
     * @return 统计结果
     */
    public LogStats getMessageStatsByDate(LocalDateTime date) {
        try {
            LogQueryCondition condition = new LogQueryCondition();
            condition.setStartDate(date.toLocalDate().atStartOfDay());
            condition.setEndDate(date.toLocalDate().atStartOfDay().plusDays(1));

            return calculateStats(condition);
        } catch (Exception e) {
            log.error("Failed to get stats by date", e);
            return null;
        }
    }

    /**
     * 删除旧日志（定时任务使用）
     *
     * @param daysAgo 删除多少天前的日志
     * @return 删除的记录数
     */
    public int deleteOldLogs(int daysAgo) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysAgo);

            QueryWrapper<MessageLog> queryWrapper = new QueryWrapper<>();
            queryWrapper.lt("created_at", cutoffDate);

            return this.getBaseMapper().delete(queryWrapper);
        } catch (Exception e) {
            log.error("Failed to delete old logs", e);
            return 0;
        }
    }
}
