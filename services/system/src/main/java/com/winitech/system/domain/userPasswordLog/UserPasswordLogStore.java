package com.winitech.system.domain.userPasswordLog;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.userPasswordLog
 * └ UserPasswordLogStore.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-23 13:36
 **/
public interface UserPasswordLogStore {
	UserPasswordLog store(UserPasswordLog userPasswordLog);

	UserPasswordLog modify(UserPasswordLog userPasswordLog, UserPasswordLogCommand command);
	
	void remove(UUID userPasswordLogId);
	
	void removeNotInIdList(UUID userId, List<UUID> userPasswordLogIds);
}
