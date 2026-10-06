package com.winitech.common.infrastructure.commonJobState;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.winitech.common.domain.commonJobState.CommonJobState;
import com.winitech.common.domain.commonJobState.CommonJobStateInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.winitech.common.domain.commonJobState.QCommonJobState.commonJobState;

/**
 * <pre>
 * com.winitech.system.domain.commonJobState
 * └ CommonJobState.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:47
 **/
@Repository
@RequiredArgsConstructor
public class CommonJobStateQueryRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public List<CommonJobStateInfo> findAll(String searchType, String searchKeyword) {
		JPAQuery<CommonJobStateInfo> query = getFindAllQuery(searchType, searchKeyword);

		return query.fetch();
	}

	public Page<CommonJobStateInfo> findAllPage(String searchType, String searchKeyword, PageRequest pageRequest) {
		JPAQuery<CommonJobStateInfo> query = getFindAllQuery(searchType, searchKeyword);

		query.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize());

		return PageableExecutionUtils.getPage(query.fetch(), pageRequest, query::fetchCount);
	}

	private JPAQuery<CommonJobStateInfo> getFindAllQuery(String searchType, String searchKeyword) {
		JPAQuery<CommonJobStateInfo> query = jpaQueryFactory.select(Projections.constructor(CommonJobStateInfo.class, commonJobState))
				.from(commonJobState)
				.where(commonJobState.systemStatus.eq(CommonJobState.SystemStatus.ENABLE));

		if (searchKeyword != null) {
			//검색조건에 따른 검색

			//if ("MENU_CODE".equals(searchType)) {
			//	query.where(CommonJobState.CommonJobStateCode.contains(searchKeyword));
			//} else if ("MENU_NAME".equals(searchType)) {
			//	query.where(CommonJobState.CommonJobStateName.contains(searchKeyword));
			//} else {
			//	query.where(CommonJobState.CommonJobStateCode.contains(searchKeyword)
			//			.or(CommonJobState.CommonJobStateName.contains(searchKeyword)));
			//}
		}

		query.orderBy(/*commonJobState.commonJobStateCode.asc(), commonJobState.commonJobStateMapping.asc(),*/ commonJobState.id.asc());
		return query;
	}
}
