package com.winitech.common.domain.common;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationUtilService.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-26 14:02
 **/
public interface CommonAuthorizationUtilService {
	boolean hasMenuPermission(UUID userId, UUID menuId, String actionType, String authType, String canonicalizedUri);
	
	boolean hasProgramPermission(UUID userId, String programCode, String actionType, String authType, String canonicalizedUri);
}
