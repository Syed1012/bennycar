package com.bennycar.orderservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private Long vehicleId;

    @Column(nullable = false)
    private String vehicleName;

    @Column(nullable = false)
    private String vehicleModel;

    @Column(columnDefinition = "TEXT")
    private String customizations;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Embedded
    private ShippingAddress shippingAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime estimatedDelivery;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(unique = true)
    private String orderNumber;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = OrderStatus.PENDING;
        }
        if (orderNumber == null) {
            orderNumber = generateOrderNumber();
        }
        if (estimatedDelivery == null) {
            estimatedDelivery = LocalDateTime.now().plusWeeks(2);
        }
    }

    @NotNull
    private String generateOrderNumber() {
        int suffix = new Random().nextInt(1000);
        return "ORD-" + System.currentTimeMillis() + "-" + suffix;
    }
}