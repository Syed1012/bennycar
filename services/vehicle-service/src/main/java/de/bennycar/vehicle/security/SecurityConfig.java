package de.bennycar.vehicle.security;

import de.bennycar.vehicle.constants.AppConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for the Vehicle Service.
 * Configures JWT authentication, CORS, and endpoint security.
 * Only active in non-dev profiles (prod, etc.).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("!dev")
public class SecurityConfig {

    @Value("${security.jwt.secret:CHANGE_ME_TO_A_LONG_RANDOM_SECRET_VALUE_32_CHARS_MIN}")
    private String jwtSecret;

    @Value("${security.cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private String allowedOrigins;

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
                // Public endpoints - browsing vehicles
                .requestMatchers(HttpMethod.GET, AppConstants.Api.BRANDS + "/**").permitAll()
                .requestMatchers(HttpMethod.GET, AppConstants.Api.VEHICLE_TYPES + "/**").permitAll()
                .requestMatchers(HttpMethod.GET, AppConstants.Api.VEHICLES + "/**").permitAll()
                .requestMatchers(HttpMethod.GET, AppConstants.Api.CUSTOMIZATION_CATEGORIES + "/**").permitAll()
                .requestMatchers(HttpMethod.GET, AppConstants.Api.CUSTOMIZATION_OPTIONS + "/**").permitAll()
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
                // Admin endpoints for catalog management
                .requestMatchers(HttpMethod.POST, AppConstants.Api.BRANDS + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, AppConstants.Api.BRANDS + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, AppConstants.Api.BRANDS + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, AppConstants.Api.VEHICLE_TYPES + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, AppConstants.Api.VEHICLE_TYPES + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, AppConstants.Api.VEHICLE_TYPES + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, AppConstants.Api.VEHICLES + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, AppConstants.Api.VEHICLES + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, AppConstants.Api.VEHICLES + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, AppConstants.Api.CUSTOMIZATION_CATEGORIES + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, AppConstants.Api.CUSTOMIZATION_CATEGORIES + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, AppConstants.Api.CUSTOMIZATION_CATEGORIES + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, AppConstants.Api.CUSTOMIZATION_OPTIONS + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, AppConstants.Api.CUSTOMIZATION_OPTIONS + "/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, AppConstants.Api.CUSTOMIZATION_OPTIONS + "/**").hasRole("ADMIN")
                // Configuration endpoints require authentication
                .requestMatchers(AppConstants.Api.CONFIGURATIONS + "/**").authenticated()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings.
     */
    @Bean(name = "prodCorsConfigurationSource")
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
     * JWT utility bean for token validation.
     */
    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(jwtSecret);
    }
}
