package com.winitech.system.domain.user;
/**
* com.winitech.user.domain
* ㄴ UserStore.java
* @author : 박준희 과장 (부설연구소)
* @since : 2021-12-15 오후 4:56
* @see : None
 **/
public interface UserStore {
    User store(User user);
    User modify(User user, UserCommand.UserModifyCommand userCommand);
}
