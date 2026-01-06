package de.bennycar.api.user.dto.request;

import de.bennycar.api.user.constants.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

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

    @Serial
    private static final long serialVersionUID = 1L;

    @Email(message = ValidationMessages.EMAIL_INVALID)
    @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
    @Schema(
        description = "User's email address",
        example = "john.doe@example.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
    @Size(
        min = 12,
        max = 128,
        message = ValidationMessages.PASSWORD_MIN_LENGTH + " and " + ValidationMessages.PASSWORD_MAX_LENGTH
    )
    @Schema(
        description = "User's password (" + ValidationMessages.PASSWORD_MIN_LENGTH + ", " + ValidationMessages.PASSWORD_MAX_LENGTH + ")",
        example = "SecurePass@123",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;

    @NotBlank(message = ValidationMessages.FIRST_NAME_REQUIRED)
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Schema(
        description = "User's first name",
        example = "John",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String firstName;

    @NotBlank(message = ValidationMessages.LAST_NAME_REQUIRED)
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Schema(
        description = "User's last name",
        example = "Doe",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String lastName;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Pattern(
        regexp = "^\\+?[0-9.\\-\\s]{7,20}$",
        message = ValidationMessages.PHONE_FORMAT
    )
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

    @Past(message = ValidationMessages.BIRTH_DATE_PAST)
    @Schema(
        description = "Optional birth date (must be in the past)",
        example = "1990-05-14"
    )
    private LocalDate birthDate;
}