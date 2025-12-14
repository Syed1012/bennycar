package de.bennycar.user.security;

import de.bennycar.user.constants.AppConstants;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for the application.
 * Configures JWT authentication, CORS, password encoding, and endpoint security.
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String DEFAULT_JWT_SECRET = "CHANGE_ME_TO_A_LONG_RANDOM_SECRET_VALUE_32_CHARS_MIN";

    private final Environment environment;

    @Value("${security.jwt.secret:CHANGE_ME_TO_A_LONG_RANDOM_SECRET_VALUE_32_CHARS_MIN}")
    private String jwtSecret;

    @Value("${security.jwt.access-ttl-seconds:600}")
    private long accessTtl;

    @Value("${security.cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private String allowedOrigins;

    public SecurityConfig(Environment environment) {
        this.environment = environment;
    }

    /**
     * Validates security configuration on application startup.
     * Fails fast if critical security settings are misconfigured, especially in production.
     */
    @PostConstruct
    public void validateSecurityConfiguration() {
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isProduction = Arrays.asList(activeProfiles).contains("prod");

        if (isProduction && DEFAULT_JWT_SECRET.equals(jwtSecret)) {
            String errorMessage = "CRITICAL SECURITY ERROR: Default JWT secret is still in use in production environment. " +
                    "This poses a severe security risk as anyone can forge JWT tokens. " +
                    "Set a strong, random JWT secret via the JWT_SECRET environment variable.";
            log.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        if (DEFAULT_JWT_SECRET.equals(jwtSecret)) {
            log.warn("WARNING: Default JWT secret is in use. This is acceptable for development but MUST be changed for production.");
        } else {
            log.info("JWT secret configured successfully.");
        }
    }

    /**
     * Configures the security filter chain with JWT authentication.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(
                    HttpMethod.POST,
                    AppConstants.Api.AUTH_ENDPOINT + "/register",
                    AppConstants.Api.AUTH_ENDPOINT + "/login",
                    AppConstants.Api.AUTH_ENDPOINT + "/refresh"
                ).permitAll()
                // Actuator endpoints
                .requestMatchers(
                    "/actuator/health",
                    "/actuator/info",
                    "/actuator/prometheus"
                ).permitAll()
                // OpenAPI/Swagger endpoints
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Password encoder using BCrypt algorithm for secure password hashing.
     * BCrypt is a well-established password hashing function that automatically
     * handles salting and is resistant to brute-force attacks.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt with strength 12 (2^12 = 4096 rounds)
        return new BCryptPasswordEncoder(12);
    }

    /**
     * JWT utility bean for token generation and validation.
     */
    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(jwtSecret, accessTtl);
    }
}
