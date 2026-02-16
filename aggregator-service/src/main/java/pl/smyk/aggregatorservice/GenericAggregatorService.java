package pl.smyk.aggregatorservice;

import reactor.core.publisher.Mono;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface GenericAggregatorService {

    <A, B, T> Mono<T> aggregateSequential(
            RequestSpec<A> first,
            Function<A, RequestSpec<B>> secondRequestFactory,
            BiFunction<A, B, T> merger
    );
}