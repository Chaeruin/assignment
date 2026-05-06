package com.assignment.domain.coupon.entity;


import com.assignment.domain.coupon.enums.CouponStatus;
import com.assignment.domain.coupon.enums.CouponType;
import com.assignment.global.exception.CouponException;
import com.assignment.global.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType couponType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;

    @Column(nullable = false)
    private Integer discountValue;

    private Integer minOrderAmount;

    private Integer maxDiscountAmount;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Integer issuedQuantity = 0;

    @Column(nullable = false)
    private Integer validDays;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    public void increaseIssuedQuantity() {
        if (this.issuedQuantity >= this.totalQuantity) {
            this.status = CouponStatus.EXHAUSTED;
            throw new CouponException(ErrorCode.COUPON_EXHAUSTED_EXCEPTION);
        }
        this.issuedQuantity++;
    }

    public void updateTotalQuantity(int quantity) {
        this.totalQuantity = quantity;
    }
}
