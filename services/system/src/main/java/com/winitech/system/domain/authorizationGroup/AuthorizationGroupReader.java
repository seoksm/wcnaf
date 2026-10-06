package com.winitech.system.domain.authorizationGroup;

import java.util.List;
import java.util.UUID;

/**
* com.winitech.system.domain.userGroup
* ㄴ UserGroupReader.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-03-25 오후 3:03
* @see : None
 **/
public interface AuthorizationGroupReader {
	AuthorizationGroup getAuthorizationGroup(UUID id);
	AuthorizationGroup getAuthorizationGroupByGroupCode(String groupCode);
    List<AuthorizationGroup> getAllAuthorizationGroup();
	Boolean existAuthorizationGroupByGroupCodeAndExcludingSelf(String groupCode, UUID id);
}
