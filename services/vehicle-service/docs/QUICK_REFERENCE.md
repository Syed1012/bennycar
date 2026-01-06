# Vehicle Service - Quick Reference Guide

## 🚀 Quick Start

### Start the Service
```bash
cd vehicle-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Access Points
- **Service**: http://localhost:8082
- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **Health Check**: http://localhost:8082/actuator/health

---

## 📍 Common API Endpoints

### Public Endpoints (No Auth)

```bash
# Get all vehicles
GET http://localhost:8082/api/vehicles

# Search vehicles
GET http://localhost:8082/api/vehicles?brandId={UUID}&minPrice=20000&maxPrice=50000

# Get vehicle details
GET http://localhost:8082/api/vehicles/{vehicleId}

# Get vehicle with customizations
GET http://localhost:8082/api/vehicles/{vehicleId}/customizations

# Get all brands
GET http://localhost:8082/api/brands

# Get customization options
GET http://localhost:8082/api/customization/options
```

### Protected Endpoints (Require JWT)

```bash
# Get my configurations
GET http://localhost:8082/api/configurations
Authorization: Bearer {token}

# Create configuration
POST http://localhost:8082/api/configurations
Authorization: Bearer {token}
Content-Type: application/json

{
  "vehicleId": "uuid",
  "name": "My Config",
  "selectedOptionIds": ["uuid1", "uuid2"]
}

# Order configuration
POST http://localhost:8082/api/configurations/{id}/order
Authorization: Bearer {token}
```

---

## 🔑 Getting a JWT Token

### 1. Login via User Service
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user@example.com",
    "password": "password"
  }'
```

### 2. Extract Token
```json
{
  "token": "eyJhbGc...",
  "type": "Bearer"
}
```

### 3. Use Token in Requests
```bash
TOKEN="eyJhbGc..."
curl http://localhost:8082/api/configurations \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🗄️ Database Quick Commands

### Connect to Database
```bash
docker exec -it bennycar-postgres psql -U admin -d bennycar_db
```

### Useful SQL Queries

```sql
-- List all tables in vehicle_service schema
\dt vehicle_service.*;

-- Count vehicles
SELECT COUNT(*) FROM vehicle_service.vehicles;

-- View vehicle with brand
SELECT v.model, v.model_year, b.name as brand, v.base_price 
FROM vehicle_service.vehicles v
JOIN vehicle_service.brands b ON v.brand_id = b.id;

-- View configurations for a user
SELECT id, name, status, total_price 
FROM vehicle_service.vehicle_configurations 
WHERE user_id = 'your-user-uuid';

-- Check stock levels
SELECT b.name, v.model, v.stock_quantity, v.status
FROM vehicle_service.vehicles v
JOIN vehicle_service.brands b ON v.brand_id = b.id
ORDER BY v.stock_quantity;

-- View customization options with prices
SELECT 
    c.name as category,
    o.name as option,
    o.price_adjustment
FROM vehicle_service.customization_options o
JOIN vehicle_service.customization_categories c ON o.category_id = c.id
ORDER BY c.display_order, o.display_order;
```

---

## 🔧 Common Tasks

### Add a New Vehicle

```bash
curl -X POST http://localhost:8082/api/admin/vehicles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "brandId": "brand-uuid",
    "vehicleTypeId": "type-uuid",
    "model": "Model S",
    "modelYear": 2024,
    "basePrice": 89990.00,
    "engine": "Tri Motor",
    "transmission": "Single-Speed",
    "fuelType": "Electric",
    "status": "AVAILABLE",
    "stockQuantity": 10
  }'
```

### Add Customization Option

```bash
curl -X POST http://localhost:8082/api/admin/customization/options \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "categoryId": "category-uuid",
    "name": "Midnight Silver Metallic",
    "priceAdjustment": 1000.00,
    "colorCode": "#5C5D61",
    "active": true
  }'
```

### Check Configuration Total Price

```sql
SELECT 
    c.id,
    c.name,
    v.base_price,
    (SELECT SUM(o.price_adjustment) 
     FROM vehicle_service.configuration_selected_options cso
     JOIN vehicle_service.customization_options o ON cso.customization_option_id = o.id
     WHERE cso.configuration_id = c.id) as options_total,
    c.total_price
FROM vehicle_service.vehicle_configurations c
JOIN vehicle_service.vehicles v ON c.vehicle_id = v.id;
```

---

## 🐛 Debugging

### View Logs

```bash
# Real-time logs
tail -f logs/vehicle-service.log

# Filter by level
grep ERROR logs/vehicle-service.log

# Filter by class
grep "VehicleService" logs/vehicle-service.log
```

### Check JWT Token

```bash
# Decode token (without verification)
echo "eyJhbGciOi..." | cut -d '.' -f2 | base64 -d | jq
```

### Verify Database Connection

```bash
# Check connection from service
curl http://localhost:8082/actuator/health | jq '.components.db'
```

### Check Active Connections

```sql
-- In PostgreSQL
SELECT count(*) FROM pg_stat_activity WHERE datname = 'bennycar_db';
```

---

## ⚡ Performance Tips

### Query Optimization

```java
// Bad: N+1 query problem
List<Vehicle> vehicles = vehicleRepository.findAll();
for (Vehicle v : vehicles) {
    v.getBrand().getName(); // Triggers query for each vehicle
}

// Good: Use fetch join
@Query("SELECT v FROM Vehicle v JOIN FETCH v.brand JOIN FETCH v.vehicleType")
List<Vehicle> findAllWithDetails();
```

### Pagination

```bash
# Always use pagination for lists
GET /api/vehicles?page=0&size=20

# Don't fetch all at once
GET /api/vehicles  # Returns paginated by default
```

### Caching (Future Enhancement)

```java
@Cacheable(value = "vehicles", key = "#id")
public VehicleResponse getVehicleById(UUID id) {
    // Cached for faster access
}
```

---

## 📊 Business Rules Checklist

### Creating Configuration
- [ ] Vehicle exists
- [ ] Vehicle status = AVAILABLE
- [ ] Vehicle stock > 0
- [ ] All options exist
- [ ] All options are active
- [ ] Options belong to vehicle's available customizations
- [ ] Single-select categories have only one option selected

### Ordering Configuration
- [ ] Configuration exists
- [ ] User owns configuration
- [ ] Configuration status = DRAFT
- [ ] Vehicle still available
- [ ] Vehicle still has stock
- [ ] Stock decremented atomically
- [ ] Status changed to ORDERED
- [ ] Timestamp recorded

---

## 🔒 Security Checklist

- [ ] JWT_SECRET is set in environment
- [ ] JWT_SECRET matches User Service
- [ ] HTTPS enabled in production
- [ ] CORS configured for frontend
- [ ] SQL injection prevented (using JPA)
- [ ] Input validation enabled
- [ ] Passwords never logged
- [ ] Tokens never logged
- [ ] Database credentials secured

---

## 📈 Monitoring

### Health Check
```bash
curl http://localhost:8082/actuator/health
```

**Expected Response:**
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

### Metrics
```bash
# JVM metrics
curl http://localhost:8082/actuator/metrics/jvm.memory.used

# HTTP metrics
curl http://localhost:8082/actuator/metrics/http.server.requests

# Database connection pool
curl http://localhost:8082/actuator/metrics/hikaricp.connections.active
```

---

## 🧪 Testing Commands

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify -Pintegration-tests
```

### Test Coverage
```bash
mvn test jacoco:report
open target/site/jacoco/index.html
```

### API Testing with Postman
1. Import collection from `docs/postman/Vehicle-Service.postman_collection.json`
2. Set environment variables (baseUrl, token)
3. Run collection

---

## 📦 Deployment

### Build JAR
```bash
mvn clean package -DskipTests
```

### Run JAR
```bash
java -jar target/vehicle-service-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url=jdbc:postgresql://prod-db:5432/bennycar_db
```

### Docker Build
```bash
docker build -t vehicle-service:latest .
```

### Docker Run
```bash
docker run -p 8082:8082 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/bennycar_db \
  -e JWT_SECRET=$JWT_SECRET \
  vehicle-service:latest
```

---

## 🆘 Emergency Commands

### Kill Service on Port 8082
```bash
lsof -ti:8082 | xargs kill -9
```

### Reset Database Schema
```bash
docker exec -it bennycar-postgres psql -U admin -d bennycar_db -c "DROP SCHEMA vehicle_service CASCADE; CREATE SCHEMA vehicle_service;"
```

### Restart PostgreSQL Container
```bash
docker restart bennycar-postgres
```

### View Recent Errors
```bash
grep ERROR logs/vehicle-service.log | tail -20
```

---

## 📚 Documentation Links

- **Full README**: [README.md](../README.md)
- **API Reference**: [API_REFERENCE.md](API_REFERENCE.md)
- **Business Logic**: [BUSINESS_LOGIC.md](BUSINESS_LOGIC.md)
- **Integration Guide**: [USER_SERVICE_INTEGRATION.md](USER_SERVICE_INTEGRATION.md)
- **Swagger UI**: http://localhost:8082/swagger-ui.html

---

## 🎯 Key Concepts

| Concept | Description |
|---------|-------------|
| **Vehicle** | Base vehicle in catalog (car model) |
| **Brand** | Vehicle manufacturer (Tesla, BMW) |
| **VehicleType** | Category (SUV, Sedan, Truck) |
| **CustomizationCategory** | Group of options (Colors, Interiors) |
| **CustomizationOption** | Individual choice (Red, Leather) |
| **Configuration** | User's personalized vehicle build |
| **Status (Vehicle)** | AVAILABLE, SOLD_OUT, DISCONTINUED |
| **Status (Config)** | DRAFT, ORDERED, DELIVERED, CANCELLED |

---

## 💡 Tips & Tricks

### Pretty Print JSON Responses
```bash
curl http://localhost:8082/api/vehicles | jq '.'
```

### Save Token to Variable
```bash
TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user@example.com","password":"pass"}' \
  | jq -r '.token')
```

### Search with Multiple Filters
```bash
curl -G http://localhost:8082/api/vehicles \
  --data-urlencode "brandId=uuid" \
  --data-urlencode "minPrice=30000" \
  --data-urlencode "maxPrice=50000" \
  --data-urlencode "modelYear=2024"
```

### Format SQL Output
```sql
-- In psql
\x auto  -- Toggle expanded output
\pset pager off  -- Disable pager
```

---

**Quick Reference Version**: 1.0  
**Last Updated**: December 12, 2025  
**For**: BennyCar Vehicle Service

