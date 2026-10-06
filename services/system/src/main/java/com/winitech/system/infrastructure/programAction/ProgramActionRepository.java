package com.winitech.system.infrastructure.programAction;

import com.winitech.common.domain.common.CommonMenuActionInfo;
import com.winitech.system.domain.programAction.ProgramAction;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.programAction
 * └ ProgramAction.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-14 10:39
 **/
public interface ProgramActionRepository extends JpaRepository<ProgramAction, UUID> {
	List<ProgramAction> findAllByOrderByIdDesc();

	List<ProgramAction> findAllByOrderByIdDesc(Sort sort);

	List<ProgramAction> findAllByProgramId(UUID programId);

	boolean existsByProgramIdAndActionTypeAndAuthTypeAndUriAndIdNot(UUID programId, ProgramAction.ActionType actionType, ProgramAction.AuthType authType, String uri, UUID programActionId);
	
	@Query("" +
			"SELECT new com.winitech.common.domain.common.CommonMenuActionInfo(m.id, p.id, p.programCode, CAST(pa.actionType string), CAST(pa.authType string), pa.uri)" +
			"  FROM Program p" +
			"  JOIN Menu m ON m.menuType = 'PROGRAM' AND m.program = p AND m.systemStatus = 'ENABLE' " +
			"  JOIN ProgramAction pa ON pa.program = p" +
			" WHERE p.id IN :programIdList")
	List<CommonMenuActionInfo> getMenuActionByProgramIdList(@Param("programIdList") List<UUID> programIdList);
}
