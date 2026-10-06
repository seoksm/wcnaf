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
public interface UserSessionBlockStore {
	UserSessionBlock store(UserSessionBlock userSessionBlock);
	UserSessionBlock modify(UserSessionBlock userSessionBlock, UserSessionBlockCommand.ModifyRequestCommand userSessionBlockCommand);
	void remove(UUID userSessionBlockId);
	void removeAll(List<UserSessionBlock> expiredUserSessionBlockList);
}
