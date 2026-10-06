package com.winitech.common.domain.common;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonMenuActionCommand.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 12:39
 **/
@Getter
@Builder
@ToString
public class CommonMenuActionCommand {
	private final UUID id;
	private final UUID menuId;
	private final UUID programId;
	private final String programCode;
	private final String actionType;
	private final String authType;
	private final String uri;

	public CommonMenuAction toEntity() {
		return CommonMenuAction.builder()
				.id(id)
				.menuId(menuId)
				.programId(programId)
				.programCode(programCode)
				.actionType(actionType)
				.authType(authType)
				.uri(uri)
				.build();
	}
}
