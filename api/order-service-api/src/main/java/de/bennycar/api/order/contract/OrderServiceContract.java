package de.bennycar.api.order.contract;

import de.bennycar.api.order.dto.request.CreateOrderRequest;
import de.bennycar.api.order.dto.response.ErrorResponse;
import de.bennycar.api.order.dto.response.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Order Management", description = "APIs for managing vehicle orders")
@RequestMapping("/api/v1/orders")
public interface OrderServiceContract {

    @Operation(summary = "Create a new order", description = "Places an order for a vehicle to be delivered to a specific address")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Vehicle not available",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request);

    @Operation(summary = "Get order by ID", description = "Retrieves details of a specific order")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{orderId}")
    ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId);

    @Operation(summary = "Get orders by User ID", description = "Retrieves all orders for a specific user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    })
    @GetMapping("/user/{userId}")
    ResponseEntity<List<OrderResponse>> getOrdersByUser(@PathVariable UUID userId);
}

