package com.winitech.common.exception;

import com.winitech.common.response.ErrorCode;

/**
 * com.winitech.common.exception
 * └ InvalidTenantException.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/09
 **/
public class InvalidTenantException extends BaseException {
	public InvalidTenantException() {
		super(ErrorCode.COMMON_INVALID_TENANT);
	}

	public InvalidTenantException(String message) {
		super(message, ErrorCode.COMMON_INVALID_TENANT);
	}
}
