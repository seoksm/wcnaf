package com.winitech.apiGateway.infrastructure.predicate;

import com.winitech.apiGateway.domain.predicate.Predicate;
import com.winitech.apiGateway.domain.predicate.PredicateCommand;
import com.winitech.apiGateway.domain.predicate.PredicateStore;
import com.winitech.common.exception.EntityNotFoundException;

import com.winitech.common.exception.IllegalStatusException;
import com.winitech.common.library.WiniString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * com.winitech.apiGateway.infrastructure.predicate
 * └ PredicateStoreImpl.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 13:04
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class PredicateStoreImpl implements PredicateStore {
	private final PredicateRepository predicateRepository;

	@Override
	public Predicate store(Predicate predicate) {
		checkPredicateType(predicate);

		return predicateRepository.save(predicate);
	}

	@Override
	public Predicate modify(Predicate predicate, PredicateCommand command) {
		predicate.setPredicateType(command.getPredicateType());
		predicate.setKey(command.getKey());
		predicate.setDefinition(command.getDefinition());
		predicate.setStatus(command.getStatus());
		
		checkPredicateType(predicate);
		
		return predicateRepository.save(predicate);
	}

	private static void checkPredicateType(Predicate predicate) {
		if (predicate.getPredicateType() != null) {
			switch (predicate.getPredicateType()) {
				case COOKIE:
				case HEADER:
				case QUERY:
				case METHOD:
				case PATH:
				case HOST:
					if (predicate.getKey() == null || predicate.getKey().isEmpty()) {
						throw new IllegalStatusException("Key must not be null or empty for predicate type: " + predicate.getPredicateType());
					}
					break;
				case WEIGHT:
					if (predicate.getKey() == null || predicate.getKey().isEmpty()) {
						throw new IllegalStatusException("Key must not be null or empty for predicate type: " + predicate.getPredicateType());
					}
					
					if (! WiniString.isNumber(predicate.getDefinition())) {
						throw new IllegalStatusException("Definition must be a number for predicate type: " + predicate.getPredicateType());
					}
					break;
				default:
					throw new IllegalArgumentException("Invalid predicate type: " + predicate.getPredicateType());
			}
		}
	}
}
