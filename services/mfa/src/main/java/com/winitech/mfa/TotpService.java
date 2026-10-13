package com.winitech.mfa;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.stereotype.Component;

/**
 * TOTP(RFC 6238) 비밀키 생성·otpauth:// URI 생성·코드 검증.
 * Google Authenticator/Microsoft Authenticator 등 표준 OTP 앱과 그대로 호환된다 - 별도의
 * MFA 서버(privacyIDEA 등) 없이 이 서비스만으로 2차 인증을 완결한다.
 */
@Component
public class TotpService {

    private static final int PERIOD_SECONDS = 30;
    private static final int DIGITS = 6;
    // 서버-휴대폰 시계가 다소 어긋나도 인증되도록 앞뒤 1스텝(±30초)까지 허용한다.
    private static final int ALLOWED_TIME_PERIOD_DISCREPANCY = 1;

    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final CodeVerifier codeVerifier;

    public TotpService() {
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        TimeProvider timeProvider = new SystemTimeProvider();
        DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        verifier.setTimePeriod(PERIOD_SECONDS);
        verifier.setAllowedTimePeriodDiscrepancy(ALLOWED_TIME_PERIOD_DISCREPANCY);
        this.codeVerifier = verifier;
    }

    public String generateSecret() {
        return secretGenerator.generate();
    }

    /**
     * Authenticator 앱이 QR로 스캔할 otpauth:// URI. QR 이미지 자체는 프론트엔드가 이 문자열로
     * 그린다(자산 QR 라벨 기능에서 이미 쓰는 qrcode 패키지 재사용, 서버에 이미지 생성 라이브러리 불필요).
     */
    public String buildOtpAuthUri(String issuer, String accountLabel, String secret) {
        QrData data = new QrData.Builder()
                .label(accountLabel)
                .secret(secret)
                .issuer(issuer)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(DIGITS)
                .period(PERIOD_SECONDS)
                .build();
        return data.getUri();
    }

    public boolean verifyCode(String secret, String code) {
        if (secret == null || code == null || code.isBlank()) {
            return false;
        }
        return codeVerifier.isValidCode(secret, code);
    }
}
