package com.winitech.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
//    COMMON_SYSTEM_ERROR("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."), // 장애 상황
    COMMON_SYSTEM_ERROR("Internal server error. Please try again later."), // 장애 상황
//    COMMON_INVALID_PARAMETER("요청한 값이 올바르지 않습니다."),
    COMMON_INVALID_PARAMETER("Invalid Request error."),
//    COMMON_ENTITY_NOT_FOUND("존재하지 않는 엔티티입니다."),
    COMMON_ENTITY_NOT_FOUND("The entity does not exist."),
//    COMMON_ILLEGAL_STATUS("잘못된 상태값입니다.");
    COMMON_ILLEGAL_STATUS("The state value is invalid."),
    
    COMMON_UNAUTHENTICATED("Unauthenticated request."),
    COMMON_UNAUTHORIZED("Unauthorized request."),
    COMMON_INVALID_TENANT("Invalid tenant."),
    
    COMMON_RESTAPI_ENC_REQUIRED("RestAPI encryption required."),
    COMMON_RESTAPI_ENC_DISABLED("RestAPI encryption disabled."),
    COMMON_JWT_TOKEN_EXPIRED("JWT token expired.");

    private final String errorMsg;

    public String getErrorMsg(Object... arg) {
        return String.format(errorMsg, arg);
    }
}
