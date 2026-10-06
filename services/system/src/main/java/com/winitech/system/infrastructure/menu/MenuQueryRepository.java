package com.winitech.system.infrastructure.menu;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.winitech.system.domain.menu.Menu;
import com.winitech.system.domain.menu.MenuInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.winitech.system.domain.menu.QMenu.menu;

/**
 * <pre>
 * com.winitech.system.infrastructure.menu
 * └ MenuQueryRepository.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-24 09:50
 **/
@Repository
@RequiredArgsConstructor
public class MenuQueryRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public List<MenuInfo> findAll(String searchType, String searchKeyword) {
		JPAQuery<MenuInfo> query = getFindAllQuery(searchType, searchKeyword);

		return query.fetch();
	}

	public Page<MenuInfo> findAllPage(String searchType, String searchKeyword, PageRequest pageRequest) {
		JPAQuery<MenuInfo> query = getFindAllQuery(searchType, searchKeyword);

		query.offset(pageRequest.getOffset())
				.limit(pageRequest.getPageSize());

		return PageableExecutionUtils.getPage(query.fetch(), pageRequest, query::fetchCount);
	}

	private JPAQuery<MenuInfo> getFindAllQuery(String searchType, String searchKeyword) {
		JPAQuery<MenuInfo> query = jpaQueryFactory.select(Projections.constructor(MenuInfo.class, menu))
				.from(menu)
				.where(menu.systemStatus.eq(Menu.SystemStatus.ENABLE));

		if (searchKeyword != null) {
			if ("MENU_CODE".equals(searchType)) {
				query.where(menu.menuCode.contains(searchKeyword));
			} else if ("MENU_NAME".equals(searchType)) {
				query.where(menu.menuName.contains(searchKeyword));
			} else {
				query.where(menu.menuCode.contains(searchKeyword)
						.or(menu.menuName.contains(searchKeyword)));
			}
		}

		query.orderBy(menu.menuCode.asc(), menu.menuMapping.asc(), menu.id.asc());
		return query;
	}
}
