package de.bennycar.api.user.dto.request;

import de.bennycar.api.user.constants.UserApiConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Request DTO for user login.
 * Validates email and password credentials.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "LoginRequest",
    description = "Request payload for user login"
)
public class LoginRequest implements Serializable {

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
    @Schema(
        description = "User's password",
        example = "SecurePass@123",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
}

