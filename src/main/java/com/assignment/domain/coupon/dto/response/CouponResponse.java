package com.assignment.domain.coupon.dto.response;

import com.assignment.domain.coupon.entity.Coupon;
import java.time.LocalDateTime;

public record CouponResponse(
        Long id,
        String name,
        Integer totalQuantity,
        LocalDateTime endAt
) {
    public static CouponResponse from(Coupon entity) {
        return new CouponResponse(
                entity.getId(),
                entity.getName(),
                entity.getTotalQuantity(),
                entity.getEndDate()
        );
    }
}
