package com.winitech.common.bean;

import com.winitech.common.library.core.MultitenantDatabaseConnectionPoolManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * <pre>
 * com.winitech.common.config
 * └ MultitenantConnectionDatabaseProvider.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-08-13 17:01
 **/
@Slf4j
@Component
@ConditionalOnExpression("'${winitech.multitenant.type:}'.toUpperCase() == 'DATABASE'")
public class MultitenantConnectionDatabaseProvider implements MultiTenantConnectionProvider, HibernatePropertiesCustomizer, InitializingBean {
	@Value("${winitech.service-name:}")
	private String serviceName; 
	
	@Value("${winitech.multitenant.database.prefix}")
	private String multitenantDatabasePrefix;
	
	// 멀티테넌트 방식이 database 일 때 connection pool 정리 시도 주기 (초)
	@Value("${winitech.multitenant.database.pool-cleanup-interval-in-seconds}")
	private Integer multitenantDatabasePoolCleanupIntervalInSeconds;

	// 멀티테넌트 방식이 database 일 때 지정된 시간 이상 사용되지 않으면 connection pool 삭제 (초)
	@Value("${winitech.multitenant.database.pool-cleanup-idle-in-seconds}")
	private Integer multitenantDatabasePoolIdleInSeconds;
	
	private final DataSource dataSource;
	
	public MultitenantConnectionDatabaseProvider(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Connection getAnyConnection() throws SQLException {
		return getConnection(null);
	}

	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException {
		MultitenantDatabaseConnectionPoolManager.getInstance().releaseConnection(null, connection);
	}
	
	@Override
	public Connection getConnection(String tenantId) throws SQLException {
		return MultitenantDatabaseConnectionPoolManager.getInstance().getConnection(tenantId);
	}

	@Override
	public void releaseConnection(String tenantId, Connection connection) throws SQLException {
		if (! "default".equals(tenantId)) {
			log.debug("Releasing connection for tenant: {}", tenantId);
		}
		
		MultitenantDatabaseConnectionPoolManager.getInstance().releaseConnection(tenantId, connection);
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}

	@Override
	public boolean isUnwrappableAs(Class aClass) {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> aClass) {
		return null;
	}

	@Override
	public void customize(Map<String, Object> hibernateProperties) {
		hibernateProperties.put(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
		hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		MultitenantDatabaseConnectionPoolManager.initialize(
				dataSource,
				serviceName,
				multitenantDatabasePrefix,
				multitenantDatabasePoolCleanupIntervalInSeconds,
				multitenantDatabasePoolIdleInSeconds
		);
	}
}
