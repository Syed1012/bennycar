package de.bennycar.vehicle.repository;

import de.bennycar.vehicle.domain.VehicleConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for VehicleConfiguration entity operations.
 */
@Repository
public interface VehicleConfigurationRepository extends JpaRepository<VehicleConfiguration, UUID> {

    Page<VehicleConfiguration> findByUserId(UUID userId, Pageable pageable);

    List<VehicleConfiguration> findByUserIdAndStatus(UUID userId, String status);

    @Query("SELECT vc FROM VehicleConfiguration vc JOIN FETCH vc.vehicle v JOIN FETCH v.brand JOIN FETCH v.vehicleType WHERE vc.id = :id")
    Optional<VehicleConfiguration> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT vc FROM VehicleConfiguration vc JOIN FETCH vc.vehicle WHERE vc.userId = :userId ORDER BY vc.createdAt DESC")
    List<VehicleConfiguration> findByUserIdWithVehicle(@Param("userId") UUID userId);

    @Query("SELECT vc FROM VehicleConfiguration vc WHERE vc.userId = :userId AND vc.id = :configId")
    Optional<VehicleConfiguration> findByIdAndUserId(@Param("configId") UUID configId, @Param("userId") UUID userId);

    @Query("SELECT COUNT(vc) FROM VehicleConfiguration vc WHERE vc.userId = :userId")
    long countByUserId(@Param("userId") UUID userId);

    @Query("SELECT vc FROM VehicleConfiguration vc WHERE vc.vehicle.id = :vehicleId")
    List<VehicleConfiguration> findByVehicleId(@Param("vehicleId") UUID vehicleId);

    void deleteByUserIdAndId(UUID userId, UUID configurationId);
}

