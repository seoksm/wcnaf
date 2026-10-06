package com.winitech.apiGateway.common.filter;

import com.winitech.common.interceptor.CommonHttpRequestInterceptor;
import com.winitech.common.library.WiniCom;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.common.filter
 * └ RequestIdFilter.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-16 16:06
 **/
@Slf4j
@Component
@Order(-2)
public class RequestIdFilter implements WebFilter {
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		String requestId = exchange.getRequest().getHeaders().getFirst(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);

		if (requestId == null || requestId.isEmpty()) {
			requestId = WiniCom.generateRequestId();

			ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
					.header(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY, requestId)
					.build();

			ServerWebExchange mutatedExchange = exchange.mutate()
					.request(mutatedRequest)
					.build();

			MDC.put(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY, requestId);

			return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {
			}));
		}

		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			// Do something after the request is processed
		}));
	}
}
