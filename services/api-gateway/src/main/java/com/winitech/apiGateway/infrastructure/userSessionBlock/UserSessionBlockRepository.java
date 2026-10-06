package com.winitech.apiGateway.infrastructure.userSessionBlock;

import com.winitech.apiGateway.domain.userSessionBlock.UserSessionBlock;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.domain.userSessionBlock
 * └ UserSessionBlock.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-04-21 15:03
 **/
public interface UserSessionBlockRepository extends JpaRepository<UserSessionBlock, UUID> {
	Optional<UserSessionBlock> findByUserSessionId(UUID userSessionId);

	List<UserSessionBlock> findAllByOrderByIdDesc();

	List<UserSessionBlock> findAll(Sort sort);

	boolean existsByUserSessionIdAndIdNot(UUID userSessionId, UUID userSessionBlockId);

	boolean existsByUserSessionId(UUID userSessionID);

	List<UserSessionBlock> findAllByAccessTokenExpiresAtLessThanEqual(OffsetDateTime now);
}
