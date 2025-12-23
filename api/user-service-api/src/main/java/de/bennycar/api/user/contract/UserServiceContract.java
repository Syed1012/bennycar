package de.bennycar.api.user.contract;

import de.bennycar.api.user.constants.UserApiConstants;
import de.bennycar.api.user.dto.request.*;
import de.bennycar.api.user.dto.response.ErrorResponse;
import de.bennycar.api.user.dto.response.SuccessResponse;
import de.bennycar.api.user.dto.response.TokenResponse;
import de.bennycar.api.user.dto.response.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User Service API Contract.
 * Defines all endpoints that the user-service must implement.
 *
 * This interface serves as a contract between services and clients.
 * Implementations must strictly follow this contract for inter-service communication.
 *
 * @author Bennycar Development Team
 * @version 1.0
 */
@Tag(
    name = "User Service API",
    description = "User authentication, profile management, and related operations"
)
public interface UserServiceContract {

    /**
     * Register a new user account.
     *
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
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = UserApiConstants.StatusDescriptions.CREATED,
            content = @Content(schema = @Schema(implementation = TokenResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = UserApiConstants.StatusDescriptions.BAD_REQUEST,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = UserApiConstants.ErrorMessages.USER_ALREADY_EXISTS,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = UserApiConstants.StatusDescriptions.INTERNAL_ERROR_DESC,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping(UserApiConstants.Endpoints.REGISTER)
    ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterUserRequest request);

    /**
     * Authenticate user and generate tokens.
     *
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
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = UserApiConstants.StatusDescriptions.SUCCESS,
            content = @Content(schema = @Schema(implementation = TokenResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = UserApiConstants.StatusDescriptions.BAD_REQUEST,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = UserApiConstants.ErrorMessages.INVALID_CREDENTIALS,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = UserApiConstants.StatusDescriptions.INTERNAL_ERROR_DESC,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping(UserApiConstants.Endpoints.LOGIN)
    ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request);

    /**
     * Refresh access token using refresh token.
     *
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
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = UserApiConstants.StatusDescriptions.SUCCESS,
            content = @Content(schema = @Schema(implementation = TokenResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = UserApiConstants.StatusDescriptions.BAD_REQUEST,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = UserApiConstants.ErrorMessages.INVALID_TOKEN,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = UserApiConstants.StatusDescriptions.INTERNAL_ERROR_DESC,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping(UserApiConstants.Endpoints.REFRESH_TOKEN)
    ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request);

    /**
     * Validate JWT token.
     *
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
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = UserApiConstants.StatusDescriptions.SUCCESS
        ),
        @ApiResponse(
            responseCode = "401",
            description = UserApiConstants.ErrorMessages.INVALID_TOKEN,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = UserApiConstants.StatusDescriptions.INTERNAL_ERROR_DESC,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping(UserApiConstants.Endpoints.VALIDATE_TOKEN)
    ResponseEntity<?> validateToken(@RequestParam String token);

    /**
     * Logout user and revoke tokens.
     *
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
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = UserApiConstants.StatusDescriptions.SUCCESS
        ),
        @ApiResponse(
            responseCode = "401",
            description = UserApiConstants.ErrorMessages.UNAUTHORIZED,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = UserApiConstants.StatusDescriptions.INTERNAL_ERROR_DESC,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping(UserApiConstants.Endpoints.LOGOUT)
    @SecurityRequirement(name = "Bearer Token")
    ResponseEntity<?> logout();

    /**
     * Get current user's profile.
     *
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
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = UserApiConstants.StatusDescriptions.SUCCESS,
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = UserApiConstants.ErrorMessages.UNAUTHORIZED,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = UserApiConstants.StatusDescriptions.INTERNAL_ERROR_DESC,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping(UserApiConstants.Endpoints.GET_PROFILE)
    @SecurityRequirement(name = "Bearer Token")
    ResponseEntity<UserProfileResponse> getProfile();

    /**
     * Get user profile by ID.
     *
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
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = UserApiConstants.StatusDescriptions.SUCCESS,
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = UserApiConstants.ErrorMessages.UNAUTHORIZED,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = UserApiConstants.ErrorMessages.FORBIDDEN,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = UserApiConstants.ErrorMessages.USER_NOT_FOUND,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = UserApiConstants.StatusDescriptions.INTERNAL_ERROR_DESC,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping(UserApiConstants.Endpoints.GET_USER_BY_ID)
    @SecurityRequirement(name = "Bearer Token")
    ResponseEntity<UserProfileResponse> getUserById(@PathVariable String userId);

    /**
     * Update current user's profile.
     *
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
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = UserApiConstants.StatusDescriptions.SUCCESS,
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = UserApiConstants.StatusDescriptions.BAD_REQUEST,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = UserApiConstants.ErrorMessages.UNAUTHORIZED,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Email already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = UserApiConstants.StatusDescriptions.INTERNAL_ERROR_DESC,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping(UserApiConstants.Endpoints.UPDATE_PROFILE)
    @SecurityRequirement(name = "Bearer Token")
    ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateUserProfileRequest request);

    /**
     * Change user password.
     *
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
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = UserApiConstants.StatusDescriptions.SUCCESS
        ),
        @ApiResponse(
            responseCode = "400",
            description = UserApiConstants.StatusDescriptions.BAD_REQUEST,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid current password",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = UserApiConstants.StatusDescriptions.INTERNAL_ERROR_DESC,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping(UserApiConstants.Endpoints.CHANGE_PASSWORD)
    @SecurityRequirement(name = "Bearer Token")
    ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request);

    /**
     * Delete user account.
     *
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
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = UserApiConstants.StatusDescriptions.SUCCESS
        ),
        @ApiResponse(
            responseCode = "401",
            description = UserApiConstants.ErrorMessages.UNAUTHORIZED,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = UserApiConstants.StatusDescriptions.INTERNAL_ERROR_DESC,
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping(UserApiConstants.Endpoints.DELETE_ACCOUNT)
    @SecurityRequirement(name = "Bearer Token")
    ResponseEntity<?> deleteAccount();
}

