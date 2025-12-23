package de.bennycar.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Request DTO for token refresh.
 * Contains the refresh token needed to obtain a new access token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "RefreshTokenRequest",
    description = "Request payload for token refresh"
)
public class RefreshTokenRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Refresh token is required")
    @Schema(
        description = "The refresh token obtained from login/registration",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String refreshToken;
}

