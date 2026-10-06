package com.winitech.apiGateway.domain.predicate;

import com.winitech.common.library.commonType.WiniPageInfo;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.domain.predicate
 * └ PredicateService.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 12:59
 **/
public interface PredicateService {
	PredicateInfo registerPredicate(UUID routeId, PredicateCommand predicateCommand);

	PredicateInfo modifyPredicate(UUID id, PredicateCommand predicateCommand);

	void removePredicate(UUID id);

	PredicateInfo searchPredicateById(UUID id);

	List<PredicateInfo> getAllPredicate();

	List<PredicateInfo> getAllPredicateByRouteId(UUID routeId);

	List<PredicateInfo> getActivePredicateByRouteIdList(List<UUID> routeIdList);

	WiniPageInfo<PredicateInfo> searchPredicatePage(UUID routeId, Integer page, Integer pageSize, String searchType, String searchKeyword, Predicate.PredicateType predicateType, Predicate.Status searchStatus);
}
