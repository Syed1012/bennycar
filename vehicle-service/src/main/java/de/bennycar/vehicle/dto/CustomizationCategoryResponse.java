package de.bennycar.vehicle.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for customization category.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customization category response")
public class CustomizationCategoryResponse {

    @Schema(description = "Category ID")
    private UUID id;

    @Schema(description = "Category name", example = "Exterior Color")
    private String name;

    @Schema(description = "Category description")
    private String description;

    @Schema(description = "Display order")
    private Integer displayOrder;

    @Schema(description = "Whether multiple options can be selected")
    private Boolean allowsMultiple;

    @Schema(description = "Whether the category is active")
    private Boolean active;

    @Schema(description = "Number of options in this category")
    private Integer optionCount;

    @Schema(description = "Created timestamp")
    private Instant createdAt;

    @Schema(description = "Last updated timestamp")
    private Instant updatedAt;
}

