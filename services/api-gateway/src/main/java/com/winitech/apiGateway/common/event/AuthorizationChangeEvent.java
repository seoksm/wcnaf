package com.winitech.apiGateway.common.event;

import org.springframework.context.ApplicationEvent;

/**
 * 권한관련 변경 이벤트.
 * 권한별 사용자, 권한별 메뉴 권한, 메뉴별 액션 목록 변경시 발생
 * <pre>
 * com.winitech.apiGateway.common.event
 * └ AuthorizationChangeEvent.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-03 11:15
 **/
public class AuthorizationChangeEvent extends ApplicationEvent {
	public AuthorizationChangeEvent(Object source) {
		super(source);
	}
}
