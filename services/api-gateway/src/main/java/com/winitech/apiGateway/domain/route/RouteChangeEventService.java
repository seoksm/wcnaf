package com.winitech.apiGateway.domain.route;
import com.winitech.apiGateway.interfaces.inboundAdapter.eventListener.route.RouteChangeEventRequestDto;

public interface RouteChangeEventService {
    void process(
        RouteChangeEventRequestDto requestDto,
        String topic
    );
}