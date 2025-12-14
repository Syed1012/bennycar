package de.bennycar.vehicle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * Request DTO for creating a new vehicle.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create vehicle request")
public class CreateVehicleRequest {

    @NotNull(message = "Brand ID is required")
    @Schema(description = "Brand ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID brandId;

    @NotNull(message = "Vehicle type ID is required")
    @Schema(description = "Vehicle type ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID vehicleTypeId;

    @NotBlank(message = "Model name is required")
    @Size(max = 100, message = "Model name must not exceed 100 characters")
    @Schema(description = "Vehicle model name", example = "X5", requiredMode = Schema.RequiredMode.REQUIRED)
    private String model;

    @NotNull(message = "Model year is required")
    @Min(value = 1900, message = "Model year must be at least 1900")
    @Max(value = 2100, message = "Model year must not exceed 2100")
    @Schema(description = "Model year", example = "2024", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer modelYear;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Vehicle description")
    private String description;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.01", message = "Base price must be greater than 0")
    @Schema(description = "Base price in EUR", example = "75000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal basePrice;

    @Size(max = 50, message = "Engine must not exceed 50 characters")
    @Schema(description = "Engine specification", example = "3.0L TwinPower Turbo Inline-6")
    private String engine;

    @Size(max = 50, message = "Transmission must not exceed 50 characters")
    @Schema(description = "Transmission type", example = "8-speed Automatic")
    private String transmission;

    @Size(max = 30, message = "Fuel type must not exceed 30 characters")
    @Schema(description = "Fuel type", example = "Petrol")
    private String fuelType;

    @Size(max = 20, message = "Horsepower must not exceed 20 characters")
    @Schema(description = "Horsepower", example = "335 hp")
    private String horsepower;

    @Min(value = 1, message = "Seating capacity must be at least 1")
    @Max(value = 20, message = "Seating capacity must not exceed 20")
    @Schema(description = "Seating capacity", example = "5")
    private Integer seatingCapacity;

    @Min(value = 0, message = "Cargo capacity must be at least 0")
    @Schema(description = "Cargo capacity in liters", example = "650")
    private Integer cargoCapacityLiters;

    @Size(max = 30, message = "Fuel efficiency must not exceed 30 characters")
    @Schema(description = "Fuel efficiency", example = "8.5 L/100km")
    private String fuelEfficiency;

    @Size(max = 512, message = "Main image URL must not exceed 512 characters")
    @Schema(description = "Main vehicle image URL")
    private String mainImageUrl;

    @Schema(description = "Additional image URLs")
    private Set<String> additionalImages;

    @Min(value = 0, message = "Stock quantity must be at least 0")
    @Schema(description = "Initial stock quantity", example = "10")
    private Integer stockQuantity;

    @Schema(description = "IDs of available customization options for this vehicle")
    private Set<UUID> customizationOptionIds;
}
