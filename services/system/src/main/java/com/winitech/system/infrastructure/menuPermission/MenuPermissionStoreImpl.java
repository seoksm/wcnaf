package com.winitech.system.infrastructure.menuPermission;

import com.winitech.system.domain.menuPermission.MenuPermission;
import com.winitech.system.domain.menuPermission.MenuPermissionCommand;
import com.winitech.system.domain.menuPermission.MenuPermissionReader;
import com.winitech.system.domain.menuPermission.MenuPermissionStore;
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
public class MenuPermissionStoreImpl implements MenuPermissionStore {
	private final MenuPermissionRepository menuPermissionRepository;
	private final MenuPermissionReader menuPermissionReader;

	@Override
	public MenuPermission store(MenuPermission menuPermission) {
		return menuPermissionRepository.save(menuPermission);
	}

	@Override
	public List<MenuPermission> storeAll(List<MenuPermission> toSaveList) {
		return menuPermissionRepository.saveAll(toSaveList);
	}

	@Override
	public MenuPermission modify(MenuPermission menuPermission, MenuPermissionCommand.ModifyRequestCommand command) {
		menuPermission.setSelectStatus(command.getSelectStatus());
		menuPermission.setInsertStatus(command.getInsertStatus());
		menuPermission.setUpdateStatus(command.getUpdateStatus());
		menuPermission.setDeleteStatus(command.getDeleteStatus());
		menuPermission.setPrintStatus(command.getPrintStatus());
		menuPermission.setDownStatus(command.getDownStatus());
		menuPermission.setManageStatus(command.getManageStatus());
		
		menuPermission.setCustom1Status(command.getCustom1Status());
		menuPermission.setCustom2Status(command.getCustom2Status());
		menuPermission.setCustom3Status(command.getCustom3Status());

		return menuPermissionRepository.save(menuPermission);
	}

	@Override
	public void remove(UUID menuPermissionId) {
		MenuPermission menuPermission = menuPermissionReader.getMenuPermissionById(menuPermissionId);
		menuPermissionRepository.delete(menuPermission);
	}
}
