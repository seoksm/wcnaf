package com.winitech.system.infrastructure.programAction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.winitech.common.interfaces.inboundAdapter.eventListener.CommonMenuActionSyncMessage;
import com.winitech.common.domain.common.CommonMenuActionInfo;
import com.winitech.system.interfaces.inboundAdapter.web.programAction.ProgramActionProducer;
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
 * com.winitech.system.infrastructure.programAction
 * └ ProgramActionProducerImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 15:12
 **/
@Slf4j
@Service
public class ProgramActionProducerImpl implements ProgramActionProducer {
	@Value("${winitech.cdc.common.topic.prefix}common.v1.common-menu-action")
	private String cdcTopic;

	@Value("${spring.kafka.bootstrap-servers}")
	private String kafkaBootstrapServer;

	private final KafkaProperties kafkaProperties;
	private KafkaProducer<String, Object> producer;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public ProgramActionProducerImpl(KafkaProperties kafkaProperties) {
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
	public void menuActionCdc(List<UUID> programIdList, List<CommonMenuActionInfo> menuActionInfoList) {
		if (producer == null) {
			return;
		}

		CommonMenuActionSyncMessage menuActionSyncMessage = CommonMenuActionSyncMessage.builder()
				.op(CommonMenuActionSyncMessage.OPERATION.s)
				.ts_ms((int) (System.currentTimeMillis()/ 1000))
				.programIdList(programIdList)
				.menuActionInfoList(menuActionInfoList)
				.build();
		String message = null;
		try {
			message = objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(menuActionSyncMessage);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		producer.send(new ProducerRecord<>(cdcTopic, message));
	}
}
