package com.assignment.global.event;

public record CouponIssueEvent(
        Long couponId,
        Long userId
) {
}
