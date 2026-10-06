package com.winitech.common.config.properties;

import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.library.core.CurrentTenantHolder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.config.props
 * └ MultitenantProperties.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-08-21 16:52
 **/
@Component
@Getter
@Setter
@ConfigurationProperties("winitech.multitenant")
public class MultitenantProperties {
	private UUID defaultTenantId;
	
	private MultitenantType type;

	public void setType(String multitenantType) {
		if (multitenantType == null || multitenantType.isEmpty()) {
			type = MultitenantType.NONE;
			return;
		}
		
		switch (multitenantType.toUpperCase()) {
			case "DATABASE":
			case "SCHEMA":
			case "NONE":
				this.type = MultitenantType.valueOf(multitenantType.toUpperCase());
				break;
			default:
				throw new IllegalStatusException("Invalid multitenant type : " + multitenantType);
		}
	}
	
	private Database database;
	
	private Schema schema;
	
	public boolean isMultitenant() {
		return this.type != MultitenantType.NONE;
	}
	
	public boolean isDefaultTenantActive() {
		CurrentTenantHolder currentTenantHolder = CurrentTenantHolder.get();
		return currentTenantHolder.getOrganizationId() == null || currentTenantHolder.getOrganizationId().equals(this.defaultTenantId);
	}
	
	@Getter
	@Setter
	public static class Database {
		private String prefix;
	}
	
	@Getter
	@Setter
	public static class Schema {
		private String prefix;
		private String sharedSchema;
	}

	public enum MultitenantType {
		DATABASE,
		SCHEMA,
		ROW,
		NONE
	}
}
