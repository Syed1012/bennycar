# User Service Configuration Guide

## 📚 Table of Contents
- [Overview](#overview)
- [Configuration Strategy](#configuration-strategy)
- [Spring Profiles](#spring-profiles)
- [Environment Variables](#environment-variables)
- [Local Development Setup](#local-development-setup)
- [Production Deployment](#production-deployment)
- [Security Best Practices](#security-best-practices)
- [Troubleshooting](#troubleshooting)

---

## 🎯 Overview

This service uses industry-standard configuration management practices:
- **Spring Profiles** for environment-specific configurations
- **Environment Variables** for sensitive data
- **`.env` files** for local development (via dotenv-java)
- **No hardcoded secrets** in version control

## 🏗️ Configuration Strategy

### Configuration Hierarchy (Priority Order)
1. **Command-line arguments** (highest priority)
2. **System environment variables**
3. **`.env` file** (loaded by dotenv-java)
4. **Profile-specific files** (`application-{profile}.yml`)
5. **Main application file** (`application.yml` with defaults)

### File Structure
```
src/main/resources/
├── application.yml              # Base configuration (committed to Git)
├── application-dev.yml          # Development profile (committed to Git)
├── application-prod.yml         # Production profile (committed to Git)
├── .env.example                 # Template (committed to Git)
└── .env                         # Actual secrets (NEVER commit - in .gitignore)
```

---

## 🎭 Spring Profiles

### What are Spring Profiles?
Spring Profiles allow you to define different configurations for different environments (dev, test, prod). Each profile can override or add to the base configuration.

### Available Profiles

#### 1. **Development Profile** (`dev`)
- **Purpose**: Local development
- **Features**:
  - Verbose logging (DEBUG level)
  - Auto-update database schema
  - SQL query logging
  - Full actuator endpoints exposed
  - Detailed error messages

#### 2. **Production Profile** (`prod`)
- **Purpose**: Production deployment
- **Features**:
  - Minimal logging (WARN/INFO level)
  - Schema validation only (no auto-updates)
  - Limited actuator endpoints
  - Graceful shutdown
  - No error details exposed to clients

### Activating Profiles

#### Method 1: Using .env file (Recommended for local development)
```bash
# In your .env file
SPRING_PROFILES_ACTIVE=dev
```

#### Method 2: Command-line argument
```bash
java -jar user-service.jar --spring.profiles.active=dev
```

#### Method 3: Environment variable
```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar user-service.jar
```

#### Method 4: Maven (for development)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## 🔐 Environment Variables

### Required Variables

| Variable | Description | Example | Required |
|----------|-------------|---------|----------|
| `SPRING_PROFILES_ACTIVE` | Active profile | `dev` or `prod` | Yes |
| `SERVER_PORT` | Server port | `8081` | Yes |
| `SPRING_DATASOURCE_URL` | Database connection URL | `jdbc:postgresql://localhost:5433/bennycar_db` | Yes |
| `SPRING_DATASOURCE_USERNAME` | Database username | `admin` | Yes |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `secure_password` | Yes |
| `JWT_SECRET` | JWT signing secret (min 256 bits) | `<generated-secret>` | Yes |
| `JWT_EXPIRATION` | Token expiration (milliseconds) | `86400000` (24h) | No (has default) |

### Optional Variables (with defaults)

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Schema management | `validate` |
| `LOG_LEVEL_ROOT` | Root logging level | `INFO` |
| `LOG_LEVEL_APP` | Application logging level | `INFO` |
| `LOG_LEVEL_SECURITY` | Security logging level | `INFO` |
| `HIBERNATE_FORMAT_SQL` | Format SQL output | `false` |
| `JPA_SHOW_SQL` | Show SQL in console | `false` |
| `ACTUATOR_HEALTH_DETAILS` | Health endpoint detail level | `when-authorized` |

---

## 💻 Local Development Setup

### Step 1: Install Dependencies
```bash
cd user-service
mvn clean install
```

### Step 2: Create Your .env File
```bash
# Copy the template
cp .env.example .env

# Edit .env with your actual values
nano .env  # or use your preferred editor
```

### Step 3: Configure Database
Make sure your PostgreSQL database is running:
```bash
# Using Docker (if you have docker-compose)
docker-compose up -d postgres

# Or start your local PostgreSQL instance
```

### Step 4: Generate a Strong JWT Secret
```bash
# Generate a secure secret key
openssl rand -base64 32

# Copy the output and set it in your .env file
JWT_SECRET=<generated-secret>
```

### Step 5: Run the Application
```bash
# Option 1: Using Maven
mvn spring-boot:run

# Option 2: Using JAR
mvn clean package
java -jar target/user-service-0.0.1-SNAPSHOT.jar

# Option 3: Using Maven with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Verification
You should see:
```
✓ .env file loaded successfully
...
Started UserServiceApplication in X.XXX seconds
```

Visit: http://localhost:8081/actuator/health

---

## 🚀 Production Deployment

### Prerequisites
1. **Never use .env files in production** - Use proper environment variables
2. Use a secrets management service (AWS Secrets Manager, Azure Key Vault, HashiCorp Vault)
3. Enable HTTPS/TLS
4. Use a reverse proxy (Nginx, Apache)
5. Set up monitoring and logging

### Docker Deployment

#### 1. Set environment variables in docker-compose.yml
```yaml
services:
  user-service:
    image: bennycar/user-service:latest
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SERVER_PORT=8081
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/bennycar_db
      - SPRING_DATASOURCE_USERNAME=admin
      - SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}  # From .env (not committed)
      - JWT_SECRET=${JWT_SECRET}  # From .env (not committed)
      - JWT_EXPIRATION=86400000
```

#### 2. Create a production .env file (separate from code repo)
```bash
# Store this securely, NOT in your code repository
DB_PASSWORD=super_secure_production_password
JWT_SECRET=production_jwt_secret_256_bits_minimum
```

### Kubernetes Deployment

#### 1. Create a Secret
```bash
kubectl create secret generic user-service-secrets \
  --from-literal=jwt-secret=your-secret-here \
  --from-literal=db-password=your-password-here
```

#### 2. Reference in Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
spec:
  template:
    spec:
      containers:
      - name: user-service
        image: bennycar/user-service:latest
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: user-service-secrets
              key: jwt-secret
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: user-service-secrets
              key: db-password
```

### Cloud Platform Deployment

#### AWS Elastic Beanstalk
```bash
# Set environment variables in EB console or CLI
eb setenv SPRING_PROFILES_ACTIVE=prod \
  JWT_SECRET=your-secret \
  SPRING_DATASOURCE_PASSWORD=your-password
```

#### Heroku
```bash
heroku config:set SPRING_PROFILES_ACTIVE=prod
heroku config:set JWT_SECRET=your-secret
heroku config:set SPRING_DATASOURCE_URL=jdbc:postgresql://...
```

---

## 🔒 Security Best Practices

### 1. JWT Secret Management
```bash
# Generate a strong secret (minimum 256 bits)
openssl rand -base64 32

# For production, use even longer secrets
openssl rand -base64 64
```

### 2. Password Security
- Never use default passwords in production
- Use password managers to generate and store passwords
- Rotate credentials regularly
- Use different passwords for each environment

### 3. Database Security
- Use separate database users with minimal required permissions
- Enable SSL/TLS for database connections in production
- Regularly update and patch PostgreSQL

### 4. Environment File Security
```bash
# Verify .env is in .gitignore
git check-ignore .env
# Should output: .env

# Check if .env was accidentally committed
git log --all --full-history -- "*/.env"
# Should be empty

# If accidentally committed, remove from history
git filter-branch --force --index-filter \
  "git rm --cached --ignore-unmatch user-service/.env" \
  --prune-empty --tag-name-filter cat -- --all
```

### 5. Secrets Rotation
- Rotate JWT secrets periodically (e.g., every 90 days)
- When rotating, consider a grace period with two valid secrets
- Update all environment configurations simultaneously

### 6. Audit and Monitoring
- Enable audit logging in production
- Monitor for unusual authentication patterns
- Set up alerts for failed authentication attempts
- Review logs regularly

---

## 🔧 Troubleshooting

### Issue: .env file not loading

**Symptoms**: Application fails to start, complaining about missing configuration

**Solutions**:
1. Verify .env file exists in the correct location:
   ```bash
   ls -la user-service/.env
   ```

2. Check file permissions:
   ```bash
   chmod 600 user-service/.env
   ```

3. Verify dotenv-java dependency is included:
   ```bash
   mvn dependency:tree | grep dotenv
   ```

4. Check application logs for:
   ```
   ✓ .env file loaded successfully
   ```

### Issue: Wrong profile is active

**Symptoms**: Application uses wrong configuration

**Solutions**:
1. Check which profile is active:
   ```bash
   curl http://localhost:8081/actuator/env | grep "spring.profiles.active"
   ```

2. Verify .env file has correct value:
   ```bash
   cat .env | grep SPRING_PROFILES_ACTIVE
   ```

3. Check for conflicting environment variables:
   ```bash
   printenv | grep SPRING_PROFILES_ACTIVE
   ```

### Issue: Database connection fails

**Symptoms**: Connection timeout or authentication failure

**Solutions**:
1. Verify database is running:
   ```bash
   docker ps | grep postgres
   # or
   pg_isready -h localhost -p 5433
   ```

2. Test connection manually:
   ```bash
   psql -h localhost -p 5433 -U admin -d bennycar_db
   ```

3. Check .env database configuration:
   ```bash
   cat .env | grep SPRING_DATASOURCE
   ```

4. Verify schema exists:
   ```sql
   \dn  -- List schemas in psql
   ```

### Issue: JWT authentication fails

**Symptoms**: Token validation errors

**Solutions**:
1. Verify JWT_SECRET is set and consistent:
   ```bash
   cat .env | grep JWT_SECRET
   ```

2. Check secret length (minimum 256 bits / 32 characters):
   ```bash
   echo -n "$JWT_SECRET" | wc -c
   ```

3. Ensure the same secret is used for signing and validation

4. Check token expiration time:
   ```bash
   cat .env | grep JWT_EXPIRATION
   ```

### Issue: Port already in use

**Symptoms**: `Port 8081 is already in use`

**Solutions**:
1. Find the process using the port:
   ```bash
   lsof -i :8081
   ```

2. Kill the process:
   ```bash
   kill <PID>
   # or force kill
   kill -9 <PID>
   ```

3. Change the port in .env:
   ```bash
   SERVER_PORT=8082
   ```

### Getting Help

If you encounter issues:
1. Check application logs: `logs/application.log`
2. Enable DEBUG logging: `LOG_LEVEL_ROOT=DEBUG` in .env
3. Verify all required environment variables are set
4. Check database connectivity
5. Ensure PostgreSQL schema `user_service` exists

---

## 📚 Additional Resources

### Official Documentation
- [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Spring Profiles](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.profiles)
- [dotenv-java Documentation](https://github.com/cdimascio/dotenv-java)

### Best Practices
- [12-Factor App Methodology](https://12factor.net/config)
- [OWASP Secrets Management](https://cheatsheetseries.owasp.org/cheatsheets/Secrets_Management_Cheat_Sheet.html)

### Tools
- [OpenSSL](https://www.openssl.org/) - Generate secure secrets
- [HashiCorp Vault](https://www.vaultproject.io/) - Secrets management
- [AWS Secrets Manager](https://aws.amazon.com/secrets-manager/)
- [Azure Key Vault](https://azure.microsoft.com/en-us/services/key-vault/)

---

## 📝 Quick Reference

### Development Commands
```bash
# Setup
cp .env.example .env
mvn clean install

# Run
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Build
mvn clean package

# Run JAR
java -jar target/user-service-0.0.1-SNAPSHOT.jar
```

### Production Commands
```bash
# Build for production
mvn clean package -Pprod

# Run with production profile
java -jar target/user-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# Run with external config
java -jar target/user-service-0.0.1-SNAPSHOT.jar --spring.config.location=/etc/bennycar/
```

### Useful Endpoints
```bash
# Health check
curl http://localhost:8081/actuator/health

# Detailed health (dev profile)
curl http://localhost:8081/actuator/health | jq

# Check environment (dev profile)
curl http://localhost:8081/actuator/env | jq

# Application info
curl http://localhost:8081/actuator/info
```

---

**Last Updated**: November 2025
**Version**: 1.0.0

