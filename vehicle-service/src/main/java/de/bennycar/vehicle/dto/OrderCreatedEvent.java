package de.bennycar.vehicle.dto;

import lombok.Data;

@Data
public class OrderCreatedEvent {
    private Long vehicleId;
    private Long userId;
    private String orderDate;
}

