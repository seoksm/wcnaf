package com.winitech.system.infrastructure.menuPermission;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.winitech.common.domain.common.CommonMenuAction;
import com.winitech.common.library.WiniCom;
import com.winitech.system.domain.authorizationGroup.AuthorizationGroup;
import com.winitech.system.domain.menu.Menu;
import com.winitech.system.domain.menuPermission.MenuPermission;
import com.winitech.system.domain.menuPermission.MenuPermissionInfo;
import com.winitech.system.domain.program.Program;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.winitech.system.domain.authorizationGroup.QAuthorizationGroup.authorizationGroup;
import static com.winitech.system.domain.authorizationGroupUser.QAuthorizationGroupUser.authorizationGroupUser;
import static com.winitech.system.domain.menu.QMenu.menu;
import static com.winitech.system.domain.menuPermission.QMenuPermission.menuPermission;
import static com.winitech.system.domain.program.QProgram.program;

/**
 * <pre>
 * com.winitech.system.infrastructure.menuPermission
 * └ MenuPermissionQueryRepository.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-18 17:57
 **/
@Repository
@RequiredArgsConstructor
public class MenuPermissionQueryRepository {
	private final JPAQueryFactory jpaQueryFactory;
	
	public List<MenuPermissionInfo.UserMenuPermissionTreeInfo> getMenuPermissionTreeByUserId(UUID userId, UUID authorizationGroupId, String extraGroupCode) {
		// step 1. 사용자의 프로그램 권한 조회
		JPAQuery<MenuPermissionInfo.UserMenuPermissionTreeInfo> userProgramSelectQuery = getBaseMenuPermissionTreeQuery(userId, authorizationGroupId, extraGroupCode);

		List<MenuPermissionInfo.UserMenuPermissionTreeInfo> userProgramPermissionList = userProgramSelectQuery
				.groupBy(menu.id, menu.parentMenu.id, program.id)
				.orderBy(new OrderSpecifier<>(Order.ASC, Expressions.stringPath("sortSeq")))
				.fetch();

		// step 2. 메뉴만 조회
		List<MenuPermissionInfo.UserMenuPermissionTreeInfo> allMenuList = jpaQueryFactory.select(
						Projections.bean(MenuPermissionInfo.UserMenuPermissionTreeInfo.class,
								menu.id.as("menuId"),
								menu.menuName.as("menuName"),
								menu.menuCode.as("menuCode"),
								menu.menuMapping.as("menuMapping"),
								menu.status.stringValue().as("status"),
								menu.menuStatus.stringValue().as("menuStatus"),
								menu.menuType.stringValue().as("menuType"),
								menu.sortSeq.as("sortSeq"),
								menu.parentMenu.id.as("parentMenuId")
						)
				).from(menu)
				.where(menu.menuType.eq(Menu.MenuType.MENU)
						.and(menu.systemStatus.eq(Menu.SystemStatus.ENABLE))
						.and(menu.status.eq(Menu.Status.ENABLE)))
				.orderBy(menu.sortSeq.asc())
				.fetch();

		// step 3. 메뉴만 조회된 메뉴를 트리로 변환
		List<MenuPermissionInfo.UserMenuPermissionTreeInfo> menuTree = new ArrayList<>();
		
		Map<UUID, MenuPermissionInfo.UserMenuPermissionTreeInfo> menuIdToMenuMap = allMenuList.stream()
				.collect(Collectors.toMap(MenuPermissionInfo.UserMenuPermissionTreeInfo::getMenuId, Function.identity()));

		// sortSeq 순으로 순회하며 자식을 부모의 children에 추가하므로, 자식 메뉴의 sortSeq가 부모보다
		// 작으면(부모가 아직 순회되기 전이면) children이 null인 상태로 참조되어 NPE가 났다(2026-09-17 발견).
		// 순회 순서와 무관하게 항상 안전하도록 모든 메뉴의 children을 먼저 초기화한다.
		for (MenuPermissionInfo.UserMenuPermissionTreeInfo menu : allMenuList) {
			menu.setChildren(new ArrayList<>(0));
		}

		for (int menuIndex = 0; menuIndex < allMenuList.size(); menuIndex++) {
			MenuPermissionInfo.UserMenuPermissionTreeInfo menu = allMenuList.get(menuIndex);

			if (menu.getParentMenuId() == null) {
				// 최상위 메뉴는 결과 메뉴 트리에 추가
				menuTree.add(menu);
			} else {
				// 자식 메뉴는 부모 메뉴의 자식으로 추가
				if (menuIdToMenuMap.get(menu.getParentMenuId()) != null) {
					menuIdToMenuMap.get(menu.getParentMenuId()).getChildren().add(menu);
				}
			}
		}
		
		// step 4. 사용자의 프로그램 권한을 메뉴 트리에 추가
		for (MenuPermissionInfo.UserMenuPermissionTreeInfo userProgramPermission : userProgramPermissionList) {
			UUID parentMenuId = userProgramPermission.getParentMenuId();
			
			if (parentMenuId == null) {
				// 최상위 메뉴는 결과 메뉴 트리에 추가
				menuTree.add(userProgramPermission);
			} else {
				// 자식 메뉴는 부모 메뉴의 자식으로 추가
				if (menuIdToMenuMap.get(parentMenuId) != null) {
					menuIdToMenuMap.get(parentMenuId).getChildren().add(userProgramPermission);
				}
			}
		}
		
		// step 6. 프로그램 갯수 카운트하고, 하위 프로그램이 없는 메뉴는 제거
		countProgramAndCutLeafMenuRecursive(menuTree);
		
		return menuTree;
	}

	public List<MenuPermissionInfo.UserMenuPermissionTreeInfo> getMenuPermissionByUserId(UUID userId, UUID menuId, UUID authorizationGroupId, String extraGroupCode) {
		JPAQuery<MenuPermissionInfo.UserMenuPermissionTreeInfo> userProgramSelectQuery = getBaseMenuPermissionTreeQuery(userId, authorizationGroupId, extraGroupCode);

		userProgramSelectQuery.where(menu.id.eq(menuId));

		return userProgramSelectQuery
				.groupBy(menu.id, menu.parentMenu.id, program.id)
				.orderBy(new OrderSpecifier<>(Order.ASC, Expressions.stringPath("sortSeq")))
				.fetch();
	}

	private int countProgramAndCutLeafMenuRecursive(List<MenuPermissionInfo.UserMenuPermissionTreeInfo> menuTree) {
		if (menuTree == null) {
			return 0;
		}
		
		int programCount = 0;

		for (int menuTreeIndex = 0; menuTreeIndex < menuTree.size(); menuTreeIndex++) {
			MenuPermissionInfo.UserMenuPermissionTreeInfo node = menuTree.get(menuTreeIndex);
			if (node.getChildren() != null) {
				int curProgramCount = countProgramAndCutLeafMenuRecursive(node.getChildren());
				node.setProgramCount(curProgramCount);

				if (curProgramCount == 0) {
					if (Menu.MenuType.MENU.toString().equals(node.getMenuType())) {
						// 하위 프로그램이 없는 메뉴이면 메뉴 제거
						menuTree.remove(menuTreeIndex);
						menuTreeIndex--;	
					}					
				} else {
					// 하위 프로그램이 있으면 카운트
					programCount += curProgramCount;
				}
			}

			if (Menu.MenuType.PROGRAM.toString().equals(node.getMenuType())) {
				programCount++;
			} 
		}
		
		menuTree.sort(Comparator.comparing(MenuPermissionInfo.UserMenuPermissionTreeInfo::getSortSeq));
		
		return programCount;
	}

	private JPAQuery<MenuPermissionInfo.UserMenuPermissionTreeInfo> getBaseMenuPermissionTreeQuery(UUID userId, UUID authorizationGroupId, String extraGroupCode) {
		// step 1. 사용자의 권한 그룹 조회
		JPAQuery<AuthorizationGroup> authorizationGroupQuery = jpaQueryFactory.selectFrom(authorizationGroup)
				.where(
						authorizationGroup.status.eq(AuthorizationGroup.Status.ENABLE)    // 사용중이고
								.and(authorizationGroup.systemStatus.eq(AuthorizationGroup.SystemStatus.ENABLE)) // 삭제되지 않은 건
				);

		if (userId != null) {
			authorizationGroupQuery
					.leftJoin(authorizationGroupUser)
					.on(authorizationGroupUser.authorizationGroup.eq(authorizationGroup)
							.and(authorizationGroupUser.user.id.eq(userId)));

			authorizationGroupQuery
					.where(authorizationGroupUser.isNotNull()
							.or(authorizationGroup.groupCode.eq(extraGroupCode)));
		}

		if (authorizationGroupId != null) {
			authorizationGroupQuery.where(authorizationGroup.id.eq(authorizationGroupId));
		}

		// step 2. 사용자의 프로그램 메뉴 권한 조회
		JPAQuery<MenuPermissionInfo.UserMenuPermissionTreeInfo> userProgramSelectQuery = jpaQueryFactory.select(
						Projections.bean(MenuPermissionInfo.UserMenuPermissionTreeInfo.class,
								menu.id.as("menuId"),
								menu.menuName.max().as("menuName"),
								menu.menuCode.max().as("menuCode"),
								menu.menuMapping.max().as("menuMapping"),
								menu.status.stringValue().max().as("status"),
								menu.menuStatus.stringValue().max().as("menuStatus"),
								menu.menuType.stringValue().max().as("menuType"),
								menu.sortSeq.max().as("sortSeq"),
								menu.parentMenu.id.as("parentMenuId"),
								program.id.as("programId"),
								program.programMapping.max().as("programMapping"),

								new CaseBuilder()
										.when(menuPermission.selectStatus.eq(MenuPermission.MenuPermissionStatus.ALLOW))
										.then(Expressions.asString(MenuPermission.MenuPermissionStatus.ALLOW.toString()))
										.otherwise(Expressions.asString(MenuPermission.MenuPermissionStatus.NONE.toString())).min().as("selectStatus"),
								new CaseBuilder()
										.when(menuPermission.insertStatus.eq(MenuPermission.MenuPermissionStatus.ALLOW))
										.then(Expressions.asString(MenuPermission.MenuPermissionStatus.ALLOW.toString()))
										.otherwise(Expressions.asString(MenuPermission.MenuPermissionStatus.NONE.toString())).min().as("insertStatus"),
								new CaseBuilder()
										.when(menuPermission.updateStatus.eq(MenuPermission.MenuPermissionStatus.ALLOW))
										.then(Expressions.asString(MenuPermission.MenuPermissionStatus.ALLOW.toString()))
										.otherwise(Expressions.asString(MenuPermission.MenuPermissionStatus.NONE.toString())).min().as("updateStatus"),
								new CaseBuilder()
										.when(menuPermission.deleteStatus.eq(MenuPermission.MenuPermissionStatus.ALLOW))
										.then(Expressions.asString(MenuPermission.MenuPermissionStatus.ALLOW.toString()))
										.otherwise(Expressions.asString(MenuPermission.MenuPermissionStatus.NONE.toString())).min().as("deleteStatus"),
								new CaseBuilder()
										.when(menuPermission.printStatus.eq(MenuPermission.MenuPermissionStatus.ALLOW))
										.then(Expressions.asString(MenuPermission.MenuPermissionStatus.ALLOW.toString()))
										.otherwise(Expressions.asString(MenuPermission.MenuPermissionStatus.NONE.toString())).min().as("printStatus"),
								new CaseBuilder()
										.when(menuPermission.downStatus.eq(MenuPermission.MenuPermissionStatus.ALLOW))
										.then(Expressions.asString(MenuPermission.MenuPermissionStatus.ALLOW.toString()))
										.otherwise(Expressions.asString(MenuPermission.MenuPermissionStatus.NONE.toString())).min().as("downStatus"),
								new CaseBuilder()
										.when(menuPermission.manageStatus.eq(MenuPermission.MenuPermissionStatus.ALLOW))
										.then(Expressions.asString(MenuPermission.MenuPermissionStatus.ALLOW.toString()))
										.otherwise(Expressions.asString(MenuPermission.MenuPermissionStatus.NONE.toString())).min().as("manageStatus"),
								new CaseBuilder()
										.when(menuPermission.custom1Status.eq(MenuPermission.MenuPermissionStatus.ALLOW))
										.then(Expressions.asString(MenuPermission.MenuPermissionStatus.ALLOW.toString()))
										.otherwise(Expressions.asString(MenuPermission.MenuPermissionStatus.NONE.toString())).min().as("custom1Status"),
								new CaseBuilder()
										.when(menuPermission.custom2Status.eq(MenuPermission.MenuPermissionStatus.ALLOW))
										.then(Expressions.asString(MenuPermission.MenuPermissionStatus.ALLOW.toString()))
										.otherwise(Expressions.asString(MenuPermission.MenuPermissionStatus.NONE.toString())).min().as("custom2Status"),
								new CaseBuilder()
										.when(menuPermission.custom3Status.eq(MenuPermission.MenuPermissionStatus.ALLOW))
										.then(Expressions.asString(MenuPermission.MenuPermissionStatus.ALLOW.toString()))
										.otherwise(Expressions.asString(MenuPermission.MenuPermissionStatus.NONE.toString())).min().as("custom3Status")
						)
				).from(menuPermission)
				.join(menuPermission.menu, menu)
				.join(menu.program, program)
				.where(menuPermission.authorizationGroup.in(authorizationGroupQuery)
						.and(menu.systemStatus.eq(Menu.SystemStatus.ENABLE))
						.and(menu.menuType.eq(Menu.MenuType.PROGRAM))
						.and(program.status.eq(Program.Status.ENABLE))				// 사용중이고
						.and(program.menuStatus.eq(Program.MenuStatus.ENABLE)));	// 메뉴여부가 체크된 경우
		return userProgramSelectQuery;
	}
}