package com.winitech.system.domain.userMfa;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MfaEnrollResult {
    /** Authenticator 앱이 QR로 스캔할 otpauth:// URI. QR 이미지는 프론트엔드가 그린다. */
    private final String otpAuthUri;
    /** 카메라를 못 쓸 때 수동으로 입력할 수 있는 평문 비밀키. */
    private final String manualEntrySecret;
}
