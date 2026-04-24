package com.assignment.global.common;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final String message;


    // 성공 응답 생성 메서드
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> success(T content) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(content)
                .message("Success")
                .build();
    }

    public static <T> ApiResponse<T> success(T content, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(content)
                .message(message)
                .build();
    }

    // 실패 응답 생성 메서드
    public static <T> ApiResponse<T> fail(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .data(null)
                .message(message)
                .build();
    }
}
