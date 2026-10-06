package com.winitech.apiGateway.common.config;

import com.winitech.apiGateway.application.predicate.PredicateFacade;
import com.winitech.apiGateway.application.route.RouteFacade;
import com.winitech.apiGateway.domain.predicate.PredicateInfo;
import com.winitech.apiGateway.domain.route.RouteInfo;
import com.winitech.common.interceptor.CommonHttpRequestInterceptor;
import com.winitech.common.library.WiniCom;
import com.winitech.common.library.WiniSecurity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.factory.DedupeResponseHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Spring Cloud Gateway에서 Route를 동적으로 관리하기 위한 RouteLocator 구현체입니다.
 * <pre>
 * com.winitech.apiGateway.common.config
 * └ RouteLocatorImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 15:48
 **/
@Slf4j
@RequiredArgsConstructor
@Component
public class RouteLocatorImpl implements RouteLocator {
	@Value("${winitech.security.api-gateway.use-magic-header}")
	private Boolean useApiGatewayMagicHeader;
	
	@Value("${winitech.security.api-gateway.magic-header}")
	private String apiGatewayMagicHeader;

	@Value("${winitech.security.api-gateway.magic-header-secret:}")
	private String apiGatewayMagicHeaderSecret;

	private static final int MIN_MAGIC_HEADER_SECRET_BYTES = 32;

	@javax.annotation.PostConstruct
	private void validateMagicHeaderSecret() {
		if (Boolean.TRUE.equals(useApiGatewayMagicHeader)
				&& apiGatewayMagicHeaderSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8).length < MIN_MAGIC_HEADER_SECRET_BYTES) {
			throw new IllegalStateException(
					"winitech.security.api-gateway.magic-header-secret(환경변수 WINITECH_API_GATEWAY_MAGIC_HEADER_SECRET)이 "
							+ "설정되지 않았거나 너무 짧습니다. 최소 " + MIN_MAGIC_HEADER_SECRET_BYTES + "바이트 이상의 무작위 값이 필요합니다.");
		}
	}

	private final RouteLocatorBuilder routeLocatorBuilder;
	private final RouteFacade routeFacade;
	private final PredicateFacade predicateFacade;
	private final DedupeResponseHeaderGatewayFilterFactory dedupeResponseHeaderFilterFactory;

	@Override
	public Flux<Route> getRoutes() {
		RouteLocatorBuilder.Builder builder = routeLocatorBuilder.routes();

		List<RouteInfo> routeList = routeFacade.getActiveRoute();
		
		List<UUID> routeIdList = routeList
				.stream()
				.map(RouteInfo::getId)
				.collect(Collectors.toList());
		
		List<PredicateInfo> predicateInfoList = predicateFacade.getActivePredicateByRouteIdList(routeIdList);
		Map<UUID, List<PredicateInfo>> predicateMap = predicateInfoList.stream()
				.collect(Collectors.groupingBy(PredicateInfo::getRouteId));

		for (RouteInfo routeInfo : routeList) {
			if (! predicateMap.containsKey(routeInfo.getId())) {
				continue;
			}

			builder = builder.route(routeInfo.getName(), r -> {
				PredicateSpec spec = r;
				BooleanSpec booleanSpec = null;
				
				r.order(routeInfo.getSortSeq() == null ? 0 : routeInfo.getSortSeq());
				
				for (PredicateInfo predicateInfo : predicateMap.get(routeInfo.getId())) {
					if (booleanSpec != null) {
						spec = booleanSpec.and();
					}
					
					switch (predicateInfo.getPredicateType()) {
						case COOKIE:
							booleanSpec = spec.cookie(predicateInfo.getKey(), "".equals(predicateInfo.getDefinition()) ? null : predicateInfo.getDefinition());
							break;
						case HEADER:
							booleanSpec = spec.header(predicateInfo.getKey(), "".equals(predicateInfo.getDefinition()) ? null : predicateInfo.getDefinition());
							break;
						case METHOD:
							booleanSpec = spec.method(predicateInfo.getKey().split("[,\\s]+"));
							break;
						case PATH:
							booleanSpec = spec.path(predicateInfo.getKey().split("[,\\s]+"));
							break;
						case HOST:
							booleanSpec = spec.host(predicateInfo.getKey().split("[,\\s]+"));
							break;
						case QUERY:
							booleanSpec = spec.query(predicateInfo.getKey(), "".equals(predicateInfo.getDefinition()) ? null : predicateInfo.getDefinition());
							break;
						case WEIGHT:
							int weight;
							try {
								weight = Integer.parseInt(predicateInfo.getDefinition());
							} catch (NumberFormatException | NullPointerException e) {
								weight = 1;
							}
							booleanSpec = spec.weight(predicateInfo.getKey(), weight);
							break;
						default:
							throw new IllegalArgumentException("Unknown predicate type: " + predicateInfo.getPredicateType());
					}
				}

				if (booleanSpec == null) {					
					return spec.uri(routeInfo.getUri());
				} else {
//					return booleanSpec.uri(routeInfo.getUri());
					return booleanSpec
							.filters(defaultRouteFilter())
							.uri(routeInfo.getUri());
				}
			});
		}

		// 마지막 라우팅 규칙 업데이트 시간 설정
		OffsetDateTime lastUpdateAt = getLastUpdateAt(routeList, predicateInfoList);

		ApiGatewayConfig.setRoutingRuleLoadedTime(lastUpdateAt);
		
		return builder.build().getRoutes();
	}

	private static OffsetDateTime getLastUpdateAt(List<RouteInfo> routeList, List<PredicateInfo> predicateInfoList) {
		OffsetDateTime maxRouteUpdateAt = routeList.stream()
				.map(RouteInfo::getUpdateAt)
				.max(Comparator.nullsFirst(Comparator.naturalOrder()))
				.orElse(null);

		OffsetDateTime maxPredicateUpdateAt = predicateInfoList.stream()
				.map(PredicateInfo::getUpdateAt)
				.max(Comparator.nullsFirst(Comparator.naturalOrder()))
				.orElse(null);

		OffsetDateTime lastUpdateAt = Collections.max(Arrays.asList(
				maxRouteUpdateAt,
				maxPredicateUpdateAt
		), Comparator.nullsFirst(Comparator.naturalOrder()));
		
		return lastUpdateAt;
	}

	private Function<GatewayFilterSpec, UriSpec> defaultRouteFilter() {
		// 중복된 헤더가 있는 경우 어느 헤더를 유지할지 지정
		// RETAIN_FIRST: API Gateway의 헤더 사용
		// RETAIN_LAST: Backend의 헤더 사용
		String strategy = DedupeResponseHeaderGatewayFilterFactory.Strategy.RETAIN_LAST.name();

		return (f) -> {
			if (useApiGatewayMagicHeader) {
				f = f.filter((exchange, chain) -> {
					ServerHttpRequest.Builder mutatedRequest = exchange.getRequest().mutate();

					String requestId = exchange.getRequest().getHeaders().getFirst(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY);

					if (requestId == null || requestId.isEmpty()) {
						requestId = WiniCom.generateRequestId();
						mutatedRequest.header(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY, requestId);

						MDC.put(CommonHttpRequestInterceptor.HEADER_REQUEST_UUID_KEY, requestId);
					}

					mutatedRequest.header(apiGatewayMagicHeader, WiniSecurity.hashMd5(apiGatewayMagicHeaderSecret + requestId));
					
					ServerWebExchange mutatedExchange = exchange
							.mutate()
							.request(mutatedRequest.build())
							.build();

					return chain.filter(mutatedExchange);
				});
			}

			f = f.dedupeResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, strategy)
					.dedupeResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, strategy)
					.dedupeResponseHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, strategy)
					.dedupeResponseHeader(HttpHeaders.VARY, strategy);

			return f;
		};
	}
}
