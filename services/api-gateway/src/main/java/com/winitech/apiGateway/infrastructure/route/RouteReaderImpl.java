package com.winitech.apiGateway.infrastructure.route;

import com.winitech.apiGateway.domain.route.Route;
import com.winitech.apiGateway.domain.route.RouteInfo;
import com.winitech.apiGateway.domain.route.RouteReader;
import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.common.library.WiniCom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static com.winitech.apiGateway.domain.route.QRoute.route;

/**
 * <pre>
 * com.winitech.apiGateway.infrastructure.route
 * └ RouteReaderImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 13:03
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class RouteReaderImpl implements RouteReader {
	private final RouteRepository routeRepository;
	private final RouteQueryRepository routeQueryRepository;

	@Override
	public Route getRouteById(UUID id) {
		return routeRepository.findByIdAndSystemStatus(id, Route.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public Route getRouteByName(String name) {
		return routeRepository.findByNameAndSystemStatus(name, Route.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public Boolean existRouteByNameAndExcludingSelf(String name, UUID id) {
		return routeRepository.existsRouteByNameAndSystemStatusAndIdNot(name, Route.SystemStatus.ENABLE, id);
	}

	@Override
	public List<Route> getAllRoute() {
		return routeRepository.findAllBySystemStatusOrderBySortSeqAscNameAscIdAsc(Route.SystemStatus.ENABLE);
	}

	@Override
	public List<Route> getActiveRoute() {
		return routeRepository.findAllByStatusAndSystemStatusOrderBySortSeqAscNameAscIdAsc(Route.Status.ENABLE, Route.SystemStatus.ENABLE);
	}

	@Override
	public Page<RouteInfo> getRoutePage(Integer page, Integer pageSize, String searchType, String searchKeyword, Route.Status status) {
		return routeQueryRepository.findAllPage(searchType, searchKeyword, status, WiniCom.getPageRequest(page, pageSize));
	}
}
