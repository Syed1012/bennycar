package de.bennycar.api.vehicle.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for customization option.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customization option response")
public class CustomizationOptionResponse {

    @Schema(description = "Option ID")
    private UUID id;

    @Schema(description = "Category ID")
    private UUID categoryId;

    @Schema(description = "Category name")
    private String categoryName;

    @Schema(description = "Option name", example = "Alpine White")
    private String name;

    @Schema(description = "Option description")
    private String description;

    @Schema(description = "Price adjustment for this option", example = "1500.00")
    private BigDecimal priceAdjustment;

    @Schema(description = "Image URL for this option")
    private String imageUrl;

    @Schema(description = "Hex color code", example = "#FFFFFF")
    private String colorCode;

    @Schema(description = "Display order")
    private Integer displayOrder;

    @Schema(description = "Whether the option is active")
    private Boolean active;

    @Schema(description = "Created timestamp")
    private Instant createdAt;

    @Schema(description = "Last updated timestamp")
    private Instant updatedAt;
}