package com.winitech.system.infrastructure.menu;

import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.common.library.WiniCom;
import com.winitech.system.domain.menu.Menu;
import com.winitech.system.domain.menu.MenuCommand;
import com.winitech.system.domain.menu.MenuStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.infrastructure.menu
 * └ MenuStoreImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-24 09:49
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class MenuStoreImpl implements MenuStore {
	private final MenuRepository menuRepository;

	@Override
	public Menu store(Menu menu) {
		return menuRepository.save(menu);
	}

	@Override
	public Menu modify(Menu menu, MenuCommand.ModifyRequestCommand command) {
		menu.setMenuName(command.getMenuName());
		menu.setMenuCode(command.getMenuCode());
		menu.setMenuMapping(command.getMenuMapping());
		menu.setStatus(command.getStatus());
		menu.setMenuStatus(command.getMenuStatus());
		menu.setMenuType(command.getMenuType());
		menu.setSortSeq(command.getSortSeq());

		return menuRepository.save(menu);
	}

	@Override
	public void remove(UUID menuId) {
		// soft delete
		List<Menu> menuList = menuRepository.findAllByIdAndSystemStatus(menuId, Menu.SystemStatus.ENABLE);

		List<Menu> toRemoveList = menuList.stream()
				.flatMap(menu -> WiniCom.flatMapRecursive(menu, childMenu -> childMenu.getChildrenMenu().stream()))
				.collect(Collectors.toList());
		
		toRemoveList.forEach(menu -> menu.setSystemStatus(Menu.SystemStatus.DISABLE));

		// // hard delete
		// menuRepository.deleteById(menuId);
	}

	@Override
	public void updateParentAndSortSeq(UUID menuId, UUID parentMenuId, int sortSeq) {
		Menu parentMenu;
		
		if (parentMenuId == null) {
			parentMenu = null;
		} else {
			parentMenu = new Menu();
			parentMenu.setId(parentMenuId);
		}
	
		menuRepository.updateParentAndSortSeq(menuId, parentMenu, sortSeq);
	}

	@Override
	public void unlinkDeletedMenuWithProgramId(UUID programId) {
		menuRepository.unlinkDeletedMenuWithProgramId(programId);
	}

	@Override
	public void flush() {
		menuRepository.flush();
	}
}
