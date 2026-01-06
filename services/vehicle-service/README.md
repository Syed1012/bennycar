# Vehicle Service - BennyCar Platform

## 📋 Table of Contents
- [Overview](#overview)
- [Business Problem & Solution](#business-problem--solution)
- [Architecture & Design](#architecture--design)
- [Technology Stack](#technology-stack)
- [Database Schema](#database-schema)
- [API Documentation](#api-documentation)
- [Business Logic](#business-logic)
- [Security & Authentication](#security--authentication)
- [Integration with User Service](#integration-with-user-service)
- [Configuration](#configuration)
- [Getting Started](#getting-started)
- [Development Guide](#development-guide)

---

## 🎯 Overview

The **Vehicle Service** is a core microservice of the BennyCar platform that manages the entire vehicle catalog, customization options, and user vehicle configurations. It serves as the backbone for the vehicle browsing and customization experience.

### Key Responsibilities
- **Vehicle Catalog Management**: Maintain a comprehensive catalog of vehicles with detailed specifications
- **Brand & Type Management**: Organize vehicles by brands and types (SUV, Sedan, Truck, etc.)
- **Customization System**: Manage customization categories and options (colors, interiors, wheels, etc.)
- **User Configurations**: Store and manage user's customized vehicle builds
- **Pricing Calculations**: Dynamic price calculation based on base vehicle and selected options
- **Inventory Tracking**: Track vehicle availability and stock levels

---

## 💼 Business Problem & Solution

### Business Problem

Modern car buyers expect a highly personalized buying experience. They want to:
1. **Browse diverse vehicle options** with detailed specifications
2. **Customize their vehicles** with various options (colors, interiors, tech packages)
3. **See real-time pricing** as they customize their build
4. **Save multiple configurations** to compare different builds
5. **Access their saved builds** from anywhere, anytime

Traditional car dealership systems are often:
- Siloed and disconnected
- Lacking real-time inventory tracking
- Unable to provide personalized experiences
- Limited in customization options

### Our Solution

The Vehicle Service solves these problems by providing:

#### 1. **Comprehensive Vehicle Catalog**
   - Centralized repository of all vehicles
   - Rich metadata (specifications, pricing, images)
   - Organized by brands and vehicle types
   - Real-time availability status

#### 2. **Flexible Customization System**
   - Hierarchical customization categories
   - Multiple options per category
   - Price adjustments per option
   - Visual representation with images

#### 3. **User Configuration Management**
   - Save and retrieve custom builds
   - Track configuration status (draft, ordered, etc.)
   - Calculate total pricing automatically
   - Link configurations to authenticated users

#### 4. **Smart Filtering & Search**
   - Filter by brand, type, price range, year
   - Pagination for large datasets
   - Optimized database queries with indexes

---

## 🏗️ Architecture & Design

### Service Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Vehicle Service                           │
├─────────────────────────────────────────────────────────────┤
│  Controllers (REST API Layer)                                │
│  ├─── VehicleController                                      │
│  ├─── BrandController                                        │
│  ├─── VehicleTypeController                                  │
│  ├─── CustomizationCategoryController                        │
│  ├─── CustomizationOptionController                          │
│  └─── ConfigurationController (Secured)                      │
├─────────────────────────────────────────────────────────────┤
│  Services (Business Logic Layer)                             │
│  ├─── VehicleService                                         │
│  ├─── BrandService                                           │
│  ├─── VehicleTypeService                                     │
│  ├─── CustomizationCategoryService                           │
│  ├─── CustomizationOptionService                             │
│  └─── ConfigurationService                                   │
├─────────────────────────────────────────────────────────────┤
│  Repositories (Data Access Layer)                            │
│  ├─── VehicleRepository                                      │
│  ├─── BrandRepository                                        │
│  ├─── VehicleTypeRepository                                  │
│  ├─── CustomizationCategoryRepository                        │
│  ├─── CustomizationOptionRepository                          │
│  └─── VehicleConfigurationRepository                         │
├─────────────────────────────────────────────────────────────┤
│  Security Layer                                              │
│  ├─── JwtAuthenticationFilter                                │
│  ├─── JwtUtil                                                │
│  └─── SecurityConfig                                         │
├─────────────────────────────────────────────────────────────┤
│  Domain Models (Entities)                                    │
│  ├─── Vehicle                                                │
│  ├─── Brand                                                  │
│  ├─── VehicleType                                            │
│  ├─── CustomizationCategory                                  │
│  ├─── CustomizationOption                                    │
│  └─── VehicleConfiguration                                   │
└─────────────────────────────────────────────────────────────┘
```

### Design Patterns Used

1. **Layered Architecture**: Clear separation of concerns (Controller → Service → Repository)
2. **DTO Pattern**: Data Transfer Objects for API requests/responses
3. **Repository Pattern**: Data access abstraction using Spring Data JPA
4. **Builder Pattern**: Used in entities for flexible object creation
5. **Mapper Pattern**: MapStruct for entity-DTO conversions
6. **Strategy Pattern**: Different search strategies for vehicle filtering

---

## 🛠️ Technology Stack

### Core Technologies
- **Java 17**: Programming language
- **Spring Boot 3.5.7**: Application framework
- **Spring Data JPA**: Data persistence
- **Hibernate 6.6**: ORM framework
- **PostgreSQL 16**: Relational database
- **Maven**: Build tool

### Security & Authentication
- **Spring Security 6**: Security framework
- **JWT (JJWT 0.11.5)**: Token-based authentication
- **BCrypt**: Password hashing (via User Service)

### Additional Libraries
- **Lombok**: Reduce boilerplate code
- **MapStruct 1.5.5**: Object mapping
- **SpringDoc OpenAPI 2.6.0**: API documentation (Swagger UI)
- **Jakarta Validation**: Request validation
- **Dotenv**: Environment variable management

### Development Tools
- **SLF4J + Logback**: Logging
- **HikariCP**: Database connection pooling
- **Jackson**: JSON serialization

---

## 🗄️ Database Schema

### Entity Relationship Diagram

```
┌──────────────┐         ┌──────────────────┐         ┌────────────────────┐
│   Brand      │         │     Vehicle      │         │   VehicleType      │
├──────────────┤         ├──────────────────┤         ├────────────────────┤
│ id (UUID)    │────────>│ id (UUID)        │<────────│ id (UUID)          │
│ name*        │ 1     n │ model*           │ n     1 │ name*              │
│ description  │         │ model_year*      │         │ description        │
│ logo_url     │         │ base_price*      │         │ icon_url           │
│ country      │         │ engine           │         │ created_at         │
│ founded_year │         │ transmission     │         │ updated_at         │
│ active       │         │ fuel_type        │         └────────────────────┘
│ created_at   │         │ horsepower       │
│ updated_at   │         │ seating_capacity │
└──────────────┘         │ cargo_capacity   │
                         │ fuel_efficiency  │
                         │ main_image_url   │
                         │ status*          │
                         │ stock_quantity   │
                         │ created_at       │
                         │ updated_at       │
                         └──────────────────┘
                                │
                                │ n
                                │
                                │ n
                         ┌──────────────────┐
                         │  VehicleImages   │
                         ├──────────────────┤
                         │ vehicle_id (FK)  │
                         │ image_url        │
                         └──────────────────┘

┌──────────────────────────┐         ┌────────────────────────┐
│ CustomizationCategory    │         │  CustomizationOption   │
├──────────────────────────┤         ├────────────────────────┤
│ id (UUID)                │────────>│ id (UUID)              │
│ name*                    │ 1     n │ category_id* (FK)      │
│ description              │         │ name*                  │
│ allows_multiple          │         │ description            │
│ display_order            │         │ price_adjustment       │
│ active                   │         │ color_code             │
│ created_at               │         │ image_url              │
│ updated_at               │         │ display_order          │
└──────────────────────────┘         │ active                 │
                                     │ created_at             │
                                     │ updated_at             │
                                     └────────────────────────┘
                                              │ n
                                              │
                         ┌────────────────────┴─────────────────────┐
                         │ m                                        │ m
                  ┌──────▼──────────────┐         ┌────────────────▼──────┐
                  │  Vehicle           │         │  VehicleConfiguration  │
                  │  Customization     │         │  SelectedOptions       │
                  │  Options (M:N)     │         │  (M:N)                 │
                  └────────────────────┘         └───────────────────────┘
                                                          │
                                                          │ n
                                                          │
                                                          │ 1
                         ┌────────────────────────────────▼────────┐
                         │    VehicleConfiguration                │
                         ├─────────────────────────────────────────┤
                         │ id (UUID)                               │
                         │ user_id* (UUID) - links to User Service│
                         │ vehicle_id* (FK)                        │
                         │ name                                    │
                         │ notes                                   │
                         │ status*                                 │
                         │ total_price                             │
                         │ ordered_at                              │
                         │ created_at                              │
                         │ updated_at                              │
                         └─────────────────────────────────────────┘
```

### Key Tables

#### 1. **vehicles**
   - Core vehicle information
   - Foreign keys: `brand_id`, `vehicle_type_id`
   - Indexes: brand, type, status, model_year, base_price

#### 2. **brands**
   - Vehicle manufacturers
   - Unique constraint on `name`
   - Indexes: name, active

#### 3. **vehicle_types**
   - Vehicle categories (SUV, Sedan, etc.)
   - Unique constraint on `name`

#### 4. **customization_categories**
   - Customization groups (Colors, Interiors, etc.)
   - Can allow multiple selections

#### 5. **customization_options**
   - Individual customization choices
   - Price adjustments (can be negative for discounts)
   - Foreign key: `category_id`

#### 6. **vehicle_configurations**
   - User's saved vehicle builds
   - Links to User Service via `user_id` (UUID)
   - Many-to-many with customization_options
   - Automatically calculates total price

---

## 📚 API Documentation

### Base URL
```
http://localhost:8082
```

### API Endpoints Overview

#### 🔓 Public Endpoints (No Authentication Required)

##### **Vehicles**
```
GET    /api/vehicles                     - Search/filter vehicles (paginated)
GET    /api/vehicles/{id}                - Get vehicle details
GET    /api/vehicles/{id}/customizations - Get vehicle with customization options
GET    /api/vehicles/model-years         - Get available model years
```

##### **Brands**
```
GET    /api/brands                       - Get all active brands
GET    /api/brands/{id}                  - Get brand details
GET    /api/brands/{id}/vehicles         - Get vehicles by brand (paginated)
```

##### **Vehicle Types**
```
GET    /api/vehicle-types                - Get all vehicle types
GET    /api/vehicle-types/{id}           - Get vehicle type details
GET    /api/vehicle-types/{id}/vehicles  - Get vehicles by type (paginated)
```

##### **Customization Categories**
```
GET    /api/customization/categories           - Get all categories
GET    /api/customization/categories/{id}      - Get category details
```

##### **Customization Options**
```
GET    /api/customization/options              - Get all options
GET    /api/customization/options/{id}         - Get option details
GET    /api/customization/options/category/{categoryId} - Get options by category
```

#### 🔒 Protected Endpoints (JWT Authentication Required)

##### **Vehicle Configurations**
```
GET    /api/configurations               - Get my configurations (paginated)
GET    /api/configurations/{id}          - Get configuration by ID
POST   /api/configurations               - Create new configuration
PUT    /api/configurations/{id}          - Update configuration
DELETE /api/configurations/{id}          - Delete configuration
POST   /api/configurations/{id}/order    - Place order for configuration
```

### Example API Requests

#### 1. Search Vehicles with Filters
```http
GET /api/vehicles?brandId=123e4567-e89b-12d3-a456-426614174000&minPrice=20000&maxPrice=50000&page=0&size=10
```

**Response:**
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "brand": {
        "id": "123e4567-e89b-12d3-a456-426614174000",
        "name": "Tesla",
        "logoUrl": "https://example.com/tesla-logo.png"
      },
      "vehicleType": {
        "id": "987fcdeb-51a2-43f8-9b8f-71c45612e123",
        "name": "Electric Sedan",
        "iconUrl": "https://example.com/sedan-icon.png"
      },
      "model": "Model 3",
      "modelYear": 2024,
      "description": "Premium electric sedan with autopilot",
      "basePrice": 42000.00,
      "engine": "Dual Motor AWD",
      "transmission": "Single-Speed Fixed Gear",
      "fuelType": "Electric",
      "horsepower": "480 hp",
      "seatingCapacity": 5,
      "cargoCapacityLiters": 561,
      "fuelEfficiency": "132 MPGe",
      "mainImageUrl": "https://example.com/model3-main.jpg",
      "status": "AVAILABLE",
      "stockQuantity": 15
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 45,
  "totalPages": 5
}
```

#### 2. Get Vehicle with Customizations
```http
GET /api/vehicles/550e8400-e29b-41d4-a716-446655440000/customizations
```

**Response:**
```json
{
  "vehicle": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "model": "Model 3",
    "basePrice": 42000.00
  },
  "customizationCategories": [
    {
      "id": "cat-001",
      "name": "Exterior Colors",
      "allowsMultiple": false,
      "options": [
        {
          "id": "opt-001",
          "name": "Pearl White Multi-Coat",
          "priceAdjustment": 0.00,
          "colorCode": "#FFFFFF",
          "imageUrl": "https://example.com/white.jpg"
        },
        {
          "id": "opt-002",
          "name": "Midnight Silver Metallic",
          "priceAdjustment": 1000.00,
          "colorCode": "#5C5D61",
          "imageUrl": "https://example.com/silver.jpg"
        }
      ]
    },
    {
      "id": "cat-002",
      "name": "Interior",
      "allowsMultiple": false,
      "options": [
        {
          "id": "opt-010",
          "name": "All Black",
          "priceAdjustment": 0.00
        },
        {
          "id": "opt-011",
          "name": "Black and White",
          "priceAdjustment": 1000.00
        }
      ]
    }
  ]
}
```

#### 3. Create Vehicle Configuration (Authenticated)
```http
POST /api/configurations
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "vehicleId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "My Dream Tesla",
  "notes": "Perfect configuration for daily commute",
  "selectedOptionIds": [
    "opt-002",  // Midnight Silver
    "opt-011",  // Black and White Interior
    "opt-020"   // Enhanced Autopilot
  ]
}
```

**Response:**
```json
{
  "id": "config-123",
  "userId": "user-456",
  "vehicle": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "model": "Model 3",
    "basePrice": 42000.00
  },
  "name": "My Dream Tesla",
  "notes": "Perfect configuration for daily commute",
  "status": "DRAFT",
  "selectedOptions": [
    {
      "id": "opt-002",
      "name": "Midnight Silver Metallic",
      "priceAdjustment": 1000.00
    },
    {
      "id": "opt-011",
      "name": "Black and White Interior",
      "priceAdjustment": 1000.00
    },
    {
      "id": "opt-020",
      "name": "Enhanced Autopilot",
      "priceAdjustment": 6000.00
    }
  ],
  "totalPrice": 50000.00,
  "createdAt": "2025-12-12T08:00:00Z",
  "updatedAt": "2025-12-12T08:00:00Z"
}
```

### Swagger UI
Access interactive API documentation at:
```
http://localhost:8082/swagger-ui.html
```

---

## 🧠 Business Logic

### 1. Vehicle Search & Filtering

**Location**: `VehicleService.java`

```java
public Page<VehicleResponse> searchVehicles(VehicleSearchParams params, Pageable pageable)
```

**Logic:**
- Builds dynamic queries based on provided filters
- Supports filtering by: brand, vehicle type, model year, price range, status
- Uses Spring Data JPA Specifications for flexible querying
- Returns paginated results for efficient data loading
- Applies indexes for optimal performance

**Use Cases:**
- Browse all available vehicles
- Filter by budget (price range)
- Find specific vehicle types (SUVs, Sedans)
- Search by brand preference
- View latest models (by year)

### 2. Vehicle Customization

**Location**: `VehicleService.java`

```java
public VehicleWithCustomizationsResponse getVehicleWithCustomizations(UUID vehicleId)
```

**Logic:**
- Fetches vehicle details
- Retrieves all applicable customization categories
- Groups options by category
- Filters only active options
- Respects category constraints (allows_multiple flag)

**Business Rules:**
- Some categories allow multiple selections (e.g., Tech Packages)
- Others allow single selection (e.g., Exterior Color)
- Inactive options are excluded from customer view
- Options are ordered by display_order for consistent UX

### 3. Configuration Creation & Price Calculation

**Location**: `ConfigurationService.java`

```java
public ConfigurationResponse createConfiguration(CreateConfigurationRequest request, UUID userId)
```

**Logic Flow:**
1. **Validate Vehicle**: Check if vehicle exists and is available
2. **Validate Options**: Verify all selected options exist and are active
3. **Check Compatibility**: Ensure options are valid for the selected vehicle
4. **Calculate Price**: 
   ```java
   totalPrice = vehicle.basePrice + Σ(option.priceAdjustment)
   ```
5. **Create Configuration**: Save configuration with DRAFT status
6. **Link to User**: Associate configuration with authenticated user

**Business Rules:**
- Vehicle must have status = "AVAILABLE"
- Vehicle must have sufficient stock (stock_quantity > 0)
- All selected options must be active
- Options must belong to categories applicable to the vehicle
- Price is calculated automatically and stored
- User can only access their own configurations

### 4. Configuration Ordering

**Location**: `ConfigurationService.java`

```java
public ConfigurationResponse orderConfiguration(UUID configId, UUID userId)
```

**Logic Flow:**
1. **Validate Ownership**: User must own the configuration
2. **Check Status**: Configuration must be in DRAFT status
3. **Verify Availability**: Re-check vehicle availability
4. **Update Stock**: Decrement vehicle stock_quantity
5. **Change Status**: Update configuration to ORDERED
6. **Set Timestamp**: Record ordered_at timestamp
7. **Notify User Service**: (Future) Trigger order notification

**Business Rules:**
- Only DRAFT configurations can be ordered
- Vehicle must still be available at order time
- Stock is decremented atomically (transaction-safe)
- Configuration becomes read-only after ordering
- Order timestamp is recorded for tracking

### 5. Brand & Type Management

**Location**: `BrandService.java`, `VehicleTypeService.java`

**Logic:**
- Manage vehicle brands and types
- Support activation/deactivation (soft delete)
- Track metadata (logos, icons, descriptions)
- Cascade to related vehicles

**Business Rules:**
- Brand names must be unique
- Type names must be unique
- Active flag allows hiding without deletion
- Deactivating a brand hides all its vehicles

---

## 🔐 Security & Authentication

### JWT-Based Authentication

The Vehicle Service implements **stateless JWT authentication** to secure endpoints.

#### How It Works

1. **User authenticates with User Service**:
   ```
   POST /api/auth/login → Returns JWT token
   ```

2. **Client includes JWT in requests**:
   ```
   Authorization: Bearer <jwt-token>
   ```

3. **Vehicle Service validates token**:
   - Extracts token from Authorization header
   - Verifies signature using shared secret
   - Validates expiration
   - Extracts user claims (userId, roles)
   - Sets Spring Security context

#### Security Components

**JwtAuthenticationFilter** (`security/JwtAuthenticationFilter.java`)
- Intercepts every HTTP request
- Validates JWT token
- Extracts user information
- Sets authentication context

**JwtUtil** (`security/JwtUtil.java`)
- Validates JWT signature
- Extracts claims (userId, username, roles)
- Checks token expiration
- Uses HMAC-SHA256 algorithm

**SecurityConfig** (`security/SecurityConfig.java`)
- Configures security rules
- Defines public vs. protected endpoints
- Disables CSRF (stateless API)
- Configures CORS for frontend integration

#### Protected Endpoints

Only `/api/configurations/**` endpoints require authentication.

**Why?**
- Vehicle browsing should be public (better SEO, user experience)
- Configurations contain personal user data
- Ordering requires verified user identity

#### JWT Token Structure

```json
{
  "sub": "user@example.com",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "roles": ["ROLE_USER"],
  "iat": 1702380000,
  "exp": 1702466400
}
```

**Claims:**
- `sub`: Username/email
- `userId`: Unique user identifier (used for configurations)
- `roles`: User authorities
- `iat`: Issued at timestamp
- `exp`: Expiration timestamp

---

## 🔗 Integration with User Service

The Vehicle Service integrates with User Service through **JWT tokens** and **user IDs**.

### Integration Architecture

```
┌─────────────────┐                    ┌─────────────────┐
│  User Service   │                    │ Vehicle Service │
│  (Port 8081)    │                    │  (Port 8082)    │
├─────────────────┤                    ├─────────────────┤
│                 │                    │                 │
│  User Signup/   │  1. Login Request  │                 │
│  Login          │<───────────────────│                 │
│                 │                    │                 │
│  Generate JWT   │  2. JWT Token      │                 │
│  Token          │────────────────────>│                 │
│                 │                    │                 │
│                 │  3. API Request    │                 │
│                 │     + JWT Token    │  Validate JWT   │
│                 │────────────────────>│  Extract userId │
│                 │                    │                 │
│                 │  4. Response       │  Access Control │
│                 │<────────────────────│  Based on userId│
│                 │                    │                 │
└─────────────────┘                    └─────────────────┘
```

### How They Work Together

#### 1. **User Authentication Flow**
   - User logs in via User Service
   - User Service validates credentials
   - User Service generates JWT with user details
   - Frontend stores JWT token

#### 2. **Configuration Creation Flow**
   - Frontend sends request to Vehicle Service with JWT
   - Vehicle Service validates JWT
   - Vehicle Service extracts `userId` from token
   - Configuration is saved with `userId` reference
   - No direct service-to-service call needed

#### 3. **Authorization Logic**
   ```java
   // ConfigurationController.java
   private UUID getCurrentUserId() {
       Authentication auth = SecurityContextHolder
           .getContext()
           .getAuthentication();
       return UUID.fromString(auth.getName());
   }
   ```
   - Extract userId from security context
   - Use userId to filter user-specific data
   - Ensure users only see their own configurations

#### 4. **Shared Security Configuration**
   - Both services share the **same JWT secret key**
   - Defined in `.env` file: `USER_SERVICE_JWT_SECRET`
   - Ensures Vehicle Service can validate tokens issued by User Service

### Key Integration Points

| Aspect | User Service | Vehicle Service |
|--------|-------------|-----------------|
| **JWT Generation** | ✅ Creates tokens | ❌ Only validates |
| **User Data** | ✅ Stores user profiles | ❌ Only stores userId reference |
| **Authentication** | ✅ Login/Signup | ❌ No auth endpoints |
| **Authorization** | ✅ User roles/permissions | ✅ Configuration ownership |
| **JWT Secret** | ✅ Uses to sign | ✅ Uses to verify |

### Why This Design?

**Benefits:**
- **Loose Coupling**: Services don't directly communicate
- **Scalability**: No synchronous dependencies
- **Performance**: No additional service calls for auth
- **Resilience**: Vehicle Service works even if User Service is down (for cached tokens)

**Limitations:**
- User details not immediately available in Vehicle Service
- No real-time user validation (until token expires)
- Requires manual user data sync if needed

### Future Enhancements

1. **Service-to-Service Communication**:
   - Call User Service to fetch user details for orders
   - Validate user status (active/blocked) on order placement

2. **Event-Driven Architecture**:
   - User Service publishes user events (user.created, user.deleted)
   - Vehicle Service subscribes and updates references

3. **API Gateway**:
   - Centralized authentication
   - Request routing
   - Rate limiting

---

## ⚙️ Configuration

### Application Profiles

#### **dev** (Development Profile)
- Local PostgreSQL database
- Detailed SQL logging
- Debug log level
- Port: 8082

#### **prod** (Production Profile)
- Production database
- Minimal logging
- Performance optimizations
- Environment-based configuration

### Environment Variables

**Database Configuration:**
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/bennycar_db?currentSchema=vehicle_service
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=bennyCar123
```

**Security Configuration:**
```bash
JWT_SECRET=dev-secret-key-change-this-in-production-min-256-bits-for-security
```

**Server Configuration:**
```bash
SERVER_PORT=8082
```

### Database Configuration

**Hibernate DDL Mode:**
- **dev**: `update` (auto-create/update tables)
- **prod**: `validate` (only validate schema)

**Connection Pool (HikariCP):**
- Maximum pool size: 10
- Minimum idle: 5
- Connection timeout: 30 seconds

**Schema:**
- All tables in `vehicle_service` schema
- Isolated from other services

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 16+
- Docker (optional, for database)

### Step 1: Start PostgreSQL

**Using Docker:**
```bash
cd /Users/syed/Documents/PSE/bennycar
docker-compose up postgres -d
```

**Manual Setup:**
```sql
CREATE DATABASE bennycar_db;
CREATE USER admin WITH PASSWORD 'bennyCar123';
CREATE SCHEMA vehicle_service;
GRANT ALL PRIVILEGES ON SCHEMA vehicle_service TO admin;
```

### Step 2: Configure Environment

Create `.env` file in project root:
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/bennycar_db?currentSchema=vehicle_service
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=bennyCar123
SERVER_PORT=8082
JWT_SECRET=your-secret-key-min-256-bits
```

### Step 3: Build the Project

```bash
cd vehicle-service
mvn clean install
```

### Step 4: Run the Service

**Development mode:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Production mode:**
```bash
java -jar target/vehicle-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Step 5: Verify Service is Running

```bash
# Health check
curl http://localhost:8082/actuator/health

# Get brands
curl http://localhost:8082/api/brands

# Swagger UI
open http://localhost:8082/swagger-ui.html
```

---

## 👨‍💻 Development Guide

### Project Structure

```
vehicle-service/
├── src/main/java/de/bennycar/vehicle/
│   ├── VehicleServiceApplication.java    # Main entry point
│   ├── config/                           # Configuration classes
│   │   ├── JpaConfig.java               # JPA/Auditing config
│   │   ├── OpenApiConfig.java           # Swagger config
│   │   └── DotEnvConfig.java            # Environment config
│   ├── constants/                        # Application constants
│   │   └── AppConstants.java
│   ├── controller/                       # REST controllers
│   │   ├── VehicleController.java
│   │   ├── BrandController.java
│   │   ├── VehicleTypeController.java
│   │   ├── CustomizationCategoryController.java
│   │   ├── CustomizationOptionController.java
│   │   └── ConfigurationController.java
│   ├── domain/                           # JPA entities
│   │   ├── Vehicle.java
│   │   ├── Brand.java
│   │   ├── VehicleType.java
│   │   ├── CustomizationCategory.java
│   │   ├── CustomizationOption.java
│   │   └── VehicleConfiguration.java
│   ├── dto/                              # Data Transfer Objects
│   │   ├── VehicleResponse.java
│   │   ├── VehicleSearchParams.java
│   │   ├── ConfigurationRequest.java
│   │   ├── ConfigurationResponse.java
│   │   └── ErrorResponse.java
│   ├── exception/                        # Custom exceptions
│   │   ├── ResourceNotFoundException.java
│   │   ├── VehicleNotAvailableException.java
│   │   ├── InvalidConfigurationException.java
│   │   └── GlobalExceptionHandler.java
│   ├── mapper/                           # MapStruct mappers
│   │   ├── VehicleMapper.java
│   │   ├── BrandMapper.java
│   │   └── ConfigurationMapper.java
│   ├── repository/                       # Spring Data repositories
│   │   ├── VehicleRepository.java
│   │   ├── BrandRepository.java
│   │   ├── VehicleTypeRepository.java
│   │   ├── CustomizationCategoryRepository.java
│   │   ├── CustomizationOptionRepository.java
│   │   └── VehicleConfigurationRepository.java
│   ├── security/                         # Security components
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtUtil.java
│   │   └── SecurityConfig.java
│   └── service/                          # Business logic
│       ├── VehicleService.java
│       ├── BrandService.java
│       ├── VehicleTypeService.java
│       ├── CustomizationCategoryService.java
│       ├── CustomizationOptionService.java
│       └── ConfigurationService.java
├── src/main/resources/
│   ├── application.yml                   # Main configuration
│   ├── application-dev.yml              # Dev profile config
│   └── application-prod.yml             # Prod profile config
└── pom.xml                              # Maven dependencies
```

### Adding a New Feature

#### Example: Add Vehicle Reviews

1. **Create Entity** (`domain/VehicleReview.java`)
2. **Create Repository** (`repository/VehicleReviewRepository.java`)
3. **Create DTOs** (`dto/ReviewRequest.java`, `dto/ReviewResponse.java`)
4. **Create Mapper** (`mapper/ReviewMapper.java`)
5. **Create Service** (`service/ReviewService.java`)
6. **Create Controller** (`controller/ReviewController.java`)
7. **Add Tests** (unit + integration)

### Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report

# Integration tests only
mvn verify -Pintegration-tests
```

### Code Quality

```bash
# Run SonarQube analysis
mvn sonar:sonar

# Check code style
mvn checkstyle:check
```

### Database Migrations

For production, use Flyway or Liquibase:

1. **Add Flyway dependency** to `pom.xml`
2. **Create migration scripts** in `src/main/resources/db/migration/`
3. **Set `hibernate.ddl-auto=validate`** in production

---

## 📊 Monitoring & Observability

### Actuator Endpoints

- **Health**: `/actuator/health`
- **Info**: `/actuator/info`
- **Metrics**: `/actuator/metrics`
- **Prometheus**: `/actuator/prometheus`

### Logging

**Log Levels:**
- Production: `INFO`
- Development: `DEBUG`
- Hibernate SQL: `DEBUG` (dev only)

**Log Format:**
```
2025-12-12T08:00:00.123+01:00 INFO [vehicle-service] [main] d.b.vehicle.VehicleServiceApplication : Starting application...
```

---

## 📝 Best Practices

1. **Always use DTOs** for API requests/responses (never expose entities)
2. **Validate input** using Jakarta Validation annotations
3. **Handle exceptions** gracefully with GlobalExceptionHandler
4. **Use transactions** for multi-step operations
5. **Log important operations** (especially orders and price calculations)
6. **Write tests** for business logic
7. **Document APIs** with OpenAPI annotations
8. **Use pagination** for list endpoints
9. **Index database columns** used in WHERE/JOIN clauses
10. **Never hardcode secrets** (use environment variables)

---

## 🐛 Troubleshooting

### Common Issues

**Problem**: Port 8082 already in use
```bash
# Solution: Kill the process
lsof -ti:8082 | xargs kill -9
```

**Problem**: Schema "vehicle_service" does not exist
```bash
# Solution: Create schema manually
docker exec -it bennycar-postgres psql -U admin -d bennycar_db
CREATE SCHEMA vehicle_service;
GRANT ALL PRIVILEGES ON SCHEMA vehicle_service TO admin;
```

**Problem**: JWT validation fails
```bash
# Solution: Ensure same JWT_SECRET in both services
# Check .env file and restart services
```

**Problem**: Connection pool exhausted
```bash
# Solution: Increase pool size in application.yml
spring.datasource.hikari.maximum-pool-size=20
```

---

## 📞 Support & Contact

For questions or issues:
- Check API documentation: `http://localhost:8082/swagger-ui.html`
- Review logs: Check application logs for detailed error messages
- Database issues: Verify PostgreSQL connection and schema

---

## 📄 License

This project is part of the BennyCar platform - Vehicle Service Module.

---

**Last Updated**: December 12, 2025  
**Version**: 0.0.1-SNAPSHOT  
**Service Port**: 8082

