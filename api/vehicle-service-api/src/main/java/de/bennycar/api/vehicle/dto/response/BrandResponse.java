package de.bennycar.api.vehicle.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for brand information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Brand response")
public class BrandResponse {

    @Schema(description = "Brand ID")
    private UUID id;

    @Schema(description = "Brand name", example = "BMW")
    private String name;

    @Schema(description = "Brand description")
    private String description;

    @Schema(description = "URL to brand logo")
    private String logoUrl;

    @Schema(description = "Country of origin", example = "Germany")
    private String countryOfOrigin;

    @Schema(description = "Year founded", example = "1916")
    private Integer foundedYear;

    @Schema(description = "Whether the brand is active")
    private Boolean active;

    @Schema(description = "Number of vehicles from this brand")
    private Integer vehicleCount;

    @Schema(description = "Created timestamp")
    private Instant createdAt;

    @Schema(description = "Last updated timestamp")
    private Instant updatedAt;
}