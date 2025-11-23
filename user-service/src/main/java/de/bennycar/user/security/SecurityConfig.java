package de.bennycar.user.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${security.jwt.secret:CHANGE_ME_TO_A_LONG_RANDOM_SECRET_VALUE_32_CHARS_MIN}")
    private String jwtSecret;

    @Value("${security.jwt.access-ttl-seconds:600}")
    private long accessTtl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/refresh").permitAll()
                    .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                    .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults()); // placeholder, replace with JWT filter later
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Argon2 parameters (saltLength, hashLength, parallelism, memory, iterations)
        return new Argon2PasswordEncoder(16, 32, 1, 4096, 3);
    }

    @Bean
    public JwtUtil jwtUtil() { return new JwtUtil(jwtSecret, accessTtl); }
}
