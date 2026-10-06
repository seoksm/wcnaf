package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonAuthorizationGroupUser;
import com.winitech.common.domain.common.CommonAuthorizationGroupUserId;
import com.winitech.common.domain.common.CommonMenuAction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonAuthorizationUtilRepository.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-26 14:10
 **/
@Repository
public interface CommonAuthorizationUtilRepository extends CrudRepository<CommonAuthorizationGroupUser, CommonAuthorizationGroupUserId> {
	@Query("" +
			"SELECT C" +
			"  FROM CommonAuthorizationGroupPermission B" +
			"  LEFT JOIN CommonAuthorizationGroupUser A ON A.authorizationGroupId = B.authorizationGroupId AND A.userId = :userId" +
			"  JOIN CommonMenuAction C ON B.menuId = C.menuId" +
			" WHERE (A.userId IS NOT NULL OR (:extraGroupCode IS NOT NULL AND B.groupCode = :extraGroupCode))" +
			"   AND B.menuId = :menuId" +
			"   AND (" +
			"		(:authType = 'SELECT' AND B.selectStatus = 'ALLOW')" +
			"		OR (:authType = 'INSERT' AND B.insertStatus = 'ALLOW')" +
			"		OR (:authType = 'UPDATE' AND B.updateStatus = 'ALLOW')" +
			"		OR (:authType = 'DELETE' AND B.deleteStatus = 'ALLOW')" +
			"		OR (:authType = 'PRINT' AND B.printStatus = 'ALLOW')" +
			"		OR (:authType = 'DOWN' AND B.downStatus = 'ALLOW')" +
			"		OR (:authType = 'MANAGE' AND B.manageStatus = 'ALLOW')" +
			"		OR (:authType = 'CUSTOM1' AND B.custom1Status = 'ALLOW')" +
			"		OR (:authType = 'CUSTOM2' AND B.custom2Status = 'ALLOW')" +
			"		OR (:authType = 'CUSTOM3' AND B.custom3Status = 'ALLOW')" +
			"	)" +
			"	AND C.actionType = :actionType" +
			"   AND C.authType = :authType")
	List<CommonMenuAction> findActionListByMenuId(
			@Param("userId") UUID userId,
			@Param("extraGroupCode") String extraGroupCode,
			@Param("menuId") UUID menuId,
			@Param("actionType") String actionType,
			@Param("authType") String authType
	);

	@Query("" +
			"SELECT C" +
			"  FROM CommonAuthorizationGroupPermission B" +
			"  LEFT JOIN CommonAuthorizationGroupUser A ON A.authorizationGroupId = B.authorizationGroupId AND A.userId = :userId" +
			"  JOIN CommonMenuAction C ON B.menuId = C.menuId" +
			" WHERE (A.userId IS NOT NULL OR (:extraGroupCode IS NOT NULL AND B.groupCode = :extraGroupCode))" +
			"   AND C.programCode = :programCode" +
			"   AND (" +
			"		(:authType = 'SELECT' AND B.selectStatus = 'ALLOW')" +
			"		OR (:authType = 'INSERT' AND B.insertStatus = 'ALLOW')" +
			"		OR (:authType = 'UPDATE' AND B.updateStatus = 'ALLOW')" +
			"		OR (:authType = 'DELETE' AND B.deleteStatus = 'ALLOW')" +
			"		OR (:authType = 'PRINT' AND B.printStatus = 'ALLOW')" +
			"		OR (:authType = 'DOWN' AND B.downStatus = 'ALLOW')" +
			"		OR (:authType = 'MANAGE' AND B.manageStatus = 'ALLOW')" +
			"		OR (:authType = 'CUSTOM1' AND B.custom1Status = 'ALLOW')" +
			"		OR (:authType = 'CUSTOM2' AND B.custom2Status = 'ALLOW')" +
			"		OR (:authType = 'CUSTOM3' AND B.custom3Status = 'ALLOW')" +
			"	)" +
			"	AND C.actionType = :actionType" +
			"   AND C.authType = :authType")
	List<CommonMenuAction> findActionListByProgramCode(
			@Param("userId") UUID userId,
			@Param("extraGroupCode") String extraGroupCode,
			@Param("programCode") String programCode,
			@Param("actionType") String actionType,
			@Param("authType") String authType
	);
}
