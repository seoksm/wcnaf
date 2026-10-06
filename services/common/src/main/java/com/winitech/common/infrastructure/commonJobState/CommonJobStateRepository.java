package com.winitech.common.infrastructure.commonJobState;

import com.winitech.common.domain.commonJobState.CommonJobState;
import org.springframework.data.domain.Sort;
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
 * com.winitech.system.domain.commonJobState
 * └ CommonJobState.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:47
 **/
public interface CommonJobStateRepository extends JpaRepository<CommonJobState, UUID> {
	List<CommonJobState> findAllBySystemStatusOrderByIdDesc(CommonJobState.SystemStatus systemStatus);

	List<CommonJobState> findAllBySystemStatusOrderByIdDesc(CommonJobState.SystemStatus systemStatus, Sort sort);

	Optional<CommonJobState> findByIdAndSystemStatus(UUID commonJobStateId, CommonJobState.SystemStatus systemStatus);
	
	List<CommonJobState> findAllByCommonJobIdAndSystemStatusOrderByIdDesc(UUID commonJobId, CommonJobState.SystemStatus systemStatus);

	boolean existsByCommonJobIdAndSystemStatus(UUID commonJobId, CommonJobState.SystemStatus systemStatus);

	@Modifying
	@Query("" +
			"UPDATE CommonJobState c " +
			"   SET c.currentRun.id = :currentRunId, " +
			" 	    c.lastStartedAt = :now," +
			"		c.updateAt = :now " +
			" WHERE c.commonJob.id = :commonJobId " +
			"   AND c.systemStatus = 'ENABLE'")
	void updateCurrentRun(@Param("commonJobId") UUID commonJobId, @Param("currentRunId") UUID currentRunId, @Param("now") OffsetDateTime now);

	@Modifying
	@Query("" +
			"UPDATE CommonJobState c " +
			"   SET c.currentRun.id = CASE WHEN c.currentRun.id = :lastRunId THEN null ELSE c.currentRun.id END, " +
			" 	    c.errCnt = 0," +
			"		c.lastRun.id = :lastRunId," +
			"		c.lastEndedAt = :now, " +
			"		c.lastSuccessAt = :now," +
			"		c.lastMessage = ''," +
			"		c.updateAt = :now " +
			" WHERE c.commonJob.id = :commonJobId " +
			"   AND c.systemStatus = 'ENABLE'")
	void updateSuccessfulRun(@Param("commonJobId") UUID commonJobId, @Param("lastRunId") UUID lastRunId, @Param("now") OffsetDateTime now);

	@Modifying
	@Query("" +
			"UPDATE CommonJobState c " +
			"   SET c.currentRun.id = CASE WHEN c.currentRun.id = :lastRunId THEN null ELSE c.currentRun.id END, " +
			" 	    c.errCnt = c.errCnt + 1," +
			"		c.lastRun.id = :lastRunId," +
			"		c.lastEndedAt = :now, " +
			"		c.lastFailedAt = :now," +
			"		c.lastMessage = :lastMessage," +
			"		c.updateAt = :now " +
			" WHERE c.commonJob.id = :commonJobId " +
			"   AND c.systemStatus = 'ENABLE'")
	void updateFailedRun(@Param("commonJobId") UUID commonJobId, @Param("lastRunId") UUID lastRunId, @Param("now") OffsetDateTime now, @Param("lastMessage") String lastMessage);
}
