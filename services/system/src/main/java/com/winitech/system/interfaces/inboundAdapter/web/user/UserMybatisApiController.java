package com.winitech.system.interfaces.inboundAdapter.web.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.domain.common.CommonPublicKeyInfo;
import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.exception.UnauthenticatedException;
import com.winitech.common.exception.UnauthorizedException;
import com.winitech.common.infrastructure.common.CommonSqlSessionDao;
import com.winitech.common.library.WiniCom;
import com.winitech.common.library.WiniSecurity;
import com.winitech.common.response.CommonResponse;
import com.winitech.common.response.ErrorCode;
import com.winitech.system.application.loginLog.LoginLogFacade;
import com.winitech.system.application.user.UserFacade;
import com.winitech.system.application.userOrganization.UserOrganizationFacade;
import com.winitech.system.application.userSession.UserSessionFacade;
import com.winitech.system.domain.organization.OrganizationInfo;
import com.winitech.system.domain.user.*;
import com.winitech.system.domain.userSession.UserSession;
import com.winitech.system.domain.userSession.UserSessionInfo;
import com.winitech.system.interfaces.inboundAdapter.web.organization.OrganizationDto;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.interfaces.inboundAdapter.web.user
 * └ UserMybatisApiController.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-06-17 16:19
 **/
@Api(tags = "사용자 서비스 (Mybatis 버전) API")
@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@ForceDefaultTenant
@RequestMapping("/api/v1/system/user-mybatis")
public class UserMybatisApiController {
	private final UserMybatisService userMybatisService;
	private final UserOrganizationFacade userOrganizationFacade;
	private final UserSessionFacade userSessionFacade;
	private final LoginUserContext loginUserContext;
	private final LoginLogFacade loginLogFacade;

	// IP 보안 강제 여부
	@Value("${winitech.security.login.force-ip-security:false}")
	private Boolean forceIpSecurity;

	@Value("${winitech.security.jwt.refresh-timeout-in-seconds:0}")
	private Long accessTokenRefreshTimeout;

	@ApiOperation(value = "전체 사용자 조회 - UserMybatisService 사용")
	@GetMapping(value = "/all", params = "joinStatus")
	public CommonResponse searchAllJoinedUser(
			@RequestParam @ApiParam(value = "joinStatus", required = true, name = "joinStatus", type = "string", defaultValue = "ACCEPTED")  
			User.JoinStatus joinStatus
	) {
		//관리자 권한의 경우에만 전체사용자를 조회할 수 있도록 수정 필요
		List<UserDto.UserResponse> response = new ArrayList<UserDto.UserResponse>();
		List<UserInfo> userInfoList = userMybatisService.searchAllUserInfo(joinStatus);

		response = userInfoList.stream()
				.map(UserDto.UserResponse::new).collect(Collectors.toList());
		return CommonResponse.success(response);
	}
	
	@ApiOperation(value = "전체 사용자 조회 - 공통 DAO 인 CommonSqlSessionDao 사용")
	@GetMapping(value = "/allCommon", params = "joinStatus")
	public CommonResponse searchAllJoinedUserCommon(
			@RequestParam @ApiParam(value = "joinStatus", required = true, name = "joinStatus", type = "string", defaultValue = "ACCEPTED") User.JoinStatus joinStatus
	) {
		//관리자 권한의 경우에만 전체사용자를 조회할 수 있도록 수정 필요
		List<UserDto.UserResponse> response = new ArrayList<UserDto.UserResponse>();

		List<User> userInfoList = userMybatisService.searchAllUserInfoByCommonDao(joinStatus);
		
		response = userInfoList.stream()
				.map(user -> new UserDto.UserResponse(new UserInfo(user))).collect(Collectors.toList());
		return CommonResponse.success(response);
	}
	
	@ApiOperation(value = "전체 사용자 조회 - 공통 DAO 인 CommonSqlSessionDao, EgovMap 사용")
	@GetMapping(value = "/allCommonEgovMap", params = "joinStatus")
	public CommonResponse searchAllJoinedUserCommonEgovMap(
			@RequestParam @ApiParam(value = "joinStatus", required = true, name = "joinStatus", type = "string", defaultValue = "ACCEPTED") User.JoinStatus joinStatus
	) {
		EgovMap params = new EgovMap();
		params.put("status", User.Status.ENABLE);
		params.put("joinStatus", joinStatus);

		List<EgovMap> response = userMybatisService.searchAllUserInfoByCommonDaoEgovMap(params);
	
		return CommonResponse.success(response);
	}

	@ApiParam(value = "사용자 등록", required = true, name = "request", type = "object")
	@PostMapping
	public CommonResponse registerUser(@RequestBody @Valid UserDto.UserRequest request) throws UnirestException, NoSuchAlgorithmException, KeyStoreException, JsonProcessingException, KeyManagementException {
		UserCommand command = request.toCommand();
		UserInfo userInfo = userMybatisService.registerUser(command);
		var response = new UserDto.UserResponse(userInfo);
		return  CommonResponse.success(response);
	}

	//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "id", value = "사용자 ID", required = true, dataType = "Long", paramType = "path"),
//            @ApiImplicitParam(name = "request", value = "수정할 필드", required = true, dataType = "object", paramType = "formData")
//    })
	@RequestMapping(value = "{id}", method = {RequestMethod.PATCH, RequestMethod.PUT})
	public CommonResponse modifyUser(@Valid @PathVariable String id, @RequestBody UserDto.UserModifyRequest request) throws UnirestException, NoSuchAlgorithmException, KeyStoreException, JsonProcessingException, KeyManagementException {
		UserCommand.UserModifyCommand command = request.toCommand();
		UserInfo userInfo = userMybatisService.modifyUser(UUID.fromString(id), command);
		var response = new UserDto.UserResponse(userInfo);
		return CommonResponse.success(response);
	}

	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "사용자 ID", required = true, dataType = "Long", paramType = "path", example = "0"),
	})
	@GetMapping("/{id}")
	public CommonResponse searchUser(@Valid @PathVariable String id) {
		UserInfo userInfo = userMybatisService.searchUserInfo(UUID.fromString(id));
		var response = new UserDto.UserResponse(userInfo);
		return CommonResponse.success(response);
	}

	@GetMapping
	public CommonResponse searchAllUser() {
		//관리자 권한의 경우에만 전체사용자를 조회할 수 있도록 수정 필요
		List<UserInfo> userInfoList = userMybatisService.searchAllUserInfo();
		List<UserDto.UserResponse> response = userInfoList.stream()
				.map(UserDto.UserResponse::new).collect(Collectors.toList());
		return CommonResponse.success(response);
	}
	
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "사용자 ID", required = true, dataType = "UUID", paramType = "path")
	})
	@DeleteMapping("/{id}")
	public CommonResponse removeUser(@Valid @PathVariable String id) throws UnirestException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, JsonProcessingException {
		userMybatisService.removeUser(UUID.fromString(id));
		return CommonResponse.success("OK");
	}

	@PostMapping("/login")
	public CommonResponse login(HttpServletRequest req, HttpServletResponse res, @RequestBody UserDto.LoginRequest request) throws UnirestException, NoSuchAlgorithmException, KeyStoreException, JsonProcessingException, KeyManagementException {
		UserSession.IpSecurityStatus ipSecurityStatus;

		if (forceIpSecurity) {
			// 서버 설정에 IP 보안 강제이면 무조건 활성화.
			// 강제처리하는 것을 실제 체크하는 부분에서 처리하지 않고, 사용자의 선택을 고정하는 이유는
			// ApiGateway라는 별도의 서비스에서 처리할때 UserSession 정보 외의 다른 정보 공유가 필요 없도록 하기 위함 
			ipSecurityStatus = UserSession.IpSecurityStatus.ENABLE;
		} else if ("ENABLE".equals(request.getIpSecurityStatus())) {
			ipSecurityStatus = UserSession.IpSecurityStatus.ENABLE;
		} else {
			ipSecurityStatus = UserSession.IpSecurityStatus.DISABLE;
		}

		String clientIp = WiniCom.getClientIp(req);

		loginLogFacade.checkUsernameAndIpIsLocked(request.getUsername(), clientIp);

		String sessionEncryptKey = null;

		UserCommand.LoginCommand command = UserCommand.LoginCommand
				.builder()
				.username(request.getUsername())
				.password(request.getPassword())
				.loginIp(clientIp)
				.ipSecurityStatus(ipSecurityStatus)
				.sessionEncryptKey(sessionEncryptKey)
				.organizationId(request.getOrganizationId())
				.build();

		var result = userMybatisService.login(command);

		if (result == null) {
			// 로그인 실패
			loginLogFacade.addLoginFailLog(request.getUsername(), clientIp);

			throw new IllegalStatusException("Invalid username or password");
		} else {
			// 로그인 성공
			loginLogFacade.addLoginSuccessLog(request.getUsername(), clientIp, result.getUserSessionId());
		}

		// refreshToken을 cookie로 전달
		userSessionFacade.setRefreshTokenCookie(req, res, result.getRefreshToken(), result.getRefreshTokenExpiresIn());

		// 보안상 refreshToken은 응답에 포함하지 않고 cookie로만 전달
		result.setRefreshToken(null);
		result.setRefreshTokenExpiresIn(null);
		result.setUserSessionId(null);

		return CommonResponse.success(result);
	}

	/**
	 * 현재 사용자를 로그아웃합니다. 
	 * Authorization Token이 있는 경우에만 로그아웃 처리를 수행합니다.
	 */
	@PostMapping("/logout")
	public CommonResponse logout(HttpServletRequest req, HttpServletResponse res) throws UnirestException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		LoginUserContext userContext = null;

		try {
			userContext = WiniSecurity.parseLoginUserContext(req);
		} catch (UnauthenticatedException | UnauthorizedException _ignored) {
			// ignored
		}

		if (userContext != null && userContext.getUserSessionId() != null) {
			// Authorization Token이 있는 경우만 로그아웃 처리

			userMybatisService.logout(userContext.getUserSessionId());
		}

		// refresh token 쿠키 삭제
		clearRefreshToken(res);

		return CommonResponse.success("OK");
	}

	private static void clearRefreshToken(HttpServletResponse res) {
		// refreshToken 삭제. 삭제는 https 사용여부에 관계없이 둘 다 삭제 시도

		// http용 refreshToken 삭제
		Cookie httpToken = new Cookie("refreshToken", "");
		httpToken.setPath("/api/v1/system/user/refreshToken");
		httpToken.setHttpOnly(true);

		httpToken.setMaxAge(-1);
		res.addCookie(httpToken);

		// https용 refreshToken 삭제
		Cookie httpsToken = new Cookie("refreshToken", "");
		httpsToken.setPath("/api/v1/system/user/refreshToken");
		httpsToken.setHttpOnly(true);

		httpsToken.setSecure(true);

		httpsToken.setMaxAge(-1);
		res.addCookie(httpsToken);
	}

	@PatchMapping("/{id}/resetPassword")
	public CommonResponse resetUserPassword(@Valid @PathVariable String id, @RequestBody UserDto.ResetPasswordRequest request, HttpServletRequest req, HttpServletRequest res) throws UnirestException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		String clientIp = WiniCom.getClientIp(req);

		userMybatisService.resetUserPassword(UUID.fromString(id), request.getNewPassword(), clientIp);
		return CommonResponse.success("OK");
	}

	@PatchMapping("/changeUserPassword")
	public CommonResponse changeUserPassword(@RequestBody UserDto.ChangeUserPasswordRequest request, HttpServletRequest req, HttpServletRequest res) throws UnirestException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		String clientIp = WiniCom.getClientIp(req);

		userMybatisService.changeUserPassword(loginUserContext.getUserId(), request.getOldPassword(), request.getNewPassword(), clientIp);
		return CommonResponse.success("OK");
	}

	@PatchMapping("/unlockLogin")
	public CommonResponse unlockLogin(@RequestBody UserDto.UnlockLoginRequest request) throws UnirestException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		loginLogFacade.unlockLogin(request.getUsername());
		return CommonResponse.success("OK");
	}

	@PatchMapping("/{id}/acceptJoin")
	public CommonResponse acceptJoin(@Valid @PathVariable String id) {
		userMybatisService.acceptJoin(UUID.fromString(id));
		return CommonResponse.success("OK");
	}

	@ApiOperation(value = "사용자 조직 목록", notes = "사용자의 조직 목록을 조회합니다.")
	@GetMapping("/{id}/organization/")
	public CommonResponse<List<OrganizationDto.OrganizationResponse>> getUserOrganizationList(@Valid @PathVariable String id) {
		List<OrganizationInfo> allOrganization = userOrganizationFacade.getAllOrganizationByUser(UUID.fromString(id));
		List<OrganizationDto.OrganizationResponse> response = allOrganization.stream()
				.map(OrganizationDto.OrganizationResponse::new).collect(Collectors.toList());
		return CommonResponse.success(response);
	}
}
