package de.bennycar.api.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create order request")
public class CreateOrderRequest {
    @NotNull(message = "User ID is required")
    @Schema(description = "User ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID userId;

    @NotNull(message = "Vehicle ID is required")
    @Schema(description = "Vehicle ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID vehicleId;

    @NotBlank(message = "Street is required")
    @Schema(description = "Delivery street address", requiredMode = Schema.RequiredMode.REQUIRED)
    private String street;

    @NotBlank(message = "City is required")
    @Schema(description = "Delivery city", requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    @NotBlank(message = "Zip code is required")
    @Schema(description = "Delivery zip code", requiredMode = Schema.RequiredMode.REQUIRED)
    private String zipCode;

    @NotBlank(message = "Country is required")
    @Schema(description = "Delivery country", requiredMode = Schema.RequiredMode.REQUIRED)
    private String country;
}

