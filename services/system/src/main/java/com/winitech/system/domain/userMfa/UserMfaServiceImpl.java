package com.winitech.system.domain.userMfa;

import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.exception.UnauthenticatedException;
import com.winitech.mfa.BackupCodeGenerator;
import com.winitech.mfa.MfaSecretCipher;
import com.winitech.mfa.TotpService;
import com.winitech.system.domain.user.User;
import com.winitech.system.domain.user.UserReader;
import com.winitech.system.domain.user.UserStore;
import com.winitech.system.domain.userMfaBackupCode.UserMfaBackupCode;
import com.winitech.system.domain.userMfaBackupCode.UserMfaBackupCodeReader;
import com.winitech.system.domain.userMfaBackupCode.UserMfaBackupCodeStore;
import com.winitech.system.domain.userMfaChallenge.UserMfaChallenge;
import com.winitech.system.domain.userMfaChallenge.UserMfaChallengeReader;
import com.winitech.system.domain.userMfaChallenge.UserMfaChallengeStore;
import com.winitech.system.domain.userSession.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.userMfa
 * └ UserMfaServiceImpl.java
 * </pre>
 * TOTP(Google/Microsoft Authenticator) 기반 2차 인증 - 개인 선택(opt-in). 실패해도 기존
 * 로그인 실패 잠금 카운트(login_log)에는 반영하지 않는다(의도된 정책 결정).
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class UserMfaServiceImpl implements UserMfaService {
    private static final int BACKUP_CODE_COUNT = 10;
    private static final String ISSUER = "WINITECH";

    private final UserReader userReader;
    private final UserStore userStore;
    private final UserMfaChallengeReader userMfaChallengeReader;
    private final UserMfaChallengeStore userMfaChallengeStore;
    private final UserMfaBackupCodeReader userMfaBackupCodeReader;
    private final UserMfaBackupCodeStore userMfaBackupCodeStore;
    private final TotpService totpService;
    private final MfaSecretCipher mfaSecretCipher;
    private final BackupCodeGenerator backupCodeGenerator;

    @Value("${winitech.mfa.challenge-ttl-seconds:180}")
    private int challengeTtlSeconds;

    // TOTP는 6자리(100만 경우의 수)뿐이라 시도횟수 제한이 없으면 챌린지 TTL 내에 무차별 대입이 가능하다.
    // 한도 초과 시 챌린지를 즉시 소비(폐기)시켜 재로그인(1차 인증)을 다시 거치도록 강제한다.
    @Value("${winitech.mfa.challenge-max-attempts:5}")
    private int challengeMaxAttempts;

    @Override
    @Transactional
    public UserMfaChallenge createChallenge(
            UUID userId,
            UUID organizationId,
            String loginIp,
            UserSession.IpSecurityStatus ipSecurityStatus,
            String sessionEncryptKey
    ) {
        UserMfaChallenge challenge = UserMfaChallenge.builder()
                .userId(userId)
                .organizationId(organizationId)
                .loginIp(loginIp)
                .ipSecurityStatus(ipSecurityStatus)
                .sessionEncryptKey(sessionEncryptKey)
                .expiresAt(OffsetDateTime.now().plusSeconds(challengeTtlSeconds))
                .build();

        return userMfaChallengeStore.store(challenge);
    }

    @Override
    @Transactional
    public UserMfaChallenge verifyChallenge(UUID challengeId, String code) {
        UserMfaChallenge challenge = userMfaChallengeReader.findById(challengeId)
                .orElseThrow(UnauthenticatedException::new);

        if (challenge.isConsumed() || challenge.isExpired() || challenge.isFailureLimitExceeded(challengeMaxAttempts)) {
            throw new UnauthenticatedException();
        }

        User user = userReader.getUser(challenge.getUserId());

        if (!verifyCode(user, code)) {
            int failCount = challenge.registerFailure();

            if (failCount >= challengeMaxAttempts) {
                // 시도 횟수 한도 초과 - 챌린지를 즉시 폐기하여 이 토큰으로는 더 이상 시도할 수 없게 한다.
                challenge.consume();
            }

            userMfaChallengeStore.store(challenge);
            throw new UnauthenticatedException();
        }

        challenge.consume();
        userMfaChallengeStore.store(challenge);

        return challenge;
    }

    private boolean verifyCode(User user, String code) {
        if (user.isTotpEnabled() && totpService.verifyCode(mfaSecretCipher.decrypt(user.getTotpSecretEncrypted()), code)) {
            return true;
        }

        return tryConsumeBackupCode(user.getId(), code);
    }

    private boolean tryConsumeBackupCode(UUID userId, String code) {
        List<UserMfaBackupCode> unusedCodes = userMfaBackupCodeReader.getUnusedBackupCodesByUserId(userId);

        for (UserMfaBackupCode backupCode : unusedCodes) {
            if (backupCodeGenerator.matches(code, backupCode.getCodeHash())) {
                backupCode.markUsed();
                userMfaBackupCodeStore.store(backupCode);
                return true;
            }
        }

        return false;
    }

    @Override
    public MfaEnrollResult startEnroll(UUID userId) {
        User user = userReader.getUser(userId);

        // 등록 중 이탈해도 로그인에 영향 없음 - totpEnabled는 confirmEnroll에서 코드 검증 후에만 켜진다.
        String secret = totpService.generateSecret();
        user.setTotpSecretEncrypted(mfaSecretCipher.encrypt(secret));
        userStore.store(user);

        String otpAuthUri = totpService.buildOtpAuthUri(ISSUER, user.getUsername(), secret);

        return MfaEnrollResult.builder()
                .otpAuthUri(otpAuthUri)
                .manualEntrySecret(secret)
                .build();
    }

    @Override
    @Transactional
    public List<String> confirmEnroll(UUID userId, String code) {
        User user = userReader.getUser(userId);

        if (user.getTotpSecretEncrypted() == null) {
            throw new IllegalStatusException("MFA 등록이 시작되지 않았습니다.");
        }

        String secret = mfaSecretCipher.decrypt(user.getTotpSecretEncrypted());

        if (!totpService.verifyCode(secret, code)) {
            throw new IllegalStatusException("인증 코드가 올바르지 않습니다.");
        }

        user.enableTotp(user.getTotpSecretEncrypted());
        userStore.store(user);

        return issueBackupCodes(userId);
    }

    @Override
    @Transactional
    public void disable(UUID userId, String code) {
        User user = userReader.getUser(userId);

        if (!verifyCode(user, code)) {
            // 이미 로그인된 사용자가 현재 세션에서 재확인하는 것이므로 UnauthenticatedException(401,
            // COMMON_UNAUTHENTICATED)을 쓰면 안 된다 - 프론트가 이 조합을 세션 만료로 해석해
            // 강제 재로그인시켜 버린다. confirmEnroll과 동일하게 단순 검증 실패로 처리한다.
            throw new IllegalStatusException("인증 코드가 올바르지 않습니다.");
        }

        user.disableTotp();
        userStore.store(user);
        userMfaBackupCodeStore.removeAllByUserId(userId);
    }

    @Override
    @Transactional
    public List<String> reissueBackupCodes(UUID userId) {
        return issueBackupCodes(userId);
    }

    private List<String> issueBackupCodes(UUID userId) {
        userMfaBackupCodeStore.removeAllByUserId(userId);

        List<String> plainCodes = backupCodeGenerator.generatePlainCodes(BACKUP_CODE_COUNT);

        List<UserMfaBackupCode> entities = plainCodes.stream()
                .map(plainCode -> UserMfaBackupCode.builder()
                        .userId(userId)
                        .codeHash(backupCodeGenerator.hash(plainCode))
                        .build())
                .collect(Collectors.toList());

        userMfaBackupCodeStore.storeAll(entities);

        return plainCodes;
    }
}
