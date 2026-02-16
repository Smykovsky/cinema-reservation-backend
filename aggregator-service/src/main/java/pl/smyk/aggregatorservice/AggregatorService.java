package pl.smyk.aggregatorservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AggregatorService {
    private final WebClient webClient;

    public AggregatorService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }



}
