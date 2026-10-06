package com.winitech.common.interfaces.inboundAdapter;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * <pre>
 * com.winitech.common.interfaces.inboundAdapter
 * └ CommonUserEventHandlerForMultitenant.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-09-19 10:25
 **/
public interface CommonUserEventHandlerForMultitenant {
	void wheneverCommonUserUpdated(String message) throws JsonProcessingException;
}
