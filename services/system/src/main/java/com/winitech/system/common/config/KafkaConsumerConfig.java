package com.winitech.system.common.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Consumer 설정
 * <pre>
 * com.winitech.system.common.config
 * └ KafkaConsumerConfig.java
 * </pre>
 *
 * @author 이승국 (CLOUD팀)
 * @since 2025-11-04
 */
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:29092}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:system-service-consumer-group}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto-offset-reset:earliest}")
    private String autoOffsetReset;

    @Value("${spring.kafka.consumer.enable-auto-commit:false}")
    private Boolean enableAutoCommit;

    /**
     * Kafka Consumer Factory 설정
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        // Kafka 브로커 주소
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Consumer Group ID
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // 자동 커밋 설정 (false로 설정하여 수동 커밋 사용)
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);

        // 오프셋 리셋 정책 (earliest: 처음부터, latest: 최신부터)
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);

        // Key/Value Deserializer
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // 세션 타임아웃 (기본: 10초)
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);

        // 최대 poll 레코드 수 (한 번에 가져올 메시지 수)
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);

        // 최대 poll 간격 (기본: 5분)
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);

        return new DefaultKafkaConsumerFactory<>(config);
    }

    /**
     * Kafka Listener Container Factory 설정
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        // 수동 커밋 모드 설정
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        // 동시성 설정 (동시에 실행될 Consumer 스레드 수)
        factory.setConcurrency(3);

        // 배치 처리 비활성화 (메시지 하나씩 처리)
        factory.setBatchListener(false);

        return factory;
    }
}

