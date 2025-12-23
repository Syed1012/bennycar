package de.bennycar.api.user.dto.request;

import de.bennycar.api.user.constants.UserApiConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Request DTO for password change.
 * Validates current and new password fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "ChangePasswordRequest",
    description = "Request payload for changing user password"
)
public class ChangePasswordRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Current password is required")
    @Schema(
        description = "The user's current password",
        example = "OldPass@123",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String currentPassword;

    @NotBlank(message = UserApiConstants.ValidationMessages.PASSWORD_REQUIRED)
    @Size(
        min = 12,
        max = 128,
        message = "New password must be between 12 and 128 characters"
    )
    @Schema(
        description = "The new password (must be different from current password)",
        example = "NewPass@456",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String newPassword;

    @NotBlank(message = "Password confirmation is required")
    @Schema(
        description = "Confirmation of the new password",
        example = "NewPass@456",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String confirmPassword;
}

