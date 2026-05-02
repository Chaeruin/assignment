package com.assignment.domain.coupon.validator;

// Redis Lua Script 검증 인터페이스 추상화
public interface CouponIssueValidator {
    /**
     * @return 1(성공), -1(중복), -2(소진)
     */
    Long validateAndPrepare(Long couponId, Long userId, Integer totalQuantity);
}
