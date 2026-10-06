package com.winitech.system.infrastructure.program;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.winitech.system.domain.program.Program;
import com.winitech.system.domain.program.ProgramInfo;
import com.winitech.system.domain.program.QProgram;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.winitech.system.domain.program.QProgram.program;

/**
 * <pre>
 * com.winitech.system.infrastructure.program
 * └ ProgramQueryRepository.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-23 13:14
 **/
@Repository
@RequiredArgsConstructor
public class ProgramQueryRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public List<ProgramInfo> findAll(String searchType, String searchKeyword, Program.MenuStatus menuStatus) {
		JPAQuery<ProgramInfo> query = getFindAllQuery(searchType, searchKeyword, menuStatus);

		return query.fetch();
	}

	public Page<ProgramInfo> findAllPage(String searchType, String searchKeyword, Program.MenuStatus menuStatus, PageRequest pageRequest) {
		JPAQuery<ProgramInfo> query = getFindAllQuery(searchType, searchKeyword, menuStatus);

		query.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize());

		return PageableExecutionUtils.getPage(query.fetch(), pageRequest, query::fetchCount);
	}

	private JPAQuery<ProgramInfo> getFindAllQuery(String searchType, String searchKeyword, Program.MenuStatus menuStatus) {
		JPAQuery<ProgramInfo> query = jpaQueryFactory.select(Projections.constructor(ProgramInfo.class, program))
				.from(program);

		if (searchKeyword != null) {
			if ("PROGRAM_CODE".equals(searchType)) {
				query.where(program.programCode.contains(searchKeyword));
			} else if ("PROGRAM_NAME".equals(searchType)) {
				query.where(program.programName.contains(searchKeyword));
			} else {
				query.where(program.programCode.contains(searchKeyword)
						.or(program.programName.contains(searchKeyword)));
			}
		}
		
		if (menuStatus != null) {
			query.where(program.menuStatus.eq(menuStatus));
		}

		query.orderBy(program.programCode.asc(), program.programMapping.asc(), program.id.asc());
		return query;
	}
}
