package com.winitech.system.domain.menu;

import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.common.library.core.CircularReferenceDetector;
import com.winitech.system.domain.program.Program;
import com.winitech.system.domain.program.ProgramReader;
import com.winitech.system.domain.program.ProgramStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.menu
 * └ MenuServiceImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-24 09:32
 **/

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuServiceImpl extends EgovAbstractServiceImpl implements MenuService {
	private final MenuStore menuStore;
	private final MenuReader menuReader;
	
	private final ProgramReader programReader;
	private final ProgramStore programStore;
	
	@Override
	public MenuInfo registerMenu(MenuCommand.RegisterRequestCommand menuCommand) {
		if (menuCommand.getMenuCode() != null && menuReader.existMenuByMenuCodeAndExcludingSelf(menuCommand.getMenuCode(), UUID.randomUUID())) {
			throw new IllegalStatusException("Already registered Menu code.");
		}

		Menu parentMenu = null;
		if(menuCommand.getParentMenuId() != null) {
			try {
				parentMenu = menuReader.getMenuById(menuCommand.getParentMenuId());
			} catch (EntityNotFoundException e) {
				throw new IllegalStatusException("부모 메뉴가 존재하지 않습니다.");
			}
		}

		Program program = null;
		if(menuCommand.getProgramId() != null) {
			try {
				program = programReader.getProgramById(menuCommand.getProgramId());
			} catch (EntityNotFoundException e) {
				throw new IllegalStatusException("프로그램이 존재하지 않습니다.");
			}
		}

		Menu initMenu = Menu.builder()
				.menuName(menuCommand.getMenuName())
				.menuCode(menuCommand.getMenuCode())
				.menuMapping(menuCommand.getMenuMapping())
				.status(menuCommand.getStatus())
				.menuStatus(menuCommand.getMenuStatus())
				.menuType(menuCommand.getMenuType())
				.sortSeq(menuCommand.getSortSeq())
				.parentMenu(parentMenu)
				.program(program)
				.systemStatus(Menu.SystemStatus.ENABLE)
				.build();
				
		Menu menu = menuStore.store(initMenu);
		return new MenuInfo(menu);
	}

	@Override
	public MenuInfo modifyMenu(UUID id, MenuCommand.ModifyRequestCommand menuCommand) {

		Menu.MenuType menuType = menuCommand.getMenuType();
		if (menuType != null && menuType.equals(Menu.MenuType.MENU)) {
			if (menuReader.existMenuByMenuCodeAndExcludingSelf(menuCommand.getMenuCode(), id)) {
				throw new IllegalStatusException("Already registered Menu code.");
			}
		}

		Menu parentMenu = null;
		if(menuCommand.getParentMenuId() != null) {
			try {
				parentMenu = menuReader.getMenuById(menuCommand.getParentMenuId());
			} catch (EntityNotFoundException e) {
				throw new IllegalStatusException("부모 메뉴가 존재하지 않습니다.");
			}
			
			if (id.equals(menuCommand.getParentMenuId())) {
				throw new IllegalStatusException("순환참조가 발견되었습니다. 부모 메뉴를 자기 자신으로 설정할 수 없습니다.");
			}
		}

		Program program = null;
		if(menuCommand.getProgramId() != null) {
			try {
				program = programReader.getProgramById(menuCommand.getProgramId());
			} catch (EntityNotFoundException e) {
				throw new IllegalStatusException("프로그램이 존재하지 않습니다.");
			}
		}

		Menu modifyMenu = menuReader.getMenuById(id);
		
		modifyMenu.setParentMenu(parentMenu);
		modifyMenu.setProgram(program);
		
		Menu menu = menuStore.modify(modifyMenu, menuCommand);
		return new MenuInfo(menu);
	}

	@Override
	public void removeMenu(UUID id) {
		/*Menu menu = menuReader.getMenuById(id);
		
		if (menu.getChildrenMenu() != null && menu.getChildrenMenu().size() > 0) {
			throw new IllegalStatusException("하위 메뉴를 먼저 삭제해 주세요.");
		}*/
		
		menuStore.remove(id);
	}

	@Override
	@Transactional(readOnly = true)
	public MenuInfo searchMenuById(UUID id) {
		Menu menu = menuReader.getMenuById(id);
		return new MenuInfo(menu);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MenuInfo> getAllMenu() {
		List<Menu> menuList = menuReader.getAllMenu();
		return menuList.stream()
				.map(MenuInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<MenuInfo.MenuTreeInfo> getAllMenuTree() {
		List<Menu> menuList = menuReader.getAllMenuTree();
		return menuList.stream()
				.filter(menu -> menu.getParentMenu() == null)
				.map(MenuInfo.MenuTreeInfo::new)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public MenuInfo searchMenuByMenuCode(String menuCode) {
		Menu menu = menuReader.getMenuByMenuCode(menuCode);
		return new MenuInfo(menu);
	}

	@Override
	@Transactional(readOnly = true)
	public WiniPageInfo<MenuInfo> getMenuPage(Integer page, Integer pageSize, String searchType, String searchKeyword) {
		Page<MenuInfo> menuPage = menuReader.getMenuPage(page, pageSize, searchType, searchKeyword);
		
		return new WiniPageInfo<>(menuPage);
	}

	@Override
	@Transactional(readOnly = true)
	public MenuInfo getMenuById(UUID menuId) {
		Menu menu = menuReader.getMenuById(menuId);
		return new MenuInfo(menu);
	}
	
	@Override
	public void modifyMenuOrder(List<MenuCommand.OrderModifyRequestCommand> commandList) {
		if (commandList == null || commandList.size() == 0) {
			return;
		}

		// 번호 부여
		int sortSeq = 1;

		if (commandList.get(0).getParentMenuId() != null) {
			Menu topParentMenu = menuReader.getMenuById(commandList.get(0).getParentMenuId());
			
			sortSeq = topParentMenu.getSortSeq();
		}		
		
		// 메뉴 순서 변경
		for (MenuCommand.OrderModifyRequestCommand command : commandList) {
			menuStore.updateParentAndSortSeq(command.getId(), command.getParentMenuId(), sortSeq);
			
			// 가장 
			sortSeq++;
		}
		
		menuStore.flush();
		
		Map<UUID, Set<UUID>> childrenMap = new HashMap<>();
		
		menuReader.getAllMenu().forEach(menu -> {
			if (menu.getParentMenu() != null) {
				Set<UUID> children = childrenMap.get(menu.getParentMenu().getId());
				
				if (children == null) {
					children = new HashSet<>();
					childrenMap.put(menu.getParentMenu().getId(), children);
				}
				
				children.add(menu.getId());
			}
		});
		
		// 순환 참조 확인
		UUID circularReferencedNodeId = CircularReferenceDetector.findFirstCyclicNode(childrenMap);
		if (circularReferencedNodeId != null) {
			log.error("A circular reference was detected. (menuId : {})", circularReferencedNodeId);
			throw new IllegalStatusException("A circular reference was detected.");
		}		
	}

	@Override
	public boolean existsMenuByProgramId(UUID programId) {
		return menuReader.existsMenuByProgramId(programId);
	}

	@Override
	public void unlinkDeletedMenuWithProgramId(UUID programId) {
		menuStore.unlinkDeletedMenuWithProgramId(programId);
	}
}