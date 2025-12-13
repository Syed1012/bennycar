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
 * Entity representing a user's configured vehicle build.
 * Contains the base vehicle and all selected customization options.
 */
@Entity
@Table(name = "vehicle_configurations", schema = "vehicle_service",
        indexes = {
                @Index(name = "idx_config_user", columnList = "user_id"),
                @Index(name = "idx_config_vehicle", columnList = "vehicle_id"),
                @Index(name = "idx_config_status", columnList = "status"),
                @Index(name = "idx_config_created", columnList = "created_at")
        })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(length = 100)
    private String name;

    @Column(length = 500)
    private String notes;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = AppConstants.ConfigurationStatus.DRAFT;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "configuration_selected_options",
            schema = "vehicle_service",
            joinColumns = @JoinColumn(name = "configuration_id"),
            inverseJoinColumns = @JoinColumn(name = "customization_option_id")
    )
    @Builder.Default
    private Set<CustomizationOption> selectedOptions = new HashSet<>();

    @Column(name = "total_price", precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "ordered_at")
    private Instant orderedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Calculates the total price based on vehicle base price and selected options.
     */
    public BigDecimal calculateTotalPrice() {
        BigDecimal basePrice = vehicle != null ? vehicle.getBasePrice() : BigDecimal.ZERO;
        BigDecimal optionsTotal = selectedOptions.stream()
                .map(CustomizationOption::getPriceAdjustment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return basePrice.add(optionsTotal);
    }

    /**
     * Updates the total price based on current selections.
     */
    public void updateTotalPrice() {
        this.totalPrice = calculateTotalPrice();
    }
}

