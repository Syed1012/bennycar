package de.bennycar.api.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * Response DTO for authentication tokens.
 * Contains access token, refresh token, and token metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "TokenResponse",
    description = "Response containing authentication tokens and metadata"
)
public class TokenResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(
        description = "JWT access token for API authentication",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String accessToken;

    @Schema(
        description = "Refresh token for obtaining new access tokens",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String refreshToken;

    @Schema(
        description = "Type of token (Bearer)",
        example = "Bearer",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tokenType;

    @Schema(
        description = "Access token expiration time in seconds",
        example = "600",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long expiresIn;

    @Schema(
        description = "Access token expiration timestamp",
        example = "2025-12-23T10:45:30Z"
    )
    private Instant expiresAt;

    @Schema(
        description = "Refresh token expiration timestamp",
        example = "2025-12-30T10:30:00Z"
    )
    private Instant refreshExpiresAt;
}

