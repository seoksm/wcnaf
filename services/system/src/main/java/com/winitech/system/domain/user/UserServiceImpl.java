package com.winitech.system.domain.user;

import com.winitech.common.exception.EntityNotFoundException;
import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.common.interfaces.outboundAdapter.eventProducer.CommonUserSessionBlockEventProducer;
import com.winitech.common.library.WiniSecurity;
import com.winitech.system.domain.department.Department;
import com.winitech.system.domain.department.DepartmentReader;
import com.winitech.system.domain.loginLog.LoginLogReader;
//import com.winitech.system.domain.userGroup.UserGroupService;
import com.winitech.system.domain.userOrganization.UserOrganizationInfo;
import com.winitech.system.domain.userOrganization.UserOrganizationService;
import com.winitech.system.domain.userPasswordLog.UserPasswordLogCommand;
import com.winitech.system.domain.userPasswordLog.UserPasswordLogService;
import com.winitech.system.domain.userSession.UserSession;
import com.winitech.system.domain.userSession.UserSessionCommand;
import com.winitech.system.domain.userSession.UserSessionInfo;
import com.winitech.system.domain.userSession.UserSessionService;
import com.winitech.system.interfaces.inboundAdapter.web.user.UserTokenDto;
import com.winitech.system.interfaces.inboundAdapter.web.userOrganization.UserOrganizationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
* com.winitech.user.domain
* ㄴ UserServiceImpl.java
* @author : 박준희 과장 (부설연구소)
* @since : 2021-12-15 오후 5:31
* @see : None
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends EgovAbstractServiceImpl implements UserService{
    //사용자 저장
    private final UserStore userStore;
    //사용자 조회
    private final UserReader userReader;

//    private final UserGroupService userGroupService;

    private final UserOrganizationService userOrganizationService;
    
    private final UserSessionService userSessionService;
    
    private final UserPasswordLogService userPasswordLogService;

    private final DepartmentReader departmentReader;

    private final LoginLogReader loginLogReader;
    
    private final CommonUserSessionBlockEventProducer commonUserSessionBlockEventProducer;

    @Value("${winitech.security.refresh-token.expires-in-seconds:2592000}")
    private Integer refreshTokenExpiresInSeconds;
    
    @Value("${winitech.security.login.allow-duplicate-login:false}")
    private Boolean allowDuplicateLogin;
     
    /**
     * 보관할 비밀번호 변경 이력 개수 
     */
    @Value("${winitech.security.password.history.keep-count:5}")
    private int passwordHistoryKeepCount;

    /**
     * 최근 비밀번호 재활용 방지 이력 개수
     */
    @Value("${winitech.security.password.history.reuse-prevent-count:5}")
    private int passwordReusePreventionCount;

    /**
     * 비밀번호 만료일수 (0이면 비밀번호 만료일 없음)
     */
    @Value("${winitech.security.login.password.expires-in-days:365}")
    private int passwordExpiresInDays;

    /**
     * email 계정이 중복값이 없으면
     * 비밀번호를 암호화하여 (평문 패스워드 BCrypt 해싱)
     * 새로운 사용자를 추가한다.
     */
    @Override
    @Transactional
    public UserInfo registerUser(UserCommand command) {
        if (userReader.existsByEmail(command.getEmail())) {
            throw new IllegalStatusException("Email already exists");
        }
        User initUser = command.toEntity(
                command.getDepartmentId() == null ? null : departmentReader.getDepartment(command.getDepartmentId())
        );
        
        // initUser.setId(id);
        initUser.setHashedPassword(BCrypt.hashpw(initUser.getHashedPassword(), BCrypt.gensalt()));
        User user = userStore.store(initUser);
        return new UserInfo(user);
    }

    @Override
    @Transactional
    public UserInfo modifyUser(UUID id, UserCommand.UserModifyCommand command) {
        User modifyUser = userReader.getUser(id);
        User user = userStore.modify(modifyUser, command);
        return new UserInfo(user);
    }

    @Override
    public UserInfo searchUserInfo(UUID id) {
        User user = userReader.getUser(id);
        return new UserInfo(user);
    }

    @Override
    public List<UserInfo> searchUserInfoListById(List<UUID> id) {
        List<User> user = userReader.getUserListById(id);
        return user.stream().map(UserInfo::new).collect(Collectors.toList());
    }

    @Override
    public UserInfo searchUserInfoByUsername(String username) {
        User user = userReader.getUserByUsername(username);
        return new UserInfo(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserInfo> searchAllUserInfo() {

        List<User> userList = userReader.getAllUser();
        return userList.stream().map(UserInfo::new).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserInfo> searchAllUserInfo(List<UUID> userIds) {
        List<User> userList = userReader.getAllUser(userIds);
        return userList.stream().map(UserInfo::new).collect(Collectors.toList());
    }

    @Override
    public List<UserInfo> searchAllJoinedUserInfo() {
        List<User> userList = userReader.getAllJoinedUser();
        return userList.stream().map(UserInfo::new).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeUser(UUID id) {
        User user = userReader.getUser(id);
        user.disable();
        userStore.store(user);
    }

    /**
     */
    @Override
    @Transactional(readOnly = true)
    public Boolean checkUserJoinStatus(String username) {
        User user = userReader.getUserByUsername(username);
        if(user.getJoinStatus().equals(User.JoinStatus.ACCEPTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User.JoinStatus getUserJoinStatus(String username) {
        User user = userReader.getUserByUsername(username);
        return user.getJoinStatus();
    }

    @Override
    @Transactional
    public void acceptJoin(UUID id) {
        User user = userReader.getUser(id);
        user.acceptUser();
        userStore.store(user);
    }

    @Override
    @Transactional
    public UserTokenDto login(UserCommand.LoginCommand command) {
        User user;
        
        try {
            user = userReader.getUserByUsername(command.getUsername());
        } catch (EntityNotFoundException e) {
            // 아이디가 없을때와 비밀번호가 다를 때의 오류를 같게하여, 
            // 공격자가 아이디의 존재 유무를 파악하지 못하도록 함
            return null;
        }
        
        if (user.getHashedPassword() == null) {
            return null;
        }
        
        if (! BCrypt.checkpw(command.getPassword(), user.getHashedPassword())) {
            return null;
        }

        if (User.JoinStatus.RESIGNED == user.getJoinStatus()) {
            throw new IllegalStatusException("퇴사한 사용자입니다.");
        } else if (User.JoinStatus.REQUIRED == user.getJoinStatus()) {
            throw new IllegalStatusException("회원가입 승인 필요");
        } else if (User.JoinStatus.ABSENCE == user.getJoinStatus()) {
            throw new IllegalStatusException("휴직중에는 로그인할 수 없습니다.");
        }

        if (User.JoinStatus.ACCEPTED != user.getJoinStatus()) {
            throw new IllegalStatusException("잘못된 가입상태입니다.");
        }

        List<UserOrganizationInfo> userOrganizationList;
        
        // 단일 조직 로그인 모드 사용 여부.
        //
        // 단일 조직 로그인인 경우 사용자의 소속 조직이 여러개라도
        // JWT 토큰에는 하나의 조직 정보만 넣어줌.
        // 단일 조직 로그인이 아닌 경우 조직이 여러개이면 
        // JWT 토큰이 너무 커지는 문제가 있으며, 헤더 크기 제한에 걸리는 문제가 있음.
        boolean useSingleOrganizationMode = true;
        // application.properties에서 선택할 경우 케이스가 많아지기 때문에 별도로 추가하지 않음
        
        if (useSingleOrganizationMode) {
            // 단일 조직 로그인 모드
            if (command.getOrganizationId() == null) {
                userOrganizationList = userOrganizationService.getUserOrganizationByUserId(user.getId());

                if (userOrganizationList != null && userOrganizationList.size() > 1) {
                    // 소속이 2개 이상인 경우 조직 ID를 로그인시 넘기도록 함

                    return UserTokenDto.builder()
                            .userOrganizationList(userOrganizationList.stream().map(UserOrganizationDto.UserOrganizationLoginResponse::new).collect(Collectors.toList()))
                            .build();
                }
            } else {
                UserOrganizationInfo userOrganization = userOrganizationService.getUserOrganization(user.getId(), command.getOrganizationId());

                userOrganizationList = Arrays.asList(userOrganization);
            }
        } else {
            // 다중 조직 로그인 모드
            userOrganizationList = userOrganizationService.getUserOrganizationByUserId(user.getId());
        }
        
        return afterLogin(command, user, userOrganizationList);
    }

    private UserTokenDto afterLogin(UserCommand.LoginCommand command, User user, List<UserOrganizationInfo> userOrganizationList) {
        UserSessionInfo lastUserSessionInfo = userSessionService.getLastLoginUserSessionByUserId(user.getId());

        if (! allowDuplicateLogin) {
            // 중복 로그인 로그아웃 처리
            List<UserSessionInfo> prevUserSessionList = userSessionService.getAllLoginSessionByUserId(user.getId());
            prevUserSessionList.forEach(userSessionInfo -> {
                userSessionService.logout(userSessionInfo.getId());

                // TODO : userSessionInfo.getRefreshTokenExpiresAt() 대신에 accessTokenExpiresAt을 넣어야 함. 하지만 현재는 accessTokenExpiresAt을 관리하지 않아서 임시로 처리 
                commonUserSessionBlockEventProducer.userSessionBlocked(userSessionInfo.getId(), userSessionInfo.getRefreshTokenExpiresAt(), userSessionInfo.getUserId());
            });
        }

        OffsetDateTime now = OffsetDateTime.now();
        String refreshToken = userSessionService.generateRefreshToken(user.getId());
        OffsetDateTime refreshTokenExpiresAt = now.plusSeconds(refreshTokenExpiresInSeconds);
        
        UserSessionCommand userSessionCommand = UserSessionCommand
                .builder()
                .userId(user.getId())
                .refreshToken(refreshToken)
                .refreshTokenExpiresAt(refreshTokenExpiresAt)
                .totalRefreshCnt(0)
                .refreshCnt(0)
                .refreshTokenUpdatedAt(now)
                .loginAt(now)
                .loginIp(command.getLoginIp())
                .loginStatus(UserSession.LoginStatus.LOGIN)
                .ipSecurityStatus(command.getIpSecurityStatus())
                .build();

        UserSessionInfo userSessionInfo = userSessionService.registerUserSession(userSessionCommand);

        boolean isUsingIpSecurity = command.getIpSecurityStatus() == UserSession.IpSecurityStatus.ENABLE;

        UserTokenDto userTokenDto = createUserTokenDtoWithJwt(
                new UserInfo(user),
                userSessionInfo.getId().toString(),
                userOrganizationList, 
                command.getSessionEncryptKey(),
                isUsingIpSecurity ? command.getLoginIp() : null    // IP 보안 사용시만 IP 체크
        );
        
        userTokenDto.setRefreshToken(refreshToken);
        userTokenDto.setRefreshTokenExpiresIn(refreshTokenExpiresInSeconds);
        userTokenDto.setUserSessionId(userSessionInfo.getId());
        
        List<String> departmentNames = new ArrayList<>();
        
        if (user.getDepartment() == null) {
            userTokenDto.setDepartmentName("");
            userTokenDto.setDepartmentNames(departmentNames);
        } else {
            Department department = user.getDepartment();

            userTokenDto.setDepartmentName(department.getDepartmentName());

            while (department != null) {
                departmentNames.add(0, department.getDepartmentName());
                department = department.getParentDepartment();
            }

            userTokenDto.setDepartmentNames(departmentNames);
        }

        userTokenDto.setDutyName(user.getDutyName());

        if (passwordExpiresInDays > 0) {
            if (user.getLastPasswordChangedAt() == null) {
                // 비밀번호 변경 이력이 없는 경우 비밀번호가 만료된것으로 처리
                userTokenDto.setPasswordValidDays(-1);
            } else {
                // 비밀번호 변경 이력이 있는 경우 비밀번호 유효일수 계산
                // ※ 비밀번호 유효일수 = 일수차이((비밀번호 변경 일 + 유효 일 수), 오늘)
                OffsetDateTime passwordExpiresAt = user.getLastPasswordChangedAt().plusDays(passwordExpiresInDays);
                userTokenDto.setPasswordValidDays((int) Duration.between(now, passwordExpiresAt).toDays());
            }
        } else {
            userTokenDto.setPasswordValidDays(null);
        }
        
       
        if (lastUserSessionInfo != null) {
            userTokenDto.setLastLoginAt(lastUserSessionInfo.getLoginAt());
            userTokenDto.setLastLoginIp(lastUserSessionInfo.getLoginIp());
        }
        
        return userTokenDto;
    }

    @Override
    @Transactional
    public void logout(UUID userSessionId) {
        userSessionService.logout(userSessionId);
    }

    @Override
    @Transactional
    public Boolean resetUserPassword(UUID id, String newPassword, String clientIp) {
        User user = userReader.getUser(id);

        setUserPassword(user, newPassword, clientIp, false);
        
        return true;
    }

    @Override
    @Transactional
    public void changeUserPassword(UUID userId, String oldPassword, String newPassword, String clientIp) {
        User user = userReader.getUser(userId);

        if (! BCrypt.checkpw(oldPassword, user.getHashedPassword())) {
            throw new IllegalStatusException("Invalid password");
        }

        setUserPassword(user, newPassword, clientIp);
    }

    private void setUserPassword(User user, String newPassword, String clientIp) {
        setUserPassword(user, newPassword, clientIp, true);
    }
    
    private void setUserPassword(User user, String newPassword, String clientIp, boolean shouldCheckPasswordReuse) {
        if (shouldCheckPasswordReuse && passwordReusePreventionCount > 0) {
            // 비밀번호 사용여부 확인
            userPasswordLogService.searchLastNthUserPasswordLogByUserId(user.getId(), passwordReusePreventionCount).forEach(userPasswordLog -> {
                if (BCrypt.checkpw(newPassword, userPasswordLog.getHashedPassword())) {
                    throw new IllegalStatusException("The password has already been used.");
                }
            });
        }

        user.setHashedPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        user.setLastPasswordChangedAt(OffsetDateTime.now());
        userStore.store(user);

        // 비밀번호 변경 로그 저장
        // 변경하는 비밀번호도 이력에 남기는데, 이것은 데이터는 1개 더 늘지만 코드를 간단하게 하기 위함임
        UserPasswordLogCommand userPasswordLogCommand = UserPasswordLogCommand
                .builder()
                .userId(user.getId())
                .hashedPassword(user.getHashedPassword())
                .passwordChangedAt(OffsetDateTime.now())
                .passwordChangeIp(clientIp)
                .build();
        userPasswordLogService.registerUserPasswordLog(userPasswordLogCommand);

        if (passwordHistoryKeepCount > 0) {
            // 비밀번호 변경 로그 정리
            userPasswordLogService.cleanUpUserPasswordLog(user.getId(), passwordHistoryKeepCount);
        }
    }

    public UserTokenDto createUserTokenDtoWithJwt(
            UserInfo user,
            String userSessionId,
            List<UserOrganizationInfo> userOrganizationList,
            String encryptKey,
            String checkIp
    ) {
        if (userOrganizationList == null) {
            throw new InvalidParamException("userOrganizationList is null");
        }
        
        String[] organizationIds = userOrganizationList
                .stream()
                .map(UserOrganizationInfo::getOrganizationId)
                .map(UUID::toString)
                .toArray(String[]::new);
        String[] organizationCodes = userOrganizationList
                .stream()
                .map(UserOrganizationInfo::getOrganizationCode)
                .toArray(String[]::new);

        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getId().toString());
        claims.put("ufn", user.getFirstName());
        claims.put("uln", user.getLastName());
        claims.put("fnm", user.getFullName());
        claims.put("oid", organizationIds);
        claims.put("tid", organizationCodes);
        if (encryptKey != null) {
            claims.put("enc", encryptKey);
        }
        if (checkIp != null)    {
            claims.put("cip", checkIp);
        }
        
        String accessToken = WiniSecurity.createJwtToken(userSessionId, claims);
		String tokenType = "Bearer";                
    
        List<UserOrganizationDto.UserOrganizationLoginResponse> organizationList = null;

        if (!userOrganizationList.isEmpty()) {
            organizationList = userOrganizationList
                    .stream()
                    .map(UserOrganizationDto.UserOrganizationLoginResponse::new)
                    .collect(Collectors.toList());
        }

        String departmentName = "";
        List<String> departmentNames = new ArrayList<>();
        
        if (user.getDepartment() != null) {
            Department department = user.getDepartment();

            departmentName = department.getDepartmentName();

            while (department != null) {
                departmentNames.add(0, department.getDepartmentName());
                department = department.getParentDepartment();
            }
        }

        UserTokenDto userTokenDto = UserTokenDto
                .builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .departmentName(departmentName)
                .departmentNames(departmentNames)
                .dutyName(user.getDutyName())
                .accessToken(accessToken)
                .tokenType(tokenType)
                .userOrganizationList(organizationList)
                .build();
        return userTokenDto;
    }

    @Override
    public Boolean isFirstLoginToday(String username) {
        return !loginLogReader.existsTodayLoginLogByUsername(username);
    }
}
