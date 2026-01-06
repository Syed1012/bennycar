package de.bennycar.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation.
 * Access the UI at: /swagger-ui.html
 * Access the API docs at: /v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:User Service}")
    private String applicationName;

    @Value("${application.version:0.0.1-SNAPSHOT}")
    private String applicationVersion;

    @Value("${server.port:8081}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        String description = "BennyCar User Service API for authentication and user management.\n\n" +
                "## Features\n" +
                "- User registration and authentication\n" +
                "- JWT-based access tokens\n" +
                "- Secure refresh token rotation\n" +
                "- User profile management\n\n" +
                "## Authentication\n" +
                "Most endpoints require a JWT access token in the Authorization header:\n" +
                "Authorization: Bearer <your-access-token>";

        return new OpenAPI()
                .info(new Info()
                        .title(applicationName + " API")
                        .version(applicationVersion)
                        .description(description)
                        .contact(new Contact()
                                .name("BennyCar Development Team")
                                .email("dev@bennycar.de")
                                .url("https://bennycar.de"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local development server"),
                        new Server()
                                .url("https://api.bennycar.de")
                                .description("Production server")
                ))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT access token")));
    }
}

