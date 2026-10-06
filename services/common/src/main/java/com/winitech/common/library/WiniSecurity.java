package com.winitech.common.library;

import com.winitech.common.bean.LoginUserContext;
import com.winitech.common.exception.*;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 암호화 및 보안 관련 공통 함수 모음 클래스입니다.
 * <pre>
 * com.winitech.common.library
 * └ WiniSecurity.java
 * </pre>
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/11/12
 **/
@Component
public class WiniSecurity {

	private static boolean isDevmode;

	@Value("${winitech.devmode:false}")
	public void setIsDevmode(boolean isDevmode) {
		WiniSecurity.isDevmode = isDevmode;
	}

	/**
	 * jwtSecret은 Base64로 디코드해 HMACSHA512 키 소재로 직접 사용되므로 최소 길이를 강제한다.
	 */
	private static final int MIN_JWT_SECRET_BYTES = 32;

	private static final Logger log = LoggerFactory.getLogger(WiniSecurity.class);

	private static final SecureRandom secureRandom = new SecureRandom();

	/**
	 * JWT 암호화/해시 키
	 */
	private static String jwtSecret;

	/**
	 * jwt secret 주입
	 * static인 변수에는 @Value 어노테이션을 사용할 수 없기 때문에 클래스를 @Component로 하고 setter를 사용하여 값을 주입 받습니다.
	 */
	@Value("${winitech.security.jwt.secret:}")
	private void setJwtSecret(String jwtSecret) {
		byte[] decoded;
		try {
			decoded = jwtSecret == null ? new byte[0] : Base64.getDecoder().decode(jwtSecret);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException(
					"winitech.security.jwt.secret(환경변수 WINITECH_JWT_SECRET)이 유효한 Base64 값이 아닙니다.");
		}

		if (decoded.length < MIN_JWT_SECRET_BYTES) {
			throw new IllegalStateException(
					"winitech.security.jwt.secret(환경변수 WINITECH_JWT_SECRET)이 설정되지 않았거나 너무 짧습니다. "
							+ "Base64로 인코딩된 " + MIN_JWT_SECRET_BYTES + "바이트 이상의 무작위 값이 필요합니다. "
							+ "예: openssl rand -base64 64");
		}

		WiniSecurity.jwtSecret = jwtSecret;

		if (jwtPbkdf2Iterations != null && jwtPbkdf2SaltLength != null) {
			initJwtSecretKey();
		}
	}

	private static String jwtIssuer;

	/**
	 * jwt issuer 설정값 주입
	 * static인 변수에는 @Value 어노테이션을 사용할 수 없기 때문에 클래스를 @Component로 하고 setter를 사용하여 값을 주입 받습니다.
	 */
	@Value("${winitech.security.jwt.issuer: null}")
	private void setJwtIssuer(String jwtIssuer) {
		WiniSecurity.jwtIssuer = jwtIssuer;
	}

	/**
	 * JWT 만료 시간 (초)
	 */
	private static Long jwtExpiresInSeconds;

	@Value("${winitech.security.jwt.expires-in-seconds}")
	private void setJwtExpiresInSeconds(Long jwtExpiresInSeconds) {
		WiniSecurity.jwtExpiresInSeconds = jwtExpiresInSeconds;
	}

	/**
	 * Refresh Token 길이
	 */
	private static Short refreshTokenLength;

	@Value("${winitech.security.refresh-token.length:108}")
	private void setRefreshTokenLength(Short refreshTokenLength) {
		WiniSecurity.refreshTokenLength = refreshTokenLength;
	}

	/**
	 * Refresh Token 만료 시간 (초)
	 */
	private static Integer refreshTokenExpiresInSeconds;

	@Value("${winitech.security.refresh-token.expires-in-seconds:2592000}")
	private void setRefreshTokenExpiresInSeconds(Integer refreshTokenExpiresInSeconds) {
		WiniSecurity.refreshTokenExpiresInSeconds = refreshTokenExpiresInSeconds;
	}

	/**
	 * JWT 암호화/해시 키 생성을 위한 SecretKey. PBKDF2 알고리즘을 사용하여 생성됩니다.
	 */
	private static SecretKey jwtSecretKey;

	/**
	 * JWT 암호화/해시 키 생성을 위한 PBKDF2 반복 횟수
	 */
	private static Integer jwtPbkdf2Iterations;

	@Value("${winitech.security.jwt.pbkdf2.iterations:20000}")
	private void setJwtPbkdf2Iterations(Integer jwtPbkdf2Iterations) {
		WiniSecurity.jwtPbkdf2Iterations = jwtPbkdf2Iterations;

		if (jwtSecret != null && jwtPbkdf2Iterations != null && jwtPbkdf2SaltLength != null) {
			initJwtSecretKey();
		}
	}

	/**
	 * JWT 암호화/해시 키 생성을 위한 PBKDF2 솔트 길이
	 */
	private static Integer jwtPbkdf2SaltLength;

	@Value("${winitech.security.jwt.pbkdf2.salt:16}")
	private void setJwtPbkdf2SaltLength(Integer jwtPbkdf2SaltLength) {
		if (jwtPbkdf2SaltLength == null || jwtPbkdf2SaltLength > 16) {
			jwtPbkdf2SaltLength = 16;
		}

		WiniSecurity.jwtPbkdf2SaltLength = jwtPbkdf2SaltLength;

		if (jwtSecret != null && jwtPbkdf2Iterations != null && jwtPbkdf2SaltLength != null) {
			initJwtSecretKey();
		}
	}

//	/**
//	 * JWT 암호화 사용 여부
//	 */
//	private static String jwtUseEncryptYn;
//
//	@Value("${winitech.security.jwt.use-encrypt-yn:Y}")
//	private void setJwtUseEncryptYn(String jwtUseEncryptYn) {
//		WiniSecurity.jwtUseEncryptYn = jwtUseEncryptYn;
//	}

	/**
	 * jwt secret key 생성
	 * jwtSecret과 jwtPbkdf2Iterations, jwtPbkdf2SaltLength가 모두 주입된 다음 jwtSecretKey를 생성합니다.
	 */
	private void initJwtSecretKey() {
		//jwtSecret 자체를 충분히 길고 랜덤하게 관리하는 게 이 복잡한 코드보다 훨씬 나음
		jwtSecretKey = new SecretKeySpec(Base64.getDecoder().decode(jwtSecret), "HMACSHA512");
	}

	/**
	 * JWT 토큰을 생성합니다.
	 * ※ winitech.security.jwt.use-encrypt-yn 설정값이 Y이면 JWT를 암호화하여 생성합니다.
	 * @param userSessionId 사용자 세션 ID
	 * @param claims 토큰 내용 (JWT Claims)
	 * @return JWT 토큰 문자열
	 */
	public static String createJwtToken(String userSessionId, Map<String, Object> claims) {
		Instant now = Instant.now();
		JwtBuilder jwtBuilder = Jwts.builder()
				//.header()
				//.keyId("wini")
				//.and()
				.issuer(jwtIssuer)
				.issuedAt(Date.from(now))
				.notBefore(Date.from(now.minusSeconds(10)))
				.expiration(Date.from(now.plusSeconds(jwtExpiresInSeconds)))
				.id(userSessionId);

		if (claims != null) {
			for (Map.Entry<String, Object> entry : claims.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();

				jwtBuilder = jwtBuilder.claim(key, value);
			}
		}


		jwtBuilder = jwtBuilder.encryptWith(jwtSecretKey, Jwts.ENC.A256CBC_HS512);

//		if ("Y".equals(jwtUseEncryptYn)) {
//			jwtBuilder = jwtBuilder.encryptWith(jwtSecretKey, Jwts.ENC.A256CBC_HS512);
//		} else {
//			jwtBuilder = jwtBuilder.signWith(jwtSecretKey, SignatureAlgorithm.HS512);
//		}

		return jwtBuilder.compact();
	}

	/**
	 * HTTP 리퀘스트 헤더의 JWT 토큰을 파싱하여 LoginUserContext를 생성합니다.
	 * request 헤더에 Authorization과 X-Org-Id가 포함되어 있어야 합니다.
	 * @param request HTTP 요청
	 * @return LoginUserContext 객체
	 */
	public static LoginUserContext parseLoginUserContext(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		String organizationId = request.getHeader("X-Org-Id");
		boolean skipValidation = false;

		skipValidation = request.getServletPath().matches("^/api/v[0-9]+/system/user/refreshToken$") && request.getMethod().equals("GET");

		return parseLoginUserContext(authorization, organizationId, skipValidation, WiniCom.getClientIp(request));
	}

	/**
	 * JWT 토큰을 파싱하여 LoginUserContext를 생성합니다.
	 * @param authorization HTTP 요청 헤더의 Authorization 값
	 * @param organizationId HTTP 요청 헤더의 X-Org-Id 값
	 * @return LoginUserContext 객체
	 */
	public static LoginUserContext parseLoginUserContext(String authorization, String organizationId) {
		return parseLoginUserContext(authorization, organizationId, false, null);
	}

	/**
	 * JWT 토큰을 파싱하여 LoginUserContext를 생성합니다.
	 * @param authorization HTTP 요청 헤더의 Authorization 값
	 * @param organizationId HTTP 요청 헤더의 X-Org-Id 값
	 * @param skipValidation JWT 유효성 검사를건너뛸지 여부
	 * @return LoginUserContext 객체
	 */
	public static LoginUserContext parseLoginUserContext(String authorization, String organizationId, boolean skipValidation, String clientIp) {
		// 소속된 조직에 대한것인지 체크
		boolean isValidOganization = false;

		LoginUserContext userContext = new LoginUserContext();

		if (log.isInfoEnabled()) {
			log.info("X-Org-Id: {}", organizationId);
		}

		if (authorization == null) {
			throw new UnauthenticatedException("Authorization token is required");
		} else {
			log.debug("Decoding JWT token...");

			if (authorization == null || authorization.isEmpty() || ! authorization.startsWith("Bearer") || authorization.replace("Bearer", "").trim().isEmpty()) {
				throw new UnauthenticatedException("Authorization token is required");
			}

			String jwt = authorization.replace("Bearer", "").trim();
			String[] jwtParts = jwt.split("\\.");
			String jwtHeader = new String(Base64.getDecoder().decode(jwtParts[0]));
			Claims decode;

			// jwtSecretKey로 서명되거나 암호화된 JWT 토큰만 처리하고 나머지는 무시

			if (jwtHeader.contains("A256CBC")) {
				// A256CBC-HS512로 암호화된 JWT 토큰
				Jwe<Claims> claims = Jwts.parser()
						.decryptWith(jwtSecretKey)
						.clockSkewSeconds(60*60*24*30)	// 실제 토큰 만료는 별도로 체크하여 Jwts에서는 30일간 유예를 두어 체크하지 않음
						.build()
						.parseEncryptedClaims(jwt);

				decode = claims.getPayload();
			} else {
				// HS512로 서명된 JWT 토큰
				Jws<Claims> claims = Jwts.parser()
						.verifyWith(jwtSecretKey)
						.clockSkewSeconds(60*60*24*30)	// 실제 토큰 만료는 별도로 체크하여 Jwts에서는 30일간 유예를 두어 체크하지 않음
						.build()
						.parseSignedClaims(jwt);

				decode = claims.getPayload();
			}

			if (! skipValidation) {
				checkJWTIsValid(decode);
			}

			log.debug("Decoding JWT token is successful");

			userContext.setAccessTokenExpiredAt(decode.getExpiration().toInstant().atOffset(ZoneOffset.UTC));

			String clientIpClaim = decode.get("cip", String.class);
			userContext.setUserIp(clientIp);

			if (!isDevmode && clientIp != null && clientIpClaim != null) {
				if (! clientIp.equals(clientIpClaim)) {
					throw new UnauthenticatedException("Ip address is changed");
				}
			}

			List<String> organizationIdClaim = decode.get("oid", List.class);
			List<String> organizationCodeClaim = decode.get("tid", List.class);

			userContext.setEncryptKey(decode.get("enc", String.class));

			if (organizationIdClaim != null) {
				log.debug("OrganizationIdClaim is not null.");

				log.debug("Count of organizationIdList : {}", organizationIdClaim.size());

				if (organizationCodeClaim != null) {
					log.debug("Count of organizationCodeList : {}", organizationCodeClaim.size());
				}

				if (organizationIdClaim != null) {
					log.debug("Find organization is available :", organizationId);

					for (int orgIdIndex = 0; orgIdIndex < organizationIdClaim.size(); orgIdIndex++) {
						String orgId = organizationIdClaim.get(orgIdIndex);

						if (organizationId.equals(orgId)) {
							String jti = decode.get("jti", String.class);
							userContext.setUserSessionId(UUID.fromString(jti));
							String uid = decode.get("uid", String.class);
							userContext.setUserId(UUID.fromString(uid));
							userContext.setOrganizationId(UUID.fromString(organizationId));

							String agc = decode.get("agc", String.class);
							userContext.setAuthGroupCode(agc);

							if (organizationCodeClaim != null && organizationCodeClaim.size() > 0) {
								if (organizationCodeClaim.size() != organizationIdClaim.size()) {
									throw new UnauthorizedException("Invalid organization id (organizationCodeClaim size not match)");
								}

								userContext.setOrganizationCode(organizationCodeClaim.get(orgIdIndex));
							}

							isValidOganization = true;

							break;
						}
					}
				}
			}
		}

		if (! isValidOganization) {
			throw new UnauthorizedException("Invalid organization id");
		}
		return userContext;
	}

	private static void checkJWTIsValid(Claims decode) {
		if (decode.getIssuer() == null || ! decode.getIssuer().equals(jwtIssuer)) {
			throw new UnauthenticatedException("Invalid authorization token (2)");
		}

		long currentTick = System.currentTimeMillis();

		if (decode.getExpiration() == null || decode.getExpiration().getTime() < currentTick) {
			throw new JwtTokenExpiredException();
		}

		if (decode.getNotBefore() == null || decode.getNotBefore().getTime() > currentTick) {
			throw new JwtTokenExpiredException();
		}
	}

	/**
	 * MD5 해시를 계산합니다.
	 * @param plainText 평문
	 * @return 해시 문자열
	 */
	public static String hashMd5(String plainText) {
		byte[] hashInBytes = hashMd5Bytes(plainText.getBytes(StandardCharsets.UTF_8), plainText.getBytes(StandardCharsets.UTF_8));

		StringBuilder sb = new StringBuilder();
		for (byte b : hashInBytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	private static byte[] hashMd5Bytes(byte[] plainBytes, byte[] salt) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt);

//			MessageDigest md = MessageDigest.getInstance("MD5");
			return md.digest(plainBytes);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private static Pattern organizationCodePattern = Pattern.compile("^[A-Z0-9-]{3,20}$");

	/**
	 * 조직 코드 형식이 유효한지 검사합니다.
	 * @param organizationCode 조직 코드
	 * @return 유효한 형식이면 true, 그렇지 않으면 false
	 */
	public static boolean isValidOrganizationCodeFormat(String organizationCode) {
		if (organizationCode == null) {
			return false;
		}

		try {
			return organizationCodePattern.matcher(organizationCode).matches();
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * 조직 코드 형식이 유효한지 검사하고, 유효하지 않으면 예외를 던집니다.
	 * @throws InvalidParamException 조직 코드 형식이 유효하지 않을 때	
	 * @param organizationCode 조직 코드
	 */
	public static void checkValidOrganizationCodeFormat(String organizationCode) {
		if (organizationCode == null || organizationCode.length() <= 3) {
			throw new InvalidParamException("Organization code is required and must be at least 3 characters long.");
		} else if (organizationCode.length() > 20) {
			throw new InvalidParamException("Organization code must be at most 20 characters long.");
		} else if (! isValidOrganizationCodeFormat(organizationCode)) {
			throw new InvalidParamException("Organization code format is invalid. Only uppercase letters, numbers, and hyphens are allowed.");
		}
	}
}
