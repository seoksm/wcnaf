package com.winitech.system.interfaces.inboundAdapter.web.authorizationGroupUser;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.library.commonType.WiniPageInfo;
import com.winitech.common.response.CommonResponse;
import com.winitech.system.application.authorizationGroupUser.AuthorizationGroupUserFacade;
import com.winitech.system.domain.authorizationGroupUser.AuthorizationGroupUserCommand;
import com.winitech.system.domain.authorizationGroupUser.AuthorizationGroupUserInfo;
import com.winitech.system.domain.programAction.ProgramActionCommand;
import com.winitech.system.domain.programAction.ProgramActionInfo;
import com.winitech.system.interfaces.inboundAdapter.spec.*;
import com.winitech.system.interfaces.inboundAdapter.web.programAction.ProgramActionDtoMapper;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.authorizationGroupUser
 * └ AuthorizationGroupUser.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-20 10:09
 **/
@RestController
@ForceDefaultTenant
@RequiredArgsConstructor
//@Validated
//@Api(tags = { "AuthorizationGroupUser 서비스 API" }, value = "AuthorizationGroupUser 서비스 API", description = "Api Controller")
//@RequestMapping("/api/v1/system")
public class AuthorizationGroupUserApiController implements AuthorizationGroupUserApi {
	private final AuthorizationGroupUserFacade authorizationGroupUserFacade;

	@Override
	public CommonResponse<AuthorizationGroupUserResponseDto> searchAuthorizationGroupUser(UUID authorizationGroupId, UUID userId) {
		AuthorizationGroupUserInfo authorizationGroupUserInfo = authorizationGroupUserFacade.searchAuthorizationGroupUser(authorizationGroupId, userId);

		AuthorizationGroupUserResponseDto response = AuthorizationGroupUserDtoMapper.INSTANCE.toAuthorizationGroupUserResponseDto(authorizationGroupUserInfo);

		return CommonResponse.success(response);
	}

	@Override
	public CommonResponse<List<AuthorizationGroupUserDetailResponseDto>> searchAllAuthorizationGroupUserAll(UUID authorizationGroupId, Integer page, Integer pageSize, String searchType, String searchKeyword, String status, String authorizationGroupUserStatus) {
		WiniPageInfo<AuthorizationGroupUserInfo.PageInfo> authorizationGroupUserPageInfo = authorizationGroupUserFacade.searchAuthorizationGroupUserPage(authorizationGroupId, status, authorizationGroupUserStatus, page, pageSize, searchType, searchKeyword);

		List<AuthorizationGroupUserDetailResponseDto> response = authorizationGroupUserPageInfo
				.stream()
				.map(AuthorizationGroupUserDtoMapper.INSTANCE::toAuthorizationGroupUserDetailResponseDto)
				.collect(Collectors.toList());

		return CommonResponse.success(response, authorizationGroupUserPageInfo);
	}	

	@Override
	public CommonResponse<List<AuthorizationGroupUserResponseDto>> searchAllAuthorizationGroupUser(UUID authorizationGroupId, Integer page, Integer pageSize, String searchType, String searchKeyword, String status) {
		WiniPageInfo<AuthorizationGroupUserInfo.PageInfo> authorizationGroupUserPageInfo = authorizationGroupUserFacade.searchAuthorizationGroupUserPage(authorizationGroupId, "ENABLE", "ENABLE", page, pageSize, searchType, searchKeyword);

		List<AuthorizationGroupUserResponseDto> response = authorizationGroupUserPageInfo
				.stream()
				.map(AuthorizationGroupUserDtoMapper.INSTANCE::toAuthorizationGroupUserResponseDto)
				.collect(Collectors.toList());

		return CommonResponse.success(response, authorizationGroupUserPageInfo);
	}

	@Override
	public CommonResponse<String> removeAuthorizationGroupUser(UUID authorizationGroupId, UUID userId) {
		authorizationGroupUserFacade.removeAuthorizationGroupUser(authorizationGroupId, userId);

		return CommonResponse.success("OK");
	}

	@Override
	public CommonResponse<AuthorizationGroupUserBatchStoreResponseDto> registerAuthorizationGroupUserBatch(UUID authorizationGroupId, AuthorizationGroupUserBatchRegisterRequestDto authorizationGroupUserBatchRegisterRequestDto) {
		List<AuthorizationGroupUserCommand.BatchRegisterRequestCommand> commands = authorizationGroupUserBatchRegisterRequestDto.getAuthorizationGroupUserList()
				.stream()
				.map(AuthorizationGroupUserDtoMapper.INSTANCE::toBatchRegisterRequestCommand)
				.collect(Collectors.toList());

		List<AuthorizationGroupUserInfo> authorizationGroupUserInfos = authorizationGroupUserFacade.registerAuthorizationGroupUserBatch(authorizationGroupId, commands);

		AuthorizationGroupUserBatchStoreResponseDto response = new AuthorizationGroupUserBatchStoreResponseDto();
		response.setAuthorizationGroupUserIdList(authorizationGroupUserInfos.stream()
				.map(AuthorizationGroupUserDtoMapper.INSTANCE::toAuthorizationGroupUserStoreResponseDto)
				.collect(Collectors.toList()));

		return CommonResponse.success(response);
	}

	@Override
	public CommonResponse<AuthorizationGroupUserStoreResponseDto> registerAuthorizationGroupUser(UUID authorizationGroupId, AuthorizationGroupUserRegisterRequestDto authorizationGroupUserRegisterRequestDto) {
		AuthorizationGroupUserCommand.RegisterRequestCommand command = AuthorizationGroupUserDtoMapper.INSTANCE.toRegisterRequestCommand(authorizationGroupUserRegisterRequestDto);

		AuthorizationGroupUserInfo authorizationGroupUserInfo = authorizationGroupUserFacade.registerAuthorizationGroupUser(authorizationGroupId, command);

		AuthorizationGroupUserStoreResponseDto response = AuthorizationGroupUserDtoMapper.INSTANCE.toAuthorizationGroupUserStoreResponseDto(authorizationGroupUserInfo);

		return CommonResponse.success(response);
	}

	@Override
	public CommonResponse<String> syncAllAuthorizationGroupUser() {
		authorizationGroupUserFacade.syncAllAuthorizationGroupUser();
		
		return CommonResponse.success("OK");
	}
}
