# Bennycar - Architecture & Conceptualization

## 🎯 Project Vision

**Bennycar** is a modern **vehicle purchasing platform** that allows users to browse, select, and buy vehicles online. Built using a **microservices architecture**, the platform is designed to be scalable, maintainable, and production-ready.

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                       Frontend Layer                         │
│                     (React + Vite)                           │
│                    Port: 3000                                │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ HTTP/REST
                     │
┌────────────────────▼────────────────────────────────────────┐
│                   API Gateway (Future)                       │
│                    Port: 8080                                │
│            (Authentication, Routing, Rate Limiting)          │
└────────────────────┬────────────────────────────────────────┘
                     │
         ┌───────────┼───────────┐
         │           │           │
         ▼           ▼           ▼
    ┌────────┐  ┌────────┐  ┌────────┐
    │ User   │  │Vehicle │  │ Order  │
    │Service │  │Service │  │Service │
    │ :8081  │  │ :8082  │  │ :8083  │
    └───┬────┘  └───┬────┘  └───┬────┘
        │           │           │
        │           │           │
        └───────────┼───────────┘
                    │
         ┌──────────▼───────────┐
         │   PostgreSQL DB      │
         │   (Schema Separated) │
         │      Port: 5433      │
         │                      │
         │ - user_service       │
         │ - vehicle_service    │
         │ - order_service      │
         └──────────────────────┘
```

---

## 🧩 Current Implementation

### ✅ User Service (Implemented)

**Purpose**: Handle user authentication, registration, and profile management

**Technology Stack**:
- **Framework**: Spring Boot 3.5.7
- **Security**: Spring Security + JWT
- **Database**: PostgreSQL (Schema: `user_service`)
- **Port**: 8081

**Features**:
- ✅ User Registration
- ✅ User Login (JWT-based)
- ✅ User Logout
- ✅ Token Refresh
- ✅ Password Encryption (BCrypt)
- ✅ Role-based Access Control (RBAC) Ready
- ✅ Health Check Endpoint

**API Endpoints**:
```
POST /api/v1/auth/register   - Register new user
POST /api/v1/auth/login      - Login user (returns JWT)
POST /api/v1/auth/logout     - Logout user
POST /api/v1/auth/refresh    - Refresh JWT token
GET  /actuator/health        - Service health check
```

**Database Schema** (`user_service`):
```sql
users:
  - id (UUID)
  - email (unique)
  - password (encrypted)
  - first_name
  - last_name
  - role (CUSTOMER, ADMIN, DEALER)
  - created_at
  - updated_at

refresh_tokens:
  - id (UUID)
  - token (unique)
  - user_id (FK)
  - expiry_date
  - created_at
```

**Security**:
- JWT tokens with configurable expiration
- Refresh token rotation
- Password encryption with BCrypt
- CORS configuration for frontend
- Stateless authentication

---

## 🔮 Future Microservices (Planned)

### 🚗 Vehicle Service (Coming Soon)
**Purpose**: Manage vehicle catalog, inventory, and listings

**Planned Features**:
- Vehicle CRUD operations
- Search and filter vehicles
- Vehicle images and details
- Dealer inventory management
- Price management
- Vehicle availability status

**Database Schema** (`vehicle_service`):
- vehicles
- vehicle_images
- vehicle_specifications
- categories (SUV, Sedan, Truck, etc.)

---

### 🛒 Order Service (Coming Soon)
**Purpose**: Handle vehicle purchases and order management

**Planned Features**:
- Create purchase orders
- Payment integration
- Order tracking
- Order history
- Invoice generation
- Delivery scheduling

**Database Schema** (`order_service`):
- orders
- order_items
- payments
- transactions
- delivery_info

---

### 🌐 API Gateway (Coming Soon)
**Purpose**: Central entry point for all microservices

**Planned Features**:
- Request routing
- Authentication & Authorization
- Rate limiting
- Load balancing
- API versioning
- Request/Response logging

**Technology**: Spring Cloud Gateway or Kong

---

### 📧 Notification Service (Future)
**Purpose**: Send emails, SMS, and push notifications

**Planned Features**:
- Email notifications (order confirmation, registration)
- SMS notifications (OTP, order updates)
- Push notifications (mobile app)
- Template management

---

## 🗄️ Database Strategy

**Approach**: Single PostgreSQL instance with **schema separation**

**Why?**
- ✅ Resource-efficient for development
- ✅ Maintains logical separation between services
- ✅ Easy to migrate to separate databases later
- ✅ Simplified local development

**Schemas**:
```
bennycar_db
├── user_service     (User Service)
├── vehicle_service  (Vehicle Service)
└── order_service    (Order Service)
```

Each service **only** accesses its own schema - maintaining microservice principles.

---

## 🔐 Security & Configuration

### Environment Variables
All sensitive data is stored in `.env` file (not committed to Git):

```env
POSTGRES_PASSWORD=...
JWT_SECRET=...
```

### JWT Authentication Flow
```
1. User logs in → POST /api/v1/auth/login
2. Server validates credentials
3. Server generates JWT + Refresh Token
4. Client stores tokens securely
5. Client includes JWT in Authorization header
6. Server validates JWT on each request
7. Token expires → Use refresh token
8. Refresh token → Get new JWT
```

---

## 📦 Deployment

### Docker Compose (Development)
```bash
# Start all services
docker-compose up -d

# Start specific services
docker-compose up -d postgres user-service

# View logs
docker-compose logs -f user-service

# Stop services
docker-compose down
```

### Production (Future)
- Kubernetes deployment
- Cloud providers (AWS EKS, Google GKE, Azure AKS)
- CI/CD with GitHub Actions
- Infrastructure as Code (Terraform)

---

## 🛣️ Development Roadmap

### Phase 1: Foundation ✅ (Current)
- [x] User Service
- [x] JWT Authentication
- [x] Database setup
- [x] Docker containerization
- [x] Environment configuration

### Phase 2: Core Features 🚧 (In Progress)
- [ ] API Gateway
- [ ] Vehicle Service
- [ ] Frontend integration with User Service
- [ ] RBAC implementation

### Phase 3: Business Logic 📅 (Planned)
- [ ] Order Service
- [ ] Payment integration
- [ ] Vehicle search & filtering
- [ ] Shopping cart

### Phase 4: Advanced Features 🔮 (Future)
- [ ] Notification Service
- [ ] Admin dashboard
- [ ] Analytics & reporting
- [ ] Mobile app
- [ ] AI-powered recommendations

---

## 🧪 Testing Strategy

### Unit Tests
- Service layer testing
- Repository testing
- JWT utility testing

### Integration Tests
- API endpoint testing
- Database integration
- Authentication flow testing

### E2E Tests (Future)
- Full user journey testing
- Cross-service testing

---

## 📊 Monitoring & Observability (Future)

- **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **Metrics**: Prometheus + Grafana
- **Tracing**: Jaeger or Zipkin
- **Health Checks**: Spring Boot Actuator

---

## 🤝 Contributing

This is a professional microservices project following industry best practices:

1. **Separation of Concerns**: Each service has a single responsibility
2. **Independent Deployment**: Services can be deployed independently
3. **Technology Agnostic**: Services can use different technologies
4. **Scalable**: Services can scale independently based on load
5. **Resilient**: Failure in one service doesn't bring down the entire system

---

## 📚 Tech Stack Summary

| Layer            | Technology                    |
|------------------|-------------------------------|
| Frontend         | React 18, Vite, TailwindCSS   |
| Backend          | Spring Boot 3.5.7, Java 17    |
| Security         | Spring Security, JWT          |
| Database         | PostgreSQL 16                 |
| Containerization | Docker, Docker Compose        |
| Build Tool       | Maven                         |
| API Docs         | Swagger/OpenAPI (Future)      |

---

## 🎓 Key Principles

✅ **Microservices Best Practices**
✅ **RESTful API Design**
✅ **Secure by Default**
✅ **Environment-based Configuration**
✅ **Database per Service (Schema Separation)**
✅ **Stateless Services**
✅ **Container-first Approach**
✅ **Production-ready Architecture**

---

## 🚀 Further Improvements & New Microservices On The Way!

This is an evolving project with continuous improvements:

- 🔜 API Gateway for centralized routing
- 🔜 Vehicle Service for catalog management
- 🔜 Order Service for purchase processing
- 🔜 Advanced RBAC with fine-grained permissions
- 🔜 Real-time notifications
- 🔜 Analytics dashboard
- 🔜 Mobile applications
- 🔜 AI-powered vehicle recommendations
- 🔜 Integration with third-party services

---

**Last Updated**: November 2025  
**Version**: 1.0.0  
**Status**: Active Development 🚀

