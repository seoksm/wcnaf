package com.winitech.system.domain.menu;

import lombok.Getter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.menu
 * └ MenuInfo.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-24 09:32
 **/
@Getter
public class MenuInfo {
	private final UUID id;
	private final String menuName;
	private final String menuCode;
	private final String menuMapping;
	private final Menu.Status status;
	private final Menu.MenuStatus menuStatus;
	private final Menu.MenuType menuType;
	private final Integer sortSeq;
	private final UUID parentMenuId;
	private final UUID programId;

	public MenuInfo(Menu menu) {
		this.id = menu.getId();
		this.menuName = menu.getMenuName();
		this.menuCode = menu.getMenuCode();
		this.menuMapping = menu.getMenuMapping();
		this.status = menu.getStatus();
		this.menuStatus = menu.getMenuStatus();
		this.menuType = menu.getMenuType();
		this.sortSeq = menu.getSortSeq();
		this.parentMenuId = menu.getParentMenu() == null ? null : menu.getParentMenu().getId();
		this.programId = menu.getProgram() == null ? null : menu.getProgram().getId();
	}
	
	@Getter
	public static class MenuTreeInfo {
		private final UUID id;
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
		private final List<MenuTreeInfo> childrenMenu;		

		public MenuTreeInfo(Menu menu) {
			this.id = menu.getId();
			this.menuName = menu.getMenuName();
			this.menuCode = menu.getMenuCode();
			this.menuMapping = menu.getMenuMapping();
			this.status = menu.getStatus();
			this.menuStatus = menu.getMenuStatus();
			this.menuType = menu.getMenuType();
			this.sortSeq = menu.getSortSeq();
			this.parentMenuId = menu.getParentMenu() == null ? null : menu.getParentMenu().getId();
			this.programId = menu.getProgram() == null ? null : menu.getProgram().getId();

			if (this.menuType != Menu.MenuType.PROGRAM || menu.getProgram() == null) {
				this.programMapping = null;
			} else {
				this.programMapping = menu.getProgram().getProgramMapping();
			}

			if (menu.getMenuType() == Menu.MenuType.MENU) {
				this.childrenMenu = menu.getChildrenMenu()
						.stream()
						.map(MenuTreeInfo::new)
						.collect(Collectors.toList());
			} else {
				this.childrenMenu = null;
			}
		}
	}
}
