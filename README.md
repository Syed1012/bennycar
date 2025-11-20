# 🚗 BennyCar - Car Inventory Management System

A full-stack application for managing car inventory with a Spring Boot backend and React frontend, fully containerized with Docker.

## 📋 Features

### Backend (Spring Boot)
- RESTful API for car management
- PostgreSQL database integration
- CRUD operations for car inventory
- Filter cars by brand, availability, price range, and year
- JPA/Hibernate for ORM
- Comprehensive data validation

### Frontend (React + Vite)
- Modern, responsive UI
- Card-based grid layout for car display
- Add, edit, and delete cars
- Filter by brand and availability
- Real-time statistics display
- Smooth animations and transitions

## 🛠️ Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Data JPA**
- **PostgreSQL 16**
- **Maven**

### Frontend
- **React 19**
- **Vite 7**
- **Axios**
- **CSS3**
- **Nginx** (for production)

### DevOps
- **Docker**
- **Docker Compose**

## 🚀 Quick Start with Docker (Recommended)

### Prerequisites
- Docker Desktop installed and running
- Port 3000 (frontend), 8080 (backend), and 5433 (postgres) available

### Start All Services

```bash
# From the root directory
docker-compose up -d
```

This will start:
- PostgreSQL database on port 5433
- Spring Boot backend on port 8080
- React frontend on port 3000

### Access the Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api/cars
- **Database**: localhost:5433

### Stop All Services

```bash
docker-compose down
```

### Stop and Remove All Data

```bash
docker-compose down -v
```

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f frontend
docker-compose logs -f backend
docker-compose logs -f postgres
```

## 🔧 Development Setup (Without Docker)

### Backend Setup

1. **Prerequisites**
   - Java 17 or higher
   - Maven 3.6+
   - PostgreSQL running on port 5433

2. **Start PostgreSQL**
   ```bash
   docker-compose up -d postgres
   ```

3. **Run the Backend**
   ```bash
   # Using Maven
   ./mvnw spring-boot:run
   
   # Or using your IDE
   # Run BennycarApplication.java
   ```

### Frontend Setup

1. **Prerequisites**
   - Node.js 16+ and npm

2. **Install Dependencies**
   ```bash
   cd frontend
   npm install
   ```

3. **Run the Frontend**
   ```bash
   npm run dev
   ```

   Access at http://localhost:5173

## 📁 Project Structure

```
bennycar/
├── src/                          # Backend source code
│   ├── main/
│   │   ├── java/
│   │   │   └── de/bennycar/
│   │   │       ├── controller/   # REST controllers
│   │   │       ├── service/      # Business logic
│   │   │       ├── repository/   # Data access layer
│   │   │       ├── model/        # Entity models
│   │   │       └── enums/        # Enum types
│   │   └── resources/
│   │       └── application.properties
│   └── test/                     # Backend tests
├── frontend/                     # Frontend application
│   ├── src/
│   │   ├── components/           # React components
│   │   ├── services/             # API services
│   │   ├── App.jsx               # Main component
│   │   └── main.jsx              # Entry point
│   ├── Dockerfile                # Frontend container config
│   └── nginx.conf                # Nginx configuration
├── Dockerfile                    # Backend container config
├── docker-compose.yaml           # Multi-container setup
├── pom.xml                       # Maven configuration
└── README.md                     # This file
```

## 🔌 API Endpoints

### Cars

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/cars` | Get all cars |
| GET | `/api/cars/{id}` | Get car by ID |
| GET | `/api/cars/vin/{vin}` | Get car by VIN |
| GET | `/api/cars/brand/{brand}` | Get cars by brand |
| GET | `/api/cars/available` | Get available cars |
| GET | `/api/cars/price?min={min}&max={max}` | Get cars by price range |
| GET | `/api/cars/year/{year}` | Get cars from year or newer |
| POST | `/api/cars` | Create a new car |
| PUT | `/api/cars/{id}` | Update a car |
| DELETE | `/api/cars/{id}` | Delete a car |

### Sample Request Body (POST/PUT)

```json
{
  "name": "BMW 3 Series 320i",
  "vin": "WBADT43452G296945",
  "brand": "BMW",
  "model": "3 Series",
  "year": 2023,
  "color": "Alpine White",
  "transmission": "AUTOMATIC",
  "fuelType": "GASOLINE",
  "bodyType": "SEDAN",
  "mileage": 15000,
  "price": 42500.00,
  "description": "Executive sedan with premium features",
  "condition": "USED_EXCELLENT",
  "location": "Munich, Germany",
  "isAvailable": true
}
```

## 🔐 Environment Variables

### Backend
- `SPRING_DATASOURCE_URL` - Database connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `SPRING_JPA_HIBERNATE_DDL_AUTO` - Hibernate DDL mode

### Frontend
- `VITE_API_BASE_URL` - Backend API URL

## 🧪 Testing

### Backend Tests
```bash
./mvnw test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## 🔨 Building for Production

### Build All Services
```bash
docker-compose build
```

### Build Individual Services
```bash
# Backend
docker build -t bennycar-backend .

# Frontend
docker build -t bennycar-frontend ./frontend
```

## 📊 Database Schema

The `cars` table includes:
- `id` (Primary Key)
- `name`, `vin` (Unique), `brand`, `model`
- `year`, `color`, `mileage`
- `transmission`, `fuelType`, `bodyType`
- `price`, `condition`, `location`
- `description`, `isAvailable`
- `createdAt`, `updatedAt` (timestamps)

## 🐛 Troubleshooting

### Backend not connecting to database
1. Ensure PostgreSQL is running: `docker-compose ps`
2. Check database logs: `docker-compose logs postgres`
3. Verify connection settings in `application.properties`

### Frontend can't reach backend
1. Ensure backend is running on port 8080
2. Check CORS settings in backend
3. Verify API URL in frontend service configuration

### Port conflicts
If ports are already in use, modify them in `docker-compose.yaml`:
```yaml
ports:
  - "3001:80"    # Change 3000 to 3001 for frontend
  - "8081:8080"  # Change 8080 to 8081 for backend
```

## 📝 License

This project is created for educational purposes.

## 👨‍💻 Author

BennyCar Development Team

---

**Happy Coding! 🚀**

