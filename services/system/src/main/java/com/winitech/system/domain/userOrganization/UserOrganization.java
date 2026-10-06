package com.winitech.system.domain.userOrganization;

import com.winitech.common.domain.AbstractEntity;
import com.winitech.system.domain.organization.Organization;
import com.winitech.system.domain.user.User;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

/**
 * com.winitech.system.domain.userOrganization
 * └ UserOrganization.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/01
 **/
@Slf4j
@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@NoArgsConstructor
@Table(name = "user_organization")
public class UserOrganization extends AbstractEntity {
	@EmbeddedId
	private UserOrganizationKey id;
	
	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne
	@MapsId("organizationId")
	@JoinColumn(name = "organization_id")
	private Organization organization;

	@Enumerated(EnumType.STRING)
	private SystemStatus systemStatus;

	@Getter
	@RequiredArgsConstructor
	public enum SystemStatus {
		ENABLE("ENABLE"),
		DISABLE("DISABLE");
		private final String description;
	}

	@Builder
	public UserOrganization(User user, Organization organization, SystemStatus systemStatus) {
		this.user = user;
		this.organization = organization;
		this.systemStatus = systemStatus;
		this.id = new UserOrganizationKey(user.getId(), organization.getId());
	}
	
	public void enable() {
		this.systemStatus = SystemStatus.ENABLE;
	}
	public void disable() {
		this.systemStatus = SystemStatus.DISABLE;
	}
}
