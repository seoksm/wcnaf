package com.winitech.system.domain.user;

import com.winitech.common.library.WiniConst;
import com.winitech.system.domain.department.Department;
import com.winitech.system.domain.userSession.UserSession;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
* com.winitech.user.domain
* ㄴ UserCommand.java
* @author : 박준희 과장 (부설연구소)
* @since : 2021-12-15 오후 4:40
* @see : None
 **/
@Getter
@Builder
public class UserCommand {
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String fullName;
    private final String email;
    private final String password;
    private final String phoneNumber;
    private final String employeeNo;
    private final String dutyName;
    private final UUID departmentId;
    private final String groupCode;
    private final User.Status status;
    private final UserSession.IpSecurityStatus ipSecurityStatus;
    private final OffsetDateTime lastPasswordChangedAt;

    public User toEntity(Department department) {

        return User.builder()
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .fullName(fullName)
                .email(email)
                .phoneNumber(phoneNumber)
                .employeeNo(employeeNo)
                .dutyName(dutyName)
                .hashedPassword(password)
                .department(department)
                .build();
    }

    @Getter
    @Builder
    public static class UserModifyCommand {
        private final String firstName;
        private final String lastName;
        private final String fullName;
        private final String phoneNumber;
        private final String employeeNo;
        private final String dutyName;
        private final UUID departmentId;
        private final User.JoinStatus joinStatus;
    }
    
    @Getter
    @Builder
    public static class LoginCommand {
        private final String username;
        private final String password;
        private final String loginIp;
        private final UserSession.IpSecurityStatus ipSecurityStatus;
        private final String sessionEncryptKey;
        private final UUID organizationId;
    }
}
