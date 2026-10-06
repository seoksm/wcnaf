package com.winitech.system.infrastructure.menuPermission;

import com.winitech.common.domain.common.CommonAuthorizationGroupPermissionInfo;
import com.winitech.common.domain.common.CommonMenuAction;
import com.winitech.system.domain.menuPermission.MenuPermission;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.menuPermission
 * └ MenuPermission.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-18 11:08
 **/
public interface MenuPermissionRepository extends JpaRepository<MenuPermission, UUID> {
	List<MenuPermission> findAllByAuthorizationGroupIdOrderByIdDesc(UUID authorizationGroupId);

	List<MenuPermission> findAllByAuthorizationGroupIdOrderByIdDesc(UUID authorizationGroupId, Sort sort);

	Optional<MenuPermission> findByIdAndAuthorizationGroupId(UUID menuPermissionId, UUID authorizationGroupId);

	boolean existsByMenuIdAndIdNot(UUID menuId, UUID menuPermissionid);

	@Query("" +
			"SELECT new com.winitech.common.domain.common.CommonAuthorizationGroupPermissionInfo(" +
			"			mp.authorizationGroup.id," +
			"			mp.menu.id," +
			"			mp.authorizationGroup.groupCode, " +
			"			CAST(mp.selectStatus string)," +
			"			CAST(mp.insertStatus string)," +
			"			CAST(mp.updateStatus string)," +
			"			CAST(mp.deleteStatus string)," +
			"			CAST(mp.printStatus string)," +
			"			CAST(mp.downStatus string)," +
			"			CAST(mp.manageStatus string)," +
			"			CAST(mp.custom1Status string)," +
			"			CAST(mp.custom2Status string)," +
			"			CAST(mp.custom3Status string)" +
			"		)" +
			"  FROM MenuPermission mp" +
			" WHERE mp.authorizationGroup.id IN :authorizationGroupIdList")
	List<CommonAuthorizationGroupPermissionInfo> getAuthorizationGroupPermissionByAuthorizationGroupIdList(
			@Param("authorizationGroupIdList") List<UUID> authorizationGroupIdList
	);

	@Query("" +
			"SELECT PA.uri" +
			"  FROM AuthorizationGroup AG" +
			"  LEFT JOIN AuthorizationGroupUser AGU " +
			"	 ON AGU.authorizationGroup = AG " +
			"		AND AGU.user.id = :userId " +
			"		AND AG.systemStatus = 'ENABLE' " +
			"		AND AG.status = 'ENABLE'" +
			"  JOIN MenuPermission MP ON AG = MP.authorizationGroup" +
			"  JOIN Program P ON P = MP.menu.program" +
			"  JOIN ProgramAction PA ON P = PA.program" +
			" WHERE (AGU.id IS NOT NULL OR (:extraGroupCode IS NOT NULL AND AG.groupCode = :extraGroupCode))" +
			"   AND MP.menu.id = :menuId" +
			"   AND MP.menu.systemStatus = 'ENABLE'" +
			"   AND MP.menu.status = 'ENABLE'" +
			"   AND P.status = 'ENABLE'" +
			"   AND (" +
			"		(:authType = 'SELECT' AND MP.selectStatus = 'ALLOW')" +
			"		OR (:authType = 'INSERT' AND MP.insertStatus = 'ALLOW')" +
			"		OR (:authType = 'UPDATE' AND MP.updateStatus = 'ALLOW')" +
			"		OR (:authType = 'DELETE' AND MP.deleteStatus = 'ALLOW')" +
			"		OR (:authType = 'PRINT' AND MP.printStatus = 'ALLOW')" +
			"		OR (:authType = 'DOWN' AND MP.downStatus = 'ALLOW')" +
			"		OR (:authType = 'MANAGE' AND MP.manageStatus = 'ALLOW')" +
			"		OR (:authType = 'CUSTOM1' AND MP.custom1Status = 'ALLOW')" +
			"		OR (:authType = 'CUSTOM2' AND MP.custom2Status = 'ALLOW')" +
			"		OR (:authType = 'CUSTOM3' AND MP.custom3Status = 'ALLOW')" +
			"	)" +
			"	AND CAST(PA.actionType AS string) = :actionType" +
			"   AND CAST(PA.authType AS string) = :authType")
	List<String> getActionListByMenuId(@Param("userId") UUID userId, @Param("extraGroupCode") String extraGroupCode, @Param("menuId") UUID menuId, @Param("actionType") String actionType, @Param("authType") String authType);
	
	@Query("" +
			"SELECT PA.uri" +
			"  FROM AuthorizationGroup AG" +
			"  LEFT JOIN AuthorizationGroupUser AGU " +
			"	 ON AGU.authorizationGroup = AG" +
			"		AND AGU.user.id = :userId" +
			"		AND AG.systemStatus = 'ENABLE'" +
			"		AND AG.status = 'ENABLE'" +
			"  JOIN MenuPermission MP ON AG = MP.authorizationGroup" +
			"  JOIN Program P ON P = MP.menu.program" +
			"  JOIN ProgramAction PA ON P = PA.program" +
			" WHERE (AGU.id IS NOT NULL OR (:extraGroupCode IS NOT NULL AND AG.groupCode = :extraGroupCode))" +
			"   AND MP.menu.systemStatus = 'ENABLE'" +
			"   AND MP.menu.status = 'ENABLE'" +
			"   AND P.programCode = :programCode" +
			"   AND P.status = 'ENABLE'" +
			"   AND (" +
			"		(:authType = 'SELECT' AND MP.selectStatus = 'ALLOW')" +
			"		OR (:authType = 'INSERT' AND MP.insertStatus = 'ALLOW')" +
			"		OR (:authType = 'UPDATE' AND MP.updateStatus = 'ALLOW')" +
			"		OR (:authType = 'DELETE' AND MP.deleteStatus = 'ALLOW')" +
			"		OR (:authType = 'PRINT' AND MP.printStatus = 'ALLOW')" +
			"		OR (:authType = 'DOWN' AND MP.downStatus = 'ALLOW')" +
			"		OR (:authType = 'MANAGE' AND MP.manageStatus = 'ALLOW')" +
			"		OR (:authType = 'CUSTOM1' AND MP.custom1Status = 'ALLOW')" +
			"		OR (:authType = 'CUSTOM2' AND MP.custom2Status = 'ALLOW')" +
			"		OR (:authType = 'CUSTOM3' AND MP.custom3Status = 'ALLOW')" +
			"	)" +
			"	AND CAST(PA.actionType AS string) = :actionType" +
			"   AND CAST(PA.authType AS string) = :authType")
	List<String> getActionListByProgramCode(@Param("userId") UUID userId, @Param("extraGroupCode") String extraGroupCode, @Param("programCode") String programCode, @Param("actionType") String actionType, @Param("authType") String authType);
}
