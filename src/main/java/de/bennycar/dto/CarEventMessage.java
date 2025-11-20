package de.bennycar.dto;

import java.time.LocalDateTime;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * CAR EVENT MESSAGE DTO
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Purpose: Represents a message about car events that flows through RabbitMQ
 *
 * Why use DTOs for messages?
 * 1. DECOUPLING: Producer and consumer don't need full Car entity
 * 2. SERIALIZATION: Easy to convert to/from JSON
 * 3. VERSIONING: Can evolve message format independently
 * 4. LIGHTWEIGHT: Only send data needed for the event
 *
 * Message Flow:
 *   CarService → Create CarEventMessage → RabbitMQ → Consumer receives → Process
 *
 * ═══════════════════════════════════════════════════════════════════════════
 */
public class CarEventMessage {

    /**
     * EVENT TYPE - What happened to the car
     * Examples: "CREATED", "UPDATED", "DELETED", "PRICE_CHANGED", "SOLD"
     */
    private String eventType;

    /**
     * CAR ID - Unique identifier for the car
     * Consumer can use this to fetch full car details if needed
     */
    private Long carId;

    /**
     * CAR VIN - Vehicle Identification Number
     * Unique identifier across all systems
     */
    private String vin;

    /**
     * CAR BRAND - Manufacturer name
     * Example: "Tesla", "BMW", "Toyota"
     */
    private String brand;

    /**
     * CAR MODEL - Specific model name
     * Example: "Model 3", "X5", "Camry"
     */
    private String model;

    /**
     * PRICE - Current price (useful for PRICE_CHANGED events)
     */
    private Double price;

    /**
     * OLD PRICE - Previous price (for price change tracking)
     */
    private Double oldPrice;

    /**
     * AVAILABILITY - Is car available for purchase?
     * true = available, false = sold/reserved
     */
    private Boolean isAvailable;

    /**
     * TIMESTAMP - When this event occurred
     * Important for:
     * - Event ordering
     * - Debugging timing issues
     * - Analytics (how long to process?)
     */
    private LocalDateTime timestamp;

    /**
     * MESSAGE - Optional description of the event
     * Example: "Price reduced by 15%", "Car marked as sold"
     */
    private String message;

    // ═══════════════════════════════════════════════════════════════════════
    // CONSTRUCTORS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Default constructor required for JSON deserialization
     * Jackson (JSON library) needs this to create object from JSON
     */
    public CarEventMessage() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Convenience constructor for quick message creation
     */
    public CarEventMessage(String eventType, Long carId, String vin, String brand,
                          String model, Double price, Boolean isAvailable, String message) {
        this.eventType = eventType;
        this.carId = carId;
        this.vin = vin;
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.isAvailable = isAvailable;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GETTERS AND SETTERS
    // ═══════════════════════════════════════════════════════════════════════
    // Required for JSON serialization/deserialization

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(Double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean available) {
        isAvailable = available;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CarEventMessage{" +
                "eventType='" + eventType + '\'' +
                ", carId=" + carId +
                ", vin='" + vin + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", price=" + price +
                ", oldPrice=" + oldPrice +
                ", isAvailable=" + isAvailable +
                ", timestamp=" + timestamp +
                ", message='" + message + '\'' +
                '}';
    }
}

