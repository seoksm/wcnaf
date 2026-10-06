package com.winitech.common.bean;

import com.winitech.common.exception.IllegalStatusException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * <pre>
 * com.winitech.common.bean
 * └ MultitenantConnectionRowProvider.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-08-20 13:15
 **/
@Slf4j
@Component
@ConditionalOnExpression("'${winitech.multitenant.type:}'.toUpperCase() == 'ROW'")
public class MultitenantConnectionRowProvider implements MultiTenantConnectionProvider, HibernatePropertiesCustomizer {
	private final DataSource dataSource;

	public MultitenantConnectionRowProvider(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Connection getAnyConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException {
		connection.close();
	}

	@Override
	public Connection getConnection(String tenantIdentifier) throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
		connection.close();
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}

	@Override
	public boolean isUnwrappableAs(Class unwrapType) {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> unwrapType) {
		return null;
	}

	@Override
	public void customize(Map<String, Object> hibernateProperties) {
		hibernateProperties.put(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.DISCRIMINATOR);
		hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
		
		throw new IllegalStatusException("(미구현) hibernate 6.x 이상부터 사용 가능합니다.");
	}
}
