package de.bennycar.api.vehicle.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Response DTO for vehicle information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Vehicle response")
public class VehicleResponse {

    @Schema(description = "Vehicle ID")
    private UUID id;

    @Schema(description = "Brand information")
    private BrandSummary brand;

    @Schema(description = "Vehicle type information")
    private VehicleTypeSummary vehicleType;

    @Schema(description = "Model name", example = "X5")
    private String model;

    @Schema(description = "Model year", example = "2024")
    private Integer modelYear;

    @Schema(description = "Vehicle description")
    private String description;

    @Schema(description = "Base price in EUR", example = "75000.00")
    private BigDecimal basePrice;

    @Schema(description = "Engine specification")
    private String engine;

    @Schema(description = "Transmission type")
    private String transmission;

    @Schema(description = "Fuel type")
    private String fuelType;

    @Schema(description = "Horsepower")
    private String horsepower;

    @Schema(description = "Seating capacity")
    private Integer seatingCapacity;

    @Schema(description = "Cargo capacity in liters")
    private Integer cargoCapacityLiters;

    @Schema(description = "Fuel efficiency")
    private String fuelEfficiency;

    @Schema(description = "Main vehicle image URL")
    private String mainImageUrl;

    @Schema(description = "Additional image URLs")
    private Set<String> additionalImages;

    @Schema(description = "Vehicle status", example = "AVAILABLE")
    private String status;

    @Schema(description = "Stock quantity")
    private Integer stockQuantity;

    @Schema(description = "Whether the vehicle is available for purchase")
    private Boolean available;

    @Schema(description = "Created timestamp")
    private Instant createdAt;

    @Schema(description = "Last updated timestamp")
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandSummary {
        private UUID id;
        private String name;
        private String logoUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleTypeSummary {
        private UUID id;
        private String name;
        private String iconUrl;
    }
}

