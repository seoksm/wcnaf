package com.winitech.system.application.authorizationGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.winitech.common.domain.common.CommonAuthorizationGroupUserInfo;
import com.winitech.system.interfaces.inboundAdapter.web.authorizationGroupUser.AuthorizationGroupUserProducer;
import org.springframework.stereotype.Service;

import com.winitech.system.domain.authorizationGroup.AuthorizationGroupCommand;
import com.winitech.system.domain.authorizationGroup.AuthorizationGroupInfo;
import com.winitech.system.domain.authorizationGroup.AuthorizationGroupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
* com.winitech.system.application.userGroup
* ㄴ UserGroupFacade.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-03-25 오후 6:15
* @see : None
 **/

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationGroupFacade {
    private final AuthorizationGroupService authorizationGroupService;

	private final AuthorizationGroupUserProducer authorizationGroupUserProducer;

	public AuthorizationGroupInfo registerAuthorizationGroup(AuthorizationGroupCommand authorizationGroupCommand) {
    	AuthorizationGroupInfo authorizationGroupInfo = authorizationGroupService.registerAuthorizationGroup(authorizationGroupCommand);
    	return authorizationGroupInfo;
    }
    public AuthorizationGroupInfo modifyAuthorizationGroup(UUID id, AuthorizationGroupCommand authorizationGroupCommand) {
    	AuthorizationGroupInfo authorizationGroupInfo = authorizationGroupService.modifyAuthorizationGroup(id, authorizationGroupCommand);
        return authorizationGroupInfo;

    }
    
    public AuthorizationGroupInfo searchAuthorizationGroup(UUID id) {
    	AuthorizationGroupInfo authorizationGroupInfo = authorizationGroupService.searchAuthorizationGroup(id);
        return authorizationGroupInfo;
    }
    
    public AuthorizationGroupInfo searchAuthorizationGroupByGroupCode(String groupCode) {
    	AuthorizationGroupInfo authorizationGroupInfo = authorizationGroupService.searchAuthorizationGroupByAuthorizationGroupCode(groupCode);
    	return authorizationGroupInfo;
    }

    public void removeAuthorizationGroup(UUID id) {
    	authorizationGroupService.removeAuthorizationGroup(id);

		syncAuthorizationGroupUserAsDeleted(Arrays.asList(id));
    }

    public List<AuthorizationGroupInfo> searchAllAuthorizationGroup() {
        List<AuthorizationGroupInfo> authorizationGroupInfoList = authorizationGroupService.getAllAuthorizationGroup();
        return authorizationGroupInfoList;
    }
	
    public List<AuthorizationGroupInfo.AuthorizationGroupTreeInfo> searchAllAuthorizationGroupTree() {
        List<AuthorizationGroupInfo.AuthorizationGroupTreeInfo> authorizationGroupTreeInfoList = authorizationGroupService.getAllAuthorizationGroupTree();
        return authorizationGroupTreeInfoList;
    }

	// 권한 그룹이 삭제되었을때 CommonAuthorizationGroupUser에서도 삭제되도록 함 
	private void syncAuthorizationGroupUserAsDeleted(List<UUID> authorizationGroupIdList) {
		authorizationGroupUserProducer.authorizationGroupUserCdc(authorizationGroupIdList, new ArrayList<>());
	}
}
