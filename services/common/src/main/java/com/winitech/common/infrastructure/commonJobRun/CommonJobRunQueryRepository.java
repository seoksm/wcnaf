package com.winitech.common.infrastructure.commonJobRun;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.winitech.common.domain.commonJobRun.CommonJobRun;
import com.winitech.common.domain.commonJobRun.CommonJobRunCommand;
import com.winitech.common.domain.commonJobRun.CommonJobRunInfo;
import com.winitech.common.library.WiniCom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.winitech.common.domain.commonJob.QCommonJob.commonJob;
import static com.winitech.common.domain.commonJobRun.QCommonJobRun.commonJobRun;

/**
 * <pre>
 * com.winitech.system.domain.commonJobRun
 * └ CommonJobRun.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:48
 **/
@Repository
@RequiredArgsConstructor
public class CommonJobRunQueryRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public List<CommonJobRunInfo> findAll(String searchType, String searchKeyword) {
		JPAQuery<CommonJobRunInfo> query = getFindAllQuery(searchType, searchKeyword);

		return query.fetch();
	}

	public Page<CommonJobRunInfo> findAllPage(CommonJobRunCommand.SearchRequestCommand command) {
		JPAQuery<CommonJobRunInfo> query = getFindAllQuery(command.getSearchType(), command.getSearchKeyword());

		if (command.getCommonJobId() != null) {
			query.where(commonJob.id.eq(command.getCommonJobId()));			
		}
		
		if (command.getSearchStartDateTime() != null) {
			query.where(commonJobRun.startedAt.goe(command.getSearchStartDateTime()));
		}

		if (command.getSearchEndDateTime() != null) {
			query.where(commonJobRun.startedAt.loe(command.getSearchEndDateTime()));
		}
		
		if (command.getSearchJobStatus() != null) {
			query.where(commonJobRun.jobStatus.eq(command.getSearchJobStatus()));
		}

		Pageable pageRequest = WiniCom.getPageRequest(command.getPage(), command.getPageSize());
		
		query.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize());

		return PageableExecutionUtils.getPage(query.fetch(), pageRequest, query::fetchCount);
	}

	private JPAQuery<CommonJobRunInfo> getFindAllQuery(String searchType, String searchKeyword) {
		JPAQuery<CommonJobRunInfo> query = jpaQueryFactory.select(Projections.constructor(CommonJobRunInfo.class, commonJobRun))
				.from(commonJobRun)
				.join(commonJob).on(commonJob.eq(commonJobRun.commonJob))
				.where(commonJobRun.systemStatus.eq(CommonJobRun.SystemStatus.ENABLE));

		if (searchKeyword != null) {
			//검색조건에 따른 검색

			//if ("MENU_CODE".equals(searchType)) {
			//	query.where(CommonJobRun.CommonJobRunCode.contains(searchKeyword));
			//} else if ("MENU_NAME".equals(searchType)) {
			//	query.where(CommonJobRun.CommonJobRunName.contains(searchKeyword));
			//} else {
			//	query.where(CommonJobRun.CommonJobRunCode.contains(searchKeyword)
			//			.or(CommonJobRun.CommonJobRunName.contains(searchKeyword)));
			//}
		}
		
		query.orderBy(/*commonJobRun.commonJobRunCode.asc(), commonJobRun.commonJobRunMapping.asc(),*/ commonJobRun.id.desc());
		return query;
	}
}
