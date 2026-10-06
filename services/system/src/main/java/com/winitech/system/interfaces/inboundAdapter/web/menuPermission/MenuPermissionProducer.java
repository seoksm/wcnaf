package com.winitech.system.interfaces.inboundAdapter.web.menuPermission;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.winitech.common.domain.common.*;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.interfaces.inboundAdapter.web.menuPermission
 * └ MenuPermissionProducerImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 15:02
 **/
public interface MenuPermissionProducer {
	/**
	 * 메뉴 CDC 메시지 발생
	 * authorizationGroupIdList에서 지정한 권한그룹의 메뉴 권한을 commonAuthorizationGroupPermissionSyncMessageList의 권한그룹별 메뉴 권한정보로 대체 
	 * @param authorizationGroupIdList 권한그룹 ID 목록
	 * @param authorizationGroupPermissionInfoList 교체할 권한그룹별 메뉴 권한정보 목록   
	 */
	void authorizationGroupPermissionCdc(
			List<UUID> authorizationGroupIdList, 
			List<CommonAuthorizationGroupPermissionInfo> authorizationGroupPermissionInfoList
	);
}
