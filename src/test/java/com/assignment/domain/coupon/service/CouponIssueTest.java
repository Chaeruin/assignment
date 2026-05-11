package com.assignment.domain.coupon.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.assignment.domain.coupon.entity.Coupon;
import com.assignment.domain.coupon.enums.CouponStatus;
import com.assignment.domain.coupon.enums.CouponType;
import com.assignment.domain.coupon.repository.CouponRepository;
import com.assignment.domain.coupon.repository.IssuedCouponRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class CouponIssueTest {

    @Autowired private CouponIssueService couponIssueService;
    @Autowired private CouponRepository couponRepository;
    @Autowired private IssuedCouponRepository issuedCouponRepository;
    @Autowired private RedisTemplate<String, String> redisTemplate;

    @Test
    @DisplayName("100개 한정 쿠폰에 200명이 동시에 발급 요청을 하면 정확히 100개만 발급되어야 한다")
    void concurrencyTest() throws InterruptedException {
        // Given: 100개 수량의 쿠폰 생성
        Coupon coupon = couponRepository.save(
                Coupon.builder()
                        .name("선착순 쿠폰")
                        .couponType(CouponType.FIXED_AMOUNT)
                        .couponStatus(CouponStatus.ACTIVE)
                        .discountValue(5000)
                        .totalQuantity(100)
                        .validDays(30)
                        .build()
        );
        int threadCount = 200;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // When: 200명이 동시에 요청
        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1;
            executorService.execute(() -> {
                try {
                    couponIssueService.issue(coupon.getId(), userId);
                    successCount.getAndIncrement();
                } catch (Exception e) {
                    failCount.getAndIncrement();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // Then: 성공 100건, 실패 100건 확인
        assertThat(successCount.get()).isEqualTo(100);
        assertThat(failCount.get()).isEqualTo(100);

        // Redis 재고 확인 (0이어야 함)
        String stock = redisTemplate.opsForValue().get("coupon:stock:" + coupon.getId());
        // 발급된 수량이 100이므로 남은 재고는 0
        assertThat(Integer.parseInt(stock)).isEqualTo(0);
    }

    @BeforeEach
    void setUp() {
        Long couponId = 1L;
        String stockKey = "coupon:stock:" + couponId;
        String userKey = "coupon:issued:user:" + couponId;
        int totalQuantity = 100;

        // 재고 초기화
        redisTemplate.opsForValue().set(stockKey, String.valueOf(totalQuantity));
        // 유저 초기화
        redisTemplate.delete(userKey);
        issuedCouponRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("동일 유저가 동시에 10번 요청 시, 1번만 성공하고 재고도 1개만 줄어야 한다")
    void issueOnlyOnceAndCheckStock() throws InterruptedException {
        // 1. 초기 설정
        int threadCount = 10;
        Long couponId = 1L;
        Long userId = 12345L;
        int initialStock = 100;
//
//        redisTemplate.opsForValue().set("coupon:stock:" + couponId, String.valueOf(initialStock));
//        // SREM 명령어를 사용하여 Set에서 해당 유저 삭제
//        redisTemplate.opsForSet().remove("coupon:issued:user:" + couponId, String.valueOf(userId));

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // 2. 동시 요청 실행
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    couponIssueService.issue(couponId, userId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // 3. 검증
        // (1) 응답 결과 검증
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(threadCount - 1);

        // (2) Redis 재고 상태 검증 (가장 중요한 부분)
        String remainingStockStr = redisTemplate.opsForValue().get("coupon:stock:" + couponId);
        int remainingStock = Integer.parseInt(remainingStockStr);

        // 재고가 정확히 1개만 줄어들어 99개여야 함
        assertThat(remainingStock).isEqualTo(initialStock - 1);
    }
}
