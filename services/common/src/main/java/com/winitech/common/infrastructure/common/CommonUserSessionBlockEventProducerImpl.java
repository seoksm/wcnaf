package com.winitech.common.infrastructure.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.winitech.common.interfaces.inboundAdapter.eventListener.CommonUserSessionBlockMessage;
import com.winitech.common.interfaces.outboundAdapter.eventProducer.CommonUserSessionBlockEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonUserSessionBlockEventProducerImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-21 15:47
 **/
@Slf4j
@Service
public class CommonUserSessionBlockEventProducerImpl implements CommonUserSessionBlockEventProducer {
	@Value("${winitech.kafka.topic.prefix}fct.common.v1.common-user-token-block")
	private String topicName;

	@Value("${spring.kafka.bootstrap-servers}")
	private String kafkaBootstrapServer;

//	private final KafkaProperties kafkaProperties;
	private final KafkaProducer<String, Object> producer;
	private final ObjectMapper objectMapper;

	public CommonUserSessionBlockEventProducerImpl(KafkaProperties kafkaProperties) {
//		this.kafkaProperties = kafkaProperties;
		this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
		this.producer = new KafkaProducer<>(kafkaProperties.buildProducerProperties());
	}

//	@PostConstruct
//	public void initialize() {
//		if (kafkaBootstrapServer == null || kafkaBootstrapServer.isEmpty()) {
//			log.info("Kafka bootstrap server is not configured. Skipping Kafka producer initialization. (topic : {})", topicName);
//			this.producer = null;
//			return;
//		}
//
//		log.info("Kafka producer initializing ... ");
//		this.producer = new KafkaProducer<>(kafkaProperties.buildProducerProperties());
//		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
//	}

	@PreDestroy
	public void shutdown() {
		if (producer == null) {
			return;
		}

		log.info("Shutdown Kafka producer");
		producer.close();
	}

	@Override
	public void userSessionBlocked(UUID userSessionId, OffsetDateTime accessTokenExpiresAt, UUID userId) {
		if (producer == null) {
			return;
		}

		CommonUserSessionBlockMessage commonUserSessionBlockMessage = CommonUserSessionBlockMessage.builder()
				.op(CommonUserSessionBlockMessage.OPERATION.s)
				.ts_ms((int) (System.currentTimeMillis()/ 1000))
				.userSessionId(userSessionId)
				.accessTokenExpiresAt(accessTokenExpiresAt)
				.userId(userId)
				.build();

		try {
			String jsonMessage = objectMapper.writeValueAsString(commonUserSessionBlockMessage);
			producer.send(new ProducerRecord<>(topicName, jsonMessage));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		} finally {
			producer.flush();
		}
	}
}
