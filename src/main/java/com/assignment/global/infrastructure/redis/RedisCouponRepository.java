package com.assignment.global.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisCouponRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String STOCK_KEY_PREFIX = "coupon:stock:";

    public Integer getIssuedCount(Long couponId) {
        String countStr = redisTemplate.opsForValue().get(STOCK_KEY_PREFIX + couponId);
        return (countStr != null) ? Integer.parseInt(countStr) : 0;
    }
}
