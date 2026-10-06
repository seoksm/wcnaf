package com.winitech.common.infrastructure.commonJobTrigger;

import com.winitech.common.domain.commonJobTrigger.CommonJobTrigger;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJobTrigger
 * └ CommonJobTrigger.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:45
 **/
public interface CommonJobTriggerRepository extends JpaRepository<CommonJobTrigger, UUID> {
	@Query("" +
			"select cjt " +
			"  from CommonJobTrigger cjt" +
			" inner join CommonJob cj on cjt.commonJob = cj and cj.systemStatus = 'ENABLE' and cj.status = 'ENABLE'" +
			" inner join CommonJobGroup cjg on cj.commonJobGroup = cjg and cjg.systemStatus = 'ENABLE' and cjg.status = 'ENABLE'" +
			" where cjt.systemStatus = :systemStatus" +
			"   and cjt.status = 'ENABLE'" +
			" order by cjt.id DESC")
	@EntityGraph(attributePaths = {"commonJob"})
	List<CommonJobTrigger> findAllBySystemStatusOrderByIdDesc(@Param("systemStatus") CommonJobTrigger.SystemStatus systemStatus);

	List<CommonJobTrigger> findAllByCommonJobIdAndSystemStatusOrderByIdDesc(UUID commonJobId, CommonJobTrigger.SystemStatus systemStatus);

	Optional<CommonJobTrigger> findByIdAndSystemStatus(UUID id, CommonJobTrigger.SystemStatus systemStatus);

	Optional<CommonJobTrigger> findByCommonJobIdAndIdAndSystemStatus(UUID commonJobId, UUID commonJobTriggerId, CommonJobTrigger.SystemStatus systemStatus);

	@Query("" +
			"SELECT MAX(COALESCE(CJT.updateAt, CJT.createAt)) AS updateAt" +
			"  FROM CommonJobTrigger CJT" +
			" WHERE CJT.applyStatus = 'PENDING'")
	OffsetDateTime findLastPendingUpdateAtIncludeDisable();

	@Modifying
	@Query("" +
			"UPDATE CommonJobTrigger cjt" +
			"   SET cjt.applyStatus = 'SUCCESSFUL'" +
			" WHERE cjt.id NOT IN :commonJobTriggerIdList" +
			"   AND cjt.applyStatus = 'PENDING'")
	void markAsAppliedExcept(@Param("commonJobTriggerIdList") List<UUID> commonJobTriggerIdList);
}
