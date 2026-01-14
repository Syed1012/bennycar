package de.bennycar.api.vehicle.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * Request DTO for updating a vehicle configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Update vehicle configuration request")
public class UpdateConfigurationRequest {

    @Size(max = 100, message = "Configuration name must not exceed 100 characters")
    @Schema(description = "Configuration name", example = "My Dream Car Updated")
    private String name;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    @Schema(description = "Notes about this configuration")
    private String notes;

    @Schema(description = "IDs of selected customization options")
    private Set<UUID> selectedOptionIds;

    @Schema(description = "Configuration status", example = "SAVED")
    private String status;
}