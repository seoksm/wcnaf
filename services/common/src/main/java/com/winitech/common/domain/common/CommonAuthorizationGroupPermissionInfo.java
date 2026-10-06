package com.winitech.common.domain.common;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationGroupPermissionInfo.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 12:43
 **/
@Getter
public class CommonAuthorizationGroupPermissionInfo {
	private UUID authorizationGroupId;
	private UUID menuId;
	private String groupCode;
	private CommonAuthorizationGroupPermission.MenuPermissionStatus selectStatus;
	private CommonAuthorizationGroupPermission.MenuPermissionStatus insertStatus;
	private CommonAuthorizationGroupPermission.MenuPermissionStatus updateStatus;
	private CommonAuthorizationGroupPermission.MenuPermissionStatus deleteStatus;
	private CommonAuthorizationGroupPermission.MenuPermissionStatus printStatus;
	private CommonAuthorizationGroupPermission.MenuPermissionStatus downStatus;
	private CommonAuthorizationGroupPermission.MenuPermissionStatus manageStatus;
	private CommonAuthorizationGroupPermission.MenuPermissionStatus custom1Status;
	private CommonAuthorizationGroupPermission.MenuPermissionStatus custom2Status;
	private CommonAuthorizationGroupPermission.MenuPermissionStatus custom3Status;
	private OffsetDateTime updateAt;

	public CommonAuthorizationGroupPermissionInfo() {
	}

	public CommonAuthorizationGroupPermissionInfo(CommonAuthorizationGroupPermission commonAuthorizationGroupPermission) {
		this.authorizationGroupId = commonAuthorizationGroupPermission.getAuthorizationGroupId();
		this.menuId = commonAuthorizationGroupPermission.getMenuId();
		this.groupCode = commonAuthorizationGroupPermission.getGroupCode();
		this.selectStatus = commonAuthorizationGroupPermission.getSelectStatus();
		this.insertStatus = commonAuthorizationGroupPermission.getInsertStatus();
		this.updateStatus = commonAuthorizationGroupPermission.getUpdateStatus();
		this.deleteStatus = commonAuthorizationGroupPermission.getDeleteStatus();
		this.printStatus = commonAuthorizationGroupPermission.getPrintStatus();
		this.downStatus = commonAuthorizationGroupPermission.getDownStatus();
		this.manageStatus = commonAuthorizationGroupPermission.getManageStatus();
		this.custom1Status = commonAuthorizationGroupPermission.getCustom1Status();
		this.custom2Status = commonAuthorizationGroupPermission.getCustom2Status();
		this.custom3Status = commonAuthorizationGroupPermission.getCustom3Status();
		this.updateAt = commonAuthorizationGroupPermission.getUpdateAt();
	}

	public CommonAuthorizationGroupPermissionInfo(
			UUID authorizationGroupId, 
			UUID menuId,
			String groupCode,
			CommonAuthorizationGroupPermission.MenuPermissionStatus selectStatus,
			CommonAuthorizationGroupPermission.MenuPermissionStatus insertStatus, 
			CommonAuthorizationGroupPermission.MenuPermissionStatus updateStatus, 
			CommonAuthorizationGroupPermission.MenuPermissionStatus deleteStatus,
			CommonAuthorizationGroupPermission.MenuPermissionStatus printStatus,
			CommonAuthorizationGroupPermission.MenuPermissionStatus downStatus,
			CommonAuthorizationGroupPermission.MenuPermissionStatus manageStatus,
			CommonAuthorizationGroupPermission.MenuPermissionStatus custom1Status,
			CommonAuthorizationGroupPermission.MenuPermissionStatus custom2Status,
			CommonAuthorizationGroupPermission.MenuPermissionStatus custom3Status
	) {
		this.authorizationGroupId = authorizationGroupId;
		this.menuId = menuId;
		this.groupCode = groupCode;
		this.selectStatus = selectStatus;
		this.insertStatus = insertStatus;
		this.updateStatus = updateStatus;
		this.deleteStatus = deleteStatus;
		this.printStatus = printStatus;
		this.downStatus = downStatus;
		this.manageStatus = manageStatus;
		this.custom1Status = custom1Status;
		this.custom2Status = custom2Status;
		this.custom3Status = custom3Status;
	}

	public CommonAuthorizationGroupPermissionInfo(
			UUID authorizationGroupId, 
			UUID menuId,
			String groupCode,
			String selectStatus,
			String insertStatus, 
			String updateStatus, 
			String deleteStatus,
			String printStatus,
			String downStatus,
			String manageStatus,
			String custom1Status,
			String custom2Status,
			String custom3Status
	) {
		this.authorizationGroupId = authorizationGroupId;
		this.menuId = menuId;
		this.groupCode = groupCode;
		this.selectStatus = parseMenuPermissionStatus(selectStatus);
		this.insertStatus = parseMenuPermissionStatus(insertStatus);
		this.updateStatus = parseMenuPermissionStatus(updateStatus);
		this.deleteStatus = parseMenuPermissionStatus(deleteStatus);
		this.printStatus = parseMenuPermissionStatus(printStatus);
		this.downStatus = parseMenuPermissionStatus(downStatus);
		this.manageStatus = parseMenuPermissionStatus(manageStatus);
		this.custom1Status = parseMenuPermissionStatus(custom1Status);
		this.custom2Status = parseMenuPermissionStatus(custom2Status);
		this.custom3Status = parseMenuPermissionStatus(custom3Status);
	}
	
	private CommonAuthorizationGroupPermission.MenuPermissionStatus parseMenuPermissionStatus(String status) {
		try {
			return CommonAuthorizationGroupPermission.MenuPermissionStatus.valueOf(status);
		} catch (IllegalArgumentException | NullPointerException ignored) {
			return CommonAuthorizationGroupPermission.MenuPermissionStatus.NONE;
		}
	}
}
