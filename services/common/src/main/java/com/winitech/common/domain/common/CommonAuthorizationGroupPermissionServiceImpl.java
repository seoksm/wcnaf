package com.winitech.common.domain.common;

import com.winitech.common.exception.IllegalStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationGroupPermissionServiceImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 12:43
 **/

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonAuthorizationGroupPermissionServiceImpl extends EgovAbstractServiceImpl implements CommonAuthorizationGroupPermissionService {
	private final CommonAuthorizationGroupPermissionStore commonAuthorizationGroupPermissionStore;
	private final CommonAuthorizationGroupPermissionReader commonAuthorizationGroupPermissionReader;
	private final CommonUserStore commonUserStore;

	@Override
	public CommonAuthorizationGroupPermissionInfo registerCommonAuthorizationGroupPermission(CommonAuthorizationGroupPermissionCommand command) {
		CommonAuthorizationGroupPermission initCommonAuthorizationGroupPermission = CommonAuthorizationGroupPermission.builder()
				.authorizationGroupId(command.getAuthorizationGroupId())
				.menuId(command.getMenuId())
				.groupCode(command.getGroupCode())
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
				.build();
		
		CommonAuthorizationGroupPermission commonAuthorizationGroupPermission = commonAuthorizationGroupPermissionStore.store(initCommonAuthorizationGroupPermission);
		return new CommonAuthorizationGroupPermissionInfo(commonAuthorizationGroupPermission);
	}

	@Override
	public CommonAuthorizationGroupPermissionInfo modifyCommonAuthorizationGroupPermission(UUID authorizationGroupId, UUID menuId, CommonAuthorizationGroupPermissionCommand command) {
		CommonAuthorizationGroupPermission modifyCommonAuthorizationGroupPermission = commonAuthorizationGroupPermissionReader.getCommonAuthorizationGroupPermissionById(authorizationGroupId, menuId);
		modifyCommonAuthorizationGroupPermission.setAuthorizationGroupId(command.getAuthorizationGroupId());
		modifyCommonAuthorizationGroupPermission.setMenuId(command.getMenuId());
		modifyCommonAuthorizationGroupPermission.setGroupCode(command.getGroupCode());
		modifyCommonAuthorizationGroupPermission.setSelectStatus(command.getSelectStatus());
		modifyCommonAuthorizationGroupPermission.setInsertStatus(command.getInsertStatus());
		modifyCommonAuthorizationGroupPermission.setUpdateStatus(command.getUpdateStatus());
		modifyCommonAuthorizationGroupPermission.setDeleteStatus(command.getDeleteStatus());
		modifyCommonAuthorizationGroupPermission.setPrintStatus(command.getPrintStatus());
		modifyCommonAuthorizationGroupPermission.setDownStatus(command.getDownStatus());
		modifyCommonAuthorizationGroupPermission.setManageStatus(command.getManageStatus());
		modifyCommonAuthorizationGroupPermission.setCustom1Status(command.getCustom1Status());
		modifyCommonAuthorizationGroupPermission.setCustom2Status(command.getCustom2Status());
		modifyCommonAuthorizationGroupPermission.setCustom3Status(command.getCustom3Status());
		
		CommonAuthorizationGroupPermission commonAuthorizationGroupPermission = commonAuthorizationGroupPermissionStore.store(modifyCommonAuthorizationGroupPermission);
		return new CommonAuthorizationGroupPermissionInfo(commonAuthorizationGroupPermission);
	}

	@Override
	public void removeCommonAuthorizationGroupPermission(UUID authorizationGroupId, UUID menuId) {
		commonAuthorizationGroupPermissionStore.remove(authorizationGroupId, menuId);
	}

	@Override
	public CommonAuthorizationGroupPermissionInfo searchCommonAuthorizationGroupPermissionById(UUID authorizationGroupId, UUID menuId) {
		CommonAuthorizationGroupPermission commonAuthorizationGroupPermission = commonAuthorizationGroupPermissionReader.getCommonAuthorizationGroupPermissionById(authorizationGroupId, menuId);
		return new CommonAuthorizationGroupPermissionInfo(commonAuthorizationGroupPermission);
	}

//	@Override
//	public List<CommonAuthorizationGroupPermissionInfo> searchCommonAuthorizationGroupPermissionByName(String name) {
//		List<CommonAuthorizationGroupPermission> commonAuthorizationGroupPermissionList = commonAuthorizationGroupPermissionReader.getCommonAuthorizationGroupPermissionByName(name);
//		return commonAuthorizationGroupPermissionList.stream().map(CommonAuthorizationGroupPermissionInfo::new).collect(Collectors.toList());
//	}

	@Override
	public List<CommonAuthorizationGroupPermissionInfo> getAllCommonAuthorizationGroupPermission() {
		List<CommonAuthorizationGroupPermission> commonAuthorizationGroupPermissionList = commonAuthorizationGroupPermissionReader.getAllCommonAuthorizationGroupPermission();
		return commonAuthorizationGroupPermissionList.stream().map(CommonAuthorizationGroupPermissionInfo::new).collect(Collectors.toList());
	}

	@Override
	public CommonAuthorizationGroupPermissionInfo saveCommonAuthorizationGroupPermission(CommonAuthorizationGroupPermissionCommand command) {
		CommonAuthorizationGroupPermission commonAuthorizationGroupPermission = commonAuthorizationGroupPermissionReader.getCommonAuthorizationGroupPermissionByIdIfExists(command.getAuthorizationGroupId(), command.getMenuId());
		
		if (commonAuthorizationGroupPermission == null) {
			return registerCommonAuthorizationGroupPermission(command);
		} else {
			return modifyCommonAuthorizationGroupPermission(
					commonAuthorizationGroupPermission.getAuthorizationGroupId(),
					commonAuthorizationGroupPermission.getMenuId(), 
					command
			);
		}
	}

	@Override
	public void syncAll(List<UUID> authorizationGroupIdList, List<CommonAuthorizationGroupPermissionCommand> commandList) {
		Map<UUID, List<CommonAuthorizationGroupPermission>> authorizationGroupIdToAuthorizationGroupPermissionListMap = commonAuthorizationGroupPermissionReader.getListByAuthorizationGroupIdList(authorizationGroupIdList)
				.stream()
				.collect(Collectors.groupingBy(CommonAuthorizationGroupPermission::getAuthorizationGroupId));

		Map<UUID, List<CommonAuthorizationGroupPermissionCommand>> authorizationGroupPermissionCommandMap = commandList
				.stream()
				.collect(Collectors.groupingBy(CommonAuthorizationGroupPermissionCommand::getAuthorizationGroupId));

		List<CommonAuthorizationGroupPermission> toDeleteList = new ArrayList<>();
		List<CommonAuthorizationGroupPermission> toSaveList = new ArrayList<>();

		authorizationGroupIdList.forEach(authorizationGroupId -> {
			List<CommonAuthorizationGroupPermission> authorizationGroupPermissionList = authorizationGroupIdToAuthorizationGroupPermissionListMap.get(authorizationGroupId);

			if (authorizationGroupPermissionList == null) {
				authorizationGroupPermissionList = new ArrayList<>();
			}

			List<CommonAuthorizationGroupPermissionCommand> authorizationGroupPermissionCommandList = authorizationGroupPermissionCommandMap.get(authorizationGroupId);

			Map<UUID, List<CommonAuthorizationGroupPermission>> menuIdToAuthorizationGroupPermissionMap = authorizationGroupPermissionList
					.stream()
					.collect(Collectors.groupingBy(CommonAuthorizationGroupPermission::getMenuId));

			if (authorizationGroupPermissionCommandList != null) {
				for (CommonAuthorizationGroupPermissionCommand command : authorizationGroupPermissionCommandList) {
					toSaveList.add(command.toEntity());

					if (menuIdToAuthorizationGroupPermissionMap.containsKey(command.getMenuId())) {
						menuIdToAuthorizationGroupPermissionMap.remove(command.getMenuId());
					}
				}
			}

			menuIdToAuthorizationGroupPermissionMap.forEach((menuId, list) -> {
				toDeleteList.addAll(list);
			});
		});

		commonAuthorizationGroupPermissionStore.storeAll(toSaveList);
		commonAuthorizationGroupPermissionStore.removeAll(toDeleteList);
	}
}
