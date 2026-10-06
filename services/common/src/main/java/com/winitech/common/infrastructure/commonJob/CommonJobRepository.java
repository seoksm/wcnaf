package com.winitech.common.infrastructure.commonJob;

import com.winitech.common.domain.commonJob.CommonJob;
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
 * com.winitech.system.domain.commonJob
 * └ CommonJob.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:37
 **/
public interface CommonJobRepository extends JpaRepository<CommonJob, UUID> {
	List<CommonJob> findAllBySystemStatusOrderByIdDesc(CommonJob.SystemStatus systemStatus);

	@Query("" +
			"select c " +
			"  from CommonJob c" +
			"  join CommonJobGroup cjg on c.commonJobGroup = cjg" +
			" where c.systemStatus = 'ENABLE'" +
			"   and c.status = 'ENABLE'" +
			"   and cjg.systemStatus = 'ENABLE'" +
			"   and cjg.status = 'ENABLE' " +
			" order by c.id DESC")
	List<CommonJob> findActiveCommonJob();

	List<CommonJob> findAllBySystemStatusOrderByIdDesc(CommonJob.SystemStatus systemStatus, Sort sort);

	Optional<CommonJob> findByIdAndSystemStatus(UUID commonJobId, CommonJob.SystemStatus systemStatus);

	@Query("" +
			"select cj " +
			"  from CommonJob cj " +
			" where cj.commonJobGroup.name = :jobGroupName" +
			"   and cj.name = :jobName " +
			"   and cj.systemStatus = :systemStatus")
	Optional<CommonJob> findByNameAndSystemStatus(@Param("jobGroupName") String jobGroupName, @Param("jobName") String jobName, @Param("systemStatus") CommonJob.SystemStatus systemStatus);

	@Query("" +
			"SELECT cj.className " +
			"  FROM CommonJob cj " +
			" WHERE cj.className IN :classNameList " +
			"   AND cj.systemStatus = :systemStatus")
	List<String> findAllByClassNameInAndSystemStatus(@Param("classNameList") List<String> classNameList, @Param("systemStatus") CommonJob.SystemStatus systemStatus);

	@Query("" +
			"SELECT MAX(COALESCE(CJ.updateAt, CJ.createAt)) AS updateAt" +
			"  FROM CommonJob CJ" +
			" INNER JOIN CommonJobGroup CJG ON CJ.commonJobGroup = CJG" +
			" WHERE CJ.applyStatus = 'PENDING'")
	OffsetDateTime findLastPendingUpdateAtIncludeDisable();
	
	@Modifying
	@Query("" +
			"UPDATE CommonJob cj" +
			"   SET cj.applyStatus = 'SUCCESSFUL'" +
			" WHERE cj.id NOT IN :commonJobIdList" +
			"   AND cj.applyStatus = 'PENDING'")
	void markAsAppliedExcept(@Param("commonJobIdList") List<UUID> commonJobIdList);

	@Query("" +
			"select (count(c) > 0)" +
			"  from CommonJob c " +
			" where c.commonJobGroup.name = :jobGroupName " +
			"   and c.name = :jobName " +
			"   and c.id <> :commonJobId " +
			"   and c.systemStatus = :systemStatus")
	boolean existsByNameAndIdNotAndSystemStatus(@Param("jobGroupName") String jobGroupName, @Param("jobName") String jobName, @Param("commonJobId") UUID commonJobId, @Param("systemStatus") CommonJob.SystemStatus systemStatus);
}
