package com.winitech.common.interfaces.outboundAdapter.eventProducer;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.interfaces.outboundAdapter.eventProducer
 * └ CommonUserSessionBlockEventProducer.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-21 15:46
 **/
public interface CommonUserSessionBlockEventProducer {
	/**
	 * 사용자 토큰 차단 이벤트 발생
	 * @param userSessionId 사용자 세션 ID
	 * @param accessTokenExpiresAt 액세스 토큰만료일시
	 * @param userId 사용자 ID 
	 */
	void userSessionBlocked(UUID userSessionId, OffsetDateTime accessTokenExpiresAt, UUID userId);
}
