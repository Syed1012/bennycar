package de.bennycar.user.controller;

import de.bennycar.user.constants.AppConstants;
import de.bennycar.user.dto.*;
import de.bennycar.user.exception.InvalidTokenException;
import de.bennycar.user.domain.RefreshToken;
import de.bennycar.user.domain.User;
import de.bennycar.user.service.AuthService;
import de.bennycar.user.service.RefreshTokenService;
import de.bennycar.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * REST controller for authentication and user management endpoints.
 * Handles registration, login, token refresh, logout, and user profile operations.
 */
@Slf4j
@RestController
@RequestMapping(AppConstants.Api.V1)
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and authorization endpoints")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided registration details. Returns authentication tokens."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "User successfully registered",
            content = @Content(schema = @Schema(implementation = TokenResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "User already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/auth/register")
    public ResponseEntity<TokenResponse> register(@RequestBody @Valid RegistrationRequest request) {
        log.info("Registration request received for email: {}", request.getEmail());

        User user = authService.register(request);
        TokenResponse tokenResponse = authService.generateTokenResponse(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponse);
    }

    @Operation(
        summary = "User login",
        description = "Authenticates a user with email and password. Returns authentication tokens on success."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(schema = @Schema(implementation = TokenResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/auth/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());

        User user = authService.authenticate(request.getEmail(), request.getPassword());
        TokenResponse tokenResponse = authService.generateTokenResponse(user);

        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(
        summary = "Refresh access token",
        description = "Generates a new access token using a valid refresh token. Implements token rotation for security."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Token refreshed successfully",
            content = @Content(schema = @Schema(implementation = TokenResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid or expired refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/auth/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        log.debug("Token refresh request received");

        RefreshToken existingToken = refreshTokenService.findValidRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired refresh token"));

        // Rotate the refresh token for security
        RefreshTokenService.RefreshTokenPair newTokenPair = refreshTokenService.rotateRefreshToken(existingToken);
        String accessToken = authService.generateAccessToken(existingToken.getUser());

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newTokenPair.rawToken())
                .expiresIn(authService.generateAccessToken(existingToken.getUser()).length())
                .build();

        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(
        summary = "User logout",
        description = "Revokes the provided refresh token, effectively logging out the user."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Logout successful"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid RefreshTokenRequest request) {
        log.debug("Logout request received");

        refreshTokenService.findValidRefreshToken(request.getRefreshToken())
                .ifPresent(refreshTokenService::revokeToken);

        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Get current user profile",
        description = "Retrieves the profile information of the currently authenticated user."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Profile retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/users/me")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString((String) authentication.getPrincipal());

        log.debug("Fetching profile for user ID: {}", userId);
        UserProfileResponse profile = userService.getUserProfile(userId);

        return ResponseEntity.ok(profile);
    }
}
