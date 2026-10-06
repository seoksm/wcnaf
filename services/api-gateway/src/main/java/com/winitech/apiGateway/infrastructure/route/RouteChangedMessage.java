package com.winitech.apiGateway.infrastructure.route;

import com.winitech.common.interfaces.inboundAdapter.eventListener.CommonUserChangedMessage;
import lombok.*;

/**
 * <pre>
 * com.winitech.apiGateway.infrastructure.route
 * └ RouteChangedMessage.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-08 09:16
 **/
@Data
@NoArgsConstructor
public class RouteChangedMessage {
	Integer ts_ms;

	@Builder
	public RouteChangedMessage(Integer ts_ms) {
		this.ts_ms = ts_ms;
	}
}
