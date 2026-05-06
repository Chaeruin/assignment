package com.assignment.domain.coupon.service;

import com.assignment.domain.coupon.dto.response.CouponQuantityResponse;
import com.assignment.domain.coupon.dto.response.CouponResponse;
import com.assignment.domain.coupon.dto.response.IssuedCouponResponse;
import com.assignment.domain.coupon.entity.Coupon;
import com.assignment.domain.coupon.entity.CouponUsageHistory;
import com.assignment.domain.coupon.entity.IssuedCoupon;
import com.assignment.domain.coupon.enums.IssuedCouponStatus;
import com.assignment.domain.coupon.repository.CouponRepository;
import com.assignment.domain.coupon.repository.CouponUsageHistoryRepository;
import com.assignment.domain.coupon.repository.IssuedCouponRepository;
import com.assignment.global.exception.CouponException;
import com.assignment.global.exception.ErrorCode;
import com.assignment.global.exception.UnauthorizedException;
import com.assignment.global.infrastructure.redis.RedisCouponRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final IssuedCouponRepository issuedCouponRepository;
    private final CouponUsageHistoryRepository usageHistoryRepository;
    private final CouponRepository couponRepository;
    private final RedisCouponRepository redisCouponRepository;

    // 사용자 보유 모든 쿠폰 목록
    public List<IssuedCouponResponse> getMyCoupons(Long userId) {
        return issuedCouponRepository.findAllByUserId(userId).stream()
                .map(IssuedCouponResponse::from)
                .toList();
    }

    // 사용 가능한 쿠폰 목록
    public List<IssuedCouponResponse> getUsableCoupons(Long userId) {
        return issuedCouponRepository.findAllByUserIdAndStatus(userId, IssuedCouponStatus.ISSUED).stream()
                .map(IssuedCouponResponse::from)
                .toList();
    }

    // 잔여 수량 조회
    public Integer getCouponStock(Long couponId) {
        Integer issuedCount = redisCouponRepository.getIssuedCount(couponId);
        if (issuedCount == null) issuedCount = 0;

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponException(ErrorCode.COUPON_NOT_FOUND_EXCEPTION));

        return Math.max(coupon.getTotalQuantity() - issuedCount, 0);
    }

    // 발급 가능한 쿠폰 목록 조회
    public List<CouponResponse> getAvailableCoupons() {
        // 현재 날짜 기준으로 발급 기간 내에 있고, 삭제되지 않은 쿠폰 조회
        return couponRepository.findAllByEndDateAfter(LocalDateTime.now()).stream()
                .map(CouponResponse::from)
                .toList();
    }

    // 쿠폰 사용 처리
    @Transactional
    public void useCoupon(Long issuedCouponId, Long userId, Long orderId) {
        IssuedCoupon issuedCoupon = issuedCouponRepository.findById(issuedCouponId)
                .orElseThrow(() -> new CouponException(ErrorCode.COUPON_NOT_FOUND_EXCEPTION));

        // 본인 확인 및 발급 상태 검증
        if (!issuedCoupon.getUserId().equals(userId))
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_EXCEPTION);
        if (issuedCoupon.getStatus() != IssuedCouponStatus.ISSUED)
            throw new CouponException(ErrorCode.COUPON_NOT_AVAILABLE_EXCEPTION);

        // 상태 변경
        issuedCoupon.use();

        // 사용 이력 저장
        CouponUsageHistory history = CouponUsageHistory.builder()
                .issuedCoupon(issuedCoupon)
                .orderId(orderId)
                .discountAmount(calculateDiscount(issuedCoupon))
                .build();

        usageHistoryRepository.save(history);
    }

    @Transactional
    public int resetStock(Long couponId) {
        // 해당 쿠폰의 발급 내역 삭제 (DELETE)
        // 재고 테이블의 수량을 100개(임의 설정)로 UPDATE

        // 쿠폰 존재 확인
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponException(ErrorCode.COUPON_NOT_FOUND_EXCEPTION));

        // 해당 쿠폰의 모든 발급 이력 삭제
        // 테스트용 초기화 - Hard Delete 진행
        issuedCouponRepository.deleteAllByCouponId(couponId);
        redisCouponRepository.resetRedisIssuedCount(couponId);

        // 재고 수량 업데이트
        coupon.updateTotalQuantity(100);
        couponRepository.save(coupon);

        return coupon.getTotalQuantity();
    }

    private Integer calculateDiscount(IssuedCoupon issuedCoupon) {
        // 쿠폰 타입(FIXED/PERCENT)에 따른 계산 로직 구현
        // 명시 요구사항에서 구체화 필요
        return issuedCoupon.getCoupon().getDiscountValue();
    }
}
