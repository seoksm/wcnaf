package com.winitech.system.domain.userPasswordLog;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.userPasswordLog
 * └ UserPasswordLogService.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-23 13:36
 **/
public interface UserPasswordLogService {
	UserPasswordLogInfo registerUserPasswordLog(UserPasswordLogCommand userPasswordLogCommand);

	UserPasswordLogInfo modifyUserPasswordLog(UUID id, UserPasswordLogCommand userPasswordLogCommand);

	void removeUserPasswordLog(UUID id);
	
	void cleanUpUserPasswordLog(UUID id, int keepCount);

	UserPasswordLogInfo searchUserPasswordLogById(UUID id);

	List<UserPasswordLog> searchLastNthUserPasswordLogByUserId(UUID userId, int userPasswordLogCount);
}
