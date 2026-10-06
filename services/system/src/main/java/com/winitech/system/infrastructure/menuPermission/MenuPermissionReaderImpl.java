package com.winitech.system.infrastructure.menuPermission;

import com.winitech.common.domain.common.CommonAuthorizationGroupPermissionInfo;
import com.winitech.common.domain.common.CommonMenuAction;
import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.system.domain.menuPermission.MenuPermission;
import com.winitech.system.domain.menuPermission.MenuPermissionInfo;
import com.winitech.system.domain.menuPermission.MenuPermissionReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.menuPermission
 * └ MenuPermission.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-18 10:58
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class MenuPermissionReaderImpl implements MenuPermissionReader {
	private final MenuPermissionRepository menuPermissionRepository;
	private final MenuPermissionQueryRepository menuPermissionQueryRepository;

	@Override
	public MenuPermission getMenuPermissionById(UUID menuPermissionId) {
		return menuPermissionRepository.findById(menuPermissionId).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public List<MenuPermission> getAllMenuPermission(UUID authorizationGroupId) {
		return menuPermissionRepository.findAllByAuthorizationGroupIdOrderByIdDesc(authorizationGroupId);
	}

	@Override
	public List<MenuPermissionInfo.UserMenuPermissionTreeInfo> getAllUserMenuPermissionTree(UUID userId, UUID authorizationGroupId, String extraGroupCode) {
		return menuPermissionQueryRepository.getMenuPermissionTreeByUserId(userId, authorizationGroupId, extraGroupCode);
	}

	@Override
	public List<MenuPermissionInfo.UserMenuPermissionTreeInfo> getUserMenuPermission(UUID userId, UUID menuId, UUID authorizationGroupId, String extraGroupCode) {
		return menuPermissionQueryRepository.getMenuPermissionByUserId(userId, menuId, authorizationGroupId, extraGroupCode);
	}

	@Override
	public boolean existMenuPermissionByMenuIdAndExcludingSelf(UUID menuId, UUID menuPermissionid) {
		return menuPermissionRepository.existsByMenuIdAndIdNot(menuId, menuPermissionid);
	}

	@Override
	public List<CommonAuthorizationGroupPermissionInfo> getAuthorizationGroupPermissionByAuthorizationGroupIdList(List<UUID> authorizationGroupIdList) {
		return menuPermissionRepository.getAuthorizationGroupPermissionByAuthorizationGroupIdList(authorizationGroupIdList);
	}

	@Override
	public List<String> getActionUriListByMenuId(UUID userId, String extraGroupCode, UUID menuId, String actionType, String authType) {
		return menuPermissionRepository.getActionListByMenuId(userId, extraGroupCode, menuId, actionType, authType);
	}

	@Override
	public List<String> getActionUriListByProgramCode(UUID userId, String extraGroupCode, String programCode, String actionType, String authType) {
		return menuPermissionRepository.getActionListByProgramCode(userId, extraGroupCode, programCode, actionType, authType);
	}
}
