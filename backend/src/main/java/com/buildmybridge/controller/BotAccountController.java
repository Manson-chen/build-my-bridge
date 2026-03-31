package com.buildmybridge.controller;

import com.buildmybridge.dto.BotWithStatus;
import com.buildmybridge.dto.RestResponse;
import com.buildmybridge.entity.BotAccount;
import com.buildmybridge.service.BotAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 机器人账户 Controller
 * 提供机器人管理的 REST API
 */
@Slf4j
@RestController
@RequestMapping("/bots")
@Tag(name = "机器人管理", description = "飞书机器人账户管理接口")
public class BotAccountController {

    @Autowired
    private BotAccountService botAccountService;

    /**
     * 获取所有机器人列表
     */
    @GetMapping
    @Operation(summary = "获取机器人列表", description = "获取所有机器人账户列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功")
    })
    public RestResponse<List<BotWithStatus>> listBots() {
        try {
            List<BotWithStatus> bots = botAccountService.getBotsWithStatus();
            return RestResponse.success(bots);
        } catch (Exception e) {
            log.error("Failed to list bots", e);
            return RestResponse.error("BOT_ERROR", "获取机器人列表失败");
        }
    }

    /**
     * 获取单个机器人详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取机器人详情", description = "根据 ID 获取单个机器人信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "机器人不存在")
    })
    public RestResponse<BotAccount> getBot(
            @Parameter(name = "id", description = "机器人 ID") @PathVariable Long id) {
        try {
            BotAccount bot = botAccountService.getBot(id);
            if (bot == null) {
                return RestResponse.error("BOT_NOT_FOUND", "机器人不存在");
            }
            return RestResponse.success(bot);
        } catch (Exception e) {
            log.error("Failed to get bot", e);
            return RestResponse.error("BOT_ERROR", "获取机器人失败");
        }
    }

    /**
     * 创建新机器人
     */
    @PostMapping
    @Operation(summary = "创建机器人", description = "创建新的飞书机器人账户")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "参数错误")
    })
    public RestResponse<BotAccount> createBot(@RequestBody CreateBotRequest request) {
        try {
            if (request.getBotName() == null || request.getBotName().isEmpty()) {
                return RestResponse.error("VALIDATION_ERROR", "机器人名称不能为空");
            }
            if (request.getAppId() == null || request.getAppId().isEmpty()) {
                return RestResponse.error("VALIDATION_ERROR", "AppID 不能为空");
            }
            if (request.getAppSecret() == null || request.getAppSecret().isEmpty()) {
                return RestResponse.error("VALIDATION_ERROR", "AppSecret 不能为空");
            }

            BotAccount bot = botAccountService.createBot(
                    request.getBotName(),
                    request.getAppId(),
                    request.getAppSecret()
            );
            return RestResponse.success(bot);
        } catch (Exception e) {
            log.error("Failed to create bot", e);
            return RestResponse.error("BOT_ERROR", "创建机器人失败");
        }
    }

    /**
     * 更新机器人
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新机器人", description = "更新现有机器人的配置")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "404", description = "机器人不存在")
    })
    public RestResponse<BotAccount> updateBot(
            @Parameter(name = "id", description = "机器人 ID") @PathVariable Long id,
            @RequestBody UpdateBotRequest request) {
        try {
            BotAccount bot = botAccountService.updateBot(
                    id,
                    request.getBotName(),
                    request.getAppId(),
                    request.getAppSecret()
            );
            return RestResponse.success(bot);
        } catch (Exception e) {
            log.error("Failed to update bot", e);
            return RestResponse.error("BOT_ERROR", "更新机器人失败");
        }
    }

    /**
     * 删除机器人
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除机器人", description = "删除指定的机器人账户")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功")
    })
    public RestResponse<Void> deleteBot(
            @Parameter(name = "id", description = "机器人 ID") @PathVariable Long id) {
        try {
            botAccountService.deleteBot(id);
            return RestResponse.success();
        } catch (Exception e) {
            log.error("Failed to delete bot", e);
            return RestResponse.error("BOT_ERROR", "删除机器人失败");
        }
    }

    /**
     * 切换机器人启用/禁用状态
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "切换机器人状态", description = "启用或禁用机器人")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "操作成功")
    })
    public RestResponse<Void> toggleBotStatus(
            @Parameter(name = "id", description = "机器人 ID") @PathVariable Long id,
            @Parameter(name = "enabled", description = "是否启用") @RequestParam boolean enabled) {
        try {
            botAccountService.toggleBotStatus(id, enabled);
            return RestResponse.success();
        } catch (Exception e) {
            log.error("Failed to toggle bot status", e);
            return RestResponse.error("BOT_ERROR", "切换机器人状态失败");
        }
    }

    /**
     * 查询单个机器人的连接状态（支持前端轮询）
     */
    @GetMapping("/{id}/connection-status")
    @Operation(summary = "查询机器人连接状态", description = "查询单个机器人的 WebSocket 连接状态")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "404", description = "机器人不存在")
    })
    public RestResponse<ConnectionStatusResponse> getConnectionStatus(
            @Parameter(name = "id", description = "机器人 ID") @PathVariable Long id) {
        try {
            BotAccount bot = botAccountService.getBot(id);
            if (bot == null) {
                return RestResponse.error("BOT_NOT_FOUND", "机器人不存在");
            }
            String status = botAccountService.getConnectionStatus(String.valueOf(id));
            return RestResponse.success(new ConnectionStatusResponse(id, status));
        } catch (Exception e) {
            log.error("Failed to get connection status", e);
            return RestResponse.error("BOT_ERROR", "查询连接状态失败");
        }
    }

    /**
     * 创建机器人的请求 DTO
     */
    @Data
    public static class CreateBotRequest {
        private String botName;     // 机器人名称
        private String appId;       // 飞书应用 ID
        private String appSecret;   // 飞书应用密钥
    }

    /**
     * 更新机器人的请求 DTO
     */
    @Data
    public static class UpdateBotRequest {
        private String botName;     // 机器人名称
        private String appId;       // 飞书应用 ID
        private String appSecret;   // 飞书应用密钥
    }

    /**
     * 连接状态响应 DTO
     */
    @Data
    public static class ConnectionStatusResponse {
        private Long botId;
        private String status;

        public ConnectionStatusResponse(Long botId, String status) {
            this.botId = botId;
            this.status = status;
        }
    }
}
