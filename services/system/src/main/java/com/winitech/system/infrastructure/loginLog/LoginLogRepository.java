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

	// X-Forwarded-For는 클라이언트가 조작 가능한 값이라 IP만으로 시도횟수를 집계하면 매번 다른 IP를
	// 자처해 잠금을 무력화할 수 있다 - username 단독(IP 무관) 기준으로도 최근 상태를 집계해 병행 체크한다.
	Optional<LoginLog> findFirstByUsernameOrderByIdDesc(String username);

	Optional<LoginLog> findFirstByUsernameAndLoginIpAndLoginLogStatusOrderByIdDesc(String username, String loginIp, LoginLog.LoginLogStatus loginLogStatus);
	
	List<LoginLog> findByUsernameAndLoginLogStatusAndLockUntilGreaterThan(String username, LoginLog.LoginLogStatus loginLogStatus, OffsetDateTime now);

	boolean existsByUsernameAndLoginLogStatusAndCreateAtBetween(String username, LoginLog.LoginLogStatus loginLogStatus, OffsetDateTime startOfDay, OffsetDateTime endOfDay);
}
