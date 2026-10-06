package com.winitech.system.domain.user;

/**
 * <pre>
 * com.winitech.system.domain.user
 * └ UserMybatisStore.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-06-18 18:02
 **/
public interface UserMybatisStore {
	User store(User user);
	User modify(User user, UserCommand.UserModifyCommand userCommand);
}
