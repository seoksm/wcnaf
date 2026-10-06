package com.winitech.system.domain.menu;

import com.winitech.system.domain.menu.Menu;
import com.winitech.system.domain.menu.MenuInfo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.menu
 * └ MenuReader.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-24 09:32
 **/
public interface MenuReader {
	Menu getMenuById(UUID menuId);

	List<Menu> getMenuByIdList(List<UUID> toFetchMenuIdSet);

	Menu getMenuByMenuCode(String menuCode);

	List<Menu> getAllMenu();

	List<Menu> getAllMenuTree();

	Page<MenuInfo> getMenuPage(Integer page, Integer pageSize, String searchType, String searchKeyword);

	boolean existMenuByMenuCodeAndExcludingSelf(String menuCode, UUID menuId);

	boolean existsMenuByProgramId(UUID programId);
}