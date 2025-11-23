# User Service

Authentication and user management service for BennyCar application.

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 14+

### Setup

1. **Clone and navigate to the service**
   ```bash
   cd user-service
   ```

2. **Set up environment variables**
   ```bash
   # Copy the template
   cp .env.example .env
   
   # Edit .env with your actual values
   nano .env
   ```

3. **Install dependencies**
   ```bash
   mvn clean install
   ```

4. **Run the service**
   ```bash
   mvn spring-boot:run
   ```

The service will start on `http://localhost:8081`

## 📖 Documentation

- **[Configuration Guide](CONFIG_GUIDE.md)** - Comprehensive guide on configuration, profiles, and deployment
- **[API Documentation](#api-endpoints)** - REST API reference

## 🎯 Features

- User registration and authentication
- JWT-based security
- Role-based access control (RBAC)
- Token refresh mechanism
- PostgreSQL integration
- Docker support

## 🔐 Security

This service uses industry-standard security practices:
- JWT tokens for authentication
- BCrypt password hashing
- Environment-based configuration (no hardcoded secrets)
- Spring Security integration

**Important**: Never commit `.env` files to version control. See [CONFIG_GUIDE.md](CONFIG_GUIDE.md) for details.

## 🏗️ Architecture

### Tech Stack
- **Framework**: Spring Boot 3.5.7
- **Security**: Spring Security + JWT
- **Database**: PostgreSQL with JPA/Hibernate
- **Build Tool**: Maven
- **Java Version**: 17

### Project Structure
```
src/main/java/de/bennycar/user/
├── controller/       # REST endpoints
├── dto/             # Data Transfer Objects
├── model/           # Entity classes
├── repository/      # Data access layer
├── security/        # Security configuration
├── service/         # Business logic
└── config/          # Application configuration

src/main/resources/
├── application.yml       # Base configuration
├── application-dev.yml   # Development profile
├── application-prod.yml  # Production profile
└── .env.example         # Environment variables template
```

## 🔧 Configuration

### Spring Profiles

This service supports multiple profiles:

- **dev** - Development environment (verbose logging, auto-update schema)
- **prod** - Production environment (minimal logging, schema validation only)

Activate a profile by setting `SPRING_PROFILES_ACTIVE=dev` in your `.env` file.

See [CONFIG_GUIDE.md](CONFIG_GUIDE.md) for detailed configuration instructions.

### Environment Variables

Required variables (set in `.env` file):

```bash
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8081
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/bennycar_db
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=your_password
JWT_SECRET=your_secret_key  # Min 256 bits
JWT_EXPIRATION=86400000
```

Generate a secure JWT secret:
```bash
openssl rand -base64 32
```

## 📡 API Endpoints

### Authentication

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecurePass123!"
}

Response:
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

#### Refresh Token
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGc..."
}
```

#### Get Profile
```http
GET /api/auth/profile
Authorization: Bearer <access_token>
```

#### Logout
```http
POST /api/auth/logout
Authorization: Bearer <access_token>
```

### Health & Monitoring

```http
GET /actuator/health
GET /actuator/info
GET /actuator/metrics
```

## 🐳 Docker

### Build Image
```bash
docker build -t bennycar/user-service:latest .
```

### Run Container
```bash
docker run -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/bennycar_db \
  -e SPRING_DATASOURCE_USERNAME=admin \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e JWT_SECRET=your_secret \
  bennycar/user-service:latest
```

### Docker Compose
```bash
docker-compose up user-service
```

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report

# Run specific test
mvn test -Dtest=AuthControllerTest
```

## 🔨 Development

### Building

```bash
# Clean and install
mvn clean install

# Package (skip tests)
mvn clean package -DskipTests

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Code Quality

```bash
# Format code
mvn spring-javaformat:apply

# Check style
mvn checkstyle:check
```

## 📊 Database

### Schema

The service uses the `user_service` schema in PostgreSQL:

- **users** - User account information
- **roles** - User roles (ROLE_USER, ROLE_ADMIN)
- **user_roles** - Many-to-many relationship
- **refresh_tokens** - JWT refresh tokens

### Migration

For production deployments, consider using:
- [Flyway](https://flywaydb.org/)
- [Liquibase](https://www.liquibase.org/)

Set `SPRING_JPA_HIBERNATE_DDL_AUTO=validate` and manage schema with migrations.

## 🚨 Troubleshooting

### Port already in use
```bash
# Find process
lsof -i :8081

# Kill process
kill <PID>
```

### Database connection failed
```bash
# Test connection
psql -h localhost -p 5433 -U admin -d bennycar_db

# Check if database is running
docker ps | grep postgres
```

### JWT errors
- Ensure JWT_SECRET is at least 256 bits (32 characters)
- Verify the same secret is used consistently
- Check token expiration time

See [CONFIG_GUIDE.md](CONFIG_GUIDE.md) for more troubleshooting tips.

## 📚 Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security](https://docs.spring.io/spring-security/reference/)
- [JWT.io](https://jwt.io/)
- [12-Factor App](https://12factor.net/)

## 📝 License

Copyright © 2025 BennyCar

## 🤝 Contributing

See [CONTRIBUTING.md](../CONTRIBUTING.md) for contribution guidelines.

---

**Need Help?** Check the [Configuration Guide](CONFIG_GUIDE.md) for detailed setup instructions.

