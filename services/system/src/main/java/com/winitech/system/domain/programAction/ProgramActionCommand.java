package com.winitech.system.domain.programAction;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.programAction
 * └ ProgramAction.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-14 10:39
 **/
@Getter
@Builder
@ToString
public class ProgramActionCommand {
	private final UUID id;
	private final ProgramAction.ActionType actionType;
	private final ProgramAction.AuthType authType;
	private final String uri;

	public ProgramAction toEntity() {
		return ProgramAction.builder()
			.id(id)
			.actionType(actionType)
			.authType(authType)
			.uri(uri)
			.build();
	}

	@Getter
	@Builder
	@ToString
	public static class RegisterRequestCommand {
		private final ProgramAction.ActionType actionType;
		private final ProgramAction.AuthType authType;
		private final String uri;
	}

	@Getter
	@Builder
	@ToString
	public static class ModifyRequestCommand {
		private final ProgramAction.ActionType actionType;
		private final ProgramAction.AuthType authType;
		private final String uri;
	}
}
