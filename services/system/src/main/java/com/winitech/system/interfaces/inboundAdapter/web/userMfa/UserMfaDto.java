package com.winitech.system.interfaces.inboundAdapter.web.userMfa;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

public class UserMfaDto {
    private UserMfaDto() {
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CodeRequest {
        @ApiModelProperty(value = "OTP 앱 6자리 코드", example = "123456", required = true)
        @NotEmpty(message = "코드는 필수 값입니다.")
        private String code;
    }
}
