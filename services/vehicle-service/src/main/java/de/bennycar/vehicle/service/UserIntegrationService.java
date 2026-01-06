package de.bennycar.vehicle.service;

import de.bennycar.api.user.dto.response.UserProfileResponse;
import de.bennycar.vehicle.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Example service demonstrating how to use the UserServiceClient (Feign client).
 * This shows type-safe inter-service communication using the API contract.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserIntegrationService {

    private final UserServiceClient userServiceClient;

    /**
     * Example: Validate a JWT token by calling the user service.
     *
     * @param token JWT token to validate
     * @return true if token is valid, false otherwise
     */
    public boolean validateUserToken(String token) {
        try {
            ResponseEntity<Void> response = userServiceClient.validateToken(token);
            boolean isValid = response.getStatusCode().is2xxSuccessful();
            log.debug("Token validation result: {}", isValid);
            return isValid;
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Example: Get the current user's profile.
     * This demonstrates type-safe response handling with the API contract.
     *
     * @return User profile information
     */
    public UserProfileResponse getCurrentUserProfile() {
        log.debug("Fetching current user profile from user-service");

        ResponseEntity<UserProfileResponse> response = userServiceClient.getProfile();

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            UserProfileResponse profile = response.getBody();
            log.info("Retrieved profile for user: {}", profile.getEmail());
            return profile;
        }

        throw new RuntimeException("Failed to retrieve user profile");
    }

    /**
     * Example: Get a user profile by ID.
     * Useful for vehicle ownership validation or order processing.
     *
     * @param userId User ID to retrieve
     * @return User profile
     */
    public UserProfileResponse getUserProfileById(String userId) {
        log.debug("Fetching user profile for ID: {}", userId);

        ResponseEntity<UserProfileResponse> response = userServiceClient.getUserById(userId);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        throw new RuntimeException("User not found: " + userId);
    }

    /**
     * Example: Check if a user exists and is authorized to perform an action.
     * This could be used before allowing vehicle customization or purchase.
     *
     * @param userId User ID to check
     * @return true if user exists and is active
     */
    public boolean isUserAuthorized(String userId) {
        try {
            UserProfileResponse profile = getUserProfileById(userId);
            // Check if user is active and has required permissions
            log.debug("User {} authorization check: email={}", userId, profile.getEmail());
            return true;
        } catch (Exception e) {
            log.warn("User {} authorization failed: {}", userId, e.getMessage());
            return false;
        }
    }
}

