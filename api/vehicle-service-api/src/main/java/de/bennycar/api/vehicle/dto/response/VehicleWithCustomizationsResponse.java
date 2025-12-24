package de.bennycar.api.vehicle.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Vehicle with full customization details")
public class VehicleWithCustomizationsResponse {

    @Schema(description = "Vehicle details")
    private VehicleResponse vehicle;

    @Schema(description = "Available customization categories with options")
    private List<CustomizationCategoryWithOptions> customizationCategories;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomizationCategoryWithOptions {
        private UUID categoryId;
        private String categoryName;
        private String categoryDescription;
        private Boolean allowsMultiple;
        private Integer displayOrder;
        private List<CustomizationOptionSummary> options;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomizationOptionSummary {
        private UUID id;
        private String name;
        private String description;
        private BigDecimal priceAdjustment;
        private String imageUrl;
        private String colorCode;
        private Integer displayOrder;
    }
}

