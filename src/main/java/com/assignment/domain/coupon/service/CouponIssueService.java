package com.assignment.domain.coupon.service;

import com.assignment.domain.coupon.producer.CouponMessageProducer;
import com.assignment.domain.coupon.validator.CouponIssueValidator;
import com.assignment.global.exception.CouponException;
import com.assignment.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponIssueService {

    private final CouponIssueValidator couponIssueValidator;
    private final CouponMessageProducer couponMessageProducer;

    public void issue(Long couponId, Long userId, Integer totalQuantity) {
        // 검증 및 수량 차감
        Long result = couponIssueValidator.validateAndPrepare(couponId, userId, totalQuantity);

        if (result == -1) throw new CouponException(ErrorCode.DUPLICATE_COUPON_ISSUE_EXCEPTION);
        if (result == -2) throw new CouponException(ErrorCode.COUPON_EXHAUSTED_EXCEPTION);

        // 이벤트 발행
        couponMessageProducer.sendIssueEvent(couponId, userId);
    }
}