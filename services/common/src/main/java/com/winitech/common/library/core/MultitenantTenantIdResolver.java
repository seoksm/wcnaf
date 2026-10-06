package com.winitech.common.library.core;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;

/**
 * <pre>
 * com.winitech.common.library.core
 * └ MultitenantTenantIdResolver.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-09-01 10:48
 **/
@Slf4j
public class MultitenantTenantIdResolver {
	// 싱글톤 인스턴스 생성을 위한 락 오브젝트
	private static Object oLock = new Object();

	// 싱글톤 인스턴스
	private static MultitenantTenantIdResolver instance;

	// 싱글톤 인스턴스 반환
	public static MultitenantTenantIdResolver getInstance() {
		if (instance == null) {
			throw new IllegalStateException("MultitenantTenantIdResolver is not initialized. Use MultitenantTenantIdResolver.initialize() method first.");
		}
		return instance;
	}

	// 싱글톤 인스턴스 초기화
	// application startup 시점에 한 번만 호출되어야 함
	public static void initialize(DataSource dataSource) {
		synchronized (oLock) {
			if (instance != null) {
				throw new IllegalStateException("MultitenantTenantIdResolver is already initialized.");
			}

			instance = new MultitenantTenantIdResolver(dataSource);
		}
	}
	
	private final DataSource dataSource;
	
	private MultitenantTenantIdResolver(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
