package com.winitech.system.domain.organization;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.winitech.common.domain.AbstractEntity;
import com.winitech.system.domain.userOrganization.UserOrganization;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

/**
 * com.winitech.system.domain.organization
 * └ Organization.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/07/30
 **/
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@Table(name = "organization")
public class Organization extends AbstractEntity {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.winitech.common.library.core.JpaUUIDv7Generator")
	private UUID id;

	private String organizationCode;

	private String organizationName;

	/**
	 * 테넌트별 database 방식의 멀티테넌트를 사용시 해당 조직의 DB 호스트
	 */
	private String databaseHost;

	/**
	 * 테넌트별 database 방식의 멀티테넌트를 사용시 해당 조직의 DB 포트
	 */
	private Integer databasePort;

	/**
	 * 테넌트별 database 관련 최근 메시지
	 */
	private String databaseMessage;

	/**
	 * 테넌트별 database 스키마 버전
	 */
	private Integer tenantSchemaVersion;

	@Enumerated(EnumType.STRING)
	private TenantSetupStatus tenantSetupStatus;

	@Enumerated(EnumType.STRING)
	private TenantStatus tenantStatus;

	@Enumerated(EnumType.STRING)
	private SystemStatus systemStatus;

//	@OneToMany(mappedBy = "station")
//	@JsonManagedReference
//	private List<RemoteTerminalUnit> remoteTerminalUnits;
//	private SystemStatus systemStatus;
	
	@OneToMany(mappedBy = "organization")
	Set<UserOrganization> userOrganizations;

	@Getter
	@RequiredArgsConstructor
	public enum SystemStatus {
		ENABLE("ENABLE"),
		DISABLE("DISABLE");
		private final String description;
	}

	@Getter
	@RequiredArgsConstructor
	public enum TenantSetupStatus {
		NONE("NONE"),
		PENDING("PENDING"),
		DBCREATE("DBCREATE"),
		DONE("DONE");
		private final String description;
	}

	@Getter
	@RequiredArgsConstructor
	public enum TenantStatus {
		UNKNOWN("UNKNOWN"),
		READY("READY"),
		DEGRADED("DEGRADED"),
		ERROR("ERROR"),
		NOCONNECTION("NOCONNECTION");
		private final String description;
	}

	@Builder
	public Organization(
			UUID id,
			String organizationCode,
			String organizationName,
			String databaseHost,
			Integer databasePort,
			Integer tenantSchemaVersion,
			String databaseMessage,
			TenantSetupStatus tenantSetupStatus,
			TenantStatus tenantStatus
	) {
		this.id = id;
		this.organizationCode = organizationCode;
		this.organizationName = organizationName;
		this.databaseHost = databaseHost;
		this.databasePort = databasePort;
		this.databaseMessage = databaseMessage;
		this.tenantSchemaVersion = tenantSchemaVersion;
		this.tenantSetupStatus = tenantSetupStatus != null ? tenantSetupStatus : TenantSetupStatus.NONE;
		this.tenantStatus = tenantStatus != null ? tenantStatus : TenantStatus.UNKNOWN;
		this.systemStatus = SystemStatus.ENABLE;
	}

	public void enable() {
		this.systemStatus = SystemStatus.ENABLE;
	}

	public void disable() {
		this.systemStatus = SystemStatus.DISABLE;
	}
}
