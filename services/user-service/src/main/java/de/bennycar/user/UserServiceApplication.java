package de.bennycar.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for BennyCar User Service.
 * Handles user authentication, authorization, and profile management.
 */
@Slf4j
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        log.info("=================================================");
        log.info("User Service started successfully!");
        log.info("API Documentation: http://localhost:8081/swagger-ui.html");
        log.info("=================================================");
    }
}