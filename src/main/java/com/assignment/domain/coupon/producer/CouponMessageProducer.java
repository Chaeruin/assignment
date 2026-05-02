package com.assignment.domain.coupon.producer;

public interface CouponMessageProducer {
    void sendIssueEvent(Long couponId, Long userId);
}
