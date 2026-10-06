package com.winitech.common.infrastructure.commonJobGroup;

import com.winitech.common.domain.commonJobGroup.CommonJobGroup;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.domain.commonJobGroup
 * └ CommonJobGroup.java
 * </pre>
 * @author : Coding (클라우드팀)
 * @since : 2025-03-11 16:36
 **/
public interface CommonJobGroupRepository extends JpaRepository<CommonJobGroup, UUID> {
	List<CommonJobGroup> findAllBySystemStatusOrderByIdDesc(CommonJobGroup.SystemStatus systemStatus);

	List<CommonJobGroup> findAllBySystemStatusOrderByIdDesc(CommonJobGroup.SystemStatus systemStatus, Sort sort);

	Optional<CommonJobGroup> findByIdAndSystemStatus(UUID commonJobGroupId, CommonJobGroup.SystemStatus systemStatus);

	List<CommonJobGroup> findAllByNameInAndSystemStatus(List<String> jobGroupNameList, CommonJobGroup.SystemStatus systemStatus);

	boolean existsByNameAndIdNotAndSystemStatus(String name, UUID commonJobGroupId, CommonJobGroup.SystemStatus systemStatus);
}
