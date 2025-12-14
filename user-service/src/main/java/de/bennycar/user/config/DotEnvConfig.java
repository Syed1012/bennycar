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
            // Try to load .env file from multiple locations
            Dotenv dotenv = null;

            // First try current directory (user-service/)
            try {
                dotenv = Dotenv.configure()
                        .directory("./")
                        .ignoreIfMissing()
                        .load();
                logger.info("Attempting to load .env from current directory: ./");
            } catch (Exception e) {
                logger.debug("Could not load .env from current directory, will try parent");
            }

            // If not found, try parent directory (for when running from IDE or mvnw from root)
            if (dotenv == null || dotenv.entries().isEmpty()) {
                try {
                    dotenv = Dotenv.configure()
                            .directory("./user-service/")
                            .ignoreIfMissing()
                            .load();
                    logger.info("Attempting to load .env from: ./user-service/");
                } catch (Exception e) {
                    logger.debug("Could not load .env from user-service directory");
                }
            }

            if (dotenv != null && !dotenv.entries().isEmpty()) {
                Map<String, Object> dotenvProperties = new HashMap<>();

                // Add all entries from .env to Spring's environment
                dotenv.entries().forEach(entry -> {
                    dotenvProperties.put(entry.getKey(), entry.getValue());
                    // Also set as system property for backward compatibility
                    System.setProperty(entry.getKey(), entry.getValue());
                    // Only log the key name, never the value to avoid leaking secrets
                    logger.debug("Loaded env variable: {}", entry.getKey());
                });

                // Add the properties to Spring's environment with high priority
                environment.getPropertySources().addFirst(
                        new MapPropertySource("dotenvProperties", dotenvProperties)
                );

                logger.info("✓ .env file loaded successfully with {} properties", dotenvProperties.size());
            } else {
                logger.warn("⚠ .env file not found. Using environment variables or defaults.");
            }
        } catch (Exception e) {
            logger.warn("⚠ .env file couldn't be loaded: {}. Using environment variables or defaults.", e.getMessage());
        }
    }
}