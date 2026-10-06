package com.winitech.apiGateway.application.predicate;

import com.winitech.apiGateway.domain.predicate.Predicate;
import com.winitech.apiGateway.infrastructure.route.RouteChangeEventProducerImpl;
import com.winitech.apiGateway.interfaces.outboundAdapter.eventProducer.route.RouteChangeEventProducer;
import com.winitech.apiGateway.interfaces.outboundAdapter.eventProducer.route.RouteChangeEventResponseDto;
import com.winitech.common.library.commonType.WiniPageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.winitech.apiGateway.domain.predicate.PredicateService;
import com.winitech.apiGateway.domain.predicate.PredicateInfo;
import com.winitech.apiGateway.domain.predicate.PredicateCommand;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.application
 * └ PredicateFacade.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 14:31
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class PredicateFacade {
	private final PredicateService predicateService;

	private final RouteChangeEventProducer routeChangeEventProducer;

	public PredicateInfo registerPredicate(UUID routeId, PredicateCommand predicateFacadeCommand) {
		PredicateInfo predicateInfo = predicateService.registerPredicate(routeId, predicateFacadeCommand);

		routeChangeEventProducer.produce(RouteChangeEventResponseDto.builder().ts_ms((int) (System.currentTimeMillis()/ 1000)).build());
		
		return predicateInfo;
	}

	public PredicateInfo modifyPredicate(UUID id, PredicateCommand predicateFacadeCommand) {
		PredicateInfo predicateInfo = predicateService.modifyPredicate(id, predicateFacadeCommand);

		routeChangeEventProducer.produce(RouteChangeEventResponseDto.builder().ts_ms((int) (System.currentTimeMillis()/ 1000)).build());

		return predicateInfo;
	}

	public void removePredicate(UUID id) {
		predicateService.removePredicate(id);

		routeChangeEventProducer.produce(RouteChangeEventResponseDto.builder().ts_ms((int) (System.currentTimeMillis()/ 1000)).build());
	}

	public PredicateInfo searchPredicateById(UUID id) {
		return predicateService.searchPredicateById(id);
	}

	public List<PredicateInfo> getAllPredicate() {
		return predicateService.getAllPredicate();
	}

	public List<PredicateInfo> getAllPredicateByRouteId(UUID routeId) {
		return predicateService.getAllPredicateByRouteId(routeId);
	}

	public List<PredicateInfo> getActivePredicateByRouteIdList(List<UUID> routeIdList) {
		return predicateService.getActivePredicateByRouteIdList(routeIdList);
	}

	public WiniPageInfo<PredicateInfo> searchPredicatePage(UUID routeId, Integer page, Integer pageSize, String searchType, String searchKeyword, Predicate.PredicateType predicateType, Predicate.Status searchStatus) {
		return predicateService.searchPredicatePage(routeId, page, pageSize, searchType, searchKeyword, predicateType, searchStatus);
	}
}
