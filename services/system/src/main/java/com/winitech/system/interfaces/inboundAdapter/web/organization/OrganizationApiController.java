package com.winitech.system.interfaces.inboundAdapter.web.organization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.bean.LoginUserContext;
import com.winitech.system.application.organization.OrganizationFacade;
import com.winitech.system.application.user.UserFacade;
import com.winitech.system.application.userOrganization.UserOrganizationFacade;
import com.winitech.common.response.CommonResponse;
import com.winitech.system.domain.organization.OrganizationInfo;
import com.winitech.system.domain.user.UserInfo;
import com.winitech.system.domain.userOrganization.UserOrganizationCommand;
import com.winitech.system.domain.userOrganization.UserOrganizationInfo;
import com.winitech.system.interfaces.inboundAdapter.web.userOrganization.UserOrganizationDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * com.winitech.system.interfaces.inboundAdapter.web.organization
 * └ OrganizationApiController.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/07/31
 **/
@Api(tags = "조직 서비스 API")
@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@ForceDefaultTenant
@RequestMapping("/api/v1/system")
public class OrganizationApiController {
	private final OrganizationFacade organizationFacade;
	private final UserOrganizationFacade userOrganizationFacade;
	private final UserFacade userFacade;

	private final LoginUserContext loginUserContext;

	// Organization API
	@ApiOperation(value = "조직 등록", notes = "조직을 등록합니다.")
	@PostMapping("/organization")
	public CommonResponse<OrganizationDto.OrganizationResponse> registerOrganization(@RequestBody @Valid OrganizationDto.OrganizationRequest request) {
		OrganizationInfo organizationInfo = organizationFacade.registerOrganization(request.toCommand());
		var response = new OrganizationDto.OrganizationResponse(organizationInfo);
		return CommonResponse.success(response);
	}
	
	@ApiOperation(value = "조직 수정", notes = "조직을 수정합니다.")
	@RequestMapping(value = "/organization/{id}", method = {RequestMethod.PATCH, RequestMethod.PUT})
	public CommonResponse<OrganizationDto.OrganizationResponse> modifyOrganization(@Valid @PathVariable String id, @RequestBody @Valid OrganizationDto.OrganizationRequest request) {
		OrganizationInfo organizationInfo = organizationFacade.modifyOrganization(UUID.fromString(id), request.toCommand());
		var response = new OrganizationDto.OrganizationResponse(organizationInfo);
		return CommonResponse.success(response);
	}

	@ApiOperation(value = "조직 삭제", notes = "조직을 삭제합니다. ※ 실제삭제는 하지 않고 systemStatus만 변경합니다.")
	@DeleteMapping(value = "/organization/{id}")
	public CommonResponse<String> deleteOrganization(@Valid @PathVariable String id) {
		organizationFacade.removeOrganization(UUID.fromString(id));
		return CommonResponse.success("OK");
	}
	
	@ApiOperation(value = "조직 조회", notes = "조직을 조회합니다.")
	@GetMapping(value = "/organization/{id}")
	public CommonResponse<OrganizationDto.OrganizationResponse> getOrganization(@Valid @PathVariable String id) {
		OrganizationInfo organizationInfo = organizationFacade.searchOrganizationById(UUID.fromString(id));
		var response = new OrganizationDto.OrganizationResponse(organizationInfo);
		return CommonResponse.success(response);
	}
	
	@ApiOperation(value = "조직 목록 조회", notes = "조직 목록을 조회합니다.")
	@GetMapping("/organization")
	public CommonResponse<List<OrganizationDto.OrganizationResponse>> getOrganizationList() {
		List<OrganizationInfo> allOrganization = organizationFacade.getAllOrganization();
		List<OrganizationDto.OrganizationResponse> response = allOrganization.stream()
				.map(OrganizationDto.OrganizationResponse::new)
				.sorted(Comparator.comparing(OrganizationDto.OrganizationResponse::getOrganizationCode).thenComparing(OrganizationDto.OrganizationResponse::getOrganizationName))
				.collect(Collectors.toList());
		return CommonResponse.success(response);
	}

	@ApiOperation(value = "조직 사용자 등록", notes = "조직에 사용자를 등록합니다.")
	@PostMapping("/organization/{organizationId}/user/{userId}")
	public CommonResponse<UserOrganizationDto.UserOrganizationResponse> registerOrganizationUser(@Valid @PathVariable String organizationId, @Valid @PathVariable String userId, @Valid @RequestBody UserOrganizationDto.RegisterUserOrganizationRequest request) throws JsonProcessingException {
		UserOrganizationCommand command = UserOrganizationCommand
				.builder()
				.userId(UUID.fromString(userId))
				.organizationId(UUID.fromString(organizationId))
				.userGroupCode(request.getUserGroupCode())
				.build();

		UserOrganizationInfo userOrganizationInfo = userOrganizationFacade.registerUserOrganization(command);

		var response = new UserOrganizationDto.UserOrganizationResponse(userOrganizationInfo);
		return CommonResponse.success(response);
	}

	@ApiOperation(value = "조직 사용자 수정", notes = "조직의 사용자를 수정합니다.")
	@RequestMapping(value = "/organization/{organizationId}/user/{userId}", method = {RequestMethod.PATCH, RequestMethod.PUT})
	public CommonResponse<UserOrganizationDto.UserOrganizationResponse> modifyOrganizationUser(@Valid @PathVariable String organizationId, @Valid @PathVariable String userId, @Valid @RequestBody UserOrganizationDto.RegisterUserOrganizationRequest request) {
		UserOrganizationInfo userOrganizationInfo = userOrganizationFacade.modifyUserOrganization(UUID.fromString(userId), UUID.fromString(organizationId), request.toCommand());
		
		var response = new UserOrganizationDto.UserOrganizationResponse(userOrganizationInfo);
		return CommonResponse.success(response);
	}

	@ApiOperation(value = "조직 사용자 삭제", notes = "조직에서 사용자를 삭제합니다.")
	@DeleteMapping("/organization/{organizationId}/user/{userId}")
	public CommonResponse<String> deleteOrganizationUser(@Valid @PathVariable String organizationId, @Valid @PathVariable String userId) throws JsonProcessingException {
		userOrganizationFacade.removeUserOrganization(UUID.fromString(userId), UUID.fromString(organizationId));
		return CommonResponse.success("OK");
	}

	@ApiOperation(value = "조직 사용자 목록", notes = "조직의 사용자 목록을 조회합니다.")
	@GetMapping("/organization/{organizationId}/user/")
	public CommonResponse<List<UserOrganizationDto.UserOrganizationWithUserInfoResponse>> getOrganizationUserList(@Valid @PathVariable String organizationId) {
		List<UserOrganizationInfo> allUserByOrganization = userOrganizationFacade.getUserOrganizationByOrganizationId(UUID.fromString(organizationId));
		
		List<UUID> userIdList = allUserByOrganization
				.stream()
				.map(UserOrganizationInfo::getUserId)
				.collect(Collectors.toList());

		Map<String, UserInfo> userInfoMap;
		
		if (userIdList.size() > 0) {
			List<UserInfo> userInfoList = userFacade.searchUserInfoListById(userIdList);
			userInfoMap = userInfoList.stream().collect(Collectors.toMap(UserInfo::getId, Function.identity()));
		} else {
			userInfoMap = new HashMap<>();
		}
		
		List<UserOrganizationDto.UserOrganizationWithUserInfoResponse> response = allUserByOrganization
				.stream()
				.map(it -> {
					UserInfo userInfo = null;
					if (it.getUserId() != null) {
						userInfo = userInfoMap.get(it.getUserId().toString());
					}
					return new UserOrganizationDto.UserOrganizationWithUserInfoResponse(it, userInfo);
				})
				.collect(Collectors.toList());
		return CommonResponse.success(response);
	}

	@ApiOperation(value = "조직 사용자 정보 조회", notes = "조직의 사용자 정보를 조회합니다.")
	@GetMapping("/organization/{organizationId}/user/{userId}")
	public CommonResponse<UserOrganizationDto.UserOrganizationWithUserInfoResponse> getOrganizationUserInfo(@Valid @PathVariable String organizationId, @Valid @PathVariable String userId) {
		UUID userUuid = UUID.fromString(userId);
		UserOrganizationInfo userOrganizationInfo = userOrganizationFacade.getUserOrganization(userUuid, UUID.fromString(organizationId));

		UserInfo userInfo = null;
		
		if (userOrganizationInfo != null && userOrganizationInfo.getUserId() != null) {
			userInfo = userFacade.searchUserInfo(userUuid);
		}

		UserOrganizationDto.UserOrganizationWithUserInfoResponse response = new UserOrganizationDto.UserOrganizationWithUserInfoResponse(userOrganizationInfo, userInfo);
		return CommonResponse.success(response);
	}
}
