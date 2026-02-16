package pl.smyk.aggregatorservice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.smyk.common.dto.MovieDto;
import pl.smyk.common.dto.ScreeningDto;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/aggregator/screening-details")
@RequiredArgsConstructor
public class ScreeningAggregatorController {

    private final GenericAggregatorService aggregator;

    @Value("${gateway-url}")
    private String screeningBaseUrl;

    @Value("${gateway-url}")
    private String movieBaseUrl;

    @GetMapping("/test")
    public String test() {
        return "Test";
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ScreeningDetailsDto>> getDetails(
            @PathVariable Long id,
            HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        RequestSpec<ScreeningDto> screeningRequest =
                RequestSpec.<ScreeningDto>builder()
                        .url(screeningBaseUrl + "/api/screening/{id}")
                        .method(HttpMethod.GET)
                        .pathVariables(Map.of("id", id))
                        .headers(Map.of("Authorization", authHeader))
                        .responseType(ScreeningDto.class)
                        .build();

        return aggregator.aggregateSequential(
                screeningRequest,

                // generujemy drugi request na podstawie odpowiedzi pierwszego
                screening -> RequestSpec.<MovieDto>builder()
                        .url(movieBaseUrl + "/api/movie/{id}")
                        .method(HttpMethod.GET)
                        .pathVariables(Map.of("id", screening.getId()))
                        .headers(Map.of("Authorization", authHeader))
                        .responseType(MovieDto.class)
                        .build(),

                // merger
                ScreeningDetailsDto::new
        ).map(ResponseEntity::ok);
    }
}