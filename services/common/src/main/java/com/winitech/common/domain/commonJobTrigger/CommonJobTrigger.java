package com.winitech.common.domain.commonJobTrigger;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.winitech.common.domain.AbstractEntity;
import com.winitech.common.domain.commonJob.CommonJob;
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
 * com.winitech.system.domain.commonJobTrigger
 * └ CommonJobTrigger.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:45
 **/
@Slf4j
@Setter
@Getter
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CommonJobTrigger extends AbstractEntity {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
	protected UUID id;

	// 예시) 컬럼
	// @NotNull // notnull인 경우
	// private String 컬럼명;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "common_job_id")
	private CommonJob commonJob;
	
	@NotNull
	private String name;

	private String triggerCron;
	
	private Long triggerSeconds;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	@ColumnDefault("'PENDING'")
	private ApplyStatus applyStatus;
	
	private OffsetDateTime appliedAt; 

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	private TriggerType triggerType;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	@ColumnDefault("'ENABLE'")
	private Status status;

	// 시스템적인 상태 (ENABLE 사용중, DISABLE 삭제됨)
	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	@ColumnDefault("'ENABLE'")
	private CommonJobTrigger.SystemStatus systemStatus;

	// 예시) N-to-1
	//@JsonBackReference
	//@ManyToOne(fetch = FetchType.LAZY)
	//@JoinColumn(name = "parent_commonJobTrigger_id")
	//private CommonJobTrigger parentCommonJobTrigger;
	
	// 예시) 1-to-N
	//@JsonBackReference
	//@OneToMany(mappedBy = "parentCommonJobTrigger")
	//private Set<CommonJobTrigger> childrenCommonJobTrigger;

	// 예시) 1-to-1
	//@JsonBackReference
	//@OneToOne(mappedBy = "user")
	//private UserDetail userDetail;

	@Getter
	@RequiredArgsConstructor
	public enum TriggerType {
		CRON("CRON"),
		SECONDS("SECONDS");
		private final String description;
	}

	@Getter
	@RequiredArgsConstructor
	public enum ApplyStatus {
		PENDING("PENDING"),
		SUCCESSFUL("SUCCESSFUL"),
		FAILED("FAILED");
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
	public CommonJobTrigger(
		UUID id,
		CommonJob commonJob,
		String name,
		String triggerCron,
		Long triggerSeconds,
		TriggerType triggerType,
		Status status,
		SystemStatus systemStatus,
		ApplyStatus applyStatus,
		OffsetDateTime appliedAt
	) {
		this.id = id;
		this.commonJob = commonJob;
		this.name = name;
		this.triggerCron = triggerCron;
		this.triggerSeconds = triggerSeconds;
		this.triggerType = triggerType;
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
