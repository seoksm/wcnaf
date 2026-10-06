package com.winitech.system.domain.menuPermission;

import com.winitech.common.library.WiniCom;
import com.winitech.common.library.WiniString;
import com.winitech.system.domain.menu.Menu;
import com.winitech.system.domain.menu.MenuInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.menuPermission
 * └ MenuPermission.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-18 11:08
 **/
@Getter
public class MenuPermissionInfo {
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

	public MenuPermissionInfo(MenuPermission menuPermission) {
		this.id = menuPermission.getId();
		this.menuId = menuPermission.getMenu().getId();
		this.authorizationGroupId = menuPermission.getAuthorizationGroup().getId();
		this.selectStatus = menuPermission.getSelectStatus();
		this.insertStatus = menuPermission.getInsertStatus();
		this.updateStatus = menuPermission.getUpdateStatus();
		this.deleteStatus = menuPermission.getDeleteStatus();
		this.printStatus = menuPermission.getPrintStatus();
		this.downStatus = menuPermission.getDownStatus();
		this.manageStatus = menuPermission.getManageStatus();
		this.custom1Status = menuPermission.getCustom1Status();
		this.custom2Status = menuPermission.getCustom2Status();
		this.custom3Status = menuPermission.getCustom3Status();
	}

	@Getter
	public static class MenuPermissionDetailInfo {
		private final UUID id;
		private final UUID menuId;
		private final UUID authorizationGroupId;

		private final String menuName;
		private final String menuCode;
		private final String menuMapping;
		private final Menu.Status status;
		private final Menu.MenuStatus menuStatus;
		private final Menu.MenuType menuType;
		private final Integer sortSeq;
		private final UUID parentMenuId;
		private final UUID programId;
		private final String programMapping;

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

		public MenuPermissionDetailInfo(MenuPermission menuPermission) {
			this.id = menuPermission.getId();
			this.menuId = menuPermission.getMenu().getId();
			this.authorizationGroupId = menuPermission.getAuthorizationGroup().getId();

			this.menuName = menuPermission.getMenu().getMenuName();
			this.menuCode = menuPermission.getMenu().getMenuCode();
			this.menuMapping = menuPermission.getMenu().getMenuMapping();
			this.status = menuPermission.getMenu().getStatus();
			this.menuStatus = menuPermission.getMenu().getMenuStatus();
			this.menuType = menuPermission.getMenu().getMenuType();
			this.sortSeq = menuPermission.getMenu().getSortSeq();
			this.parentMenuId = menuPermission.getMenu().getParentMenu() == null ? null : menuPermission.getMenu().getParentMenu().getId();
			this.programId = menuPermission.getMenu().getProgram() == null ? null : menuPermission.getMenu().getProgram().getId();
			this.programMapping = menuPermission.getMenu().getProgram() == null ? null : menuPermission.getMenu().getProgram().getProgramMapping();

			this.selectStatus = menuPermission.getSelectStatus();
			this.insertStatus = menuPermission.getInsertStatus();
			this.updateStatus = menuPermission.getUpdateStatus();
			this.deleteStatus = menuPermission.getDeleteStatus();
			this.printStatus = menuPermission.getPrintStatus();
			this.downStatus = menuPermission.getDownStatus();
			this.manageStatus = menuPermission.getManageStatus();
			this.custom1Status = menuPermission.getCustom1Status();
			this.custom2Status = menuPermission.getCustom2Status();
			this.custom3Status = menuPermission.getCustom3Status();
		}
	}

	@Getter
	@Setter
	public static class MenuPermissionTreeInfo  {
		private UUID id;
		private UUID menuId;
		private UUID authorizationGroupId;

		private String menuName;
		private String menuCode;
		private String menuMapping;
		private Menu.Status status;
		private Menu.MenuStatus menuStatus;
		private Menu.MenuType menuType;
		private Integer sortSeq;
		private UUID parentMenuId;
		private UUID programId;
		private String programMapping;
		
		private Integer depth;

		private MenuPermission.MenuPermissionStatus selectStatus;
		private MenuPermission.MenuPermissionStatus insertStatus;
		private MenuPermission.MenuPermissionStatus updateStatus;
		private MenuPermission.MenuPermissionStatus deleteStatus;
		private MenuPermission.MenuPermissionStatus printStatus;
		private MenuPermission.MenuPermissionStatus downStatus;
		private MenuPermission.MenuPermissionStatus manageStatus;
		private MenuPermission.MenuPermissionStatus custom1Status;
		private MenuPermission.MenuPermissionStatus custom2Status;
		private MenuPermission.MenuPermissionStatus custom3Status;

		private List<MenuPermissionTreeInfo> children;
		
		public MenuPermissionTreeInfo(MenuInfo.MenuTreeInfo menuTreeInfo, Map<UUID, MenuPermission> menuIdToMenuPermissionMap, int depth) {
			MenuPermission menuPermission = menuIdToMenuPermissionMap.get(menuTreeInfo.getId());

			this.menuId = menuTreeInfo.getId();
			this.menuName = menuTreeInfo.getMenuName();
			this.menuCode = menuTreeInfo.getMenuCode();
			this.menuMapping = menuTreeInfo.getMenuMapping();
			this.status = menuTreeInfo.getStatus();
			this.menuStatus = menuTreeInfo.getMenuStatus();
			this.menuType = menuTreeInfo.getMenuType();
			this.sortSeq = menuTreeInfo.getSortSeq();
			this.parentMenuId = menuTreeInfo.getParentMenuId();
			this.programId = menuTreeInfo.getProgramId();
			this.programMapping = menuTreeInfo.getProgramMapping();
			
			this.depth = depth;

			if (menuPermission == null) {
				this.id = null;
				this.authorizationGroupId = null;

				this.selectStatus = null;
				this.insertStatus = null;
				this.updateStatus = null;
				this.deleteStatus = null;
				this.printStatus = null;
				this.downStatus = null;
				this.manageStatus = null;
				this.custom1Status = null;
				this.custom2Status = null;
				this.custom3Status = null;
			} else {
				this.id = menuPermission.getId();
				this.authorizationGroupId = menuPermission.getAuthorizationGroup().getId();

				this.selectStatus = menuPermission.getSelectStatus();
				this.insertStatus = menuPermission.getInsertStatus();
				this.updateStatus = menuPermission.getUpdateStatus();
				this.deleteStatus = menuPermission.getDeleteStatus();
				this.printStatus = menuPermission.getPrintStatus();
				this.downStatus = menuPermission.getDownStatus();
				this.manageStatus = menuPermission.getManageStatus();
				this.custom1Status = menuPermission.getCustom1Status();
				this.custom2Status = menuPermission.getCustom2Status();
				this.custom3Status = menuPermission.getCustom3Status();
			}

			if (menuTreeInfo.getChildrenMenu() == null) {
				this.children = null;
			} else {
				this.children = menuTreeInfo.getChildrenMenu().stream()
						.map(child -> new MenuPermissionTreeInfo(child, menuIdToMenuPermissionMap, depth + 1))
						.collect(Collectors.toList());
			}
		}
	}

	/**
	 * 사용자 메뉴 권한 트리 정보
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class UserMenuPermissionTreeInfo  {
		private UUID menuId;
		private UUID authorizationGroupId;

		private String menuName;
		private String menuCode;
		private String menuMapping;
		private String status;
		private String menuStatus;
		private String menuType;
		private Integer sortSeq;
		private UUID parentMenuId;
		private UUID programId;
		private String programMapping;
		
		private Integer depth;
		private Integer programCount;

		private String selectStatus;
		private String insertStatus;
		private String updateStatus;
		private String deleteStatus;
		private String printStatus;
		private String downStatus;
		private String manageStatus;
		private String custom1Status;
		private String custom2Status;
		private String custom3Status;

		private List<UserMenuPermissionTreeInfo> children;
	}
}
