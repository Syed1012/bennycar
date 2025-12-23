package de.bennycar.api.user.dto.request;

import de.bennycar.api.user.constants.UserApiConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Request DTO for user registration.
 * Validates user input during account creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "RegisterUserRequest",
    description = "Request payload for user registration"
)
public class RegisterUserRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Email(message = UserApiConstants.ValidationMessages.EMAIL_INVALID)
    @NotBlank(message = UserApiConstants.ValidationMessages.EMAIL_REQUIRED)
    @Schema(
        description = "User's email address",
        example = "john.doe@example.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @NotBlank(message = UserApiConstants.ValidationMessages.PASSWORD_REQUIRED)
    @Size(
        min = 12,
        max = 128,
        message = "Password must be between 12 and 128 characters"
    )
    @Schema(
        description = "User's password (minimum 12 characters, must include uppercase, lowercase, number, and special character)",
        example = "SecurePass@123",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;

    @NotBlank(message = UserApiConstants.ValidationMessages.FIRST_NAME_REQUIRED)
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Schema(
        description = "User's first name",
        example = "John",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String firstName;

    @NotBlank(message = UserApiConstants.ValidationMessages.LAST_NAME_REQUIRED)
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Schema(
        description = "User's last name",
        example = "Doe",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String lastName;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Schema(
        description = "Optional phone number in international format",
        example = "+1-555-555-5555"
    )
    private String phoneNumber;

    @Size(max = 512, message = "Profile picture URL must not exceed 512 characters")
    @Schema(
        description = "Optional profile picture URL",
        example = "https://cdn.example.com/avatar.png"
    )
    private String profilePictureUrl;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Schema(
        description = "Optional mailing address",
        example = "221B Baker Street, London"
    )
    private String address;
}

