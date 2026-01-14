package de.bennycar.api.vehicle.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new vehicle type.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create vehicle type request")
public class CreateVehicleTypeRequest {

    @NotBlank(message = "Vehicle type name is required")
    @Size(max = 50, message = "Vehicle type name must not exceed 50 characters")
    @Schema(description = "Vehicle type name", example = "SUV", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Vehicle type description", example = "Sport Utility Vehicle - versatile vehicles for various terrains")
    private String description;

    @Size(max = 512, message = "Icon URL must not exceed 512 characters")
    @Schema(description = "URL to type icon", example = "https://cdn.example.com/icons/suv.svg")
    private String iconUrl;
}