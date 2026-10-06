package com.winitech.apiGateway.domain.route;

import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.library.commonType.WiniPageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.apiGateway.domain.route
 * └ RouteServiceImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 12:37
 **/

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

	private final RouteStore routeStore;
	private final RouteReader routeReader;

	@Override
	public RouteInfo registerRoute(RouteCommand routeCommand) {
		if (Boolean.TRUE.equals(routeReader.existRouteByNameAndExcludingSelf(routeCommand.getName(), UUID.randomUUID()))) {
			throw new IllegalStatusException("Already registered Route Name.");
		}
		Route initRoute = routeCommand.toEntity();
		initRoute.setSystemStatus(Route.SystemStatus.ENABLE);
		Route route = routeStore.store(initRoute);
		return new RouteInfo(route);
	}

	@Override
	public RouteInfo modifyRoute(UUID id, RouteCommand routeCommand) {
		if (Boolean.TRUE.equals(routeReader.existRouteByNameAndExcludingSelf(routeCommand.getName(), id))) {
			throw new IllegalStatusException("Already registered Route Name.");
		}
		Route modifyRoute = routeReader.getRouteById(id);
		Route route = routeStore.modify(modifyRoute, routeCommand);
		return new RouteInfo(route);
	}

	@Override
	public void removeRoute(UUID id) {
		Route route = routeReader.getRouteById(id);
		route.disable();
		routeStore.store(route);
	}

	@Override
	public RouteInfo searchRouteById(UUID id) {
		Route route = routeReader.getRouteById(id);
		return new RouteInfo(route);
	}

	@Override
	public RouteInfo searchRouteByRouteName(String routeName) {
		Route route = routeReader.getRouteByName(routeName);
		return new RouteInfo(route);
	}
	
	@Override
	public List<RouteInfo> getAllRoute() {
		List<Route> routeList = routeReader.getAllRoute();
		return routeList.stream().map(RouteInfo::new).collect(Collectors.toList());
	}
	
	@Override
	public List<RouteInfo> getActiveRoute() {
		List<Route> routeList = routeReader.getActiveRoute();
		return routeList.stream().map(RouteInfo::new).collect(Collectors.toList());
	}

	@Override
	public WiniPageInfo<RouteInfo> searchRoutePage(Integer page, Integer pageSize, String searchType, String searchKeyword, Route.Status status) {
		Page<RouteInfo> commonRoutePage = routeReader.getRoutePage(page, pageSize, searchType, searchKeyword, status);

		return new WiniPageInfo<>(commonRoutePage);
	}
}
