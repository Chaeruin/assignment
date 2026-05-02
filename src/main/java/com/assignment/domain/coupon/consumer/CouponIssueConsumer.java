package com.assignment.domain.coupon.consumer;

import com.assignment.domain.coupon.entity.Coupon;
import com.assignment.domain.coupon.entity.IssuedCoupon;
import com.assignment.domain.coupon.repository.CouponRepository;
import com.assignment.domain.coupon.repository.IssuedCouponRepository;
import com.assignment.global.event.CouponIssueEvent;
import com.assignment.global.exception.CouponException;
import com.assignment.global.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponIssueConsumer {

    private final CouponRepository couponRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final ObjectMapper objectMapper;

    @RetryableTopic(
            attempts = "3",         // 총 3번 시도 (최초 1회 + 재시도 2회)
            backoff = @Backoff(delay = 2000, multiplier = 2.0),     // 2초 후 재시도, 실패 시 간격 2배 증가
            include = {RuntimeException.class, Exception.class},    // 모든 예외에 대해 재시도
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltStrategy = DltStrategy.FAIL_ON_ERROR                 // 마지막 재시도 실패 시 DLT로 이동
    )
    @KafkaListener(topics = "coupon-issue-topic", groupId = "coupon-issue-group")
    @Transactional
    public void consume(String message) {
        try {
            CouponIssueEvent event = objectMapper.readValue(message, CouponIssueEvent.class);

            Coupon coupon = couponRepository.findById(event.couponId())
                    .orElseThrow(() -> new CouponException(ErrorCode.COUPON_NOT_FOUND_EXCEPTION));

            // DB 수량 업데이트
            coupon.increaseIssuedQuantity();

            // 발급 이력 저장
            IssuedCoupon issuedCoupon = new IssuedCoupon(coupon, event.userId());

            issuedCouponRepository.save(issuedCoupon);

        } catch (JsonProcessingException e) {
            log.error("메시지 파싱 실패 (재시도 하지 않음): {}", message);
            throw new RuntimeException(e); // 필요시 별도 예외 처리
        }
    }

    @DltHandler
    public void handleDlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("최종 발급 실패 (DLT 전송됨) - Topic: {}, Message: {}", topic, message);
    }
}
