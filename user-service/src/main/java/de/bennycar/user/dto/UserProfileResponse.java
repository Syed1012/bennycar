package de.bennycar.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * Response DTO for user profile information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User profile information")
public class UserProfileResponse {

    @Schema(description = "User's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "User's roles", example = "[\"ADMIN\", \"USER\"]")
    private Set<String> roles;

    @Schema(description = "Optional phone number", example = "+1-555-555-5555")
    private String phoneNumber;

    @Schema(description = "Profile picture URL", example = "https://cdn.example.com/avatar.png")
    private String profilePictureUrl;

    @Schema(description = "Mailing address", example = "221B Baker Street, London")
    private String address;
}
