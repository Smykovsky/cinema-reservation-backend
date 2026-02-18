package pl.smyk.apigateway.filter;

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

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

    public Predicate<ServerHttpRequest> requiresOperatorRole =
            request -> operatorRoleEndpoints
                    .stream()
                    .anyMatch(uri -> request.getURI().getPath().startsWith(uri));
}
