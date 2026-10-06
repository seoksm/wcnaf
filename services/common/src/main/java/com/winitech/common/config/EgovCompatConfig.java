package com.winitech.common.config;

import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.trace.LeaveaTrace;
import org.egovframe.rte.fdl.cmmn.trace.handler.DefaultTraceHandler;
import org.egovframe.rte.fdl.cmmn.trace.handler.TraceHandler;
import org.egovframe.rte.fdl.cmmn.trace.manager.DefaultTraceHandleManager;
import org.egovframe.rte.fdl.cmmn.trace.manager.TraceHandlerService;
import org.egovframe.rte.psl.dataaccess.mapper.MapperConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

/**
 * 전자정부 프레임워크 호환성을 위한 설정 클래스
 * <pre>
 * com.winitech.common.config
 * └ EgovCompatConfig.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-06-30 16:26
 **/
@Slf4j
@Configuration
public class EgovCompatConfig {
	@Bean("mapperConfigurer")
	public MapperConfigurer mapperConfigurer() {
		log.info("MapperConfigurer bean is created.");
		MapperConfigurer mapperConfigurer = new MapperConfigurer();
		mapperConfigurer.setBasePackage("com.winitech.**.infrastructure.**");
		mapperConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
		return mapperConfigurer;
	}

	@Bean
	public LeaveaTrace leaveaTrace(DefaultTraceHandleManager traceHandleManager) {
		LeaveaTrace leaveaTrace = new LeaveaTrace();
		leaveaTrace.setTraceHandlerServices(new TraceHandlerService[]{traceHandleManager});
		return leaveaTrace;
	}

	@Bean
	public DefaultTraceHandleManager traceHandleManager(AntPathMatcher antPathMatcher, DefaultTraceHandler defaultTraceHandler) {
		DefaultTraceHandleManager defaultTraceHandleManager = new DefaultTraceHandleManager();
		defaultTraceHandleManager.setReqExpMatcher(antPathMatcher);
		defaultTraceHandleManager.setPatterns(new String[]{"*"});
		defaultTraceHandleManager.setHandlers(new TraceHandler[]{defaultTraceHandler});
		return defaultTraceHandleManager;
	}

	@Bean
	public AntPathMatcher antPathMatcher() {
		AntPathMatcher antPathMatcher = new AntPathMatcher();
		return antPathMatcher;
	}

	@Bean
	public DefaultTraceHandler defaultTraceHandler() {
		DefaultTraceHandler defaultTraceHandler = new DefaultTraceHandler();
		return defaultTraceHandler;
	}
}
