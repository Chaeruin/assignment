package com.assignment.global.exception;

import com.assignment.global.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CouponException.class)
    public ResponseEntity<ApiResponse<Void>> handleCouponException(CouponException e) {
        log.info(e.getMessage(), e);
        ApiResponse<Void> response = ApiResponse.fail(
                e.getMessage()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }
}
