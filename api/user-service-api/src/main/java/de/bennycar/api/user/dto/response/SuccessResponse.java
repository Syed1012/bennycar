package de.bennycar.api.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Response DTO for successful operations.
 * Generic wrapper for successful API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "SuccessResponse",
    description = "Standardized success response wrapper"
)
public class SuccessResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(
        description = "HTTP status code",
        example = "200",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer status;

    @Schema(
        description = "Success message",
        example = "Operation completed successfully",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String message;

    @Schema(
        description = "Response data payload"
    )
    private T data;

    @Schema(
        description = "Timestamp of response",
        example = "2025-12-23T10:30:00"
    )
    private LocalDateTime timestamp;

    /**
     * Factory method to create a success response with status 200
     */
    public static <T> SuccessResponse<T> ok(T data, String message) {
        return SuccessResponse.<T>builder()
            .status(200)
            .message(message)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }

    /**
     * Factory method to create a success response with status 201 (Created)
     */
    public static <T> SuccessResponse<T> created(T data, String message) {
        return SuccessResponse.<T>builder()
            .status(201)
            .message(message)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }

    /**
     * Factory method to create a success response with custom status
     */
    public static <T> SuccessResponse<T> ofStatus(Integer status, String message, T data) {
        return SuccessResponse.<T>builder()
            .status(status)
            .message(message)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }
}

