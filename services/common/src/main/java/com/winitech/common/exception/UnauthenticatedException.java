package com.winitech.common.exception;

import com.winitech.common.response.ErrorCode;

/**
 * com.winitech.common.exception
 * └ UnauthenticatedException.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/12
 **/
public class UnauthenticatedException extends BaseException {
	public UnauthenticatedException() {
		super(ErrorCode.COMMON_UNAUTHENTICATED);
	}

	public UnauthenticatedException(String message) {
		super(message, ErrorCode.COMMON_UNAUTHENTICATED);
	}
}
