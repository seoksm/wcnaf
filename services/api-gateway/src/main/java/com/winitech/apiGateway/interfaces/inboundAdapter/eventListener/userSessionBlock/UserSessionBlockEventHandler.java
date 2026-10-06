package com.winitech.apiGateway.interfaces.inboundAdapter.eventListener.userSessionBlock;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface UserSessionBlockEventHandler {
    void handle(String message, String topic) throws JsonProcessingException;
}