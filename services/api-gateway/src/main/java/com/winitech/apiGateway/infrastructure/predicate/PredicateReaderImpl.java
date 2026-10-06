package com.winitech.apiGateway.infrastructure.predicate;

import com.winitech.apiGateway.domain.predicate.Predicate;
import com.winitech.apiGateway.domain.predicate.PredicateInfo;
import com.winitech.apiGateway.domain.predicate.PredicateReader;
import com.winitech.apiGateway.domain.route.Route;
import com.winitech.apiGateway.domain.route.RouteInfo;
import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.common.library.WiniCom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.infrastructure.predicate
 * └ PredicateReaderImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 13:04
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class PredicateReaderImpl implements PredicateReader {
	private final PredicateRepository predicateRepository;
	private final PredicateQueryRepository predicateQueryRepository;

	@Override
	public Predicate getPredicateById(UUID id) {
		return predicateRepository.findByIdAndSystemStatus(id, Predicate.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public List<Predicate> getAllPredicate() {
		return predicateRepository.findAllBySystemStatus(Predicate.SystemStatus.ENABLE);
	}

	@Override
	public List<Predicate> getAllPredicateByRouteId(UUID routeId) {
		return predicateRepository.findAllByRouteIdAndSystemStatus(routeId, Predicate.SystemStatus.ENABLE);
	}

	@Override
	public List<Predicate> getActivePredicateByRouteIdList(List<UUID> routeIdList) {
		return predicateRepository.findAllByRouteIdInAndStatusAndSystemStatus(routeIdList, Predicate.Status.ENABLE, Predicate.SystemStatus.ENABLE);
	}

	@Override
	public Page<PredicateInfo> getPredicatePage(UUID routeId, Integer page, Integer pageSize, String searchType, String searchKeyword, Predicate.PredicateType predicateType, Predicate.Status status) {
		return predicateQueryRepository.findAllPage(routeId, searchType, searchKeyword, predicateType, status, WiniCom.getPageRequest(page, pageSize));
	}
}
