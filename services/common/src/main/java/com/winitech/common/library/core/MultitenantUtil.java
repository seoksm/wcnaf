package com.winitech.common.library.core;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * <pre>
 * com.winitech.common.library.core
 * └ MultitenantUtil.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-09-18 23:32
 **/
public class MultitenantUtil {
	/**
	 * 테넌트 ID로 테넌트 DB명 조회
	 * @param tenantId
	 * @return
	 */
	public static String getTenantDatabaseName(String tenantId) {
		return MultitenantDatabaseConnectionPoolManager.getInstance().getTenantDatabaseName(tenantId);
	}

	/**
	 * 특정 테넌트용 JdbcTemplate 생성. 이 방식은 컨넥션 풀을 사용하지 않고 일시적으로 연결만 맺습니다. 
	 * @param orgDataSource
	 * @param tenantId
	 * @return
	 * @throws SQLException
	 */
	public static JdbcTemplate getJdbcTemplate(DataSource orgDataSource, String tenantId) throws SQLException {
		if (!(orgDataSource instanceof HikariDataSource)) {
			throw new SQLException("Unsupported DataSource type: " + orgDataSource.getClass().getName() + ". Only HikariDataSource is supported.");
		}

		HikariDataSource hikariDataSource = (HikariDataSource) orgDataSource;

		String nextJdbcUrl;

		try {
			String jdbcUrl = hikariDataSource.getJdbcUrl();
			int sepIdx = jdbcUrl.indexOf("://");
			URI prevUri = new URI("jdbc" + jdbcUrl.substring(sepIdx));

			String newDatabaseName = getTenantDatabaseName(tenantId);

			URI nextUri = new URI(jdbcUrl.substring(0, sepIdx), prevUri.getUserInfo(), prevUri.getHost(), prevUri.getPort(), "/" + newDatabaseName, prevUri.getQuery(), prevUri.getFragment());

			nextJdbcUrl = nextUri.toString();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		
		dataSource.setDriverClassName(hikariDataSource.getDriverClassName());
		dataSource.setUrl(nextJdbcUrl);
		dataSource.setUsername(hikariDataSource.getUsername());
		dataSource.setPassword(hikariDataSource.getPassword());

		return new JdbcTemplate(dataSource);
	}

	/**
	 * 특정 테넌트로 임시 변경하여 Runnable 실행
	 * ⚠️ 주의!! 이 방식으로 테넌트를 변경해서 실행하면, 테넌트 별로 컨넥션 풀이 생성되고
	 * 타임아웃이 될때까지 컨넥션 풀이 유지됩니다. 
	 * 가능하면 MultitenantUtil.getJdbcTemplate() 방식을 사용하시기 바랍니다.
	 * @param organizationId
	 * @param organizationCode
	 * @param runnable
	 */
	public static void runAsTenant(UUID organizationId, String organizationCode, Runnable runnable) {
		CurrentTenantHolder currentTenantHolder = CurrentTenantHolder.get();

		try {
			// 테넌트 임시 변경
			CurrentTenantHolder.set(organizationId, organizationCode);

			runnable.run();
		} finally {
			// 원래 테넌트로 복원
			CurrentTenantHolder.set(currentTenantHolder.getOrganizationId(), currentTenantHolder.getTenantId());
		}
	}
}
