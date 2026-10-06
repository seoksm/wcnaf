package com.winitech.apiGateway.domain.route;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.domain.route
 * └ RouteCommand.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 12:33
 **/
@Getter
@Builder
@ToString
public class RouteCommand {
	private final String name;
	private final String uri;
	private final Integer sortSeq;
	private final Route.Status status;

	public Route toEntity() {
		return Route.builder()
				.name(name)
				.uri(uri)
				.sortSeq(sortSeq)
				.status(status)
				.build();
	}
}
