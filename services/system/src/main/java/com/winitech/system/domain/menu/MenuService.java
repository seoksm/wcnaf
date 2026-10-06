package com.winitech.system.domain.menu;

import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.system.domain.menu.MenuInfo;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.menu
 * └ MenuService.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-24 09:32
 **/
public interface MenuService {
	MenuInfo registerMenu(MenuCommand.RegisterRequestCommand menuCommand);
	MenuInfo modifyMenu(UUID id, MenuCommand.ModifyRequestCommand menuCommand);
	void removeMenu(UUID id);
	MenuInfo searchMenuById(UUID id);
	MenuInfo searchMenuByMenuCode(String menuCode);
	List<MenuInfo> getAllMenu();
	List<MenuInfo.MenuTreeInfo> getAllMenuTree();
	WiniPageInfo<MenuInfo> getMenuPage(Integer page, Integer pageSize, String searchType, String searchKeyword);
	MenuInfo getMenuById(UUID menuId);
	void modifyMenuOrder(List<MenuCommand.OrderModifyRequestCommand> commandList);
	boolean existsMenuByProgramId(UUID programId);
	void unlinkDeletedMenuWithProgramId(UUID programId);
}
