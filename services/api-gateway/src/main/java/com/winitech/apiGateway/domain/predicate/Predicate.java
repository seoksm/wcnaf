package com.winitech.apiGateway.domain.predicate;

import com.winitech.apiGateway.domain.route.Route;
import com.winitech.common.domain.AbstractEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.domain.predicate
 * └ Predicate.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 11:42
 **/
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@Table(name = "predicate")
public class Predicate extends AbstractEntity {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
	private UUID id;
	
	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(length = 10, nullable = false)
	private PredicateType predicateType;
	
	@Column(columnDefinition = "TEXT")
	private String key;

	@Column(columnDefinition = "TEXT")
	private String definition;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "route_id")
	private Route route;

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

	@Getter
	@RequiredArgsConstructor
	public enum PredicateType {
		COOKIE("COOKIE"),
		HEADER("HEADER"),
		METHOD("METHOD"),
		PATH("PATH"),
		HOST("HOST"),
		QUERY("QUERY"),
		WEIGHT("WEIGHT");
		private final String description;
	}
	
	@Builder
	public Predicate(
		UUID id,
		PredicateType predicateType,
		String key,
		String definition,
		Status status,
		Route route
	) {
		this.id = id;
		this.predicateType = predicateType;
		this.key = key;
		this.definition = definition;
		this.status = status;
		this.route = route;
	}

	public void enable() {
		this.systemStatus = SystemStatus.ENABLE;
	}

	public void disable() {
		this.systemStatus = SystemStatus.DISABLE;
	}
}
