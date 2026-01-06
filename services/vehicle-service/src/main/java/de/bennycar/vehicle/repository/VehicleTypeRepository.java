package de.bennycar.vehicle.repository;

import de.bennycar.vehicle.domain.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for VehicleType entity operations.
 */
@Repository
public interface VehicleTypeRepository extends JpaRepository<VehicleType, UUID> {

    Optional<VehicleType> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT vt FROM VehicleType vt ORDER BY vt.name ASC")
    List<VehicleType> findAllOrderByName();
}

