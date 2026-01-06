package de.bennycar.order.domain.ports.in;

import de.bennycar.order.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GetOrderUseCase {
    Optional<Order> getOrder(UUID orderId);
    List<Order> getOrdersByUserId(UUID userId);
}

