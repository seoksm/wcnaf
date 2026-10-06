package com.winitech.system.domain.menu;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.winitech.common.domain.AbstractUuidEntity;
import com.winitech.system.domain.program.Program;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.menu
 * └ Menu.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-23 18:07
 **/
@Slf4j
@Setter
@Getter
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Table(name = "menu")
public class Menu extends AbstractUuidEntity {
	@NotNull
	private String menuName;

	private String menuCode;

	private String menuMapping;
	
	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	@ColumnDefault("'ENABLE'")
	private Menu.SystemStatus systemStatus;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	private Menu.Status status;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10)
	private Menu.MenuStatus menuStatus;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10)
	private Menu.MenuType menuType;

	private Integer sortSeq;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "parent_menu_id")
	private Menu parentMenu;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "program_id")
	private Program program;

	@JsonBackReference
	@OneToMany(mappedBy = "parentMenu")
	@OrderBy("sortSeq ASC, menuCode ASC, menuMapping ASC, id ASC")
	@Where(clause = "system_status = 'ENABLE'")
	private List<Menu> childrenMenu;

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
	public enum MenuType {
		MENU("MENU"),
		PROGRAM("PROGRAM"),
		URL("URL");
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
	public Menu(
			UUID id,
			@Nonnull String menuName,
			String menuCode,
			String menuMapping,
			@Nonnull Status status,
			@Nonnull MenuStatus menuStatus,
			@Nonnull MenuType menuType,
			Integer sortSeq,
			Menu parentMenu,
			Program program,
			@Nonnull SystemStatus systemStatus
	) {
		this.menuName = menuName;
		this.menuCode = menuCode;
		this.menuMapping = menuMapping;
		this.status = status;
		this.menuStatus = menuStatus;
		this.menuType = menuType;
		this.sortSeq = sortSeq;
		this.parentMenu = parentMenu;
		this.program = program;
		this.systemStatus = systemStatus;
	}
}
