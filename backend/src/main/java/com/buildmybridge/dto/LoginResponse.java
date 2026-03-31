package com.buildmybridge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录响应")
public class LoginResponse {
    /**
     * 用户 ID
     */
    @Schema(description = "用户 ID", example = "123")
    private Long userId;

    /**
     * JWT Token
     */
    @Schema(description = "JWT Token", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String token;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    /**
     * 飞书 OpenID
     */
    @Schema(description = "飞书 OpenID", example = "ou_xxx")
    private String feishuOpenId;

    /**
     * Token 过期时间（毫秒）
     */
    @Schema(description = "Token 过期时间（毫秒）", example = "86400000")
    private Long expiresIn;
}
