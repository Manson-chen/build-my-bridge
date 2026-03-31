package com.buildmybridge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OAuth 授权 URL 响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "OAuth 授权 URL 响应")
public class OAuthUrlResponse {
    /**
     * 授权 URL
     */
    @Schema(description = "飞书 OAuth 授权 URL", example = "https://open.larkenterprise.com/open/oauth/authorize?...")
    private String url;
}
