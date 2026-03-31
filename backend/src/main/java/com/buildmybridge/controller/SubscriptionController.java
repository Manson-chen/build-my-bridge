package com.buildmybridge.controller;

import com.buildmybridge.dto.RestResponse;
import com.buildmybridge.entity.Subscription;
import com.buildmybridge.service.SubscriptionService;
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

/**
 * 绑定关系 Controller
 * 提供绑定关系管理的 REST API
 */
@Slf4j
@RestController
@RequestMapping("/subscriptions")
@Tag(name = "绑定管理", description = "机器人与应用的绑定关系管理接口")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    /**
     * 创建绑定关系
     */
    @PostMapping
    @Operation(summary = "创建绑定", description = "创建机器人与应用的绑定关系")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "参数错误")
    })
    public RestResponse<Subscription> createSubscription(@RequestBody CreateSubscriptionRequest request) {
        try {
            if (request.getBotAccountId() == null) {
                return RestResponse.error("VALIDATION_ERROR", "机器人 ID 不能为空");
            }
            if (request.getAiAppId() == null) {
                return RestResponse.error("VALIDATION_ERROR", "AI 应用 ID 不能为空");
            }

            Subscription subscription = subscriptionService.createSubscription(
                    request.getBotAccountId(),
                    request.getAiAppId(),
                    request.getSessionTimeoutMinutes(),
                    request.getShowErrorToUser()
            );
            return RestResponse.success(subscription);
        } catch (Exception e) {
            log.error("Failed to create subscription", e);
            return RestResponse.error("SUBSCRIPTION_ERROR", "创建绑定失败：" + e.getMessage());
        }
    }

    /**
     * 更新绑定关系配置
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新绑定配置", description = "更新绑定关系的会话超时和错误提示配置")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "404", description = "绑定不存在")
    })
    public RestResponse<Subscription> updateSubscription(
            @Parameter(name = "id", description = "绑定 ID") @PathVariable Long id,
            @RequestBody UpdateSubscriptionRequest request) {
        try {
            Subscription subscription = subscriptionService.updateSubscription(
                    id,
                    request.getSessionTimeoutMinutes(),
                    request.getShowErrorToUser()
            );
            return RestResponse.success(subscription);
        } catch (Exception e) {
            log.error("Failed to update subscription", e);
            return RestResponse.error("SUBSCRIPTION_ERROR", "更新绑定失败：" + e.getMessage());
        }
    }

    /**
     * 查询机器人的绑定关系
     */
    @GetMapping("/bot/{botId}")
    @Operation(summary = "查询机器人绑定", description = "查询指定机器人的所有绑定关系及配置")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功")
    })
    public RestResponse<List<Subscription>> getSubscriptionsByBot(
            @Parameter(name = "botId", description = "机器人 ID") @PathVariable Long botId) {
        try {
            List<Subscription> subscriptions = subscriptionService.getSubscriptionsByBot(botId);
            return RestResponse.success(subscriptions);
        } catch (Exception e) {
            log.error("Failed to get subscriptions by bot", e);
            return RestResponse.error("SUBSCRIPTION_ERROR", "查询绑定失败");
        }
    }

    /**
     * 查询禁用应用时的受影响机器人列表
     */
    @GetMapping("/app/{appId}/affected-bots")
    @Operation(summary = "查询受影响的机器人", description = "查询禁用应用时会受影响的机器人列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功")
    })
    public RestResponse<List<Subscription>> getAffectedBots(
            @Parameter(name = "appId", description = "应用 ID") @PathVariable Long appId) {
        try {
            List<Subscription> subscriptions = subscriptionService.getAffectedBots(appId);
            return RestResponse.success(subscriptions);
        } catch (Exception e) {
            log.error("Failed to get affected bots", e);
            return RestResponse.error("SUBSCRIPTION_ERROR", "查询受影响的机器人失败");
        }
    }

    /**
     * 删除绑定关系
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除绑定", description = "删除指定的绑定关系")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功")
    })
    public RestResponse<Void> deleteSubscription(
            @Parameter(name = "id", description = "绑定 ID") @PathVariable Long id) {
        try {
            subscriptionService.deleteSubscription(id);
            return RestResponse.success();
        } catch (Exception e) {
            log.error("Failed to delete subscription", e);
            return RestResponse.error("SUBSCRIPTION_ERROR", "删除绑定失败");
        }
    }

    /**
     * 获取绑定详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取绑定详情", description = "根据 ID 获取绑定关系详情")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "绑定不存在")
    })
    public RestResponse<Subscription> getSubscription(
            @Parameter(name = "id", description = "绑定 ID") @PathVariable Long id) {
        try {
            Subscription subscription = subscriptionService.getSubscription(id);
            if (subscription == null) {
                return RestResponse.error("SUBSCRIPTION_NOT_FOUND", "绑定不存在");
            }
            return RestResponse.success(subscription);
        } catch (Exception e) {
            log.error("Failed to get subscription", e);
            return RestResponse.error("SUBSCRIPTION_ERROR", "获取绑定失败");
        }
    }

    /**
     * 创建绑定的请求 DTO
     */
    @Data
    public static class CreateSubscriptionRequest {
        private Long botAccountId;              // 机器人 ID
        private Long aiAppId;                   // AI 应用 ID
        private Integer sessionTimeoutMinutes;  // 会话超时时间（分钟）
        private Boolean showErrorToUser;        // 是否向用户展示错误信息
    }

    /**
     * 更新绑定的请求 DTO
     */
    @Data
    public static class UpdateSubscriptionRequest {
        private Integer sessionTimeoutMinutes;  // 会话超时时间（分钟）
        private Boolean showErrorToUser;        // 是否向用户展示错误信息
    }
}
