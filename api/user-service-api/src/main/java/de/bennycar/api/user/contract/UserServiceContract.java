package de.bennycar.api.user.contract;

import de.bennycar.api.user.constants.EndpointPaths;
import de.bennycar.api.user.constants.ErrorMessages;
import de.bennycar.api.user.constants.StatusDescriptions;
import de.bennycar.api.user.dto.request.ChangePasswordRequest;
import de.bennycar.api.user.dto.request.LoginRequest;
import de.bennycar.api.user.dto.request.RefreshTokenRequest;
import de.bennycar.api.user.dto.request.RegisterUserRequest;
import de.bennycar.api.user.dto.request.UpdateUserProfileRequest;
import de.bennycar.api.user.dto.response.ErrorResponse;
import de.bennycar.api.user.dto.response.TokenResponse;
import de.bennycar.api.user.dto.response.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * User Service API Contract.
 * Defines all endpoints that the user-service must implement.
 * <p>
 * This interface serves as a contract between services and clients.
 * Implementations must strictly follow this contract for inter-service communication.
 */
@Tag(
    name = "User Service API",
    description = "User authentication, profile management, and related operations"
)
public interface UserServiceContract {

    /**
     * Register a new user account.
     * <p>
     * Business Logic:
     * - Validates unique email constraint
     * - Encrypts password using industry-standard algorithms
     * - Creates user with ACTIVE status by default
     * - Generates and returns JWT tokens for immediate authentication
     *
     * @param request Registration request containing user details
     * @return TokenResponse with access and refresh tokens
     * @throws IllegalArgumentException if email already exists
     */
    @Operation(
        summary = "Register a new user account",
        description = "Creates a new user with the provided registration details. " +
            "Returns JWT access and refresh tokens for immediate authentication."
    )
    @ApiResponse(
        responseCode = "201",
        description = StatusDescriptions.CREATED,
        content = @Content(schema = @Schema(implementation = TokenResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = StatusDescriptions.BAD_REQUEST,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "409",
        description = ErrorMessages.USER_ALREADY_EXISTS,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "500",
        description = StatusDescriptions.INTERNAL_ERROR_DESC,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PostMapping(EndpointPaths.REGISTER)
    ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterUserRequest request);

    /**
     * Authenticate user and generate tokens.
     * <p>
     * Business Logic:
     * - Validates email exists in system
     * - Verifies password matches stored hash
     * - Generates new access and refresh tokens
     * - Logs authentication event
     *
     * @param request Login credentials
     * @return TokenResponse with authentication tokens
     * @throws IllegalArgumentException if credentials are invalid
     */
    @Operation(
        summary = "Authenticate user and generate tokens",
        description = "Validates user credentials and returns JWT access and refresh tokens."
    )
    @ApiResponse(
        responseCode = "200",
        description = StatusDescriptions.SUCCESS,
        content = @Content(schema = @Schema(implementation = TokenResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = StatusDescriptions.BAD_REQUEST,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = ErrorMessages.INVALID_CREDENTIALS,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "500",
        description = StatusDescriptions.INTERNAL_ERROR_DESC,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PostMapping(EndpointPaths.LOGIN)
    ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request);

    /**
     * Refresh access token using refresh token.
     * <p>
     * Business Logic:
     * - Validates refresh token is still valid
     * - Checks if refresh token hasn't been revoked
     * - Generates new access token with same claims
     * - Optionally rotates refresh token
     *
     * @param request Request containing refresh token
     * @return TokenResponse with new access token
     * @throws IllegalArgumentException if refresh token is invalid or expired
     */
    @Operation(
        summary = "Refresh access token",
        description = "Generates a new access token using a valid refresh token."
    )
    @ApiResponse(
        responseCode = "200",
        description = StatusDescriptions.SUCCESS,
        content = @Content(schema = @Schema(implementation = TokenResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = StatusDescriptions.BAD_REQUEST,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = ErrorMessages.INVALID_TOKEN + " or " + ErrorMessages.TOKEN_EXPIRED,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "500",
        description = StatusDescriptions.INTERNAL_ERROR_DESC,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PostMapping(EndpointPaths.REFRESH_TOKEN)
    ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request);

    /**
     * Validate JWT token.
     * <p>
     * Business Logic:
     * - Verifies token signature
     * - Checks token expiration
     * - Validates token claims (issuer, audience, etc.)
     * - Returns token validity status
     *
     * @param token JWT token to validate
     * @return Response with validation status
     */
    @Operation(
        summary = "Validate JWT token",
        description = "Validates if a JWT token is valid and not expired."
    )
    @ApiResponse(
        responseCode = "200",
        description = StatusDescriptions.SUCCESS
    )
    @ApiResponse(
        responseCode = "401",
        description = ErrorMessages.INVALID_TOKEN,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "500",
        description = StatusDescriptions.INTERNAL_ERROR_DESC,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @GetMapping(EndpointPaths.VALIDATE_TOKEN)
    ResponseEntity<Void> validateToken(@RequestParam String token);

    /**
     * Logout user and revoke tokens.
     * <p>
     * Business Logic:
     * - Revokes refresh token
     * - Optionally blacklists access token
     * - Logs logout event
     * - Clears user session if applicable
     *
     * @return Success response
     */
    @Operation(
        summary = "Logout user",
        description = "Invalidates user's tokens and clears session."
    )
    @ApiResponse(
        responseCode = "200",
        description = StatusDescriptions.SUCCESS
    )
    @ApiResponse(
        responseCode = "401",
        description = ErrorMessages.UNAUTHORIZED,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "500",
        description = StatusDescriptions.INTERNAL_ERROR_DESC,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PostMapping(EndpointPaths.LOGOUT)
    @SecurityRequirement(name = "Bearer Token")
    ResponseEntity<Void> logout();

    /**
     * Get current user's profile.
     * <p>
     * Business Logic:
     * - Extracts user from JWT token
     * - Retrieves user profile from database
     * - Returns complete profile information
     *
     * @return User profile response
     */
    @Operation(
        summary = "Get current user profile",
        description = "Returns the authenticated user's profile information."
    )
    @ApiResponse(
        responseCode = "200",
        description = StatusDescriptions.SUCCESS,
        content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = ErrorMessages.UNAUTHORIZED,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "500",
        description = StatusDescriptions.INTERNAL_ERROR_DESC,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @GetMapping(EndpointPaths.GET_PROFILE)
    @SecurityRequirement(name = "Bearer Token")
    ResponseEntity<UserProfileResponse> getProfile();

    /**
     * Get user profile by ID.
     * <p>
     * Business Logic:
     * - Validates user exists
     * - Checks authorization (user can view own profile or has admin role)
     * - Returns user profile data
     *
     * @param userId User ID to retrieve
     * @return User profile response
     * @throws IllegalArgumentException if user not found
     */
    @Operation(
        summary = "Get user profile by ID",
        description = "Returns profile information for a specific user. " +
            "User can only access their own profile unless they are an administrator."
    )
    @ApiResponse(
        responseCode = "200",
        description = StatusDescriptions.SUCCESS,
        content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = StatusDescriptions.UNAUTHORIZED_DESC,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "403",
        description = StatusDescriptions.FORBIDDEN_DESC,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = StatusDescriptions.NOT_FOUND,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "500",
        description = StatusDescriptions.INTERNAL_ERROR_DESC,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @GetMapping(EndpointPaths.GET_USER_BY_ID)
    @SecurityRequirement(name = "Bearer Token")
    ResponseEntity<UserProfileResponse> getUserById(@PathVariable String userId);

    /**
     * Update current user's profile.
     * <p>
     * Business Logic:
     * - Validates email uniqueness if updated
     * - Allows partial updates (null fields are ignored)
     * - Updates only allowed fields
     * - Audits the change
     *
     * @param request Update request with new profile data
     * @return Updated user profile
     */
    @Operation(
        summary = "Update current user profile",
        description = "Updates the authenticated user's profile information. " +
            "Null fields are ignored, allowing partial updates."
    )
    @ApiResponse(
        responseCode = "200",
        description = StatusDescriptions.SUCCESS,
        content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = StatusDescriptions.BAD_REQUEST,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = StatusDescriptions.UNAUTHORIZED_DESC,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "409",
        description = StatusDescriptions.CONFLICT,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "500",
        description = StatusDescriptions.INTERNAL_ERROR_DESC,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PutMapping(EndpointPaths.UPDATE_PROFILE)
    @SecurityRequirement(name = "Bearer Token")
    ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateUserProfileRequest request);

    /**
     * Change user password.
     * <p>
     * Business Logic:
     * - Verifies current password matches
     * - Validates new password meets security requirements
     * - Ensures new password differs from current
     * - Confirms password and confirmation match
     * - Hashes and stores new password
     * - Invalidates all existing refresh tokens
     *
     * @param request Password change request
     * @return Success response
     */
    @Operation(
        summary = "Change user password",
        description = "Changes the authenticated user's password. " +
            "Current password verification is required. All active sessions will be invalidated."
    )
    @ApiResponse(
        responseCode = "200",
        description = StatusDescriptions.SUCCESS
    )
    @ApiResponse(
        responseCode = "400",
        description = StatusDescriptions.BAD_REQUEST,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "401",
        description = StatusDescriptions.UNAUTHORIZED_DESC,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "500",
        description = StatusDescriptions.INTERNAL_ERROR_DESC,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PostMapping(EndpointPaths.CHANGE_PASSWORD)
    @SecurityRequirement(name = "Bearer Token")
    ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request);

    /**
     * Delete user account.
     * <p>
     * Business Logic:
     * - Requires current password for security
     * - Soft-deletes user (marks as DELETED status)
     * - Preserves audit trail
     * - Revokes all tokens
     * - Clears user sessions
     *
     * @return Success response
     */
    @Operation(
        summary = "Delete user account",
        description = "Permanently deletes the authenticated user's account. " +
            "Current password verification is required. This action is irreversible."
    )
    @ApiResponse(
        responseCode = "200",
        description = StatusDescriptions.SUCCESS
    )
    @ApiResponse(
        responseCode = "401",
        description = StatusDescriptions.UNAUTHORIZED_DESC,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
        responseCode = "500",
        description = StatusDescriptions.INTERNAL_ERROR_DESC,
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @DeleteMapping(EndpointPaths.DELETE_ACCOUNT)
    @SecurityRequirement(name = "Bearer Token")
    ResponseEntity<Void> deleteAccount();
}

