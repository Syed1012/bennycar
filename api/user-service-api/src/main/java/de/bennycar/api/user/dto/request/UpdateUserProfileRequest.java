package de.bennycar.api.user.dto.request;

import de.bennycar.api.user.constants.ValidationMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
 * Request DTO for updating user profile.
 * All fields are optional to allow partial updates.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "UpdateUserProfileRequest",
    description = "Request payload for updating user profile information"
)
public class UpdateUserProfileRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Schema(
        description = "User's first name",
        example = "John"
    )
    private String firstName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Schema(
        description = "User's last name",
        example = "Doe"
    )
    private String lastName;

    @Email(message = ValidationMessages.EMAIL_INVALID)
    @Schema(
        description = "User's email address",
        example = "john.doe.new@example.com"
    )
    private String email;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Pattern(
        regexp = "^\\+?[0-9.\\-\\s]{7,20}$",
        message = ValidationMessages.PHONE_FORMAT
    )
    @Schema(
        description = "Phone number in international format",
        example = "+1-555-555-5555"
    )
    private String phoneNumber;

    @Size(max = 512, message = "Profile picture URL must not exceed 512 characters")
    @Schema(
        description = "Profile picture URL",
        example = "https://cdn.example.com/avatar.png"
    )
    private String profilePictureUrl;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Schema(
        description = "Mailing address",
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