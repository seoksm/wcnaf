package com.winitech.system.domain.menuPermission;

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
public interface MenuPermissionStore {
	MenuPermission store(MenuPermission menuPermission);
	List<MenuPermission> storeAll(List<MenuPermission> toSaveList);
	MenuPermission modify(MenuPermission menuPermission, MenuPermissionCommand.ModifyRequestCommand menuPermissionCommand);
	void remove(UUID menuPermissionId);
}
