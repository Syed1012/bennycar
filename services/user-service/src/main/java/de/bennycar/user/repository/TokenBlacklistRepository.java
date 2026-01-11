package de.bennycar.user.repository;

import de.bennycar.user.domain.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for token blacklist operations.
 */
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, UUID> {

    /**
     * Checks if a token ID is blacklisted.
     *
     * @param tokenId JWT token ID (jti claim)
     * @return true if token is blacklisted and not expired
     */
    default boolean isTokenBlacklisted(String tokenId) {
        return findByTokenId(tokenId)
                .filter(token -> !token.isExpired())
                .isPresent();
    }

    /**
     * Finds a blacklisted token by its ID.
     *
     * @param tokenId JWT token ID (jti claim)
     * @return Optional containing the blacklisted token if found
     */
    Optional<TokenBlacklist> findByTokenId(String tokenId);

    /**
     * Deletes expired blacklist entries (cleanup task).
     *
     * @param cutoff timestamp before which entries are considered expired
     * @return number of deleted entries
     */
    long deleteAllByExpiresAtBefore(Instant cutoff);

    /**
     * Deletes all blacklist entries for a user.
     * Used when user logs out from all devices.
     *
     * @param userId user ID
     */
    void deleteAllByUserId(UUID userId);
}
