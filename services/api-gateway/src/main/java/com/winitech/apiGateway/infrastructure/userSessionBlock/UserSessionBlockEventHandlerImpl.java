package com.winitech.apiGateway.infrastructure.userSessionBlock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winitech.apiGateway.domain.userSessionBlock.UserSessionBlockEventService;
import com.winitech.apiGateway.interfaces.inboundAdapter.eventListener.userSessionBlock.UserSessionBlockEventHandler;
import com.winitech.apiGateway.interfaces.inboundAdapter.eventListener.userSessionBlock.UserSessionBlockEventRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.*;
import org.springframework.messaging.handler.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSessionBlockEventHandlerImpl implements UserSessionBlockEventHandler {
    private final ObjectMapper objectMapper;
    private final UserSessionBlockEventService service;

    @KafkaListener(
        topics = "wini.fct.common.v1.common-user-token-block",
        groupId = "api-gateway"
    )
    public void subscribe(
        @Payload String message,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        Acknowledgment ack
    ) {
		log.info("|  SV  | RECV    | TOPIC | {}", "wini.fct.common.v1.common-user-token-block");
        try {
            this.handle(message, topic);
        }catch (JsonProcessingException e){
            log.error("세션블락이벤트: error={}", e.getMessage());
            // 에러 발생 시에도 커밋하여 무한 재시도 방지
            // 필요시 DLQ(Dead Letter Queue)로 전송하는 로직 추가 가능
            if (ack != null) {
                ack.acknowledge();
            }
        }
    }

    @Override
    public void handle(String message, String topic) throws JsonProcessingException {
        UserSessionBlockEventRequestDto eventDto = objectMapper.readValue(message, UserSessionBlockEventRequestDto.class);
        service.process(eventDto, topic);
    }
}
