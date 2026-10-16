package com.winitech.system.domain.userMfa;

import com.winitech.system.domain.userMfaChallenge.UserMfaChallenge;
import com.winitech.system.domain.userSession.UserSession;

import java.util.List;
import java.util.UUID;

public interface UserMfaService {
    /** 로그인 1단계(비밀번호) 통과 직후 호출한다 - MFA 대기 challenge를 발급한다. */
    UserMfaChallenge createChallenge(
            UUID userId,
            UUID organizationId,
            String loginIp,
            UserSession.IpSecurityStatus ipSecurityStatus,
            String sessionEncryptKey
    );

    /**
     * 2단계(OTP 또는 백업코드) 검증. 통과하면 challenge를 소비 처리하고 그대로 반환한다
     * (호출부가 여기 담긴 userId/organizationId 등으로 로그인을 완료시킨다).
     * 실패/만료/이미 소비된 challenge는 전부 동일하게 UnauthenticatedException을 던진다
     * (어느 실패인지 노출하지 않음 - 기존 로그인 실패 처리와 동일한 원칙).
     */
    UserMfaChallenge verifyChallenge(UUID challengeId, String code);

    MfaEnrollResult startEnroll(UUID userId);

    /** 등록 확정. 코드 검증 성공 시 totpEnabled=true로 전환하고 백업코드 10개를 발급해 평문으로 1회 반환한다. */
    List<String> confirmEnroll(UUID userId, String code);

    /** 본인의 현재 TOTP 코드(또는 백업코드) 재확인 후에만 해제한다 - 탈취된 세션만으로 2차 인증을 영구히 제거하는 것을 막는다. */
    void disable(UUID userId, String code);

    /** 관리자 승인 하에만 호출되는 백업코드 재발급 - 기존 코드는 전부 무효화된다. */
    List<String> reissueBackupCodes(UUID userId);
}
