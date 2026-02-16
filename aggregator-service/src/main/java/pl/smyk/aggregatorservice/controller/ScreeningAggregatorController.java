package pl.smyk.aggregatorservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import pl.smyk.aggregatorservice.dto.ScreeningDetailsDto;
import pl.smyk.aggregatorservice.dto.RequestSpec;
import pl.smyk.aggregatorservice.service.GenericAggregatorService;
import pl.smyk.common.dto.MovieDto;
import pl.smyk.common.dto.ScreeningDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/aggregator")
@RequiredArgsConstructor
public class ScreeningAggregatorController {

    private final GenericAggregatorService aggregator;
    private final WebClient.Builder webClientBuilder;

    @Value("${gateway-url}")
    private String screeningBaseUrl;

    @Value("${gateway-url}")
    private String movieBaseUrl;

    @GetMapping("/test")
    public String test() {
        return "Test";
    }

    @GetMapping("/screenings")
    public Mono<ResponseEntity<List<ScreeningDetailsDto>>> getAllScreeningsWithDetails(
            @RequestParam Long cinemaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        WebClient webClient = webClientBuilder.build();

        // 1. Pobierz listę screeningów
        Mono<List<ScreeningDto>> screeningsMono = webClient
                .get()
                .uri(screeningBaseUrl + "/api/screening/by-cinema-and-date?cinemaId={cinemaId}&date={date}",
                        cinemaId, date.toString())
                .header("Authorization", authHeader)
                .retrieve()
                .bodyToFlux(ScreeningDto.class)
                .collectList();

        return screeningsMono
                .flatMapMany(Flux::fromIterable)
                .flatMap(screening -> {
                    RequestSpec<ScreeningDto> screeningRequest =
                            RequestSpec.<ScreeningDto>builder()
                                    .url(screeningBaseUrl + "/api/screening/{id}")
                                    .method(HttpMethod.GET)
                                    .pathVariables(Map.of("id", screening.getId()))
                                    .headers(Map.of("Authorization", authHeader))
                                    .responseType(ScreeningDto.class)
                                    .build();

                    return aggregator.aggregateSequential(
                            screeningRequest,
                            s -> RequestSpec.<MovieDto>builder()
                                    .url(movieBaseUrl + "/api/movie/{id}")
                                    .method(HttpMethod.GET)
                                    .pathVariables(Map.of("id", s.getMovieId()))
                                    .headers(Map.of("Authorization", authHeader))
                                    .responseType(MovieDto.class)
                                    .build(),
                            ScreeningDetailsDto::new
                    );
                })
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/screening-details/{id}")
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
                screening -> RequestSpec.<MovieDto>builder()
                        .url(movieBaseUrl + "/api/movie/{id}")
                        .method(HttpMethod.GET)
                        .pathVariables(Map.of("id", screening.getMovieId()))
                        .headers(Map.of("Authorization", authHeader))
                        .responseType(MovieDto.class)
                        .build(),
                ScreeningDetailsDto::new
        ).map(ResponseEntity::ok);
    }
}