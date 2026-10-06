package com.winitech.system.infrastructure.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.winitech.system.domain.user.UserInfo;
import com.winitech.system.interfaces.inboundAdapter.web.user.UserProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.UUID;

/**
* com.winitech.system.infrastructure.user
* ㄴ UserProducerImpl.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-08-29 오후 5:50
* @see : None
 **/
@Slf4j
@Service
public class UserProducerImpl implements UserProducer {

    //private static final String SPACE_CONNECT = "wini.cdc.common.v1.common-user";
    @Value("${winitech.cdc.common.topic.prefix}common.v1.common-user")
    private String cdcTopic;

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapServer;

    private final KafkaProperties kafkaProperties;
    private KafkaProducer<String, Object> producer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserProducerImpl(KafkaProperties kafkaProperties) {
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
    public void userCreated(UserInfo.UserCdcInfo afterUserInfo) throws JsonProcessingException {
        if (producer == null) {
            return;
        }

        UserChangedMessage userChangedMessage = UserChangedMessage.builder()
                .op(UserChangedMessage.OPERATION.c)
                .ts_ms((int) (System.currentTimeMillis()/ 1000))
                .before(null)
                .after(afterUserInfo)
                .build();
        String message = objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(userChangedMessage);
        producer.send(new ProducerRecord<>(cdcTopic, message));
    }

    @Override
    public void userUpdated(UserInfo.UserCdcInfo beforeUserInfo, UserInfo.UserCdcInfo afterUserInfo) throws JsonProcessingException {
        if (producer == null) {
            return;
        }

        UserChangedMessage userChangedMessage = UserChangedMessage.builder()
                .op(UserChangedMessage.OPERATION.u)
                .ts_ms((int) (System.currentTimeMillis()/ 1000))
                .before(beforeUserInfo)
                .after(afterUserInfo)
                .build();
        String message = objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(userChangedMessage);
        producer.send(new ProducerRecord<>(cdcTopic, message));
    }

    @Override
    public void userDeleted(UserInfo.UserCdcInfo beforeUserInfo) throws JsonProcessingException {
        if (producer == null) {
            return;
        }

        UserChangedMessage userChangedMessage = UserChangedMessage.builder()
                .op(UserChangedMessage.OPERATION.d)
                .ts_ms((int) (System.currentTimeMillis()/ 1000))
                .before(beforeUserInfo)
                .after(null)
                .build();
        String message = objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(userChangedMessage);
        producer.send(new ProducerRecord<>(cdcTopic, message));
    }

    @Override
    public void userOrganizationRemove(UserInfo.UserOrganizationCdcInfo userOrganizationCdcInfo) throws JsonProcessingException {
        if (producer == null) {
            return;
        }
        
        UserChangedMessage userChangedMessage = UserChangedMessage.builder()
                .op(UserChangedMessage.OPERATION.r)
                .ts_ms((int) (System.currentTimeMillis()/ 1000))
                .before(null)
                .after(null)
                .userOrganizationInfo(userOrganizationCdcInfo)
                .build();
        String message = objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(userChangedMessage);
        producer.send(new ProducerRecord<>(cdcTopic, message));
    }
}
