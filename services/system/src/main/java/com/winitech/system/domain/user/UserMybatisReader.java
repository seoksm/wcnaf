package com.winitech.system.domain.user;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.user
 * └ UserMybatisReader.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-06-18 18:02
 **/
public interface UserMybatisReader {
	User getUser(UUID id);
	List<User> getUserListById(List<UUID> idList);
	User getUserByUsername(String userId);
	List<User> getAllUser();
	List<User> getAllUser(List<UUID> userIds);
	List<User> getAllJoinedUser();
	List<User> getAllUserByJoinStatus(User.JoinStatus joinStatus);
	boolean existsByEmail(String email);
}
