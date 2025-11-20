package de.bennycar.controller;

import de.bennycar.model.Car;
import de.bennycar.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = "*")
public class CarController {

    @Autowired
    private CarService carService;

    // Create a new car
    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody Car car) {
        try {
            Car savedCar = carService.saveCar(car);
            return new ResponseEntity<>(savedCar, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all cars
    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        try {
            List<Car> cars = carService.getAllCars();
            if (cars.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(cars, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get car by ID
    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable("id") Long id) {
        return carService.getCarById(id)
                .map(car -> new ResponseEntity<>(car, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Get car by VIN
    @GetMapping("/vin/{vin}")
    public ResponseEntity<Car> getCarByVin(@PathVariable("vin") String vin) {
        return carService.getCarByVin(vin)
                .map(car -> new ResponseEntity<>(car, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Get cars by brand
    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<Car>> getCarsByBrand(@PathVariable("brand") String brand) {
        try {
            List<Car> cars = carService.getCarsByBrand(brand);
            if (cars.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(cars, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get cars by brand and model
    @GetMapping("/brand/{brand}/model/{model}")
    public ResponseEntity<List<Car>> getCarsByBrandAndModel(
            @PathVariable("brand") String brand,
            @PathVariable("model") String model) {
        try {
            List<Car> cars = carService.getCarsByBrandAndModel(brand, model);
            if (cars.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(cars, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get available cars
    @GetMapping("/available")
    public ResponseEntity<List<Car>> getAvailableCars() {
        try {
            List<Car> cars = carService.getAvailableCars();
            if (cars.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(cars, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get cars by price range
    @GetMapping("/price")
    public ResponseEntity<List<Car>> getCarsByPriceRange(
            @RequestParam("min") Double minPrice,
            @RequestParam("max") Double maxPrice) {
        try {
            List<Car> cars = carService.getCarsByPriceRange(minPrice, maxPrice);
            if (cars.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(cars, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get cars by year or newer
    @GetMapping("/year/{year}")
    public ResponseEntity<List<Car>> getCarsByYearOrNewer(@PathVariable("year") Integer year) {
        try {
            List<Car> cars = carService.getCarsByYearOrNewer(year);
            if (cars.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(cars, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update car
    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable("id") Long id, @RequestBody Car car) {
        try {
            Car updatedCar = carService.updateCar(id, car);
            return new ResponseEntity<>(updatedCar, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete car
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCar(@PathVariable("id") Long id) {
        try {
            carService.deleteCar(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

