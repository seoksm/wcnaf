package com.winitech.common.library.core;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.library.core
 * └ CurrentUserContext.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-08-14 09:26
 **/
public class CurrentTenantHolder {
	private static final ThreadLocal<CurrentTenantHolder> currentTenant = new ThreadLocal<>();
	
	private static final CurrentTenantHolder emptyTenant = new CurrentTenantHolder(null, null);

	public static CurrentTenantHolder get() {
		CurrentTenantHolder currentTenantHolder = currentTenant.get();
		
		if (currentTenantHolder == null) {
			return emptyTenant;
		}
		
		return currentTenantHolder;
	}

	public static void set(UUID organizationId, String tenantId) {
		currentTenant.set(new CurrentTenantHolder(organizationId, tenantId));
	}

	public static void clear() {
		currentTenant.set(emptyTenant);
	}

	private UUID organizationId;
	private String tenantId; 
	
	private CurrentTenantHolder(UUID organizationId, String tenantId) {
		this.organizationId = organizationId;
		this.tenantId = tenantId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public UUID getOrganizationId() {
		return organizationId;
	}
}
