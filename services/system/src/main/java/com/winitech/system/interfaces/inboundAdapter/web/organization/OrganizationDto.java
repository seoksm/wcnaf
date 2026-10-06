package com.winitech.system.interfaces.inboundAdapter.web.organization;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.winitech.system.domain.organization.Organization;
import com.winitech.system.domain.organization.OrganizationCommand;
import com.winitech.system.domain.organization.OrganizationInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * com.winitech.system.interfaces.inboundAdapter.web.organization
 * └ OrganizationDto.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/07/31
 **/
@NoArgsConstructor
public class OrganizationDto {
	@ApiModel("Organization management request")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class OrganizationRequest {
		@ApiModelProperty(value = "조직 코드", example = "WINITECH", required = true)
		@NotBlank
		private String organizationCode;
		@ApiModelProperty(value = "조직 이름", example = "(주)위니텍", required = true)
		@NotBlank
		private String organizationName;
		@ApiModelProperty(value = "조직 DB 호스트", example = "localhost", required = false)
		private String databaseHost;
		@ApiModelProperty(value = "조직 DB 포트", example = "5432", required = false)
		private Integer databasePort;
		@ApiModelProperty(value = "국가 코드", example = "KR", required = false)
		private String nationalCode;
		
		public OrganizationCommand toCommand() {
			return OrganizationCommand.builder()
					.organizationCode(organizationCode)
					.organizationName(organizationName)
					.databaseHost(databaseHost)
					.databasePort(databasePort)
					.build();
		} 
	}

	@ApiModel("Organization management response")
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class OrganizationResponse {
		@ApiModelProperty(value = "조직 ID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
		private String id;
		@ApiModelProperty(value = "조직 코드", example = "WINITECH", required = true)
		private String organizationCode;
		@ApiModelProperty(value = "조직 이름", example = "(주)위니텍", required = true)
		private String organizationName;
		@ApiModelProperty(value = "조직 DB 호스트", example = "localhost", required = false)
		private String databaseHost;
		@ApiModelProperty(value = "조직 DB 포트", example = "5432", required = false)
		private Integer databasePort;
		@ApiModelProperty(value = "조직 DB 메시지", example = "정상", required = false)
		private String databaseMessage;
		@ApiModelProperty(value = "테넌트 DB 스키마 버전", example = "15", required = false)
		private Integer tenantSchemaVersion;
		@ApiModelProperty(value = "테넌트 설정 상태", required = false)
		private Organization.TenantSetupStatus tenantSetupStatus;
		@ApiModelProperty(value = "테넌트 상태", required = false)
		private Organization.TenantStatus tenantStatus;

		public OrganizationResponse(OrganizationInfo organization) {
			this.id = organization.getId().toString();
			this.organizationCode = organization.getOrganizationCode();
			this.organizationName = organization.getOrganizationName();
			this.databaseHost = organization.getDatabaseHost();
			this.databasePort = organization.getDatabasePort();
			this.databaseMessage = organization.getDatabaseMessage();
			this.tenantSchemaVersion = organization.getTenantSchemaVersion();
			this.tenantSetupStatus = organization.getTenantSetupStatus();
			this.tenantStatus = organization.getTenantStatus();
		}
	}

}
