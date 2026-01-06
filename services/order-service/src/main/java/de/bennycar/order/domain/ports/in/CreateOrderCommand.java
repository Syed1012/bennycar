package de.bennycar.order.domain.ports.in;

import de.bennycar.order.domain.model.Address;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateOrderCommand {
    private UUID userId;
    private UUID vehicleId;
    private Address deliveryAddress;
}

