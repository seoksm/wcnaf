package com.winitech.common.library.mybatisUtils;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * <pre>
 * com.winitech.common.library.mybatisUtils
 * └ OffsetDateTimeTypeHandler.java
 * </pre>
 * 
 * MyBatis에서 OffsetDateTime 타입을 처리하기 위한 TypeHandler입니다.
 * 사용하기 위해서는 다음과 같이 MyBatis 설정 파일에 등록해야 합니다:
 * <pre><code>
 * 	&lt;typeHandlers>
 * 		&lt;typeHandler handler="com.winitech.common.library.mybatisUtils.OffsetDateTimeTypeHandler" javaType="java.time.OffsetDateTime" />
 * 	&lt;/typeHandlers>
 * </code></pre>                 
 * @author : coding (클라우드팀)
 * @since : 2025-07-22 13:32
 **/
public class OffsetDateTimeTypeHandler extends BaseTypeHandler<OffsetDateTime> {
	private final ZoneOffset systemTimeZoneOffset = ZoneId.systemDefault().getRules().getOffset(Instant.now());

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, OffsetDateTime parameter, JdbcType jdbcType) throws SQLException {
		ps.setObject(i, parameter);
	}

	@Override
	public OffsetDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
		int columnIndex = rs.findColumn(columnName);

		// timezone 정보가 없는 경우 서버의 타임존 오프셋을 적용
		return getNullableResult(rs, columnIndex);
	}

	@Override
	public OffsetDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		
		int columnType = metaData.getColumnType(columnIndex);

		if (columnType == java.sql.Types.TIMESTAMP_WITH_TIMEZONE) {
			return rs.getObject(columnIndex, OffsetDateTime.class);
		} else if ("timestamptz".equals(metaData.getColumnTypeName(columnIndex))) {
			// PostgreSQL의 timestamptz 타입 처리
			return rs.getObject(columnIndex, OffsetDateTime.class);
		}
	
		// timezone 정보가 없는 경우 서버의 타임존 오프셋을 적용
		return rs.getObject(columnIndex, OffsetDateTime.class).with(systemTimeZoneOffset);
	}

	@Override
	public OffsetDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		ResultSetMetaData metaData = cs.getMetaData();
		
		int columnType = metaData.getColumnType(columnIndex);

		if (columnType == java.sql.Types.TIMESTAMP_WITH_TIMEZONE) {
			return cs.getObject(columnIndex, OffsetDateTime.class);
		} else if ("timestamptz".equals(metaData.getColumnTypeName(columnIndex))) {
			// PostgreSQL의 timestamptz 타입 처리
			return cs.getObject(columnIndex, OffsetDateTime.class);
		}

		// timezone 정보가 없는 경우 서버의 타임존 오프셋을 적용
		return cs.getObject(columnIndex, OffsetDateTime.class).with(systemTimeZoneOffset);
	}
}
