package com.winitech.system.domain.department;

import java.util.List;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.winitech.system.domain.menu.Menu;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import com.winitech.common.domain.AbstractEntity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Where;

/**
* com.winitech.user.domain
* ㄴ User.java
* @author : 박준희 과장 (부설연구소)
* @since : 2025-02-10
* @see : None
 **/
@Slf4j
@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@NoArgsConstructor
@Table(name = "department")
public class Department extends AbstractEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
    private UUID id;
    @NotNull
    private String departmentCode;
	@NotNull
    private String departmentName;

	private Integer sortSeq;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "parent_department_id")
	private Department parentDepartment;

	@JsonBackReference
	@OneToMany(mappedBy = "parentDepartment")
	@OrderBy("sortSeq ASC, departmentName ASC, id ASC")
	@Where(clause = "system_status = 'ENABLE'")
	private List<Department> childrenDepartment;

	/**
	 * 사용여부
	 */
	@Enumerated(EnumType.STRING)
	@NotNull
	@ColumnDefault("'ENABLE'")
    private Status status;

	@Enumerated(EnumType.STRING)
    private SystemStatus systemStatus;
	
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
    public Department(
        String departmentCode,
        String departmentName,
        Department parentDepartment,
		Integer sortSeq,
		Status status
    ) {
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
        this.parentDepartment = parentDepartment;
		this.sortSeq = sortSeq;
		this.status = status;
        this.systemStatus = SystemStatus.ENABLE;
    }

	public void enable() {
		this.systemStatus = SystemStatus.ENABLE;
	}
	public void disable() {
		this.systemStatus = SystemStatus.DISABLE;
	}
}
