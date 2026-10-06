package com.winitech.apiGateway.domain.route;

import com.winitech.apiGateway.interfaces.inboundAdapter.eventListener.route.RouteChangeEventRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteChangeEventServiceImpl implements RouteChangeEventService {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void process(RouteChangeEventRequestDto requestDto, String topic) {

        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }
}
