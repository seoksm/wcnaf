package com.winitech.apiGateway.infrastructure.predicate;

import com.winitech.apiGateway.domain.predicate.Predicate;
import com.winitech.apiGateway.domain.predicate.PredicateInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.infrastructure.predicate
 * └ PredicateRepository.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 13:05
 **/
public interface PredicateRepository extends JpaRepository<Predicate, UUID> {
	List<Predicate> findAllBySystemStatus(Predicate.SystemStatus systemStatus);

	Optional<Predicate> findByIdAndSystemStatus(UUID id, Predicate.SystemStatus systemStatus);

	List<Predicate> findAllByRouteIdAndSystemStatus(UUID routeId, Predicate.SystemStatus systemStatus);
	
	List<Predicate> findAllByRouteIdInAndSystemStatus(List<UUID> routeIdList, Predicate.SystemStatus systemStatus);
	
	List<Predicate> findAllByRouteIdInAndStatusAndSystemStatus(List<UUID> routeIdList, Predicate.Status status, Predicate.SystemStatus systemStatus);
}
