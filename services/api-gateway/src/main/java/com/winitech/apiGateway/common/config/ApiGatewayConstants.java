package com.winitech.apiGateway.common.config;

/**
 * <pre>
 * com.winitech.apiGateway.common.config
 * └ Constants.java
 * </pre>
 * 
 * @author : coding (클라우드팀)
 * @since : 2025-04-09 11:14
 **/
public class ApiGatewayConstants {
	public static final String[] EXCLUDE_PATHS = {
			"/api/v1/system/commonSecurity/generateKeyPair",

			"/api/v1/system/user/login",
			"/api/v1/system/user/login/admin",
			"/api/v1/system/user/secondAuth",
			"/api/v1/system/user/logout",
			"/api/v1/system/user/join",
			"/api/v1/system/user/refreshToken",
			"/api/v1/system/commonSecurity/startRestApiEnc", // 암호화 시작 API
			"/api/v1/system/user/passwordReset/requestCode",
			"/api/v1/system/user/passwordReset/verifyAndReset",

			// 첨부파일 다운로드와 미리보기는 파일 ID를 서명하여 별도로 처리
			"/api/v1/*/commonFile/download/**",
			"/api/v1/*/commonFile/preview/**",

			// 이미지 AI 업로드는 임시로 로그인 해제
			"/api/v1/collection/rtu/transfer/water-level-image",

			// sms 수신 api webhook은 로그인 해제
			"/api/v1/sms/twilio/webHook",

			// 개발용 swagger 페이지는 권한체크 X
			"/api/v1/*/swagger-ui/**",
			"/api/v1/*/swagger-resources/**",
			"/api/v1/*/v2/api-docs/**",
			"/api/v1/*/v3/api-docs/**"
	};
}
