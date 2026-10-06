package com.winitech.common.domain.common;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonMenuActionInfo.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 12:39
 **/
@Getter
public class CommonMenuActionInfo {
	private UUID id;
	private UUID menuId;
	private UUID programId;
	private String programCode;
	private String actionType;
	private String authType;
	private String uri;
	private OffsetDateTime updateAt;

	public CommonMenuActionInfo() {
	}

	public CommonMenuActionInfo(CommonMenuAction commonMenuAction) {
		this.id = commonMenuAction.getId();
		this.menuId = commonMenuAction.getMenuId();
		this.programId = commonMenuAction.getProgramId();
		this.programCode = commonMenuAction.getProgramCode();
		this.actionType = commonMenuAction.getActionType();
		this.authType = commonMenuAction.getAuthType();
		this.uri = commonMenuAction.getUri();
		this.updateAt = commonMenuAction.getUpdateAt();
	}

	public CommonMenuActionInfo(UUID menuId, UUID programId, String programCode, String actionType, String authType, String uri) {
		this.id = null;
		this.menuId = menuId;
		this.programId = programId;
		this.programCode = programCode;
		this.actionType = actionType;
		this.authType = authType;
		this.uri = uri;
	}
}
