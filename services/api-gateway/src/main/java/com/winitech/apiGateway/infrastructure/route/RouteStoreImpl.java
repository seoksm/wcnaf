package com.winitech.apiGateway.infrastructure.route;

import com.winitech.apiGateway.domain.route.Route;
import com.winitech.apiGateway.domain.route.RouteCommand;
import com.winitech.apiGateway.domain.route.RouteStore;
import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.common.exception.InvalidParamException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.infrastructure.route
 * └ RouteStoreImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 13:03
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class RouteStoreImpl implements RouteStore {
	private final RouteRepository routeRepository;

	// Spring Cloud Gateway 라우팅에서 실제로 쓰이는 스킴만 허용 - file:// 등 다른 스킴으로의
	// 라우트 등록을 막는다(이 API 자체는 관리자 메뉴권한으로 보호되지만 방어심층 차원).
	private static final List<String> ALLOWED_URI_SCHEMES = List.of("http://", "https://", "lb://");

	private static void validateUri(String uri) {
		if (ALLOWED_URI_SCHEMES.stream().noneMatch(uri::startsWith)) {
			throw new InvalidParamException("route.getUri()가 허용되지 않은 스킴입니다. (허용: http, https, lb)");
		}
	}

	@Override
	public Route store(Route route) {
		if(StringUtils.isEmpty(route.getName())) throw new InvalidParamException("route.getName()");
		if(StringUtils.isEmpty(route.getUri())) throw new InvalidParamException("route.getUri()");
		if(route.getSortSeq() == null) throw new InvalidParamException("route.getSortSeq()");
		validateUri(route.getUri());

		return routeRepository.save(route);
	}

	@Override
	public Route modify(Route route, RouteCommand command) {
		if(StringUtils.isEmpty(route.getName())) throw new InvalidParamException("route.getName()");
		if(StringUtils.isEmpty(route.getUri())) throw new InvalidParamException("route.getUri()");
		if(route.getSortSeq() == null) throw new InvalidParamException("route.getSortSeq()");
		validateUri(command.getUri());

		route.setName(command.getName());
        route.setUri(command.getUri());
		route.setSortSeq(command.getSortSeq());
        route.setStatus(command.getStatus());
		return routeRepository.save(route);
	}
}
