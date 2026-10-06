package com.winitech.apiGateway.domain.predicate;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.domain.predicate
 * └ PredicateReader.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 12:55
 **/
public interface PredicateReader {
	Predicate getPredicateById(UUID id);

	List<Predicate> getAllPredicate();

	List<Predicate> getAllPredicateByRouteId(UUID routeId);

	List<Predicate> getActivePredicateByRouteIdList(List<UUID> routeIdList);

	Page<PredicateInfo> getPredicatePage(UUID routeId, Integer page, Integer pageSize, String searchType, String searchKeyword, Predicate.PredicateType predicateType, Predicate.Status status);
}