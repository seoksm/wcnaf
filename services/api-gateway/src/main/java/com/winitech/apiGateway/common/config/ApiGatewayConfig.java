package com.winitech.apiGateway.common.config;

import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.domain.common.CommonAuthorizationUtilService;
import com.winitech.common.exception.UnauthorizedException;
import com.winitech.common.interceptor.CommonHttpRequestInterceptor;
import com.winitech.common.library.WiniCom;
import com.winitech.common.library.WiniSecurity;
import com.winitech.common.response.CommonResponse;
import com.winitech.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.common.config
 * └ ApiGatewayConfig.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 10:35
 **/
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApiGatewayConfig {
	@Value("${winitech.log.show-stacktrace:true}")
	private boolean shouldShowStackTrace;

	@Value("${winitech.log.show-full-stacktrace:false}")
	private boolean shouldShowFullStackTrace;

	@Value("${winitech.security.cors.origins:*}")
	private String[] corsOriginPattern;

    @Autowired
    List<HandlerMapping> mappings;

    @PostConstruct
    public void reorderMappings() {
        mappings.stream()
                .filter(m -> m instanceof RoutePredicateHandlerMapping)
                .forEach(m -> ((RoutePredicateHandlerMapping) m).setOrder(-1));
    }

	@Bean
	public CorsWebFilter corsWebFilter() {
		CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
		config.setAllowedOriginPatterns(Arrays.asList(corsOriginPattern));
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
		config.setAllowedHeaders(Arrays.asList("Authorization", "X-Org-Id", "Content-Type", "X-Restapi-Enc", "X-Menu-Id", "X-Program-Id", "X-Enc-Iv", "X-Enc-Key"));
		config.setExposedHeaders(Arrays.asList("Content-Disposition"));
		config.setAllowCredentials(true);
		config.setMaxAge(Duration.ofMinutes(60));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		CorsWebFilter corsWebFilter = new CorsWebFilter(source);
		return corsWebFilter;
	}
	
	@Bean
	@Order(-2)
	public ErrorWebExceptionHandler customErrorWebExceptionHandler(ApplicationContext applicationContext,
																   ServerCodecConfigurer serverCodecConfigurer) {
		CommonErrorWebExceptionHandler exceptionHandler = new CommonErrorWebExceptionHandler(applicationContext, serverCodecConfigurer);
		exceptionHandler.setShouldShowStackTrace(shouldShowStackTrace);
		exceptionHandler.setShouldShowFullStackTrace(shouldShowFullStackTrace);
		return exceptionHandler;
	}

	@Getter
	@Setter
	private static OffsetDateTime routingRuleLoadedTime = null;

	@Getter
	@Setter
	private static OffsetDateTime authorizationDataLoadedTime = null;
}
