package de.bennycar.vehicle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Standardized error response DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response")
public class ErrorResponse {

    @Schema(description = "Request path", example = "/api/v1/vehicles/123")
    private String path;

    @Schema(description = "Error code", example = "VEHICLE_NOT_FOUND")
    private String code;

    @Schema(description = "Error message", example = "Vehicle not found with id: 123")
    private String message;

    @Schema(description = "Additional error details")
    private List<String> details;

    @Schema(description = "Timestamp of the error")
    @Builder.Default
    private Instant timestamp = Instant.now();
}
