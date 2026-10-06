package com.winitech.common.infrastructure.common;

import com.winitech.common.domain.common.CommonMenuAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.common.infrastructure.common
 * └ CommonMenuActionRepository.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-02-24 13:33
 **/
public interface CommonMenuActionRepository extends JpaRepository<CommonMenuAction, UUID> {
	List<CommonMenuAction> findByProgramIdIn(List<UUID> menuIdList);
}
