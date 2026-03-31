package com.buildmybridge.controller;

import com.buildmybridge.dto.RestResponse;
import com.buildmybridge.entity.AiApp;
import com.buildmybridge.service.AiAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI 应用 Controller
 * 提供 AI 应用管理的 REST API
 */
@Slf4j
@RestController
@RequestMapping("/apps")
@Tag(name = "AI应用管理", description = "AI 应用账户管理接口")
public class AiAppController {

    @Autowired
    private AiAppService aiAppService;

    /**
     * 获取应用列表（支持分页、筛选）
     */
    @GetMapping
    @Operation(summary = "获取应用列表", description = "获取所有 AI 应用列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功")
    })
    public RestResponse<List<AiApp>> listApps(
            @Parameter(name = "type", description = "应用类型筛选（可选）") @RequestParam(required = false) String type) {
        try {
            List<AiApp> apps;
            if (type != null && !type.isEmpty()) {
                apps = aiAppService.getAppsByType(type);
            } else {
                apps = aiAppService.list();
            }
            return RestResponse.success(apps);
        } catch (Exception e) {
            log.error("Failed to list apps", e);
            return RestResponse.error("APP_ERROR", "获取应用列表失败");
        }
    }

    /**
     * 获取应用详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取应用详情", description = "根据 ID 获取单个应用信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "应用不存在")
    })
    public RestResponse<AiApp> getApp(
            @Parameter(name = "id", description = "应用 ID") @PathVariable Long id) {
        try {
            AiApp app = aiAppService.getApp(id);
            if (app == null) {
                return RestResponse.error("APP_NOT_FOUND", "应用不存在");
            }
            return RestResponse.success(app);
        } catch (Exception e) {
            log.error("Failed to get app", e);
            return RestResponse.error("APP_ERROR", "获取应用失败");
        }
    }

    /**
     * 创建应用
     */
    @PostMapping
    @Operation(summary = "创建应用", description = "创建新的 AI 应用")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "参数错误")
    })
    public RestResponse<AiApp> createApp(@RequestBody CreateAppRequest request) {
        try {
            if (request.getAppType() == null || request.getAppType().isEmpty()) {
                return RestResponse.error("VALIDATION_ERROR", "应用类型不能为空");
            }
            if (request.getAppName() == null || request.getAppName().isEmpty()) {
                return RestResponse.error("VALIDATION_ERROR", "应用名称不能为空");
            }
            if (request.getAppConfig() == null) {
                return RestResponse.error("VALIDATION_ERROR", "应用配置不能为空");
            }

            AiApp app = aiAppService.createApp(
                    request.getAppType(),
                    request.getAppName(),
                    request.getAppConfig()
            );
            return RestResponse.success(app);
        } catch (Exception e) {
            log.error("Failed to create app", e);
            return RestResponse.error("APP_ERROR", "创建应用失败");
        }
    }

    /**
     * 编辑应用
     */
    @PutMapping("/{id}")
    @Operation(summary = "编辑应用", description = "更新现有应用的配置")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "404", description = "应用不存在")
    })
    public RestResponse<AiApp> updateApp(
            @Parameter(name = "id", description = "应用 ID") @PathVariable Long id,
            @RequestBody UpdateAppRequest request) {
        try {
            AiApp app = aiAppService.updateApp(
                    id,
                    request.getAppName(),
                    request.getAppConfig()
            );
            return RestResponse.success(app);
        } catch (Exception e) {
            log.error("Failed to update app", e);
            return RestResponse.error("APP_ERROR", "更新应用失败");
        }
    }

    /**
     * 删除应用
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除应用", description = "删除指定的应用")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功")
    })
    public RestResponse<Void> deleteApp(
            @Parameter(name = "id", description = "应用 ID") @PathVariable Long id) {
        try {
            aiAppService.deleteApp(id);
            return RestResponse.success();
        } catch (Exception e) {
            log.error("Failed to delete app", e);
            return RestResponse.error("APP_ERROR", "删除应用失败");
        }
    }

    /**
     * 禁用应用
     */
    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用应用", description = "禁用指定的应用（返回受影响的机器人列表）")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "禁用成功")
    })
    public RestResponse<Void> disableApp(
            @Parameter(name = "id", description = "应用 ID") @PathVariable Long id) {
        try {
            aiAppService.disableApp(id);
            return RestResponse.success();
        } catch (Exception e) {
            log.error("Failed to disable app", e);
            return RestResponse.error("APP_ERROR", "禁用应用失败");
        }
    }

    /**
     * 获取所有启用的应用列表
     */
    @GetMapping("/available/list")
    @Operation(summary = "获取可用应用列表", description = "获取所有启用的 AI 应用列表（用于机器人配置）")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功")
    })
    public RestResponse<List<AiApp>> getAvailableApps() {
        try {
            List<AiApp> apps = aiAppService.getEnabledApps();
            return RestResponse.success(apps);
        } catch (Exception e) {
            log.error("Failed to get available apps", e);
            return RestResponse.error("APP_ERROR", "获取可用应用列表失败");
        }
    }

    /**
     * 创建应用的请求 DTO
     */
    @Data
    public static class CreateAppRequest {
        private String appType;                      // 应用类型（DIFY、OPENAI 等）
        private String appName;                      // 应用名称
        private Map<String, Object> appConfig;       // 应用配置
    }

    /**
     * 更新应用的请求 DTO
     */
    @Data
    public static class UpdateAppRequest {
        private String appName;                      // 应用名称
        private Map<String, Object> appConfig;       // 应用配置
    }
}
