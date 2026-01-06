# MQTT Real-Time Coordinate Streaming

This document explains how the World-View service publishes real-time journey updates via MQTT and how the frontend should subscribe to receive them.

---

## Overview

The World-View service uses **MQTT over WebSocket** (via RabbitMQ) to stream real-time car position updates to the frontend. This is more scalable and reliable than Server-Sent Events (SSE).

```
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│  World-View     │  MQTT   │    RabbitMQ     │  MQTT   │    Frontend     │
│  Service        │ ──────> │    Broker       │ ──────> │    (React)      │
│  (Publisher)    │         │  (WebSocket)    │         │  (Subscriber)   │
└─────────────────┘         └─────────────────┘         └─────────────────┘
```

---

## MQTT Topics

### 1. Position Updates Topic

**Topic Pattern:** `nebula/journey/{journeyId}/position`

**Example:** `nebula/journey/journey-abc123/position`

**Purpose:** Real-time coordinate updates sent every 2 seconds as the car moves along the route.

**Message Format:**
```json
{
  "journeyId": "journey-abc123",
  "latitude": 48.8521,
  "longitude": 9.1634,
  "status": "IN_PROGRESS",
  "currentWaypointIndex": 45,
  "totalWaypoints": 150,
  "progressPercentage": 30.0,
  "speedMetersPerSecond": 13.89
}
```

**Fields:**
| Field | Type | Description |
|-------|------|-------------|
| `journeyId` | string | Unique identifier for this journey |
| `latitude` | number | Current latitude of the car |
| `longitude` | number | Current longitude of the car |
| `status` | string | Journey status: `NOT_STARTED`, `IN_PROGRESS`, `PAUSED`, `COMPLETED` |
| `currentWaypointIndex` | number | Current position in the waypoints array |
| `totalWaypoints` | number | Total number of waypoints in the route |
| `progressPercentage` | number | Completion percentage (0-100) |
| `speedMetersPerSecond` | number | Current speed in m/s |

---

### 2. Journey Events Topic

**Topic Pattern:** `nebula/journey/{journeyId}/events`

**Example:** `nebula/journey/journey-abc123/events`

**Purpose:** Lifecycle events for the journey (started, completed).

**Message Format:**
```json
{
  "eventType": "STARTED",
  "data": {
    "journeyId": "journey-abc123",
    "latitude": 48.8973,
    "longitude": 9.1928,
    "status": "IN_PROGRESS",
    "currentWaypointIndex": 0,
    "totalWaypoints": 150,
    "progressPercentage": 0.0,
    "speedMetersPerSecond": 13.89
  }
}
```

**Event Types:**
| Event | Description |
|-------|-------------|
| `STARTED` | Journey has begun, car is at starting position |
| `COMPLETED` | Journey finished, car reached the dealership |

---

## Connection Details

### Development Environment

| Setting | Value |
|---------|-------|
| Broker URL | `ws://localhost:15675/ws` |
| Username | `nebula` |
| Password | `nebula@2025` |
| Protocol | MQTT 5.0 over WebSocket |

### RabbitMQ Ports

| Port | Protocol | Purpose |
|------|----------|---------|
| 5672 | AMQP | Backend messaging |
| 1883 | MQTT | Native MQTT clients |
| 15672 | HTTP | Management UI |
| **15675** | **WebSocket** | **MQTT over WebSocket (Frontend)** |

---

## Frontend Implementation Guide

### 1. Install MQTT Library

```bash
npm install mqtt
```

### 2. Connect to MQTT Broker

```typescript
import mqtt from 'mqtt';

const MQTT_BROKER_URL = 'ws://localhost:15675/ws';

const client = mqtt.connect(MQTT_BROKER_URL, {
  clientId: `frontend-${Math.random().toString(16).slice(2, 10)}`,
  username: 'nebula',
  password: 'nebula@2025',
  clean: true,
  reconnectPeriod: 5000,
});

client.on('connect', () => {
  console.log('Connected to MQTT broker');
});

client.on('error', (error) => {
  console.error('MQTT error:', error);
});
```

### 3. Subscribe to Journey Updates

```typescript
function subscribeToJourney(journeyId: string) {
  const positionTopic = `nebula/journey/${journeyId}/position`;
  const eventsTopic = `nebula/journey/${journeyId}/events`;

  // Subscribe to both topics
  client.subscribe([positionTopic, eventsTopic], { qos: 0 });

  // Handle incoming messages
  client.on('message', (topic, message) => {
    const data = JSON.parse(message.toString());

    if (topic.endsWith('/position')) {
      // Update car marker on map
      updateCarPosition(data.latitude, data.longitude);
      updateProgressBar(data.progressPercentage);
    } else if (topic.endsWith('/events')) {
      if (data.eventType === 'COMPLETED') {
        showCompletionMessage('Car has arrived at the dealership!');
      }
    }
  });
}
```

### 4. Unsubscribe When Done

```typescript
function unsubscribeFromJourney(journeyId: string) {
  const positionTopic = `nebula/journey/${journeyId}/position`;
  const eventsTopic = `nebula/journey/${journeyId}/events`;

  client.unsubscribe([positionTopic, eventsTopic]);
}
```

### 5. Cleanup on Component Unmount

```typescript
useEffect(() => {
  return () => {
    client.end(); // Disconnect when component unmounts
  };
}, []);
```

---

## Complete Flow

### Starting a Journey

1. **Frontend** calls `POST /api/v1/journeys` with journey details
2. **Backend** creates journey and returns initial state
3. **Frontend** subscribes to MQTT topics for that journey ID
4. **Backend** starts publishing position updates every 2 seconds
5. **Frontend** receives updates and moves car marker on map

### During Journey

```
Backend (every 2 seconds):
  └──> Publish to: nebula/journey/{id}/position
         │
         ▼
RabbitMQ (broker):
  └──> Forward to all subscribers
         │
         ▼
Frontend (subscriber):
  └──> Receive message
  └──> Parse JSON
  └──> Update map marker
  └──> Update progress bar
```

### Journey Completion

1. **Backend** publishes `COMPLETED` event to events topic
2. **Frontend** receives event and shows completion message
3. **Frontend** unsubscribes from topics
4. **Backend** stops publishing updates

---

## Error Handling

### Connection Lost

```typescript
client.on('close', () => {
  console.log('Connection lost, will auto-reconnect...');
});

client.on('reconnect', () => {
  console.log('Reconnecting...');
  // Re-subscribe to topics after reconnection
});
```

### Message Parse Errors

```typescript
client.on('message', (topic, message) => {
  try {
    const data = JSON.parse(message.toString());
    handleUpdate(data);
  } catch (error) {
    console.error('Failed to parse MQTT message:', error);
  }
});
```

---

## Testing with MQTT Explorer

You can use [MQTT Explorer](http://mqtt-explorer.com/) to test the MQTT topics:

1. Connect to `mqtt://localhost:1883` (or `ws://localhost:15675/ws`)
2. Username: `nebula_user`, Password: `nebula@2025`
3. Subscribe to `nebula/journey/#` to see all journey messages
4. Start a journey via the API and watch messages flow

---

## Troubleshooting

### Cannot Connect to Broker

1. Ensure RabbitMQ is running: `docker-compose ps rabbitmq`
2. Check MQTT plugin is enabled: `docker exec nebula-rabbitmq rabbitmq-plugins list`
3. Verify WebSocket port 15675 is accessible

### Not Receiving Messages

1. Verify journey is started and running
2. Check topic subscription pattern matches exactly
3. Look at RabbitMQ Management UI for connection status

### Messages Delayed

1. Check network latency
2. Verify backend scheduler is running (check logs)
3. Ensure update interval in config is correct (default: 2000ms)

---

## Configuration Reference

### Backend (application.yaml)

```yaml
mqtt:
  enabled: true
  broker:
    host: localhost
    port: 1883
  username: nebula
  password: nebula@2025
  topic:
    prefix: nebula/journey
```

### Frontend (.env)

```env
NEXT_PUBLIC_MQTT_URL=ws://localhost:15675/ws
NEXT_PUBLIC_MQTT_USERNAME=nebula
NEXT_PUBLIC_MQTT_PASSWORD=nebula@2025
```

---

*For more details, see the [Journey Flow Guide](./JOURNEY_FLOW_GUIDE.md).*

