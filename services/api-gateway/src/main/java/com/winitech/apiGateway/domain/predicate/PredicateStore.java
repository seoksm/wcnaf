package com.winitech.apiGateway.domain.predicate;

/**
 * <pre>
 * com.winitech.apiGateway.domain.predicate
 * └ PredicateStore.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 12:59
 **/
public interface PredicateStore {
	Predicate store(Predicate predicate);

	Predicate modify(Predicate predicate, PredicateCommand command);
}
