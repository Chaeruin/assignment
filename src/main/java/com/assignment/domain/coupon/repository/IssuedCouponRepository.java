package com.assignment.domain.coupon.repository;

import com.assignment.domain.coupon.entity.IssuedCoupon;
import com.assignment.domain.coupon.enums.IssuedCouponStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {

    List<IssuedCoupon> findAllByUserId(Long userId);

    List<IssuedCoupon> findAllByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") IssuedCouponStatus status
    );

    void deleteAllByCouponId(Long couponId);
}
