package com.winitech.system.interfaces.inboundAdapter.web.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.winitech.system.domain.user.UserInfo;

import java.util.UUID;

/**
* com.winitech.system.interfaces.user
* ㄴ UserProducer.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-08-29 오후 5:47
* @see : None
 **/
public interface UserProducer {
    void userCreated (UserInfo.UserCdcInfo afterUserInfo) throws JsonProcessingException;
    void userUpdated (UserInfo.UserCdcInfo beforeUserInfo, UserInfo.UserCdcInfo afterUserInfo) throws JsonProcessingException;
    void userDeleted (UserInfo.UserCdcInfo beforeUserInfo) throws JsonProcessingException;
    void userOrganizationRemove (UserInfo.UserOrganizationCdcInfo removedUserOrganizationCdcInfo) throws JsonProcessingException;
}
