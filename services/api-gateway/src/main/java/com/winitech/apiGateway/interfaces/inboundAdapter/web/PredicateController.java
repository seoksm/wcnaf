package com.winitech.apiGateway.interfaces.inboundAdapter.web;

import com.winitech.apiGateway.application.predicate.PredicateFacade;
import com.winitech.apiGateway.common.ApiGatewayUtil;
import com.winitech.apiGateway.domain.predicate.Predicate;
import com.winitech.apiGateway.domain.predicate.PredicateCommand;
import com.winitech.apiGateway.domain.predicate.PredicateInfo;
import com.winitech.apiGateway.interfaces.inboundAdapter.spec.PredicateApi;
import com.winitech.apiGateway.interfaces.inboundAdapter.spec.PredicateResponseDto;
import com.winitech.apiGateway.interfaces.inboundAdapter.spec.PredicateStoreRequestDto;
import com.winitech.apiGateway.interfaces.inboundAdapter.spec.PredicateStoreResponseDto;
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
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.apiGateway.interfaces.inboundAdapter.web
 * └ PredicateController.java
 * </pre>
 * @author : yhpark
 * @since : 2025-11-05
 **/
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class PredicateController implements PredicateApi {
	private final PredicateFacade predicateFacade;
	private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public CommonResponse<PredicateStoreResponseDto> modifyPredicate(UUID routeId, UUID predicateId, PredicateStoreRequestDto predicateStoreRequestDto) {
        PredicateCommand command = PredicateDtoMapper.INSTANCE.toPredicateCommand(predicateStoreRequestDto);
        PredicateInfo departmentInfo = predicateFacade.modifyPredicate(predicateId, command);
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
        PredicateStoreResponseDto response = PredicateDtoMapper.INSTANCE.toPredicateStoreResponseDto(departmentInfo);
        return CommonResponse.success(response);
    }

    @Override
    public CommonResponse<PredicateStoreResponseDto> registerPredicate(UUID routeId, PredicateStoreRequestDto predicateStoreRequestDto) {
        PredicateCommand command = PredicateDtoMapper.INSTANCE.toPredicateCommand(predicateStoreRequestDto);
        PredicateInfo predicateInfo = predicateFacade.registerPredicate(routeId, command);
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
        PredicateStoreResponseDto response = PredicateDtoMapper.INSTANCE.toPredicateStoreResponseDto(predicateInfo);
        return  CommonResponse.success(response);
    }

    @Override
    public CommonResponse<String> removePredicate(UUID routeId, UUID predicateId) {
        predicateFacade.removePredicate(predicateId);
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
        return CommonResponse.success("OK");
    }

    @Override
    public CommonResponse<List<PredicateResponseDto>> searchAllPredicate(UUID routeId, Integer page, Integer pageSize, String searchType, String searchKeyword, String searchStatus, String searchPredicateType) {

        Predicate.PredicateType typeEnum = null;
        if(searchPredicateType != null && !searchPredicateType.isEmpty()) typeEnum = Predicate.PredicateType.valueOf(searchPredicateType);

        Predicate.Status statusEnum = null;
        if(searchStatus != null && !searchStatus.isEmpty()) statusEnum = Predicate.Status.valueOf(searchStatus);

        WiniPageInfo<PredicateInfo> predicatePageInfo = predicateFacade.searchPredicatePage(routeId, page, pageSize, searchType, searchKeyword, typeEnum, statusEnum);
        List<PredicateResponseDto> response = predicatePageInfo
                .stream()
                .map(PredicateDtoMapper.INSTANCE::toPredicateResponseDto)
                .collect(Collectors.toList());
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));

        return CommonResponse.success(response, predicatePageInfo);
    }

    @Override
    public CommonResponse<PredicateResponseDto> searchPredicateInfo(UUID routeId, UUID predicateId) {
        PredicateInfo departmentInfo = predicateFacade.searchPredicateById(predicateId);
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
        PredicateResponseDto response = PredicateDtoMapper.INSTANCE.toPredicateResponseDto(departmentInfo);
        return CommonResponse.success(response);
    }


}
