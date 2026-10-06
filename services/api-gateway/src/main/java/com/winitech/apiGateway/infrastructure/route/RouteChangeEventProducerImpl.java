package com.winitech.apiGateway.infrastructure.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.winitech.apiGateway.interfaces.outboundAdapter.eventProducer.route.RouteChangeEventProducer;;
import com.winitech.apiGateway.interfaces.outboundAdapter.eventProducer.route.RouteChangeEventResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RouteChangeEventProducerImpl implements RouteChangeEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void produce(RouteChangeEventResponseDto responseDto) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(responseDto);
            kafkaTemplate.send("wini.fct.api-gateway.v1.route-change", jsonPayload);
            log.info("|  SV  | CMD    | TOPIC | {}", "wini.fct.api-gateway.v1.route-change");
        }catch (JsonProcessingException e){
            log.error("라우팅변경전달: error={}", e.getMessage());
        }
    }
}
