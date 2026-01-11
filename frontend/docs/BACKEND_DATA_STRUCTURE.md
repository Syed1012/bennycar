# Backend Data Structure Requirements for Browse Cars Page

This document outlines the data structure that the backend (Vehicle Service) should provide for the Browse Cars page to function optimally.

## API Endpoints Required

### 1. Search/Filter Vehicles
**Endpoint:** `GET /api/v1/vehicles/search`

**Query Parameters:**
```typescript
{
  search?: string;              // Search by brand, model, or type
  brandId?: string;             // Filter by brand ID
  vehicleTypeId?: string;       // Filter by vehicle type ID
  modelYear?: number;           // Filter by model year
  minPrice?: number;            // Minimum price filter
  maxPrice?: number;            // Maximum price filter
  status?: "AVAILABLE" | "SOLD_OUT" | "DISCONTINUED";
  page?: number;                // Page number (0-indexed)
  size?: number;                // Page size (default: 12)
  sort?: string;                // Sort field (e.g., "basePrice,asc", "modelYear,desc")
}
```

**Response Structure:**
```typescript
{
  content: Vehicle[];
  pageable: {
    sort: {
      sorted: boolean;
      unsorted: boolean;
      empty: boolean;
    };
    pageNumber: number;
    pageSize: number;
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  size: number;
  number: number;
  numberOfElements: number;
  empty: boolean;
}
```

### 2. Get All Brands
**Endpoint:** `GET /api/v1/brands`

**Response:**
```typescript
Brand[]
```

### 3. Get All Vehicle Types
**Endpoint:** `GET /api/v1/vehicle-types`

**Response:**
```typescript
VehicleType[]
```

## Data Models

### Vehicle
```typescript
{
  id: string;                    // UUID
  brand: Brand;                  // Brand object (see below)
  vehicleType: VehicleType;       // Vehicle type object (see below)
  model: string;                 // Model name (e.g., "Camry", "Model S")
  modelYear: number;             // Year (e.g., 2024)
  description: string;            // Full description
  basePrice: number;             // Starting price in currency units
  engine: string;                 // Engine description (e.g., "3.5L V6")
  transmission: string;           // Transmission type (e.g., "Automatic", "Manual")
  fuelType: string;               // Fuel type (e.g., "Gasoline", "Electric", "Hybrid")
  horsepower: string;             // Power output (e.g., "350 HP", "500 kW")
  seatingCapacity: number;        // Number of seats
  cargoCapacityLiters: number;    // Cargo space in liters
  fuelEfficiency: string;        // Efficiency (e.g., "25 MPG", "4.5L/100km")
  mainImageUrl: string;          // Primary image URL (required for display)
  additionalImages: string[];     // Array of additional image URLs
  status: "AVAILABLE" | "SOLD_OUT" | "DISCONTINUED";
  stockQuantity: number;          // Available stock count
  createdAt: string;              // ISO 8601 timestamp
  updatedAt: string;              // ISO 8601 timestamp
}
```

### Brand
```typescript
{
  id: string;                    // UUID
  name: string;                  // Brand name (e.g., "Toyota", "Tesla")
  description?: string;          // Optional brand description
  logoUrl?: string;              // Optional brand logo URL
  countryOfOrigin?: string;      // Optional country
  foundedYear?: number;          // Optional founding year
}
```

### VehicleType
```typescript
{
  id: string;                    // UUID
  name: string;                   // Type name (e.g., "Sedan", "SUV", "Sports Car")
  description?: string;           // Optional description
  iconUrl?: string;              // Optional icon URL
}
```

## Important Notes for Backend Implementation

### 1. Image Handling
- **mainImageUrl** is **required** - The UI heavily relies on this for the card display
- If no image is available, provide a placeholder or default image URL
- Images should be optimized and served via CDN if possible
- Recommended image dimensions: 800x600px (4:3 aspect ratio)

### 2. Search Functionality
- The `search` parameter should search across:
  - Brand name
  - Vehicle model
  - Vehicle type name
  - Description (optional, for better results)
- Use case-insensitive matching
- Consider fuzzy matching for better UX

### 3. Filtering
- All filters should work in combination (AND logic)
- Empty/null filters should be ignored (show all)
- Price filters should be inclusive (minPrice <= price <= maxPrice)

### 4. Pagination
- Default page size: 12 items per page
- Page numbers are 0-indexed
- Return accurate `totalElements` and `totalPages` for pagination UI

### 5. Sorting
- Default sort: Most relevant or newest first
- Common sort options:
  - `basePrice,asc` - Price: Low to High
  - `basePrice,desc` - Price: High to Low
  - `modelYear,desc` - Newest First
  - `modelYear,asc` - Oldest First

### 6. Status Handling
- `AVAILABLE`: Vehicle is in stock and available for purchase
- `SOLD_OUT`: Temporarily out of stock
- `DISCONTINUED`: No longer available

### 7. Performance Considerations
- Implement caching for brands and vehicle types (they change infrequently)
- Use database indexes on frequently filtered fields:
  - `brand_id`
  - `vehicle_type_id`
  - `model_year`
  - `base_price`
  - `status`
- Consider implementing search using full-text search (PostgreSQL, Elasticsearch)

### 8. Error Handling
- Return appropriate HTTP status codes:
  - `200 OK` - Success
  - `400 Bad Request` - Invalid query parameters
  - `500 Internal Server Error` - Server errors
- Include error messages in response body for debugging

## Example API Response

```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "brand": {
        "id": "123e4567-e89b-12d3-a456-426614174000",
        "name": "Tesla",
        "logoUrl": "https://example.com/logos/tesla.png"
      },
      "vehicleType": {
        "id": "223e4567-e89b-12d3-a456-426614174001",
        "name": "Sedan"
      },
      "model": "Model S",
      "modelYear": 2024,
      "description": "Luxury electric sedan with cutting-edge technology...",
      "basePrice": 79990.00,
      "engine": "Dual Motor",
      "transmission": "Single-Speed",
      "fuelType": "Electric",
      "horsepower": "670 HP",
      "seatingCapacity": 5,
      "cargoCapacityLiters": 709,
      "fuelEfficiency": "120 MPGe",
      "mainImageUrl": "https://example.com/vehicles/tesla-model-s.jpg",
      "additionalImages": [
        "https://example.com/vehicles/tesla-model-s-2.jpg",
        "https://example.com/vehicles/tesla-model-s-3.jpg"
      ],
      "status": "AVAILABLE",
      "stockQuantity": 15,
      "createdAt": "2024-01-15T10:30:00Z",
      "updatedAt": "2024-01-20T14:45:00Z"
    }
  ],
  "totalElements": 45,
  "totalPages": 4,
  "number": 0,
  "size": 12,
  "first": true,
  "last": false
}
```

## Frontend Integration Points

The frontend expects:
1. **Immediate response** - No long loading times
2. **Consistent data structure** - All vehicles should have the same fields
3. **Image URLs** - Must be accessible and valid
4. **Accurate counts** - For pagination and result counts
5. **Error handling** - Graceful degradation when API fails

## Next Steps for Backend

1. ✅ Verify Vehicle entity matches this structure
2. ✅ Implement search endpoint with all filters
3. ✅ Add pagination support
4. ✅ Implement sorting options
5. ✅ Add image URL handling
6. ✅ Optimize database queries with indexes
7. ✅ Add caching for brands/types
8. ✅ Implement error handling
9. ✅ Add API documentation (OpenAPI/Swagger)
