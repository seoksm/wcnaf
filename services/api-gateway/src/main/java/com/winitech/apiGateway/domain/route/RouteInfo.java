package com.winitech.apiGateway.domain.route;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.domain.route
 * └ RouteInfo.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 12:35
 **/
@Getter
public class RouteInfo {
	private final UUID id;
    private final UUID routeId;
	private final String name;
	private final String uri;
	private final Integer sortSeq;
	private final Route.Status status;
	private final OffsetDateTime updateAt;

	public RouteInfo(Route route) {
		this.id = route.getId();
        this.routeId = route.getId();
		this.name = route.getName();
		this.uri = route.getUri();
		this.sortSeq = route.getSortSeq();
		this.status = route.getStatus();
		this.updateAt = route.getUpdateAt();
	}
}
