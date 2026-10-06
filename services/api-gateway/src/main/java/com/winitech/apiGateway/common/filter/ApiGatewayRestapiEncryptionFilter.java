package com.winitech.apiGateway.common.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.winitech.common.application.common.CommonEncFacade;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(-1)
public class ApiGatewayRestapiEncryptionFilter implements WebFilter {
    private final CommonEncFacade commonEncFacade;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String pubkey = request.getHeaders().getFirst("X-Enc-Key");
        String iv = request.getHeaders().getFirst("X-Enc-Iv");

        // 1. 암호화 헤더가 없으면 그대로 통과
        if (pubkey == null || iv == null) {
            return chain.filter(exchange);
        }
        else{
            return DataBufferUtils.join(request.getBody()).flatMap(dataBuffer -> {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                DataBufferUtils.release(dataBuffer);

                byte[] decrypted = commonEncFacade.getBody(pubkey, iv, bytes);

                if(decrypted == null){
                    return chain.filter(exchange.mutate().request(request).build()).then(Mono.fromRunnable(() -> {
                    }));
                }
                else {
                    ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(request) {
                        @Override
                        public HttpHeaders getHeaders() {
                            HttpHeaders headers = new HttpHeaders();
                            headers.putAll(super.getHeaders());
                            headers.remove("X-Enc-Key");
                            headers.remove("X-Enc-Iv");
                            // 복호화된 바디 길이에 맞춰 Content-Length 갱신
                            headers.setContentLength(decrypted.length);
                            return headers;
                        }

                        @Override
                        public Flux<DataBuffer> getBody() {
                            return Flux.just(DefaultDataBufferFactory.sharedInstance.wrap(decrypted));
                        }
                    };
                    return chain.filter(exchange.mutate().request(mutatedRequest).build()).then(Mono.fromRunnable(() -> {
                    }));
                }

            });
        }
    }

}