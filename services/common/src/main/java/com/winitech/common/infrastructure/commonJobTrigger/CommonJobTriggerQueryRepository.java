package com.winitech.common.infrastructure.commonJobTrigger;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.winitech.common.domain.commonJobTrigger.CommonJobTrigger;
import com.winitech.common.domain.commonJobTrigger.CommonJobTriggerInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.winitech.common.domain.commonJob.QCommonJob.commonJob;
import static com.winitech.common.domain.commonJobTrigger.QCommonJobTrigger.commonJobTrigger;

/**
 * <pre>
 * com.winitech.system.domain.commonJobTrigger
 * └ CommonJobTrigger.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:45
 **/
@Repository
@RequiredArgsConstructor
public class CommonJobTriggerQueryRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public List<CommonJobTriggerInfo> findAll(String searchType, String searchKeyword) {
		JPAQuery<CommonJobTriggerInfo> query = getFindAllQuery(searchType, searchKeyword);

		return query.fetch();
	}

	public Page<CommonJobTriggerInfo> findAllPage(UUID commonJobId, CommonJobTrigger.Status status, String searchType, String searchKeyword, PageRequest pageRequest) {
		JPAQuery<CommonJobTriggerInfo> query = getFindAllQuery(searchType, searchKeyword);
		
		if (commonJobId != null) {
			query.where(commonJob.id.eq(commonJobId));
		}
		
		if (status != null) {
			query.where(commonJobTrigger.status.eq(status));
		}

		query.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize());

		return PageableExecutionUtils.getPage(query.fetch(), pageRequest, query::fetchCount);
	}

	private JPAQuery<CommonJobTriggerInfo> getFindAllQuery(String searchType, String searchKeyword) {
		JPAQuery<CommonJobTriggerInfo> query = jpaQueryFactory.select(Projections.constructor(CommonJobTriggerInfo.class, commonJobTrigger))
				.from(commonJobTrigger)
				.join(commonJob).on(commonJob.eq(commonJobTrigger.commonJob))
				.where(commonJobTrigger.systemStatus.eq(CommonJobTrigger.SystemStatus.ENABLE));

		if (searchKeyword != null) {
			//검색조건에 따른 검색

			//if ("MENU_CODE".equals(searchType)) {
			//	query.where(CommonJobTrigger.CommonJobTriggerCode.contains(searchKeyword));
			//} else if ("MENU_NAME".equals(searchType)) {
			//	query.where(CommonJobTrigger.CommonJobTriggerName.contains(searchKeyword));
			//} else {
			//	query.where(CommonJobTrigger.CommonJobTriggerCode.contains(searchKeyword)
			//			.or(CommonJobTrigger.CommonJobTriggerName.contains(searchKeyword)));
			//}
		}

		query.orderBy(/*commonJobTrigger.commonJobTriggerCode.asc(), commonJobTrigger.commonJobTriggerMapping.asc(),*/ commonJobTrigger.id.asc());
		return query;
	}
}
