package com.winitech.apiGateway.common.filter;

import com.winitech.common.domain.common.CommonAuthorizationGroupPermissionInfo;
import com.winitech.common.domain.common.CommonMenuActionInfo;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.common.filter
 * └ AuthorizationFilterData.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-08 11:26
 **/
@Getter
public class AuthorizationFilterData {
	/**
	 * UserId => GroupId List 로 변환
	 */
	private Map<UUID, List<UUID>> userGroupListMap = new HashMap<>();
	/**
	 * GroupCode => GroupId 로 변환
	 */
	private Map<String, UUID> groupCodeToGroupMap = new HashMap<>();
	/**
	 * GroupId => MenuId => CommonAuthorizationGroupPermissionInfo
	 * 메뉴별 권한
	 */
	private Map<UUID, Map<UUID, CommonAuthorizationGroupPermissionInfo>> groupMenuPermissionMap = new HashMap<>();
	/**
	 * ProgramCode => MenuId 로 변환
	 */
	private Map<String, UUID> programCodeToMenuIdMap = new HashMap<>();
	/**
	 * MenuId => CommonMenuActionInfo List
	 * 메뉴별 액션
	 */
	private Map<UUID, List<CommonMenuActionInfo>> menuActionMap = new HashMap<>();

	public AuthorizationFilterData() {
		this.userGroupListMap = new HashMap<>();
		this.groupCodeToGroupMap = new HashMap<>();
		this.groupMenuPermissionMap = new HashMap<>();
		this.programCodeToMenuIdMap = new HashMap<>();
		this.menuActionMap = new HashMap<>();
	}
	
	public AuthorizationFilterData(
			Map<UUID, List<UUID>> userGroupListMap,
			Map<String, UUID> groupCodeToGroupMap, 
			Map<UUID, Map<UUID, CommonAuthorizationGroupPermissionInfo>> groupMenuPermissionMap, 
			Map<String, UUID> programCodeToMenuIdMap, 
			Map<UUID, List<CommonMenuActionInfo>> menuActionMap
	) {
		this.userGroupListMap = userGroupListMap;
		this.groupCodeToGroupMap = groupCodeToGroupMap;
		this.groupMenuPermissionMap = groupMenuPermissionMap;
		this.programCodeToMenuIdMap = programCodeToMenuIdMap;
		this.menuActionMap = menuActionMap;
	}
}
