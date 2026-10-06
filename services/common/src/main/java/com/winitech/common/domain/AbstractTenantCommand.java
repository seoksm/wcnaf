package com.winitech.common.domain;

import com.winitech.common.exception.InvalidTenantException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * com.winitech.common.domain
 * └ AbstractTenantCommand.java
 *   extends 하는 class 에서도 private final UUID organizationId; 를 선언해야 함
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/08
 **/
@Getter
@Setter
public abstract class AbstractTenantCommand {
	protected UUID organizationId;

	@SuppressWarnings("unchecked")
	public <T> T checkOrgnizationId(UUID organizationId) {
		if (this.getOrganizationId() == null) {
			throw new InvalidTenantException("organizationId is null");
		}

		if (organizationId == null) {
			throw new InvalidTenantException("organizationId is null (parameter)");
		}
		
		if (! organizationId.equals(this.getOrganizationId())) {
			throw new InvalidTenantException("organizationId is not matched");
		}
		
		return (T) this;
	}
}
