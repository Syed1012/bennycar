package de.bennycar.order.infrastructure.adapters.in.web.dto;

import de.bennycar.order.domain.model.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private UUID id;
    private UUID userId;
    private UUID vehicleId;
    private String street;
    private String city;
    private String zipCode;
    private String country;
    private OrderStatus status;
    private LocalDateTime createdAt;
}

