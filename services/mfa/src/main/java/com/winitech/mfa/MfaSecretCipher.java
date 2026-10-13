package com.winitech.mfa;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * TOTP 비밀키 등 MFA 민감정보를 AES-256-GCM으로 암복호화한다.
 * <p>
 * 키는 {@code winitech.mfa.secret-key}(환경변수 {@code WINITECH_MFA_SECRET_KEY})로 주입되며,
 * Base64 디코드 후 32바이트 미만이면 빈약한 키로 조용히 뜨는 것을 막기 위해 애플리케이션 기동
 * 자체를 즉시 실패시킨다 - common의 {@code ExcelRowSignerImpl}과 동일한 fail-fast 패턴.
 */
@Component
public class MfaSecretCipher {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_BYTES = 12;
    private static final int GCM_TAG_BITS = 128;
    private static final int MIN_KEY_BYTES = 32;

    private final SecretKeySpec key;
    private final SecureRandom secureRandom = new SecureRandom();

    public MfaSecretCipher(@Value("${winitech.mfa.secret-key:}") String secretKeyBase64) {
        byte[] keyBytes;
        try {
            keyBytes = secretKeyBase64 == null ? new byte[0] : Base64.getDecoder().decode(secretKeyBase64);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "winitech.mfa.secret-key(환경변수 WINITECH_MFA_SECRET_KEY)가 유효한 Base64 값이 아닙니다.");
        }

        if (keyBytes.length < MIN_KEY_BYTES) {
            throw new IllegalStateException(
                    "winitech.mfa.secret-key(환경변수 WINITECH_MFA_SECRET_KEY)가 설정되지 않았거나 너무 짧습니다. "
                            + "Base64로 인코딩된 " + MIN_KEY_BYTES + "바이트(AES-256) 키가 필요합니다. 예: openssl rand -base64 32");
        }

        this.key = new SecretKeySpec(keyBytes, 0, 32, "AES");
    }

    public String encrypt(String plaintext) {
        try {
            byte[] iv = new byte[GCM_IV_BYTES];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            byte[] ivAndCipherText = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, ivAndCipherText, 0, iv.length);
            System.arraycopy(cipherText, 0, ivAndCipherText, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(ivAndCipherText);
        } catch (Exception e) {
            throw new IllegalStateException("MFA 비밀정보 암호화 중 오류가 발생했습니다.", e);
        }
    }

    public String decrypt(String encoded) {
        try {
            byte[] ivAndCipherText = Base64.getDecoder().decode(encoded);
            byte[] iv = new byte[GCM_IV_BYTES];
            byte[] cipherText = new byte[ivAndCipherText.length - GCM_IV_BYTES];
            System.arraycopy(ivAndCipherText, 0, iv, 0, GCM_IV_BYTES);
            System.arraycopy(ivAndCipherText, GCM_IV_BYTES, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] plainBytes = cipher.doFinal(cipherText);

            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("MFA 비밀정보 복호화 중 오류가 발생했습니다.", e);
        }
    }
}
