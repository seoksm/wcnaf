package com.winitech.apiGateway.interfaces.inboundAdapter.web;

import com.winitech.apiGateway.domain.predicate.PredicateCommand;
import com.winitech.apiGateway.domain.predicate.PredicateInfo;
import com.winitech.apiGateway.domain.route.RouteCommand;
import com.winitech.apiGateway.domain.route.RouteInfo;
import com.winitech.apiGateway.interfaces.inboundAdapter.spec.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PredicateDtoMapper {
    PredicateDtoMapper INSTANCE = Mappers.getMapper(PredicateDtoMapper.class);

    PredicateCommand toPredicateCommand(PredicateStoreRequestDto routeStoreRequestDto);
    PredicateStoreResponseDto toPredicateStoreResponseDto(PredicateInfo predicateInfo);
    PredicateResponseDto toPredicateResponseDto(PredicateInfo predicateInfo);
}
