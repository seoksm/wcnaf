package com.winitech.common.interfaces.outboundAdapter.eventProducer;

/**
 * <pre>
 * com.winitech.common.interfaces.outboundAdapter.eventProducer
 * └ CommonAuthorizationDataChangeEventProducer.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-09 09:29
 **/
public interface CommonAuthorizationDataChangeEventProducer {
	/**
	 * 권한데이터 변경 이벤트 발생
	 * @param message 메시지
	 */
	void authorizationDataChanged(String message);
}
