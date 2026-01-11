package de.bennycar.user.repository;

import de.bennycar.user.domain.RefreshToken;
import de.bennycar.user.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    @EntityGraph(attributePaths = {"user", "user.roles"})
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @EntityGraph(attributePaths = {"user", "user.roles"})
    List<RefreshToken> findAllByUserAndRevokedFalse(User user);

    @EntityGraph(attributePaths = {"user", "user.roles"})
    List<RefreshToken> findAllByUserIdAndRevokedFalse(UUID userId);

    void deleteAllByExpiresAtBefore(Instant cutoff);
}