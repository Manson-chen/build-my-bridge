package com.buildmybridge.dto;

import com.buildmybridge.entity.BotAccount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 机器人及其连接状态 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "机器人及其连接状态")
public class BotWithStatus {
    /**
     * 机器人账户信息
     */
    @Schema(description = "机器人账户信息")
    private BotAccount bot;

    /**
     * 连接状态 (CONNECTED, DISCONNECTED, CONNECTING, ERROR等)
     */
    @Schema(description = "连接状态", example = "CONNECTED")
    private String connectionStatus;
}
