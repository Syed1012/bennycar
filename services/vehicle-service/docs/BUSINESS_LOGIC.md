# Vehicle Service - Business Logic & Implementation Guide

## Table of Contents
- [Core Business Concepts](#core-business-concepts)
- [Service Layer Details](#service-layer-details)
- [Data Models & Relationships](#data-models--relationships)
- [Business Rules & Validations](#business-rules--validations)
- [Pricing Logic](#pricing-logic)
- [Inventory Management](#inventory-management)
- [Configuration Workflow](#configuration-workflow)
- [Code Examples](#code-examples)

---

## Core Business Concepts

### 1. Vehicle Catalog

The vehicle catalog represents all vehicles available for purchase. Each vehicle has:

**Core Attributes:**
- Brand (manufacturer)
- Vehicle Type (SUV, Sedan, etc.)
- Model name and year
- Base price
- Technical specifications (engine, transmission, fuel, etc.)
- Capacity information (seating, cargo)
- Visual assets (images)
- Availability status and stock

**Business Purpose:**
Enable customers to browse and compare vehicles before customization.

---

### 2. Customization System

The customization system allows customers to personalize vehicles according to their preferences.

**Hierarchy:**
```
Customization Category (e.g., "Exterior Colors")
  └── Customization Options (e.g., "Red", "Blue", "White")
```

**Category Properties:**
- `allowsMultiple`: Can customer select multiple options? (e.g., Tech Packages: Yes, Color: No)
- `displayOrder`: Order in which categories appear
- `active`: Is category currently available?

**Option Properties:**
- `priceAdjustment`: Price change (positive = extra cost, negative = discount)
- `colorCode`: For color options (visual preview)
- `imageUrl`: Visual representation
- `displayOrder`: Order within category

**Business Rules:**
1. Each option belongs to exactly one category
2. Categories can be single-select or multi-select
3. Options have price adjustments (can be $0, positive, or negative)
4. Inactive options are hidden from customers but preserved in existing configurations

---

### 3. Vehicle Configuration

A vehicle configuration represents a customer's personalized vehicle build.

**Configuration Lifecycle:**

```
┌─────────┐      ┌─────────┐      ┌─────────┐
│  DRAFT  │─────>│ ORDERED │─────>│DELIVERED│
└─────────┘      └─────────┘      └─────────┘
     │                 │
     └────> CANCELLED  │
                       │
                       └────> CANCELLED
```

**Status Definitions:**
- **DRAFT**: Customer is building/editing the configuration
- **ORDERED**: Customer has placed an order (immutable)
- **DELIVERED**: Vehicle has been delivered (future state)
- **CANCELLED**: Configuration or order was cancelled

**Business Purpose:**
- Allow customers to save and compare different builds
- Track customer preferences
- Process orders efficiently
- Maintain order history

---

## Service Layer Details

### VehicleService

**Responsibilities:**
- Manage vehicle catalog (CRUD operations)
- Search and filter vehicles
- Retrieve vehicle with customization options
- Track availability and stock

**Key Methods:**

#### `searchVehicles(VehicleSearchParams params, Pageable pageable)`
```java
/**
 * Search vehicles with multiple filter criteria.
 * Uses Spring Data JPA Specifications for dynamic querying.
 */
```

**Logic:**
1. Build query specifications from parameters
2. Apply filters (brand, type, price range, year, status)
3. Combine with AND logic
4. Apply pagination and sorting
5. Map entities to DTOs

**Performance Optimization:**
- Database indexes on frequently queried fields
- Fetch joins for related entities (brand, type)
- DTO projection to avoid loading unnecessary data

---

#### `getVehicleWithCustomizations(UUID vehicleId)`
```java
/**
 * Retrieve vehicle with all applicable customization options.
 * Groups options by category for easy rendering.
 */
```

**Logic:**
1. Fetch vehicle by ID
2. Load associated customization options (from junction table)
3. Group options by category
4. Sort categories by displayOrder
5. Sort options within categories by displayOrder
6. Filter inactive options
7. Map to response DTO

**Business Rules:**
- Only active options are returned
- Categories are ordered for consistent UI
- Price adjustments are included for calculation preview

---

### ConfigurationService

**Responsibilities:**
- Create, read, update, delete configurations
- Calculate total pricing
- Process orders
- Manage configuration status transitions

**Key Methods:**

#### `createConfiguration(CreateConfigurationRequest request, UUID userId)`

**Logic Flow:**
```
1. Validate Input
   ├── Check vehicle exists
   ├── Check vehicle is available
   └── Check all options exist and are active

2. Verify Business Rules
   ├── Vehicle status = AVAILABLE
   ├── Stock quantity > 0
   └── Options are valid for vehicle

3. Create Configuration
   ├── Build entity from request
   ├── Link to user (from JWT)
   ├── Set status = DRAFT
   └── Associate selected options

4. Calculate Price
   ├── Get vehicle base price
   ├── Sum option price adjustments
   └── Set total price

5. Persist
   ├── Save configuration
   └── Return response DTO
```

**Implementation Details:**
```java
@Service
@Transactional
public class ConfigurationService {
    
    public ConfigurationResponse createConfiguration(
            CreateConfigurationRequest request, 
            UUID userId) {
        
        // 1. Fetch and validate vehicle
        Vehicle vehicle = vehicleRepository
            .findById(request.getVehicleId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Vehicle not found"));
        
        if (!vehicle.getStatus().equals(AppConstants.VehicleStatus.AVAILABLE)) {
            throw new VehicleNotAvailableException(
                "Vehicle is not available. Status: " + vehicle.getStatus());
        }
        
        if (vehicle.getStockQuantity() <= 0) {
            throw new VehicleNotAvailableException(
                "Vehicle is out of stock");
        }
        
        // 2. Validate and fetch options
        Set<CustomizationOption> selectedOptions = 
            validateAndFetchOptions(request.getSelectedOptionIds());
        
        // 3. Build configuration
        VehicleConfiguration configuration = VehicleConfiguration.builder()
            .userId(userId)
            .vehicle(vehicle)
            .name(request.getName())
            .notes(request.getNotes())
            .status(AppConstants.ConfigurationStatus.DRAFT)
            .selectedOptions(selectedOptions)
            .build();
        
        // 4. Calculate and set price
        configuration.updateTotalPrice();
        
        // 5. Save and return
        VehicleConfiguration saved = configurationRepository.save(configuration);
        return configurationMapper.toResponse(saved);
    }
    
    private Set<CustomizationOption> validateAndFetchOptions(
            Set<UUID> optionIds) {
        
        Set<CustomizationOption> options = new HashSet<>();
        
        for (UUID optionId : optionIds) {
            CustomizationOption option = optionRepository
                .findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Option not found: " + optionId));
            
            if (!option.isActive()) {
                throw new InvalidConfigurationException(
                    "Option is no longer active: " + option.getName());
            }
            
            options.add(option);
        }
        
        return options;
    }
}
```

---

#### `orderConfiguration(UUID configId, UUID userId)`

**Logic Flow:**
```
1. Validate Configuration
   ├── Check configuration exists
   ├── Check user ownership
   └── Check status = DRAFT

2. Re-validate Vehicle
   ├── Vehicle still exists
   ├── Vehicle status = AVAILABLE
   └── Stock quantity > 0

3. Process Order
   ├── Decrement vehicle stock (atomic)
   ├── Update status = ORDERED
   ├── Set ordered_at timestamp
   └── Save configuration

4. Future: External Actions
   ├── Notify User Service (email)
   ├── Create payment record
   └── Trigger fulfillment process
```

**Implementation:**
```java
@Transactional
public ConfigurationResponse orderConfiguration(UUID configId, UUID userId) {
    
    // 1. Fetch and validate configuration
    VehicleConfiguration config = configurationRepository
        .findById(configId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Configuration not found"));
    
    // 2. Verify ownership
    if (!config.getUserId().equals(userId)) {
        throw new ForbiddenException("Access denied");
    }
    
    // 3. Verify status
    if (!config.getStatus().equals(AppConstants.ConfigurationStatus.DRAFT)) {
        throw new InvalidConfigurationException(
            "Only DRAFT configurations can be ordered. Current status: " 
            + config.getStatus());
    }
    
    // 4. Re-validate vehicle availability
    Vehicle vehicle = config.getVehicle();
    if (!vehicle.getStatus().equals(AppConstants.VehicleStatus.AVAILABLE) 
            || vehicle.getStockQuantity() <= 0) {
        throw new VehicleNotAvailableException(
            "Vehicle is no longer available for order");
    }
    
    // 5. Decrement stock (atomic operation within transaction)
    vehicle.setStockQuantity(vehicle.getStockQuantity() - 1);
    vehicleRepository.save(vehicle);
    
    // 6. Update configuration
    config.setStatus(AppConstants.ConfigurationStatus.ORDERED);
    config.setOrderedAt(Instant.now());
    
    VehicleConfiguration ordered = configurationRepository.save(config);
    
    // 7. Future: Trigger notifications/events
    // eventPublisher.publishOrderCreated(ordered);
    
    return configurationMapper.toResponse(ordered);
}
```

**Critical Aspects:**
- **Transaction boundary**: Stock decrement and status update are atomic
- **Race condition prevention**: Database transactions ensure consistency
- **Idempotency**: Can't order twice (status check)
- **Data integrity**: Re-validation prevents stale data issues

---

## Data Models & Relationships

### Entity Relationship Details

#### Vehicle Entity

```java
@Entity
@Table(name = "vehicles", schema = "vehicle_service")
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private VehicleType vehicleType;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "vehicle_customization_options",
        schema = "vehicle_service",
        joinColumns = @JoinColumn(name = "vehicle_id"),
        inverseJoinColumns = @JoinColumn(name = "customization_option_id")
    )
    private Set<CustomizationOption> availableCustomizations;
    
    // ... other fields
}
```

**Relationship Explanations:**

1. **Vehicle ↔ Brand** (Many-to-One)
   - Many vehicles belong to one brand
   - Lazy loading for performance
   - Indexed foreign key

2. **Vehicle ↔ VehicleType** (Many-to-One)
   - Categorizes vehicle (SUV, Sedan, etc.)
   - Used for filtering and organization

3. **Vehicle ↔ CustomizationOption** (Many-to-Many)
   - Defines which options are applicable to this vehicle
   - Junction table: `vehicle_customization_options`
   - Allows vehicle-specific customizations

---

#### VehicleConfiguration Entity

```java
@Entity
@Table(name = "vehicle_configurations", schema = "vehicle_service")
@EntityListeners(AuditingEntityListener.class)
public class VehicleConfiguration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // User reference (not a JPA relationship - links to User Service)
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "configuration_selected_options",
        schema = "vehicle_service",
        joinColumns = @JoinColumn(name = "configuration_id"),
        inverseJoinColumns = @JoinColumn(name = "customization_option_id")
    )
    private Set<CustomizationOption> selectedOptions;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private String status;
    
    @Column(name = "total_price", precision = 12, scale = 2)
    private BigDecimal totalPrice;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    @Column(name = "ordered_at")
    private Instant orderedAt;
    
    /**
     * Calculate total price from vehicle base price and option adjustments
     */
    public BigDecimal calculateTotalPrice() {
        BigDecimal basePrice = vehicle != null 
            ? vehicle.getBasePrice() 
            : BigDecimal.ZERO;
            
        BigDecimal optionsTotal = selectedOptions.stream()
            .map(CustomizationOption::getPriceAdjustment)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        return basePrice.add(optionsTotal);
    }
    
    /**
     * Update the stored total price
     */
    public void updateTotalPrice() {
        this.totalPrice = calculateTotalPrice();
    }
}
```

**Key Design Decisions:**

1. **userId as UUID (not JPA relationship)**
   - Loose coupling with User Service
   - No foreign key constraint
   - Services remain independent

2. **JPA Auditing**
   - `@EntityListeners(AuditingEntityListener.class)`
   - Automatically tracks created_at and updated_at
   - Reduces boilerplate code

3. **Price Calculation Method**
   - Stored in database for historical accuracy
   - Recalculated on configuration changes
   - Immutable after ordering

---

## Business Rules & Validations

### Configuration Creation Rules

1. **Vehicle Availability**
   ```java
   if (!vehicle.getStatus().equals("AVAILABLE")) {
       throw new VehicleNotAvailableException();
   }
   ```

2. **Stock Validation**
   ```java
   if (vehicle.getStockQuantity() <= 0) {
       throw new VehicleNotAvailableException();
   }
   ```

3. **Option Validation**
   ```java
   for (UUID optionId : selectedOptionIds) {
       CustomizationOption option = findOption(optionId);
       if (!option.isActive()) {
           throw new InvalidConfigurationException();
       }
       // Verify option is applicable to vehicle
       if (!vehicle.getAvailableCustomizations().contains(option)) {
           throw new InvalidConfigurationException();
       }
   }
   ```

4. **Category Constraints**
   ```java
   // Check single-select categories
   Map<UUID, List<CustomizationOption>> optionsByCategory = 
       groupByCategory(selectedOptions);
       
   for (Map.Entry<UUID, List<CustomizationOption>> entry : 
           optionsByCategory.entrySet()) {
       CustomizationCategory category = findCategory(entry.getKey());
       
       if (!category.isAllowsMultiple() && entry.getValue().size() > 1) {
           throw new InvalidConfigurationException(
               "Category '" + category.getName() + 
               "' allows only one selection");
       }
   }
   ```

---

### Order Placement Rules

1. **Configuration Status**
   ```java
   if (!configuration.getStatus().equals("DRAFT")) {
       throw new InvalidConfigurationException(
           "Only DRAFT configurations can be ordered");
   }
   ```

2. **User Ownership**
   ```java
   if (!configuration.getUserId().equals(currentUserId)) {
       throw new ForbiddenException("Access denied");
   }
   ```

3. **Vehicle Re-validation**
   ```java
   // Time may have passed since configuration creation
   Vehicle vehicle = configuration.getVehicle();
   if (!vehicle.getStatus().equals("AVAILABLE") || 
           vehicle.getStockQuantity() <= 0) {
       throw new VehicleNotAvailableException(
           "Vehicle is no longer available");
   }
   ```

4. **Atomic Stock Decrement**
   ```java
   @Transactional
   public void processOrder(VehicleConfiguration config) {
       // Transaction ensures atomicity
       Vehicle vehicle = config.getVehicle();
       vehicle.setStockQuantity(vehicle.getStockQuantity() - 1);
       vehicleRepository.save(vehicle);
       
       config.setStatus("ORDERED");
       configurationRepository.save(config);
       // Both operations succeed or both fail
   }
   ```

---

## Pricing Logic

### Price Calculation Formula

```
Total Price = Vehicle Base Price + Σ(Selected Option Price Adjustments)
```

### Implementation

```java
public BigDecimal calculateTotalPrice() {
    // 1. Get vehicle base price
    BigDecimal basePrice = vehicle.getBasePrice(); // e.g., $42,000
    
    // 2. Sum all option price adjustments
    BigDecimal optionsTotal = selectedOptions.stream()
        .map(option -> option.getPriceAdjustment())
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    // Example:
    // - Midnight Silver: +$1,000
    // - Black & White Interior: +$1,000
    // - Enhanced Autopilot: +$6,000
    // Total adjustments: $8,000
    
    // 3. Calculate total
    return basePrice.add(optionsTotal); // $42,000 + $8,000 = $50,000
}
```

### Price Adjustment Examples

**Positive Adjustments (Upgrades):**
```java
// Premium paint color
option.setPriceAdjustment(new BigDecimal("1000.00"));

// Technology package
option.setPriceAdjustment(new BigDecimal("5000.00"));
```

**Zero Adjustments (Standard Options):**
```java
// Standard white color
option.setPriceAdjustment(BigDecimal.ZERO);
```

**Negative Adjustments (Discounts):**
```java
// Promotional discount
option.setPriceAdjustment(new BigDecimal("-500.00"));

// Simplified feature package
option.setPriceAdjustment(new BigDecimal("-1000.00"));
```

### Price Storage

**Why store calculated price?**
1. **Historical accuracy**: Prices may change over time
2. **Performance**: No recalculation needed for read operations
3. **Immutability**: Ordered configurations preserve original price
4. **Reporting**: Easy aggregation for analytics

---

## Inventory Management

### Stock Tracking

**Vehicle Entity:**
```java
@Column(name = "stock_quantity")
private Integer stockQuantity;
```

### Stock Operations

#### 1. Decrement Stock (Order Placement)
```java
@Transactional
public void processOrder(VehicleConfiguration configuration) {
    Vehicle vehicle = configuration.getVehicle();
    
    // Check availability
    if (vehicle.getStockQuantity() <= 0) {
        throw new VehicleNotAvailableException("Out of stock");
    }
    
    // Decrement atomically
    vehicle.setStockQuantity(vehicle.getStockQuantity() - 1);
    vehicleRepository.save(vehicle);
    
    // Update status if out of stock
    if (vehicle.getStockQuantity() == 0) {
        vehicle.setStatus(AppConstants.VehicleStatus.SOLD_OUT);
    }
}
```

#### 2. Increment Stock (Order Cancellation - Future)
```java
@Transactional
public void cancelOrder(VehicleConfiguration configuration) {
    Vehicle vehicle = configuration.getVehicle();
    
    // Increment stock
    vehicle.setStockQuantity(vehicle.getStockQuantity() + 1);
    
    // Restore availability if was sold out
    if (vehicle.getStatus().equals(AppConstants.VehicleStatus.SOLD_OUT)) {
        vehicle.setStatus(AppConstants.VehicleStatus.AVAILABLE);
    }
    
    vehicleRepository.save(vehicle);
}
```

### Concurrency Handling

**Problem:** Multiple users ordering the same vehicle simultaneously

**Solution 1: Database Transaction Isolation**
```yaml
# application.yml
spring:
  jpa:
    properties:
      hibernate:
        connection:
          isolation: REPEATABLE_READ
```

**Solution 2: Optimistic Locking (Recommended)**
```java
@Entity
public class Vehicle {
    @Version
    private Long version;
    
    // ... other fields
}
```

- Version field auto-incremented on updates
- Concurrent updates cause `OptimisticLockException`
- Retry logic handles conflicts

---

## Configuration Workflow

### Complete User Journey

```
1. Browse Vehicles
   ├── User filters by brand/type/price
   ├── User views vehicle details
   └── User selects a vehicle

2. Customize Vehicle
   ├── System loads customization options
   ├── User selects color, interior, wheels, etc.
   ├── System calculates price in real-time (frontend)
   └── User saves configuration (becomes DRAFT)

3. Review Configurations
   ├── User views saved configurations
   ├── User compares different builds
   ├── User can edit DRAFT configurations
   └── User can delete unwanted configurations

4. Place Order
   ├── User selects a configuration to order
   ├── System validates vehicle availability
   ├── System processes payment (future)
   ├── System decrements stock
   ├── Configuration becomes ORDERED (immutable)
   └── User receives confirmation

5. Post-Order (Future)
   ├── Fulfillment tracking
   ├── Delivery scheduling
   ├── Configuration becomes DELIVERED
   └── Order history
```

### State Transitions

```java
public class AppConstants {
    public static class ConfigurationStatus {
        public static final String DRAFT = "DRAFT";
        public static final String ORDERED = "ORDERED";
        public static final String DELIVERED = "DELIVERED";
        public static final String CANCELLED = "CANCELLED";
    }
    
    // Valid transitions
    // DRAFT → ORDERED
    // DRAFT → CANCELLED
    // ORDERED → DELIVERED
    // ORDERED → CANCELLED (with conditions)
}
```

**Transition Rules:**
- **DRAFT → ORDERED**: Requires payment and stock availability
- **DRAFT → CANCELLED**: User can cancel anytime
- **ORDERED → DELIVERED**: Admin/System action after fulfillment
- **ORDERED → CANCELLED**: Only before fulfillment starts

---

## Code Examples

### Example 1: Custom Query with Specifications

```java
public class VehicleSpecifications {
    
    public static Specification<Vehicle> hasBrand(UUID brandId) {
        return (root, query, cb) -> 
            brandId == null ? null : cb.equal(root.get("brand").get("id"), brandId);
    }
    
    public static Specification<Vehicle> hasVehicleType(UUID typeId) {
        return (root, query, cb) -> 
            typeId == null ? null : cb.equal(root.get("vehicleType").get("id"), typeId);
    }
    
    public static Specification<Vehicle> priceBetween(
            BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice != null && maxPrice != null) {
                return cb.between(root.get("basePrice"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return cb.greaterThanOrEqualTo(root.get("basePrice"), minPrice);
            } else if (maxPrice != null) {
                return cb.lessThanOrEqualTo(root.get("basePrice"), maxPrice);
            }
            return null;
        };
    }
    
    public static Specification<Vehicle> hasStatus(String status) {
        return (root, query, cb) -> 
            status == null ? null : cb.equal(root.get("status"), status);
    }
}

// Usage in VehicleService
public Page<VehicleResponse> searchVehicles(
        VehicleSearchParams params, Pageable pageable) {
    
    Specification<Vehicle> spec = Specification
        .where(VehicleSpecifications.hasBrand(params.getBrandId()))
        .and(VehicleSpecifications.hasVehicleType(params.getVehicleTypeId()))
        .and(VehicleSpecifications.priceBetween(
            params.getMinPrice(), params.getMaxPrice()))
        .and(VehicleSpecifications.hasStatus(params.getStatus()));
    
    Page<Vehicle> vehicles = vehicleRepository.findAll(spec, pageable);
    return vehicles.map(vehicleMapper::toResponse);
}
```

### Example 2: DTO Mapping with MapStruct

```java
@Mapper(componentModel = "spring")
public interface VehicleMapper {
    
    @Mapping(source = "brand.name", target = "brandName")
    @Mapping(source = "vehicleType.name", target = "typeName")
    VehicleResponse toResponse(Vehicle vehicle);
    
    List<VehicleResponse> toResponseList(List<Vehicle> vehicles);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Vehicle toEntity(VehicleRequest request);
}
```

### Example 3: Global Exception Handling

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error(HttpStatus.NOT_FOUND.getReasonPhrase())
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(VehicleNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleVehicleNotAvailable(
            VehicleNotAvailableException ex, WebRequest request) {
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.CONFLICT.value())
            .error(HttpStatus.CONFLICT.getReasonPhrase())
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            validationErrors.put(error.getField(), error.getDefaultMessage())
        );
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .message("Invalid request data")
            .path(request.getDescription(false).replace("uri=", ""))
            .validationErrors(validationErrors)
            .build();
        
        return ResponseEntity.badRequest().body(error);
    }
}
```

---

**Last Updated**: December 12, 2025  
**Version**: 1.0

