package com.winitech.system.interfaces.inboundAdapter.web.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.winitech.system.domain.userOrganization.UserOrganization;
import com.winitech.system.interfaces.inboundAdapter.web.userOrganization.UserOrganizationDto;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserTokenDto {
    String accessToken;
    String tokenType;
    OffsetDateTime lastLoginAt;
    String lastLoginIp;
    String userId;
    String firstName;
    String lastName;
    String fullName;
    String departmentName;
    List<String> departmentNames;
    String dutyName;
    /**
     * 비밀번호 만료일 (비밀번호가 만료되지 않으면 null)
     * 0이면 당일까지 유효, 0 미만이면 만료된것 (예) -1, -100), 0 초과이면 해당 일수까지 유효
     */
    Integer passwordValidDays;
    
    @JsonIgnore // refreshToken은 사용자에게 반환하지 않음
    String refreshToken;
    
    @JsonIgnore // accessTokenExpiresIn은 사용자에게 반환하지 않음
    Integer refreshTokenExpiresIn;
    
    @JsonIgnore // userSessionId는 사용자에게 반환하지 않음
    UUID userSessionId;
    
    List<UserOrganizationDto.UserOrganizationLoginResponse> userOrganizationList;
}
