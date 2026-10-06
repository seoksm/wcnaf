package com.winitech.system.domain.programRel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.winitech.common.domain.AbstractUuidEntity;
import com.winitech.system.domain.program.Program;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * <pre>
 * com.winitech.system.domain.programRel
 * └ ProgramRel.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-23 09:02
 **/
@Slf4j
@Setter
@Getter
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ProgramRel extends AbstractUuidEntity {
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "program_id")
	private Program program;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "rel_program_id")
	private Program relProgram;

	@Builder
	public ProgramRel(
			Program program,
			Program relProgram
	) {
		this.program = program;
		this.relProgram = relProgram;
	}
}
