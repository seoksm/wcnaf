package com.winitech.system.domain.menuPermission;

import com.winitech.common.domain.common.CommonAuthorizationGroupPermissionInfo;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.menuPermission
 * └ MenuPermission.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-18 11:08
 **/
public interface MenuPermissionReader {
	MenuPermission getMenuPermissionById(UUID menuPermissionId);
	List<MenuPermission> getAllMenuPermission(UUID authorizationGroupId);
	List<MenuPermissionInfo.UserMenuPermissionTreeInfo> getAllUserMenuPermissionTree(UUID userId, UUID authorizationGroupId, String extraGroupCode);
	List<MenuPermissionInfo.UserMenuPermissionTreeInfo> getUserMenuPermission(UUID userId, UUID menuId, UUID authorizationGroupId, String extraGroupCode);
	boolean existMenuPermissionByMenuIdAndExcludingSelf(UUID menuId, UUID id);

	List<CommonAuthorizationGroupPermissionInfo> getAuthorizationGroupPermissionByAuthorizationGroupIdList(List<UUID> authorizationGroupIdList);

	List<String> getActionUriListByMenuId(UUID userId, String extraGroupCode, UUID menuId, String actionType, String authType);
	List<String> getActionUriListByProgramCode(UUID userId, String extraGroupCode, String programCode, String actionType, String authType);
}
