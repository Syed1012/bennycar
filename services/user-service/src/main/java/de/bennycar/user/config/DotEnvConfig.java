package de.bennycar.user.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads environment variables from .env files into Spring's environment.
 * This is useful for local development to avoid exposing secrets in config files.
 *
 * Note: In production, use proper secret management (e.g., Kubernetes secrets, Vault).
 */
public class DotEnvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger log = LoggerFactory.getLogger(DotEnvConfig.class);
    private static final String[] SEARCH_PATHS = {"./", "./user-service/", "../user-service/"};

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();

        try {
            Dotenv dotenv = loadDotenvFromPaths();

            if (dotenv != null && !dotenv.entries().isEmpty()) {
                Map<String, Object> properties = new HashMap<>();
                dotenv.entries().forEach(entry -> {
                    properties.put(entry.getKey(), entry.getValue());
                    System.setProperty(entry.getKey(), entry.getValue());
                });

                environment.getPropertySources().addFirst(
                        new MapPropertySource("dotenvProperties", properties)
                );
                log.info("Loaded {} properties from .env file", properties.size());
            } else {
                log.debug(".env file not found - using system environment variables");
            }
        } catch (Exception e) {
            log.debug("Could not load .env file: {}", e.getMessage());
        }
    }

    private Dotenv loadDotenvFromPaths() {
        for (String dir : SEARCH_PATHS) {
            File envFile = new File(dir + ".env");
            if (envFile.exists()) {
                log.debug("Found .env file at: {}", envFile.getAbsolutePath());
                return Dotenv.configure()
                        .directory(dir)
                        .ignoreIfMissing()
                        .load();
            }
        }
        return null;
    }
}