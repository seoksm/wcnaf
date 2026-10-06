package com.winitech.apiGateway.domain.route;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.domain.route
 * └ RouteReader.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 12:35
 **/
public interface RouteReader {
    Route getRouteById(UUID id);

    Route getRouteByName(String name);

    Boolean existRouteByNameAndExcludingSelf(String name, UUID id);

    List<Route> getAllRoute();
	
    List<Route> getActiveRoute();

	Page<RouteInfo> getRoutePage(Integer page, Integer pageSize, String searchType, String searchKeyword, Route.Status status);
}
