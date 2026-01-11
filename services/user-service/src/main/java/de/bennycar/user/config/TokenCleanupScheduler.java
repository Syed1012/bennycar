package de.bennycar.user.config;

import de.bennycar.user.service.RefreshTokenService;
import de.bennycar.user.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled tasks for periodic maintenance operations.
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * Cleans up expired refresh tokens every 24 hours.
     * Runs at 2 AM daily.
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupExpiredTokens() {
        log.info("Starting cleanup of expired refresh tokens");
        try {
            refreshTokenService.deleteExpiredTokens();
            log.info("Successfully completed cleanup of expired refresh tokens");
        } catch (Exception e) {
            log.error("Error during refresh token cleanup", e);
        }
    }

    /**
     * Cleans up expired blacklist entries every 24 hours.
     * Runs at 2:30 AM daily.
     */
    @Scheduled(cron = "0 30 2 * * *")
    public void cleanupExpiredBlacklistEntries() {
        log.info("Starting cleanup of expired blacklist entries");
        try {
            tokenBlacklistService.deleteExpiredEntries();
            log.info("Successfully completed cleanup of expired blacklist entries");
        } catch (Exception e) {
            log.error("Error during blacklist cleanup", e);
        }
    }
}