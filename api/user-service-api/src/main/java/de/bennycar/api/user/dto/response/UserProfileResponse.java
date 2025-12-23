package de.bennycar.api.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for user profile information.
 * Contains complete user profile data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "UserProfileResponse",
    description = "Complete user profile information"
)
public class UserProfileResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(
        description = "Unique user identifier",
        example = "550e8400-e29b-41d4-a716-446655440000",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID userId;

    @Schema(
        description = "User's email address",
        example = "john.doe@example.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @Schema(
        description = "User's first name",
        example = "John",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String firstName;

    @Schema(
        description = "User's last name",
        example = "Doe",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String lastName;

    @Schema(
        description = "User's phone number",
        example = "+1-555-555-5555"
    )
    private String phoneNumber;

    @Schema(
        description = "Profile picture URL",
        example = "https://cdn.example.com/avatar.png"
    )
    private String profilePictureUrl;

    @Schema(
        description = "Mailing address",
        example = "221B Baker Street, London"
    )
    private String address;

    @Schema(
        description = "Account creation timestamp",
        example = "2025-01-01T10:30:00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime createdAt;

    @Schema(
        description = "Last account update timestamp",
        example = "2025-12-23T15:45:30",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime updatedAt;

    @Schema(
        description = "Account status (ACTIVE, INACTIVE, SUSPENDED)",
        example = "ACTIVE",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String status;

    @Schema(
        description = "Email verification status",
        example = "true",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean emailVerified;
}

