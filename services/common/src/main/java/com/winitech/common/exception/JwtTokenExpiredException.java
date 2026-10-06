package com.winitech.common.exception;

import com.winitech.common.response.ErrorCode;

/**
 * <pre>
 * com.winitech.common.exception
 * └ JwtTokenExpiredException.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-03 14:11
 **/
public class JwtTokenExpiredException extends BaseException {
	public JwtTokenExpiredException() {
		super(ErrorCode.COMMON_JWT_TOKEN_EXPIRED);
	}

	public JwtTokenExpiredException(String message) {
		super(message, ErrorCode.COMMON_JWT_TOKEN_EXPIRED);
	}
}
