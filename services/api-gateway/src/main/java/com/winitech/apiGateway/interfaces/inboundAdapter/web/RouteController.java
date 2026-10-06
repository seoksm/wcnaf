package com.winitech.apiGateway.interfaces.inboundAdapter.web;

import com.winitech.apiGateway.application.route.RouteFacade;
import com.winitech.apiGateway.common.ApiGatewayUtil;
import com.winitech.apiGateway.domain.predicate.Predicate;
import com.winitech.apiGateway.domain.route.Route;
import com.winitech.apiGateway.domain.route.RouteCommand;
import com.winitech.apiGateway.domain.route.RouteInfo;
import com.winitech.apiGateway.interfaces.inboundAdapter.spec.*;
import com.winitech.common.domain.commonJobGroup.CommonJobGroup;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.common.response.CommonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.apiGateway.interfaces.inboundAdapter.web
 * └ RouteController.java
 * </pre>
 * @author : yhpark
 * @since : 2025-11-05
 **/
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class RouteController implements RouteApi {
	private final ApplicationEventPublisher applicationEventPublisher;
	private final RouteFacade routeFacade;

    @Override
    public CommonResponse<RouteStoreResponseDto> registerRoute(RouteStoreRequestDto routeStoreRequestDto) {
        RouteCommand command = RouteDtoMapper.INSTANCE.toRouteCommand(routeStoreRequestDto);
        RouteInfo routeInfo = routeFacade.registerRoute(command);
        RouteStoreResponseDto response = RouteDtoMapper.INSTANCE.toRouteStoreResponseDto(routeInfo);
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
        return CommonResponse.success(response);
    }

    @Override
    public CommonResponse<String> removeRoute(UUID routeId) {
        routeFacade.removeRoute(routeId);
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<List<RouteResponseDto>> searchAllRoute() {
        List<RouteInfo> userInfoList = routeFacade.getAllRoute();
        List<RouteResponseDto> response = userInfoList.stream()
                .map(RouteDtoMapper.INSTANCE::toRouteResponseDto).collect(Collectors.toList());
        return CommonResponse.success(response);
    }

    @Override
    public CommonResponse<List<RouteResponseDto>> searchRoute(Integer page, Integer pageSize, String searchType, String searchKeyword, String searchStatus) {

        Route.Status statusEnum = null;
        if(searchStatus != null && !searchStatus.isEmpty()) statusEnum = Route.Status.valueOf(searchStatus);

        WiniPageInfo<RouteInfo> routePageInfo = routeFacade.searchRoutePage(page, pageSize, searchType, searchKeyword, statusEnum);
        List<RouteResponseDto> response = routePageInfo
                .stream()
                .map(RouteDtoMapper.INSTANCE::toRouteResponseDto)
                .collect(Collectors.toList());
        return CommonResponse.success(response, routePageInfo);
    }

    @Override
    public CommonResponse<RouteResponseDto> searchRouteInfo(UUID routeId) {
        RouteInfo routeInfo = routeFacade.searchRouteById(routeId);
        RouteResponseDto response = RouteDtoMapper.INSTANCE.toRouteResponseDto(routeInfo);
        return CommonResponse.success(response);
    }

    @Override
    public CommonResponse<RouteStoreResponseDto> modifyRoute(UUID routeId, RouteStoreRequestDto routeStoreRequestDto) {
        RouteCommand command = RouteDtoMapper.INSTANCE.toRouteCommand(routeStoreRequestDto);
        RouteInfo routeInfo = routeFacade.modifyRoute(routeId, command);
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
        RouteStoreResponseDto response = RouteDtoMapper.INSTANCE.toRouteStoreResponseDto(routeInfo);
        return CommonResponse.success(response);
    }

    @Override
    public CommonResponse<String> reloadRoute() {
        return CommonResponse.success("Reload requested");
    }

}
