package com.winitech.system.domain.programAction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.winitech.common.domain.AbstractEntity;
import com.winitech.system.domain.program.Program;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.programAction
 * └ ProgramAction.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-14 10:39
 **/
@Slf4j
@Setter
@Getter
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Table(uniqueConstraints = {
	@UniqueConstraint(columnNames = {"program_id", "actionType", "authType", "uri"})
})
public class ProgramAction extends AbstractEntity {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
	protected UUID id;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	private ActionType actionType;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	private AuthType authType;

	@NotNull
	private String uri;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "program_id")
	private Program program;

	@Getter
	@RequiredArgsConstructor
	public enum ActionType {
		RESTAPI("RESTAPI"),
		QUERYID("QUERYID");
		private final String description;
	}

	@Getter
	@RequiredArgsConstructor
	public enum AuthType {
		SELECT("SELECT"),
		INSERT("INSERT"),
		UPDATE("UPDATE"),
		DELETE("DELETE"),
		PRINT("PRINT"),
		DOWN("DOWN"),
		MANAGE("MANAGE");
		private final String description;
	}

	@Builder
	public ProgramAction(
		UUID id,
		ActionType actionType,
		AuthType authType,
		String uri,
		Program program
	) {
		this.id = id;
		this.actionType = actionType;
		this.authType = authType;
		this.uri = uri;
		this.program = program;
	}
}
