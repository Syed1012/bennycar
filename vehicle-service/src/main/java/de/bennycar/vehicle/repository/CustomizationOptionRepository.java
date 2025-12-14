package de.bennycar.vehicle.repository;

import de.bennycar.vehicle.domain.CustomizationOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for CustomizationOption entity operations.
 */
@Repository
public interface CustomizationOptionRepository extends JpaRepository<CustomizationOption, UUID> {

    List<CustomizationOption> findByCategoryId(UUID categoryId);

    List<CustomizationOption> findByCategoryIdAndActiveTrue(UUID categoryId);

    @Query("SELECT co FROM CustomizationOption co WHERE co.category.id = :categoryId AND co.active = true ORDER BY co.displayOrder ASC, co.name ASC")
    List<CustomizationOption> findActiveByCategoryOrderByDisplayOrder(@Param("categoryId") UUID categoryId);

    @Query("SELECT co FROM CustomizationOption co WHERE co.active = true ORDER BY co.category.displayOrder ASC, co.displayOrder ASC")
    List<CustomizationOption> findAllActiveOrderByCategory();

    @Query("SELECT co FROM CustomizationOption co JOIN co.compatibleVehicles v WHERE v.id = :vehicleId AND co.active = true")
    List<CustomizationOption> findActiveByVehicleId(@Param("vehicleId") UUID vehicleId);

    boolean existsByNameAndCategoryId(String name, UUID categoryId);
}

