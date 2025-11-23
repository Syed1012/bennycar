package de.bennycar.user.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class to load .env file variables into Spring's environment.
 * This allows us to use environment variables from .env files in application.yml
 * and throughout the application.
 * Industry Best Practice:
 * - Keep sensitive data (passwords, secrets) in .env files (never commit these)
 * - Use .env.example as a template (commit this)
 * - Use Spring Profiles for environment-specific configurations
 */
public class DotEnvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger logger = LoggerFactory.getLogger(DotEnvConfig.class);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();

        try {
            // Load .env file from the project root
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")  // Look for .env in the project root
                    .ignoreIfMissing() // Don't fail if .env is missing (useful for production)
                    .load();

            Map<String, Object> dotenvProperties = new HashMap<>();

            // Add all entries from .env to Spring's environment
            dotenv.entries().forEach(entry -> {
                dotenvProperties.put(entry.getKey(), entry.getValue());
                // Also set as system property for backward compatibility
                System.setProperty(entry.getKey(), entry.getValue());
            });

            // Add the properties to Spring's environment with high priority
            environment.getPropertySources().addFirst(
                    new MapPropertySource("dotenvProperties", dotenvProperties)
            );

            logger.info("✓ .env file loaded successfully");
        } catch (Exception e) {
            logger.warn("⚠ .env file not found or couldn't be loaded. Using environment variables or defaults.");
        }
    }
}