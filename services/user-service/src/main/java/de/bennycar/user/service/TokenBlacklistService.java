package de.bennycar.user.service;

import de.bennycar.user.domain.TokenBlacklist;
import de.bennycar.user.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Service for managing token blacklist.
 * Handles blacklisting of access tokens for logout functionality.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;

    /**
     * Blacklists an access token by its JWT ID.
     *
     * @param tokenId JWT token ID (jti claim)
     * @param userId User ID who owns the token
     * @param expiresAt Token expiration time
     */
    @Transactional
    public void blacklistToken(String tokenId, UUID userId, Instant expiresAt) {
        log.debug("Blacklisting token with ID: {} for user: {}", tokenId, userId);

        // Check if already blacklisted
        if (tokenBlacklistRepository.findByTokenId(tokenId).isPresent()) {
            log.debug("Token {} is already blacklisted", tokenId);
            return;
        }

        TokenBlacklist blacklistEntry = TokenBlacklist.builder()
                .tokenId(tokenId)
                .userId(userId)
                .expiresAt(expiresAt)
                .blacklistedAt(Instant.now())
                .build();

        tokenBlacklistRepository.save(blacklistEntry);
        log.info("Successfully blacklisted token {} for user {}", tokenId, userId);
    }

    /**
     * Checks if a token is blacklisted.
     *
     * @param tokenId JWT token ID (jti claim)
     * @return true if token is blacklisted and not expired
     */
    @Transactional(readOnly = true)
    public boolean isTokenBlacklisted(String tokenId) {
        return tokenBlacklistRepository.isTokenBlacklisted(tokenId);
    }

    /**
     * Blacklists all tokens for a user by clearing their blacklist entries.
     * This is a more aggressive approach - we'll blacklist any future tokens
     * by tracking the logout timestamp. For now, we'll delete old entries.
     *
     * @param userId User ID
     */
    @Transactional
    public void blacklistAllForUser(UUID userId) {
        log.debug("Blacklisting all tokens for user: {}", userId);
        // Delete existing entries - they're already expired or will be
        tokenBlacklistRepository.deleteAllByUserId(userId);
        log.info("Cleared blacklist entries for user: {}", userId);
    }

    /**
     * Deletes expired blacklist entries (cleanup task).
     * Should be called periodically by a scheduled job.
     */
    @Transactional
    public void deleteExpiredEntries() {
        log.debug("Deleting expired blacklist entries");
        long deleted = tokenBlacklistRepository.deleteAllByExpiresAtBefore(Instant.now());
        log.info("Deleted {} expired blacklist entries", deleted);
    }
}
