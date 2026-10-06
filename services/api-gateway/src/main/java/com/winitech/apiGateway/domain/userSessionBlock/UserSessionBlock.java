package com.winitech.apiGateway.domain.userSessionBlock;

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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.domain.userSessionBlock
 * └ UserSessionBlock.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-21 15:03
 **/
@Slf4j
@Setter
@Getter
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class UserSessionBlock extends AbstractEntity {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
	protected UUID id;

	@Column(unique = true)
	@NotNull
	private UUID userSessionId;

	@NotNull
	private OffsetDateTime accessTokenExpiresAt;

	private UUID userId;

	@Builder
	public UserSessionBlock(
		UUID id,
		UUID userSessionId,
		OffsetDateTime accessTokenExpiresAt,
		UUID userId
	) {
		this.id = id;
		this.userSessionId = userSessionId;
		this.accessTokenExpiresAt = accessTokenExpiresAt;
		this.userId = userId;
	}
}
