package com.winitech.system.infrastructure.userPasswordLog;

import com.winitech.system.domain.organization.Organization;
import com.winitech.system.domain.userPasswordLog.UserPasswordLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.infrastructure.userPasswordLog
 * └ UserPasswordLogRepository.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-23 13:37
 **/
public interface UserPasswordLogRepository extends JpaRepository<UserPasswordLog, UUID> {
	long deleteByUserIdAndIdNotIn(UUID userId, List<UUID> userPasswordLogIdList);
}
