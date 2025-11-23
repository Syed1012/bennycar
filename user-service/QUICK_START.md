# 🚀 Quick Start Guide

## ✅ Setup Complete!

Your user-service has been successfully configured with professional configuration management.

---

## 📋 What You Have Now

### 🗂️ File Structure

```
user-service/
├── 📄 .env                          ❌ NOT COMMITTED (your secrets)
├── 📄 .env.example                  ✅ COMMITTED (template)
├── 📚 CONFIG_GUIDE.md               ✅ Full documentation
├── 📚 CONFIGURATION_MIGRATION.md    ✅ What changed & why
├── 📚 README.md                     ✅ Project overview
│
└── src/main/
    ├── java/de/bennycar/user/config/
    │   └── DotEnvConfig.java        ✅ Loads .env files
    │
    └── resources/
        ├── application.yml           ✅ Base config (no secrets!)
        ├── application-dev.yml       ✅ Development settings
        ├── application-prod.yml      ✅ Production settings
        └── META-INF/
            └── spring.factories      ✅ Registers DotEnv loader
```

---

## 🎯 How to Run Your Application

### Option 1: Maven (Recommended for Development)

```bash
cd user-service
mvn spring-boot:run
```

### Option 2: JAR File

```bash
mvn clean package
java -jar target/user-service-0.0.1-SNAPSHOT.jar
```

### Option 3: With Specific Profile

```bash
# Run with production profile
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Or
SPRING_PROFILES_ACTIVE=prod java -jar target/user-service-0.0.1-SNAPSHOT.jar
```

---

## 🔍 Verify It's Working

### 1. Check Application Starts

Look for these log messages:

```
✓ .env file loaded successfully                    # DotEnv loaded
The following 1 profile is active: "dev"           # Profile active
Started UserServiceApplication in X.XXX seconds    # App started
```

### 2. Test Health Endpoint

```bash
curl http://localhost:8081/actuator/health

# Expected response:
{
  "status": "UP"
}
```

### 3. Check Profile Configuration

```bash
curl http://localhost:8081/actuator/env | grep "spring.profiles.active"
```

---

## 🎭 Understanding the Setup

### Spring Profiles (Environment Modes)


| Profile  | Purpose           | Settings                               |
| -------- | ----------------- | -------------------------------------- |
| **dev**  | Local development | Verbose logging, auto-update DB schema |
| **prod** | Production        | Minimal logging, strict validation     |

**Activate via .env file:**

```bash
SPRING_PROFILES_ACTIVE=dev
```

### .env Files (Local Secrets)


| File           | Committed to Git? | Purpose                             |
| -------------- | ----------------- | ----------------------------------- |
| `.env.example` | ✅ Yes            | Template for team members           |
| `.env`         | ❌ No             | Your actual secrets (never commit!) |

**Your .env contains:**

```bash
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8081
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/bennycar_db
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=bennyCar123
JWT_SECRET=dev-secret-key-change-this-in-production
```

---

## 🔐 Security Best Practices

### ✅ What We Did Right

1. **No secrets in application.yml** - Only references to environment variables
2. **.env in .gitignore** - Secrets never committed to Git
3. **.env.example as template** - Team members know what variables are needed
4. **Separate profiles** - Dev settings don't leak into production

### ⚠️ Important Notes

- **Never commit .env** - It's in .gitignore, keep it that way!
- **Generate strong JWT secrets** - Use: `openssl rand -base64 32`
- **Different secrets per environment** - Dev, staging, prod should all differ
- **Rotate secrets regularly** - Especially in production

---

## 📚 Documentation Reference

### Quick Links


| Document                                                       | Purpose                      | When to Read       |
| -------------------------------------------------------------- | ---------------------------- | ------------------ |
| **QUICK_START.md** (this file)                                 | Get running quickly          | Right now!         |
| **[CONFIGURATION_MIGRATION.md](./CONFIGURATION_MIGRATION.md)** | Understand what changed      | To learn concepts  |
| **[CONFIG_GUIDE.md](./CONFIG_GUIDE.md)**                       | Full configuration reference | For detailed setup |
| **[README.md](./README.md)**                                   | Project overview & API docs  | For general info   |

---

## 🛠️ Common Tasks

### Change Database Password

```bash
# 1. Edit .env file
nano .env

# 2. Change this line:
SPRING_DATASOURCE_PASSWORD=your_new_password

# 3. Restart application
mvn spring-boot:run
```

### Switch to Production Mode

```bash
# 1. Edit .env file
SPRING_PROFILES_ACTIVE=prod

# 2. Restart application (will use prod settings)
mvn spring-boot:run
```

### Add New Environment Variable

```bash
# 1. Add to .env.example (template)
MY_NEW_CONFIG=example_value

# 2. Add to your .env (actual value)
MY_NEW_CONFIG=actual_secret_value

# 3. Use in application.yml
my-app:
  config: ${MY_NEW_CONFIG}
```

### Generate Strong JWT Secret

```bash
# Generate a 256-bit secret
openssl rand -base64 32

# Copy output and update .env:
JWT_SECRET=<generated-secret-here>
```

---

## 🔧 Troubleshooting

### Problem: Port 8081 already in use

```bash
# Find what's using the port
lsof -i :8081

# Kill the process
kill <PID>

# Or change port in .env
SERVER_PORT=8082
```

### Problem: Database connection failed

```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Test connection manually
psql -h localhost -p 5433 -U admin -d bennycar_db

# Verify .env has correct database settings
cat .env | grep SPRING_DATASOURCE
```

### Problem: .env not loading

```bash
# Verify .env exists in correct location
ls -la user-service/.env

# Check file permissions
chmod 600 user-service/.env

# Look for log message during startup:
# "✓ .env file loaded successfully"
```

### Problem: Wrong profile active

```bash
# Check .env file
cat .env | grep SPRING_PROFILES_ACTIVE

# Override via command line
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
