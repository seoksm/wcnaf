package com.winitech.common.domain.commonJobRun;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.winitech.common.domain.AbstractEntity;
import com.winitech.common.domain.commonJob.CommonJob;
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
 * com.winitech.system.domain.commonJobRun
 * └ CommonJobRun.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:48
 **/
@Slf4j
@Setter
@Getter
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CommonJobRun extends AbstractEntity {
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

	@ManyToOne
	@JoinColumn(name = "common_job_trigger_id")
	private CommonJobTrigger commonJobTrigger;

	private OffsetDateTime startedAt;

	private OffsetDateTime endedAt;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	@ColumnDefault("'INVALID'")
	private JobStatus jobStatus;

	private String message;

	// 시스템적인 상태 (ENABLE 사용중, DISABLE 삭제됨)
	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	@ColumnDefault("'ENABLE'")
	private CommonJobRun.SystemStatus systemStatus;

	// 예시) N-to-1
	//@JsonBackReference
	//@ManyToOne(fetch = FetchType.LAZY)
	//@JoinColumn(name = "parent_commonJobRun_id")
	//private CommonJobRun parentCommonJobRun;
	
	// 예시) 1-to-N
	//@JsonBackReference
	//@OneToMany(mappedBy = "parentCommonJobRun")
	//private Set<CommonJobRun> childrenCommonJobRun;

	// 예시) 1-to-1
	//@JsonBackReference
	//@OneToOne(mappedBy = "user")
	//private UserDetail userDetail;

	@Getter
	@RequiredArgsConstructor
	public enum JobStatus {
		PENDING("대기"),
		PROCESSING("처리중"),
		RETRYING("재시도중"),
		SUCCESSFUL("성공"),
		CANCEL("중단됨"),
		FAILED("실패"),
		INVALID("문제발생");
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
	public CommonJobRun(
		UUID id,
		CommonJob commonJob,
		CommonJobTrigger commonJobTrigger,
		OffsetDateTime startedAt,
		OffsetDateTime endedAt,
		JobStatus jobStatus,
		String message,
		SystemStatus systemStatus
	) {
		this.id = id;
		this.commonJob = commonJob;
		this.commonJobTrigger = commonJobTrigger;
		this.startedAt = startedAt;
		this.endedAt = endedAt;
		this.jobStatus = jobStatus;
		this.message = message;
		this.systemStatus = systemStatus;
	}
	
	public void enable() {
		this.systemStatus = SystemStatus.ENABLE;
	}
	
	public void disable() {
		this.systemStatus = SystemStatus.DISABLE;
	}
}
