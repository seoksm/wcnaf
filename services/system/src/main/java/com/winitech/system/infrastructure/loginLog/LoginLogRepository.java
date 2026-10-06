package com.winitech.system.infrastructure.loginLog;

import com.winitech.system.domain.loginLog.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.system.infrastructure.loginLog
 * └ LoginLogRepository.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2024-12-20 18:20
 **/
public interface LoginLogRepository extends JpaRepository<LoginLog, UUID> {
	Optional<LoginLog> findFirstByUsernameAndLoginIpOrderByIdDesc(String username, String loginIp);

	Optional<LoginLog> findFirstByUsernameAndLoginIpAndLoginLogStatusOrderByIdDesc(String username, String loginIp, LoginLog.LoginLogStatus loginLogStatus);
	
	List<LoginLog> findByUsernameAndLoginLogStatusAndLockUntilGreaterThan(String username, LoginLog.LoginLogStatus loginLogStatus, OffsetDateTime now);

	boolean existsByUsernameAndLoginLogStatusAndCreateAtBetween(String username, LoginLog.LoginLogStatus loginLogStatus, OffsetDateTime startOfDay, OffsetDateTime endOfDay);
}
