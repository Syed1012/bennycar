# World-View Service: Complete Journey Flow Guide

> A beginner-friendly explanation of how the world-view service works, from the moment you click "Start Journey" to when the car reaches its destination.

---

## 📋 Table of Contents

1. [Big Picture Overview](#big-picture-overview)
2. [Architecture Comparison (What You Know vs. What's New)](#architecture-comparison)
3. [The Complete Journey Flow](#the-complete-journey-flow)
4. [Understanding Each Layer](#understanding-each-layer)
5. [Database (PostgreSQL) Purpose](#database-postgresql-purpose)
6. [Real-Time Updates (SSE & MQTT)](#real-time-updates-sse--mqtt)
7. [Visual Flow Diagram](#visual-flow-diagram)

---

## Big Picture Overview

Imagine you're building a **car simulation app** where:
- A car starts at Location A (e.g., Ludwigsburg)
- The car travels along a predefined route with many waypoints
- The car reaches Location B (Porsche Zentrum Stuttgart)
- The frontend shows the car moving on a map in **real-time**

The **world-view service** is the backend that makes this happen!

### What Does This Service Do?

| Component | What It Does |
|-----------|--------------|
| **Routes** | Pre-defined paths (like Google Maps routes) stored in PostgreSQL |
| **Journeys** | A car traveling on a route - tracks position, speed, progress |
| **Real-time Updates** | Sends car position to frontend every 500ms via SSE or MQTT |

---

## Architecture Comparison

### What You Already Know (Traditional Spring Boot)

```
┌─────────────┐     ┌─────────────┐     ┌──────────────┐     ┌────────────┐
│ Controller  │ --> │   Service   │ --> │  Repository  │ --> │  Database  │
└─────────────┘     └─────────────┘     └──────────────┘     └────────────┘
```

In traditional Spring Boot:
- **Controller** = Receives HTTP requests
- **Service** = Business logic
- **Repository** = Database operations
- They're all directly connected

### Hexagonal Architecture (What This Project Uses)

```
                    ┌──────────────────────────────────────┐
                    │           DOMAIN (Core)              │
                    │  ┌─────────────────────────────┐     │
  INBOUND           │  │   Models & Business Logic   │     │        OUTBOUND
  (Driving Side)    │  │   - Coordinate              │     │        (Driven Side)
                    │  │   - DrivingRoute            │     │
┌───────────────┐   │  │   - JourneyState            │     │   ┌───────────────┐
│  Controllers  │   │  └─────────────────────────────┘     │   │   Database    │
│  (REST API)   │──>│                                      │──>│  (PostgreSQL) │
└───────────────┘   │  ┌─────────────────────────────┐     │   └───────────────┘
                    │  │         PORTS                │     │
                    │  │  Inbound:  JourneyUseCase    │     │   ┌───────────────┐
                    │  │            RouteUseCase      │     │──>│   Messaging   │
                    │  │                              │     │   │  (SSE/MQTT)   │
                    │  │  Outbound: RouteRepository   │     │   └───────────────┘
                    │  │            JourneyStateRepo  │     │
                    │  │            CoordinatePublisher│    │
                    │  └─────────────────────────────┘     │
                    └──────────────────────────────────────┘
```

### The Key Difference - PORTS

Think of **ports** like electrical outlets in your home:

| Traditional | Hexagonal |
|-------------|-----------|
| Service directly calls Repository | Service calls a **Port (interface)** |
| If you change database, you change service | Change only the **Adapter** (implementation) |
| Everything is tightly connected | Loose coupling, easy to swap parts |

**Analogy:**
- **Port** = The shape of an electrical outlet (standard interface)
- **Adapter** = The plug that fits into that outlet (implementation)
- You can plug in any device (PostgreSQL, MongoDB, InMemory) as long as it fits the outlet!

---

## The Complete Journey Flow

Let's trace what happens when you click **"Start Journey"** on the frontend.

### Step-by-Step Flow

```
┌────────────────────────────────────────────────────────────────────────────┐
│                           FRONTEND (React/Next.js)                         │
│                                                                            │
│   1. User clicks "Start Journey"                                           │
│   2. Frontend calls: POST /api/v1/journeys                                 │
│   3. Frontend also subscribes to: GET /api/v1/journeys/{id}/stream (SSE)   │
└─────────────────────────────────────┬──────────────────────────────────────┘
                                      │
                                      ▼
┌────────────────────────────────────────────────────────────────────────────┐
│               STEP 1: JourneyController receives the request               │
│                                                                            │
│   File: infrastructure/adapter/inbound/web/controller/JourneyController.java│
│                                                                            │
│   @PostMapping                                                             │
│   public ResponseEntity<JourneyStateDto> startJourney(StartJourneyRequest) │
│   {                                                                        │
│       // 1. Calls the use case (port) to start journey                     │
│       JourneyState journeyState = journeyUseCase.startNewJourney(...);     │
│                                                                            │
│       // 2. Registers journey for scheduled updates (every 500ms)          │
│       journeySchedulerService.registerJourney(journeyId);                  │
│                                                                            │
│       // 3. Returns the initial state to frontend                          │
│       return ResponseEntity.status(HttpStatus.CREATED).body(dto);          │
│   }                                                                        │
└─────────────────────────────────────┬──────────────────────────────────────┘
                                      │
                                      ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                  STEP 2: JourneyService (Application Layer)                │
│                                                                            │
│   File: application/service/JourneyService.java                            │
│   Implements: JourneyUseCase (inbound port)                                │
│                                                                            │
│   public JourneyState startNewJourney(journeyId, speed) {                  │
│       // 1. Get a random route from database                               │
│       DrivingRoute route = routeUseCase.getRandomRoute();                  │
│                                                                            │
│       // 2. Create a new JourneyState object                               │
│       JourneyState journeyState = new JourneyState(journeyId, route, speed);│
│       journeyState.start();                                                │
│                                                                            │
│       // 3. Save to repository (in-memory for journeys)                    │
│       journeyStateRepository.save(journeyState);                           │
│                                                                            │
│       // 4. Publish "journey started" event                                │
│       coordinatePublisher.publishJourneyStarted(journeyState);             │
│                                                                            │
│       return journeyState;                                                 │
│   }                                                                        │
└─────────────────────────────────────┬──────────────────────────────────────┘
                                      │
                                      ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                STEP 3: RouteService fetches route from DB                  │
│                                                                            │
│   File: application/service/RouteService.java                              │
│                                                                            │
│   public DrivingRoute getRandomRoute() {                                   │
│       List<DrivingRoute> routes = routeRepository.findAll();               │
│       // Picks a random route from the 8 predefined routes                 │
│       return routes.get(randomIndex);                                      │
│   }                                                                        │
│                                                                            │
│   The routeRepository is actually JpaRouteRepositoryAdapter which          │
│   talks to PostgreSQL!                                                     │
└─────────────────────────────────────┬──────────────────────────────────────┘
                                      │
                                      ▼
┌────────────────────────────────────────────────────────────────────────────┐
│               STEP 4: JourneySchedulerService kicks in                     │
│                                                                            │
│   File: application/service/JourneySchedulerService.java                   │
│                                                                            │
│   // Runs automatically every 500ms (configured in application.yaml)       │
│   @Scheduled(fixedRateString = "${journey.scheduler.update-interval-ms}")  │
│   public void updateActiveJourneys() {                                     │
│       for (String journeyId : activeJourneyIds) {                          │
│           processJourney(journeyId);  // Move the car!                     │
│       }                                                                    │
│   }                                                                        │
│                                                                            │
│   private void processJourney(journeyId) {                                 │
│       // Advance the journey by 0.5 seconds worth of travel                │
│       journeyUseCase.advanceJourney(journeyId, 0.5);                       │
│   }                                                                        │
└─────────────────────────────────────┬──────────────────────────────────────┘
                                      │
                                      ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                 STEP 5: JourneyService.advanceJourney()                    │
│                                                                            │
│   This is where the magic happens - the car actually moves!                │
│                                                                            │
│   public Coordinate advanceJourney(journeyId, elapsedSeconds) {            │
│       JourneyState state = journeyStateRepository.findById(journeyId);     │
│                                                                            │
│       // Move the car forward based on speed and time                      │
│       boolean completed = state.advance(elapsedSeconds);                   │
│       // Example: speed=50m/s, time=0.5s → car moves 25 meters             │
│                                                                            │
│       // Save updated position                                             │
│       journeyStateRepository.save(state);                                  │
│                                                                            │
│       // ★ IMPORTANT: Publish the new position to frontend!                │
│       coordinatePublisher.publishCoordinateUpdate(journeyId, position);    │
│                                                                            │
│       if (completed) {                                                     │
│           coordinatePublisher.publishJourneyCompleted(state);              │
│       }                                                                    │
│   }                                                                        │
└─────────────────────────────────────┬──────────────────────────────────────┘
                                      │
                                      ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                 STEP 6: CompositeCoordinatePublisher                       │
│                                                                            │
│   File: infrastructure/adapter/outbound/messaging/                         │
│         CompositeCoordinatePublisherAdapter.java                           │
│                                                                            │
│   This is a "composite" - it sends updates to MULTIPLE channels!           │
│                                                                            │
│   public void publishCoordinateUpdate(journeyId, coordinate, state) {      │
│       // Send via SSE (Server-Sent Events)                                 │
│       ssePublisher.publishCoordinateUpdate(...);                           │
│                                                                            │
│       // Send via MQTT (if enabled)                                        │
│       mqttPublisher.publishCoordinateUpdate(...);                          │
│   }                                                                        │
│                                                                            │
│   ┌─────────────────┐                                                      │
│   │  SSE Publisher  │ ────> Browser EventSource API                        │
│   └─────────────────┘                                                      │
│   ┌─────────────────┐                                                      │
│   │ MQTT Publisher  │ ────> RabbitMQ ────> MQTT WebSocket clients          │
│   └─────────────────┘                                                      │
└─────────────────────────────────────┬──────────────────────────────────────┘
                                      │
                                      ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                    STEP 7: Frontend receives update                        │
│                                                                            │
│   File: frontend/world-view-FE/lib/api.ts                                  │
│                                                                            │
│   // Frontend subscribed earlier via EventSource                           │
│   eventSource.addEventListener('coordinate-update', (event) => {           │
│       const data = JSON.parse(event.data);                                 │
│       // Update the car marker on the map!                                 │
│       updateCarPosition(data.latitude, data.longitude);                    │
│   });                                                                      │
│                                                                            │
│   // When journey completes                                                │
│   eventSource.addEventListener('journey-completed', (event) => {           │
│       showMessage("Car has arrived at Porsche Zentrum Stuttgart!");        │
│   });                                                                      │
└────────────────────────────────────────────────────────────────────────────┘
```

---

## Understanding Each Layer

### 1. Infrastructure Layer (Outer Ring)
**Location:** `infrastructure/adapter/`

This is where ALL external things live:
- **inbound/web/** → REST Controllers, DTOs, Exception handlers
- **outbound/persistence/** → Database access (PostgreSQL)
- **outbound/messaging/** → SSE and MQTT publishers

**Think of it as:** The "shell" that connects the outside world to your business logic.

### 2. Application Layer (Middle Ring)
**Location:** `application/service/`

This is where **use cases** are implemented:
- `JourneyService` → Implements `JourneyUseCase`
- `RouteService` → Implements `RouteUseCase`
- `JourneySchedulerService` → Runs the periodic updates

**Think of it as:** The "coordinator" that knows what to do but not how to do infrastructure stuff.

### 3. Domain Layer (Inner Core)
**Location:** `domain/`

This is the heart of the application:
- `model/` → Coordinate, DrivingRoute, JourneyState
- `port/inbound/` → Use case interfaces (what the app can do)
- `port/outbound/` → Repository interfaces (what the app needs)
- `exception/` → Domain-specific exceptions

**Think of it as:** Pure business logic with ZERO external dependencies.

---

## Database (PostgreSQL) Purpose

### What's Stored in PostgreSQL?

| Table | Purpose | Example |
|-------|---------|---------|
| `routes` | Predefined driving routes | "Ludwigsburg → Porsche Zentrum" |
| `waypoints` | GPS coordinates for each route | List of 100+ coordinates per route |

### Why PostgreSQL and Not In-Memory?

**Routes are stored in PostgreSQL because:**
1. They are **permanent data** - routes don't change
2. They have **many waypoints** (some routes have 500+ coordinates)
3. They survive server restarts

**Journey states are stored in memory because:**
1. They are **temporary** - only exist while journey is active
2. They change **every 500ms** - too fast for database
3. They are **recreated** if server restarts

### How Routes Get Into Database

```java
// JpaRouteRepositoryAdapter.java
@PostConstruct  // Runs when application starts
public void initializeRoutes() {
    if (database is empty) {
        // Save 8 predefined routes with all their waypoints
        saveRoute(createRoute1FromLudwigsburg());
        saveRoute(createRoute2FromFavoritepark());
        // ... 6 more routes
    }
}
```

---

## Real-Time Updates (SSE & MQTT)

### What's SSE (Server-Sent Events)?

SSE is like a **one-way radio broadcast**:
- Server keeps the connection open
- Server pushes data to client whenever it wants
- Client just listens

```
┌────────────┐                           ┌────────────┐
│  Backend   │ ──── coordinate update ───>│  Frontend  │
│            │ ──── coordinate update ───>│            │
│            │ ──── journey completed ───>│            │
└────────────┘                           └────────────┘
     |
     |  (Connection stays open)
     |
```

**In code:**
```java
// Backend sends
sseEmitter.send(SseEmitter.event()
    .name("coordinate-update")
    .data(coordinateDto));
```

```typescript
// Frontend receives
eventSource.addEventListener('coordinate-update', (event) => {
    const position = JSON.parse(event.data);
});
```

### What's MQTT with RabbitMQ?

MQTT is a **messaging protocol** often used in IoT:
- Backend publishes to a **topic** (like a TV channel)
- Frontend subscribes to that topic
- RabbitMQ is the **broker** (the TV station)

```
┌────────────┐     ┌────────────┐     ┌────────────┐
│  Backend   │ ──> │  RabbitMQ  │ ──> │  Frontend  │
│ (Publisher)│     │  (Broker)  │     │(Subscriber)│
└────────────┘     └────────────┘     └────────────┘
       |                 |                  |
       |    Topic: nebula/journey/123/position
       |
```

**Topic structure:**
- `nebula/journey/{journeyId}/position` → Car position updates
- `nebula/journey/{journeyId}/events` → Started, Completed events

### Why Two Methods (SSE + MQTT)?

| SSE | MQTT |
|-----|------|
| Simpler to implement | More scalable |
| Built into browsers | Needs MQTT library |
| Direct connection | Via message broker |
| Good for single server | Good for distributed systems |

The service supports **both** - frontend can choose which to use!

---

## Visual Flow Diagram

```
                    ┌─────────────────────────────────────────────────────────────┐
                    │                      FRONTEND                               │
                    │  ┌───────────┐                        ┌───────────────┐     │
                    │  │ Start Btn │                        │   Map View    │     │
                    │  └─────┬─────┘                        └───────▲───────┘     │
                    └────────┼──────────────────────────────────────┼─────────────┘
                             │ POST /api/v1/journeys                │ SSE Updates
                             ▼                                      │
┌────────────────────────────────────────────────────────────────────────────────────┐
│                              WORLD-VIEW SERVICE                                     │
│                                                                                    │
│  ┌──────────────────────────────────────────────────────────────────────────────┐ │
│  │ INFRASTRUCTURE LAYER (Adapters)                                               │ │
│  │                                                                               │ │
│  │  ┌─────────────────┐         ┌─────────────────┐      ┌─────────────────┐    │ │
│  │  │JourneyController│         │ SsePublisher    │      │ PostgreSQL      │    │ │
│  │  │                 │         │ (sends updates) │      │ Adapter         │    │ │
│  │  └────────┬────────┘         └────────▲────────┘      └────────▲────────┘    │ │
│  └───────────┼──────────────────────────┼─────────────────────────┼─────────────┘ │
│              │                          │                         │               │
│  ┌───────────┼──────────────────────────┼─────────────────────────┼─────────────┐ │
│  │ APPLICATION LAYER                    │                         │             │ │
│  │           │                          │                         │             │ │
│  │  ┌────────▼────────┐   ┌─────────────┴───────────┐            │             │ │
│  │  │ JourneyService  │   │JourneySchedulerService  │            │             │ │
│  │  │ (starts journey)│<──│ (runs every 500ms)      │            │             │ │
│  │  └────────┬────────┘   └─────────────────────────┘            │             │ │
│  │           │                                                    │             │ │
│  │  ┌────────▼────────┐                                          │             │ │
│  │  │  RouteService   │──────────────────────────────────────────┘             │ │
│  │  │(fetches routes) │                                                        │ │
│  │  └─────────────────┘                                                        │ │
│  └──────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                    │
│  ┌──────────────────────────────────────────────────────────────────────────────┐ │
│  │ DOMAIN LAYER (Core Business Logic)                                           │ │
│  │                                                                               │ │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐              │ │
│  │  │   Coordinate    │  │  DrivingRoute   │  │  JourneyState   │              │ │
│  │  │ (lat, lng)      │  │ (route data)    │  │ (position, %)   │              │ │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘              │ │
│  │                                                                               │ │
│  │  ┌─────────────────────────────────────────────────────────────────────────┐ │ │
│  │  │ PORTS (Interfaces)                                                       │ │ │
│  │  │                                                                          │ │ │
│  │  │  Inbound:  JourneyUseCase, RouteUseCase                                 │ │ │
│  │  │  Outbound: RouteRepository, JourneyStateRepository, CoordinatePublisher │ │ │
│  │  └─────────────────────────────────────────────────────────────────────────┘ │ │
│  └──────────────────────────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────────────────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │   PostgreSQL    │
                    │   (routes &     │
                    │    waypoints)   │
                    └─────────────────┘
```

---

## Quick Reference: File Locations

| What | File Location |
|------|---------------|
| REST API endpoints | `infrastructure/adapter/inbound/web/controller/` |
| Business logic | `application/service/` |
| Domain models | `domain/model/` |
| Port interfaces | `domain/port/inbound/` and `domain/port/outbound/` |
| Database adapters | `infrastructure/adapter/outbound/persistence/` |
| SSE/MQTT adapters | `infrastructure/adapter/outbound/messaging/` |

---

## Summary

1. **Frontend** calls `POST /api/v1/journeys` to start
2. **JourneyController** receives and delegates to `JourneyService`
3. **JourneyService** creates journey, saves it, publishes "started" event
4. **JourneySchedulerService** runs every 500ms and calls `advanceJourney()`
5. **JourneyState.advance()** calculates new position based on speed × time
6. **CoordinatePublisher** sends position to frontend via SSE/MQTT
7. **Frontend** receives updates and moves the car on the map
8. When journey completes, "journey-completed" event is sent

The hexagonal architecture ensures:
- Business logic (domain) is **independent** of frameworks
- You can swap PostgreSQL for MongoDB just by changing the adapter
- You can add new publishing channels (WebSocket, Kafka) easily
- Testing is easier because you can mock the ports

---

*This guide was created to help understand the world-view service architecture. For code-level details, refer to the source files.*

