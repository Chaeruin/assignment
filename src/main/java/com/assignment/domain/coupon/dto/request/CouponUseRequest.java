package com.assignment.domain.coupon.dto.request;

public record CouponUseRequest(
        Long userId,
        Long orderId
) {
}
