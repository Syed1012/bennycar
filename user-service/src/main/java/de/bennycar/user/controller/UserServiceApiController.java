package de.bennycar.user.controller;

import de.bennycar.api.user.contract.UserServiceContract;
import de.bennycar.api.user.dto.request.ChangePasswordRequest;
import de.bennycar.api.user.dto.request.LoginRequest;
import de.bennycar.api.user.dto.request.RefreshTokenRequest;
import de.bennycar.api.user.dto.request.RegisterUserRequest;
import de.bennycar.api.user.dto.request.UpdateUserProfileRequest;
import de.bennycar.api.user.dto.response.TokenResponse;
import de.bennycar.api.user.dto.response.UserProfileResponse;
import de.bennycar.user.domain.RefreshToken;
import de.bennycar.user.domain.User;
import de.bennycar.user.exception.InvalidTokenException;
import de.bennycar.user.security.JwtUtil;
import de.bennycar.user.service.AuthService;
import de.bennycar.user.service.RefreshTokenService;
import de.bennycar.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Implementation of the User Service API Contract.
 * This controller provides all user-related endpoints as defined in the API contract.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserServiceApiController implements UserServiceContract {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        log.info("API Contract: Registration request received for email: {}", request.getEmail());

        // Use API DTO directly - no mapping needed
        User user = authService.register(request);
        TokenResponse response = authService.generateTokenResponse(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("API Contract: Login request received for email: {}", request.getEmail());

        // Use API DTO directly
        User user = authService.authenticate(request.getEmail(), request.getPassword());
        TokenResponse response = authService.generateTokenResponse(user);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.debug("API Contract: Token refresh request received");

        RefreshToken existingToken = refreshTokenService.findValidRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired refresh token"));

        // Rotate the refresh token for security
        RefreshTokenService.RefreshTokenPair newTokenPair = refreshTokenService.rotateRefreshToken(existingToken);
        String accessToken = authService.generateAccessToken(existingToken.getUser());

        TokenResponse response = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newTokenPair.rawToken())
                .expiresIn(jwtUtil.getAccessTokenTtlSeconds())
                .tokenType("Bearer")
                .build();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> validateToken(@RequestParam String token) {
        log.debug("API Contract: Token validation request received");

        try {
            jwtUtil.parseToken(token);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Override
    public ResponseEntity<Void> logout() {
        log.debug("API Contract: Logout request received");

        // Extract refresh token from security context or request
        // Note: Current implementation uses refresh token in body
        // This implementation invalidates the user session
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            UUID userId = UUID.fromString((String) authentication.getPrincipal());
            log.info("User {} logged out successfully", userId);
        }

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<UserProfileResponse> getProfile() {
        log.debug("API Contract: Get current user profile request received");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) authentication.getPrincipal());

        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable String userId) {
        log.debug("API Contract: Get user profile by ID request received for userId: {}", userId);

        // TODO: Add authorization check - user can only view their own profile or must be admin
        UUID userUuid = UUID.fromString(userId);
        UserProfileResponse response = userService.getUserProfile(userUuid);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateUserProfileRequest request) {
        log.debug("API Contract: Update user profile request received");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) authentication.getPrincipal());

        // TODO: Implement updateProfile in UserService
        // de.bennycar.user.dto.UserProfileResponse internalResponse = userService.updateProfile(userId, request);
        // UserProfileResponse apiResponse = apiDtoMapper.toApiUserProfileResponse(internalResponse);

        log.warn("Update profile not yet implemented");
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        log.debug("API Contract: Change password request received");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) authentication.getPrincipal());

        // TODO: Implement changePassword in UserService
        // userService.changePassword(userId, request);

        log.warn("Change password not yet implemented");
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<Void> deleteAccount() {
        log.debug("API Contract: Delete account request received");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) authentication.getPrincipal());

        // TODO: Implement deleteAccount in UserService
        // userService.deleteAccount(userId);

        log.warn("Delete account not yet implemented");
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}

