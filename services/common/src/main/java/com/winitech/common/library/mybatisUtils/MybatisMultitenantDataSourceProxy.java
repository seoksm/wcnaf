package com.winitech.common.library.mybatisUtils;

import com.winitech.common.library.core.CurrentTenantHolder;
import com.winitech.common.library.core.MultitenantDatabaseConnectionPoolManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.ConnectionProxy;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * <pre>
 * com.winitech.common.library.mybatisUtils
 * └ MybatisMultitenantDataSourceProxy.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-09-01 10:42
 **/
@Slf4j
public class MybatisMultitenantDataSourceProxy extends DelegatingDataSource {

	private boolean reobtainTransactionalConnections = false;


	public MybatisMultitenantDataSourceProxy() {
	}

	public MybatisMultitenantDataSourceProxy(DataSource targetDataSource) {
		super(targetDataSource);
	}

	public void setReobtainTransactionalConnections(boolean reobtainTransactionalConnections) {
		this.reobtainTransactionalConnections = reobtainTransactionalConnections;
	}


	@Override
	public Connection getConnection() throws SQLException {
		return getTransactionAwareConnectionProxy(obtainTargetDataSource());
	}

	protected Connection getTransactionAwareConnectionProxy(DataSource targetDataSource) {
		return (Connection) Proxy.newProxyInstance(
				ConnectionProxy.class.getClassLoader(),
				new Class<?>[] {ConnectionProxy.class},
				new MybatisMultitenantDataSourceProxy.TransactionAwareInvocationHandler(targetDataSource));
	}

	protected boolean shouldObtainFixedConnection(DataSource targetDataSource) {
		return (!TransactionSynchronizationManager.isSynchronizationActive() ||
				!this.reobtainTransactionalConnections);
	}


	private class TransactionAwareInvocationHandler implements InvocationHandler {

		private final DataSource targetDataSource;

		@Nullable
		private Connection target;

		private boolean closed = false;

		public TransactionAwareInvocationHandler(DataSource targetDataSource) {
			this.targetDataSource = targetDataSource;
			/*CurrentTenantHolder currentTenantHolder = CurrentTenantHolder.get();

			try {
				this.targetDataSource = MultitenantDatabaseConnectionPoolManager.getInstance().getDataSource(currentTenantHolder.getTenantId());
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}*/
		}

		@Override
		@Nullable
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			switch (method.getName()) {
				case "equals":
					return (proxy == args[0]);
				case "hashCode":
					return System.identityHashCode(proxy);
				case "toString":
					StringBuilder sb = new StringBuilder("Transaction-aware proxy for target Connection ");
					if (this.target != null) {
						sb.append('[').append(this.target.toString()).append(']');
					}
					else {
						sb.append(" from DataSource [").append(this.targetDataSource).append(']');
					}
					return sb.toString();
				case "close":
					DataSourceUtils.doReleaseConnection(this.target, this.targetDataSource);
					this.closed = true;
					return null;
				case "isClosed":
					return this.closed;
				case "unwrap":
					if (((Class<?>) args[0]).isInstance(proxy)) {
						return proxy;
					}
					break;
				case "isWrapperFor":
					if (((Class<?>) args[0]).isInstance(proxy)) {
						return true;
					}
					break;
			}

			if (this.target == null) {
				if (method.getName().equals("getWarnings") || method.getName().equals("clearWarnings")) {
					return null;
				}
				if (this.closed) {
					throw new SQLException("Connection handle already closed");
				}
				if (shouldObtainFixedConnection(this.targetDataSource)) {
					this.target = DataSourceUtils.doGetConnection(this.targetDataSource);
				}
			}
			Connection actualTarget = this.target;
			if (actualTarget == null) {
				actualTarget = DataSourceUtils.doGetConnection(this.targetDataSource);
			}

			if (method.getName().equals("getTargetConnection")) {
				return actualTarget;
			}

			try {
				Object retVal = method.invoke(actualTarget, args);

				if (retVal instanceof Statement) {
					DataSourceUtils.applyTransactionTimeout((Statement) retVal, this.targetDataSource);
				}

				return retVal;
			}
			catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			}
			finally {
				if (actualTarget != this.target) {
					DataSourceUtils.doReleaseConnection(actualTarget, this.targetDataSource);
				}
			}
		}
	}
}
