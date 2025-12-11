package de.bennycar.user;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for BennyCar User Service.
 * Handles user authentication, authorization, and profile management.
 */
@Slf4j
@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "BennyCar User Service API",
        version = "1.0",
        description = "User authentication and management microservice"
    )
)
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        log.info("=================================================");
        log.info("User Service started successfully!");
        log.info("API Documentation: http://localhost:8081/swagger-ui.html");
        log.info("=================================================");
    }
}