package com.winitech.apiGateway.interfaces.inboundAdapter.web;

import com.winitech.apiGateway.domain.route.RouteCommand;
import com.winitech.apiGateway.domain.route.RouteInfo;
import com.winitech.apiGateway.interfaces.inboundAdapter.spec.RouteResponseDto;
import com.winitech.apiGateway.interfaces.inboundAdapter.spec.RouteStoreRequestDto;
import com.winitech.apiGateway.interfaces.inboundAdapter.spec.RouteStoreResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RouteDtoMapper {
    RouteDtoMapper INSTANCE = Mappers.getMapper(RouteDtoMapper.class);

    RouteCommand toRouteCommand(RouteStoreRequestDto routeStoreRequestDto);
    RouteStoreResponseDto toRouteStoreResponseDto(RouteInfo routeInfo);
    RouteResponseDto toRouteResponseDto(RouteInfo routeInfo);
}
