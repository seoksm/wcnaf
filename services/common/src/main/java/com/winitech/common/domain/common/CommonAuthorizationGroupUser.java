package com.winitech.common.domain.common;

import com.winitech.common.domain.AbstractEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.domain.common
 * └ CommonAuthorizationGroupUser.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 11:02
 **/
@Slf4j
@Getter
@Setter
@Entity
@NoArgsConstructor
@IdClass(CommonAuthorizationGroupUserId.class)
public class CommonAuthorizationGroupUser extends AbstractEntity {
	@Id
	protected UUID userId;
	@Id
	protected UUID authorizationGroupId;
	
	@Builder
	public CommonAuthorizationGroupUser(UUID userId, UUID authorizationGroupId) {
		this.userId = userId;
		this.authorizationGroupId = authorizationGroupId;
	}
}
