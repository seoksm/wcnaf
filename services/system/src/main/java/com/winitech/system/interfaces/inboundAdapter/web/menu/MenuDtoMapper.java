package com.winitech.system.interfaces.inboundAdapter.web.menu;

import com.winitech.system.domain.menu.MenuCommand;
import com.winitech.system.domain.menu.MenuInfo;
import com.winitech.system.interfaces.inboundAdapter.spec.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * <pre>
 * com.winitech.system.interfaces.inboundAdapter.web.menu
 * └ MenuDtoMapper.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-23 17:24
 **/
@Mapper
public interface MenuDtoMapper {
	MenuDtoMapper INSTANCE = Mappers.getMapper(MenuDtoMapper.class);

	@Mapping(target = "chk", ignore = true)
	@Mapping(target = "name", source = "menuName")
	@Mapping(target = "children", source = "childrenMenu")
	MenuTreeResponseDto toMenuTreeResponseDto(MenuInfo.MenuTreeInfo menuInfo);

	@Mapping(target = "menuId", source = "id")
	MenuResponseDto toMenuResponseDto(MenuInfo menuInfo);

	MenuCommand.RegisterRequestCommand toRegisterRequestCommand(MenuRegisterRequestDto menuRegisterRequestDto);

	MenuCommand.ModifyRequestCommand toModifyRequestCommand(MenuRegisterRequestDto menuModifyRequestDto);

	@Mapping(target = "menuId", source = "id")
	MenuStoreResponseDto toMenuStoreResponseDto(MenuInfo menuInfo);

	@Mapping(target = "id", source = "menuId")
	MenuCommand.OrderModifyRequestCommand toOrderModifyRequestCommand(MenuOrderModifyRequestDto menuOrderModifyRequestDto);
}
