package com.winitech.common.library;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.library.log
 * └ WiniConst.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-01-16 11:46
 **/
public class WiniConst {
	/**
	 * 기본 조직 UUID (문자열)
	 */
	public static final String DEFAULT_ORG_ID = "00000000-0000-0000-0000-000000000000";
	/**
	 * 기본 조직 UUID
	 */
	public static final UUID DEFAULT_ORG_UUID = UUID.fromString(DEFAULT_ORG_ID);
	/**
	 * 기본 조직명
	 */
	public static final String DEFAULT_ORG_NAME = "Default";
	/**
	 * 기본 사용자 그룹 고유번호
	 */
	public static final Long DEFAULT_GROUP_ID = -1L;
	/**
	 * 기본 사용자 그룹 코드
	 */
	public static final String DEFAULT_GROUP_CODE = "GUEST";
	/**
	 * 기본 사용자 그룹명
	 */
	public static final String DEFAULT_GROUP_NAME = "Guest";
}
