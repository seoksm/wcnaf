package com.winitech.common.infrastructure.commonJobGroup;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.winitech.common.domain.commonJobGroup.CommonJobGroup;
import com.winitech.common.domain.commonJobGroup.CommonJobGroupInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.winitech.common.domain.commonJobGroup.QCommonJobGroup.commonJobGroup;

/**
 * <pre>
 * com.winitech.system.domain.commonJobGroup
 * └ CommonJobGroup.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:36
 **/
@Repository
@RequiredArgsConstructor
public class CommonJobGroupQueryRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public List<CommonJobGroupInfo> findAll(String searchType, String searchKeyword) {
		JPAQuery<CommonJobGroupInfo> query = getFindAllQuery(searchType, searchKeyword);

		return query.fetch();
	}

	public Page<CommonJobGroupInfo> findAllPage(String searchType, String searchKeyword, CommonJobGroup.Status status, PageRequest pageRequest) {
		JPAQuery<CommonJobGroupInfo> query = getFindAllQuery(searchType, searchKeyword);
		
		if (status != null) {
			query.where(commonJobGroup.status.eq(status));
		}

		query.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize());

		return PageableExecutionUtils.getPage(query.fetch(), pageRequest, query::fetchCount);
	}

	private JPAQuery<CommonJobGroupInfo> getFindAllQuery(String searchType, String searchKeyword) {
		JPAQuery<CommonJobGroupInfo> query = jpaQueryFactory.select(Projections.constructor(CommonJobGroupInfo.class, commonJobGroup))
				.from(commonJobGroup)
				.where(commonJobGroup.systemStatus.eq(CommonJobGroup.SystemStatus.ENABLE));

		if (searchKeyword != null) {
			//검색조건에 따른 검색

			if ("NAME".equals(searchType)) {
				query.where(commonJobGroup.name.contains(searchKeyword));
			} else {
				query.where(commonJobGroup.name.contains(searchKeyword));
			}
		}

		query.orderBy(commonJobGroup.name.asc(), commonJobGroup.id.asc());
		return query;
	}
}
