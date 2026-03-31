package com.buildmybridge.controller;

import com.buildmybridge.dto.RestResponse;
import com.buildmybridge.entity.MessageLog;
import com.buildmybridge.service.MessageLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 消息日志 Controller
 * 提供消息日志查询和分析的 REST API
 */
@Slf4j
@RestController
@RequestMapping("/logs")
@Tag(name = "消息日志", description = "消息日志查询和分析接口")
public class MessageLogController {

    @Autowired
    private MessageLogService messageLogService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * 获取消息日志（分页、筛选）
     */
    @GetMapping
    @Operation(summary = "获取消息日志", description = "获取消息日志列表，支持分页、筛选和搜索")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功")
    })
    public RestResponse<List<MessageLog>> getLogs(
            @Parameter(name = "pageNum", description = "页码，默认 1") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(name = "pageSize", description = "每页条数，默认 20") @RequestParam(defaultValue = "20") int pageSize,
            @Parameter(name = "botId", description = "机器人 ID（可选）") @RequestParam(required = false) Long botId,
            @Parameter(name = "appId", description = "应用 ID（可选）") @RequestParam(required = false) Long appId,
            @Parameter(name = "status", description = "状态：success 或 error（可选）") @RequestParam(required = false) String status,
            @Parameter(name = "startDate", description = "开始日期（ISO 格式，可选）") @RequestParam(required = false) String startDate,
            @Parameter(name = "endDate", description = "结束日期（ISO 格式，可选）") @RequestParam(required = false) String endDate,
            @Parameter(name = "keyword", description = "关键词搜索（可选）") @RequestParam(required = false) String keyword) {
        try {
            MessageLogService.LogQueryCondition condition = new MessageLogService.LogQueryCondition();
            condition.setBotAccountId(botId);
            condition.setAiAppId(appId);
            condition.setStatus(status);
            condition.setKeyword(keyword);

            if (startDate != null && !startDate.isEmpty()) {
                condition.setStartDate(LocalDateTime.parse(startDate, DATE_FORMATTER));
            }

            if (endDate != null && !endDate.isEmpty()) {
                condition.setEndDate(LocalDateTime.parse(endDate, DATE_FORMATTER));
            }

            List<MessageLog> logs = messageLogService.queryLogs(condition, pageNum, pageSize);
            return RestResponse.success(logs);
        } catch (Exception e) {
            log.error("Failed to get logs", e);
            return RestResponse.error("LOG_ERROR", "获取日志失败：" + e.getMessage());
        }
    }

    /**
     * 获取日志详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取日志详情", description = "根据 ID 获取单条消息日志的完整信息")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "日志不存在")
    })
    public RestResponse<MessageLog> getLogDetail(
            @Parameter(name = "id", description = "日志 ID") @PathVariable Long id) {
        try {
            MessageLog messageLog = messageLogService.getLog(id);
            if (messageLog == null) {
                return RestResponse.error("LOG_NOT_FOUND", "日志不存在");
            }
            return RestResponse.success(messageLog);
        } catch (Exception e) {
            log.error("Failed to get log detail", e);
            return RestResponse.error("LOG_ERROR", "获取日志失败");
        }
    }

    /**
     * 获取日志统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取日志统计", description = "获取消息日志的统计信息（总数、成功率等）")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功")
    })
    public RestResponse<MessageLogService.LogStats> getStats(
            @Parameter(name = "botId", description = "机器人 ID（可选）") @RequestParam(required = false) Long botId,
            @Parameter(name = "appId", description = "应用 ID（可选）") @RequestParam(required = false) Long appId,
            @Parameter(name = "startDate", description = "开始日期（ISO 格式，可选）") @RequestParam(required = false) String startDate,
            @Parameter(name = "endDate", description = "结束日期（ISO 格式，可选）") @RequestParam(required = false) String endDate) {
        try {
            MessageLogService.LogQueryCondition condition = new MessageLogService.LogQueryCondition();
            condition.setBotAccountId(botId);
            condition.setAiAppId(appId);

            if (startDate != null && !startDate.isEmpty()) {
                condition.setStartDate(LocalDateTime.parse(startDate, DATE_FORMATTER));
            }

            if (endDate != null && !endDate.isEmpty()) {
                condition.setEndDate(LocalDateTime.parse(endDate, DATE_FORMATTER));
            }

            MessageLogService.LogStats stats = messageLogService.calculateStats(condition);
            return RestResponse.success(stats);
        } catch (Exception e) {
            log.error("Failed to get stats", e);
            return RestResponse.error("LOG_ERROR", "获取统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取今日消息数
     */
    @GetMapping("/today-count")
    @Operation(summary = "获取今日消息数", description = "获取今天的消息总数")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功")
    })
    public RestResponse<TodayCountResponse> getTodayCount() {
        try {
            Long count = messageLogService.getTodayMessageCount();
            return RestResponse.success(new TodayCountResponse(count));
        } catch (Exception e) {
            log.error("Failed to get today count", e);
            return RestResponse.error("LOG_ERROR", "获取今日消息数失败");
        }
    }

    /**
     * 导出日志（CSV 格式）
     */
    @GetMapping("/export")
    @Operation(summary = "导出日志", description = "导出消息日志为 CSV 格式")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "导出成功")
    })
    public RestResponse<ExportResponse> exportLogs(
            @Parameter(name = "botId", description = "机器人 ID（可选）") @RequestParam(required = false) Long botId,
            @Parameter(name = "appId", description = "应用 ID（可选）") @RequestParam(required = false) Long appId,
            @Parameter(name = "status", description = "状态（可选）") @RequestParam(required = false) String status,
            @Parameter(name = "startDate", description = "开始日期（可选）") @RequestParam(required = false) String startDate,
            @Parameter(name = "endDate", description = "结束日期（可选）") @RequestParam(required = false) String endDate) {
        try {
            MessageLogService.LogQueryCondition condition = new MessageLogService.LogQueryCondition();
            condition.setBotAccountId(botId);
            condition.setAiAppId(appId);
            condition.setStatus(status);

            if (startDate != null && !startDate.isEmpty()) {
                condition.setStartDate(LocalDateTime.parse(startDate, DATE_FORMATTER));
            }

            if (endDate != null && !endDate.isEmpty()) {
                condition.setEndDate(LocalDateTime.parse(endDate, DATE_FORMATTER));
            }

            // 获取所有匹配的日志（不分页）
            List<MessageLog> logs = messageLogService.queryLogs(condition, 1, Integer.MAX_VALUE);

            // 构建 CSV 内容
            StringBuilder csv = new StringBuilder();
            csv.append("日志ID,机器人ID,应用ID,消息内容,回复内容,状态,错误信息,创建时间\n");

            for (MessageLog messageLog : logs) {
                csv.append(messageLog.getId()).append(",")
                   .append(messageLog.getBotAccountId()).append(",")
                   .append(messageLog.getAiAppId()).append(",")
                   .append(escapeCSV(messageLog.getMessageContent())).append(",")
                   .append(escapeCSV(messageLog.getResponseContent())).append(",")
                   .append(messageLog.getStatus()).append(",")
                   .append(escapeCSV(messageLog.getErrorMessage())).append(",")
                   .append(messageLog.getCreatedAt()).append("\n");
            }

            ExportResponse response = new ExportResponse();
            response.setContent(csv.toString());
            response.setRecordCount(logs.size());

            return RestResponse.success(response);
        } catch (Exception e) {
            log.error("Failed to export logs", e);
            return RestResponse.error("LOG_ERROR", "导出日志失败：" + e.getMessage());
        }
    }

    /**
     * 转义 CSV 特殊字符
     */
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * 今日消息数响应
     */
    @Data
    public static class TodayCountResponse {
        private Long count;

        public TodayCountResponse(Long count) {
            this.count = count;
        }
    }

    /**
     * 导出响应
     */
    @Data
    public static class ExportResponse {
        private String content;         // CSV 内容
        private int recordCount;        // 记录数
    }
}
