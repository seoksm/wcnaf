package com.winitech.common.infrastructure.common;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.winitech.common.domain.common.CommonUser;
import com.winitech.common.domain.common.CommonUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static com.winitech.common.domain.common.QCommonUser.commonUser;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonUserQueryRepository.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-14 14:22
 **/
@Slf4j
@Repository
@RequiredArgsConstructor
public class CommonUserQueryRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public Page<CommonUserInfo> findAllPage(String searchType, String searchKeyword, CommonUser.Status status, PageRequest pageRequest) {
		JPAQuery<CommonUserInfo> query = getFindAllQuery(searchType, searchKeyword);

		if (status != null) {
			query.where(commonUser.status.eq(status));
		}

		query.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize());

		return PageableExecutionUtils.getPage(query.fetch(), pageRequest, query::fetchCount);
	}

	private JPAQuery<CommonUserInfo> getFindAllQuery(String searchType, String searchKeyword) {
		JPAQuery<CommonUserInfo> query = jpaQueryFactory.select(Projections.constructor(CommonUserInfo.class, commonUser))
				.from(commonUser);

		if (searchKeyword != null) {
			if ("FULL_NAME".equals(searchType)) {
				query.where(commonUser.fullName.containsIgnoreCase(searchKeyword));
			} else if ("DEPARTMENT_NAME".equals(searchType)) {
				query.where(commonUser.departmentName.containsIgnoreCase(searchKeyword));
			} else if ("DUTY_NAME".equals(searchType)) {
				query.where(commonUser.dutyName.containsIgnoreCase(searchKeyword));
			} else if ("EMAIL".equals(searchType)) {
				query.where(commonUser.email.containsIgnoreCase(searchKeyword));
			} else if ("EMPLOYEE_NO".equals(searchType)) {
				query.where(commonUser.employeeNo.containsIgnoreCase(searchKeyword));
			} else {
				query.where(commonUser.fullName.containsIgnoreCase(searchKeyword)
						.or(commonUser.departmentName.containsIgnoreCase(searchKeyword))
						.or(commonUser.dutyName.containsIgnoreCase(searchKeyword))
						.or(commonUser.email.containsIgnoreCase(searchKeyword))
						.or(commonUser.employeeNo.containsIgnoreCase(searchKeyword)));
			}
		}

		query.orderBy(commonUser.fullName.asc(), commonUser.id.asc());
		return query;
	}
}
