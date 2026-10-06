package com.winitech.apiGateway.interfaces.inboundAdapter.eventListener.route;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface RouteChangeEventHandler {
    void handle(String message, String topic) throws JsonProcessingException;
}