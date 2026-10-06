package com.winitech.apiGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
        scanBasePackages = "com.winitech"   // 기본적으로 Application의 패키지 및 하위패키지의 컴포넌트를 검색하지만, 
                                            // common을 추가로 금색하기 위해서 전체에서 검색 하도록 변경
)
@EnableJpaRepositories(basePackages = "com.winitech.**.infrastructure")
@EntityScan(basePackages = "com.winitech.**.domain")
@ComponentScan(basePackages = "com.winitech.**", 
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.winitech\\.common\\.interfaces\\.inboundAdapter\\.web\\.([a-zA-Z0-9]+)Controller"),
        }
)
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
    
    @Bean
    public ReactiveWebServerFactory reactiveWebServerFactory() {
        return new NettyReactiveWebServerFactory();
    }
}
