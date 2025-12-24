package de.bennycar.api.vehicle.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for creating a customization option.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create customization option request")
public class CreateCustomizationOptionRequest {

    @NotNull(message = "Category ID is required")
    @Schema(description = "Category ID this option belongs to", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID categoryId;

    @NotBlank(message = "Option name is required")
    @Size(max = 100, message = "Option name must not exceed 100 characters")
    @Schema(description = "Option name", example = "Alpine White", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Option description", example = "Classic pure white finish")
    private String description;

    @DecimalMin(value = "0.00", message = "Price adjustment must be at least 0")
    @Schema(description = "Price adjustment for this option", example = "1500.00")
    private BigDecimal priceAdjustment;

    @Size(max = 512, message = "Image URL must not exceed 512 characters")
    @Schema(description = "Image URL for this option")
    private String imageUrl;

    @Size(max = 7, message = "Color code must not exceed 7 characters")
    @Schema(description = "Hex color code (for color options)", example = "#FFFFFF")
    private String colorCode;

    @Schema(description = "Display order for sorting", example = "1")
    private Integer displayOrder;
}

