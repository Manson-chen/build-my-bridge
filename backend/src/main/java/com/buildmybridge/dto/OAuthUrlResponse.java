package com.buildmybridge.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OAuth 授权 URL 响应 DTO
 */
@Schema(description = "OAuth 授权 URL 响应")
public class OAuthUrlResponse {
    /**
     * 授权 URL
     */
    @Schema(description = "飞书 OAuth 授权 URL", example = "https://open.larkenterprise.com/open/oauth/authorize?...")
    private String authUrl;

    public OAuthUrlResponse() {
    }

    public OAuthUrlResponse(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }
}
