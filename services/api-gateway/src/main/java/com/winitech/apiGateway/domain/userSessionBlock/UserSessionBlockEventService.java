package com.winitech.apiGateway.domain.userSessionBlock;
import com.winitech.apiGateway.interfaces.inboundAdapter.eventListener.userSessionBlock.UserSessionBlockEventRequestDto;

public interface UserSessionBlockEventService {
    void process(
        UserSessionBlockEventRequestDto requestDto,
        String topic
    );
}