package de.bennycar.order.application.service;

import de.bennycar.order.domain.event.OrderCreatedEvent;
import de.bennycar.order.domain.exception.VehicleNotAvailableException;
import de.bennycar.order.domain.model.Order;
import de.bennycar.order.domain.model.OrderStatus;
import de.bennycar.order.domain.ports.in.CreateOrderCommand;
import de.bennycar.order.domain.ports.in.CreateOrderUseCase;
import de.bennycar.order.domain.ports.in.GetOrderUseCase;
import de.bennycar.order.domain.ports.out.OrderRepositoryPort;
import de.bennycar.order.domain.ports.out.VehicleServicePort;
import de.bennycar.order.infrastructure.adapters.out.messaging.RabbitMQPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService implements CreateOrderUseCase, GetOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final VehicleServicePort vehicleService;
    private final RabbitMQPublisher rabbitMQPublisher;

    @Override
    @Transactional
    public Order createOrder(CreateOrderCommand command) {
        log.info("Creating order for vehicle {} by user {}", command.getVehicleId(), command.getUserId());

        if (!vehicleService.isVehicleAvailable(command.getVehicleId())) {
            throw new VehicleNotAvailableException("Vehicle " + command.getVehicleId() + " is not available for order");
        }

        Order order = Order.builder()
                .id(UUID.randomUUID())
                .userId(command.getUserId())
                .vehicleId(command.getVehicleId())
                .deliveryAddress(command.getDeliveryAddress())
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        // In a real distributed transaction scenario (Saga), we would publish an event here.
        // For simplicity, we are calling the service directly, but this should be idempotent.
        vehicleService.markVehicleAsOrdered(command.getVehicleId());

        // Publish event for other services (e.g. analytics, notification)
        rabbitMQPublisher.publishOrderCreatedEvent(OrderCreatedEvent.builder()
                .vehicleId(command.getVehicleId())
                .userId(command.getUserId())
                .orderDate(savedOrder.getCreatedAt().toString())
                .build());

        // Confirm the order after marking vehicle
        savedOrder.confirm();
        return orderRepository.save(savedOrder);
    }

    @Override
    public Optional<Order> getOrder(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<Order> getOrdersByUserId(UUID userId) {
        return orderRepository.findByUserId(userId);
    }
}
