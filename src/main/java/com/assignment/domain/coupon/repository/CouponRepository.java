package com.assignment.domain.coupon.repository;

import com.assignment.domain.coupon.entity.Coupon;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findAllByEndDateAfter(LocalDateTime now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Coupon c SET c.issuedQuantity = :finalCount WHERE c.id = :couponId")
    void updateIssuedQuantity(@Param("couponId") Long couponId, @Param("finalCount") int finalCount);
}
