package com.winitech.apiGateway.domain.route;

import com.winitech.apiGateway.domain.predicate.Predicate;
import com.winitech.common.domain.AbstractEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.domain.route
 * └ Route.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 11:40
 **/
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@Table(name = "route")
public class Route extends AbstractEntity {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
	private UUID id;

	private String name;
	
	@NonNull
	private String uri;
	
	@OneToMany(mappedBy = "route")
	private Set<Predicate> predicates;

	@NotNull
	private Integer sortSeq;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	@ColumnDefault("'ENABLE'")
	private Status status;

	@Getter
	@RequiredArgsConstructor
	public enum Status {
		ENABLE("ENABLE"),
		DISABLE("DISABLE");
		private final String description;
	}

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	@ColumnDefault("'ENABLE'")
	private SystemStatus systemStatus;

	@Getter
	@RequiredArgsConstructor
	public enum SystemStatus {
		ENABLE("ENABLE"),
		DISABLE("DISABLE");
		private final String description;
	}

	@Builder
	public Route(
		UUID id,
		String name,
		String uri,
		Integer sortSeq,
		Status status
	) {
		this.id = id;
		this.name = name;
		this.uri = uri;
		this.sortSeq = sortSeq;
		this.status = status;
	}
	
	public void enable() {
		this.systemStatus = SystemStatus.ENABLE;
	}
	
	public void disable() {
		this.systemStatus = SystemStatus.DISABLE;
	}
}
