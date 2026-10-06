package com.winitech.apiGateway.common.filter;

import com.winitech.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.ognl.MethodFailedException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(-3)
public class MethodFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpMethod method = request.getMethod();

        //preflight 통과
        if (method == HttpMethod.OPTIONS && request.getHeaders().containsKey("Access-Control-Request-Method")) {
            return chain.filter(exchange);
        }
        else if (method == HttpMethod.OPTIONS || method == HttpMethod.HEAD) {
            return Mono.error(new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED));
        }
        else {
            return chain.filter(exchange);
        }

    }
}
