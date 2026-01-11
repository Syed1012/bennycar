package de.bennycar.gateway.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            // Auth endpoints
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/api/v1/auth/validate",
            // Public vehicle endpoints (browsing)
            "/api/v1/vehicles",
            "/api/v1/brands",
            "/api/v1/vehicle-types",
            "/api/v1/customization-categories",
            "/api/v1/customization-options",
            // Actuator health
            "/actuator/health",
            "/actuator/info",
            // Swagger/OpenAPI
            "/v3/api-docs",
            "/swagger-ui",
            "/eureka"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}

