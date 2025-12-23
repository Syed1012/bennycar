package de.bennycar.api.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Standardized error response DTO.
 * Used for all error responses across the API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "ErrorResponse",
    description = "Standardized error response"
)
public class ErrorResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(
        description = "HTTP status code",
        example = "400",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer status;

    @Schema(
        description = "Error code for programmatic handling",
        example = "INVALID_REQUEST",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String code;

    @Schema(
        description = "Human-readable error message",
        example = "Email must be valid",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String message;

    @Schema(
        description = "Detailed error description",
        example = "The provided email address format is invalid"
    )
    private String detail;

    @Schema(
        description = "Timestamp when error occurred",
        example = "2025-12-23T10:30:00"
    )
    private LocalDateTime timestamp;

    @Schema(
        description = "Request path that caused the error",
        example = "/api/v1/auth/register"
    )
    private String path;

    @Schema(
        description = "Field validation errors (for validation errors only)"
    )
    private java.util.Map<String, String> fieldErrors;
}

