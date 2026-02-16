package pl.smyk.aggregatorservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.smyk.aggregatorservice.dto.RequestSpec;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class WebClientGenericAggregatorService
        implements GenericAggregatorService {

    private final WebClient webClient;

    public WebClientGenericAggregatorService(WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }

    @Override
    public <A, B, T> Mono<T> aggregateSequential(
            RequestSpec<A> first,
            Function<A, RequestSpec<B>> secondRequestFactory,
            BiFunction<A, B, T> merger) {

        return execute(first)
                .flatMap(a ->
                        execute(secondRequestFactory.apply(a))
                                .map(b -> merger.apply(a, b))
                );
    }

    private <R> Mono<R> execute(RequestSpec<R> spec) {
        // Buduj request
        WebClient.RequestHeadersSpec<?> request = webClient
                .method(spec.getMethod())
                .uri(spec.getUrl(), spec.getPathVariables());

        // ✅ DODAJ NAGŁÓWKI!
        if (spec.getHeaders() != null && !spec.getHeaders().isEmpty()) {
            spec.getHeaders().forEach(request::header);
        }

        return request
                .retrieve()
                .bodyToMono(spec.getResponseType());
    }
}