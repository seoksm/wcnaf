package com.winitech.system.interfaces.inboundAdapter.web.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.domain.common.CommonPublicKeyInfo;
import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.exception.UnauthenticatedException;
import com.winitech.common.exception.UnauthorizedException;
import com.winitech.common.library.WiniCom;
import com.winitech.common.library.WiniSecurity;
import com.winitech.common.response.ErrorCode;
import com.winitech.system.application.loginLog.LoginLogFacade;
import com.winitech.system.application.user.UserFacade;
import com.winitech.system.application.userOrganization.UserOrganizationFacade;
import com.winitech.system.application.userSession.UserSessionFacade;
import com.winitech.common.response.CommonResponse;
import com.winitech.system.domain.organization.OrganizationInfo;
import com.winitech.system.domain.user.User;
import com.winitech.system.domain.user.UserCommand;
import com.winitech.system.domain.user.UserInfo;
import com.winitech.system.domain.userSession.UserSession;
import com.winitech.system.domain.userSession.UserSessionInfo;
import com.winitech.system.interfaces.inboundAdapter.web.organization.OrganizationDto;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
* com.winitech.user.interfaces.user
* ㄴ UserApiController.java
* @author : 박준희 과장 (부설연구소)
* @since : 2021-12-15 오후 6:12
* @see : None
 **/
@Api(tags = "사용자 서비스 API")
@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@ForceDefaultTenant
@RequestMapping("/api/v1/system/user")
public class UserApiController {
    private final UserFacade userFacade;
    private final UserOrganizationFacade userOrganizationFacade;
    private final UserSessionFacade userSessionFacade;
    private final LoginUserContext loginUserContext;
    private final LoginLogFacade loginLogFacade;

    // IP 보안 강제 여부
    @Value("${winitech.security.login.force-ip-security:false}")
    private Boolean forceIpSecurity;

    @Value("${winitech.security.jwt.refresh-timeout-in-seconds:0}")
    private Long accessTokenRefreshTimeout;

    @Value("${spring.profiles.active}") // 값이 없을 경우 'default' 사용
    private String activeProfile;

    // Organization API
    @ApiParam(value = "사용자 등록", required = true, name = "request", type = "object")
    @PostMapping
    public CommonResponse registerUser(@RequestBody @Valid UserDto.UserRequest request) throws UnirestException, NoSuchAlgorithmException, KeyStoreException, JsonProcessingException, KeyManagementException {
        UserCommand command = request.toCommand();
        UserInfo userInfo = userFacade.registerUser(command);
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
        UserInfo userInfo = userFacade.modifyUser(UUID.fromString(id), command);
        var response = new UserDto.UserResponse(userInfo);
        return CommonResponse.success(response);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "사용자 ID", required = true, dataType = "Long", paramType = "path", example = "0"),
    })
    @GetMapping("/{id}")
    public CommonResponse searchUser(@Valid @PathVariable String id) {
        UserInfo userInfo = userFacade.searchUserInfo(UUID.fromString(id));
        var response = new UserDto.UserResponse(userInfo);
        return CommonResponse.success(response);
    }

    @GetMapping
    public CommonResponse searchAllUser() {
        //관리자 권한의 경우에만 전체사용자를 조회할 수 있도록 수정 필요
        List<UserInfo> userInfoList = userFacade.searchAllUserInfo();
        List<UserDto.UserResponse> response = userInfoList.stream()
                .map(UserDto.UserResponse::new).collect(Collectors.toList());
        return CommonResponse.success(response);
    }

    @GetMapping(params = "joinStatus")
    public CommonResponse searchAllJoinedUser(
            @RequestParam User.JoinStatus joinStatus
    ) {
        //관리자 권한의 경우에만 전체사용자를 조회할 수 있도록 수정 필요
        List<UserDto.UserResponse> response = new ArrayList<UserDto.UserResponse>();
        List<UserInfo> userInfoList = new ArrayList<UserInfo>();
        if(joinStatus.equals(User.JoinStatus.ACCEPTED)) {
            userInfoList = userFacade.searchAllJoinedUserInfo();
        } else {
            userInfoList = userFacade.searchAllUserInfo();
        }
        response = userInfoList.stream()
                .map(UserDto.UserResponse::new).collect(Collectors.toList());
        return CommonResponse.success(response);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "사용자 ID", required = true, dataType = "UUID", paramType = "path")
    })
    @DeleteMapping("/{id}")
    public CommonResponse removeUser(@Valid @PathVariable String id) throws UnirestException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, JsonProcessingException {
        userFacade.removeUser(UUID.fromString(id));
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
        
        var result = userFacade.login(command);
        
        if (result == null) {
            // 로그인 실패
            loginLogFacade.addLoginFailLog(request.getUsername(), clientIp);

            throw new IllegalStatusException("Invalid username or password");
        } else {
            // 로그인 성공
            loginLogFacade.addLoginSuccessLog(request.getUsername(), clientIp, result.getUserSessionId());
        }

        if (result.getRefreshToken() != null) {
            // refreshToken을 cookie로 전달
            userSessionFacade.setRefreshTokenCookie(req, res, result.getRefreshToken(), result.getRefreshTokenExpiresIn());
        }
                
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

            userFacade.logout(userContext.getUserSessionId());
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

    @GetMapping("/refreshToken")
    public CommonResponse<UserTokenDto> refreshToken(HttpServletRequest req, HttpServletResponse res) throws UnirestException, NoSuchAlgorithmException, KeyStoreException, JsonProcessingException, KeyManagementException {
        Cookie[] cookies = req.getCookies();


        if(activeProfile.equals("docker") || activeProfile.equals("local")){
            return CommonResponse.success(userFacade.devRefreshAccessToken(WiniSecurity.parseLoginUserContext(req)));
        }

        if (cookies == null) {
            return CommonResponse.fail(ErrorCode.COMMON_UNAUTHENTICATED, "RefreshToken does not exist", HttpStatus.OK.value());
        }

        String refreshToken = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
            }
        }
        /*var result = userFacade.refreshToken(refreshToken);
        
        Cookie token = new Cookie("refreshToken", "TEST");
        token.setPath("/api/v1/system/user/refreshToken");
        token.setHttpOnly(true);
        token.setMaxAge(60 * 60 * 24 * 7);       
        res.addCookie(token);*/
        
        UserSessionInfo userSessionInfo = userSessionFacade.getUserSessionByRefreshToken(refreshToken);
        
        if (userSessionInfo == null) {
            return CommonResponse.fail(ErrorCode.COMMON_UNAUTHENTICATED, "Valid refreshToken does not exist", HttpStatus.OK.value());
        }
        
        if (userSessionInfo.getLoginStatus() != UserSession.LoginStatus.LOGIN) {
            return CommonResponse.fail(ErrorCode.COMMON_UNAUTHENTICATED, "The session has already been logged out.", HttpStatus.OK.value());
        }
        
        if (userSessionInfo.getIpSecurityStatus() == UserSession.IpSecurityStatus.ENABLE) {
            // IP 보안이 활성화된 경우, IP가 변경되었는지 확인
            
            if (! userSessionInfo.getLoginIp().equals(WiniCom.getClientIp(req))) {
                // logout 처리
                userFacade.logout(userSessionInfo.getId());

                // refresh token 쿠키 삭제
                clearRefreshToken(res);

                return CommonResponse.fail(ErrorCode.COMMON_UNAUTHENTICATED, "You've been logged out because your IP has changed. Please log in again.", HttpStatus.OK.value());
            }
        }

        LoginUserContext userContext = WiniSecurity.parseLoginUserContext(req);

        if (accessTokenRefreshTimeout != null && accessTokenRefreshTimeout > 0L) {
            // JWT 토큰 만료 후 지난 시간
            long accessTokenExpiredDuration = Duration.between(userContext.getAccessTokenExpiredAt(), OffsetDateTime.now()).toSeconds();

            // 액세스토큰 만료 후 갱신시까지 타임아웃이 지정된 경우, 만료시간이 지나면 로그아웃 처리
            if (accessTokenExpiredDuration > accessTokenRefreshTimeout) {
                return CommonResponse.fail(ErrorCode.COMMON_UNAUTHENTICATED, "Your access token has expired due to inactivity. Please sign in again.", HttpStatus.OK.value());
            }
        }

        // 액세스토큰 갱신
        UserTokenDto result;

        result = userFacade.refreshAccessToken(userSessionInfo, null);

        if (result.getRefreshToken() != null) {
            // refreshToken을 cookie로 전달
            userSessionFacade.setRefreshTokenCookie(req, res, result.getRefreshToken(), result.getRefreshTokenExpiresIn());
        }

        return CommonResponse.success(result);
    }

    @PatchMapping("/{id}/resetPassword")
    public CommonResponse resetUserPassword(@Valid @PathVariable String id, @RequestBody UserDto.ResetPasswordRequest request, HttpServletRequest req, HttpServletRequest res) throws UnirestException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String clientIp = WiniCom.getClientIp(req);

        userFacade.resetUserPassword(UUID.fromString(id), request.getNewPassword(), clientIp);
        return CommonResponse.success("OK");
    }

    @PatchMapping("/{id}/changeUserPassword")
    public CommonResponse changeUserPassword(@Valid @PathVariable UUID id, @RequestBody UserDto.ChangeUserPasswordRequest request, HttpServletRequest req, HttpServletRequest res) throws UnirestException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String clientIp = WiniCom.getClientIp(req);

        UUID userId = loginUserContext.getUserId();

        if (!id.equals(userId)) {
            return CommonResponse.fail(ErrorCode.COMMON_ILLEGAL_STATUS, 400);
        }

        userFacade.changeUserPassword(userId, request.getOldPassword(), request.getNewPassword(), clientIp);
        return CommonResponse.success("OK");
    }

    @PatchMapping("/unlockLogin")
    public CommonResponse unlockLogin(@RequestBody UserDto.UnlockLoginRequest request) throws UnirestException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        loginLogFacade.unlockLogin(request.getUsername());
        return CommonResponse.success("OK");
    }

    @PatchMapping("/{id}/acceptJoin")
    public CommonResponse acceptJoin(@Valid @PathVariable String id) {
        userFacade.acceptJoin(UUID.fromString(id));
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
    
    @ApiOperation(value = "공통 사용자 목록 동기화", notes = "각 서비스의 공통 사용자 목록을 동기화합니다.")
    @GetMapping("/syncAllCommonUser")
    public CommonResponse syncAllUser() throws JsonProcessingException {
        userFacade.syncAllCommonUser();;
        return CommonResponse.success("OK");
    }
}
