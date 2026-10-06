package com.winitech.apiGateway.domain.common;

import com.winitech.apiGateway.common.event.AuthorizationChangeEvent;
import com.winitech.apiGateway.interfaces.inboundAdapter.eventListener.common.CommonAuthorizationDataEventRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonAuthorizationDataEventServiceImpl implements CommonAuthorizationDataEventService{
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void process(CommonAuthorizationDataEventRequestDto requestDto, String topic) {
        if (requestDto.getOp() == CommonAuthorizationDataEventRequestDto.OPERATION.s) {
            applicationEventPublisher.publishEvent(new AuthorizationChangeEvent(this));
        }
    }
}
