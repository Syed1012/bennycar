# GitHub Copilot Instructions for BennyCar

## Project Overview

BennyCar is a full-stack Car Inventory Management System with:
- **Backend**: Spring Boot 3.5.7 REST API with Java 17
- **Frontend**: React 19 with Vite 7
- **Database**: PostgreSQL 16
- **Message Queue**: RabbitMQ 3.12
- **Containerization**: Docker and Docker Compose

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.5.7
- Spring Data JPA with Hibernate
- PostgreSQL 16 driver
- Spring AMQP for RabbitMQ integration
- Maven for build management

### Frontend
- React 19
- Vite 7
- Axios for HTTP requests
- ESLint for linting
- Nginx for production deployment

### DevOps
- Docker and Docker Compose
- PostgreSQL running on port 5433 (host) / 5432 (container)
- Backend running on port 8081
- Frontend running on port 3000
- RabbitMQ on ports 5672 (AMQP) and 15672 (Management UI)

## Code Structure and Patterns

### Backend Structure
```
src/main/java/de/bennycar/
├── BennycarApplication.java      # Main Spring Boot application
├── controller/                   # REST controllers
│   ├── CarController.java
│   └── RabbitMQTestController.java
├── service/                      # Business logic layer
│   └── CarService.java
├── repository/                   # Data access layer
│   └── CarRepository.java
├── model/                        # Entity models
│   └── Car.java
├── dto/                          # Data Transfer Objects
│   └── CarEventMessage.java
├── enums/                        # Enum types
│   ├── BodyType.java
│   ├── CarCondition.java
│   ├── FuelType.java
│   └── TransmissionType.java
├── messaging/                    # RabbitMQ integration
│   ├── CarEventProducer.java
│   └── CarEventConsumer.java
└── config/                       # Configuration classes
    └── RabbitMQConfig.java
```

### Frontend Structure
```
frontend/src/
├── App.jsx                       # Main application component
├── main.jsx                      # Entry point
├── components/                   # React components
│   ├── CarCard.jsx
│   └── CarForm.jsx
└── services/                     # API service layer
    └── carService.js
```

## Coding Conventions

### Backend (Java/Spring Boot)

1. **Controller Layer**
   - Use `@RestController` and `@RequestMapping("/api/...")` annotations
   - Enable CORS with `@CrossOrigin(origins = "*")`
   - Use `@Autowired` for dependency injection
   - Return `ResponseEntity<T>` with appropriate HTTP status codes
   - Handle exceptions with try-catch blocks, returning appropriate error responses

2. **Service Layer**
   - Implement business logic in service classes
   - Use `@Service` annotation
   - Return `Optional<T>` for single entity lookups
   - Throw `RuntimeException` for error cases (e.g., entity not found)

3. **Repository Layer**
   - Extend `JpaRepository<Entity, ID>`
   - Use method name conventions for query derivation
   - Use `Optional<T>` for methods that may not find results

4. **Entity Models**
   - Use JPA annotations (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`)
   - Include `@Column` annotations for constraints
   - Implement timestamp fields (`createdAt`, `updatedAt`) with `@CreationTimestamp` and `@UpdateTimestamp`
   - Use enums for type-safe constants

5. **RabbitMQ Integration**
   - Define exchanges, queues, and bindings in configuration class
   - Use `RabbitTemplate` for sending messages
   - Use `@RabbitListener` for consuming messages
   - Implement manual acknowledgment for reliability

### Frontend (React)

1. **Component Structure**
   - Use functional components with hooks
   - Import React at the top: `import React, { useState, useEffect } from 'react';`
   - Separate concerns: presentational components (CarCard) vs. container components (App)

2. **State Management**
   - Use `useState` for local component state
   - Use `useEffect` for side effects (data fetching)
   - Always handle loading and error states

3. **API Integration**
   - Use Axios for HTTP requests
   - Centralize API calls in service files (`carService.js`)
   - Handle errors gracefully with try-catch blocks
   - Always check if response data is an array before mapping

4. **Styling**
   - Use CSS files co-located with components
   - Follow existing naming conventions (kebab-case for class names)

## API Endpoints

All backend endpoints are prefixed with `/api/cars`:

- `GET /api/cars` - Get all cars
- `GET /api/cars/{id}` - Get car by ID
- `GET /api/cars/vin/{vin}` - Get car by VIN
- `GET /api/cars/brand/{brand}` - Get cars by brand
- `GET /api/cars/brand/{brand}/model/{model}` - Get cars by brand and model
- `GET /api/cars/available` - Get available cars
- `GET /api/cars/price?min={min}&max={max}` - Get cars by price range
- `GET /api/cars/year/{year}` - Get cars from year or newer
- `POST /api/cars` - Create a new car
- `PUT /api/cars/{id}` - Update a car
- `DELETE /api/cars/{id}` - Delete a car

## Database Configuration

- Database: PostgreSQL 16
- Connection URL: `jdbc:postgresql://localhost:5433/mydatabase`
- Username: `myuser`
- Password: `secret`
- Hibernate DDL: `update` (auto-update schema)
- Use Hikari connection pool with settings defined in `application.yml`

## RabbitMQ Configuration

- Host: `localhost`
- Port: `5672` (AMQP)
- Management UI: `15672`
- Username: `admin`
- Password: `admin123`
- Concurrent consumers: 3 (min) to 10 (max)
- Manual acknowledgment mode for reliability
- Retry enabled with 3 max attempts

## Development Workflow

### Running the Application

**With Docker (Recommended)**:
```bash
docker-compose up -d
```

**Without Docker**:
1. Start PostgreSQL: `docker-compose up -d postgres`
2. Start RabbitMQ: `docker-compose up -d rabbitmq`
3. Start Backend: `./mvnw spring-boot:run`
4. Start Frontend: `cd frontend && npm install && npm run dev`

### Building

**Backend**:
```bash
./mvnw clean package
```

**Frontend**:
```bash
cd frontend && npm run build
```

### Testing

**Backend**:
```bash
./mvnw test
```

**Frontend**:
```bash
cd frontend && npm test
```

## Environment Variables

### Backend (application.yml)
- `spring.datasource.url` - Database connection URL
- `spring.datasource.username` - Database username
- `spring.datasource.password` - Database password
- `spring.rabbitmq.host` - RabbitMQ host
- `spring.rabbitmq.port` - RabbitMQ port
- `spring.rabbitmq.username` - RabbitMQ username
- `spring.rabbitmq.password` - RabbitMQ password

### Frontend (.env.production)
- `VITE_API_BASE_URL` - Backend API base URL

## Best Practices

1. **Always validate input data** in controllers before processing
2. **Use appropriate HTTP status codes** (200, 201, 204, 400, 404, 500)
3. **Handle exceptions gracefully** with meaningful error messages
4. **Keep business logic in service layer**, not in controllers
5. **Use enums for type-safe constants** (TransmissionType, FuelType, BodyType, CarCondition)
6. **Ensure proper error handling** in frontend (loading states, error messages)
7. **Follow RESTful principles** for API design
8. **Use Docker Compose** for local development to ensure environment consistency
9. **Implement proper validation** for car entities (VIN format, price > 0, year range, etc.)
10. **Use RabbitMQ for event-driven communication** between services

## Common Patterns

### Creating a New REST Endpoint

1. Add repository method in `CarRepository.java`
2. Add service method in `CarService.java`
3. Add controller method in `CarController.java` with proper annotations
4. Return `ResponseEntity<T>` with appropriate status codes
5. Update frontend service if needed

### Adding a New Entity Field

1. Add field to entity class with JPA annotations
2. Update database schema (Hibernate will auto-update with ddl-auto: update)
3. Update DTOs if necessary
4. Update service and controller logic
5. Update frontend components and forms

### RabbitMQ Message Flow

1. Producer sends message via `RabbitTemplate.convertAndSend()`
2. Message routed through exchange to queue
3. Consumer receives message via `@RabbitListener`
4. Manual acknowledgment with `channel.basicAck()` or `channel.basicNack()`

## Troubleshooting

- **Backend can't connect to database**: Check if PostgreSQL is running on port 5433
- **Frontend can't reach backend**: Verify CORS settings and backend is running on port 8081
- **RabbitMQ connection issues**: Ensure RabbitMQ is running and credentials are correct
- **Port conflicts**: Modify ports in `docker-compose.yaml`

## Additional Resources

- Spring Boot Documentation: https://spring.io/projects/spring-boot
- React Documentation: https://react.dev
- Vite Documentation: https://vitejs.dev
- RabbitMQ Documentation: https://www.rabbitmq.com/documentation.html
- PostgreSQL Documentation: https://www.postgresql.org/docs/
