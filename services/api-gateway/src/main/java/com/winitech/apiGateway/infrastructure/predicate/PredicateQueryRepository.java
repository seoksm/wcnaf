package com.winitech.apiGateway.infrastructure.predicate;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.winitech.apiGateway.domain.predicate.Predicate;
import com.winitech.apiGateway.domain.predicate.PredicateInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.winitech.apiGateway.domain.predicate.QPredicate.predicate;

/**
 * <pre>
 * com.winitech.apiGateway.infrastructure.predicate
 * └ PredicateQueryRepository.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-07 09:30
 **/
@Repository
@RequiredArgsConstructor
public class PredicateQueryRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public List<PredicateInfo> findAll(UUID routeId, String searchType, String searchKeyword, Predicate.PredicateType predicateType) {
		JPAQuery<PredicateInfo> query = getFindAllQuery(routeId, searchType, searchKeyword, predicateType);

		return query.fetch();
	}
	
	public Page<PredicateInfo> findAllPage(UUID routeId, String searchType, String searchKeyword, Predicate.PredicateType predicateType, Predicate.Status status, PageRequest pageRequest) {
		JPAQuery<PredicateInfo> query = getFindAllQuery(routeId, searchType, searchKeyword, predicateType);

		if (status != null) {
			query.where(predicate.status.eq(status));
		}

		query.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize());

		return PageableExecutionUtils.getPage(query.fetch(), pageRequest, query::fetchCount);
	}

	private JPAQuery<PredicateInfo> getFindAllQuery(UUID routeId, String searchType, String searchKeyword, Predicate.PredicateType predicateType) {
		JPAQuery<PredicateInfo> query = jpaQueryFactory.select(Projections.constructor(PredicateInfo.class, predicate))
				.from(predicate)
				.where(predicate.systemStatus.eq(Predicate.SystemStatus.ENABLE)
						.and(predicate.route.id.eq(routeId)));

		if (predicateType != null) {
			query.where(predicate.predicateType.eq(predicateType));
		}

		if (searchKeyword != null) {
			//검색조건에 따른 검색

			if ("KEY".equals(searchType)) {
				query.where(predicate.key.containsIgnoreCase(searchKeyword));
			} else if ("DEFINITION".equals(searchType)) {
				query.where(predicate.definition.containsIgnoreCase(searchKeyword));
			} else {
				query.where(predicate.key.containsIgnoreCase(searchKeyword).or(predicate.definition.containsIgnoreCase(searchKeyword)));
			}
		}

		query.orderBy(predicate.predicateType.asc(), predicate.key.asc(), predicate.definition.asc(), predicate.id.asc());
		return query;
	}
}
