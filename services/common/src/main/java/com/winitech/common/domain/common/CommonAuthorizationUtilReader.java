package com.winitech.common.domain.common;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationUtilReader.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-26 14:08
 **/
public interface CommonAuthorizationUtilReader {
    List<CommonMenuAction> getActionListByMenuId(UUID userId, String extraGroupCode, UUID menuId, String actionType, String authType);

    List<CommonMenuAction> getActionListByProgramCode(UUID userId, String extraGroupCode, String programCode, String actionType, String authType);
}