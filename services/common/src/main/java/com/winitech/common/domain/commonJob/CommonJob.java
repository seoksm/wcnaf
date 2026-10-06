package com.winitech.common.domain.commonJob;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.winitech.common.domain.AbstractEntity;
import com.winitech.common.domain.commonJobGroup.CommonJobGroup;
import com.winitech.common.domain.commonJobTrigger.CommonJobTrigger;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJob
 * └ CommonJob.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:37
 **/
@Slf4j
@Setter
@Getter
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CommonJob extends AbstractEntity {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
	protected UUID id;

	// 예시) 컬럼
	// @NotNull // notnull인 경우
	// private String 컬럼명;

	@NotNull
	private String name;

	private String className;

	private String sql;

	private String remark;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	@ColumnDefault("'PENDING'")
	private ApplyStatus applyStatus;

	private OffsetDateTime appliedAt;

	@ManyToOne
	@JoinColumn(name = "common_job_group_id")
	private CommonJobGroup commonJobGroup;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	private CommonJob.JobType jobType;

	@Getter
	@RequiredArgsConstructor
	public enum ApplyStatus {
		PENDING("PENDING"),
		SUCCESSFUL("SUCCESSFUL"),
		FAILED("FAILED");
		private final String description;
	}

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	@ColumnDefault("'ENABLE'")
	private CommonJob.Status status;

	// 시스템적인 상태 (ENABLE 사용중, DISABLE 삭제됨)
	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	@ColumnDefault("'ENABLE'")
	private CommonJob.SystemStatus systemStatus;

	@Getter
	@RequiredArgsConstructor
	public enum JobType {
		JAVA("JAVA Class 기반"),
		SQL("SQL 쿼리문 기반");
		private final String description;
	}

	@Getter
	@RequiredArgsConstructor
	public enum Status {
		ENABLE("ENABLE"),
		DISABLE("DISABLE");
		private final String description;
	}

	@Getter
	@RequiredArgsConstructor
	public enum SystemStatus {
		ENABLE("ENABLE"),
		DISABLE("DISABLE");
		private final String description;
	}

	@Builder
	public CommonJob(
		UUID id,
		String name,
		String className,
		String sql,
		String remark,
		CommonJobGroup commonJobGroup,
		CommonJob.JobType jobType,
		CommonJob.Status status,
		SystemStatus systemStatus,
		ApplyStatus applyStatus,
		OffsetDateTime appliedAt
	) {
		this.id = id;
		this.name = name;
		this.className = className;
		this.sql = sql;
		this.remark = remark;
		this.commonJobGroup = commonJobGroup;
		this.jobType = jobType;
		this.status = status;
		this.systemStatus = systemStatus;
		this.applyStatus = applyStatus;
		this.appliedAt = appliedAt;
	}
	
	public void enable() {
		this.systemStatus = SystemStatus.ENABLE;
	}
	
	public void disable() {
		this.systemStatus = SystemStatus.DISABLE;
	}
}
