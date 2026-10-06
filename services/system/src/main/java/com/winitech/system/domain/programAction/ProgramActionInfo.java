package com.winitech.system.domain.programAction;

import lombok.Getter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 * com.winitech.system.domain.programAction
 * └ ProgramAction.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-14 10:39
 **/
@Getter
public class ProgramActionInfo {
	private final UUID id;
	private final UUID programId;
	private final ProgramAction.ActionType actionType;
	private final ProgramAction.AuthType authType;
	private final String uri;

	public ProgramActionInfo(ProgramAction programAction) {
		this.id = programAction.getId();
		this.programId = programAction.getProgram().getId();
		this.actionType = programAction.getActionType();
		this.authType = programAction.getAuthType();
		this.uri = programAction.getUri();
	}
}
