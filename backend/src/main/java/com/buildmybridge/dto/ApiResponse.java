package com.buildmybridge.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 统一的 API 响应格式
 */
@Schema(description = "API 统一响应格式")
public class ApiResponse<T> {

    /**
     * 响应码（"SUCCESS" 或错误代码）
     */
    @Schema(description = "响应码", example = "SUCCESS")
    private String code;

    /**
     * 响应消息
     */
    @Schema(description = "响应消息", example = "请求成功")
    private String message;

    /**
     * 响应数据
     */
    @Schema(description = "响应数据")
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> ApiResponseBuilder<T> builder() {
        return new ApiResponseBuilder<>();
    }

    public static class ApiResponseBuilder<T> {
        private String code;
        private String message;
        private T data;

        public ApiResponseBuilder<T> code(String code) {
            this.code = code;
            return this;
        }

        public ApiResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public ApiResponseBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ApiResponse<T> build() {
            return new ApiResponse<>(code, message, data);
        }
    }

    /**
     * 创建成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code("SUCCESS")
                .message("请求成功")
                .data(data)
                .build();
    }

    /**
     * 创建成功响应（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    /**
     * 创建错误响应
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

    /**
     * 创建错误响应（默认代码）
     */
    public static <T> ApiResponse<T> error(String message) {
        return error("ERROR", message);
    }
}

