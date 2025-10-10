package com.agrosmart.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        String status,
        String message,
        T data,
        Instant timestamp,
        String path
) {
    public static <T> ApiResponse<T> ok(T data, String path) {
        return new ApiResponse<>("success", "OK", data, Instant.now(), path);
    }
    public static <T> ApiResponse<T> created(T data, String path) {
        return new ApiResponse<>("success", "Created", data, Instant.now(), path);
    }
    public static <T> ApiResponse<T> success(String message, T data, String path) {
        return new ApiResponse<>("success", message, data, Instant.now(), path);
    }
    public static ApiResponse<Void> successNoData(String message, String path) {
        return new ApiResponse<>("success", message, null, Instant.now(), path);
    }
    public static ApiResponse<Void> error(String message, String path) {
        return new ApiResponse<>("error", message, null, Instant.now(), path);
    }
}
