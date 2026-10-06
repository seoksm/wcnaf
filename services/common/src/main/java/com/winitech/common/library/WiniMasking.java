package com.winitech.common.library;

/**
 * 개인정보(PII) 마스킹 공통함수 모음 클래스입니다.
 * 이름, 이메일, 전화번호, 사용자명, 좌표 등 개인정보를 마스킹 처리합니다.
 * <pre>
 * com.winitech.common.library
 * └ WiniMasking.java
 * </pre>
 *
 * @author : coding (클라우드팀)
 * @since : 2026-03-18
 **/
public final class WiniMasking {

    private static final String DEFAULT_MASK = "***";

    private WiniMasking() {
        // 유틸리티 클래스 - 인스턴스 생성 방지
    }

    // ==================== 이름 마스킹 ====================

    /**
     * 이름(fullName) 마스킹
     * <pre><code>
     *     // 사용예제
     *     String masked = WiniMasking.maskName("홍길동");
     *     System.out.println(masked); // 홍*동
     *
     *     // 2글자
     *     WiniMasking.maskName("김수"); // 김*
     *
     *     // 4글자
     *     WiniMasking.maskName("제갈공명"); // 제**명
     * </code></pre>
     *
     * @param name 원본 이름
     * @return 마스킹된 이름
     */
    public static String maskName(String name) {
        if (name == null || name.isEmpty()) {
            return DEFAULT_MASK;
        }
        int len = name.length();
        if (len == 1) {
            return "*";
        }
        if (len == 2) {
            return name.charAt(0) + "*";
        }
        // 3글자 이상: 첫 글자 + 마스킹 + 마지막 글자
        return name.charAt(0) + "*".repeat(len - 2) + name.charAt(len - 1);
    }

    // ==================== 이메일 마스킹 ====================

    /**
     * 이메일 주소 마스킹
     * <pre><code>
     *     // 사용예제
     *     String masked = WiniMasking.maskEmail("test@example.com");
     *     System.out.println(masked); // t***@example.com
     * </code></pre>
     *
     * @param email 원본 이메일
     * @return 마스킹된 이메일
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return DEFAULT_MASK;
        }
        String[] parts = email.split("@");
        String local = parts[0];
        if (local.length() <= 1) {
            return local + "***@" + parts[1];
        }
        return local.charAt(0) + "***@" + parts[1];
    }

    // ==================== 전화번호 마스킹 ====================

    /**
     * 전화번호 마스킹 (가운데 번호 마스킹)
     * <pre><code>
     *     // 사용예제
     *     WiniMasking.maskPhone("010-1234-5678"); // 010-****-5678
     *     WiniMasking.maskPhone("01012345678");   // 010****5678
     *     WiniMasking.maskPhone("02-123-4567");   // 02-***-4567
     * </code></pre>
     *
     * @param phone 원본 전화번호
     * @return 마스킹된 전화번호
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return DEFAULT_MASK;
        }

        // 하이픈이 있는 경우
        if (phone.contains("-")) {
            String[] parts = phone.split("-");
            if (parts.length == 3) {
                return parts[0] + "-" + "*".repeat(parts[1].length()) + "-" + parts[2];
            }
            if (parts.length == 2) {
                return parts[0] + "-" + "*".repeat(parts[1].length());
            }
            return DEFAULT_MASK;
        }

        // 하이픈 없는 경우 (01012345678)
        int len = phone.length();
        if (len <= 4) {
            return "*".repeat(len);
        }
        // 앞 3자리 + 마스킹 + 뒤 4자리
        int maskLen = len - 7;
        if (maskLen <= 0) {
            maskLen = len - 4;
            return "*".repeat(len - 4) + phone.substring(len - 4);
        }
        return phone.substring(0, 3) + "*".repeat(maskLen) + phone.substring(len - 4);
    }

    // ==================== 사용자명(로그인 아이디) 마스킹 ====================

    /**
     * 사용자명(로그인 아이디) 마스킹
     * <pre><code>
     *     // 사용예제
     *     WiniMasking.maskUsername("testuser"); // te****er
     *     WiniMasking.maskUsername("admin001"); // ad***01
     *     WiniMasking.maskUsername("abc");      // a*c
     * </code></pre>
     *
     * @param username 원본 사용자명
     * @return 마스킹된 사용자명
     */
    public static String maskUsername(String username) {
        if (username == null || username.isEmpty()) {
            return DEFAULT_MASK;
        }
        int len = username.length();
        if (len == 1) {
            return "*";
        }
        if (len == 2) {
            return username.charAt(0) + "*";
        }
        if (len <= 4) {
            return username.charAt(0) + "*".repeat(len - 2) + username.charAt(len - 1);
        }
        // 5글자 이상: 앞 2자리 + 마스킹 + 뒤 2자리
        return username.substring(0, 2) + "*".repeat(len - 4) + username.substring(len - 2);
    }

    // ==================== 좌표 마스킹 ====================

    /**
     * 좌표(위도/경도) 마스킹 — 소수점 이하 3자리까지만 노출
     * <pre><code>
     *     // 사용예제
     *     WiniMasking.maskCoordinate("37.123456");  // 37.123***
     *     WiniMasking.maskCoordinate("127.001234"); // 127.001***
     * </code></pre>
     *
     * @param coordinate 원본 좌표 문자열
     * @return 마스킹된 좌표
     */
    public static String maskCoordinate(String coordinate) {
        if (coordinate == null || coordinate.isEmpty()) {
            return DEFAULT_MASK;
        }
        int dotIndex = coordinate.indexOf('.');
        if (dotIndex < 0) {
            // 소수점이 없는 경우
            return coordinate + "***";
        }
        // 소수점 이하 3자리까지만 노출
        int endIndex = Math.min(dotIndex + 4, coordinate.length());
        return coordinate.substring(0, endIndex) + "***";
    }

    /**
     * 좌표(위도/경도) 마스킹 — double 타입
     *
     * @param coordinate 원본 좌표
     * @return 마스킹된 좌표 문자열
     */
    public static String maskCoordinate(double coordinate) {
        return maskCoordinate(String.valueOf(coordinate));
    }

    // ==================== 주소 마스킹 ====================

    /**
     * 주소 마스킹 — 상세주소 부분 마스킹
     * <pre><code>
     *     // 사용예제
     *     WiniMasking.maskAddress("서울특별시 강남구 테헤란로 123"); // 서울특별시 강남구 ***
     *     WiniMasking.maskAddress("경기도 수원시 팔달구");          // 경기도 수원시 ***
     * </code></pre>
     *
     * @param address 원본 주소
     * @return 마스킹된 주소
     */
    public static String maskAddress(String address) {
        if (address == null || address.isEmpty()) {
            return DEFAULT_MASK;
        }
        // 공백으로 분리하여 앞 2개 단어만 노출
        String[] parts = address.split("\\s+");
        if (parts.length <= 2) {
            return address;
        }
        return parts[0] + " " + parts[1] + " ***";
    }

    // ==================== 주민등록번호 마스킹 ====================

    /**
     * 주민등록번호 마스킹
     * <pre><code>
     *     // 사용예제
     *     WiniMasking.maskSsn("900101-1234567"); // 900101-*******
     *     WiniMasking.maskSsn("9001011234567");  // 900101*******
     * </code></pre>
     *
     * @param ssn 원본 주민등록번호
     * @return 마스킹된 주민등록번호
     */
    public static String maskSsn(String ssn) {
        if (ssn == null || ssn.isEmpty()) {
            return DEFAULT_MASK;
        }
        if (ssn.contains("-")) {
            String[] parts = ssn.split("-");
            if (parts.length == 2) {
                return parts[0] + "-" + "*".repeat(parts[1].length());
            }
        }
        if (ssn.length() >= 7) {
            return ssn.substring(0, 6) + "*".repeat(ssn.length() - 6);
        }
        return ssn.charAt(0) + "*".repeat(ssn.length() - 1);
    }
}
