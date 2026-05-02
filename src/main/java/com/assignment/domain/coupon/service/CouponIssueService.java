package com.assignment.domain.coupon.service;

import com.assignment.domain.coupon.entity.Coupon;
import com.assignment.domain.coupon.producer.CouponMessageProducer;
import com.assignment.domain.coupon.repository.CouponRepository;
import com.assignment.domain.coupon.validator.CouponIssueValidator;
import com.assignment.global.exception.CouponException;
import com.assignment.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponIssueService {

    private final CouponRepository couponRepository;
    private final CouponIssueValidator couponIssueValidator;
    private final CouponMessageProducer couponMessageProducer;

    public void issue(Long couponId, Long userId) {
        // DB에서 해당 쿠폰의 설정된 전체 수량(totalQuantity)을 가져옴
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponException(ErrorCode.COUPON_NOT_FOUND_EXCEPTION));

        // Redis에 (쿠폰ID, 유저ID, 쿠폰전체수량)을 전달하여 검증
        // 유저는 1개만 요청하지만, Redis는 '전체 수량'을 알아야 꽉 찼는지 판단 가능
        Long result = couponIssueValidator.validateAndPrepare(
                couponId,
                userId,
                coupon.getTotalQuantity()
        );

        if (result == -1) throw new CouponException(ErrorCode.DUPLICATE_COUPON_ISSUE_EXCEPTION);
        if (result == -2) throw new CouponException(ErrorCode.COUPON_EXHAUSTED_EXCEPTION);

        // Kafka 발행
        couponMessageProducer.sendIssueEvent(couponId, userId);
    }
}