package com.winitech.common.library.core;

import org.quartz.SchedulerConfigException;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerSignaler;
import org.quartz.utils.ConnectionProvider;
import org.quartz.utils.DBConnectionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * <pre>
 * com.winitech.common.library.core
 * └ QuartzMultitenantAwareJobStore.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-08-20 17:44
 **/
public class QuartzMultitenantAwareJobStore extends LocalDataSourceJobStore {
	@Override
	public void initialize(ClassLoadHelper loadHelper, SchedulerSignaler signaler) throws SchedulerConfigException {
		super.initialize(loadHelper, signaler);

		final DataSource dataSource = SchedulerFactoryBean.getConfigTimeDataSource();
		if (dataSource == null) {
			throw new SchedulerConfigException("No local DataSource found for configuration - " +
					"'dataSource' property must be set on SchedulerFactoryBean");
		}

		DBConnectionManager.getInstance().addConnectionProvider(
				TX_DATA_SOURCE_PREFIX + getInstanceName(),
				new ConnectionProvider() {
					@Override
					public Connection getConnection() throws SQLException {
						Connection connection = DataSourceUtils.doGetConnection(dataSource);
						// winitech.multitenant.type = SCHEMA인 경우를 위해 기본 스키마로 변경
						connection.setSchema(null);
						return connection;
					}
					@Override
					public void shutdown() {
						// Do nothing - a Spring-managed DataSource has its own lifecycle.
					}
					@Override
					public void initialize() {
						// Do nothing - a Spring-managed DataSource has its own lifecycle.
					}
				}
		);

		DataSource nonTxDataSource = SchedulerFactoryBean.getConfigTimeNonTransactionalDataSource();
		final DataSource nonTxDataSourceToUse = (nonTxDataSource != null ? nonTxDataSource : dataSource);

		setNonManagedTXDataSource(NON_TX_DATA_SOURCE_PREFIX + getInstanceName());

		DBConnectionManager.getInstance().addConnectionProvider(
				NON_TX_DATA_SOURCE_PREFIX + getInstanceName(),
				new ConnectionProvider() {
					@Override
					public Connection getConnection() throws SQLException {
						Connection connection = nonTxDataSourceToUse.getConnection();
						// winitech.multitenant.type = SCHEMA인 경우를 위해 기본 스키마로 변경
						connection.setSchema(null);
						return connection;
					}
					@Override
					public void shutdown() {
						// Do nothing - a Spring-managed DataSource has its own lifecycle.
					}
					@Override
					public void initialize() {
						// Do nothing - a Spring-managed DataSource has its own lifecycle.
					}
				}
		);
	}
}
