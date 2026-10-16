package com.winitech.system.interfaces.inboundAdapter.web.userMfa;

import com.winitech.common.annotation.ForceDefaultTenant;
import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.response.CommonResponse;
import com.winitech.system.domain.userMfa.MfaEnrollResult;
import com.winitech.system.domain.userMfa.UserMfaService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.interfaces.inboundAdapter.web.userMfa
 * └ UserMfaApiController.java
 * </pre>
 * MFA(TOTP) 등록/해제 - 전부 본인 계정 대상(opt-in), 로그인된 사용자만 호출 가능.
 * 관리자 전용 백업코드 재발급(/admin/...)은 일반 인가 체계(메뉴 권한)로 관리자에게만
 * 노출되는 화면에서만 호출되도록 운영한다 - 컨트롤러 안에서 역할을 별도로 체크하지 않는다
 * (이 프로젝트의 인가 모델 자체가 역할(enum)이 아니라 메뉴/프로그램 단위 권한그룹 방식이다).
 **/
@Api(tags = "사용자 MFA(TOTP) API")
@Slf4j
@RestController
@RequiredArgsConstructor
@ForceDefaultTenant
@RequestMapping("/api/v1/system/user/mfa")
public class UserMfaApiController {
    private final UserMfaService userMfaService;
    private final LoginUserContext loginUserContext;

    /** 등록 시작 - secret을 발급해 otpauth URI(QR용)와 수동입력용 평문키를 반환한다. */
    @PostMapping("/enroll")
    public CommonResponse<MfaEnrollResult> enroll() {
        return CommonResponse.success(userMfaService.startEnroll(loginUserContext.getUserId()));
    }

    /** 등록 확정 - 앱에 뜬 코드 1회 검증 후 활성화하고, 백업코드 10개를 평문으로 1회 반환한다. */
    @PostMapping("/enroll/confirm")
    public CommonResponse<List<String>> confirmEnroll(@RequestBody @Valid UserMfaDto.CodeRequest request) {
        return CommonResponse.success(userMfaService.confirmEnroll(loginUserContext.getUserId(), request.getCode()));
    }

    /**
     * 본인의 MFA를 해제한다 (기존 백업코드도 전부 폐기됨). 탈취된 세션만으로 2차 인증을 영구히
     * 제거하는 것을 막기 위해 현재 TOTP 코드(또는 백업코드) 재확인을 요구한다.
     */
    @PostMapping("/disable")
    public CommonResponse disable(@RequestBody @Valid UserMfaDto.CodeRequest request) {
        userMfaService.disable(loginUserContext.getUserId(), request.getCode());
        return CommonResponse.success("OK");
    }

    /**
     * 관리자가 특정 사용자의 백업코드를 재발급한다(기기 분실 등 본인 요청을 받아 관리자가 대신 처리).
     * 재발급된 평문 코드는 관리자가 본인 확인 후 당사자에게 안전하게 전달해야 한다.
     */
    @PostMapping("/admin/users/{userId}/backupCodes/reissue")
    public CommonResponse<List<String>> reissueBackupCodes(@PathVariable UUID userId) {
        return CommonResponse.success(userMfaService.reissueBackupCodes(userId));
    }
}
