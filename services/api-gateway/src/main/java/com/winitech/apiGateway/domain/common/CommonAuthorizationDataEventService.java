package com.winitech.apiGateway.domain.common;
import com.winitech.apiGateway.interfaces.inboundAdapter.eventListener.common.CommonAuthorizationDataEventRequestDto;

public interface CommonAuthorizationDataEventService {
    void process(
        CommonAuthorizationDataEventRequestDto requestDto,
        String topic
    );
}