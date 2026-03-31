package com.buildmybridge.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 登录请求 DTO
 */
@Schema(description = "登录请求")
public class LoginRequest {
    /**
     * 授权码
     */
    @Schema(description = "飞书 OAuth 授权码", example = "code_xxx")
    private String code;

    public LoginRequest() {
    }

    public LoginRequest(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
