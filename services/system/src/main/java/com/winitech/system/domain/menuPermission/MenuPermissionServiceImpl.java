package com.winitech.system.domain.menuPermission;

import com.winitech.common.domain.common.CommonAuthorizationGroupPermissionInfo;
import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.system.domain.authorizationGroup.AuthorizationGroup;
import com.winitech.system.domain.authorizationGroup.AuthorizationGroupReader;
import com.winitech.system.domain.menu.MenuInfo;
import com.winitech.system.domain.menu.MenuReader;
import com.winitech.system.domain.menu.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
@Transactional
public class MenuPermissionServiceImpl extends EgovAbstractServiceImpl implements MenuPermissionService {
	private final MenuPermissionStore menuPermissionStore;
	private final MenuPermissionReader menuPermissionReader;
	private final MenuReader menuReader;
	
	private final MenuService menuService;
	
	private final AuthorizationGroupReader authorizationGroupReader;

	@Override
	public MenuPermissionInfo registerMenuPermission(MenuPermissionCommand.RegisterRequestCommand menuPermissionCommand) {
		if (menuPermissionCommand.getMenuId() != null && menuPermissionReader.existMenuPermissionByMenuIdAndExcludingSelf(menuPermissionCommand.getMenuId(), UUID.randomUUID())) {
			throw new IllegalStatusException("Already registered Menu.");
		}

		MenuPermission initMenuPermission = MenuPermission.builder()
				.menu(menuReader.getMenuById(menuPermissionCommand.getMenuId()))
				.authorizationGroup(authorizationGroupReader.getAuthorizationGroup(menuPermissionCommand.getAuthorizationGroupId()))
				.selectStatus(menuPermissionCommand.getSelectStatus())
				.insertStatus(menuPermissionCommand.getInsertStatus())
				.updateStatus(menuPermissionCommand.getUpdateStatus())
				.deleteStatus(menuPermissionCommand.getDeleteStatus())
				.printStatus(menuPermissionCommand.getPrintStatus())
				.downStatus(menuPermissionCommand.getDownStatus())
				.manageStatus(menuPermissionCommand.getManageStatus())
				.custom1Status(menuPermissionCommand.getCustom1Status())
				.custom2Status(menuPermissionCommand.getCustom2Status())
				.custom3Status(menuPermissionCommand.getCustom3Status())

				.build();

		MenuPermission menuPermission = menuPermissionStore.store(initMenuPermission);
		return new MenuPermissionInfo(menuPermission);
	}

	@Override
	public List<MenuPermissionInfo> registerMenuPermissionBatch(UUID authorizationGroupId, List<MenuPermissionCommand.RegisterRequestCommand> commands) {
		AuthorizationGroup authorizationGroup = AuthorizationGroup.builder().id(authorizationGroupId).build();

		List<MenuPermission> menuPermissionList = commands.stream()
				.filter(command -> {
					boolean isAllNone = (command.getSelectStatus() == null || command.getSelectStatus().equals(MenuPermission.MenuPermissionStatus.NONE))
							&& (command.getInsertStatus() == null || command.getInsertStatus().equals(MenuPermission.MenuPermissionStatus.NONE))
							&& (command.getUpdateStatus() == null || command.getUpdateStatus().equals(MenuPermission.MenuPermissionStatus.NONE))
							&& (command.getDeleteStatus() == null || command.getDeleteStatus().equals(MenuPermission.MenuPermissionStatus.NONE))
							&& (command.getPrintStatus() == null || command.getPrintStatus().equals(MenuPermission.MenuPermissionStatus.NONE))
							&& (command.getDownStatus() == null || command.getDownStatus().equals(MenuPermission.MenuPermissionStatus.NONE))
							&& (command.getManageStatus() == null || command.getManageStatus().equals(MenuPermission.MenuPermissionStatus.NONE))
							&& (command.getCustom1Status() == null || command.getCustom1Status().equals(MenuPermission.MenuPermissionStatus.NONE))
							&& (command.getCustom2Status() == null || command.getCustom2Status().equals(MenuPermission.MenuPermissionStatus.NONE))
							&& (command.getCustom3Status() == null || command.getCustom3Status().equals(MenuPermission.MenuPermissionStatus.NONE));

					return !isAllNone;
				})
				.map(command -> MenuPermission.builder()
						.menu(menuReader.getMenuById(command.getMenuId()))
						.authorizationGroup(authorizationGroup)
						.selectStatus(command.getSelectStatus())
						.insertStatus(command.getInsertStatus())
						.updateStatus(command.getUpdateStatus())
						.deleteStatus(command.getDeleteStatus())
						.printStatus(command.getPrintStatus())
						.downStatus(command.getDownStatus())
						.manageStatus(command.getManageStatus())
						.custom1Status(command.getCustom1Status())
						.custom2Status(command.getCustom2Status())
						.custom3Status(command.getCustom3Status())
						.build())
				.collect(Collectors.toList());

		final Map<String, MenuPermission> prevMenuPermissionMap = new HashMap<>();
		final Map<String, MenuPermission> toDeleteMap = prevMenuPermissionMap;

		// 기존 항목 조회하여 Map에 저장
		menuPermissionReader.getAllMenuPermission(authorizationGroupId).forEach(menuPermission -> {
			String key = menuPermission.getMenu().getId().toString();
			prevMenuPermissionMap.put(key, menuPermission);
		});

		List<MenuPermission> toSaveList = new ArrayList<>();
		Set<String> toSaveKeySet = new HashSet<>();

		// 신규 항목 및 수정된 항목을 toSaveList에 저장 
		menuPermissionList.forEach(menuPermission -> {
			String key = menuPermission.getMenu().getId().toString();

			if (toSaveKeySet.contains(key)) {
				return;
			}

			if (prevMenuPermissionMap.containsKey(key)) {
				MenuPermission prevMenuPermission = prevMenuPermissionMap.get(key);
				
				prevMenuPermission.setSelectStatus(menuPermission.getSelectStatus());
				prevMenuPermission.setInsertStatus(menuPermission.getInsertStatus());
				prevMenuPermission.setUpdateStatus(menuPermission.getUpdateStatus());
				prevMenuPermission.setDeleteStatus(menuPermission.getDeleteStatus());
				prevMenuPermission.setPrintStatus(menuPermission.getPrintStatus());
				prevMenuPermission.setDownStatus(menuPermission.getDownStatus());
				prevMenuPermission.setManageStatus(menuPermission.getManageStatus());
				prevMenuPermission.setCustom1Status(menuPermission.getCustom1Status());
				prevMenuPermission.setCustom2Status(menuPermission.getCustom2Status());
				prevMenuPermission.setCustom3Status(menuPermission.getCustom3Status());

				toSaveList.add(prevMenuPermission);

				// 기존에 있는 항목 중 사용되는 항목은 삭제 대상에서 제외 
				toDeleteMap.remove(key);
			} else {
				toSaveList.add(menuPermission);
			}

			toSaveKeySet.add(key);
		});

		// 신규 항목 및 수정된 항목 저장
		List<MenuPermission> storedMenuPermissionList = menuPermissionStore.storeAll(toSaveList);

		// 기존 항목중 삭제된 항목 삭제
		toDeleteMap.values().forEach(menuPermission -> menuPermissionStore.remove(menuPermission.getId()));

		return storedMenuPermissionList.stream()
				.map(MenuPermissionInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public MenuPermissionInfo modifyMenuPermission(UUID id, MenuPermissionCommand.ModifyRequestCommand menuPermissionCommand) {
		if (menuPermissionCommand.getMenuId() != null && menuPermissionReader.existMenuPermissionByMenuIdAndExcludingSelf(menuPermissionCommand.getMenuId(), id)) {
			throw new IllegalStatusException("Already registered Menu.");
		}

		MenuPermission modifyMenuPermission = menuPermissionReader.getMenuPermissionById(id);

		MenuPermission menuPermission = menuPermissionStore.modify(modifyMenuPermission, menuPermissionCommand);
		return new MenuPermissionInfo(menuPermission);
	}

	@Override
	public void removeMenuPermission(UUID id) {
		menuPermissionStore.remove(id);
	}

	@Override
	public MenuPermissionInfo searchMenuPermissionById(UUID id) {
		MenuPermission menuPermission = menuPermissionReader.getMenuPermissionById(id);
		return new MenuPermissionInfo(menuPermission);
	}

	@Override
	public List<MenuPermissionInfo> getAllMenuPermission(UUID authorizationGroupId) {
		List<MenuPermission> menuPermissionList = menuPermissionReader.getAllMenuPermission(authorizationGroupId);
		return menuPermissionList.stream()
				.map(MenuPermissionInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	public List<MenuPermissionInfo.MenuPermissionTreeInfo> getAllMenuPermissionTree(UUID authorizationGroupId) {
		List<MenuInfo.MenuTreeInfo> allMenuTree = menuService.getAllMenuTree();
		Map<UUID, MenuPermission> menuIdToMenuPermissionMap = menuPermissionReader.getAllMenuPermission(authorizationGroupId)
				.stream()
				.collect(Collectors.toMap(menuPermission -> menuPermission.getMenu().getId(), menuPermission -> menuPermission));
		
		List<MenuPermissionInfo.MenuPermissionTreeInfo> allMenuPermissionTree = allMenuTree
				.stream()
				.map(menuTreeInfo -> {
					return new MenuPermissionInfo.MenuPermissionTreeInfo(menuTreeInfo, menuIdToMenuPermissionMap, 0);	
				})
				.collect(Collectors.toList());
		
		return allMenuPermissionTree;
	}

	@Override
	public List<MenuPermissionInfo.UserMenuPermissionTreeInfo> getAllUserMenuPermissionTree(UUID userId, UUID authorizationGroupId) {
		// 로그인 되어있으면 MEMBER 그룹권한, 로그인 안되어있으면 GUEST 그룹권한 추가 부여
		String extraGroupCode = userId == null ? "GUEST" : "MEMBER";

		List<MenuPermissionInfo.UserMenuPermissionTreeInfo> allMenuPermissionTree = menuPermissionReader.getAllUserMenuPermissionTree(userId, authorizationGroupId, extraGroupCode);
	
		return allMenuPermissionTree;
	}

	@Override
	public List<MenuPermissionInfo.UserMenuPermissionTreeInfo> getUserMenuPermission(UUID userId, UUID menuId, UUID authorizationGroupId) {
		// 로그인 되어있으면 MEMBER 그룹권한, 로그인 안되어있으면 GUEST 그룹권한 추가 부여
		String extraGroupCode = userId == null ? "GUEST" : "MEMBER";

		List<MenuPermissionInfo.UserMenuPermissionTreeInfo> menuPermissionList = menuPermissionReader.getUserMenuPermission(userId, menuId, authorizationGroupId, extraGroupCode);

		return menuPermissionList;
	}

	@Override
	public List<CommonAuthorizationGroupPermissionInfo> searchAuthorizationGroupPermissionByAuthorizationGroupIdList(List<UUID> authorizationGroupIdList) {
		return menuPermissionReader.getAuthorizationGroupPermissionByAuthorizationGroupIdList(authorizationGroupIdList);
	}
}
