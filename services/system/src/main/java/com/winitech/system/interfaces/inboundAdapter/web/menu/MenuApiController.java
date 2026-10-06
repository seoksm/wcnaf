package com.winitech.system.interfaces.inboundAdapter.web.menu;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.response.CommonResponse;
import com.winitech.common.response.ErrorCode;
import com.winitech.system.application.menu.MenuFacade;
import com.winitech.system.domain.menu.MenuCommand;
import com.winitech.system.domain.menu.MenuInfo;
import com.winitech.system.domain.menu.MenuService;
import com.winitech.system.interfaces.inboundAdapter.spec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.interfaces.inboundAdapter.web.menu
 * └ MenuApiController.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-23 17:23
 **/
@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
public class MenuApiController implements MenuApi {
	private final MenuFacade menuFacade;
	
	/**
	 * 전체 메뉴 조회
	 * @return
	 */
	@Override
	public CommonResponse<List<MenuResponseDto>> searchAllMenu() {
		List<MenuInfo> allMenu = menuFacade.getAllMenu();

		allMenu.sort(Comparator.comparing(MenuInfo::getMenuCode, Comparator.nullsFirst(Comparator.naturalOrder()))
				.thenComparing(MenuInfo::getMenuMapping, Comparator.nullsFirst(Comparator.naturalOrder()))
				.thenComparing(MenuInfo::getId));
		
		List<MenuResponseDto> response = allMenu
				.stream()
				.map(MenuDtoMapper.INSTANCE::toMenuResponseDto)
				.collect(Collectors.toList());

		return CommonResponse.success(response);
	}

	/**
	 * 메뉴 트리 조회
	 * @return
	 */
	@Override
	public CommonResponse<List<MenuTreeResponseDto>> searchMenuTree() {
		List<MenuInfo.MenuTreeInfo> allMenu = menuFacade.getAllMenuTree();
		
		List<MenuTreeResponseDto> response = allMenu
				.stream()
				.map(MenuDtoMapper.INSTANCE::toMenuTreeResponseDto)
				.collect(Collectors.toList());

		return CommonResponse.success(response);
	}

	/**
	 * 상세 메뉴 조회
	 * @param menuId
	 * @return
	 */
	@Override
	public CommonResponse<MenuResponseDto> searchMenu(UUID menuId) {
		MenuInfo menuInfo = menuFacade.getMenuById(menuId);
		
		MenuResponseDto response = MenuDtoMapper.INSTANCE.toMenuResponseDto(menuInfo);
		
		return CommonResponse.success(response);
	}

	/**
	 * 메뉴 등록
	 * @param menuRegisterRequestDto
	 * @return
	 */
	@Override
	public CommonResponse<MenuStoreResponseDto> registerMenu(MenuRegisterRequestDto menuRegisterRequestDto) {
		MenuCommand.RegisterRequestCommand command = MenuDtoMapper.INSTANCE.toRegisterRequestCommand(menuRegisterRequestDto);

		MenuInfo menuInfo = menuFacade.registerMenu(command);
		
		MenuStoreResponseDto response = MenuDtoMapper.INSTANCE.toMenuStoreResponseDto(menuInfo);
		
		return CommonResponse.success(response);
	}

	/**
	 * 메뉴 수정
	 * @param menuId
	 * @param menuRegisterRequestDto
	 * @return
	 */
	@Override
	public CommonResponse<MenuStoreResponseDto> modifyMenu(UUID menuId, MenuRegisterRequestDto menuRegisterRequestDto) {
		MenuCommand.ModifyRequestCommand command = MenuDtoMapper.INSTANCE.toModifyRequestCommand(menuRegisterRequestDto);

		MenuInfo menuInfo = menuFacade.modifyMenu(menuId, command);
		
		MenuStoreResponseDto response = MenuDtoMapper.INSTANCE.toMenuStoreResponseDto(menuInfo);
		
		return CommonResponse.success(response);
	}

	/**
	 * 메뉴 순서 변경
	 * @param menuOrderModifyRequestDto  (optional)
	 * @return
	 */
	@Override
	public CommonResponse<String> modifyMenuOrder(List<MenuOrderModifyRequestDto> menuOrderModifyRequestDto) {
		List<MenuCommand.OrderModifyRequestCommand> command = menuOrderModifyRequestDto
				.stream()
				.map(MenuDtoMapper.INSTANCE::toOrderModifyRequestCommand)
				.collect(Collectors.toList());
		
		menuFacade.modifyMenuOrder(command);
		
		return CommonResponse.success("OK");
	}

	@Override
	public CommonResponse<String> removeMenu(UUID menuId, String confirmYn) {
		if (!"Y".equals(confirmYn)) {
			return CommonResponse.fail(ErrorCode.COMMON_SYSTEM_ERROR, "Confirmation of deletion is required.", HttpStatus.OK.value());
		}
		
		menuFacade.removeMenu(menuId);
		
		return CommonResponse.success("OK");
	}
}
