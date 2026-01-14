package de.bennycar.api.vehicle.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request parameters for searching vehicles.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Vehicle search parameters")
public class VehicleSearchParams {

    @Schema(description = "Filter by brand ID")
    private UUID brandId;

    @Schema(description = "Filter by vehicle type ID")
    private UUID vehicleTypeId;

    @Schema(description = "Filter by model year")
    private Integer modelYear;

    @Schema(description = "Minimum price filter")
    private BigDecimal minPrice;

    @Schema(description = "Maximum price filter")
    private BigDecimal maxPrice;

    @Schema(description = "Filter by status (AVAILABLE, RESERVED, SOLD)")
    private String status;

    @Schema(description = "Search query for model name")
    private String query;

    @Schema(description = "Search text for brand, model, or type")
    private String search;
}