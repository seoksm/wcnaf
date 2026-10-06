package com.winitech.system.interfaces.inboundAdapter.web.authorizationGroup;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.winitech.system.domain.authorizationGroup.AuthorizationGroup;
import com.winitech.system.domain.authorizationGroup.AuthorizationGroupCommand;
import com.winitech.system.domain.authorizationGroup.AuthorizationGroupInfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
* com.winitech.system.interfaces.userGroup
* ㄴ UserGroupDto.java
* @author : 박준희 과장 (부설연구소)
* @since : 2022-03-25 오후 9:34
* @see : None
 **/
public class AuthorizationGroupDto {
    /*
    * ID
    * 그룹 코드
    * 그룹 이름
    * */
	@Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorizationGroupRequest {
        @NotNull(message = "그룹 코드는 필수 값입니다.")
        @ApiModelProperty(value = "그릅 코드", example = "ADMIN", required = true)
        private String groupCode;
        @ApiModelProperty(value = "그릅 코드", example = "관리자", required = true)
        private String groupName;
        @ApiModelProperty(value = "비고", example = "비고")
        private String remark;
        @ApiModelProperty(value = "사용 상태", example = "ENABLE")
        private AuthorizationGroup.Status status;
        @ApiModelProperty(value = "관리자 상태", example = "ENABLE")
        private AuthorizationGroup.AdminStatus adminStatus;
        @ApiModelProperty(value = "부모 권한 그룹 ID", example = "00000000-0000-0000-0000-000000000000")
        private UUID parentAuthorizationGroupId;

        public AuthorizationGroupCommand toCommand() {
            return AuthorizationGroupCommand.builder()
                    .groupCode(groupCode)
                    .groupName(groupName)
                    .remark(remark)
                    .status(status)
                    .adminStatus(adminStatus)
                    .parentAuthorizationGroupId(parentAuthorizationGroupId)
                    .build();
        }
    }
    @Getter
    @ToString
    public static class AuthorizationGroupStoreResponse {
        @ApiModelProperty(value = "그룹 ID", example = "1")
        private String groupId;

        public AuthorizationGroupStoreResponse (AuthorizationGroupInfo authorizationGroupInfo) {
            this.groupId = authorizationGroupInfo.getId().toString();
        }
    }
    @Getter
    @ToString
    public static class AuthorizationGroupResponse {
    	@ApiModelProperty(value = "그룹 ID", example = "00000000-0000-0000-0000-000000000000")
        private String id;
        @ApiModelProperty(value = "그룹 코드", example = "ADMIN", required = true)
        private String groupCode;
        @ApiModelProperty(value = "그룹 이름", example = "관리자", required = true)
        private String groupName;
        @ApiModelProperty(value = "비고", example = "비고")
        private String remark;
        @ApiModelProperty(value = "사용 상태", example = "ENABLE")
        private AuthorizationGroup.Status status;
        @ApiModelProperty(value = "관리자 상태", example = "ENABLE")
        private AuthorizationGroup.AdminStatus adminStatus;
        @ApiModelProperty(value = "부모 권한 그룹 ID", example = "00000000-0000-0000-0000-000000000000")
        private UUID parentAuthorizationGroupId;

        public AuthorizationGroupResponse (AuthorizationGroupInfo authorizationGroupInfo) {
            this.id = authorizationGroupInfo.getId().toString();
            this.groupCode = authorizationGroupInfo.getGroupCode();
            this.groupName = authorizationGroupInfo.getGroupName();
            this.remark = authorizationGroupInfo.getRemark();
            this.status = authorizationGroupInfo.getStatus();
            this.adminStatus = authorizationGroupInfo.getAdminStatus();
            this.parentAuthorizationGroupId = authorizationGroupInfo.getParentAuthorizationGroupId();
        }
    }
    @Getter
    @ToString
    public static class AuthorizationGroupTreeResponse {
    	@ApiModelProperty(value = "그룹 ID", example = "00000000-0000-0000-0000-000000000000")
        private String id;
        @ApiModelProperty(value = "그룹 코드", example = "ADMIN", required = true)
        private String groupCode;
        @ApiModelProperty(value = "그룹 이름", example = "관리자", required = true)
        private String groupName;
        @ApiModelProperty(value = "비고", example = "비고")
        private String remark;
        @ApiModelProperty(value = "사용 상태", example = "ENABLE")
        private AuthorizationGroup.Status status;
        @ApiModelProperty(value = "관리자 상태", example = "ENABLE")
        private AuthorizationGroup.AdminStatus adminStatus;
        @ApiModelProperty(value = "부모 권한 그룹 ID", example = "00000000-0000-0000-0000-000000000000")
        private UUID parentAuthorizationGroupId;
        @ApiModelProperty(value = "하위 권한 그룹", example = "[]")
        private List<AuthorizationGroupTreeResponse> children;

        public AuthorizationGroupTreeResponse (AuthorizationGroupInfo.AuthorizationGroupTreeInfo authorizationGroupTreeInfo) {
            this.id = authorizationGroupTreeInfo.getId().toString();
            this.groupCode = authorizationGroupTreeInfo.getGroupCode();
            this.groupName = authorizationGroupTreeInfo.getGroupName();
            this.remark = authorizationGroupTreeInfo.getRemark();
            this.status = authorizationGroupTreeInfo.getStatus();
            this.adminStatus = authorizationGroupTreeInfo.getAdminStatus();
            this.parentAuthorizationGroupId = authorizationGroupTreeInfo.getParentAuthorizationGroupId();
            
            if (authorizationGroupTreeInfo.getChildrenAuthorizationGroup().size() == 0) {
                this.children = null;
            } else {
                this.children = authorizationGroupTreeInfo.getChildrenAuthorizationGroup().stream()
                        .map(AuthorizationGroupTreeResponse::new).collect(Collectors.toList());
            }
        }
    }
}
