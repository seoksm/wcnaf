package com.winitech.apiGateway.domain.predicate;

import com.winitech.apiGateway.domain.route.RouteReader;
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
 * com.winitech.apiGateway.domain.predicate
 * └ PredicateServiceImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 12:59
 **/

@Slf4j
@Service
@RequiredArgsConstructor
public class PredicateServiceImpl implements PredicateService {

	private final PredicateStore predicateStore;
	private final PredicateReader predicateReader;
	private final RouteReader routeReader;

	@Override
	public PredicateInfo registerPredicate(UUID routeId, PredicateCommand predicateCommand) {
		Predicate initPredicate = predicateCommand.toEntity();
		initPredicate.setRoute(routeReader.getRouteById(routeId));

		initPredicate.setSystemStatus(Predicate.SystemStatus.ENABLE);

		Predicate predicate = predicateStore.store(initPredicate);

		return new PredicateInfo(predicate);
	}

	@Override
	public PredicateInfo modifyPredicate(UUID id, PredicateCommand predicateCommand) {
		Predicate modifyPredicate = predicateReader.getPredicateById(id);
		Predicate predicate = predicateStore.modify(modifyPredicate, predicateCommand);
	
		return new PredicateInfo(predicate);
	}

	@Override
	public void removePredicate(UUID id) {
		Predicate predicate = predicateReader.getPredicateById(id);
		predicate.disable();
		predicateStore.store(predicate);
	}

	@Override
	public PredicateInfo searchPredicateById(UUID id) {
		Predicate predicate = predicateReader.getPredicateById(id);
		return new PredicateInfo(predicate);
	}

	@Override
	public List<PredicateInfo> getAllPredicate() {
		List<Predicate> predicateList = predicateReader.getAllPredicate();
		return predicateList.stream().map(PredicateInfo::new).collect(Collectors.toList());
	}

	@Override
	public List<PredicateInfo> getAllPredicateByRouteId(UUID routeId) {
		return predicateReader.getAllPredicateByRouteId(routeId)
				.stream()
				.map(PredicateInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public List<PredicateInfo> getActivePredicateByRouteIdList(List<UUID> routeIdList) {
		return predicateReader.getActivePredicateByRouteIdList(routeIdList)
				.stream()
				.map(PredicateInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public WiniPageInfo<PredicateInfo> searchPredicatePage(UUID routeId, Integer page, Integer pageSize, String searchType, String searchKeyword, Predicate.PredicateType predicateType, Predicate.Status searchStatus) {
		Page<PredicateInfo> predicatePage = predicateReader.getPredicatePage(routeId, page, pageSize, searchType, searchKeyword, predicateType, searchStatus);
		return new WiniPageInfo<>(predicatePage);		
	}
}
