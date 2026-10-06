package com.winitech.system.domain.userOrganization;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

/**
 * com.winitech.system.domain.userOrganization
 * └ UserOrganizationKey.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/01
 **/
@Data
@EqualsAndHashCode(callSuper=false)
@Embeddable
@NoArgsConstructor
public class UserOrganizationKey implements Serializable {
	@Column(name = "user_id")
	private UUID userId;
	
	@Column(name = "organization_id")
	private UUID organizationId;

	public UserOrganizationKey(UUID userId, UUID organizationId) {
		this.userId = userId;
		this.organizationId = organizationId;
	}
}
