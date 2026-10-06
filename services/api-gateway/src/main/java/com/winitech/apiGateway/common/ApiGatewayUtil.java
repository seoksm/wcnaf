package com.winitech.apiGateway.common;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.function.Supplier;

/**
 * <pre>
 * com.winitech.apiGateway.common
 * └ ApiGatewayUtil.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-31 15:17
 **/
public class ApiGatewayUtil {
	public static <T> Mono<T> blockingToMono(Supplier<T> supplier) {
		return Mono.fromSupplier(supplier).subscribeOn(Schedulers.boundedElastic());
	}
	
	public static <T> Flux<T> blockingToFlux(Supplier<List<T>> supplier) {
		return Flux.defer(() -> Flux.fromIterable(supplier.get())).subscribeOn(Schedulers.boundedElastic());
	}
}
