package com.winitech.system.domain.user;

import com.winitech.system.domain.userOrganization.UserOrganizationInfo;
import com.winitech.system.interfaces.inboundAdapter.web.user.UserTokenDto;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.user
 * └ UserMybatisService.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-06-17 16:13
 **/
public interface UserMybatisService {
	UserInfo registerUser(UserCommand userCommand);
	UserInfo modifyUser(UUID id, UserCommand.UserModifyCommand userCommand);
	UserInfo searchUserInfo(UUID id);
	List<UserInfo> searchUserInfoListById(List<UUID> idList);
	UserInfo searchUserInfoByUsername(String userId);
	List<UserInfo> searchAllUserInfo();
	List<User> searchAllUserInfoByCommonDao(User.JoinStatus joinStatus);
	List<EgovMap> searchAllUserInfoByCommonDaoEgovMap(EgovMap params);
	List<UserInfo> searchAllUserInfo(User.JoinStatus joinStatus);
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
}
