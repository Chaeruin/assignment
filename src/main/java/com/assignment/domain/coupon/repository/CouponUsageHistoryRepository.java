package com.assignment.domain.coupon.repository;

import com.assignment.domain.coupon.entity.CouponUsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponUsageHistoryRepository extends JpaRepository<CouponUsageHistory, Long> {
}
