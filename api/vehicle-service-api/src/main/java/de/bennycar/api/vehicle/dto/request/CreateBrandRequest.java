package de.bennycar.api.vehicle.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new brand.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create brand request")
public class CreateBrandRequest {

    @NotBlank(message = "Brand name is required")
    @Size(max = 100, message = "Brand name must not exceed 100 characters")
    @Schema(description = "Brand name", example = "BMW", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Brand description", example = "Bavarian Motor Works - German luxury automobile manufacturer")
    private String description;

    @Size(max = 512, message = "Logo URL must not exceed 512 characters")
    @Schema(description = "URL to brand logo", example = "https://cdn.example.com/logos/bmw.png")
    private String logoUrl;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    @Schema(description = "Country of origin", example = "Germany")
    private String countryOfOrigin;

    @Schema(description = "Year the brand was founded", example = "1916")
    private Integer foundedYear;
}