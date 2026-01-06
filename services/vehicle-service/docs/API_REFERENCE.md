# Vehicle Service - Complete API Reference

## Table of Contents
- [Authentication](#authentication)
- [Vehicles API](#vehicles-api)
- [Brands API](#brands-api)
- [Vehicle Types API](#vehicle-types-api)
- [Customization API](#customization-api)
- [Configurations API](#configurations-api)
- [Error Responses](#error-responses)
- [Pagination](#pagination)

---

## Authentication

### JWT Token Format

All protected endpoints require a JWT token in the Authorization header:

```http
Authorization: Bearer <jwt-token>
```

### Getting a JWT Token

Tokens are issued by the User Service:

```http
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

---

## Vehicles API

### 1. Search Vehicles

Search and filter vehicles with pagination.

**Endpoint:** `GET /api/vehicles`

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `brandId` | UUID | No | Filter by brand |
| `vehicleTypeId` | UUID | No | Filter by vehicle type |
| `modelYear` | Integer | No | Filter by model year |
| `minPrice` | Decimal | No | Minimum price filter |
| `maxPrice` | Decimal | No | Maximum price filter |
| `status` | String | No | Vehicle status (AVAILABLE, SOLD_OUT, DISCONTINUED) |
| `page` | Integer | No | Page number (default: 0) |
| `size` | Integer | No | Page size (default: 20) |
| `sort` | String | No | Sort field and direction (e.g., "basePrice,asc") |

**Example Request:**
```http
GET /api/vehicles?brandId=123e4567-e89b-12d3-a456-426614174000&minPrice=20000&maxPrice=50000&page=0&size=10&sort=basePrice,asc
```

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "brand": {
        "id": "123e4567-e89b-12d3-a456-426614174000",
        "name": "Tesla",
        "logoUrl": "https://cdn.example.com/logos/tesla.png",
        "countryOfOrigin": "USA"
      },
      "vehicleType": {
        "id": "987fcdeb-51a2-43f8-9b8f-71c45612e123",
        "name": "Electric Sedan",
        "iconUrl": "https://cdn.example.com/icons/sedan.png"
      },
      "model": "Model 3",
      "modelYear": 2024,
      "description": "Premium electric sedan with advanced autopilot capabilities",
      "basePrice": 42000.00,
      "engine": "Dual Motor AWD",
      "transmission": "Single-Speed Fixed Gear",
      "fuelType": "Electric",
      "horsepower": "480 hp",
      "seatingCapacity": 5,
      "cargoCapacityLiters": 561,
      "fuelEfficiency": "132 MPGe",
      "mainImageUrl": "https://cdn.example.com/vehicles/model3-main.jpg",
      "additionalImages": [
        "https://cdn.example.com/vehicles/model3-1.jpg",
        "https://cdn.example.com/vehicles/model3-2.jpg",
        "https://cdn.example.com/vehicles/model3-3.jpg"
      ],
      "status": "AVAILABLE",
      "stockQuantity": 15,
      "createdAt": "2025-01-15T10:00:00Z",
      "updatedAt": "2025-12-10T14:30:00Z"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 0,
    "pageSize": 10,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 45,
  "totalPages": 5,
  "last": false,
  "first": true,
  "size": 10,
  "number": 0,
  "numberOfElements": 10,
  "empty": false
}
```

---

### 2. Get Vehicle by ID

Retrieve detailed information about a specific vehicle.

**Endpoint:** `GET /api/vehicles/{id}`

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | UUID | Yes | Vehicle ID |

**Example Request:**
```http
GET /api/vehicles/550e8400-e29b-41d4-a716-446655440000
```

**Response:** `200 OK`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "brand": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "Tesla",
    "description": "Leading electric vehicle manufacturer",
    "logoUrl": "https://cdn.example.com/logos/tesla.png",
    "countryOfOrigin": "USA",
    "foundedYear": 2003
  },
  "vehicleType": {
    "id": "987fcdeb-51a2-43f8-9b8f-71c45612e123",
    "name": "Electric Sedan",
    "description": "Fully electric four-door sedan",
    "iconUrl": "https://cdn.example.com/icons/sedan.png"
  },
  "model": "Model 3",
  "modelYear": 2024,
  "description": "Premium electric sedan with advanced autopilot capabilities and impressive range",
  "basePrice": 42000.00,
  "engine": "Dual Motor AWD",
  "transmission": "Single-Speed Fixed Gear",
  "fuelType": "Electric",
  "horsepower": "480 hp",
  "seatingCapacity": 5,
  "cargoCapacityLiters": 561,
  "fuelEfficiency": "132 MPGe",
  "mainImageUrl": "https://cdn.example.com/vehicles/model3-main.jpg",
  "additionalImages": [
    "https://cdn.example.com/vehicles/model3-interior.jpg",
    "https://cdn.example.com/vehicles/model3-exterior-1.jpg",
    "https://cdn.example.com/vehicles/model3-exterior-2.jpg",
    "https://cdn.example.com/vehicles/model3-trunk.jpg"
  ],
  "status": "AVAILABLE",
  "stockQuantity": 15,
  "createdAt": "2025-01-15T10:00:00Z",
  "updatedAt": "2025-12-10T14:30:00Z"
}
```

**Error Response:** `404 Not Found`
```json
{
  "timestamp": "2025-12-12T08:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Vehicle not found with id: 550e8400-e29b-41d4-a716-446655440000",
  "path": "/api/vehicles/550e8400-e29b-41d4-a716-446655440000"
}
```

---

### 3. Get Vehicle with Customizations

Retrieve a vehicle with all available customization options.

**Endpoint:** `GET /api/vehicles/{id}/customizations`

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | UUID | Yes | Vehicle ID |

**Example Request:**
```http
GET /api/vehicles/550e8400-e29b-41d4-a716-446655440000/customizations
```

**Response:** `200 OK`
```json
{
  "vehicle": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "brand": {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "name": "Tesla"
    },
    "model": "Model 3",
    "modelYear": 2024,
    "basePrice": 42000.00,
    "mainImageUrl": "https://cdn.example.com/vehicles/model3-main.jpg",
    "status": "AVAILABLE"
  },
  "customizationCategories": [
    {
      "id": "cat-001",
      "name": "Exterior Colors",
      "description": "Choose your vehicle's exterior paint color",
      "allowsMultiple": false,
      "displayOrder": 1,
      "options": [
        {
          "id": "opt-001",
          "name": "Pearl White Multi-Coat",
          "description": "Premium pearl white finish",
          "priceAdjustment": 0.00,
          "colorCode": "#FFFFFF",
          "imageUrl": "https://cdn.example.com/colors/pearl-white.jpg",
          "displayOrder": 1,
          "active": true
        },
        {
          "id": "opt-002",
          "name": "Midnight Silver Metallic",
          "description": "Sleek silver metallic paint",
          "priceAdjustment": 1000.00,
          "colorCode": "#5C5D61",
          "imageUrl": "https://cdn.example.com/colors/midnight-silver.jpg",
          "displayOrder": 2,
          "active": true
        },
        {
          "id": "opt-003",
          "name": "Deep Blue Metallic",
          "description": "Rich blue metallic finish",
          "priceAdjustment": 1000.00,
          "colorCode": "#0F3C5C",
          "imageUrl": "https://cdn.example.com/colors/deep-blue.jpg",
          "displayOrder": 3,
          "active": true
        },
        {
          "id": "opt-004",
          "name": "Red Multi-Coat",
          "description": "Stunning red multi-coat paint",
          "priceAdjustment": 2000.00,
          "colorCode": "#C41E3A",
          "imageUrl": "https://cdn.example.com/colors/red.jpg",
          "displayOrder": 4,
          "active": true
        }
      ]
    },
    {
      "id": "cat-002",
      "name": "Interior",
      "description": "Select your interior design and color",
      "allowsMultiple": false,
      "displayOrder": 2,
      "options": [
        {
          "id": "opt-010",
          "name": "All Black",
          "description": "Classic all-black interior",
          "priceAdjustment": 0.00,
          "imageUrl": "https://cdn.example.com/interiors/black.jpg",
          "displayOrder": 1,
          "active": true
        },
        {
          "id": "opt-011",
          "name": "Black and White",
          "description": "Premium black and white interior",
          "priceAdjustment": 1000.00,
          "imageUrl": "https://cdn.example.com/interiors/black-white.jpg",
          "displayOrder": 2,
          "active": true
        }
      ]
    },
    {
      "id": "cat-003",
      "name": "Wheels",
      "description": "Choose your wheel design",
      "allowsMultiple": false,
      "displayOrder": 3,
      "options": [
        {
          "id": "opt-020",
          "name": "18\" Aero Wheels",
          "description": "Standard aerodynamic wheels",
          "priceAdjustment": 0.00,
          "imageUrl": "https://cdn.example.com/wheels/18-aero.jpg",
          "displayOrder": 1,
          "active": true
        },
        {
          "id": "opt-021",
          "name": "19\" Sport Wheels",
          "description": "Upgraded sport wheels",
          "priceAdjustment": 1500.00,
          "imageUrl": "https://cdn.example.com/wheels/19-sport.jpg",
          "displayOrder": 2,
          "active": true
        }
      ]
    },
    {
      "id": "cat-004",
      "name": "Autopilot",
      "description": "Advanced driver assistance features",
      "allowsMultiple": true,
      "displayOrder": 4,
      "options": [
        {
          "id": "opt-030",
          "name": "Enhanced Autopilot",
          "description": "Navigate on Autopilot, Auto Lane Change, Autopark, Summon",
          "priceAdjustment": 6000.00,
          "displayOrder": 1,
          "active": true
        },
        {
          "id": "opt-031",
          "name": "Full Self-Driving Capability",
          "description": "All Enhanced Autopilot features plus Traffic Light and Stop Sign Control",
          "priceAdjustment": 12000.00,
          "displayOrder": 2,
          "active": true
        }
      ]
    }
  ]
}
```

---

### 4. Get Available Model Years

Get a list of distinct model years for available vehicles.

**Endpoint:** `GET /api/vehicles/model-years`

**Example Request:**
```http
GET /api/vehicles/model-years
```

**Response:** `200 OK`
```json
[2024, 2023, 2022, 2021]
```

---

## Brands API

### 1. Get All Brands

Retrieve all active vehicle brands.

**Endpoint:** `GET /api/brands`

**Example Request:**
```http
GET /api/brands
```

**Response:** `200 OK`
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "Tesla",
    "description": "American electric vehicle and clean energy company",
    "logoUrl": "https://cdn.example.com/logos/tesla.png",
    "countryOfOrigin": "USA",
    "foundedYear": 2003,
    "active": true
  },
  {
    "id": "234e5678-f90c-23d4-b567-537725285111",
    "name": "BMW",
    "description": "German luxury vehicle manufacturer",
    "logoUrl": "https://cdn.example.com/logos/bmw.png",
    "countryOfOrigin": "Germany",
    "foundedYear": 1916,
    "active": true
  }
]
```

---

### 2. Get Brand by ID

Retrieve a specific brand by its ID.

**Endpoint:** `GET /api/brands/{id}`

**Response:** `200 OK`
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Tesla",
  "description": "American electric vehicle and clean energy company specializing in electric cars and battery energy storage",
  "logoUrl": "https://cdn.example.com/logos/tesla.png",
  "countryOfOrigin": "USA",
  "foundedYear": 2003,
  "active": true,
  "createdAt": "2025-01-01T00:00:00Z",
  "updatedAt": "2025-12-01T10:00:00Z"
}
```

---

### 3. Get Vehicles by Brand

Get all vehicles for a specific brand with pagination.

**Endpoint:** `GET /api/brands/{id}/vehicles`

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `page` | Integer | No | Page number (default: 0) |
| `size` | Integer | No | Page size (default: 20) |

**Example Request:**
```http
GET /api/brands/123e4567-e89b-12d3-a456-426614174000/vehicles?page=0&size=10
```

**Response:** `200 OK` (Same structure as Search Vehicles)

---

## Vehicle Types API

### 1. Get All Vehicle Types

Retrieve all vehicle types.

**Endpoint:** `GET /api/vehicle-types`

**Example Request:**
```http
GET /api/vehicle-types
```

**Response:** `200 OK`
```json
[
  {
    "id": "987fcdeb-51a2-43f8-9b8f-71c45612e123",
    "name": "Electric Sedan",
    "description": "Fully electric four-door sedan vehicles",
    "iconUrl": "https://cdn.example.com/icons/electric-sedan.png"
  },
  {
    "id": "876fedcb-42a1-34f7-8a7e-62b34501d234",
    "name": "SUV",
    "description": "Sport Utility Vehicles with spacious interiors",
    "iconUrl": "https://cdn.example.com/icons/suv.png"
  },
  {
    "id": "765edcba-31a0-23e6-7996-51a23490c345",
    "name": "Truck",
    "description": "Pickup trucks for work and recreation",
    "iconUrl": "https://cdn.example.com/icons/truck.png"
  }
]
```

---

### 2. Get Vehicles by Type

Get all vehicles of a specific type with pagination.

**Endpoint:** `GET /api/vehicle-types/{id}/vehicles`

**Response:** `200 OK` (Same structure as Search Vehicles)

---

## Customization API

### 1. Get All Customization Categories

Retrieve all customization categories with their options.

**Endpoint:** `GET /api/customization/categories`

**Response:** `200 OK`
```json
[
  {
    "id": "cat-001",
    "name": "Exterior Colors",
    "description": "Choose your vehicle's exterior paint color",
    "allowsMultiple": false,
    "displayOrder": 1,
    "active": true,
    "optionsCount": 5
  },
  {
    "id": "cat-002",
    "name": "Interior",
    "description": "Select your interior design and color",
    "allowsMultiple": false,
    "displayOrder": 2,
    "active": true,
    "optionsCount": 3
  }
]
```

---

### 2. Get Customization Category by ID

Retrieve a specific category with all its options.

**Endpoint:** `GET /api/customization/categories/{id}`

**Response:** `200 OK`
```json
{
  "id": "cat-001",
  "name": "Exterior Colors",
  "description": "Choose your vehicle's exterior paint color",
  "allowsMultiple": false,
  "displayOrder": 1,
  "active": true,
  "options": [
    {
      "id": "opt-001",
      "name": "Pearl White Multi-Coat",
      "description": "Premium pearl white finish",
      "priceAdjustment": 0.00,
      "colorCode": "#FFFFFF",
      "imageUrl": "https://cdn.example.com/colors/pearl-white.jpg",
      "displayOrder": 1,
      "active": true
    }
  ]
}
```

---

### 3. Get Options by Category

Retrieve all options for a specific category.

**Endpoint:** `GET /api/customization/options/category/{categoryId}`

**Response:** `200 OK`
```json
[
  {
    "id": "opt-001",
    "categoryId": "cat-001",
    "categoryName": "Exterior Colors",
    "name": "Pearl White Multi-Coat",
    "description": "Premium pearl white finish",
    "priceAdjustment": 0.00,
    "colorCode": "#FFFFFF",
    "imageUrl": "https://cdn.example.com/colors/pearl-white.jpg",
    "displayOrder": 1,
    "active": true
  }
]
```

---

## Configurations API

**🔒 All configuration endpoints require JWT authentication.**

### 1. Get My Configurations

Retrieve all configurations for the authenticated user.

**Endpoint:** `GET /api/configurations`

**Headers:**
```http
Authorization: Bearer <jwt-token>
```

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `page` | Integer | No | Page number (default: 0) |
| `size` | Integer | No | Page size (default: 10) |

**Example Request:**
```http
GET /api/configurations?page=0&size=10
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": "config-123",
      "userId": "user-456",
      "vehicle": {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "brand": {
          "name": "Tesla"
        },
        "model": "Model 3",
        "modelYear": 2024,
        "basePrice": 42000.00,
        "mainImageUrl": "https://cdn.example.com/vehicles/model3-main.jpg"
      },
      "name": "My Dream Tesla",
      "notes": "Perfect configuration for daily commute",
      "status": "DRAFT",
      "selectedOptions": [
        {
          "id": "opt-002",
          "categoryName": "Exterior Colors",
          "name": "Midnight Silver Metallic",
          "priceAdjustment": 1000.00
        },
        {
          "id": "opt-011",
          "categoryName": "Interior",
          "name": "Black and White",
          "priceAdjustment": 1000.00
        },
        {
          "id": "opt-030",
          "categoryName": "Autopilot",
          "name": "Enhanced Autopilot",
          "priceAdjustment": 6000.00
        }
      ],
      "totalPrice": 50000.00,
      "createdAt": "2025-12-10T10:00:00Z",
      "updatedAt": "2025-12-11T15:30:00Z"
    }
  ],
  "totalElements": 3,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

---

### 2. Get Configuration by ID

Retrieve a specific configuration.

**Endpoint:** `GET /api/configurations/{id}`

**Headers:**
```http
Authorization: Bearer <jwt-token>
```

**Response:** `200 OK`
```json
{
  "id": "config-123",
  "userId": "user-456",
  "vehicle": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "brand": {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "name": "Tesla",
      "logoUrl": "https://cdn.example.com/logos/tesla.png"
    },
    "model": "Model 3",
    "modelYear": 2024,
    "basePrice": 42000.00,
    "mainImageUrl": "https://cdn.example.com/vehicles/model3-main.jpg",
    "status": "AVAILABLE"
  },
  "name": "My Dream Tesla",
  "notes": "Perfect configuration for daily commute",
  "status": "DRAFT",
  "selectedOptions": [
    {
      "id": "opt-002",
      "categoryId": "cat-001",
      "categoryName": "Exterior Colors",
      "name": "Midnight Silver Metallic",
      "description": "Sleek silver metallic paint",
      "priceAdjustment": 1000.00,
      "colorCode": "#5C5D61",
      "imageUrl": "https://cdn.example.com/colors/midnight-silver.jpg"
    },
    {
      "id": "opt-011",
      "categoryId": "cat-002",
      "categoryName": "Interior",
      "name": "Black and White",
      "description": "Premium black and white interior",
      "priceAdjustment": 1000.00,
      "imageUrl": "https://cdn.example.com/interiors/black-white.jpg"
    },
    {
      "id": "opt-030",
      "categoryId": "cat-004",
      "categoryName": "Autopilot",
      "name": "Enhanced Autopilot",
      "description": "Navigate on Autopilot, Auto Lane Change, Autopark, Summon",
      "priceAdjustment": 6000.00
    }
  ],
  "totalPrice": 50000.00,
  "createdAt": "2025-12-10T10:00:00Z",
  "updatedAt": "2025-12-11T15:30:00Z",
  "orderedAt": null
}
```

---

### 3. Create Configuration

Create a new vehicle configuration.

**Endpoint:** `POST /api/configurations`

**Headers:**
```http
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "vehicleId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "My Dream Tesla",
  "notes": "Perfect configuration for daily commute",
  "selectedOptionIds": [
    "opt-002",
    "opt-011",
    "opt-030"
  ]
}
```

**Response:** `201 Created`
```json
{
  "id": "config-789",
  "userId": "user-456",
  "vehicle": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "model": "Model 3",
    "basePrice": 42000.00
  },
  "name": "My Dream Tesla",
  "notes": "Perfect configuration for daily commute",
  "status": "DRAFT",
  "selectedOptions": [...],
  "totalPrice": 50000.00,
  "createdAt": "2025-12-12T08:00:00Z",
  "updatedAt": "2025-12-12T08:00:00Z"
}
```

**Error Responses:**

`400 Bad Request` - Invalid request data
```json
{
  "timestamp": "2025-12-12T08:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Vehicle ID is required",
  "path": "/api/configurations"
}
```

`404 Not Found` - Vehicle not found
```json
{
  "timestamp": "2025-12-12T08:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Vehicle not found with id: 550e8400-e29b-41d4-a716-446655440000",
  "path": "/api/configurations"
}
```

`409 Conflict` - Vehicle not available
```json
{
  "timestamp": "2025-12-12T08:00:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Vehicle is not available. Current status: SOLD_OUT",
  "path": "/api/configurations"
}
```

---

### 4. Update Configuration

Update an existing configuration.

**Endpoint:** `PUT /api/configurations/{id}`

**Headers:**
```http
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Updated Configuration Name",
  "notes": "Updated notes",
  "selectedOptionIds": [
    "opt-003",
    "opt-011",
    "opt-021"
  ]
}
```

**Response:** `200 OK` (Same structure as Get Configuration)

---

### 5. Delete Configuration

Delete a configuration.

**Endpoint:** `DELETE /api/configurations/{id}`

**Headers:**
```http
Authorization: Bearer <jwt-token>
```

**Response:** `204 No Content`

**Error Response:** `404 Not Found`

---

### 6. Order Configuration

Place an order for a configuration.

**Endpoint:** `POST /api/configurations/{id}/order`

**Headers:**
```http
Authorization: Bearer <jwt-token>
```

**Response:** `200 OK`
```json
{
  "id": "config-123",
  "userId": "user-456",
  "vehicle": {...},
  "name": "My Dream Tesla",
  "status": "ORDERED",
  "totalPrice": 50000.00,
  "orderedAt": "2025-12-12T08:00:00Z",
  "createdAt": "2025-12-10T10:00:00Z",
  "updatedAt": "2025-12-12T08:00:00Z"
}
```

**Error Responses:**

`409 Conflict` - Configuration already ordered
```json
{
  "timestamp": "2025-12-12T08:00:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Configuration is already ordered",
  "path": "/api/configurations/config-123/order"
}
```

`409 Conflict` - Vehicle no longer available
```json
{
  "timestamp": "2025-12-12T08:00:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Vehicle is no longer available for order",
  "path": "/api/configurations/config-123/order"
}
```

---

## Error Responses

### Standard Error Format

All errors follow this structure:

```json
{
  "timestamp": "2025-12-12T08:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found with id: 123",
  "path": "/api/vehicles/123"
}
```

### Common HTTP Status Codes

| Status Code | Description | Common Causes |
|-------------|-------------|---------------|
| `200 OK` | Request successful | Successful GET/PUT |
| `201 Created` | Resource created | Successful POST |
| `204 No Content` | Successful deletion | Successful DELETE |
| `400 Bad Request` | Invalid request data | Validation errors, malformed JSON |
| `401 Unauthorized` | Missing/invalid token | No JWT token, expired token |
| `403 Forbidden` | Insufficient permissions | Accessing other user's data |
| `404 Not Found` | Resource not found | Invalid ID |
| `409 Conflict` | Business rule violation | Vehicle unavailable, duplicate resource |
| `500 Internal Server Error` | Server error | Unexpected errors |

---

## Pagination

All paginated endpoints support these query parameters:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | Integer | 0 | Page number (0-indexed) |
| `size` | Integer | 20 | Number of items per page |
| `sort` | String | - | Sort criteria (field,direction) |

### Sort Examples

```http
# Sort by price ascending
?sort=basePrice,asc

# Sort by name descending
?sort=name,desc

# Multiple sort criteria
?sort=basePrice,asc&sort=modelYear,desc
```

### Pagination Response Structure

```json
{
  "content": [...],          // Array of items
  "pageable": {
    "pageNumber": 0,         // Current page
    "pageSize": 20,          // Items per page
    "offset": 0              // Offset from start
  },
  "totalElements": 100,      // Total items
  "totalPages": 5,           // Total pages
  "last": false,             // Is last page?
  "first": true,             // Is first page?
  "size": 20,                // Page size
  "number": 0,               // Page number
  "numberOfElements": 20,    // Items in current page
  "empty": false             // Is empty?
}
```

---

**API Version**: 1.0  
**Base URL**: `http://localhost:8082`  
**Documentation**: `http://localhost:8082/swagger-ui.html`

