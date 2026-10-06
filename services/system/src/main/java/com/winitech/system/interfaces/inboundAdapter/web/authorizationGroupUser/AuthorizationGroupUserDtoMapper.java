package com.winitech.system.interfaces.inboundAdapter.web.authorizationGroupUser;

import com.winitech.system.domain.authorizationGroupUser.AuthorizationGroupUserCommand;
import com.winitech.system.domain.authorizationGroupUser.AuthorizationGroupUserInfo;
import com.winitech.system.interfaces.inboundAdapter.spec.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * <pre>
 * com.winitech.system.interfaces.inboundAdapter.web.authorizationGroupUser
 * └ AuthorizationGroupUserDtoMapper.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-20 11:00
 **/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorizationGroupUserDtoMapper {
	AuthorizationGroupUserDtoMapper INSTANCE = Mappers.getMapper(AuthorizationGroupUserDtoMapper.class);

	@Mapping(source = "id", target = "authorizationGroupUserId")
	AuthorizationGroupUserResponseDto toAuthorizationGroupUserResponseDto(AuthorizationGroupUserInfo authorizationGroupUserInfo);
	
	@Mapping(source = "id", target = "authorizationGroupUserId")
	AuthorizationGroupUserResponseDto toAuthorizationGroupUserResponseDto(AuthorizationGroupUserInfo.PageInfo authorizationGroupUserInfo);

	@Mapping(source = "id", target = "authorizationGroupUserId")
	AuthorizationGroupUserDetailResponseDto toAuthorizationGroupUserDetailResponseDto(AuthorizationGroupUserInfo.PageInfo authorizationGroupUserInfo);

	AuthorizationGroupUserCommand.BatchRegisterRequestCommand toBatchRegisterRequestCommand(AuthorizationGroupUserBatchRegisterRequestItemDto authorizationGroupUserRegisterRequestItemDto);

	@Mapping(source = "id", target = "authorizationGroupUserId")
	AuthorizationGroupUserStoreResponseDto toAuthorizationGroupUserStoreResponseDto(AuthorizationGroupUserInfo authorizationGroupUserInfo);

	AuthorizationGroupUserCommand.RegisterRequestCommand toRegisterRequestCommand(AuthorizationGroupUserRegisterRequestDto authorizationGroupUserRegisterRequestDto);
}
