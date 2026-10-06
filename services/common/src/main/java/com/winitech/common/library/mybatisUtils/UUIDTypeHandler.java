package com.winitech.common.library.mybatisUtils;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.nio.ByteBuffer;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.library.mybatisUtils
 * └ UUIDTypeHandler.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-06-17 09:24
 **/
public class UUIDTypeHandler extends BaseTypeHandler<UUID> {
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType) throws SQLException {
		// ps.setString(i, parameter.toString());
		
		ps.setObject(i, parameter);
	}

	@Override
	public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String value = rs.getString(columnName);
		if (value == null || value.isEmpty()) {
			return null;
		}
		return UUID.fromString(value);
	}

	@Override
	public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String value = rs.getString(columnIndex);
		if (value == null || value.isEmpty()) {
			return null;
		}
		return UUID.fromString(value);
	}

	@Override
	public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String value = cs.getString(columnIndex);
		if (value == null || value.isEmpty()) {
			return null;
		}
		return UUID.fromString(value);
	}
}
