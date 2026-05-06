package com.assignment.global.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaRetryTopic;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableKafkaRetryTopic
public class KafkaConfig {

    @Bean
    public NewTopic couponIssueTopic() {
        return TopicBuilder.name("coupon-issue-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // DLT 토픽 명시적 생성 (선택 사항, 자동 생성되지만 관리 편의를 위해 등록)
    @Bean
    public NewTopic couponIssueDltTopic() {
        return TopicBuilder.name("coupon-issue-topic-dlt")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("kafka-retry-scheduler-");
        scheduler.initialize();
        return scheduler;
    }
}
