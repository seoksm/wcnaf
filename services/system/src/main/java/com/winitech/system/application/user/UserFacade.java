package com.winitech.system.application.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.interfaces.outboundAdapter.eventProducer.CommonUserSessionBlockEventProducer;
import com.winitech.common.library.WiniConst;
import com.winitech.system.application.userSession.UserSessionFacade;
import com.winitech.system.domain.user.User;
import com.winitech.system.domain.user.UserCommand;
import com.winitech.system.domain.user.UserInfo;
import com.winitech.system.domain.user.UserService;
import com.winitech.system.domain.userOrganization.UserOrganization;
import com.winitech.system.domain.userOrganization.UserOrganizationCommand;
import com.winitech.system.domain.userOrganization.UserOrganizationInfo;
import com.winitech.system.domain.userOrganization.UserOrganizationService;
import com.winitech.system.domain.userSession.UserSession;
import com.winitech.system.domain.userSession.UserSessionInfo;
import com.winitech.system.domain.userSession.UserSessionService;
import com.winitech.system.interfaces.inboundAdapter.web.user.UserProducer;
import com.winitech.system.interfaces.inboundAdapter.web.user.UserTokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
* com.winitech.user.application
* ㄴ UserFacade.java
* @author : 박준희 과장 (부설연구소)
* @since : 2021-12-15 오후 5:19
* @see : None
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class UserFacade {
    private final UserService userService;
    private final UserProducer userProducer;
    private final UserSessionService userSessionService;
    private final UserOrganizationService userOrganizationService;
    private final UserSessionFacade userSessionFacade;
    private final LoginUserContext loginUserContext;
    private final CommonUserSessionBlockEventProducer commonUserSessionBlockEventProducer;

    @Transactional
    public UserInfo registerUser(UserCommand command) throws UnirestException, NoSuchAlgorithmException, KeyStoreException, JsonProcessingException, KeyManagementException {
        /*//키클락 연동
        UserServiceTokenDto userServiceTokenDto = userService.getUserServiceToken();
        String accessToken = "";
        KeycloakUserDto resultUser = null;
        Boolean registerResult;
        accessToken = userServiceTokenDto.getAccessToken();
        UserDto.UserRequest userRequest = UserDto.UserRequest.builder()
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .fullName(command.getFullName())
                .userId(command.getUserId())
                .email(command.getEmail())
                .password(command.getPassword())
                .groupCode(command.getGroupCode())
                .build();
        registerResult = userService.registerUser(accessToken, command);
        if(!registerResult) {
            throw new IllegalStatusException();
        }
        resultUser = keyCloakAdapter.searchUserInfoByEmail(accessToken, command.getEmail());
        registerResult = keyCloakAdapter.giveUserRole(accessToken, resultUser.getId(), String.valueOf(command.getGroupCode()));
        if(!registerResult) {
            throw new IllegalStatusException();
        }
        keyCloakAdapter.resetUserPassword(accessToken, resultUser.getId(), command.getPassword());*/
        
        //사용자 정보 저장
        UserInfo userInfo = userService.registerUser(command);

        userOrganizationService.registerUserOrganization(
                UserOrganizationCommand.builder()
                        .userId(UUID.fromString(userInfo.getId()))
                        .organizationId(WiniConst.DEFAULT_ORG_UUID)
                        .systemStatus(UserOrganization.SystemStatus.ENABLE)
                        .build()
        );
        
        userProducer.  userCreated(new UserInfo.UserCdcInfo(userInfo, userOrganizationService));
        return userInfo;
    }

    public UserInfo modifyUser(UUID id, UserCommand.UserModifyCommand command) throws UnirestException, NoSuchAlgorithmException, KeyStoreException, JsonProcessingException, KeyManagementException {
        /*Boolean modifyResult = null;
        UserDto.UserRequest userRequest = UserDto.UserRequest.builder()
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .fullName(command.getFullName())
                .userId(command.getUserId())
                .email(command.getEmail())
                .groupCode(command.getGroupCode())
                .build();
        modifyResult = keyCloakAdapter.modifyUser(accessToken, id, userRequest);
        if(!modifyResult) {
            throw new IllegalStatusException();
        }
        UserInfo prevUserInfo = userService.searchUserInfo(id);
        modifyResult = keyCloakAdapter.removeUserRole(accessToken, prevUserInfo.getId(), prevUserInfo.getUserGroup().getGroupCode());
        if(!modifyResult) {
            throw new IllegalStatusException();
        }
        modifyResult = keyCloakAdapter.giveUserRole(accessToken, prevUserInfo.getId(), userRequest.getGroupCode());
        if(!modifyResult) {
            throw new IllegalStatusException();
        }        
         */
        UserInfo beforeUserInfo = userService.searchUserInfo(id);
        UserInfo userInfo = userService.modifyUser(id, command);
        userProducer.userUpdated(new UserInfo.UserCdcInfo(beforeUserInfo, userOrganizationService), new UserInfo.UserCdcInfo(userInfo, userOrganizationService));
        return userInfo;
    }

    public UserInfo searchUserInfoByUsername(String username) {
        UserInfo userInfo = userService.searchUserInfoByUsername(username);
        return userInfo;
    }

    public UserInfo searchUserInfo(UUID id) {
        UserInfo userInfo = userService.searchUserInfo(id);
        return userInfo;
    }

    public List<UserInfo> searchAllUserInfo() {
        List<UserInfo> userInfoList = userService.searchAllUserInfo();
        return userInfoList;
    }
    public List<UserInfo> searchAllJoinedUserInfo() {
        List<UserInfo> userInfoList = userService.searchAllJoinedUserInfo();
        return userInfoList;
    }

    public void removeUser(UUID id) throws UnirestException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, JsonProcessingException {
        /*
        keyCloakUser.removeUser(accessToken, id);        
         */
        UserInfo beforeUserInfo = userService.searchUserInfo(id);
        userService.removeUser(id);
        userProducer.userDeleted(new UserInfo.UserCdcInfo(beforeUserInfo, userOrganizationService));
    }

    public UserTokenDto login(UserCommand.LoginCommand command) throws UnirestException, NoSuchAlgorithmException, KeyStoreException, JsonProcessingException, KeyManagementException {
        UserTokenDto userTokenDto = userService.login(command);
        if(userTokenDto != null) {
            // 유저가 처음으로 오늘 처음으로 로그인했는지 파악
            Boolean isUserFirstLoginToday = userService.isFirstLoginToday(command.getUsername());
            if (isUserFirstLoginToday) {
                // 필요시 처음 로그인시 처리
                log.info("User {} logged in for the first time today.", command.getUsername());
            }
        }
        
        return userTokenDto;
    }

    public void logout(UUID userSessionId){
        userService.logout(userSessionId);
        
        if (loginUserContext.getUserSessionId() != null && loginUserContext.getUserSessionId().equals(userSessionId)) {
            // JWT 토큰 블락처리
            OffsetDateTime accessTokenExpiresAt = loginUserContext.getAccessTokenExpiredAt().plusSeconds(30);
            commonUserSessionBlockEventProducer.userSessionBlocked(loginUserContext.getUserSessionId(), accessTokenExpiresAt, loginUserContext.getUserId());
        }
    }
    
    public void resetUserPassword(UUID id, String newPassword, String clientIp) {
        Boolean resetResult = userService.resetUserPassword(id, newPassword, clientIp);
        if(!resetResult) {
            throw new IllegalStatusException();
        }
    }

    public void acceptJoin(UUID id) {
         userService.acceptJoin(id);
    }

    public List<UserInfo> searchUserInfoListById(List<UUID> idList) {
        return userService.searchUserInfoListById(idList);
    }

    public void changeUserPassword(UUID userId, String oldPassword, String newPassword, String clientIp) {
        userService.changeUserPassword(userId, oldPassword, newPassword, clientIp);
    }

    public UserTokenDto refreshAccessToken(UserSessionInfo userSessionInfo, String encryptKey) {
        UserInfo userInfo = userService.searchUserInfo(userSessionInfo.getUserId());

        List<UserOrganizationInfo> userOrganizationList = userOrganizationService.getUserOrganizationByUserId(userSessionInfo.getUserId());

        UserTokenDto userTokenDto = userService.createUserTokenDtoWithJwt(
                userInfo, 
                userSessionInfo.getId().toString(),
                userOrganizationList, 
                encryptKey,
                userSessionInfo.getIpSecurityStatus() == UserSession.IpSecurityStatus.ENABLE ? userSessionInfo.getLoginIp() : null
        );

        userSessionService.increaseRefreshCnt(userSessionInfo.getId());

        UserSessionInfo nextUserSessionInfo = userSessionService.rotateRefreshTokenIfNeeded(userSessionInfo.getId());
       
        if (! nextUserSessionInfo.getRefreshToken().equals(userSessionInfo.getRefreshToken())) {
            // refresh token이 변경된 경우 새로운 refresh token을 반환            
            userTokenDto.setRefreshToken(nextUserSessionInfo.getRefreshToken());
            userTokenDto.setRefreshTokenExpiresIn((int) Duration.between(OffsetDateTime.now(), nextUserSessionInfo.getRefreshTokenExpiresAt()).getSeconds());
        }
        
        return userTokenDto;
    }

    public UserTokenDto devRefreshAccessToken(LoginUserContext loginUserContext){
        UserInfo userInfo = userService.searchUserInfo(loginUserContext.getUserId());
        List<UserOrganizationInfo> userOrganizationList = userOrganizationService.getUserOrganizationByUserId(loginUserContext.getUserId());
        return userService.createUserTokenDtoWithJwt(
                userInfo,
                loginUserContext.getUserSessionId().toString(),
                userOrganizationList,
                loginUserContext.getEncryptKey(),
                loginUserContext.getUserIp()
        );

    }

    public void syncAllCommonUser() throws JsonProcessingException {
        List<UserInfo.UserCdcInfo> userInfoList = userService.searchAllUserInfo()
                .stream()
                .map(userInfo -> new UserInfo.UserCdcInfo(userInfo, userOrganizationService))
                .collect(Collectors.toList());
        
        for (UserInfo.UserCdcInfo userInfo : userInfoList) {
            userProducer.userUpdated(userInfo, userInfo);
        }
    }
}

