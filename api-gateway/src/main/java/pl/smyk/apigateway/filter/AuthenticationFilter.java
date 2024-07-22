package pl.smyk.apigateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pl.smyk.apigateway.util.JwtUtil;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

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
      if (validator.isSecured.test(exchange.getRequest())) {
        if (!exchange.getRequest().getHeaders().containsKey(AUTHORIZATION)) {
          logger.warning("Missing authorization header");
          return handleUnauthorized(exchange, "Missing authorization header");
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION);
        if (authHeader != null) {
          if (authHeader.startsWith("Bearer ")) {
            authHeader = authHeader.substring(7);
          }
        } else {
          logger.warning("Authorization header does not start with Bearer");
          return handleUnauthorized(exchange, "Invalid authorization header");
        }

        try {
          jwtUtil.validateToken(authHeader);
          logger.info("Token validated successfully");
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
