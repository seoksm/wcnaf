package com.winitech.apiGateway.domain.userSessionBlock;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.domain.userSessionBlock
 * └ UserSessionBlock.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-21 15:03
 **/
public interface UserSessionBlockService {
	UserSessionBlockInfo registerUserSessionBlock(UserSessionBlockCommand.RegisterRequestCommand userSessionBlockCommand);
	UserSessionBlockInfo modifyUserSessionBlock(UUID id, UserSessionBlockCommand.ModifyRequestCommand userSessionBlockCommand);
	void removeUserSessionBlock(UUID id);
	void removeExpiredAccessToken();
	UserSessionBlockInfo searchUserSessionBlockById(UUID id);
	UserSessionBlockInfo searchUserSessionBlockByUserSessionId(UUID userSessionId);
	List<UserSessionBlockInfo> getAllUserSessionBlock();
	boolean existUserSessionBlockByUserSessionId(UUID userSessionId);
}
