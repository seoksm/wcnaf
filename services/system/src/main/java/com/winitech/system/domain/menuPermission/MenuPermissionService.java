package com.winitech.system.domain.menuPermission;

import com.winitech.common.domain.common.CommonAuthorizationGroupPermissionInfo;
import com.winitech.common.library.commonType.WiniPageInfo;

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
public interface MenuPermissionService {
	MenuPermissionInfo registerMenuPermission(MenuPermissionCommand.RegisterRequestCommand menuPermissionCommand);
	List<MenuPermissionInfo> registerMenuPermissionBatch(UUID authorizationGroupId, List<MenuPermissionCommand.RegisterRequestCommand> commands);
	MenuPermissionInfo modifyMenuPermission(UUID id, MenuPermissionCommand.ModifyRequestCommand menuPermissionCommand);
	void removeMenuPermission(UUID id);
	MenuPermissionInfo searchMenuPermissionById(UUID id);
	List<MenuPermissionInfo> getAllMenuPermission(UUID authorizationGroupId);
	List<MenuPermissionInfo.MenuPermissionTreeInfo> getAllMenuPermissionTree(UUID authorizationGroupId);
	List<MenuPermissionInfo.UserMenuPermissionTreeInfo> getAllUserMenuPermissionTree(UUID userId, UUID authorizationGroupId);
	List<MenuPermissionInfo.UserMenuPermissionTreeInfo> getUserMenuPermission(UUID userId, UUID menuId, UUID authorizationGroupId);

	List<CommonAuthorizationGroupPermissionInfo> searchAuthorizationGroupPermissionByAuthorizationGroupIdList(List<UUID> authorizationGroupIdList);
}
