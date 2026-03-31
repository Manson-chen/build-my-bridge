package com.buildmybridge.controller;

import com.buildmybridge.dto.RestResponse;
import com.buildmybridge.dto.LoginResponse;
import com.buildmybridge.dto.OAuthUrlResponse;
import com.buildmybridge.service.FeishuOAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证 Controller
 * 提供 OAuth 登录和认证相关的 REST API
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "认证管理", description = "飞书 OAuth 认证接口")
public class AuthController {

    @Autowired
    private FeishuOAuthService feishuOAuthService;

    /**
     * 生成飞书 OAuth 授权 URL
     * 前端在 iframe 中加载此 URL 以获取用户授权
     */
    @GetMapping("/login")
    @Operation(summary = "生成 OAuth 授权 URL", description = "生成飞书 OAuth 授权链接，供前端跳转使用")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "成功生成授权 URL",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = "{\"code\": \"SUCCESS\", \"message\": \"请求成功\", \"data\": {\"url\": \"https://open.feishu.cn/open-apis/oauth2/v2/authorize?...\"}}"
                )
            )
        )
    })
    public RestResponse<OAuthUrlResponse> login() {
        try {
            String authUrl = feishuOAuthService.generateAuthUrl();
            return RestResponse.success(OAuthUrlResponse.builder()
                    .url(authUrl)
                    .build());
        } catch (Exception e) {
            log.error("Failed to generate auth URL", e);
            return RestResponse.error("AUTH_ERROR", "生成授权链接失败");
        }
    }

    /**
     * OAuth 回调处理
     * 用户在飞书授权后，飞书 API 会重定向到此端点
     */
    @GetMapping("/callback")
    @Operation(summary = "OAuth 回调处理", description = "处理飞书 OAuth 回调，返回 JWT Token")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "登录成功",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = "{\"code\": \"SUCCESS\", \"message\": \"请求成功\", \"data\": {\"userId\": 123, \"username\": \"john\", \"token\": \"eyJhbGc...\"}}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "授权失败或 code 无效"
        )
    })
    public RestResponse<LoginResponse> callback(
            @Parameter(name = "code", description = "授权码") @RequestParam String code,
            @Parameter(name = "state", description = "OAuth state 参数") @RequestParam String state) {
        try {
            LoginResponse loginResponse = feishuOAuthService.handleOAuthCallback(code, state);
            log.info("User {} logged in successfully", loginResponse.getUsername());
            return RestResponse.success(loginResponse);
        } catch (Exception e) {
            log.error("OAuth callback failed", e);
            return RestResponse.error("AUTH_ERROR", "登录失败：" + e.getMessage());
        }
    }

    /**
     * 登出（可选）
     * 前端调用此端点清理 token（可选实现）
     */
    @PostMapping("/logout")
    @Operation(summary = "登出", description = "登出当前用户（可选，主要在前端清理 token）")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "登出成功"
        )
    })
    public RestResponse<Void> logout() {
        // 主要在前端清理 token，后端可选实现
        // 例如：清理 Redis 中的 session、记录登出日志等
        log.info("User logged out");
        return RestResponse.success();
    }
}
