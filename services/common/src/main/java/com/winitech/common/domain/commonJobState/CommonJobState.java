package com.winitech.common.domain.commonJobState;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.winitech.common.domain.AbstractEntity;
import com.winitech.common.domain.commonJob.CommonJob;
import com.winitech.common.domain.commonJobRun.CommonJobRun;
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
 * com.winitech.system.domain.commonJobState
 * └ CommonJobState.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:47
 **/
@Slf4j
@Setter
@Getter
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CommonJobState extends AbstractEntity {
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

	private OffsetDateTime lastStartedAt;

	private OffsetDateTime lastEndedAt;

	private OffsetDateTime lastSuccessAt;

	private OffsetDateTime lastFailedAt;

	@ManyToOne
	@JoinColumn(name = "current_run_id")
	private CommonJobRun currentRun;

	@ManyToOne
	@JoinColumn(name = "last_run_id")
	private CommonJobRun lastRun;

	private Integer errCnt;

	private String lastMessage;

	// 시스템적인 상태 (ENABLE 사용중, DISABLE 삭제됨)
	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	@ColumnDefault("'ENABLE'")
	private CommonJobState.SystemStatus systemStatus;

	// 예시) N-to-1
	//@JsonBackReference
	//@ManyToOne(fetch = FetchType.LAZY)
	//@JoinColumn(name = "parent_commonJobState_id")
	//private CommonJobState parentCommonJobState;
	
	// 예시) 1-to-N
	//@JsonBackReference
	//@OneToMany(mappedBy = "parentCommonJobState")
	//private Set<CommonJobState> childrenCommonJobState;

	// 예시) 1-to-1
	//@JsonBackReference
	//@OneToOne(mappedBy = "user")
	//private UserDetail userDetail;

	@Getter
	@RequiredArgsConstructor
	public enum SystemStatus {
		ENABLE("ENABLE"),
		DISABLE("DISABLE");
		private final String description;
	}

	@Builder
	public CommonJobState(
		UUID id,
		CommonJob commonJob,
		OffsetDateTime lastStartedAt,
		OffsetDateTime lastEndedAt,
		OffsetDateTime lastSuccessAt,
		OffsetDateTime lastFailedAt,
		CommonJobRun currentRun,
		CommonJobRun lastRun,
		Integer errCnt,
		String lastMessage,
		SystemStatus systemStatus
	) {
		this.id = id;
		this.commonJob = commonJob;
		this.lastStartedAt = lastStartedAt;
		this.lastEndedAt = lastEndedAt;
		this.lastSuccessAt = lastSuccessAt;
		this.lastFailedAt = lastFailedAt;
		this.currentRun = currentRun;
		this.lastRun = lastRun;
		this.errCnt = errCnt;
		this.lastMessage = lastMessage;
		this.systemStatus = systemStatus;
	}
	
	public void enable() {
		this.systemStatus = SystemStatus.ENABLE;
	}
	
	public void disable() {
		this.systemStatus = SystemStatus.DISABLE;
	}
}
