package com.winitech.system.infrastructure.programAction;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.winitech.system.domain.programAction.ProgramAction;
import com.winitech.system.domain.programAction.ProgramActionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.winitech.system.domain.programAction.QProgramAction.programAction;

/**
 * <pre>
 * com.winitech.system.domain.programAction
 * └ ProgramAction.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-14 10:39
 **/
@Repository
@RequiredArgsConstructor
public class ProgramActionQueryRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public List<ProgramActionInfo> findAll(UUID programId, String searchType, String searchKeyword) {
		JPAQuery<ProgramActionInfo> query = getFindAllQuery(programId, searchType, searchKeyword);

		return query.fetch();
	}

	public Page<ProgramActionInfo> findAllPage(UUID programId, String searchType, String searchKeyword, PageRequest pageRequest) {
		JPAQuery<ProgramActionInfo> query = getFindAllQuery(programId, searchType, searchKeyword);

		query.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize());

		return PageableExecutionUtils.getPage(query.fetch(), pageRequest, query::fetchCount);
	}

	private JPAQuery<ProgramActionInfo> getFindAllQuery(UUID programId, String searchType, String searchKeyword) {
		JPAQuery<ProgramActionInfo> query = jpaQueryFactory.select(Projections.constructor(ProgramActionInfo.class, programAction))
				.from(programAction)
				.where(programAction.program.id.eq(programId));

		if (searchKeyword != null) {
			//검색조건에 따른 검색

			//if ("MENU_CODE".equals(searchType)) {
			//	query.where(ProgramAction.ProgramActionCode.contains(searchKeyword));
			//} else if ("MENU_NAME".equals(searchType)) {
			//	query.where(ProgramAction.ProgramActionName.contains(searchKeyword));
			//} else {
			//	query.where(ProgramAction.ProgramActionCode.contains(searchKeyword)
			//			.or(ProgramAction.ProgramActionName.contains(searchKeyword)));
			//}
		}

		query.orderBy(/*programAction.programActionCode.asc(), programAction.programActionMapping.asc(),*/ programAction.id.asc());
		return query;
	}
}
