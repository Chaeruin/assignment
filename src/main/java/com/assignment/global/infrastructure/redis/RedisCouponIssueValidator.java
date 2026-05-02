package com.assignment.global.infrastructure.redis;

import com.assignment.domain.coupon.validator.CouponIssueValidator;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisCouponIssueValidator implements CouponIssueValidator {

    private final RedisTemplate<String, String> redisTemplate;
    private final DefaultRedisScript<Long> script;

    @Override
    public Long validateAndPrepare(Long couponId, Long userId, Integer totalQuantity) {
        return redisTemplate.execute(
                script,
                Collections.emptyList(),
                couponId.toString(),
                userId.toString(),
                totalQuantity.toString()
        );
    }
}
