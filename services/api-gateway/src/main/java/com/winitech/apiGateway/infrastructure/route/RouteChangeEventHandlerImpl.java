package com.winitech.apiGateway.infrastructure.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.winitech.apiGateway.domain.route.RouteChangeEventService;
import com.winitech.apiGateway.interfaces.inboundAdapter.eventListener.route.RouteChangeEventHandler;
import com.winitech.apiGateway.interfaces.inboundAdapter.eventListener.route.RouteChangeEventRequestDto;
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
public class RouteChangeEventHandlerImpl implements RouteChangeEventHandler {
    private final ObjectMapper objectMapper;
    private final RouteChangeEventService service;

    @KafkaListener(
        topics = "wini.fct.api-gateway.v1.route-change",
        groupId = "api-gateway"
    )
    public void subscribe(
        @Payload String message,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        Acknowledgment ack
    ) {
		log.info("|  SV  | RECV    | TOPIC | {}", "wini.fct.api-gateway.v1.route-change");
        try {
            this.handle(message, topic);
        }catch (JsonProcessingException e){
            log.error("라우트변경: error={}", e.getMessage());
            // 에러 발생 시에도 커밋하여 무한 재시도 방지
            // 필요시 DLQ(Dead Letter Queue)로 전송하는 로직 추가 가능
            if (ack != null) {
                ack.acknowledge();
            }
        }
    }

    @Override
    public void handle(String message, String topic) throws JsonProcessingException {
        RouteChangeEventRequestDto eventDto = objectMapper.readValue(message, RouteChangeEventRequestDto.class);
        service.process(eventDto, topic);
    }
}
