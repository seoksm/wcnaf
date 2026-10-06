package com.winitech.system.domain.program;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.winitech.common.domain.AbstractEntity;
import com.winitech.common.domain.AbstractUuidEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * com.winitech.system.domain.program
 * ㄴ Program.java
 * @author : coding
 * @see : None
 * @since : 2025-01-22
 **/
@Slf4j
@Setter
@Getter
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Table(name = "program")
public class Program extends AbstractUuidEntity {
    /**
     * 프로그램 코드
     */
	private String programCode;

    /**
     * 프로그램 이름
     */
	@NonNull
	private String programName;

    /**
     * 프로그램 매핑
     */
	@NonNull
	private String programMapping;

    /**
     * 사용 여부
     */
	@Enumerated(EnumType.STRING)
    @NonNull
	@Column(length = 10)
	private Status status;

    /**
     * 메뉴 여부
     */
	@Enumerated(EnumType.STRING)
    @NonNull
	@Column(length = 10)
	private MenuStatus menuStatus;

    /**
     * 모바일 사용 상태
     */
	@Enumerated(EnumType.STRING)
    @NonNull
	@Column(length = 10)
	private MobileStatus mobileStatus;

    /**
     * 프로그램 매핑 고정 상태
     */
	@Enumerated(EnumType.STRING)
    @NonNull
	@Column(length = 10)
	private ProgramMappingStatus programMappingStatus;

    /**
     * 비고
     */
	private String remark;

	@Getter
	@RequiredArgsConstructor
	public enum Status {
		ENABLE("ENABLE"),
		DISABLE("DISABLE");
		private final String description;
	}

	@Getter
	@RequiredArgsConstructor
	public enum MenuStatus {
		ENABLE("ENABLE"),
		DISABLE("DISABLE");
		private final String description;
	}

	@Getter
	@RequiredArgsConstructor
	public enum MobileStatus {
		ENABLE("ENABLE"),
		DISABLE("DISABLE");
		private final String description;
	}

	@Getter
	@RequiredArgsConstructor
	public enum ProgramMappingStatus {
		FIXED("FIXED"),
		DEFAULT("DEFAULT");
		private final String description;
	}

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "parent_program_id")
	private Program parentProgram;

	@JsonBackReference
	@OneToMany(mappedBy = "parentProgram")
	private List<Program> childrenProgram;

	@Builder
	public Program(
			UUID id,
			String programCode,
			String programName,
			String programMapping,
			Status status,
			
			MenuStatus menuStatus,
			MobileStatus mobileStatus,
			ProgramMappingStatus programMappingStatus,
			String remark,
            Program parentProgram
	) {
		this.id = id;
		this.programCode = programCode;
		this.programName = programName;
		this.programMapping = programMapping;
		this.status = status;
		
		this.menuStatus = menuStatus;
		this.mobileStatus = mobileStatus;
		this.programMappingStatus = programMappingStatus;
		this.remark = remark;
        this.parentProgram = parentProgram;
	}
}
