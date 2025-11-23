# Bennycar - Port Configuration Guide

This document provides an overview of all ports used in the Bennycar microservices architecture.

## 📋 Port Mapping Table

| Service          | Host Port | Container Port | Protocol | Description                          |
|------------------|-----------|----------------|----------|--------------------------------------|
| PostgreSQL       | 5433      | 5432           | TCP      | PostgreSQL database                  |
| User Service     | 8081      | 8081           | HTTP     | User authentication & management     |
| Frontend         | 3000      | 80             | HTTP     | React frontend application           |

## 🔧 Configuration

All ports are configurable via environment variables in the `.env` file:

```env
# Database
POSTGRES_PORT=5433

# Backend Services
USER_SERVICE_PORT=8081

# Frontend
FRONTEND_PORT=3000
```

## 🌐 Service Endpoints

### User Service (Port 8081)
- **Health Check**: `http://localhost:8081/actuator/health`
- **API Base**: `http://localhost:8081/api/v1`
- **Auth Endpoints**:
  - POST `/api/v1/auth/register` - User registration
  - POST `/api/v1/auth/login` - User login
  - POST `/api/v1/auth/logout` - User logout
  - POST `/api/v1/auth/refresh` - Refresh JWT token

### Frontend (Port 3000)
- **URL**: `http://localhost:3000`
- React application for vehicle purchasing platform

### PostgreSQL (Port 5433)
- **Host Connection**: `jdbc:postgresql://localhost:5433/bennycar_db`
- **Container Connection**: `jdbc:postgresql://postgres:5432/bennycar_db`
- **Default Credentials**: Defined in `.env` file

## 🔐 Security Notes

1. **Never expose database ports in production** - Use internal Docker networking
2. **Change default passwords** - Update `.env` file with secure credentials
3. **Use reverse proxy** - Consider nginx/traefik for production deployments
4. **HTTPS in production** - Always use SSL/TLS certificates

## 🚀 Future Ports (Reserved)

| Service          | Planned Port | Purpose                        |
|------------------|--------------|--------------------------------|
| API Gateway      | 8080         | Central API gateway            |
| Vehicle Service  | 8082         | Vehicle catalog management     |
| Order Service    | 8083         | Order processing & payments    |
| Notification Svc | 8084         | Email/SMS notifications        |

## 📝 Development vs Production

### Development (docker-compose)
- Services exposed on localhost
- Direct port mapping from host to container
- Easy debugging and testing

### Production (Kubernetes/Cloud)
- Services internal to cluster
- Load balancer exposes only necessary endpoints
- Environment-specific configurations

## 🛠️ Troubleshooting

### Port Already in Use
```bash
# Check what's using the port
lsof -i :8081

# Kill the process
kill -9 <PID>
```

### Cannot Connect to Service
```bash
# Check if service is running
docker-compose ps

# Check service logs
docker-compose logs user-service

# Verify health
curl http://localhost:8081/actuator/health
```

### Database Connection Issues
```bash
# Test database connection
docker exec -it bennycar-postgres psql -U admin -d bennycar_db

# Check if schemas exist
\dn
```

---

**Last Updated**: November 2025  
**Maintained By**: Bennycar Development Team

