package com.winitech.mfa;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * TOTP 기기 분실 대비 1회용 백업코드. 생성 시 평문은 사용자에게 한 번만 보여주고, 저장은 해시로만
 * 한다(system의 비밀번호 해싱과 동일하게 BCrypt 사용).
 */
@Component
public class BackupCodeGenerator {

    // 혼동되는 0/O, 1/I/L을 뺀 알파벳 - 사람이 직접 손으로 옮겨 적어도 틀리기 어렵게.
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 10;

    private final SecureRandom secureRandom = new SecureRandom();

    public List<String> generatePlainCodes(int count) {
        List<String> codes = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            codes.add(generateOne());
        }
        return codes;
    }

    private String generateOne() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(secureRandom.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public String hash(String plainCode) {
        return BCrypt.hashpw(normalize(plainCode), BCrypt.gensalt());
    }

    public boolean matches(String plainCode, String hash) {
        return BCrypt.checkpw(normalize(plainCode), hash);
    }

    private String normalize(String code) {
        return code == null ? "" : code.trim().toUpperCase().replace("-", "");
    }
}
