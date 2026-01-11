package de.bennycar.user.service;

import de.bennycar.user.constants.AppConstants;
import de.bennycar.user.exception.InvalidTokenException;
import de.bennycar.user.domain.RefreshToken;
import de.bennycar.user.domain.User;
import de.bennycar.user.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing refresh tokens.
 * Handles token creation, validation, rotation, and revocation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Creates a new refresh token for the given user.
     *
     * @param user User for whom to create the token
     * @return Tuple containing the raw token (to return to client) and the saved entity
     */
    @Transactional
    public RefreshTokenPair createRefreshToken(User user) {
        log.debug("Creating refresh token for user: {}", user.getEmail());

        String rawToken = generateRawToken();
        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().plusSeconds(AppConstants.TokenTtl.REFRESH_TOKEN_SECONDS))
                .build();

        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        log.info("Created refresh token with ID: {} for user: {}", saved.getId(), user.getEmail());

        return new RefreshTokenPair(rawToken, saved);
    }

    /**
     * Finds a valid refresh token by its raw value.
     *
     * @param rawToken Raw token string
     * @return Optional containing the refresh token if valid
     */
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findValidRefreshToken(String rawToken) {
        String tokenHash = hashToken(rawToken);
        return refreshTokenRepository.findByTokenHash(tokenHash)
                .filter(RefreshToken::isValid);
    }

    /**
     * Rotates a refresh token - revokes the old one and creates a new one.
     * This is a security best practice to prevent token reuse.
     *
     * @param oldToken The token to rotate
     * @return New refresh token pair
     */
    @Transactional
    public RefreshTokenPair rotateRefreshToken(RefreshToken oldToken) {
        log.debug("Rotating refresh token with ID: {}", oldToken.getId());

        // Revoke the old token
        oldToken.setRevoked(true);
        oldToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(oldToken);

        // Create new token
        String rawToken = generateRawToken();
        String tokenHash = hashToken(rawToken);

        RefreshToken newToken = RefreshToken.builder()
                .user(oldToken.getUser())
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().plusSeconds(AppConstants.TokenTtl.REFRESH_TOKEN_SECONDS))
                .rotatedFrom(oldToken.getId())
                .build();

        RefreshToken saved = refreshTokenRepository.save(newToken);

        // Ensure user and roles are initialized before leaving transaction
        saved.getUser().getRoles().forEach(role -> {});

        log.info("Rotated refresh token. Old ID: {}, New ID: {}", oldToken.getId(), saved.getId());

        return new RefreshTokenPair(rawToken, saved);
    }

    /**
     * Revokes a refresh token.
     *
     * @param token Token to revoke
     */
    @Transactional
    public void revokeToken(RefreshToken token) {
        log.debug("Revoking refresh token with ID: {}", token.getId());
        token.setRevoked(true);
        token.setRevokedAt(Instant.now());
        refreshTokenRepository.save(token);
        log.info("Revoked refresh token with ID: {}", token.getId());
    }

    /**
     * Revokes all active refresh tokens for a user.
     * Used during logout to prevent further refreshes.
     *
     * @param userId ID of the user whose tokens should be revoked
     */
    @Transactional
    public void revokeAllForUser(UUID userId) {
        log.debug("Revoking all active refresh tokens for user: {}", userId);
        Instant now = Instant.now();
        refreshTokenRepository.findAllByUserIdAndRevokedFalse(userId)
                .forEach(token -> {
                    token.setRevoked(true);
                    token.setRevokedAt(now);
                });
        log.info("Revoked all active refresh tokens for user: {}", userId);
    }

    /**
     * Deletes expired refresh tokens (cleanup task).
     * Should be called periodically by a scheduled job.
     */
    @Transactional
    public void deleteExpiredTokens() {
        log.debug("Deleting expired refresh tokens");
        refreshTokenRepository.deleteAllByExpiresAtBefore(Instant.now());
    }

    /**
     * Generates a cryptographically secure random token string.
     */
    private String generateRawToken() {
        byte[] bytes = new byte[AppConstants.Security.REFRESH_TOKEN_LENGTH];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Hashes a raw token using SHA-256 for secure storage.
     * We never store raw tokens in the database.
     */
    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new InvalidTokenException("Unable to hash token", e);
        }
    }

    /**
     * Record to hold both the raw token and the persisted entity.
     */
    public record RefreshTokenPair(String rawToken, RefreshToken entity) {}
}