package de.bennycar.order.infrastructure.adapters.out.persistence;

import de.bennycar.order.domain.model.Address;
import de.bennycar.order.domain.model.Order;
import de.bennycar.order.domain.ports.out.OrderRepositoryPort;
import de.bennycar.order.infrastructure.adapters.out.persistence.entity.OrderJpaEntity;
import de.bennycar.order.infrastructure.adapters.out.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = toJpaEntity(order);
        OrderJpaEntity savedEntity = Objects.requireNonNull(
                orderJpaRepository.save(entity),
                "Failed to save order entity"
        );
        return toDomainModel(savedEntity);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        Objects.requireNonNull(id, "Order ID cannot be null");
        return orderJpaRepository.findById(id).map(this::toDomainModel);
    }

    @Override
    public List<Order> findByUserId(UUID userId) {
        return orderJpaRepository.findByUserId(userId).stream()
                .map(this::toDomainModel)
                .collect(Collectors.toList());
    }

    private OrderJpaEntity toJpaEntity(Order order) {
        return OrderJpaEntity.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .vehicleId(order.getVehicleId())
                .street(order.getDeliveryAddress().getStreet())
                .city(order.getDeliveryAddress().getCity())
                .zipCode(order.getDeliveryAddress().getZipCode())
                .country(order.getDeliveryAddress().getCountry())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private Order toDomainModel(OrderJpaEntity entity) {
        return Order.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .vehicleId(entity.getVehicleId())
                .deliveryAddress(Address.builder()
                        .street(entity.getStreet())
                        .city(entity.getCity())
                        .zipCode(entity.getZipCode())
                        .country(entity.getCountry())
                        .build())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

