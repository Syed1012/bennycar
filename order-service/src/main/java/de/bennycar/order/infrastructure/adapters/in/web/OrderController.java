package de.bennycar.order.infrastructure.adapters.in.web;

import de.bennycar.api.order.contract.OrderServiceContract;
import de.bennycar.api.order.dto.request.CreateOrderRequest;
import de.bennycar.api.order.dto.response.OrderResponse;
import de.bennycar.order.domain.model.Address;
import de.bennycar.order.domain.model.Order;
import de.bennycar.order.domain.ports.in.CreateOrderCommand;
import de.bennycar.order.domain.ports.in.CreateOrderUseCase;
import de.bennycar.order.domain.ports.in.GetOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "APIs for managing vehicle orders")
public class OrderController implements OrderServiceContract {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;

    @PostMapping
    @Operation(summary = "Create a new order", description = "Places an order for a vehicle to be delivered to a specific address")
    @Override
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        CreateOrderCommand command = CreateOrderCommand.builder()
                .userId(request.getUserId())
                .vehicleId(request.getVehicleId())
                .deliveryAddress(Address.builder()
                        .street(request.getStreet())
                        .city(request.getCity())
                        .zipCode(request.getZipCode())
                        .country(request.getCountry())
                        .build())
                .build();

        Order order = createOrderUseCase.createOrder(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(order));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieves details of a specific order")
    @Override
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId) {
        return getOrderUseCase.getOrder(orderId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get orders by User ID", description = "Retrieves all orders for a specific user")
    @Override
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(@PathVariable UUID userId) {
        List<Order> orders = getOrderUseCase.getOrdersByUserId(userId);
        List<OrderResponse> response = orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .vehicleId(order.getVehicleId())
                .street(order.getDeliveryAddress().getStreet())
                .city(order.getDeliveryAddress().getCity())
                .zipCode(order.getDeliveryAddress().getZipCode())
                .country(order.getDeliveryAddress().getCountry())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
