package com.assignment.domain.coupon.dto.response;

import com.assignment.domain.coupon.entity.IssuedCoupon;
import java.time.LocalDateTime;

public record IssuedCouponResponse(
        Long id,
        String status,
        LocalDateTime issuedAt,
        LocalDateTime expiredAt
) {
    public static IssuedCouponResponse from(IssuedCoupon entity) {
        return new IssuedCouponResponse(
                entity.getId(),
                entity.getStatus().name(),
                entity.getIssuedAt(),
                entity.getExpiredAt()
        );
    }
}
