package com.winitech.apiGateway.infrastructure.route;

import com.winitech.apiGateway.domain.route.Route;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.infrastructure.route
 * └ RouteRepository.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 13:03
 **/
public interface RouteRepository extends JpaRepository<Route, UUID> {
	List<Route> findAllBySystemStatus(Route.SystemStatus systemStatus);
	
	List<Route> findAllBySystemStatusOrderBySortSeqAscNameAscIdAsc(Route.SystemStatus systemStatus);
	
	List<Route> findAllByStatusAndSystemStatusOrderBySortSeqAscNameAscIdAsc(Route.Status status, Route.SystemStatus systemStatus);
	
	Boolean existsRouteByNameAndSystemStatusAndIdNot(String name, Route.SystemStatus systemStatus, UUID id);

	Optional<Route> findByIdAndSystemStatus(UUID id, Route.SystemStatus systemStatus);

	Optional<Route> findByNameAndSystemStatus(String name, Route.SystemStatus systemStatus);
}
