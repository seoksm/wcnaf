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

	@Override
	public Route store(Route route) {
		if(StringUtils.isEmpty(route.getName())) throw new InvalidParamException("route.getName()");
		if(StringUtils.isEmpty(route.getUri())) throw new InvalidParamException("route.getUri()");
		if(route.getSortSeq() == null) throw new InvalidParamException("route.getSortSeq()");

		return routeRepository.save(route);
	}

	@Override
	public Route modify(Route route, RouteCommand command) {
		if(StringUtils.isEmpty(route.getName())) throw new InvalidParamException("route.getName()");
		if(StringUtils.isEmpty(route.getUri())) throw new InvalidParamException("route.getUri()");
		if(route.getSortSeq() == null) throw new InvalidParamException("route.getSortSeq()");

		route.setName(command.getName());
        route.setUri(command.getUri());
		route.setSortSeq(command.getSortSeq());
        route.setStatus(command.getStatus());
		return routeRepository.save(route);
	}
}
