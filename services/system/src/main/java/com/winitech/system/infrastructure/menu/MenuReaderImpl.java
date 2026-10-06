package com.winitech.system.infrastructure.menu;

import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.common.library.WiniCom;
import com.winitech.system.domain.menu.Menu;
import com.winitech.system.domain.menu.MenuInfo;
import com.winitech.system.domain.menu.MenuReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.infrastructure.menu
 * └ MenuReaderImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-24 09:49
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class MenuReaderImpl implements MenuReader {
	private final MenuRepository menuRepository;

	private final MenuQueryRepository menuQueryRepository;
	
	private final EntityManager em;

	@Override
	public Menu getMenuById(UUID menuId) {
		return menuRepository.findByIdAndSystemStatus(menuId, Menu.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public List<Menu> getMenuByIdList(List<UUID> menuIdList) {
		return menuRepository.findAllByIdAndSystemStatus(menuIdList, Menu.SystemStatus.ENABLE);
	}

	@Override
	public Menu getMenuByMenuCode(String menuCode) {
		return menuRepository.findByMenuCodeAndSystemStatus(menuCode, Menu.SystemStatus.ENABLE).orElseThrow(EntityNotFoundException::new);
	}

	@Override
	public List<Menu> getAllMenu() {
		return menuRepository.findAllBySystemStatus(Menu.SystemStatus.ENABLE);
	}

	@Override
	public List<Menu> getAllMenuTree() {
		return menuRepository.findAllBySystemStatus(
				Menu.SystemStatus.ENABLE,
				Sort.by(
						Sort.Order.asc("sortSeq"),
						Sort.Order.asc("menuCode"),
						Sort.Order.asc("menuMapping"),
						Sort.Order.asc("id")
				)
		);
	}

	@Override
	public Page<MenuInfo> getMenuPage(Integer page, Integer pageSize, String searchType, String searchKeyword) {
		return menuQueryRepository.findAllPage(searchType, searchKeyword, WiniCom.getPageRequest(page, pageSize));
	}

	@Override
	public boolean existMenuByMenuCodeAndExcludingSelf(String menuCode, UUID menuId) {
		return menuRepository.existsByMenuCodeAndIdNotAndSystemStatus(menuCode, menuId, Menu.SystemStatus.ENABLE);
	}
	
	@Override
	public boolean existsMenuByProgramId(UUID programId) {
		return menuRepository.existsByProgramIdAndSystemStatus(programId, Menu.SystemStatus.ENABLE);
	}
}
