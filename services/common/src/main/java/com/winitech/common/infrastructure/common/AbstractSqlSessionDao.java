package com.winitech.common.infrastructure.common;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;

import javax.annotation.Resource;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ AbstractSqlSessionDao.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-08 14:46
 **/
public abstract class AbstractSqlSessionDao extends EgovAbstractMapper {
	@Resource
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		super.setSqlSessionFactory(sqlSessionFactory);
	}

	public int insert(String queryId) {
		return this.getSqlSession().insert(queryId);
	}

	public int insert(String queryId, Object parameterObject) {
		return this.getSqlSession().insert(queryId, parameterObject);
	}

	public int update(String queryId) {
		return this.getSqlSession().update(queryId);
	}

	public int update(String queryId, Object parameterObject) {
		return this.getSqlSession().update(queryId, parameterObject);
	}

	public int delete(String queryId) {
		return this.getSqlSession().delete(queryId);
	}

	public int delete(String queryId, Object parameterObject) {
		return this.getSqlSession().delete(queryId, parameterObject);
	}

	public <T> T selectOne(String queryId) {
		return (T)this.getSqlSession().selectOne(queryId);
	}

	public <T> T selectOne(String queryId, Object parameterObject) {
		return (T)this.getSqlSession().selectOne(queryId, parameterObject);
	}

	public <K, V> Map<K, V> selectMap(String queryId, String mapKey) {
		return this.getSqlSession().selectMap(queryId, mapKey);
	}

	public <K, V> Map<K, V> selectMap(String queryId, Object parameterObject, String mapKey) {
		return this.getSqlSession().selectMap(queryId, parameterObject, mapKey);
	}

	public <K, V> Map<K, V> selectMap(String queryId, Object parameterObject, String mapKey, RowBounds rowBounds) {
		return this.getSqlSession().selectMap(queryId, parameterObject, mapKey, rowBounds);
	}

	public <E> List<E> selectList(String queryId) {
		return this.getSqlSession().selectList(queryId);
	}

	public <E> List<E> selectList(String queryId, Object parameterObject) {
		return this.getSqlSession().selectList(queryId, parameterObject);
	}

	public <E> List<E> selectList(String queryId, Object parameterObject, RowBounds rowBounds) {
		return this.getSqlSession().selectList(queryId, parameterObject, rowBounds);
	}

	public List<?> listWithPaging(String queryId, Object parameterObject, int pageIndex, int pageSize) {
		int skipResults = pageIndex * pageSize;
		RowBounds rowBounds = new RowBounds(skipResults, pageSize);
		return this.getSqlSession().selectList(queryId, parameterObject, rowBounds);
	}

	public void listToOutUsingResultHandler(String queryId, ResultHandler handler) {
		this.getSqlSession().select(queryId, handler);
	}
}
