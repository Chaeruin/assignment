package com.assignment.domain.coupon.repository;

import com.assignment.domain.coupon.entity.Coupon;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findAllByIsDisplayTrue();
}
