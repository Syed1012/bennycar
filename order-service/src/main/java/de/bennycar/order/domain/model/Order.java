package de.bennycar.order.domain.model;

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
public class Order {
    private UUID id;
    private UUID userId;
    private UUID vehicleId;
    private Address deliveryAddress;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void confirm() {
        if (this.status == OrderStatus.PENDING) {
            this.status = OrderStatus.CONFIRMED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void ship() {
        if (this.status == OrderStatus.CONFIRMED) {
            this.status = OrderStatus.SHIPPED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void deliver() {
        if (this.status == OrderStatus.SHIPPED) {
            this.status = OrderStatus.DELIVERED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void cancel() {
        if (this.status != OrderStatus.DELIVERED && this.status != OrderStatus.SHIPPED) {
            this.status = OrderStatus.CANCELLED;
            this.updatedAt = LocalDateTime.now();
        }
    }
}

