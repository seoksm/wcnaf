package com.winitech.system.application.menu;

import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.system.domain.menu.MenuCommand;
import com.winitech.system.domain.menu.MenuInfo;
import com.winitech.system.domain.menu.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.application.menu
 * └ MenuFacade.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-31 13:16
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuFacade {
	private final MenuService menuService;

	public MenuInfo registerMenu(MenuCommand.RegisterRequestCommand menuCommand) {
		return menuService.registerMenu(menuCommand);
	}

	public MenuInfo modifyMenu(UUID id, MenuCommand.ModifyRequestCommand menuCommand) {
		return menuService.modifyMenu(id, menuCommand);
	}

	public void removeMenu(UUID id) {
		menuService.removeMenu(id);
	}

	public MenuInfo searchMenuById(UUID id) {
		return menuService.searchMenuById(id);
	}

	public MenuInfo searchMenuByMenuCode(String menuCode) {
		return menuService.searchMenuByMenuCode(menuCode);
	}

	public List<MenuInfo> getAllMenu() {
		return menuService.getAllMenu();
	}

	public List<MenuInfo.MenuTreeInfo> getAllMenuTree() {
		return menuService.getAllMenuTree();
	}

	public WiniPageInfo<MenuInfo> getMenuPage(Integer page, Integer pageSize, String searchType, String searchKeyword) {
		return menuService.getMenuPage(page, pageSize, searchType, searchKeyword);
	}

	public MenuInfo getMenuById(UUID menuId) {
		return menuService.getMenuById(menuId);
	}

	public void modifyMenuOrder(List<MenuCommand.OrderModifyRequestCommand> commandList) {
		menuService.modifyMenuOrder(commandList);
	}
}
