#!/bin/bash

# =============================================================================
# BennyCar Services Startup Script
# =============================================================================
# Starts all microservices in the correct order with proper configuration
# =============================================================================

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

SERVICES_DIR="/Users/syed/Documents/PSE/bennycar/services"

echo -e "${BLUE}=========================================="
echo "BennyCar Services Startup"
echo -e "==========================================${NC}"
echo ""

# Function to check if port is in use
check_port() {
    local port=$1
    lsof -i :$port > /dev/null 2>&1
    return $?
}

# Function to wait for service
wait_for_service() {
    local url=$1
    local name=$2
    local max_attempts=30
    local attempt=1

    echo -e "${YELLOW}Waiting for $name to start...${NC}"

    while [ $attempt -le $max_attempts ]; do
        if curl -s "$url" > /dev/null 2>&1; then
            echo -e "${GREEN}✓ $name is ready!${NC}"
            return 0
        fi
        echo -n "."
        sleep 2
        attempt=$((attempt + 1))
    done

    echo -e "${RED}✗ $name failed to start after $max_attempts attempts${NC}"
    return 1
}

# Stop any running services
echo -e "${YELLOW}Stopping any running services...${NC}"
pkill -f "spring-boot:run" || true
sleep 2

# Check Docker services
echo ""
echo -e "${BLUE}Checking Docker services...${NC}"
cd /Users/syed/Documents/PSE/bennycar/docker

if ! docker compose ps | grep -q "bennycar-postgres.*Up"; then
    echo -e "${YELLOW}Starting Docker services...${NC}"
    docker compose up -d
    sleep 10
fi

echo -e "${GREEN}✓ Docker services running${NC}"

# Start services in order
echo ""
echo -e "${BLUE}Starting microservices...${NC}"
echo ""

# 1. User Service (Port 8081)
echo -e "${BLUE}[1/5] Starting User Service (port 8081)...${NC}"
if check_port 8081; then
    echo -e "${YELLOW}Port 8081 already in use, skipping...${NC}"
else
    cd "$SERVICES_DIR/user-service"
    nohup mvn spring-boot:run -Dspring-boot.run.profiles=dev > /tmp/user-service.log 2>&1 &
    echo $! > /tmp/user-service.pid
    wait_for_service "http://localhost:8081/actuator/health" "User Service"
fi

# 2. Vehicle Service (Port 8082)
echo ""
echo -e "${BLUE}[2/5] Starting Vehicle Service (port 8082)...${NC}"
if check_port 8082; then
    echo -e "${YELLOW}Port 8082 already in use, skipping...${NC}"
else
    cd "$SERVICES_DIR/vehicle-service"
    nohup mvn spring-boot:run -Dspring-boot.run.profiles=dev > /tmp/vehicle-service.log 2>&1 &
    echo $! > /tmp/vehicle-service.pid
    wait_for_service "http://localhost:8082/actuator/health" "Vehicle Service"
fi

# 3. Order Service (Port 8083)
echo ""
echo -e "${BLUE}[3/5] Starting Order Service (port 8083)...${NC}"
if check_port 8083; then
    echo -e "${YELLOW}Port 8083 already in use, skipping...${NC}"
else
    cd "$SERVICES_DIR/order-service"
    nohup mvn spring-boot:run -Dspring-boot.run.profiles=dev > /tmp/order-service.log 2>&1 &
    echo $! > /tmp/order-service.pid
    wait_for_service "http://localhost:8083/actuator/health" "Order Service"
fi

# 4. World View Service (Port 8084)
echo ""
echo -e "${BLUE}[4/5] Starting World View Service (port 8084)...${NC}"
if check_port 8084; then
    echo -e "${YELLOW}Port 8084 already in use, skipping...${NC}"
else
    cd "$SERVICES_DIR/world-view"
    nohup mvn spring-boot:run -Dspring-boot.run.profiles=dev > /tmp/world-view.log 2>&1 &
    echo $! > /tmp/world-view.pid
    wait_for_service "http://localhost:8084/actuator/health" "World View Service"
fi

# 5. Gateway Service (Port 8080)
echo ""
echo -e "${BLUE}[5/5] Starting Gateway Service (port 8080)...${NC}"
if check_port 8080; then
    echo -e "${YELLOW}Port 8080 already in use, skipping...${NC}"
else
    cd "$SERVICES_DIR/gateway-service"
    nohup mvn spring-boot:run -Dspring-boot.run.profiles=dev > /tmp/gateway-service.log 2>&1 &
    echo $! > /tmp/gateway-service.pid
    wait_for_service "http://localhost:8080/actuator/health" "Gateway Service"
fi

echo ""
echo -e "${GREEN}=========================================="
echo "✓ All Services Started Successfully!"
echo -e "==========================================${NC}"
echo ""
echo "Service URLs:"
echo "  Gateway:      http://localhost:8080"
echo "  User:         http://localhost:8081"
echo "  Vehicle:      http://localhost:8082"
echo "  Order:        http://localhost:8083"
echo "  World View:   http://localhost:8084"
echo ""
echo "Docker Services:"
echo "  PostgreSQL:   localhost:5433"
echo "  RabbitMQ:     http://localhost:15672 (guest/guest)"
echo "  SonarQube:    http://localhost:9000"
echo ""
echo "Logs location: /tmp/*-service.log"
echo "PIDs location: /tmp/*-service.pid"
echo ""
echo -e "${YELLOW}To stop all services, run: ./stop-services.sh${NC}"
echo ""

