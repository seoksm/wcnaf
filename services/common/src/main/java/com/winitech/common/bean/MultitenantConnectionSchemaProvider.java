package com.winitech.common.bean;

import com.winitech.common.exception.IllegalStatusException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * <pre>
 * com.winitech.common.bean
 * └ MultitenantConnectionSchemaProvider.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-08-20 13:15
 **/
@Slf4j
@Component
@ConditionalOnExpression("'${winitech.multitenant.type:}'.toUpperCase() == 'SCHEMA'")
public class MultitenantConnectionSchemaProvider implements MultiTenantConnectionProvider, HibernatePropertiesCustomizer {
	private final DataSource dataSource;

	private String schemaPrefix = "";

	@Value("${winitech.multitenant.schema.prefix:}")
	private void setSchemaPrefix(String prefix) {
		if (prefix != null && !prefix.isEmpty()) {
			this.schemaPrefix = prefix.toLowerCase();

			if (this.schemaPrefix.matches("[^a-z0-9_\\-]+")) {
				throw new IllegalStatusException("winitech.multitenant.schema.prefix에는 영문, 숫자, 밑줄(_), 하이픈(-)만 포함될 수 있습니다.");
			}
		}
	}

	private String sharedSchema = null;

	@Value("${winitech.multitenant.schema.shared-schema:}")
	private void setSharedSchema(String schema) {
		if (schema != null && !schema.isEmpty()) {
			this.sharedSchema = schema.toLowerCase();

			if (this.sharedSchema.matches("[^a-z0-9_\\-]+")) {
				throw new IllegalStatusException("winitech.multitenant.schema.shared-schema에는 영문, 숫자, 밑줄(_), 하이픈(-)만 포함될 수 있습니다.");
			}
		}
	}

	public MultitenantConnectionSchemaProvider(DataSource dataSource) throws SQLException {
		this.dataSource = dataSource;

		try (Connection connection = dataSource.getConnection()) {
			if (! connection.getMetaData().getDatabaseProductName().equalsIgnoreCase("postgresql")) {
				throw new IllegalStatusException("SCHEMA 방식 mutitenant는 PostgreSQL 데이터베이스에서만 지원됩니다.");
			}
		}
	}

	@Override
	public Connection getAnyConnection() throws SQLException {
		return getConnection(null);
	}

	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException {
		connection.close();
	}

	@Override
	public Connection getConnection(String tenantId) throws SQLException {
		//자원반환하므로 해제를 하면 안됨
		Connection connection = dataSource.getConnection();

		if (tenantId == null || tenantId.equals("default")) {
			// tenantId가 null이거나 "default"인 경우 기본 스키마에서 가져옴
			//connection.setSchema("public");
			try (Statement statement = connection.createStatement()) {
				statement.execute("SET search_path TO public");
			}
		} else {
			// connection.setSchema(tenantId);
			try (Statement statement = connection.createStatement()) {
				String sql = "SET search_path TO '" + schemaPrefix + tenantId + "'";

				if (sharedSchema != null) {
					sql += ", '" + sharedSchema + "'";
				}

				statement.execute(sql);
			}
		}

		return connection;
	}

	@Override
	public void releaseConnection(String s, Connection connection) throws SQLException {
		if (! "default".equals(s)) {
			log.debug("Releasing connection for tenant: {}", s);
		}

		connection.close();
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
		hibernateProperties.put(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
		hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
	}
}
