package com.winitech.system.domain.userPasswordLog;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.userPasswordLog
 * └ UserPasswordLogReader.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-23 13:36
 **/
public interface UserPasswordLogReader {
	UserPasswordLog getUserPasswordLogById(UUID id);
	
	List<UserPasswordLog> getLastNthUserPasswordLogByUserId(UUID userId, int userPasswordLogCount);
}