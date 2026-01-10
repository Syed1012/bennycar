package de.bennycar.vehicle.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Development security configuration - permits all requests for testing.
 * This configuration is ONLY active when the 'dev' profile is enabled.
 */
@Configuration
@EnableWebSecurity
@Profile("dev")
public class DevSecurityConfig {

    @Value("${security.jwt.secret:dev-jwt-secret-key-for-development-only-32chars}")
    private String jwtSecret;

    /**
     * Configures a permissive security filter chain for development.
     * Allows all requests without authentication.
     */
    @Bean
    public SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Allow all requests in development mode
                .anyRequest().permitAll()
            );

        return http.build();
    }

    /**
     * Configures CORS for development.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * JWT utility bean for token validation (required by JwtAuthenticationFilter).
     */
    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(jwtSecret);
    }
}

