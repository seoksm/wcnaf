package com.winitech.common.domain.common;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonUserStore.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-21 12:44
 **/
public interface CommonUserStore {
	CommonUser store(CommonUser commonUser);

	CommonUser modify(CommonUser commonUser, CommonUserCommand command);
}
