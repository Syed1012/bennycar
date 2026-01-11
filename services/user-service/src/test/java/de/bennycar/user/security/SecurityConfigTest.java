package de.bennycar.user.security;

import de.bennycar.user.service.TokenBlacklistService;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for SecurityConfig validation logic.
 */
class SecurityConfigTest {

    private static final String CUSTOM_JWT_SECRET = "my-custom-super-secret-jwt-key-that-is-very-long-and-secure-123456";

    @Test
    void validateSecurityConfiguration_shouldFailInProductionWithDefaultSecret() {
        // Given
        Environment environment = mock(Environment.class);
        TokenBlacklistService tokenBlacklistService = mock(TokenBlacklistService.class);
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});

        SecurityConfig securityConfig = new SecurityConfig(environment, tokenBlacklistService);
        // Use reflection to set the jwtSecret field
        setJwtSecret(securityConfig, SecurityConfig.DEFAULT_JWT_SECRET);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            securityConfig::validateSecurityConfiguration);
        
        assertTrue(exception.getMessage().contains("CRITICAL SECURITY ERROR"));
        assertTrue(exception.getMessage().contains("Default JWT secret is still in use"));
    }

    @Test
    void validateSecurityConfiguration_shouldSucceedInProductionWithCustomSecret() {
        // Given
        Environment environment = mock(Environment.class);
        TokenBlacklistService tokenBlacklistService = mock(TokenBlacklistService.class);
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});

        SecurityConfig securityConfig = new SecurityConfig(environment, tokenBlacklistService);
        setJwtSecret(securityConfig, CUSTOM_JWT_SECRET);

        // When & Then - should not throw any exception
        assertDoesNotThrow(securityConfig::validateSecurityConfiguration);
    }

    @Test
    void validateSecurityConfiguration_shouldSucceedInDevWithDefaultSecret() {
        // Given
        Environment environment = mock(Environment.class);
        TokenBlacklistService tokenBlacklistService = mock(TokenBlacklistService.class);
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});

        SecurityConfig securityConfig = new SecurityConfig(environment, tokenBlacklistService);
        setJwtSecret(securityConfig, SecurityConfig.DEFAULT_JWT_SECRET);

        // When & Then - should not throw any exception
        assertDoesNotThrow(securityConfig::validateSecurityConfiguration);
    }

    @Test
    void validateSecurityConfiguration_shouldSucceedInDevWithCustomSecret() {
        // Given
        Environment environment = mock(Environment.class);
        TokenBlacklistService tokenBlacklistService = mock(TokenBlacklistService.class);
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});

        SecurityConfig securityConfig = new SecurityConfig(environment, tokenBlacklistService);
        setJwtSecret(securityConfig, CUSTOM_JWT_SECRET);

        // When & Then - should not throw any exception
        assertDoesNotThrow(securityConfig::validateSecurityConfiguration);
    }

    /**
     * Helper method to set jwtSecret using reflection
     */
    private void setJwtSecret(SecurityConfig config, String secret) {
        try {
            var field = SecurityConfig.class.getDeclaredField("jwtSecret");
            field.setAccessible(true);
            field.set(config, secret);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set jwtSecret via reflection", e);
        }
    }
}
