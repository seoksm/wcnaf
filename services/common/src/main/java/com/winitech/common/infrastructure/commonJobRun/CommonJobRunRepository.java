package com.winitech.common.infrastructure.commonJobRun;

import com.winitech.common.domain.commonJobRun.CommonJobRun;
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
 * com.winitech.system.domain.commonJobRun
 * └ CommonJobRun.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:48
 **/
public interface CommonJobRunRepository extends JpaRepository<CommonJobRun, UUID> {
	List<CommonJobRun> findAllBySystemStatusOrderByIdDesc(CommonJobRun.SystemStatus systemStatus);

	List<CommonJobRun> findAllBySystemStatusOrderByIdDesc(CommonJobRun.SystemStatus systemStatus, Sort sort);

	Optional<CommonJobRun> findByIdAndSystemStatus(UUID commonJobRunId, CommonJobRun.SystemStatus systemStatus);
}
