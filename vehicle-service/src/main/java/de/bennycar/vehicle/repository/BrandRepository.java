package de.bennycar.vehicle.repository;

import de.bennycar.vehicle.domain.Brand;
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
 * Repository for Brand entity operations.
 */
@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {

    Optional<Brand> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Brand> findByActiveTrue();

    Page<Brand> findByActiveTrue(Pageable pageable);

    @Query("SELECT b FROM Brand b WHERE b.active = true ORDER BY b.name ASC")
    List<Brand> findAllActiveOrderByName();

    @Query("SELECT b FROM Brand b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :query, '%')) AND b.active = true")
    List<Brand> searchByName(@Param("query") String query);
}
