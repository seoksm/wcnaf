package com.winitech.apiGateway.interfaces.outboundAdapter.eventProducer.route;
import com.winitech.apiGateway.interfaces.outboundAdapter.eventProducer.route.RouteChangeEventResponseDto;

public interface RouteChangeEventProducer {
    void produce(RouteChangeEventResponseDto responseDto);
}