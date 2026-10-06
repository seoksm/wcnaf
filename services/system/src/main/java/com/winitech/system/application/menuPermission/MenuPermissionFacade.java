package com.winitech.system.application.menuPermission;

import com.winitech.common.domain.common.CommonAuthorizationGroupPermissionInfo;
import com.winitech.common.library.WiniString;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.system.domain.authorizationGroup.AuthorizationGroupService;
import com.winitech.system.domain.menuPermission.MenuPermissionCommand;
import com.winitech.system.domain.menuPermission.MenuPermissionInfo;
import com.winitech.system.domain.menuPermission.MenuPermissionService;
import com.winitech.system.interfaces.inboundAdapter.web.menuPermission.MenuPermissionProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.menuPermission
 * └ MenuPermission.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-18 11:08
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuPermissionFacade {
	private final MenuPermissionService menuPermissionService;
	private final AuthorizationGroupService authorizationGroupService;
	
	private final MenuPermissionProducer menuPermissionProducer;

	public MenuPermissionInfo registerMenuPermission(MenuPermissionCommand.RegisterRequestCommand menuPermissionCommand) {
		MenuPermissionInfo result = menuPermissionService.registerMenuPermission(menuPermissionCommand);

		syncAuthorizationGroupPermission(Arrays.asList(result.getAuthorizationGroupId()));

		return result;
	}

	public MenuPermissionInfo modifyMenuPermission(UUID menuPermissionId, MenuPermissionCommand.ModifyRequestCommand menuPermissionCommand) {
		MenuPermissionInfo result = menuPermissionService.modifyMenuPermission(menuPermissionId, menuPermissionCommand);
		
		syncAuthorizationGroupPermission(Arrays.asList(result.getAuthorizationGroupId()));

		return result;
	}

	public void removeMenuPermission(UUID menuPermissionId) {
		MenuPermissionInfo menuPermissionInfo = menuPermissionService.searchMenuPermissionById(menuPermissionId);
		
		menuPermissionService.removeMenuPermission(menuPermissionId);

		syncAuthorizationGroupPermission(Arrays.asList(menuPermissionInfo.getAuthorizationGroupId()));
	}

	public MenuPermissionInfo searchMenuPermissionById(UUID id) {
		return menuPermissionService.searchMenuPermissionById(id);
	}

	public List<MenuPermissionInfo> getAllMenuPermission(UUID authorizationGroupId) {
		return menuPermissionService.getAllMenuPermission(authorizationGroupId);
	}

	public List<MenuPermissionInfo.MenuPermissionTreeInfo> getAllMenuPermissionTree(UUID authorizationGroupId) {
		return menuPermissionService.getAllMenuPermissionTree(authorizationGroupId);
	}

	public List<MenuPermissionInfo.MenuPermissionTreeInfo> getAllMenuPermissionTreeList(UUID authorizationGroupId, String childrenMark, String padString, Integer padLength) {
		List<MenuPermissionInfo.MenuPermissionTreeInfo> allMenuPermissionTree = menuPermissionService.getAllMenuPermissionTree(authorizationGroupId);
		
//		allMenuPermissionTree
//				.stream()
//				.flatMap(menuPermissionTreeInfo -> menuPermissionTreeInfo.getChildren().stream())
//				.forEach(menuPermissionTreeInfo -> {
//				System.out.println(menuPermissionTreeInfo.getDepth() + "_" + menuPermissionTreeInfo.getMenuName());	
//		});
		
		List<MenuPermissionInfo.MenuPermissionTreeInfo> treeList = new ArrayList<>();
		
		treeToTreeList(treeList, allMenuPermissionTree, childrenMark, padString, padLength);
		
		return treeList;
	}

	/**
	 * 트리구조를 플랫한 리스트로 변환
	 * @param result 변환된 리스트 데이터
	 * @param treeData List로 변환할 트리 데이터
	 * @param childrenMark 자식노드 표시
	 * @param padString 들여쓰기 문자                       
	 * @param padLength 들여쓰기 길이
	 */
	private void treeToTreeList( List<MenuPermissionInfo.MenuPermissionTreeInfo> result, List<MenuPermissionInfo.MenuPermissionTreeInfo> treeData, String childrenMark, String padString, Integer padLength) {
		for (int treeDataIndex = 0; treeDataIndex < treeData.size(); treeDataIndex++) {
			MenuPermissionInfo.MenuPermissionTreeInfo node = treeData.get(treeDataIndex);
			if (node.getDepth() > 0) {
				node.setMenuName(WiniString.repeat(padString, node.getDepth() * padLength) + childrenMark + node.getMenuName());
			}

			result.add(node);
			if (node.getChildren() != null) {
				treeToTreeList(result, node.getChildren(), childrenMark, padString, padLength);

				node.setChildren(null);
			}
		}
	}

	public List<MenuPermissionInfo> registerMenuPermissionBatch(UUID authorizationGroupId, List<MenuPermissionCommand.RegisterRequestCommand> commands) {
		List<MenuPermissionInfo> result = menuPermissionService.registerMenuPermissionBatch(authorizationGroupId, commands);

		syncAuthorizationGroupPermission(Arrays.asList(authorizationGroupId));
				
		return result;
	}

	public List<MenuPermissionInfo.UserMenuPermissionTreeInfo> getAllUserMenuPermissionTree(UUID userId, UUID authorizationGroupId) {
		return menuPermissionService.getAllUserMenuPermissionTree(userId, authorizationGroupId);
	}

	public List<MenuPermissionInfo.UserMenuPermissionTreeInfo> getUserMenuPermission(UUID userId, UUID menuId, UUID authorizationGroupId) {
		return menuPermissionService.getUserMenuPermission(userId, menuId, authorizationGroupId);
	}
	
	public void syncAllAuthorizationGroupPermission() {
		List<UUID> authorizationGroupIdList = authorizationGroupService.getAllAuthorizationGroup().stream()
				.map(authorizationGroupInfo -> authorizationGroupInfo.getId())
				.distinct()
				.collect(Collectors.toList());
		
		syncAuthorizationGroupPermission(authorizationGroupIdList);
	}
	
	private void syncAuthorizationGroupPermission(List<UUID> authorizationGroupIdList) {
		List<CommonAuthorizationGroupPermissionInfo> authorizationGroupPermissionList = menuPermissionService.searchAuthorizationGroupPermissionByAuthorizationGroupIdList(authorizationGroupIdList);
		menuPermissionProducer.authorizationGroupPermissionCdc(authorizationGroupIdList, authorizationGroupPermissionList);
	}
}
