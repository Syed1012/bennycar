# Quick Start Guide

## Prerequisites

- Docker & Docker Compose
- Java 17+ (for local development)
- Maven 3.9+ (for local development)
- Node.js 18+ (for frontend development)

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd bennycar
```

### 2. Configure Environment Variables
```bash
# Copy the example env file
cp .env.example .env

# Edit .env and update with your values
nano .env
```

**Important**: Update these values in `.env`:
- `POSTGRES_PASSWORD` - Change to a secure password
- `USER_SERVICE_JWT_SECRET` - Generate a secure secret (min 256 bits)

### 3. Start the Services
```bash
# Start all services
docker-compose up -d

# Or start specific services
docker-compose up -d postgres user-service
```

### 4. Verify Services are Running
```bash
# Check service status
docker-compose ps

# Check user-service health
curl http://localhost:8081/actuator/health

# Expected response: {"status":"UP"}
```

### 5. View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f user-service
```

## 📝 Test the User Service

### Register a New User
```bash
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@bennycar.com",
    "password": "SecurePass123!",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### Login
```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@bennycar.com",
    "password": "SecurePass123!"
  }'
```

Response will include JWT token:
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "uuid-token",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

## 🛠️ Development Mode

### Run User Service Locally
```bash
# Start only postgres
docker-compose up -d postgres

# Run user-service locally
cd user-service
mvn spring-boot:run
```

### Run Frontend Locally
```bash
cd frontend
npm install
npm run dev
```

## 🧹 Cleanup

### Stop Services
```bash
docker-compose down
```

### Remove Volumes (⚠️ Deletes all data)
```bash
docker-compose down -v
```

### Remove Images
```bash
docker-compose down --rmi all
```

## 🐛 Troubleshooting

### Port Already in Use
```bash
# Find process using the port
lsof -i :8081

# Kill the process
kill -9 <PID>
```

### Database Connection Failed
```bash
# Check postgres logs
docker-compose logs postgres

# Verify postgres is healthy
docker-compose ps postgres
```

### Service Won't Start
```bash
# Rebuild the image
docker-compose build user-service

# Start with fresh logs
docker-compose up user-service
```

## 📚 Next Steps

- Read [README.md](./README.md) for architecture details
- Check [PORTS.md](./PORTS.md) for port configuration
- Explore the API using Postman or curl
- Start building new microservices!

## 🔗 Useful Links

- Spring Boot Docs: https://spring.io/projects/spring-boot
- Docker Compose Docs: https://docs.docker.com/compose/
- PostgreSQL Docs: https://www.postgresql.org/docs/

---

**Happy Coding! 🚀**

