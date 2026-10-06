package com.winitech.system.domain.authorizationGroup;

import java.util.List;
import java.util.UUID;

/**
* s
* ㄴ UserGroupService.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-03-25 오후 2:11
* @see : None
 **/
public interface AuthorizationGroupService {

	AuthorizationGroupInfo registerAuthorizationGroup(AuthorizationGroupCommand authorizationGroupCommand);
	AuthorizationGroupInfo modifyAuthorizationGroup(UUID id, AuthorizationGroupCommand authorizationGroupCommand);
    AuthorizationGroupInfo searchAuthorizationGroup(UUID id);
    AuthorizationGroupInfo searchAuthorizationGroupByAuthorizationGroupCode(String authorizationGroupCode);
    void removeAuthorizationGroup(UUID id);
    List<AuthorizationGroupInfo> getAllAuthorizationGroup();
	List<AuthorizationGroupInfo.AuthorizationGroupTreeInfo> getAllAuthorizationGroupTree();
}
