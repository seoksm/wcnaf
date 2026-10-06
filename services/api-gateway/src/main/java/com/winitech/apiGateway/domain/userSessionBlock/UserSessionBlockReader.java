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
public interface UserSessionBlockReader {
	UserSessionBlock getUserSessionBlockById(UUID userSessionBlockId);
	UserSessionBlock getUserSessionBlockByUserSessionId(UUID userSessionId);
	UserSessionBlock getUserSessionBlockByUserSessionIdIfExists(UUID userSessionId);
	List<UserSessionBlock> getExpiredUserSessionBlock();
	List<UserSessionBlock> getAllUserSessionBlock();
	boolean existUserSessionBlockByExcludingSelf(UUID userSessionId, UUID userSessionBlockId);
	boolean existUserSessionBlockByUserSessionId(UUID userSessionId);
}
