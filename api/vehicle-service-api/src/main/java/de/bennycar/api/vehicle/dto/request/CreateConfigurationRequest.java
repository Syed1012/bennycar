package de.bennycar.api.vehicle.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * Request DTO for creating a vehicle configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create vehicle configuration request")
public class CreateConfigurationRequest {

    @NotNull(message = "Vehicle ID is required")
    @Schema(description = "Vehicle ID to configure", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID vehicleId;

    @Size(max = 100, message = "Configuration name must not exceed 100 characters")
    @Schema(description = "Optional name for this configuration", example = "My Dream Car")
    private String name;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    @Schema(description = "Optional notes about this configuration")
    private String notes;

    @Schema(description = "IDs of selected customization options")
    private Set<UUID> selectedOptionIds;
}

