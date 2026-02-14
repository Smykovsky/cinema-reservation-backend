package pl.smyk.apigateway.filter;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pl.smyk.apigateway.util.JwtUtil;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final Logger logger = Logger.getLogger(AuthenticationFilter.class.getName());

    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (validator.isSecured.test(request)) {
                if (!request.getHeaders().containsKey(AUTHORIZATION)) {
                    logger.warning("Missing authorization header");
                    return handleUnauthorized(exchange, "Missing authorization header");
                }

                String authHeader = request.getHeaders().getFirst(AUTHORIZATION);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                } else {
                    logger.warning("Invalid authorization header");
                    return handleUnauthorized(exchange, "Invalid authorization header");
                }

                try {
                    jwtUtil.validateToken(authHeader);
                    logger.info("Token validated successfully");

                    Claims claims = jwtUtil.extractAllClaims(authHeader);
                    String email = claims.getSubject();
                    Long userId = claims.get("userId", Long.class);
                    List<String> scope = claims.get("scope", List.class);

                    List<String> roles = scope.stream()
                            .filter(s -> s.startsWith("ROLE_"))
                            .map(s -> s.substring(5))
                            .collect(Collectors.toList());

                    List<String> permissions = scope.stream()
                            .filter(s -> !s.startsWith("ROLE_"))
                            .collect(Collectors.toList());

                    logger.info("Extracted Roles: " + String.join(",", roles));
                    logger.info("Extracted Permissions: " + String.join(",", permissions));

                    if (validator.requiresOperatorRole.test(request)) {
                        if (!roles.contains("OPERATOR")) { // Check for OPERATOR role directly
                            logger.warning("Operator role required");
                            return handleUnauthorized(exchange, "Operator role required");
                        }
                    }

                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .header("X-User-Email", email)
                            .header("X-User-Roles", String.join(",", roles))
                            .header("X-User-Permissions", String.join(",", permissions))
                            .header("X-User-Id", userId.toString())
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());

                } catch (Exception e) {
                    logger.severe("Invalid or expired token: " + e.getMessage());
                    return handleUnauthorized(exchange, "Invalid or expired token");
                }
            }
            return chain.filter(exchange);
        };
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        String body = "{\"error\": \"" + message + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }


    public static class Config {

    }
}