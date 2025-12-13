package de.bennycar.vehicle.security;

import de.bennycar.vehicle.constants.AppConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.Key;

/**
 * Utility class for JWT token validation.
 * This service only validates tokens - it does not generate them.
 * Token generation is handled by the user-service.
 */
@Slf4j
@Getter
public class JwtUtil {

    private final Key signingKey;

    public JwtUtil(String secret) {
        validateSecret(secret);
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        log.info("JwtUtil initialized for token validation");
    }

    /**
     * Parses and validates a JWT token, extracting its claims.
     *
     * @param token JWT token to parse
     * @return Claims from the token
     * @throws io.jsonwebtoken.JwtException if token is invalid
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private void validateSecret(String secret) {
        if (secret == null || secret.length() < AppConstants.Security.MIN_JWT_SECRET_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("JWT secret must be at least %d characters",
                            AppConstants.Security.MIN_JWT_SECRET_LENGTH)
            );
        }
    }
}
