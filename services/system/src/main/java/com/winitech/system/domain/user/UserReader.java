package com.winitech.system.domain.user;

import java.util.List;
import java.util.UUID;

/**
* com.winitech.user.domain
* ㄴ USerReader.java
* @author : 박준희 과장 (부설연구소)
* @since : 2021-12-15 오후 4:55
* @see : None
 **/
public interface UserReader {
    User getUser(UUID id);
    List<User> getUserListById(List<UUID> idList);
    User getUserByUsername(String userId);
    List<User> getAllUser();
    List<User> getAllUser(List<UUID> userIds);
    List<User> getAllJoinedUser();
    boolean existsByEmail(String email);
}
