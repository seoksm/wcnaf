package com.winitech.system.infrastructure.userSession;

import com.winitech.system.domain.organization.Organization;
import com.winitech.system.domain.userSession.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * com.winitech.system.infrastructure.userSession
 * └ UserSessionRepository.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/06
 **/
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
	Optional<List<UserSession>> findByLoginStatus(UserSession.LoginStatus loginStatus);

	Optional<List<UserSession>> findByUserIdAndLoginStatus(UUID userId, UserSession.LoginStatus loginStatus);

	boolean existsByIdAndUserIdAndLoginStatus(UUID userSessionId, UUID userId, UserSession.LoginStatus loginStatus);

	Optional<UserSession> findByIdAndLoginStatus(UUID userSessionId, UserSession.LoginStatus loginStatus);
	
	List<UserSession> findAllByRefreshTokenAndRefreshTokenExpiresAtGreaterThanOrderByRefreshTokenExpiresAtDesc(String refreshToken, OffsetDateTime now);

	Optional<UserSession> findFirstByUserIdAndLoginStatusOrderByLoginAtDesc(UUID userId, UserSession.LoginStatus loginStatus);
}
