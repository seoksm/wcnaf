package com.winitech.common.infrastructure.common;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Repository;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonSqlSessionDao.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-08 14:51
 **/
@Repository
@ConditionalOnExpression("${winitech.mybatis.enabled:false}")
public class CommonSqlSessionDao extends AbstractSqlSessionDao {
	public CommonSqlSessionDao() {
	}
}
