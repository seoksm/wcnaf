package com.winitech.common.library.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 * com.winitech.common.library.core
 * └ MultitenantDatabaseConnectionPoolManager.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-09-01 10:23
 **/
@Slf4j
public class MultitenantDatabaseConnectionPoolManager {
	// 싱글톤 인스턴스 생성을 위한 락 오브젝트
	private static Object oLock = new Object();
	
	// 싱글톤 인스턴스
	private static MultitenantDatabaseConnectionPoolManager instance;
	
	// 싱글톤 인스턴스 반환
	public static MultitenantDatabaseConnectionPoolManager getInstance() {
		if (instance == null) {
			throw new IllegalStateException("MultitenantDatabaseConnectionPoolManager is not initialized. Use MultitenantDatabaseConnectionPoolManager.initialize() method first.");
		}
		return instance;
	}

	// 싱글톤 인스턴스 초기화
	// application startup 시점에 한 번만 호출되어야 함
	public static void initialize(DataSource dataSource,
								  String serviceName,
								  String multitenantDatabasePrefix,
								  Integer multitenantDatabasePoolCleanupIntervalInSeconds,
								  Integer multitenantDatabasePoolIdleInSeconds) {
		synchronized (oLock) {
			if (instance != null) {
				throw new IllegalStateException("MultitenantDatabaseConnectionPoolManager is already initialized.");
			}
			
			instance = new MultitenantDatabaseConnectionPoolManager(
					dataSource, 
					serviceName,
					multitenantDatabasePrefix,
					multitenantDatabasePoolCleanupIntervalInSeconds,
					multitenantDatabasePoolIdleInSeconds
			);
		}
	}

	private final String serviceName;
	private final String multitenantDatabasePrefix;
	// 멀티테넌트 방식이 database 일 때 connection pool 정리 시도 주기 (초)
	private final Integer multitenantDatabasePoolCleanupIntervalInSeconds;
	// 멀티테넌트 방식이 database 일 때 지정된 시간 이상 사용되지 않으면 connection pool 삭제 (초)
	private final Integer multitenantDatabasePoolIdleInSeconds;

	private final DataSource dataSource;
	private Thread cleanUpThread;

	private ConcurrentHashMap<String, DataSource> tenantDataSourceMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, OffsetDateTime> tenantLastAccessTimeMap = new ConcurrentHashMap<>();
		
	protected MultitenantDatabaseConnectionPoolManager(
			DataSource dataSource,
			String serviceName,
			String multitenantDatabasePrefix,
			Integer multitenantDatabasePoolCleanupIntervalInSeconds,
			Integer multitenantDatabasePoolIdleInSeconds
	) {
		this.dataSource = dataSource;
		this.serviceName = serviceName;
		this.multitenantDatabasePrefix = multitenantDatabasePrefix;
		this.multitenantDatabasePoolCleanupIntervalInSeconds = multitenantDatabasePoolCleanupIntervalInSeconds;
		this.multitenantDatabasePoolIdleInSeconds = multitenantDatabasePoolIdleInSeconds;

		//setupCleanupThread
		if (multitenantDatabasePoolCleanupIntervalInSeconds != null && multitenantDatabasePoolCleanupIntervalInSeconds > 0 && multitenantDatabasePoolIdleInSeconds != null) {
			cleanUpThread = new Thread(() -> {
				while (true) {
					try {
						// winitech.multitenant.database.pool-cleanup-interval-in-seconds에 지정된 시간마다 정리 시도
						Thread.sleep(multitenantDatabasePoolCleanupIntervalInSeconds * 1000);
						cleanUp(); // 사용하지 않는 데이터소스 제거
					} catch (InterruptedException e) {
						log.error("Error in MultitenantConnectionDatabaseProvider cleanup thread", e);
						Thread.currentThread().interrupt();
					}
				}
			});

			//분석도구용처리
			if(cleanUpThread!=null){
				cleanUpThread.start();
			}

		}

	}
	
	public DataSource getDataSource(String tenantId) throws SQLException {
		if (tenantId == null || tenantId.equals("default")) {
			// tenantId가 null이거나 "default"인 경우 기본 데이터소스에서 연결을 가져옴
			return dataSource;
		}

		tenantLastAccessTimeMap.put(tenantId, OffsetDateTime.now());

		DataSource tenantDataSource = tenantDataSourceMap.get(tenantId);

		if (tenantDataSource == null) {
			// tenant에 대한 dataSource가 없으면 생성
			tenantDataSource = createDataSource(tenantId);
		}

		return tenantDataSource;
	}

	public Connection getConnection(String tenantId) throws SQLException {
		return getDataSource(tenantId).getConnection();
	}
	
	public void releaseConnection(String tenantId, Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}

	private DataSource createDataSource(String tenantId) throws SQLException {
		synchronized (this) {
			DataSource tenantDataSource = tenantDataSourceMap.get(tenantId);

			if (tenantDataSource != null) {
				return tenantDataSource;
			}

			log.info("Creating DataSource for tenant: {}", tenantId);

			if (!(dataSource instanceof HikariDataSource)) {
				throw new SQLException("Unsupported DataSource type: " + dataSource.getClass().getName() + ". Only HikariDataSource is supported.");
			}

			HikariConfig config = new HikariConfig();
			HikariDataSource orgDataSource = (HikariDataSource) dataSource;

			String nextJdbcUrl;

			try {
				String jdbcUrl = orgDataSource.getJdbcUrl();
				int sepIdx = jdbcUrl.indexOf("://");
				URI prevUri = new URI("jdbc" + jdbcUrl.substring(sepIdx));

				String newDatabaseName = getTenantDatabaseName(tenantId);

				URI nextUri = new URI(jdbcUrl.substring(0, sepIdx), prevUri.getUserInfo(), prevUri.getHost(), prevUri.getPort(), "/" + newDatabaseName, prevUri.getQuery(), prevUri.getFragment());

				nextJdbcUrl = nextUri.toString();
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}

			config.setCatalog(orgDataSource.getCatalog());
			config.setConnectionInitSql(orgDataSource.getConnectionInitSql());
			config.setConnectionTestQuery(orgDataSource.getConnectionTestQuery());
			config.setConnectionTimeout(orgDataSource.getConnectionTimeout());
			config.setDataSource(orgDataSource.getDataSource());
			config.setDataSourceClassName(orgDataSource.getDataSourceClassName());
			config.setDataSourceJNDI(orgDataSource.getDataSourceJNDI());
			config.setDataSourceProperties(orgDataSource.getDataSourceProperties());
			config.setDriverClassName(orgDataSource.getDriverClassName());
			config.setHealthCheckProperties(orgDataSource.getHealthCheckProperties());
			config.setHealthCheckRegistry(orgDataSource.getHealthCheckRegistry());
			config.setIdleTimeout(orgDataSource.getIdleTimeout());
			config.setInitializationFailTimeout(orgDataSource.getInitializationFailTimeout());
			config.setJdbcUrl(orgDataSource.getJdbcUrl());
			config.setKeepaliveTime(orgDataSource.getKeepaliveTime());
			config.setLeakDetectionThreshold(orgDataSource.getLeakDetectionThreshold());
			config.setMaxLifetime(orgDataSource.getMaxLifetime());
			config.setMaximumPoolSize(orgDataSource.getMaximumPoolSize());
			config.setMetricRegistry(orgDataSource.getMetricRegistry());
			config.setMetricsTrackerFactory(orgDataSource.getMetricsTrackerFactory());
			config.setMinimumIdle(orgDataSource.getMinimumIdle());
			config.setPassword(orgDataSource.getPassword());
			config.setPoolName(orgDataSource.getPoolName());
			config.setScheduledExecutor(orgDataSource.getScheduledExecutor());
			config.setSchema(orgDataSource.getSchema());
			config.setThreadFactory(orgDataSource.getThreadFactory());
			config.setTransactionIsolation(orgDataSource.getTransactionIsolation());
			config.setUsername(orgDataSource.getUsername());
			config.setValidationTimeout(orgDataSource.getValidationTimeout());

			config.setJdbcUrl(nextJdbcUrl); // URL만 수정

			tenantDataSource = new HikariDataSource(config);

			tenantDataSourceMap.put(tenantId, tenantDataSource);
			return tenantDataSource;
		}
	}

	public String getTenantDatabaseName(String tenantId) {
		String newDatabaseName = multitenantDatabasePrefix + tenantId.toLowerCase();
		if (serviceName != null && !serviceName.isBlank()) {
			newDatabaseName += "-" + serviceName.toLowerCase();
		}
		return newDatabaseName;
	}

	private void cleanUp() {
		// 마지막 접속 시간 기준으로 오래된 데이터소스 제거
		OffsetDateTime now = OffsetDateTime.now();

		synchronized (this) {
			tenantLastAccessTimeMap.entrySet().removeIf(entry -> {
				String tenantId = entry.getKey();
				OffsetDateTime lastAccessTime = entry.getValue();

				// winitech.multitenant.database.pool-cleanup-idle-in-seconds에 지정된 시간 컨넥션 풀이 사용 안 되면 정리
				if (lastAccessTime.isBefore(now.minusSeconds(multitenantDatabasePoolIdleInSeconds))) {
					log.info("Removing unused DataSource for tenant: {}", tenantId);
					DataSource ds = tenantDataSourceMap.remove(tenantId);
					if (ds instanceof HikariDataSource) {
						((HikariDataSource) ds).close();
					}
					return true;
				}
				return false;
			});
		}
	}
}
