package com.winitech.system.domain.user;

import com.winitech.system.domain.userOrganization.UserOrganizationInfo;
import com.winitech.system.interfaces.inboundAdapter.web.user.UserTokenDto;

import java.util.List;
import java.util.UUID;

/**
* com.winitech.user.domain
* ㄴ UserService.java
* @author : 박준희 과장 (부설연구소)
* @since : 2021-12-15 오후 4:41
* @see : None
 **/
public interface UserService {
    UserInfo registerUser(UserCommand userCommand);
    UserInfo modifyUser(UUID id, UserCommand.UserModifyCommand userCommand);
    UserInfo searchUserInfo(UUID id);
    List<UserInfo> searchUserInfoListById(List<UUID> idList);
    UserInfo searchUserInfoByUsername(String userId);
    List<UserInfo> searchAllUserInfo();
    List<UserInfo> searchAllUserInfo(List<UUID> userIds);
    List<UserInfo> searchAllJoinedUserInfo();
    void removeUser(UUID id);
    Boolean checkUserJoinStatus(String username);
    User.JoinStatus getUserJoinStatus(String username);
    void acceptJoin(UUID id);
    UserTokenDto login(UserCommand.LoginCommand command);
    void logout(UUID userSessionId);
    Boolean resetUserPassword(UUID id, String newPassword, String clientIp);
    void changeUserPassword(UUID userId, String oldPassword, String newPassword, String clientIp);
    UserTokenDto createUserTokenDtoWithJwt(UserInfo user, String userSessionId, List<UserOrganizationInfo> userOrganizationList, String encryptKey, String checkIp);
    Boolean isFirstLoginToday(String username);
}
