package com.winitech.common.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * com.winitech.common.config
 * └ MultitenantConfig.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-09-16 16:44
 **/
@Configuration
public class MultitenantConfig implements InitializingBean {
	@Value("${winitech.multitenant.type:}")
	private String multitenantType;

	@Value("${spring.quartz.job-store-type:}")
	private String quartzJobStoreType;

	@Value("${spring.quartz.properties.org.quartz.jobStore.class:}")
	private String quartzJobStoreClass;

	@Value("${spring.quartz.properties.org.quartz.jobStore.dontSetAutoCommitFalse:}")
	private String quartzJobStoreDontSetAutoCommitFalse;

	@Override
	public void afterPropertiesSet() throws Exception {
		checkMultitenantConfiguration();
	}

	private void checkMultitenantConfiguration() {
		if ("DATABASE".equalsIgnoreCase(multitenantType) && "JDBC".equalsIgnoreCase(quartzJobStoreType)) {
			if (quartzJobStoreClass == null || !quartzJobStoreClass.equals("com.winitech.common.library.core.QuartzMultitenantAwareJobStore")) {
				throw new IllegalArgumentException("Application property Error!!\n멀티테넌트가 DATABASE 방식일때는 spring.quartz.properties.org.quartz.jobStore.class 속성은 \"com.winitech.common.library.core.QuartzMultitenantAwareJobStore\" 로 설정해야 합니다."
						+ "\nIf multitenant type is DATABASE, spring.quartz.properties.org.quartz.jobStore.class must be \"com.winitech.common.library.core.QuartzMultitenantAwareJobStore.\"");
			}

			if (quartzJobStoreDontSetAutoCommitFalse == null || !quartzJobStoreDontSetAutoCommitFalse.equalsIgnoreCase("false")) {
				throw new IllegalArgumentException("Application property Error!!\n멀티테넌트가 DATABASE 방식일때는 spring.quartz.properties.org.quartz.jobStore.dontSetAutoCommitFalse 속성은 false 로 설정해야 합니다."
						+ "\nIf multitenant type is DATABASE, spring.quartz.properties.org.quartz.jobStore.dontSetAutoCommitFalse must be false.");
			}
		} else {
			if ("com.winitech.common.library.core.QuartzMultitenantAwareJobStore".equals(quartzJobStoreClass)) {
				throw new IllegalArgumentException("Application property Error!!\n멀티테넌트가 DATABASE 방식이 아닐때는 spring.quartz.properties.org.quartz.jobStore.class 속성은 제거해주세요."
						+ "\nIf multitenant type is not DATABASE, please remove spring.quartz.properties.org.quartz.jobStore.class.");
			}

			if ("false".equalsIgnoreCase(quartzJobStoreDontSetAutoCommitFalse)) {
				throw new IllegalArgumentException("Application property Error!!\n멀티테넌트가 DATABASE 방식이 아닐때는 spring.quartz.properties.org.quartz.jobStore.dontSetAutoCommitFalse 속성은 제거해주세요."
						+ "\nIf multitenant type is not DATABASE, please remove spring.quartz.properties.org.quartz.jobStore.dontSetAutoCommitFalse.");
			}
		}
	}
}
