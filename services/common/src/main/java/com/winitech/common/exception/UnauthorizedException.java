package com.winitech.common.exception;

import com.winitech.common.response.ErrorCode;

/**
 * com.winitech.common.exception
 * └ AccessDeniedException.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/07
 **/
public class UnauthorizedException extends BaseException {
	public UnauthorizedException() {
		super(ErrorCode.COMMON_UNAUTHORIZED);
	}

	public UnauthorizedException(String message) {
		super(message, ErrorCode.COMMON_UNAUTHORIZED);
	}
}
