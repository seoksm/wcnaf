package com.winitech.common.domain.common;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationGroupPermissionCommand.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 12:43
 **/
@Getter
@Builder
@ToString
public class CommonAuthorizationGroupPermissionCommand {
	private final UUID authorizationGroupId;
	private final UUID menuId;
	private final String groupCode;
	private final CommonAuthorizationGroupPermission.MenuPermissionStatus selectStatus;
	private final CommonAuthorizationGroupPermission.MenuPermissionStatus insertStatus;
	private final CommonAuthorizationGroupPermission.MenuPermissionStatus updateStatus;
	private final CommonAuthorizationGroupPermission.MenuPermissionStatus deleteStatus;
	private final CommonAuthorizationGroupPermission.MenuPermissionStatus printStatus;
	private final CommonAuthorizationGroupPermission.MenuPermissionStatus downStatus;
	private final CommonAuthorizationGroupPermission.MenuPermissionStatus manageStatus;
	private final CommonAuthorizationGroupPermission.MenuPermissionStatus custom1Status;
	private final CommonAuthorizationGroupPermission.MenuPermissionStatus custom2Status;
	private final CommonAuthorizationGroupPermission.MenuPermissionStatus custom3Status;

	public CommonAuthorizationGroupPermission toEntity() {
		return CommonAuthorizationGroupPermission.builder()
				.authorizationGroupId(authorizationGroupId)
				.menuId(menuId)
				.groupCode(groupCode)
				.selectStatus(selectStatus)
				.insertStatus(insertStatus)
				.updateStatus(updateStatus)
				.deleteStatus(deleteStatus)
				.printStatus(printStatus)
				.downStatus(downStatus)
				.manageStatus(manageStatus)
				.custom1Status(custom1Status)
				.custom2Status(custom2Status)
				.custom3Status(custom3Status)
				.build();
	}
}
