package com.winitech.system.domain.user;

import com.winitech.system.domain.department.Department;
import com.winitech.system.domain.userOrganization.UserOrganization;
import com.winitech.system.domain.userOrganization.UserOrganizationInfo;
import com.winitech.system.domain.userOrganization.UserOrganizationService;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
* com.winitech.user.domain
* ㄴ UserInfo.java
* @author : 박준희 과장 (부설연구소)
* @since : 2021-12-15 오후 4:28
* @see : None
 **/
@Getter
public class UserInfo {
    private final String id;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String fullName;
    private final String email;
    private final String phoneNumber;
    private final String employeeNo;
    private final String dutyName;
    private final Department department;
    private final User.Status status;
    private final User.JoinStatus joinStatus;
    private final OffsetDateTime lastPasswordChangedAt;
    private final OffsetDateTime createAt;
    private final OffsetDateTime updateAt;

    public UserInfo(User user) {
        this.id = user.getId().toString();
        this.username = user.getUsername();
        this.lastName = user.getLastName();
        this.firstName = user.getFirstName();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.employeeNo = user.getEmployeeNo();
        this.dutyName = user.getDutyName();
        this.department = user.getDepartment();
        this.status = user.getStatus();
        this.joinStatus = user.getJoinStatus();
        this.lastPasswordChangedAt = user.getLastPasswordChangedAt();
        this.createAt = user.getCreateAt();
        this.updateAt = user.getUpdateAt();
    }

    @Getter
    public static class UserOrganizationCdcInfo {
        private final String id;
        private final String organizationId;
        private final String organizationCode;
        
        public UserOrganizationCdcInfo(UUID userId, UUID organizationId, String organizationCode) {
            this.id = userId.toString();
            this.organizationId = organizationId.toString();
            this.organizationCode = organizationCode;
        }
    }

    @Getter
    public static class UserCdcInfo {
        private final String id;
        private final String username;
        private final String firstName;
        private final String lastName;
        private final String fullName;
        private final String email;
        private final String phoneNumber;
        private final String employeeNo;
        private final String dutyName;
        private final String departmentName;
        private final User.Status status;
        private final User.JoinStatus joinStatus;
        
        private final String[] organizationIds;
        private final String[] organizationCodes;

        private final OffsetDateTime createAt;
        private final OffsetDateTime updateAt;

        public UserCdcInfo(UserInfo user, UserOrganizationService userOrganizationService) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.lastName = user.getLastName();
            this.firstName = user.getFirstName();
            this.fullName = user.getFullName();
            this.email = user.getEmail();
            this.phoneNumber = user.getPhoneNumber();
            this.employeeNo = user.getEmployeeNo();
            this.dutyName = user.getDutyName();
            this.departmentName = user.getDepartment() == null ? null : user.getDepartment().getDepartmentName();
            this.status = user.getStatus();
            this.joinStatus = user.getJoinStatus();

            List<UserOrganizationInfo> userOrganizationInfoList = userOrganizationService.getUserOrganizationByUserId(UUID.fromString(user.getId()));

            this.organizationIds = userOrganizationInfoList
                    .stream()
                    .map(UserOrganizationInfo::getOrganizationId)
                    .map(UUID::toString)
                    .toArray(String[]::new);

            this.organizationCodes = userOrganizationInfoList
                    .stream()
                    .map(UserOrganizationInfo::getOrganizationCode)
                    .toArray(String[]::new);
            
            this.createAt = user.getCreateAt();
            this.updateAt = user.getUpdateAt();
        }
    }
}
