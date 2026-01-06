package de.bennycar.order.domain.ports.in;

import de.bennycar.order.domain.model.Order;

public interface CreateOrderUseCase {
    Order createOrder(CreateOrderCommand command);
}

