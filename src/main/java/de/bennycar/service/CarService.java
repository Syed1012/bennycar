package de.bennycar.service;

import de.bennycar.dto.CarEventMessage;
import de.bennycar.messaging.CarEventProducer;
import de.bennycar.model.Car;
import de.bennycar.repository.CarRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * CAR SERVICE - Enhanced with AMQP Event Publishing
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * This service demonstrates EVENT-DRIVEN ARCHITECTURE with RabbitMQ
 *
 * Key Pattern: DATABASE + MESSAGE QUEUE
 * 1. Perform database operation (save, update, delete)
 * 2. Publish event to RabbitMQ
 * 3. Other services/consumers react to event asynchronously
 *
 * Benefits:
 * - DECOUPLING: Services don't directly depend on each other
 * - SCALABILITY: Consumers can scale independently
 * - RELIABILITY: Messages persist if consumer is down
 * - FLEXIBILITY: Add new consumers without changing producer
 *
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Service
public class CarService {

    private static final Logger log = LoggerFactory.getLogger(CarService.class);

    @Autowired
    private CarRepository carRepository;

    /**
     * AMQP PRODUCER - Publishes events to RabbitMQ
     * Injected by Spring automatically
     */
    @Autowired
    private CarEventProducer carEventProducer;

    /**
     * SAVE CAR - Create new car and publish CREATED event
     *
     * Flow:
     * 1. Save car to database (PostgreSQL)
     * 2. Create event message with car details
     * 3. Publish to RabbitMQ (asynchronous, fire-and-forget)
     * 4. Return saved car to caller
     * 5. Meanwhile: Consumers process event (analytics, notifications, etc.)
     */
    public Car saveCar(Car car) {
        // Step 1: Save to database
        Car savedCar = carRepository.save(car);
        log.info("Car saved to database: ID={}, VIN={}", savedCar.getId(), savedCar.getVin());

        // Step 2: Create event message
        CarEventMessage event = new CarEventMessage(
                "CREATED",
                savedCar.getId(),
                savedCar.getVin(),
                savedCar.getBrand(),
                savedCar.getModel(),
                savedCar.getPrice(),
                savedCar.getIsAvailable(),
                "New car added to inventory"
        );

        // Step 3: Publish event to RabbitMQ
        carEventProducer.sendCarCreatedEvent(event);
        log.info("Car CREATED event published to RabbitMQ");

        return savedCar;
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Optional<Car> getCarById(Long id) {
        return carRepository.findById(id);
    }

    public Optional<Car> getCarByVin(String vin) {
        return carRepository.findByVin(vin);
    }

    public List<Car> getCarsByBrand(String brand) {
        return carRepository.findByBrand(brand);
    }

    public List<Car> getCarsByModel(String model) {
        return carRepository.findByModel(model);
    }

    public List<Car> getCarsByBrandAndModel(String brand, String model) {
        return carRepository.findByBrandAndModel(brand, model);
    }

    public List<Car> getAvailableCars() {
        return carRepository.findByIsAvailable(true);
    }

    public List<Car> getCarsByPriceRange(Double minPrice, Double maxPrice) {
        return carRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public List<Car> getCarsByYearOrNewer(Integer year) {
        return carRepository.findByYearGreaterThanEqual(year);
    }

    /**
     * UPDATE CAR - Update car details and publish appropriate events
     *
     * Smart event publishing:
     * - If price changed → send PRICE_CHANGED event to price alert queue
     * - If availability changed → send AVAILABILITY_CHANGED to inventory queue
     * - Always send general UPDATE event
     *
     * This demonstrates SELECTIVE ROUTING:
     * - Different queues receive different events
     * - Consumers only process events they care about
     */
    public Car updateCar(Long id, Car carDetails) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + id));

        // Track changes for event publishing
        Double oldPrice = car.getPrice();
        Boolean oldAvailability = car.getIsAvailable();

        // Update fields
        if (carDetails.getName() != null) car.setName(carDetails.getName());
        if (carDetails.getVin() != null) car.setVin(carDetails.getVin());
        if (carDetails.getBrand() != null) car.setBrand(carDetails.getBrand());
        if (carDetails.getModel() != null) car.setModel(carDetails.getModel());
        if (carDetails.getYear() != null) car.setYear(carDetails.getYear());
        if (carDetails.getColor() != null) car.setColor(carDetails.getColor());
        if (carDetails.getTransmission() != null) car.setTransmission(carDetails.getTransmission());
        if (carDetails.getFuelType() != null) car.setFuelType(carDetails.getFuelType());
        if (carDetails.getMileage() != null) car.setMileage(carDetails.getMileage());
        if (carDetails.getBodyType() != null) car.setBodyType(carDetails.getBodyType());
        if (carDetails.getPrice() != null) car.setPrice(carDetails.getPrice());
        if (carDetails.getDescription() != null) car.setDescription(carDetails.getDescription());
        if (carDetails.getCondition() != null) car.setCondition(carDetails.getCondition());
        if (carDetails.getLocation() != null) car.setLocation(carDetails.getLocation());
        if (carDetails.getIsAvailable() != null) car.setIsAvailable(carDetails.getIsAvailable());

        Car updatedCar = carRepository.save(car);
        log.info("Car updated in database: ID={}", updatedCar.getId());

        // ═══════════════════════════════════════════════════════════════════
        // SMART EVENT PUBLISHING - Detect what changed
        // ═══════════════════════════════════════════════════════════════════

        // 1. Check for PRICE CHANGE
        if (carDetails.getPrice() != null && !oldPrice.equals(updatedCar.getPrice())) {
            CarEventMessage priceEvent = new CarEventMessage(
                    "PRICE_CHANGED",
                    updatedCar.getId(),
                    updatedCar.getVin(),
                    updatedCar.getBrand(),
                    updatedCar.getModel(),
                    updatedCar.getPrice(),
                    updatedCar.getIsAvailable(),
                    String.format("Price changed from $%.2f to $%.2f", oldPrice, updatedCar.getPrice())
            );
            priceEvent.setOldPrice(oldPrice);

            // Send to TOPIC exchange - will route to price alert queue
            carEventProducer.sendPriceChangedEvent(priceEvent);
            log.info("Price CHANGED event published: {} → {}", oldPrice, updatedCar.getPrice());
        }

        // 2. Check for AVAILABILITY CHANGE (sold/available)
        if (carDetails.getIsAvailable() != null && !oldAvailability.equals(updatedCar.getIsAvailable())) {
            CarEventMessage availabilityEvent = new CarEventMessage(
                    "AVAILABILITY_CHANGED",
                    updatedCar.getId(),
                    updatedCar.getVin(),
                    updatedCar.getBrand(),
                    updatedCar.getModel(),
                    updatedCar.getPrice(),
                    updatedCar.getIsAvailable(),
                    updatedCar.getIsAvailable() ? "Car is now available" : "Car has been sold"
            );

            // Send to TOPIC exchange - will route to inventory queue
            carEventProducer.sendAvailabilityChangedEvent(availabilityEvent);
            log.info("Availability CHANGED event published: {} → {}",
                    oldAvailability, updatedCar.getIsAvailable());
        }

        // 3. Always send general UPDATE event
        CarEventMessage updateEvent = new CarEventMessage(
                "UPDATED",
                updatedCar.getId(),
                updatedCar.getVin(),
                updatedCar.getBrand(),
                updatedCar.getModel(),
                updatedCar.getPrice(),
                updatedCar.getIsAvailable(),
                "Car details updated"
        );
        carEventProducer.sendCarUpdatedEvent(updateEvent);
        log.info("Car UPDATED event published");

        return updatedCar;
    }

    /**
     * DELETE CAR - Remove car and publish DELETED event
     *
     * Important: Fetch car details BEFORE deleting
     * - Need car info for the event message
     * - After delete, data is gone from database
     */
    public void deleteCar(Long id) {
        // Fetch car before deleting (need details for event)
        Optional<Car> carOptional = carRepository.findById(id);

        if (carOptional.isPresent()) {
            Car car = carOptional.get();

            // Delete from database
            carRepository.deleteById(id);
            log.info("Car deleted from database: ID={}, VIN={}", id, car.getVin());

            // Publish DELETE event
            CarEventMessage deleteEvent = new CarEventMessage(
                    "DELETED",
                    car.getId(),
                    car.getVin(),
                    car.getBrand(),
                    car.getModel(),
                    car.getPrice(),
                    false, // No longer available
                    "Car removed from inventory"
            );
            carEventProducer.sendCarDeletedEvent(deleteEvent);
            log.info("Car DELETED event published");
        } else {
            log.warn("Attempted to delete non-existent car: ID={}", id);
            throw new RuntimeException("Car not found with id: " + id);
        }
    }
}
