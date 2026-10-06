package com.winitech.system.infrastructure.authorizationGroupUser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.winitech.common.interfaces.inboundAdapter.eventListener.CommonAuthorizationGroupUserSyncMessage;
import com.winitech.common.domain.common.CommonAuthorizationGroupUserInfo;
import com.winitech.system.interfaces.inboundAdapter.web.authorizationGroupUser.AuthorizationGroupUserProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.infrastructure.authorizationGroupUser
 * └ AuthorizationGroupUserProducerImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 14:37
 **/
@Slf4j
@Service
public class AuthorizationGroupUserProducerImpl implements AuthorizationGroupUserProducer {
	@Value("${winitech.cdc.common.topic.prefix}common.v1.common-authorization-group-user")
	private String cdcTopic;
	
	@Value("${spring.kafka.bootstrap-servers}")
	private String kafkaBootstrapServer;

	private final KafkaProperties kafkaProperties;
	private KafkaProducer<String, Object> producer;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public AuthorizationGroupUserProducerImpl(KafkaProperties kafkaProperties) {
		this.kafkaProperties = kafkaProperties;
	}
	
	@PostConstruct
	public void initialize() {
		if (kafkaBootstrapServer == null || kafkaBootstrapServer.isEmpty()) {		
			log.info("Kafka bootstrap server is not configured. Skipping Kafka producer initialization. (topic : {})", cdcTopic);
			this.producer = null;
			return;
		}
		
		log.info("Kafka producer initializing ... ");
		this.producer = new KafkaProducer<>(kafkaProperties.buildProducerProperties());
		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
	}

	@PreDestroy
	public void shutdown() {
		if (producer == null) {
			return;
		}

		log.info("Shutdown Kafka producer");
		
		producer.close();
	}

	@Override
	public void authorizationGroupUserCdc(List<UUID> authorizationGroupIdList, List<CommonAuthorizationGroupUserInfo> authorizationGroupUserInfoList) {
		if (producer == null) {
			return;
		}
		
		CommonAuthorizationGroupUserSyncMessage authorizationGroupuserSyncMessage = CommonAuthorizationGroupUserSyncMessage.builder()
				.op(CommonAuthorizationGroupUserSyncMessage.OPERATION.s)
				.ts_ms((int) (System.currentTimeMillis()/ 1000))
				.authorizationGroupIdList(authorizationGroupIdList)
				.authorizationGroupUserInfoList(authorizationGroupUserInfoList)
				.build();
		String message = null;
		try {
			message = objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(authorizationGroupuserSyncMessage);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		producer.send(new ProducerRecord<>(cdcTopic, message));
	}
}
