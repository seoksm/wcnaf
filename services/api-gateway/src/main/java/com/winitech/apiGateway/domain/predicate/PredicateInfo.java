package com.winitech.apiGateway.domain.predicate;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.domain.predicate
 * └ PredicateInfo.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 12:52
 **/
@Getter
public class PredicateInfo {
	private final UUID id;
    private final UUID predicateId;
	private final UUID routeId;
	private final Predicate.PredicateType predicateType;
	private final String key;
	private final String definition;
	private final Predicate.Status status;
	private final OffsetDateTime updateAt;
	
	public PredicateInfo(Predicate predicate) {
		this.id = predicate.getId();
        this.predicateId = predicate.getId();
		this.routeId = predicate.getRoute().getId();
		this.predicateType = predicate.getPredicateType();
		this.key = predicate.getKey();
		this.definition = predicate.getDefinition();
		this.status = predicate.getStatus();
		this.updateAt = predicate.getUpdateAt();
	}
}
