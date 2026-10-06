package com.winitech.system.interfaces.inboundAdapter.web.menuPermission;

import com.winitech.system.domain.menuPermission.MenuPermissionCommand;
import com.winitech.system.domain.menuPermission.MenuPermissionInfo;
import com.winitech.system.interfaces.inboundAdapter.spec.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.interfaces.inboundAdapter.web.menuPermission
 * └ MenuPermissionDtoMapper.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-18 11:48
 **/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MenuPermissionDtoMapper {
	MenuPermissionDtoMapper INSTANCE = Mappers.getMapper(MenuPermissionDtoMapper.class);

	@Mapping(target = "menuPermissionId", source = "id")
	@Mapping(target = "id", source = "menuId")
	@Mapping(target = "menuId", source = "menuId")
	@Mapping(target = "name", source = "menuName")
	MenuPermissionTreeResponseDto toMenuPermissionTreeResponseDto(MenuPermissionInfo.MenuPermissionTreeInfo menuTreeInfo);

	@Mapping(target = "id", source = "menuId")
	@Mapping(target = "menuId", source = "menuId")
	@Mapping(target = "name", source = "menuName")
	//@Mapping(target = "menuPermissionId", ignore = true)
	//@Mapping(target = "menuId", ignore = true)
	MenuPermissionTreeResponseDto toMenuPermissionTreeResponseDto(MenuPermissionInfo.UserMenuPermissionTreeInfo menuTreeInfo);

	@Mapping(target = "menuPermissionId", source = "id")
	MenuPermissionResponseDto toMenuPermissionResponseDto(MenuPermissionInfo menuInfo);

	MenuPermissionCommand.RegisterRequestCommand toRegisterRequestCommand(UUID authorizationGroupId, MenuPermissionRegisterRequestDto menuPermissionRegisterRequestDto);

	MenuPermissionCommand.ModifyRequestCommand toModifyRequestCommand(UUID authorizationGroupId, MenuPermissionModifyRequestDto menuPermissionModifyRequestDto);

	@Mapping(target = "menuPermissionId", source = "id")
	MenuPermissionStoreResponseDto toMenuPermissionStoreResponseDto(MenuPermissionInfo menuInfo);

	MenuPermissionDetailResponseDto toMenuPermissionDetailResponseDto(MenuPermissionInfo.UserMenuPermissionTreeInfo userMenuPermissionTreeInfo);
}
