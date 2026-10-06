package com.winitech.apiGateway.interfaces.inboundAdapter.actuator;

import com.winitech.apiGateway.common.config.ApiGatewayConfig;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Actuator를 사용하여 API Gateway의 상태를 확인하는 엔드포인트입니다.
 * <pre>
 * com.winitech.apiGateway.interfaces.inboundAdapter.actuator
 * └ ApiGatewayInfoActuatorEndpoint.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-14 18:09
 **/
@Component
@Endpoint(id = "apiGatewayInfo")
public class ApiGatewayInfoActuatorEndpoint {
	@ReadOperation
	public Map<String, String> getApiGatewayInfo() {
		return Map.of(
				"routingRuleLoadedTime", String.valueOf(ApiGatewayConfig.getRoutingRuleLoadedTime()),
				"authorizationDataLoadedTime", String.valueOf(ApiGatewayConfig.getAuthorizationDataLoadedTime())
		);
	}
}
