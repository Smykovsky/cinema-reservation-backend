package pl.smyk.apigateway.filter;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/eureka",

            "/api/auth/v3/api-docs",
            "/api/movie/v3/api-docs",
            "/api/payment/v3/api-docs",
            "/swagger-ui",

            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/forgot-password",
            "/api/auth/reset-password"
    );

    public static final List<String> operatorRoleEndpoints = List.of(
            "/api/movie/management"
    );

    public Predicate<ServerHttpRequest> isSecured = request -> {

        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        boolean isPublicGet =
                method == HttpMethod.GET && (
                        path.matches("^/api/aggregator/screenings$") ||
                                path.matches("^/api/aggregator/screening-details/\\d+$") ||
                                path.matches("^/api/cinema$") ||
                                path.matches("^/api/cinema/\\d+$") ||
                                path.matches("^/api/hall$") ||
                                path.matches("^/api/hall/\\d+$") ||
                                path.matches("^/api/screening$") ||
                                path.matches("^/api/screening/\\d+$") ||
                                path.matches("^/api/screening/by-cinema-and-date$") ||
                                path.matches("^/api/seat$") ||
                                path.matches("^/api/seat/\\d+$") ||
                                path.matches("^/api/movie/\\d+$") ||
                                path.matches("^/api/movie$")
                );

        boolean isAuthEndpoint =
                openApiEndpoints
                        .stream()
                        .anyMatch(uri -> path.startsWith(uri));

        return !(isPublicGet || isAuthEndpoint);
    };

    public Predicate<ServerHttpRequest> requiresOperatorRole =
            request -> operatorRoleEndpoints
                    .stream()
                    .anyMatch(uri -> request.getURI().getPath().startsWith(uri));
}