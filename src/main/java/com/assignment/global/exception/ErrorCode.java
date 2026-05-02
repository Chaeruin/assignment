package com.assignment.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    COUPON_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "404", "쿠폰 없음"),
    COUPON_EXHAUSTED_EXCEPTION(HttpStatus.CONFLICT, "409", "재고 소진"),
    DUPLICATE_COUPON_ISSUE_EXCEPTION(HttpStatus.CONFLICT, "409", "중복 발급 시도"),
    COUPON_NOT_AVAILABLE_EXCEPTION(HttpStatus.BAD_REQUEST, "400", "발급 기간 아님"),

    UNAUTHORIZED_EXCEPTION(HttpStatus.FORBIDDEN, "403", "발급 권한 없음");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
