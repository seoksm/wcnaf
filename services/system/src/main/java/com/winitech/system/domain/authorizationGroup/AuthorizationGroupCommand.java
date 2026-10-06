package com.winitech.system.domain.authorizationGroup;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
* com.winitech.system.domain.userGroup
* ㄴ AuthorizationGroupCommand.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-03-25 오후 2:38
* @see : None
 **/
@Getter
@Builder
@ToString
public class AuthorizationGroupCommand {
    private final UUID id;
    private final String groupCode;
    private final String groupName;
    private final String remark;
    private final AuthorizationGroup.Status status;
    private final AuthorizationGroup.AdminStatus adminStatus;
    private final UUID parentAuthorizationGroupId;

    public AuthorizationGroup toEntity(AuthorizationGroupReader authorizationGroupReader) {
        AuthorizationGroup parentAuthorizationGroup = null;
        
        if (parentAuthorizationGroupId != null) {
            parentAuthorizationGroup = authorizationGroupReader.getAuthorizationGroup(parentAuthorizationGroupId);
        }
        
        return AuthorizationGroup.builder()
                .id(id)
                .groupCode(groupCode)
                .groupName(groupName)
                .remark(remark)
                .status(status)
                .adminStatus(adminStatus)
                .systemStatus(AuthorizationGroup.SystemStatus.ENABLE)
                .parentAuthorizationGroup(parentAuthorizationGroup)
                .build();
    }
}
