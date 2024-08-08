package pl.smyk.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

  public static final List<String> openApiEndpoints = List.of(
    "/api/auth/register",
    "/api/auth/login",
    "/api/auth/test",
    "/eureka"
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
