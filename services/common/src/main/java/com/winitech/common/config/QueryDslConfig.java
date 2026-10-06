package com.winitech.common.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * <pre>
 * com.winitech.common.config
 * └ QueryDslConfig.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-23 13:22
 **/
@Configuration
public class QueryDslConfig {
	@PersistenceContext
	private EntityManager entityManager;
	
	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(entityManager);
	}
}
