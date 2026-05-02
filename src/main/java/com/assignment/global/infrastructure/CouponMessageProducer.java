package com.assignment.global.infrastructure;

public interface CouponMessageProducer {
    void sendIssueEvent(Long couponId, Long userId);
}
