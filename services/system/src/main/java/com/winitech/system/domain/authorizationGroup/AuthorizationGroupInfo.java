package com.winitech.system.domain.authorizationGroup;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.Getter;

/**
* com.winitech.system.domain.userGroup
* ㄴ UserGroupInfo.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-03-25 오후 2:17
* @see : None
 **/
@Getter
public class AuthorizationGroupInfo {
    private final UUID id;
    private final String groupCode;
    private final String groupName;
    private final String remark;
    private final AuthorizationGroup.Status status;
    private final AuthorizationGroup.AdminStatus adminStatus;
    private final UUID parentAuthorizationGroupId;

    public AuthorizationGroupInfo(AuthorizationGroup authorizationGroup) {
    	id = authorizationGroup.getId();
        groupCode = authorizationGroup.getGroupCode();
        groupName = authorizationGroup.getGroupName();
        remark = authorizationGroup.getRemark();
        status = authorizationGroup.getStatus();
        adminStatus = authorizationGroup.getAdminStatus();
        
        if (authorizationGroup.getParentAuthorizationGroup() == null) {
            parentAuthorizationGroupId = null;
        } else {
            parentAuthorizationGroupId = authorizationGroup.getParentAuthorizationGroup().getId();
        }
    }

    @Getter
    public static class AuthorizationGroupTreeInfo {
        private final UUID id;
        private final String groupCode;
        private final String groupName;
        private final String remark;
        private final AuthorizationGroup.Status status;
        private final AuthorizationGroup.AdminStatus adminStatus;
        private final UUID parentAuthorizationGroupId;
        private final List<AuthorizationGroupTreeInfo> childrenAuthorizationGroup;
        
        public AuthorizationGroupTreeInfo(AuthorizationGroup authorizationGroup) {
            id = authorizationGroup.getId();
            groupCode = authorizationGroup.getGroupCode();
            groupName = authorizationGroup.getGroupName();
            remark = authorizationGroup.getRemark();
            status = authorizationGroup.getStatus();
            adminStatus = authorizationGroup.getAdminStatus();
            
            if (authorizationGroup.getParentAuthorizationGroup() == null) {
                parentAuthorizationGroupId = null;
            } else {
                parentAuthorizationGroupId = authorizationGroup.getParentAuthorizationGroup().getId();
            }
            
            childrenAuthorizationGroup = authorizationGroup.getChildrenAuthorizationGroup().stream()
                    .map(AuthorizationGroupTreeInfo::new)
                    .collect(Collectors.toList());
        }
    }
}
