package com.winitech.system.domain.menu;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.menu
 * └ MenuCommand.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-24 09:31
 **/
@Getter
@Builder
@ToString
public class MenuCommand {
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

	@Getter
	@Builder
	@ToString
	public static class RegisterRequestCommand {
		private final String menuName;
		private final String menuCode;
		private final String menuMapping;
		private final Menu.Status status;
		private final Menu.MenuStatus menuStatus;
		private final Menu.MenuType menuType;
		private final Integer sortSeq;
		private final UUID parentMenuId;
		private final UUID programId;
	}

	@Getter
	@Builder
	@ToString
	public static class ModifyRequestCommand {
		private final String menuName;
		private final String menuCode;
		private final String menuMapping;
		private final Menu.Status status;
		private final Menu.MenuStatus menuStatus;
		private final Menu.MenuType menuType;
		private final Integer sortSeq;
		private final UUID parentMenuId;
		private final UUID programId;
	}

	@Getter
	@Builder
	@ToString
	public static class OrderModifyRequestCommand {
		private final UUID id;
		private final UUID parentMenuId;
	}
}
