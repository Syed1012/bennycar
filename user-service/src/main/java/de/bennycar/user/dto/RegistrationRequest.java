package de.bennycar.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for user registration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User registration request")
public class RegistrationRequest {

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    @Schema(description = "User's email address", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 12, max = 128, message = "Password must be between 12 and 128 characters")
    @Schema(description = "User's password (minimum 12 characters)", example = "SecurePassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Schema(description = "User's first name", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Schema(description = "User's last name", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Schema(description = "Optional phone number", example = "+1-555-555-5555")
    private String phoneNumber;

    @Size(max = 512, message = "Profile picture URL must not exceed 512 characters")
    @Schema(description = "Optional profile picture URL", example = "https://cdn.example.com/avatar.png")
    private String profilePictureUrl;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Schema(description = "Optional mailing address", example = "221B Baker Street, London")
    private String address;
}