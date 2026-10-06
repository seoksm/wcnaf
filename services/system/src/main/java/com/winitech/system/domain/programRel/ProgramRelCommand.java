package com.winitech.system.domain.programRel;

import com.winitech.system.domain.program.Program;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.programRel
 * └ ProgramRelCommand.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-23 09:05
 **/
@Getter
@Builder
@ToString
public class ProgramRelCommand {
	private final UUID programId;

	private final UUID relProgramId;
}
