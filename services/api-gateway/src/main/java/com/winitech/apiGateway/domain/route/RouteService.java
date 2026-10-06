package com.winitech.apiGateway.domain.route;

import com.winitech.common.library.commonType.WiniPageInfo;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.domain.route
 * └ RouteService.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 12:36
 **/
public interface RouteService {
	RouteInfo registerRoute(RouteCommand routeCommand);

	RouteInfo modifyRoute(UUID id, RouteCommand routeCommand);

	void removeRoute(UUID id);

	RouteInfo searchRouteById(UUID id);

	RouteInfo searchRouteByRouteName(String routeName);

	List<RouteInfo> getAllRoute();

	List<RouteInfo> getActiveRoute();

	WiniPageInfo<RouteInfo> searchRoutePage(Integer page, Integer pageSize, String searchType, String searchKeyword, Route.Status status);
}
