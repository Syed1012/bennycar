#!/bin/bash

# =============================================================================
# BennyCar Services Stop Script
# =============================================================================

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

echo -e "${RED}Stopping all BennyCar services...${NC}"

# Stop services by PID
for pid_file in /tmp/*-service.pid; do
    if [ -f "$pid_file" ]; then
        pid=$(cat "$pid_file")
        service_name=$(basename "$pid_file" .pid)

        if kill -0 "$pid" 2>/dev/null; then
            echo "Stopping $service_name (PID: $pid)..."
            kill "$pid"
            rm "$pid_file"
        fi
    fi
done

# Fallback: kill all spring-boot:run processes
pkill -f "spring-boot:run" || true

echo -e "${GREEN}✓ All services stopped${NC}"
echo ""
echo "Logs are still available at: /tmp/*-service.log"

