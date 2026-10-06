package com.winitech.system.domain.authorizationGroupUser;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.winitech.common.domain.AbstractEntity;
import com.winitech.system.domain.authorizationGroup.AuthorizationGroup;
import com.winitech.system.domain.user.User;
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
 * com.winitech.system.domain.authorizationGroupUser
 * └ AuthorizationGroupUser.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-20 10:09
 **/
@Slf4j
@Setter
@Getter
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class AuthorizationGroupUser extends AbstractEntity {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
	protected UUID id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "authorization_group_id")
	private AuthorizationGroup authorizationGroup;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Getter
	@RequiredArgsConstructor
	public enum AuthorizationGroupUserStatus {
		ENABLE("ENABLE"),
		DISABLE("DISABLE");
		private final String description;
	}

	@Builder
	public AuthorizationGroupUser(
		UUID id,
		AuthorizationGroup authorizationGroup,
		User user
	) {
		this.id = id;
		this.authorizationGroup = authorizationGroup;
		this.user = user;
	}
}
