package com.winitech.apiGateway.infrastructure.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winitech.apiGateway.domain.common.CommonAuthorizationDataEventService;
import com.winitech.apiGateway.interfaces.inboundAdapter.eventListener.common.CommonAuthorizationDataEventHandler;
import com.winitech.apiGateway.interfaces.inboundAdapter.eventListener.common.CommonAuthorizationDataEventRequestDto;
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
public class CommonAuthorizationDataEventHandlerImpl implements CommonAuthorizationDataEventHandler {
    private final ObjectMapper objectMapper;
    private final CommonAuthorizationDataEventService service;

    @KafkaListener(
        topics = "wini.fct.common.v1.common-authorization-data-change",
        groupId = "api-gateway"
    )
    public void subscribe(
        @Payload String message,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        Acknowledgment ack
    ) {
		log.info("|  SV  | RECV    | TOPIC | {}", "wini.fct.common.v1.common-authorization-data-change");
        try {
            this.handle(message, topic);
        }catch (JsonProcessingException e){
            log.error("공통권한데이터변경: error={}", e.getMessage());
            // 필요시 DLQ(Dead Letter Queue)로 전송하는 로직 추가 가능
        } finally {
            // 성공 시에도 반드시 커밋해야 한다. ack-mode=manual에서 커밋을 누락하면
            // 컨슈머 재기동 시마다 토픽 전체가 재생되는 문제가 있었다(2026-09-17 발견).
            if (ack != null) {
                ack.acknowledge();
            }
        }
    }

    @Override
    public void handle(String message, String topic) throws JsonProcessingException {
        CommonAuthorizationDataEventRequestDto eventDto = objectMapper.readValue(message, CommonAuthorizationDataEventRequestDto.class);
        service.process(eventDto, topic);
    }
}
