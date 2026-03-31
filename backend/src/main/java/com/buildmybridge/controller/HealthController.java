package com.buildmybridge.controller;

import com.buildmybridge.dto.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查 Controller
 */
@Slf4j
@RestController
@RequestMapping("/health")
@Tag(
    name = "健康检查",
    description = "系统健康状态检查接口"
)
public class HealthController {

    /**
     * 健康检查接口
     */
    @GetMapping("/")
    @Operation(
        summary = "健康检查",
        description = "检查 BuildMyBridge 后端服务是否正常运行"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "服务正常运行",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = "{\"code\": \"SUCCESS\", \"message\": \"请求成功\", \"data\": \"BuildMyBridge Backend is running\"}"
                )
            )
        )
    })
    public RestResponse<?> health() {
        return RestResponse.success("BuildMyBridge Backend is running");
    }
}

