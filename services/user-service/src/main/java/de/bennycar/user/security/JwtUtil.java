package de.bennycar.user.security;

import de.bennycar.user.constants.AppConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * Utility class for JWT token generation and validation.
 * Handles access token creation with user claims.
 */
@Slf4j
@Getter
public class JwtUtil {

    private final Key signingKey;
    private final long accessTokenTtlSeconds;

    public JwtUtil(String secret, long accessTokenTtlSeconds) {
        validateSecret(secret);
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
        log.info("JwtUtil initialized with access token TTL: {} seconds", accessTokenTtlSeconds);
    }

    /**
     * Generates a JWT access token for the given user.
     *
     * @param userId User's unique identifier
     * @param email User's email address
     * @param roles User's roles
     * @return JWT access token
     */
    public String generateAccessToken(UUID userId, String email, Set<String> roles) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(accessTokenTtlSeconds);

        String token = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(userId.toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .claim("email", email)
                .claim("roles", roles)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();

        log.debug("Generated access token for user: {}", email);
        return token;
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