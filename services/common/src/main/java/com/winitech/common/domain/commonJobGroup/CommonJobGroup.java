package com.winitech.common.domain.commonJobGroup;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.winitech.common.domain.AbstractEntity;
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
 * com.winitech.system.domain.commonJobGroup
 * └ CommonJobGroup.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:36
 **/
@Slf4j
@Setter
@Getter
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CommonJobGroup extends AbstractEntity {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
	protected UUID id;

	// 예시) 컬럼
	// @NotNull // notnull인 경우
	// private String 컬럼명;

	@NotNull
	private String name;

	private String remark;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	@ColumnDefault("'ENABLE'")
	private CommonJobGroup.Status status;

	// 시스템적인 상태 (ENABLE 사용중, DISABLE 삭제됨)
	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	@ColumnDefault("'ENABLE'")
	private CommonJobGroup.SystemStatus systemStatus;

	// 예시) N-to-1
	//@JsonBackReference
	//@ManyToOne(fetch = FetchType.LAZY)
	//@JoinColumn(name = "parent_commonJobGroup_id")
	//private CommonJobGroup parentCommonJobGroup;
	
	// 예시) 1-to-N
	//@JsonBackReference
	//@OneToMany(mappedBy = "parentCommonJobGroup")
	//private Set<CommonJobGroup> childrenCommonJobGroup;

	// 예시) 1-to-1
	//@JsonBackReference
	//@OneToOne(mappedBy = "user")
	//private UserDetail userDetail;

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
	public CommonJobGroup(
		UUID id,
		String name,
		String remark,
		Status status,
		SystemStatus systemStatus
	) {
		this.id = id;
		this.name = name;
		this.remark = remark;
		this.status = status;
		this.systemStatus = systemStatus;
	}
	
	public void enable() {
		this.systemStatus = SystemStatus.ENABLE;
	}
	
	public void disable() {
		this.systemStatus = SystemStatus.DISABLE;
	}
}
