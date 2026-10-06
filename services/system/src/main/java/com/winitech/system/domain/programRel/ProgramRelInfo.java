package com.winitech.system.domain.programRel;

import lombok.Getter;

/**
 * <pre>
 * com.winitech.system.domain.programRel
 * └ ProgramRelInfo.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-23 09:05
 **/
@Getter
public class ProgramRelInfo {
	private final String id;
	private final String programId;
	private final String relProgramId;
	private final String relProgramCode;
	private final String relProgramName;

	public ProgramRelInfo(ProgramRel programRel) {
		this.id = programRel.getId().toString();
		this.programId = programRel.getProgram().getId().toString();
		this.relProgramId = programRel.getRelProgram().getId().toString();
		this.relProgramCode = programRel.getRelProgram().getProgramCode();
		this.relProgramName = programRel.getRelProgram().getProgramName();
	}
}
