package com.winitech.common.config;

import com.winitech.common.bean.RefreshableSqlSessionFactoryBean;
import com.winitech.common.config.properties.MultitenantProperties;
import com.winitech.common.exception.InvalidParamException;
import com.winitech.common.library.mybatisUtils.MybatisMultitenantDataSourceProxy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.egovframe.rte.psl.dataaccess.mapper.Mapper;
import org.egovframe.rte.psl.dataaccess.mapper.MapperConfigurer;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * <pre>
 * com.winitech.common.config
 * └ MyBatisConfig.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-07 15:54
 **/
@Configuration
@MapperScan("com.winitech.**.infrastructure.**")
@RequiredArgsConstructor
@Slf4j
public class MyBatisConfig {
	@Value("${winitech.mybatis.config-location:}")
	private String myBatisConfigLocation;
	
	@Value("${winitech.mybatis.mapper-locations:}")
	private String myBatisMapperLocations;
	
	@Value("${winitech.mybatis.use-auto-mapper-refresh:false}")
	private boolean useMyBatisAutoMapperRefresh;
	
	@Value("${winitech.mybatis.use-auto-mapper-refresh-interval-in-ms:500}")
	private Integer myBatisAutoMapperRefreshIntervalInMs;
	
	private final MultitenantProperties multitenantProperties;
	
//	@Bean("mapperConfigurer")
//	public MapperConfigurer mapperConfigurer() {
//		log.info("MapperConfigurer bean is created.");
//		MapperConfigurer mapperConfigurer = new MapperConfigurer();
//		mapperConfigurer.setBasePackage("com.winitech.**.infrastructure.**");
//		mapperConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
//		mapperConfigurer.setLazyInitialization("true");
//		// mapperConfigurer.setAnnotationClass(Mapper.class);
//		return mapperConfigurer;
//	}
	
	@Bean("sqlSessionFactory")
	@ConditionalOnExpression("${winitech.mybatis.enabled:false}")
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean factory;
		
		if (useMyBatisAutoMapperRefresh) {
			log.info("MyBatis auto mapper refresh is enabled. (RefreshableSqlSessionFactoryBean)");

			try {
				// devtools가 있는지 확인
				if (Class.forName("org.springframework.boot.devtools.settings.DevToolsSettings") != null) {
					log.error("Devtool is found.");
					log.error("MyBatis auto mapper refresh cannot be used with spring-boot-devtools. Please remove spring-boot-devtools from your dependencies.");
					log.error("Or set 'winitech.mybatis.use-auto-mapper-refresh=false' to application.properties.");
					
					throw new InvalidParamException("MyBatis auto mapper refresh cannot be used with spring-boot-devtools. Please remove spring-boot-devtools from your dependencies.");
				}
			} catch (ClassNotFoundException _ignored) {
				// devtools가 없는 경우 무시
				log.debug("Ok. Devtool is not found.");
			}
			
			if (myBatisAutoMapperRefreshIntervalInMs == null || myBatisAutoMapperRefreshIntervalInMs < 100) {
				log.warn("winitech.mybatis.use-auto-mapper-refresh-timeout-in-ms should be greater than 100ms. Set to 100ms.");
				myBatisAutoMapperRefreshIntervalInMs = 100;
			}
			
			factory = new RefreshableSqlSessionFactoryBean(myBatisAutoMapperRefreshIntervalInMs);
		} else {
			log.info("MyBatis auto mapper refresh is disabled.");
			
			factory = new SqlSessionFactoryBean();
		}
		
		if (multitenantProperties.getType() == MultitenantProperties.MultitenantType.DATABASE) {
			// 멀티테넌트 방식이 database 일 때는 MybatisMultitenantDataSourceProxy 사용
			factory.setDataSource(new MybatisMultitenantDataSourceProxy(dataSource));
		} else {
			// 기본 DataSource 설정
			factory.setDataSource(dataSource);
		}
		
		factory.setConfigLocation(new PathMatchingResourcePatternResolver().getResource(myBatisConfigLocation));
		factory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(myBatisMapperLocations));
		
		// mapper-config.xml에서 설정하도록 변경. 전역으로 하고 싶을때는 주석해제
		//factory.setTypeAliasesPackage("com.winitech.**.domain.**");
		
		return factory.getObject();
	}
}
