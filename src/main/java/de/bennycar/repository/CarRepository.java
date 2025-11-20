package de.bennycar.repository;

import de.bennycar.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByVin(String vin);

    List<Car> findByBrand(String brand);

    List<Car> findByModel(String model);

    List<Car> findByBrandAndModel(String brand, String model);

    List<Car> findByIsAvailable(Boolean isAvailable);

    List<Car> findByPriceBetween(Double minPrice, Double maxPrice);

    List<Car> findByYearGreaterThanEqual(Integer year);
}

