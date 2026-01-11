# Vehicle Service - Integration Guide

## Overview
The Vehicle Service has been updated to support the frontend Browse Cars page with:
- Text search functionality across brand, model, and vehicle type
- Real car data seeding on startup (dev profile)
- Proper API endpoints matching frontend requirements

## Changes Made

### 1. Search Functionality
- **Added `search` parameter** to `VehicleSearchParams` DTO
- **Updated `VehicleController`** to accept `search` query parameter
- **Enhanced `VehicleRepository`** with text search query across:
  - Brand name
  - Vehicle model
  - Vehicle type name
  - Description
- **Updated `VehicleService`** to handle search parameter

### 2. Data Seeding
- **Created `DataSeeder`** component that runs on application startup (dev profile only)
- Seeds **8 brands**: Tesla, BMW, Mercedes-Benz, Audi, Porsche, Toyota, Ford, Lamborghini
- Seeds **7 vehicle types**: Sedan, SUV, Coupe, Convertible, Hatchback, Truck, Electric
- Seeds **25+ real vehicles** with:
  - Real specifications (engine, transmission, fuel type, horsepower, etc.)
  - Real pricing
  - High-quality images from Unsplash
  - Stock quantities
  - Availability status

### 3. API Endpoints

#### Search Vehicles
```
GET /api/v1/vehicles?search={query}&brandId={uuid}&vehicleTypeId={uuid}&modelYear={year}&minPrice={price}&maxPrice={price}&status={status}&page={page}&size={size}
```

**Query Parameters:**
- `search` (optional): Text search across brand, model, type, and description
- `brandId` (optional): Filter by brand UUID
- `vehicleTypeId` (optional): Filter by vehicle type UUID
- `modelYear` (optional): Filter by model year
- `minPrice` (optional): Minimum price filter
- `maxPrice` (optional): Maximum price filter
- `status` (optional): Filter by status (AVAILABLE, SOLD_OUT, DISCONTINUED)
- `page` (optional): Page number (0-indexed, default: 0)
- `size` (optional): Page size (default: 12)

**Response:** Paginated `Page<VehicleResponse>`

#### Get All Brands
```
GET /api/v1/brands
```

**Response:** `List<BrandResponse>`

#### Get All Vehicle Types
```
GET /api/v1/vehicle-types
```

**Response:** `List<VehicleTypeResponse>`

## Running the Service

### Prerequisites
1. PostgreSQL running on `localhost:5433`
2. Database `bennycar_db` created
3. Schema `vehicle_service` exists

### Start the Service
```bash
cd services/vehicle-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The service will:
1. Connect to PostgreSQL
2. Auto-create tables if they don't exist (Hibernate DDL auto-update)
3. Seed data automatically on first startup (if database is empty)

### Verify Data Seeding
Check the logs for:
```
Starting data seeding...
Seeded X vehicle types
Seeded X brands
Seeded X vehicles
Data seeding completed successfully!
```

## Testing the Integration

### 1. Test Search Endpoint
```bash
curl "http://localhost:8082/api/v1/vehicles?search=tesla&size=12"
```

### 2. Test Filters
```bash
# Filter by brand
curl "http://localhost:8082/api/v1/vehicles?brandId={brand-uuid}"

# Filter by type
curl "http://localhost:8082/api/v1/vehicles?vehicleTypeId={type-uuid}"

# Filter by price range
curl "http://localhost:8082/api/v1/vehicles?minPrice=30000&maxPrice=80000"
```

### 3. Test Brands Endpoint
```bash
curl "http://localhost:8082/api/v1/brands"
```

### 4. Test Vehicle Types Endpoint
```bash
curl "http://localhost:8082/api/v1/vehicle-types"
```

## Frontend Integration

The frontend is already configured to call:
- `GET /api/v1/vehicles/search` - But the backend uses `/api/v1/vehicles` (needs frontend update)
- `GET /api/v1/brands` - ✅ Matches
- `GET /api/v1/vehicle-types` - ✅ Matches

### Frontend Service Update Needed
Update `frontend/lib/api/vehicle.service.ts` to use:
- `GET /api/v1/vehicles` instead of `/api/v1/vehicles/search`

## Data Structure

### Vehicle Response
```json
{
  "id": "uuid",
  "brand": {
    "id": "uuid",
    "name": "Tesla",
    "logoUrl": "https://..."
  },
  "vehicleType": {
    "id": "uuid",
    "name": "Electric",
    "iconUrl": null
  },
  "model": "Model S",
  "modelYear": 2024,
  "description": "Luxury electric sedan...",
  "basePrice": 79990.00,
  "engine": "Dual Motor",
  "transmission": "Single-Speed",
  "fuelType": "Electric",
  "horsepower": "670 HP",
  "seatingCapacity": 5,
  "cargoCapacityLiters": 709,
  "fuelEfficiency": "120 MPGe",
  "mainImageUrl": "https://images.unsplash.com/...",
  "additionalImages": ["https://..."],
  "status": "AVAILABLE",
  "stockQuantity": 15,
  "available": true,
  "createdAt": "2024-01-11T...",
  "updatedAt": "2024-01-11T..."
}
```

## Notes

1. **Images**: Using Unsplash images via URL. These are publicly accessible and high-quality.
2. **Data Seeding**: Only runs in `dev` profile and only if database is empty
3. **Search**: Case-insensitive, supports partial matches
4. **Pagination**: Default page size is 12 (matching frontend)
5. **Status Filter**: If not provided, shows all statuses (not just AVAILABLE)

## Troubleshooting

### Data Not Seeding
- Check if database already has data (seeder skips if data exists)
- Verify `dev` profile is active
- Check application logs for errors

### Search Not Working
- Verify PostgreSQL is running
- Check database connection in `application-dev.yml`
- Verify schema `vehicle_service` exists

### Images Not Loading
- Unsplash images should load automatically
- Check network connectivity
- Verify image URLs in database

## Next Steps

1. ✅ Backend search functionality - DONE
2. ✅ Data seeding - DONE
3. ⏳ Update frontend service to use correct endpoint
4. ⏳ Test full integration (gateway + user + vehicle services)
5. ⏳ Verify images load correctly in frontend
