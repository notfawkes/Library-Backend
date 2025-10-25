package com.library.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Don't include null fields in JSON
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Integer statusCode;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    // Helper methods for success
    public static <T> ApiResponse<T> success(T data, String message, int statusCode) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .statusCode(statusCode)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return success(data, message, 200);
    }

    public static ApiResponse<Void> success(String message, int statusCode) {
        return ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .statusCode(statusCode)
                .build();
    }

    // Helper methods for error
    public static <T> ApiResponse<T> error(String message, int statusCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .statusCode(statusCode)
                .build();
    }
}