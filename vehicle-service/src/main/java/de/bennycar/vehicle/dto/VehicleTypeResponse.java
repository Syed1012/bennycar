package de.bennycar.vehicle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for vehicle type information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Vehicle type response")
public class VehicleTypeResponse {

    @Schema(description = "Vehicle type ID")
    private UUID id;

    @Schema(description = "Vehicle type name", example = "SUV")
    private String name;

    @Schema(description = "Vehicle type description")
    private String description;

    @Schema(description = "URL to type icon")
    private String iconUrl;

    @Schema(description = "Number of vehicles of this type")
    private Integer vehicleCount;

    @Schema(description = "Created timestamp")
    private Instant createdAt;

    @Schema(description = "Last updated timestamp")
    private Instant updatedAt;
}

