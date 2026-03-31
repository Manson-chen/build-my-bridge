package com.buildmybridge.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 登录响应 DTO
 */
@Schema(description = "登录响应")
public class LoginResponse {
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

    public LoginResponse() {
    }

    public LoginResponse(String token, String username, String feishuOpenId, Long expiresIn) {
        this.token = token;
        this.username = username;
        this.feishuOpenId = feishuOpenId;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFeishuOpenId() {
        return feishuOpenId;
    }

    public void setFeishuOpenId(String feishuOpenId) {
        this.feishuOpenId = feishuOpenId;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
