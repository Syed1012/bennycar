package de.bennycar.vehicle.repository;

import de.bennycar.vehicle.domain.CustomizationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for CustomizationCategory entity operations.
 */
@Repository
public interface CustomizationCategoryRepository extends JpaRepository<CustomizationCategory, UUID> {

    Optional<CustomizationCategory> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<CustomizationCategory> findByActiveTrue();

    @Query("SELECT cc FROM CustomizationCategory cc WHERE cc.active = true ORDER BY cc.displayOrder ASC, cc.name ASC")
    List<CustomizationCategory> findAllActiveOrderByDisplayOrder();
}

