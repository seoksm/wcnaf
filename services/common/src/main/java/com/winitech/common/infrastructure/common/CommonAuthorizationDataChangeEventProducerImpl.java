package com.winitech.common.infrastructure.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.winitech.common.interfaces.outboundAdapter.eventProducer.CommonAuthorizationDataChangeEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonAuthorizationDataChangeEventProducerImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-09 09:31
 **/
@Slf4j
@Service
public class CommonAuthorizationDataChangeEventProducerImpl implements CommonAuthorizationDataChangeEventProducer {
	@Value("${winitech.kafka.topic.prefix}fct.common.v1.common-authorization-data-change")
	private String topicName;

	@Value("${spring.kafka.bootstrap-servers}")
	private String kafkaBootstrapServer;

//	private final KafkaProperties kafkaProperties;
	private final KafkaProducer<String, Object> producer;
	private final ObjectMapper objectMapper;

	public CommonAuthorizationDataChangeEventProducerImpl(KafkaProperties kafkaProperties) {
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
//		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
//	}

	@PreDestroy
	public void shutdown() {
		log.info("Shutdown Kafka producer");
		if (producer != null) {
			producer.close();
		}
	}

	@Override
	public void authorizationDataChanged(String message) {
		CommonAuthorizationDataChangeMessage commonAuthorizationDataChangeMessage = CommonAuthorizationDataChangeMessage.builder()
				.op(CommonAuthorizationDataChangeMessage.OPERATION.s)
				.ts_ms((int) (System.currentTimeMillis()/ 1000))
				.message(message)
				.build();
		
		if (producer == null) {
			log.warn("Kafka producer is not initialized. Skipping event publish.");
			return;
		}

		try {
			String jsonMessage = objectMapper.writeValueAsString(commonAuthorizationDataChangeMessage);
			producer.send(new ProducerRecord<>(topicName, jsonMessage));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		} finally {
			producer.flush();
		}
	}
}
