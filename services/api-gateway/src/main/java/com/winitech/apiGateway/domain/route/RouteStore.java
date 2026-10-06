package com.winitech.apiGateway.domain.route;

/**
 * <pre>
 * com.winitech.apiGateway.domain.route
 * └ RouteStore.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 12:36
 **/
public interface RouteStore {
	Route store(Route route);

	Route modify(Route route, RouteCommand command);
}
