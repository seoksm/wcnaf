package com.winitech.system.infrastructure.authorizationGroupUser;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.system.domain.authorizationGroupUser.AuthorizationGroupUserInfo;
import com.winitech.system.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.winitech.system.domain.authorizationGroupUser.QAuthorizationGroupUser.authorizationGroupUser;
import static com.winitech.system.domain.user.QUser.user;

/**
 * <pre>
 * com.winitech.system.domain.authorizationGroupUser
 * └ AuthorizationGroupUser.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-20 10:09
 **/
@Repository
@RequiredArgsConstructor
public class AuthorizationGroupUserQueryRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public List<AuthorizationGroupUserInfo.PageInfo> findAll(UUID authorizationGroupId, String status, String authorizationGroupUserStatus, String searchType, String searchKeyword) {
		JPAQuery<AuthorizationGroupUserInfo.PageInfo> query = getFindAllQuery(authorizationGroupId, status, authorizationGroupUserStatus, searchType, searchKeyword);

		return query.fetch();
	}

	public Page<AuthorizationGroupUserInfo.PageInfo> findAllPage(UUID authorizationGroupId, String status, String authorizationGroupUserStatus, String searchType, String searchKeyword, PageRequest pageRequest) {
		JPAQuery<AuthorizationGroupUserInfo.PageInfo> query = getFindAllQuery(authorizationGroupId, status, authorizationGroupUserStatus, searchType, searchKeyword);

		query.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize());

		return PageableExecutionUtils.getPage(query.fetch(), pageRequest, query::fetchCount);
	}

	private JPAQuery<AuthorizationGroupUserInfo.PageInfo> getFindAllQuery(UUID authorizationGroupId, String status, String authorizationGroupUserStatus, String searchType, String searchKeyword) {
		JPAQuery<AuthorizationGroupUserInfo.PageInfo> query = jpaQueryFactory.select(Projections.bean(AuthorizationGroupUserInfo.PageInfo.class,
								authorizationGroupUser.id,
								authorizationGroupUser.authorizationGroup.id.as("authorizationGroupId"),
								user.id.as("userId"),
								user.username,
								//user.userDepartment.name.as("userDepartmentName"),
								Expressions.asString("TBD").as("userDepartmentName"),
								user.fullName,
								user.status,
								new CaseBuilder().when(authorizationGroupUser.isNotNull()).then("ENABLE").otherwise("DISABLE").as("authorizationGroupUserStatus")
						)
				)
				.from(user)
				.leftJoin(authorizationGroupUser)
				.on(user.eq(authorizationGroupUser.user)
						.and(authorizationGroupUser.authorizationGroup.id.eq(authorizationGroupId)));

		if (authorizationGroupUserStatus != null) {
			switch (authorizationGroupUserStatus) {
				case "ENABLE":
					query.where(authorizationGroupUser.isNotNull());
					break;
				case "DISABLE":
					query.where(authorizationGroupUser.isNull());
					break;
				default:
					throw new InvalidParamException("Invalid authorizationGroupUserStatus");
			}
		}

		if (status != null) {
			switch (status) {
				case "ENABLE":
					query.where(user.status.eq(User.Status.ENABLE));
					break;
				case "DISABLE":
					query.where(user.status.eq(User.Status.DISABLE));
					break;
				default:
					throw new InvalidParamException("Invalid status");
			}
		}

		if (searchKeyword != null) {
			//검색조건에 따른 검색

			if ("FULL_NAME".equals(searchType)) {
				query.where(user.fullName.contains(searchKeyword));
			} else if ("USERNAME".equals(searchType)) {
				query.where(user.username.contains(searchKeyword));
			} else {
				query.where(user.fullName.contains(searchKeyword)
						.or(user.username.contains(searchKeyword)));
			}
		}

		query.orderBy(
				new CaseBuilder().when(authorizationGroupUser.isNotNull()).then(0).otherwise(1).asc(), 
				user.fullName.asc(),
				user.id.asc()
		);
		return query;
	}
}
