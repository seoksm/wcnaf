package com.winitech.apiGateway.domain.predicate;

import com.winitech.apiGateway.domain.route.RouteReader;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.domain.predicate
 * └ PredicateCommand.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 12:33
 **/
@Getter
@Builder
@ToString
public class PredicateCommand {
	private final Predicate.PredicateType predicateType;
	private final String key;
	private final String definition;
	private final Predicate.Status status;

	public Predicate toEntity() {
		return Predicate.builder()
				.predicateType(predicateType)
				.key(key)
				.definition(definition)
				.status(status)
				.build();
	}

}
