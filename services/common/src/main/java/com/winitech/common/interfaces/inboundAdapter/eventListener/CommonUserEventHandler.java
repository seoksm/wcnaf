package com.winitech.common.interfaces.inboundAdapter.eventListener;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * <pre>
 * com.winitech.common.interfaces.inboundAdapter.eventListener
 * └ CommonUserEventHandler.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-21 13:09
 **/
public interface CommonUserEventHandler {
	void wheneverCommonUserUpdated(String message) throws JsonProcessingException;
}
