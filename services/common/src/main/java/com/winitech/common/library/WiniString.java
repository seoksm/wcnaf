package com.winitech.common.library;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotNull;
import java.util.regex.Pattern;

/**
 * 백엔드 프레임워크에서 사용하는 문자열처리 공통함수 모음 클래스입니다.
 * <pre>
 * com.winitech.common.library
 * └ WiniString.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-07 10:39
 **/
@Slf4j
public class WiniString {
	/**
	 * 문자열을 소문자로 변환합니다.
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     String str = "Hello World!";
	 *     String lowerStr = WiniString.lower(str);
	 *     System.out.println(lowerStr); // hello world!
	 * </code></pre>
	 * @param str 변환할 문자열
	 * @return 소문자로 변환된 문자열
	 */
	@NonNull
	public static String lower(String str) {
		if (str == null) {
			return "";
		}
		
		return str.toLowerCase();
	}

	/**
	 * 문자열을 대문자로 변환합니다.
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     String str = "Hello World!";
	 *     String upperStr = WiniString.upper(str);
	 *     System.out.println(upperStr); // HELLO WORLD!
	 * </code></pre>
	 * @param str 변환할 문자열
	 * @return 대문자로 변환된 문자열
	 */
	@NonNull
	public static String upper(String str) {
		if (str == null) {
			return "";
		}
		
		return str.toUpperCase();
	}

	/**
	 * 문자열의 앞 뒤 공백을 제거합니다.
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     String str = "   Hello World!   ";
	 *     String trimmedStr = WiniString.trim(str);
	 *     System.out.println(trimmedStr); // Hello World!
	 * </code></pre>
	 * @param str 문자열
	 * @return 앞 뒤 공백이 제거된 문자열
	 */
	@NonNull
	public static String trim(String str) {
		if (str == null) {
			return "";
		}
		
		return str.trim();
	}

	private static final Pattern CJK_PATTERN = Pattern.compile(
			"[\\p{IsHan}\\p{IsHiragana}\\p{IsKatakana}\\p{IsHangul}]"
	);
	
	/**
	 * 한글, 일본어, 한자 등이 포함되어 있는지 확인
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     // 한글, 일본어, 한자 등이 포함되어 있는 경우
	 *     String str = "안녕하세요. Hello, こんにちは, 你好!";
	 *     boolean hasCjk = WiniString.hasCjkCharacter(str);
	 *     System.out.println(hasCjk); // true
	 *
	 *     // 한글, 일본어, 한자 등이 포함되어 있지 않은 경우
	 *     String str2 = "Hello!";
	 *     boolean hasCjk2 = WiniString.hasCjkCharacter(str2);
	 *     System.out.println(hasCjk2); // false
	 * </code></pre>
	 * @param str 문자열
	 * @return true: 포함되어 있음, false: 포함되어 있지 않음
	 */
	public static boolean hasCjkCharacter(String str) {
		if (str == null) {
			return false;
		}
		
		return CJK_PATTERN.matcher(str).find();
	}
	
	private static final Pattern UUID_PATTERN = Pattern.compile(
			"^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
	);
	
	/**
	 * UUID 형식인지 확인
	 * 
	 * <pre><code>
	 *     // 사용예제
	 *     String uuid = "123e4567-e89b-12d3-a456-426614174000";
	 *     boolean isUuid = WiniString.isUuid(uuid);
	 *     System.out.println(isUuid); // true
	 *     
	 *     // UUID 형식이 아닌 경우
	 *     String notUuid = "12345678-1234-1234-1234-1234567890123";
	 *     boolean isNotUuid = WiniString.isUuid(notUuid);
	 *     System.out.println(isNotUuid); // false
	 * </code></pre>
	 * @param str 문자열
	 * @return true: UUID 형식, false: UUID 형식 아님
	 */
	public static boolean isUuid(String str) {
		if (str == null) {
			return false;
		}
		
		return UUID_PATTERN.matcher(str).matches();
	}

	/**
	 * 문자열을 반복해서 생성
	 * @param str 문자열
	 * @param count 반복 횟수
	 * @return
	 */
	public static String repeat(String str, int count) {
		// java 11 이상부터는 str.repeat(count) 사용 가능하지만 Java 8 호환성을 위해 별도로 구현
		
		if (str == null || count <= 0) {
			return "";
		}
		
		StringBuilder result = new StringBuilder();
		
		for (; count > 0; count--) {
			result.append(str);
		}
		
		return result.toString();
	}

	/**
	 * 문자열을 지정된 길이로 잘라내고, 잘린 부분에 ...를 붙임
	 * @param str 문자열
	 * @param length 문자열을 자를 길이
	 * @return
	 */
	public static String cut(String str, int length) {
		return cut(str, length, "...");
	}

	/**
	 * 문자열을 지정된 길이로 잘라내고, 잘린 부분에 지정된 문자열을 붙임
	 * @param str 문자열
	 * @param length 문자열을 자를 길이
	 * @param ellipsis 잘린 부분에 붙일 문자열
	 * @return
	 */
	public static String cut(String str, int length, @NotNull String ellipsis) {
		if (str == null) {
			return str;
		}
		
		if (str.length() < length) {
			return str;
		}
		
		if (length < ellipsis.length()) {
			return str.substring(0, length);
		}
		
		return str.substring(0, length - ellipsis.length()) + ellipsis;
	}
	
	/**
	 * 숫자만 포함되어 있는지 확인
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		if (str == null) {
			return false;
		}
		
		return str.matches("^[0-9]+$");
	}

	private static final Pattern phoneCheckPattern = Pattern.compile("^(070|02|0[3-9]{1}[0-9]{1}|00[5-6]{1}[0-9]{1,2})([0-9]{3,4})([0-9]{4})$");
	private static final Pattern phoneCheckAltPattern = Pattern.compile("(0[3-9]{1}[0-9]{1})([0-9]{3,4})([0-9]{4})$");

	/**
	 * 전화번호 형식 체크
	 * @param phone '01011111111' or '010-1111-1111'
	 * @return boolean
	 */
	public static boolean phoneCheck(String phone) {
		phone = phone.replaceAll("-", "");
		if (phoneCheckPattern.matcher(phone).matches()) {
			return true;
		} else {
			return phoneCheckAltPattern.matcher(phone).matches();
		}
	}

	private static final Pattern mobileRegExp = Pattern.compile("(01[016789])([1-9]{1}[0-9]{2,3})([0-9]{4})$");

	/**
	 * 휴대폰 번호 형식 체크
	 * @param phone '01011111111' or '010-1111-1111'
	 * @return boolean
	 */
	public static boolean mobileCheck(String phone) {
		phone = phone.replaceAll("-", "");
		return mobileRegExp.matcher(phone).matches();
	}

	private static final Pattern emailRegExp = Pattern.compile("^[A-Za-z0-9_.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+");

	/**
	 * 이메일 형식 체크
	 * @param email
	 * @return boolean
	 */
	public static boolean emailCheck(String email) {
		return emailRegExp.matcher(email).matches();
	}
}
