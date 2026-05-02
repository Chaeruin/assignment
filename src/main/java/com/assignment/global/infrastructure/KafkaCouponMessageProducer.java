package com.assignment.global.infrastructure;

import com.assignment.global.event.CouponIssueEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCouponMessageProducer implements CouponMessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC = "coupon-issue-topic";

    @Override
    public void sendIssueEvent(Long couponId, Long userId) {
        try {
            String message = objectMapper.writeValueAsString(new CouponIssueEvent(couponId, userId));
            // acks=1 설정에 따라 비동기 전송
            kafkaTemplate.send(TOPIC, message);
        } catch (JsonProcessingException e) {
            log.error("Kafka 메시지 직렬화 실패: couponId={}, userId={}", couponId, userId, e);
            throw new RuntimeException("메시지 발행 중 오류 발생");
        }
    }
}
