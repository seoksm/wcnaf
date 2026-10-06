package com.winitech.system.domain.menuPermission;

import com.winitech.system.domain.authorizationGroup.AuthorizationGroupReader;
import com.winitech.system.domain.menu.MenuReader;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.menuPermission
 * └ MenuPermission.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-18 11:08
 **/
@Getter
@Builder
@ToString
public class MenuPermissionCommand {
	private final UUID id;
	private final UUID menuId;
	private final UUID authorizationGroupId;
	private final MenuPermission.MenuPermissionStatus selectStatus;
	private final MenuPermission.MenuPermissionStatus insertStatus;
	private final MenuPermission.MenuPermissionStatus updateStatus;
	private final MenuPermission.MenuPermissionStatus deleteStatus;
	private final MenuPermission.MenuPermissionStatus printStatus;
	private final MenuPermission.MenuPermissionStatus downStatus;
	private final MenuPermission.MenuPermissionStatus manageStatus;
	private final MenuPermission.MenuPermissionStatus custom1Status;
	private final MenuPermission.MenuPermissionStatus custom2Status;
	private final MenuPermission.MenuPermissionStatus custom3Status;
	private final MenuPermission.MenuPermissionStatus custom4Status;
	private final MenuPermission.MenuPermissionStatus custom5Status;
	
	public MenuPermission toEntity(MenuReader menuReader, AuthorizationGroupReader authorizationGroupReader) {
		return MenuPermission.builder()
			.id(id)
			.menu(menuReader.getMenuById(menuId))
			.authorizationGroup(authorizationGroupReader.getAuthorizationGroup(authorizationGroupId))
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

	@Getter
	@Builder
	@ToString
	public static class RegisterRequestCommand {
		private final UUID menuId;
		private final UUID authorizationGroupId;
		private final MenuPermission.MenuPermissionStatus selectStatus;
		private final MenuPermission.MenuPermissionStatus insertStatus;
		private final MenuPermission.MenuPermissionStatus updateStatus;
		private final MenuPermission.MenuPermissionStatus deleteStatus;
		private final MenuPermission.MenuPermissionStatus printStatus;
		private final MenuPermission.MenuPermissionStatus downStatus;
		private final MenuPermission.MenuPermissionStatus manageStatus;
		private final MenuPermission.MenuPermissionStatus custom1Status;
		private final MenuPermission.MenuPermissionStatus custom2Status;
		private final MenuPermission.MenuPermissionStatus custom3Status;
	}

	@Getter
	@Builder
	@ToString
	public static class ModifyRequestCommand {
		private final UUID menuId;
		private final UUID authorizationGroupId;
		private final MenuPermission.MenuPermissionStatus selectStatus;
		private final MenuPermission.MenuPermissionStatus insertStatus;
		private final MenuPermission.MenuPermissionStatus updateStatus;
		private final MenuPermission.MenuPermissionStatus deleteStatus;
		private final MenuPermission.MenuPermissionStatus printStatus;
		private final MenuPermission.MenuPermissionStatus downStatus;
		private final MenuPermission.MenuPermissionStatus manageStatus;
		private final MenuPermission.MenuPermissionStatus custom1Status;
		private final MenuPermission.MenuPermissionStatus custom2Status;
		private final MenuPermission.MenuPermissionStatus custom3Status;
	}
}
