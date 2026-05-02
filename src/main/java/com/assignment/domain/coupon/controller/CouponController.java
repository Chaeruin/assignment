package com.assignment.domain.coupon.controller;

import com.assignment.domain.coupon.dto.request.CouponUseRequest;
import com.assignment.domain.coupon.dto.request.IssueRequest;
import com.assignment.domain.coupon.dto.response.CouponResponse;
import com.assignment.domain.coupon.dto.response.IssuedCouponResponse;
import com.assignment.domain.coupon.service.CouponIssueService;
import com.assignment.domain.coupon.service.CouponService;
import com.assignment.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponIssueService couponIssueService;
    private final CouponService couponService;

    // 발급 가능한 쿠폰 목록 조회
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAvailableCoupons() {
        return ResponseEntity.ok(ApiResponse.success(couponService.getAvailableCoupons()));
    }

    // 선착순 쿠폰 발급 요청
    // 한 명의 사용자가 한 번 요청하면 1개가 차감
    @PostMapping("/{couponId}/issue")
    public ResponseEntity<ApiResponse<Void>> issue(
            @PathVariable Long couponId,
            @RequestBody IssueRequest request) {

        couponIssueService.issue(couponId, request.userId());

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 잔여 수량 조회
    @GetMapping("/{couponId}/stock")
    public ResponseEntity<ApiResponse<Integer>> getCouponStock(@PathVariable Long couponId) {
        return ResponseEntity.ok(ApiResponse.success(couponService.getCouponStock(couponId)));
    }

    // 사용자 보유 쿠폰 목록 조회 API
    // 사용자가 가진 모든 쿠폰(사용 가능 + 사용 완료)을 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<List<IssuedCouponResponse>>> getMyCoupons(
            @PathVariable Long userId) {

        List<IssuedCouponResponse> responses = couponService.getMyCoupons(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // 사용 가능한 쿠폰 목록 조회 API
    // 현재 이미 발급한 상태(ISSUED)의 쿠폰만 필터링하여 조회
    @GetMapping("/users/{userId}/usable")
    public ResponseEntity<ApiResponse<List<IssuedCouponResponse>>> getUsableCoupons(
            @PathVariable Long userId) {

        List<IssuedCouponResponse> responses = couponService.getUsableCoupons(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // 쿠폰 사용 API
    @PostMapping("/{issuedCouponId}/use")
    public ResponseEntity<ApiResponse<Void>> use(
            @PathVariable Long issuedCouponId,
            @RequestBody CouponUseRequest request) {

        couponService.useCoupon(issuedCouponId, request.userId(), request.orderId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
