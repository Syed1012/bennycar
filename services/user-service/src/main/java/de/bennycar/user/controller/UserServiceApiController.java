package de.bennycar.user.controller;

import de.bennycar.api.user.constants.ApiPaths;
import de.bennycar.api.user.constants.EndpointPaths;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller implementing the User Service API Contract.
 * Provides authentication and user management endpoints.
 */
@Slf4j
@RestController
@RequestMapping(ApiPaths.V1)
@RequiredArgsConstructor
public class UserServiceApiController implements UserServiceContract {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final HttpServletRequest httpServletRequest;

    @Override
    @PostMapping(EndpointPaths.REGISTER)
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        log.info("Registration request for email: {}", request.getEmail());
        User user = authService.register(request);
        TokenResponse response = authService.generateTokenResponse(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PostMapping(EndpointPaths.LOGIN)
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for email: {}", request.getEmail());
        User user = authService.authenticate(request.getEmail(), request.getPassword());
        TokenResponse response = authService.generateTokenResponse(user);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping(EndpointPaths.REFRESH_TOKEN)
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.debug("Token refresh request received");

        RefreshToken existingToken = refreshTokenService.findValidRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired refresh token"));

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
    @GetMapping(EndpointPaths.VALIDATE_TOKEN)
    public ResponseEntity<Void> validateToken(@RequestParam String token) {
        log.debug("Token validation request received");
        try {
            jwtUtil.parseToken(token);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Override
    @PostMapping(EndpointPaths.LOGOUT)
    public ResponseEntity<Void> logout() {
        log.debug("Logout request received");
        UUID userId = getCurrentUserId();
        refreshTokenService.revokeAllForUser(userId);


        log.info("User {} logged out", userId);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping(EndpointPaths.GET_PROFILE)
    public ResponseEntity<UserProfileResponse> getProfile() {
        log.debug("Get profile request received");
        UUID userId = getCurrentUserId();
        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping(EndpointPaths.GET_USER_BY_ID)
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable String userId) {
        log.debug("Get user by ID request for userId: {}", userId);
        UUID userUuid = UUID.fromString(userId);
        UserProfileResponse response = userService.getUserProfile(userUuid);
        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping(EndpointPaths.UPDATE_PROFILE)
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateUserProfileRequest request) {
        log.debug("Update profile request received");
        UUID userId = getCurrentUserId();
        UserProfileResponse response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(response);
    }

    @Override
    @PutMapping(EndpointPaths.CHANGE_PASSWORD)
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        log.debug("Change password request received");
        UUID userId = getCurrentUserId();
        userService.changePassword(userId, request);
        return ResponseEntity.ok().build();
    }

    @Override
    @DeleteMapping(EndpointPaths.DELETE_ACCOUNT)
    public ResponseEntity<Void> deleteAccount() {
        log.debug("Delete account request received");
        UUID userId = getCurrentUserId();
        userService.deleteAccount(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Extracts the current authenticated user's ID from the security context.
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString((String) authentication.getPrincipal());
    }
}