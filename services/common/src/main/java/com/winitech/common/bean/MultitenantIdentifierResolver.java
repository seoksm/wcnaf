package com.winitech.common.bean;

import com.winitech.common.exception.InvalidTenantException;
import com.winitech.common.library.core.CurrentTenantHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 * com.winitech.common.bean
 * └ MultitenantIdentifierResolver.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-08-13 17:21
 **/
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnExpression("{'DATABASE', 'SCHEMA', 'ROW'}.contains('${winitech.multitenant.type:}'.toUpperCase())")
public class MultitenantIdentifierResolver implements CurrentTenantIdentifierResolver, HibernatePropertiesCustomizer {
	private boolean isInitialized = false;
	
	private final Map<UUID, String> orgIdToTenantIdMap = new ConcurrentHashMap<>();
	
	private UUID defaultTenantId;

	@Value("${winitech.multitenant.default-tenant-id}")
	private void setDefaultTenantId(String defaultTenantId) {
		this.defaultTenantId = UUID.fromString(defaultTenantId);
	}

	@Override
	public String resolveCurrentTenantIdentifier() {
		if (! isInitialized) {
			return "default";
		}
		
		CurrentTenantHolder currentTenantHolder = CurrentTenantHolder.get();
		
		UUID organizationId = currentTenantHolder.getOrganizationId();
		String tenantId = currentTenantHolder.getTenantId();

		if (tenantId == null) {
			// 조직 ID가 없으면 기본 DB를 반환
			return "default";
		}
		
		if (defaultTenantId.equals(organizationId)) {
			return "default";
		}
		
		return tenantId;
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return false;
	}

	@Override
	public void customize(Map<String, Object> hibernateProperties) {
		hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		isInitialized = true;

		log.debug("MultitenantIdentifierResolver 초기화 완료");
	}
}
