package de.bennycar.api.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order response")
public class OrderResponse {
    @Schema(description = "Order ID")
    private UUID id;

    @Schema(description = "User ID")
    private UUID userId;

    @Schema(description = "Vehicle ID")
    private UUID vehicleId;

    @Schema(description = "Delivery street address")
    private String street;

    @Schema(description = "Delivery city")
    private String city;

    @Schema(description = "Delivery zip code")
    private String zipCode;

    @Schema(description = "Delivery country")
    private String country;

    @Schema(description = "Order status")
    private String status;

    @Schema(description = "Order creation timestamp")
    private LocalDateTime createdAt;
}

