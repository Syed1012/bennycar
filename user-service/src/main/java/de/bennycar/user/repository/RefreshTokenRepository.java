package de.bennycar.user.repository;

import de.bennycar.user.domain.RefreshToken;
import de.bennycar.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findAllByUserAndRevokedFalse(User user);
    void deleteAllByExpiresAtBefore(Instant cutoff);
}

