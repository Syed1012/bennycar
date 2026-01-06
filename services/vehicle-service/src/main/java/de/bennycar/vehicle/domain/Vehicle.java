package de.bennycar.vehicle.domain;

import de.bennycar.vehicle.constants.AppConstants;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Core entity representing a vehicle in the catalog.
 * Contains all vehicle details including pricing, specifications, and available customizations.
 */
@Entity
@Table(name = "vehicles", schema = "vehicle_service",
        indexes = {
                @Index(name = "idx_vehicle_brand", columnList = "brand_id"),
                @Index(name = "idx_vehicle_type", columnList = "vehicle_type_id"),
                @Index(name = "idx_vehicle_status", columnList = "status"),
                @Index(name = "idx_vehicle_model_year", columnList = "model_year"),
                @Index(name = "idx_vehicle_base_price", columnList = "base_price")
        })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private VehicleType vehicleType;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(name = "model_year", nullable = false)
    private Integer modelYear;

    @Column(length = 500)
    private String description;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(length = 50)
    private String engine;

    @Column(length = 50)
    private String transmission;

    @Column(name = "fuel_type", length = 30)
    private String fuelType;

    @Column(length = 20)
    private String horsepower;

    @Column(name = "seating_capacity")
    private Integer seatingCapacity;

    @Column(name = "cargo_capacity_liters")
    private Integer cargoCapacityLiters;

    @Column(name = "fuel_efficiency", length = 30)
    private String fuelEfficiency;

    @Column(name = "main_image_url", length = 512)
    private String mainImageUrl;

    @ElementCollection
    @CollectionTable(
            name = "vehicle_images",
            schema = "vehicle_service",
            joinColumns = @JoinColumn(name = "vehicle_id")
    )
    @Column(name = "image_url", length = 512)
    @Builder.Default
    private Set<String> additionalImages = new HashSet<>();

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = AppConstants.VehicleStatus.AVAILABLE;

    @Column(name = "stock_quantity")
    @Builder.Default
    private Integer stockQuantity = 0;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "vehicle_customization_options",
            schema = "vehicle_service",
            joinColumns = @JoinColumn(name = "vehicle_id"),
            inverseJoinColumns = @JoinColumn(name = "customization_option_id")
    )
    @Builder.Default
    private Set<CustomizationOption> availableCustomizations = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Checks if the vehicle is available for purchase.
     */
    public boolean isAvailable() {
        return AppConstants.VehicleStatus.AVAILABLE.equals(this.status) && this.stockQuantity > 0;
    }
}
