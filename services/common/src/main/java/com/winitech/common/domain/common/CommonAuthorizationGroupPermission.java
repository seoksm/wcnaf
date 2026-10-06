package com.winitech.common.domain.common;

import com.sun.istack.NotNull;
import com.winitech.common.domain.AbstractEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationGroupPermission.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 11:02
 **/
@Slf4j
@Getter
@Setter
@Entity
@NoArgsConstructor
@IdClass(CommonAuthorizationGroupPermissionId.class)
public class CommonAuthorizationGroupPermission extends AbstractEntity {
	@Id
	protected UUID authorizationGroupId;
	@Id
	protected UUID menuId;
	
	private String groupCode;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 5, nullable = false)
	@ColumnDefault("'NONE'")
	private MenuPermissionStatus selectStatus;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 5, nullable = false)
	@ColumnDefault("'NONE'")
	private MenuPermissionStatus insertStatus;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 5, nullable = false)
	@ColumnDefault("'NONE'")
	private MenuPermissionStatus updateStatus;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 5, nullable = false)
	@ColumnDefault("'NONE'")
	private MenuPermissionStatus deleteStatus;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 5, nullable = false)
	@ColumnDefault("'NONE'")
	private MenuPermissionStatus printStatus;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 5, nullable = false)
	@ColumnDefault("'NONE'")
	private MenuPermissionStatus downStatus;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 5, nullable = false)
	@ColumnDefault("'NONE'")
	private MenuPermissionStatus manageStatus;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 5, nullable = false)
	@ColumnDefault("'NONE'")
	private MenuPermissionStatus custom1Status;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 5, nullable = false)
	@ColumnDefault("'NONE'")
	private MenuPermissionStatus custom2Status;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 5, nullable = false)
	@ColumnDefault("'NONE'")
	private MenuPermissionStatus custom3Status;

	@Getter
	@RequiredArgsConstructor
	public enum MenuPermissionStatus {
		ALLOW("ALLOW"),
		//DENY("DENY"),
		NONE("NONE");
		private final String description;
	}

	@Builder
	public CommonAuthorizationGroupPermission(
			UUID authorizationGroupId, UUID menuId, String groupCode, MenuPermissionStatus selectStatus, MenuPermissionStatus insertStatus,
			MenuPermissionStatus updateStatus, MenuPermissionStatus deleteStatus, MenuPermissionStatus printStatus, MenuPermissionStatus downStatus, MenuPermissionStatus manageStatus,
			MenuPermissionStatus custom1Status, MenuPermissionStatus custom2Status, MenuPermissionStatus custom3Status
	) {
		this.authorizationGroupId = authorizationGroupId;
		this.menuId = menuId;
		this.groupCode = groupCode;		
		this.selectStatus = selectStatus;
		this.insertStatus = insertStatus;
		
		this.updateStatus = updateStatus;		
		this.deleteStatus = deleteStatus;
		this.printStatus = printStatus;
		this.downStatus = downStatus;
		this.manageStatus = manageStatus;
		
		this.custom1Status = custom1Status;		
		this.custom2Status = custom2Status;
		this.custom3Status = custom3Status;
	}		
}
