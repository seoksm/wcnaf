package com.winitech.common.infrastructure.commonJob;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.winitech.common.domain.commonJob.CommonJob;
import com.winitech.common.domain.commonJob.CommonJobInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.winitech.common.domain.commonJob.QCommonJob.commonJob;
import static com.winitech.common.domain.commonJobGroup.QCommonJobGroup.commonJobGroup;

/**
 * <pre>
 * com.winitech.system.domain.commonJob
 * └ CommonJob.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:37
 **/
@Repository
@RequiredArgsConstructor
public class CommonJobQueryRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public List<CommonJobInfo> findAll(String searchType, String searchKeyword) {
		JPAQuery<CommonJobInfo> query = getFindAllQuery(searchType, searchKeyword);

		return query.fetch();
	}

	public Page<CommonJobInfo> findAllPage(UUID commonJobGroupId, String searchType, String searchKeyword, PageRequest pageRequest, CommonJob.Status status, CommonJob.JobType jobType) {
		JPAQuery<CommonJobInfo> query = getFindAllQuery(searchType, searchKeyword);
		
		if (commonJobGroupId != null) {
			query.where(commonJob.commonJobGroup.id.eq(commonJobGroupId));
		}
		
		if (status != null) {
			query.where(commonJob.status.eq(status));
		}
		
		if (jobType != null) {
			query.where(commonJob.jobType.eq(jobType));
		}

		query.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize());

		return PageableExecutionUtils.getPage(query.fetch(), pageRequest, query::fetchCount);
	}

	private JPAQuery<CommonJobInfo> getFindAllQuery(String searchType, String searchKeyword) {
		JPAQuery<CommonJobInfo> query = jpaQueryFactory.select(Projections.constructor(CommonJobInfo.class, commonJob))
				.from(commonJob)
				.join(commonJobGroup).on(commonJob.commonJobGroup.eq(commonJobGroup))	
				.where(commonJob.systemStatus.eq(CommonJob.SystemStatus.ENABLE));

		if (searchKeyword != null) {
			//검색조건에 따른 검색

			if ("NAME".equals(searchType)) {
				query.where(commonJob.name.contains(searchKeyword));
			} else {
				query.where(commonJob.name.contains(searchKeyword));
			}
		}

		query.orderBy(commonJob.name.asc(), commonJob.id.asc());
		return query;
	}
}
