package com.assignment.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CouponException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public CouponException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}
