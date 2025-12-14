package de.bennycar.vehicle.repository;

import de.bennycar.vehicle.domain.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Vehicle entity operations.
 * Extends JpaSpecificationExecutor for dynamic query support.
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID>, JpaSpecificationExecutor<Vehicle> {

    @Query("SELECT v FROM Vehicle v JOIN FETCH v.brand JOIN FETCH v.vehicleType WHERE v.id = :id")
    Optional<Vehicle> findByIdWithDetails(@Param("id") UUID id);

    Page<Vehicle> findByStatus(String status, Pageable pageable);

    Page<Vehicle> findByBrandId(UUID brandId, Pageable pageable);

    Page<Vehicle> findByVehicleTypeId(UUID vehicleTypeId, Pageable pageable);

    @Query("SELECT v FROM Vehicle v WHERE v.brand.id = :brandId AND v.status = :status")
    Page<Vehicle> findByBrandIdAndStatus(@Param("brandId") UUID brandId, @Param("status") String status, Pageable pageable);

    @Query("SELECT v FROM Vehicle v WHERE v.basePrice BETWEEN :minPrice AND :maxPrice AND v.status = 'AVAILABLE'")
    Page<Vehicle> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT v FROM Vehicle v WHERE v.modelYear = :year AND v.status = 'AVAILABLE'")
    List<Vehicle> findAvailableByModelYear(@Param("year") Integer year);

    @Query("SELECT v FROM Vehicle v WHERE " +
            "(:brandId IS NULL OR v.brand.id = :brandId) AND " +
            "(:vehicleTypeId IS NULL OR v.vehicleType.id = :vehicleTypeId) AND " +
            "(:modelYear IS NULL OR v.modelYear = :modelYear) AND " +
            "(:minPrice IS NULL OR v.basePrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR v.basePrice <= :maxPrice) AND " +
            "(:status IS NULL OR v.status = :status)")
    Page<Vehicle> searchVehicles(
            @Param("brandId") UUID brandId,
            @Param("vehicleTypeId") UUID vehicleTypeId,
            @Param("modelYear") Integer modelYear,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("status") String status,
            Pageable pageable);

    @Query("SELECT DISTINCT v.modelYear FROM Vehicle v WHERE v.status = 'AVAILABLE' ORDER BY v.modelYear DESC")
    List<Integer> findDistinctAvailableModelYears();

    boolean existsByBrandIdAndModelAndModelYear(UUID brandId, String model, Integer modelYear);
}

