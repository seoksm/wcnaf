package com.winitech.apiGateway.interfaces.inboundAdapter.eventListener.common;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface CommonAuthorizationDataEventHandler {
    void handle(String message, String topic) throws JsonProcessingException;
}