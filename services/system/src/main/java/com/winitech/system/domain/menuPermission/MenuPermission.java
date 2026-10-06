package com.winitech.system.domain.menuPermission;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.winitech.common.domain.AbstractEntity;
import com.winitech.system.domain.authorizationGroup.AuthorizationGroup;
import com.winitech.system.domain.menu.Menu;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.menuPermission
 * └ MenuPermission.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-18 11:08
 **/
@Slf4j
@Setter
@Getter
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MenuPermission extends AbstractEntity {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
	protected UUID id;

	@JsonBackReference
	@NotNull
	@ManyToOne
	@JoinColumn(name = "menu_id")
	private Menu menu;

	@JsonBackReference
	@NotNull
	@ManyToOne
	@JoinColumn(name = "authorization_group_id")
	private AuthorizationGroup authorizationGroup;

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
	public MenuPermission(
			UUID id,
			Menu menu,
			AuthorizationGroup authorizationGroup,
			MenuPermissionStatus selectStatus,
			MenuPermissionStatus insertStatus,
			MenuPermissionStatus updateStatus,
			MenuPermissionStatus deleteStatus,
			MenuPermissionStatus printStatus,
			MenuPermissionStatus downStatus,
			MenuPermissionStatus manageStatus,
			MenuPermissionStatus custom1Status,
			MenuPermissionStatus custom2Status,
			MenuPermissionStatus custom3Status
	) {
		this.id = id;
		this.menu = menu;
		this.authorizationGroup = authorizationGroup;
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
