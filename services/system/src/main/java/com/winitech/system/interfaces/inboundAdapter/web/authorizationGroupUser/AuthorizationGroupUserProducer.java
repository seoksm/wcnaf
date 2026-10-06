package com.winitech.system.interfaces.inboundAdapter.web.authorizationGroupUser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.winitech.common.domain.common.CommonAuthorizationGroupUserInfo;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.interfaces.inboundAdapter.web.authorizationGroupUser
 * └ AuthorizationGroupUserProducer.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 14:32
 **/
public interface AuthorizationGroupUserProducer {
	/**
	 * 권한그룹 사용자 CDC 메시지 발생
	 * authorizationGroupIdList에서 지정한 권한그룹을 authorizationGroupUserInfoList로 대체 
	 * @param authorizationGroupIdList 교체할 권한그룹 ID 목록           
	 * @param authorizationGroupUserInfoList 교체할 권한그룹 사용자 정보 목록
	 */
	void authorizationGroupUserCdc(
			List<UUID> authorizationGroupIdList,
			List<CommonAuthorizationGroupUserInfo> authorizationGroupUserInfoList
	);
}
