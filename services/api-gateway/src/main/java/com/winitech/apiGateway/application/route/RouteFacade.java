package com.winitech.apiGateway.application.route;

import com.winitech.apiGateway.domain.route.Route;
import com.winitech.apiGateway.domain.route.RouteCommand;
import com.winitech.apiGateway.domain.route.RouteInfo;
import com.winitech.apiGateway.domain.route.RouteService;
import com.winitech.apiGateway.interfaces.outboundAdapter.eventProducer.route.RouteChangeEventProducer;
import com.winitech.apiGateway.interfaces.outboundAdapter.eventProducer.route.RouteChangeEventResponseDto;
import com.winitech.common.library.commonType.WiniPageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.application.route
 * └ RouteFacade.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 14:31
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class RouteFacade {
	private final RouteService routeService;
	
	private final RouteChangeEventProducer routeChangeEventProducer;

	public RouteInfo registerRoute(RouteCommand routeCommand) {
		RouteInfo routeInfo = routeService.registerRoute(routeCommand);
		
		routeChangeEventProducer.produce(RouteChangeEventResponseDto.builder().ts_ms((int) (System.currentTimeMillis()/ 1000)).build());
		
		return routeInfo;
	}

	public RouteInfo modifyRoute(UUID id, RouteCommand routeCommand) {
		RouteInfo routeInfo = routeService.modifyRoute(id, routeCommand);

		routeChangeEventProducer.produce(RouteChangeEventResponseDto.builder().ts_ms((int) (System.currentTimeMillis()/ 1000)).build());

		return routeInfo;
	}

	public void removeRoute(UUID id) {
		routeService.removeRoute(id);

		routeChangeEventProducer.produce(RouteChangeEventResponseDto.builder().ts_ms((int) (System.currentTimeMillis()/ 1000)).build());
	}

	public RouteInfo searchRouteById(UUID id) {
		return routeService.searchRouteById(id);
	}

	public RouteInfo searchRouteByRouteName(String routeName) {
		return routeService.searchRouteByRouteName(routeName);
	}

	public List<RouteInfo> getAllRoute() {
		return routeService.getAllRoute();
	}
	
	public List<RouteInfo> getActiveRoute() {
		return routeService.getActiveRoute();
	}
	
	public WiniPageInfo<RouteInfo> searchRoutePage(Integer page, Integer pageSize, String searchType, String searchKeyword, Route.Status status) {
		return routeService.searchRoutePage(page, pageSize, searchType, searchKeyword, status);
	}
}
