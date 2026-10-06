package com.winitech.common.interfaces.inboundAdapter.eventListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.kafka.support.Acknowledgment;

/**
 * <pre>
 * com.winitech.common.interfaces.inboundAdapter.eventListener
 * └ CommonAuthorizationGroupUserEventHandler.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-25 09:26
 **/
public interface CommonAuthorizationGroupUserEventHandler {
	void wheneverCommonAuthorizationGroupUserSynced(String message, Acknowledgment ack) throws JsonProcessingException;
}
