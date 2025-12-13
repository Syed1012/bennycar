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
 * Request DTO for updating a vehicle.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Update vehicle request")
public class UpdateVehicleRequest {

    @Schema(description = "Brand ID")
    private UUID brandId;

    @Schema(description = "Vehicle type ID")
    private UUID vehicleTypeId;

    @Size(max = 100, message = "Model name must not exceed 100 characters")
    @Schema(description = "Vehicle model name", example = "X5 M")
    private String model;

    @Min(value = 1900, message = "Model year must be at least 1900")
    @Max(value = 2100, message = "Model year must not exceed 2100")
    @Schema(description = "Model year", example = "2024")
    private Integer modelYear;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Vehicle description")
    private String description;

    @DecimalMin(value = "0.01", message = "Base price must be greater than 0")
    @Schema(description = "Base price in EUR", example = "85000.00")
    private BigDecimal basePrice;

    @Size(max = 50, message = "Engine must not exceed 50 characters")
    @Schema(description = "Engine specification")
    private String engine;

    @Size(max = 50, message = "Transmission must not exceed 50 characters")
    @Schema(description = "Transmission type")
    private String transmission;

    @Size(max = 30, message = "Fuel type must not exceed 30 characters")
    @Schema(description = "Fuel type")
    private String fuelType;

    @Size(max = 20, message = "Horsepower must not exceed 20 characters")
    @Schema(description = "Horsepower")
    private String horsepower;

    @Min(value = 1, message = "Seating capacity must be at least 1")
    @Max(value = 20, message = "Seating capacity must not exceed 20")
    @Schema(description = "Seating capacity")
    private Integer seatingCapacity;

    @Min(value = 0, message = "Cargo capacity must be at least 0")
    @Schema(description = "Cargo capacity in liters")
    private Integer cargoCapacityLiters;

    @Size(max = 30, message = "Fuel efficiency must not exceed 30 characters")
    @Schema(description = "Fuel efficiency")
    private String fuelEfficiency;

    @Size(max = 512, message = "Main image URL must not exceed 512 characters")
    @Schema(description = "Main vehicle image URL")
    private String mainImageUrl;

    @Schema(description = "Additional image URLs")
    private Set<String> additionalImages;

    @Schema(description = "Vehicle status", example = "AVAILABLE")
    private String status;

    @Min(value = 0, message = "Stock quantity must be at least 0")
    @Schema(description = "Stock quantity")
    private Integer stockQuantity;

    @Schema(description = "IDs of available customization options for this vehicle")
    private Set<UUID> customizationOptionIds;
}

