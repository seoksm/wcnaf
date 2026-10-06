package com.winitech.system.interfaces.inboundAdapter.web.menuPermission;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.response.CommonResponse;
import com.winitech.system.application.menuPermission.MenuPermissionFacade;
import com.winitech.system.domain.menuPermission.MenuPermission;
import com.winitech.system.domain.menuPermission.MenuPermissionCommand;
import com.winitech.system.domain.menuPermission.MenuPermissionInfo;
import com.winitech.system.interfaces.inboundAdapter.spec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Comparator;
import java.util.List;
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
@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
public class MenuPermissionApiController implements MenuPermissionApi {
	private final MenuPermissionFacade menuPermissionFacade;

	@Override
	public CommonResponse<List<MenuPermissionTreeResponseDto>> searchAllUserMenuPermission(UUID userId, UUID authorizationGroupId) {
		List<MenuPermissionInfo.UserMenuPermissionTreeInfo> userMenuPermissionList = menuPermissionFacade.getAllUserMenuPermissionTree(userId, authorizationGroupId);

		List<MenuPermissionTreeResponseDto> response = userMenuPermissionList
				.stream()
				.map(MenuPermissionDtoMapper.INSTANCE::toMenuPermissionTreeResponseDto)
				.collect(Collectors.toList());

		return CommonResponse.success(response);
	}

	@Override
	public CommonResponse<MenuPermissionDetailResponseDto> searchUserMenuPermission(UUID userId, UUID menuId, UUID authorizationGroupId) {
		List<MenuPermissionInfo.UserMenuPermissionTreeInfo> userMenuPermissionList = menuPermissionFacade.getUserMenuPermission(userId, menuId, authorizationGroupId);

		MenuPermissionDetailResponseDto response;
				
		if (userMenuPermissionList.size() > 0) {
			response = MenuPermissionDtoMapper.INSTANCE.toMenuPermissionDetailResponseDto(userMenuPermissionList.get(0));
		} else {
			response = new MenuPermissionDetailResponseDto().builder()
					.menuId(menuId)
					.menuName("No Menu")
					.selectStatus(MenuPermissionDetailResponseDto.SelectStatusEnum.NONE)
					.insertStatus(MenuPermissionDetailResponseDto.InsertStatusEnum.NONE)
					.updateStatus(MenuPermissionDetailResponseDto.UpdateStatusEnum.NONE)
					.deleteStatus(MenuPermissionDetailResponseDto.DeleteStatusEnum.NONE)
					.printStatus(MenuPermissionDetailResponseDto.PrintStatusEnum.NONE)
					.downStatus(MenuPermissionDetailResponseDto.DownStatusEnum.NONE)
					.manageStatus(MenuPermissionDetailResponseDto.ManageStatusEnum.NONE)
					.custom1Status(MenuPermissionDetailResponseDto.Custom1StatusEnum.NONE)
					.custom2Status(MenuPermissionDetailResponseDto.Custom2StatusEnum.NONE)
					.custom3Status(MenuPermissionDetailResponseDto.Custom3StatusEnum.NONE)
					.build();
		}

		return CommonResponse.success(response);
	}

	@Override
	public CommonResponse<List<MenuPermissionResponseDto>> searchAllMenuPermission(UUID authorizationGroupId) {
		List<MenuPermissionInfo> allMenuPermission = menuPermissionFacade.getAllMenuPermission(authorizationGroupId);

		allMenuPermission.sort(Comparator.comparing(MenuPermissionInfo::getId));

		List<MenuPermissionResponseDto> response = allMenuPermission
				.stream()
				.map(MenuPermissionDtoMapper.INSTANCE::toMenuPermissionResponseDto)
				.collect(Collectors.toList());

		return CommonResponse.success(response);
	}

	@Override
	public CommonResponse<MenuPermissionResponseDto> searchMenuPermission(UUID authorizationGroupId, UUID menuPermissionId) {
		MenuPermissionInfo menuPermissionInfo = menuPermissionFacade.searchMenuPermissionById(menuPermissionId);

		MenuPermissionResponseDto response = MenuPermissionDtoMapper.INSTANCE.toMenuPermissionResponseDto(menuPermissionInfo);

		return CommonResponse.success(response);
	}

	@Override
	public CommonResponse<List<MenuPermissionTreeResponseDto>> searchMenuPermissionTree(UUID authorizationGroupId) {
		List<MenuPermissionInfo.MenuPermissionTreeInfo> allMenuPermission = menuPermissionFacade.getAllMenuPermissionTree(authorizationGroupId);

		List<MenuPermissionTreeResponseDto> response = allMenuPermission
				.stream()
				.map(MenuPermissionDtoMapper.INSTANCE::toMenuPermissionTreeResponseDto)
				.collect(Collectors.toList());

		return CommonResponse.success(response);
	}

	@Override
	public CommonResponse<List<MenuPermissionTreeResponseDto>> searchMenuPermissionTreeList(UUID authorizationGroupId, String childrenMark, String padString, Integer padLength) {
		List<MenuPermissionInfo.MenuPermissionTreeInfo> allMenuPermission = menuPermissionFacade.getAllMenuPermissionTreeList(authorizationGroupId, childrenMark, padString, padLength);

		List<MenuPermissionTreeResponseDto> response = allMenuPermission
				.stream()
				.map(MenuPermissionDtoMapper.INSTANCE::toMenuPermissionTreeResponseDto)
				.collect(Collectors.toList());
		
		return CommonResponse.success(response);
	}

	@Override
	public CommonResponse<MenuPermissionStoreResponseDto> registerMenuPermission(UUID authorizationGroupId, MenuPermissionRegisterRequestDto menuPermissionRegisterRequestDto) {
		MenuPermissionCommand.RegisterRequestCommand command = MenuPermissionDtoMapper.INSTANCE.toRegisterRequestCommand(authorizationGroupId, menuPermissionRegisterRequestDto);

		MenuPermissionInfo menuPermissionInfo = menuPermissionFacade.registerMenuPermission(command);

		MenuPermissionStoreResponseDto response = MenuPermissionDtoMapper.INSTANCE.toMenuPermissionStoreResponseDto(menuPermissionInfo);

		return CommonResponse.success(response);
	}

	@Override
	public CommonResponse<MenuPermissionBatchStoreResponseDto> registerMenuPermissionBatch(UUID authorizationGroupId, MenuPermissionBatchRegisterRequestDto menuPermissionBatchRegisterRequestDto) {
		List<MenuPermissionCommand.RegisterRequestCommand> commands = menuPermissionBatchRegisterRequestDto.getPermissionList()
				.stream()
				.map(dto -> 
						MenuPermissionDtoMapper.INSTANCE.toRegisterRequestCommand(authorizationGroupId, dto)
				)
				.collect(Collectors.toList());

		List<MenuPermissionInfo> menuPermissionInfos = menuPermissionFacade.registerMenuPermissionBatch(authorizationGroupId, commands);

		MenuPermissionBatchStoreResponseDto response = new MenuPermissionBatchStoreResponseDto();
		response.setMenuPermissionIdList(menuPermissionInfos.stream()
				.map(MenuPermissionDtoMapper.INSTANCE::toMenuPermissionStoreResponseDto)
				.collect(Collectors.toList()));

		return CommonResponse.success(response);
	}

	@Override
	public CommonResponse<MenuPermissionStoreResponseDto> modifyMenuPermission(UUID authorizationGroupId, UUID menuPermissionId, MenuPermissionModifyRequestDto menuPermissionModifyRequestDto) {
		MenuPermissionCommand.ModifyRequestCommand command = MenuPermissionDtoMapper.INSTANCE.toModifyRequestCommand(authorizationGroupId, menuPermissionModifyRequestDto);

		MenuPermissionInfo menuPermissionInfo = menuPermissionFacade.modifyMenuPermission(menuPermissionId, command);

		MenuPermissionStoreResponseDto response = MenuPermissionDtoMapper.INSTANCE.toMenuPermissionStoreResponseDto(menuPermissionInfo);

		return CommonResponse.success(response);
	}

	@Override
	public CommonResponse<String> removeMenuPermission(UUID authorizationGroupId, UUID menuPermissionId) {
		menuPermissionFacade.removeMenuPermission(menuPermissionId);

		return CommonResponse.success("OK");
	}

	@Override
	public CommonResponse<String> syncAllAuthorizationGroupPermission() {
		menuPermissionFacade.syncAllAuthorizationGroupPermission();
		
		return CommonResponse.success("OK");
	}
}
//	/**
//	 * MenuPermission 페이지 조회
//	 * @return
//	 */
//	@ApiOperation(tags = { "MenuPermission 서비스 API" }, value = "MenuPermission 조회", nickname = "searchAllMenuPermission", notes = "MenuPermission를 조회합니다.", response = MenuPermissionDto.MenuPermissionResponse.class, responseContainer = "List")
//	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = MenuPermissionDto.MenuPermissionResponse.class, responseContainer = "List")})
//	@RequestMapping(method = RequestMethod.GET, value = "/menuPermission", produces = { "application/json" })
//	public CommonResponse<List<MenuPermissionDto.MenuPermissionResponse>> searchAllMenuPermission(
//			@ApiParam(value = "페이지 번호 (0부터 시작)") @Valid @RequestParam(value = "page", required = false) Integer page,
//			@ApiParam(value = "페이지 사이즈") @Valid @RequestParam(value = "pageSize", required = false) Integer pageSize,
//			@ApiParam(value = "검색 타입", allowableValues = "name,code") @Valid @RequestParam(value = "searchType", required = false) String searchType,
//			@ApiParam(value = "검색어") @Valid @RequestParam(value = "searchKeyword", required = false) String searchKeyword
//	) {
//		WiniPageInfo<MenuPermissionInfo> menuPermissionPageInfo = menuPermissionFacade.searchMenuPermissionPage(page, pageSize, searchType, searchKeyword);
//
//		List<MenuPermissionDto.MenuPermissionResponse> response = menuPermissionPageInfo
//				.stream()
//				.map(MenuPermissionDto.MenuPermissionResponse::new)
//				.collect(Collectors.toList());
//
//		return CommonResponse.success(response, menuPermissionPageInfo);
//	}