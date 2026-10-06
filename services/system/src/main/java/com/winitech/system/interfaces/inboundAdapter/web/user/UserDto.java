package com.winitech.system.interfaces.inboundAdapter.web.user;

import com.winitech.common.library.WiniString;
import com.winitech.system.domain.user.User;
import com.winitech.system.domain.user.UserCommand;
import com.winitech.system.domain.user.UserInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
* com.winitech.user.interfaces.user
* ㄴ UserDto.java
* @author : 박준희 과장 (부설연구소)
* @since : 2021-12-15 오후 5:53
* @see : None
 **/
public class UserDto {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRequest {
        @ApiModelProperty(value="사용자 아이디", example = "tester", required = true)
        @NotEmpty(message = "사용자 아이디는 필수 값입니다.")
        private String username;
        @ApiModelProperty(value="사용자 성", example = "홍", required = true)
        @NotEmpty(message = "사용자 성은 필수 값입니다.")
        private String lastName;
        @ApiModelProperty(value="사용자 이름", example = "길동", required = true)
        @NotEmpty(message = "사용자 이름은 필수 값입니다.")
        private String firstName;
        @ApiModelProperty(value="사용자 전체 이름", example = "홍길동", required = false)
        private String fullName;
        @ApiModelProperty(value="이메일 주소", example = "sample@gmail.com", required = true)
        @NotEmpty(message = "이메일 주소는 필수 값입니다.")
        @Email
        private String email;
        @ApiModelProperty(value="연락처", example = "010-0000-0000", required = true)
        @NotEmpty(message = "연락처는 필수 값입니다.")
        //@Pattern(regexp = "/01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$/", message = "연락처의 포맷이 맞지 안습니다.")
        private String phoneNumber;
        @ApiModelProperty(value="사원번호", example = "ABC-123", required = false)
        private String employeeNo;
        @ApiModelProperty(value="직책", example = "사원", required = false)
        private String dutyName;
        @ApiModelProperty(value="비밀번호", example = "**********", required = false)
        private String password;
        //@ApiModelProperty(value="권한", example = "ADMIN", required = false)
        //@NotNull(message = "권한은 필수 값입니다.")
        //private String groupCode;
        @ApiModelProperty(value="부서ID", example = "xxxx-xxxx-xxxx-xxxx", required = false)
        private UUID departmentId;

        public UserCommand toCommand() {
            if (fullName == null) {
                // fullName이 없으면 firstName과 lastName을 조합하여 fullName을 설정
                fullName = makeFullName(firstName, lastName);
            }

            return UserCommand.builder()
                    .username(username)
                    .firstName(firstName)
                    .lastName(lastName)
                    .fullName(fullName)
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .employeeNo(employeeNo)
                    .dutyName(dutyName)
                    .password(password)
                    //.groupCode(groupCode)
                    .departmentId(departmentId)
                    .build();
        }
    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserModifyRequest {
        @ApiModelProperty(value="사용자 성", example = "홍", required = true)
        @NotEmpty(message = "사용자 성은 필수 값입니다.")
        private String lastName;
        @ApiModelProperty(value="사용자 이름", example = "길동", required = true)
        @NotEmpty(message = "사용자 이름은 필수 값입니다.")
        private String firstName;
        @ApiModelProperty(value="사용자 전체 이름", example = "홍길동", required = false)
        private String fullName;
        @ApiModelProperty(value="연락처", example = "010-0000-0000", required = true)
        @NotEmpty(message = "연락처는 필수 값입니다.")
        //@Pattern(regexp = "/01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$/", message = "연락처의 포맷이 맞지 안습니다.")
        private String phoneNumber;
        @ApiModelProperty(value="사원번호", example = "ABC-123", required = false)
        private String employeeNo;
        @ApiModelProperty(value="직책", example = "사원", required = false)
        private String dutyName;
        //@ApiModelProperty(value="권한", example = "ADMIN", required = false)
        //@NotNull(message = "권한은 필수 값입니다.")
        //private String groupCode;
        @ApiModelProperty(value="부서ID", example = "xxxx-xxxx-xxxx-xxxx", required = false)
        private UUID departmentId;
        @ApiModelProperty(value="가입 승인 여부", example = "ACCEPTED", required = true)
        private User.JoinStatus joinStatus;

        public UserCommand.UserModifyCommand toCommand() {
            if (fullName == null) {
                // fullName이 없으면 firstName과 lastName을 조합하여 fullName을 설정
                fullName = makeFullName(firstName, lastName);
            }

            return UserCommand.UserModifyCommand.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .fullName(fullName)
                    .phoneNumber(phoneNumber)
                    .employeeNo(employeeNo)
                    .dutyName(dutyName)
                    .departmentId(departmentId)
                    .joinStatus(joinStatus)
                    .build();
        }
    }
    @Getter
    @ToString
    public static class UserResponse {
        @ApiModelProperty(value="ID", example = "xxxx-xxxx-xxxx-xxxx", required = true)
        private final String id;
        @ApiModelProperty(value="사용자 아이디", example = "tester", required = true)
        private final String username;
        @ApiModelProperty(value="사용자 성", example = "홍", required = true)
        private final String lastName;
        @ApiModelProperty(value="사용자 이름", example = "길동", required = true)
        private final String firstName;
        @ApiModelProperty(value="사용자 전체 이름", example = "홍길동")
        private final String fullName;
        @ApiModelProperty(value="이메일 주소", example = "sample@gmail.com", required = true)
        private final String email;
        @ApiModelProperty(value="연락처", example = "010-0000-0000", required = true)
        private final String phoneNumber;
        @ApiModelProperty(value="사원번호", example = "ABC-123", required = false)
        private final String employeeNo;
        @ApiModelProperty(value="직책", example = "사원", required = false)
        private final String dutyName;
        @ApiModelProperty(value="부서 ID", example = "ADMIN", required = true)
        private final String departmentId;
        @ApiModelProperty(value="부서 Code", example = "ADMIN", required = true)
        private final String departmentCode;
        @ApiModelProperty(value="부서 이름", example = "관리자", required = true)
        private final String departmentName;
        @ApiModelProperty(value="사용자 활성화 여부", example = "ENABLED", required = true)
        private final User.Status status;
        @ApiModelProperty(value="가입 승인 여부", example = "ACCEPTED", required = true)
        private final User.JoinStatus joinStatus;
        @ApiModelProperty(value="데이터 생성 시각", example = "2021-11-17T13:46:40.108365", required = true)
        private final OffsetDateTime createAt;
        @ApiModelProperty(value="데이터 수정 시각", example = "2021-11-17T13:46:40.108365", required = true)
        private final OffsetDateTime updateAt;

        public UserResponse(UserInfo userInfo) {
            this.id = userInfo.getId();
            this.username = userInfo.getUsername();
            this.firstName = userInfo.getFirstName();
            this.lastName = userInfo.getLastName();
            this.fullName = userInfo.getFullName();
            this.email = userInfo.getEmail();
            this.phoneNumber = userInfo.getPhoneNumber();
            this.employeeNo = userInfo.getEmployeeNo();
            this.dutyName = userInfo.getDutyName();
            if (userInfo.getDepartment() == null) {
                this.departmentId = null;
                this.departmentCode = null;
                this.departmentName = null;
            } else {
                this.departmentId = userInfo.getDepartment().getId().toString();
                this.departmentCode = userInfo.getDepartment().getDepartmentCode();
                this.departmentName = userInfo.getDepartment().getDepartmentName();
            }
            this.status = userInfo.getStatus();
            this.joinStatus = userInfo.getJoinStatus();
            this.createAt = userInfo.getCreateAt();
            this.updateAt = userInfo.getUpdateAt();
        }
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @ApiModelProperty(value="사용자 ID", example = "tester", required = true)
        @NotEmpty(message = "사용자 ID는 필수 값입니다.")
        @Email
        private String username;
        @ApiModelProperty(value="비밀번호", example = "**********", required = true)
        @NotEmpty(message = "비밀번호는 필수 값입니다.")
        private String password;
        @ApiModelProperty(value="IP 보안 상태", example = "ENABLE", required = false, allowableValues = "ENABLE, DISABLE")
        private String ipSecurityStatus;
        @ApiModelProperty(value="조직 ID", example = "xxxx-xxxx-xxxx-xxxx", required = false)
        private UUID organizationId;
    }

    @Data
    @NoArgsConstructor
    public static class ResetPasswordRequest {
        @ApiModelProperty(value="새 비밀번호", example = "**********", required = true)
        @NotEmpty(message = "새 비밀번호는 필수 값입니다.")
        private String newPassword;
    }
    
    @Data
    @NoArgsConstructor
    public static class AcceptJoin {
        @ApiModelProperty(value="사용자 ID", example = "xxxx-xxxx-xxxx-xxxx", required = true)
        @NotEmpty(message = "사용자 ID는 필수 값입니다.")
        private String id;
    }
    
    @Data
    @NoArgsConstructor
    public static class UnlockLoginRequest {
        @ApiModelProperty(value="사용자 ID", example = "user", required = true)
        @NotEmpty(message = "사용자 ID는 필수 값입니다.")
        private String username;
    }

    @Data
    @NoArgsConstructor
    public static class ChangeUserPasswordRequest {
        @ApiModelProperty(value="새 비밀번호", example = "**********", required = true)
        @NotEmpty(message = "새 비밀번호는 필수 값입니다.")
        private String newPassword;
        @ApiModelProperty(value="기존 비밀번호", example = "**********", required = true)
        @NotEmpty(message = "기존 비밀번호는 필수 값입니다.")
        private String oldPassword;
    }

    private static String makeFullName(String firstName, String lastName) {
        String nextFullName;
        if (WiniString.hasCjkCharacter(firstName) || WiniString.hasCjkCharacter(lastName)) {
            // 한글이 사용되었으면 성과 이름을 바꾸지 않고 성+이름으로 설정
            nextFullName = lastName + firstName;
        } else {
            // 한글이 없으면 이름 "공백" 성으로 설정
            nextFullName = (firstName + " " + lastName).trim();
        }
        return nextFullName;
    }
}
