package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonAuthorizationGroupPermission;
import com.winitech.common.domain.common.CommonAuthorizationGroupPermissionCommand;
import com.winitech.common.domain.common.CommonAuthorizationGroupPermissionStore;
import com.winitech.common.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonAuthorizationGroupPermissionStoreImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 13:31
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonAuthorizationGroupPermissionStoreImpl implements CommonAuthorizationGroupPermissionStore {
	private final CommonAuthorizationGroupPermissionRepository commonAuthorizationGroupPermissionRepository;

	@Override
	public CommonAuthorizationGroupPermission store(CommonAuthorizationGroupPermission commonAuthorizationGroupPermission) {
		return commonAuthorizationGroupPermissionRepository.save(commonAuthorizationGroupPermission);
	}

	@Override
	public CommonAuthorizationGroupPermission modify(CommonAuthorizationGroupPermission commonAuthorizationGroupPermission, CommonAuthorizationGroupPermissionCommand command) {
		commonAuthorizationGroupPermission.setGroupCode(command.getGroupCode());
		commonAuthorizationGroupPermission.setSelectStatus(command.getSelectStatus());
		commonAuthorizationGroupPermission.setInsertStatus(command.getInsertStatus());
		commonAuthorizationGroupPermission.setUpdateStatus(command.getUpdateStatus());
		commonAuthorizationGroupPermission.setDeleteStatus(command.getDeleteStatus());
		commonAuthorizationGroupPermission.setPrintStatus(command.getPrintStatus());
		commonAuthorizationGroupPermission.setDownStatus(command.getDownStatus());
		commonAuthorizationGroupPermission.setManageStatus(command.getManageStatus());
		commonAuthorizationGroupPermission.setCustom1Status(command.getCustom1Status());
		commonAuthorizationGroupPermission.setCustom2Status(command.getCustom2Status());
		commonAuthorizationGroupPermission.setCustom3Status(command.getCustom3Status());

		return commonAuthorizationGroupPermissionRepository.save(commonAuthorizationGroupPermission);
	}

	@Override
	public void remove(UUID authorizationGroupId, UUID menuId) {
		commonAuthorizationGroupPermissionRepository.deleteByAuthorizationGroupIdAndMenuId(authorizationGroupId, menuId);
	}

	@Override
	public void storeAll(List<CommonAuthorizationGroupPermission> toSaveList) {
		commonAuthorizationGroupPermissionRepository.saveAll(toSaveList);
	}

	@Override
	public void removeAll(List<CommonAuthorizationGroupPermission> toDeleteList) {
		commonAuthorizationGroupPermissionRepository.deleteAll(toDeleteList);
	}
}
