package de.bennycar.vehicle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for vehicle configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Vehicle configuration response")
public class ConfigurationResponse {

    @Schema(description = "Configuration ID")
    private UUID id;

    @Schema(description = "User ID who owns this configuration")
    private UUID userId;

    @Schema(description = "Vehicle summary")
    private VehicleSummary vehicle;

    @Schema(description = "Configuration name")
    private String name;

    @Schema(description = "Configuration notes")
    private String notes;

    @Schema(description = "Configuration status", example = "DRAFT")
    private String status;

    @Schema(description = "Selected customization options")
    private List<SelectedOption> selectedOptions;

    @Schema(description = "Base vehicle price")
    private BigDecimal basePrice;

    @Schema(description = "Total customization cost")
    private BigDecimal customizationTotal;

    @Schema(description = "Total price including customizations")
    private BigDecimal totalPrice;

    @Schema(description = "Order timestamp (if ordered)")
    private Instant orderedAt;

    @Schema(description = "Created timestamp")
    private Instant createdAt;

    @Schema(description = "Last updated timestamp")
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleSummary {
        private UUID id;
        private String brandName;
        private String brandLogoUrl;
        private String model;
        private Integer modelYear;
        private String vehicleTypeName;
        private BigDecimal basePrice;
        private String mainImageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectedOption {
        private UUID id;
        private String categoryName;
        private String optionName;
        private BigDecimal priceAdjustment;
        private String imageUrl;
        private String colorCode;
    }
}

