package com.winitech.apiGateway.infrastructure.route;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.winitech.apiGateway.domain.route.Route;
import com.winitech.apiGateway.domain.route.RouteInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.winitech.apiGateway.domain.route.QRoute.route;

/**
 * <pre>
 * com.winitech.apiGateway.infrastructure.route
 * └ RouteQueryRepository.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-04 17:43
 **/
@Repository
@RequiredArgsConstructor
public class RouteQueryRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public List<RouteInfo> findAll(String searchType, String searchKeyword) {
		JPAQuery<RouteInfo> query = getFindAllQuery(searchType, searchKeyword);

		return query.fetch();
	}

	public Page<RouteInfo> findAllPage(String searchType, String searchKeyword, Route.Status status, PageRequest pageRequest) {
		JPAQuery<RouteInfo> query = getFindAllQuery(searchType, searchKeyword);
		
		if (status != null) {
			query.where(route.status.eq(status));
		}

		query.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize());

		return PageableExecutionUtils.getPage(query.fetch(), pageRequest, query::fetchCount);
	}

	private JPAQuery<RouteInfo> getFindAllQuery(String searchType, String searchKeyword) {
		JPAQuery<RouteInfo> query = jpaQueryFactory.select(Projections.constructor(RouteInfo.class, route))
				.from(route)
				.where(route.systemStatus.eq(Route.SystemStatus.ENABLE));

		if (searchKeyword != null) {
			//검색조건에 따른 검색

			if ("NAME".equals(searchType)) {
				query.where(route.name.contains(searchKeyword));
			} else if ("URI".equals(searchType)) {
				query.where(route.uri.contains(searchKeyword));
			} else {
				query.where(route.name.contains(searchKeyword).or(route.uri.contains(searchKeyword)));
			}
		}

		query.orderBy(route.sortSeq.asc(), route.name.asc(), route.id.asc());
		return query;
	}
}
