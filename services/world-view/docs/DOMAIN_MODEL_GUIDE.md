# Domain Model Guide - World View Service

## What are Java Records?

**Records** are a special type of class introduced in Java 14 (finalized in Java 16) that make it easy to create immutable data carriers. Think of them as a shortcut for creating simple classes that just hold data.

### Before Records (Traditional Way)
```java
public class Point {
    private final double x;
    private final double y;
    
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    
    @Override
    public boolean equals(Object o) { /* ... */ }
    @Override
    public int hashCode() { /* ... */ }
    @Override
    public String toString() { /* ... */ }
}
```

### With Records (Modern Way)
```java
public record Point(double x, double y) { }
```

That's it! The record automatically gives you:
- A constructor that takes both fields
- Getter methods (no "get" prefix - just `x()` and `y()`)
- `equals()`, `hashCode()`, and `toString()` methods
- All fields are `private final` (immutable)

---

## 1. Coordinate.java

**What it represents**: A point on Earth using latitude and longitude.

### Fields
- `latitude` - North/South position (-90° to 90°)
- `longitude` - East/West position (-180° to 180°)

### Constructor Validation
```java
public Coordinate {
    if (latitude < -90 || latitude > 90) {
        throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
    }
    if (longitude < -180 || longitude > 180) {
        throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
    }
}
```
**Purpose**: Ensures you can't create invalid coordinates. For example, latitude 100° doesn't exist on Earth, so it throws an error immediately.

---

### Method: `distanceTo(Coordinate other)`

**What it does**: Calculates the actual distance between two points on Earth in meters.

**Why we need it**: You can't just subtract coordinates to get distance because Earth is a sphere, not a flat surface. The method uses the **Haversine formula** - a mathematical formula that accounts for Earth's curvature.

**Example**:
```java
Coordinate newYork = new Coordinate(40.7128, -74.0060);
Coordinate london = new Coordinate(51.5074, -0.1278);
double distance = newYork.distanceTo(london); // Returns ~5570000 meters (5570 km)
```

**How it works**:
1. Converts coordinates from degrees to radians (the math format computers prefer)
2. Calculates the angular difference using Haversine formula
3. Multiplies by Earth's radius (6,371 km) to get actual distance

---

### Method: `interpolateTo(Coordinate target, double fraction)`

**What it does**: Finds a point between two coordinates.

**Problem it solves**: When simulating a car driving from point A to point B, you need to calculate where the car is at any moment in between.

**Parameters**:
- `target` - The destination coordinate
- `fraction` - How far along the path (0.0 = start, 0.5 = halfway, 1.0 = end)

**Example**:
```java
Coordinate start = new Coordinate(40.0, -74.0);
Coordinate end = new Coordinate(41.0, -73.0);

Coordinate halfway = start.interpolateTo(end, 0.5); 
// Returns a coordinate at the midpoint

Coordinate quarterWay = start.interpolateTo(end, 0.25);
// Returns a coordinate 25% of the way to the end
```

**How it works**: Linearly blends the latitude and longitude values based on the fraction.

---

### Method: `toString()`

**What it does**: Creates a readable text representation of the coordinate.

**Output example**: `Coordinate[lat=40.712800, lng=-74.006000]`

**Purpose**: Useful for debugging and logging.

---

## 2. DrivingRoute.java

**What it represents**: A complete route from a starting point to the dealership, including all the points along the way.

### Fields
- `id` - Unique identifier for this route (e.g., "route-001")
- `name` - Human-readable name (e.g., "Highway Route")
- `description` - Details about the route (e.g., "Scenic highway through mountains")
- `startPoint` - Where the journey begins
- `endPoint` - Where the journey ends (the dealership)
- `waypoints` - List of all coordinates that form the path (including start and end)
- `totalDistanceMeters` - Total length of the route in meters
- `estimatedDurationSeconds` - Expected time to complete the route

---

### Constructor Validation
```java
public DrivingRoute {
    if (id == null || id.isBlank()) {
        throw new IllegalArgumentException("Route ID cannot be null or empty");
    }
    // ... more validations ...
    waypoints = List.copyOf(waypoints);
}
```

**Purpose**: 
- Ensures no route can be created with missing critical data
- `List.copyOf(waypoints)` creates an immutable copy - once the route is created, nobody can modify the waypoint list

**Why immutability matters**: If routes could be changed after creation, a car could be following a route while someone else is modifying it, causing chaos in the simulation!

---

### Method: `getTotalWaypoints()`

**What it does**: Returns how many waypoints are in the route.

**Example**:
```java
DrivingRoute route = new DrivingRoute(...);
int count = route.getTotalWaypoints(); // e.g., returns 50
```

**Purpose**: Useful when iterating through waypoints or checking if you've reached the end.

---

### Method: `getWaypointAt(int index)`

**What it does**: Gets the coordinate at a specific position in the route.

**Parameters**: `index` - Position in the waypoint list (0 = first waypoint)

**Example**:
```java
Coordinate first = route.getWaypointAt(0);      // Start point
Coordinate second = route.getWaypointAt(1);     // Next point
Coordinate last = route.getWaypointAt(route.getTotalWaypoints() - 1); // End
```

**Error handling**: Throws `IndexOutOfBoundsException` if you ask for a waypoint that doesn't exist (e.g., index -1 or 100 when there are only 50 waypoints).

**Purpose**: Safely access waypoints with bounds checking to prevent crashes.

---

## 3. JourneyState.java

**What it represents**: The current state of an ongoing journey. Unlike the records above, this is a **mutable class** because the journey state changes over time as the car moves.

### Key Difference from Records
Records are immutable (can't change). `JourneyState` is a regular class because:
- The car's position changes as it drives
- The status changes (not started → in progress → completed)
- The progress percentage updates continuously

---

### Fields
- `journeyId` - Unique ID for this specific journey
- `route` - The route being traveled
- `currentWaypointIndex` - Which waypoint segment we're currently on
- `currentPosition` - Exact current location of the car
- `status` - Current state: `NOT_STARTED`, `IN_PROGRESS`, `PAUSED`, or `COMPLETED`
- `speedMetersPerSecond` - How fast the car is traveling
- `progressPercentage` - How much of the route is complete (0-100%)

---

### Constructor
```java
public JourneyState(String journeyId, DrivingRoute route, double speedMetersPerSecond)
```

**What it does**: Creates a new journey ready to start.

**Initial state**:
- Position: at the route's starting point
- Status: `NOT_STARTED`
- Progress: 0%
- Waypoint index: 0 (first waypoint)

**Validations**: Ensures journey has an ID, a valid route, and positive speed.

---

### Method: `start()`

**What it does**: Begins the journey by changing status to `IN_PROGRESS`.

**Error handling**: Throws an exception if you try to restart a completed journey.

**Example**:
```java
JourneyState journey = new JourneyState("journey-1", route, 20.0);
journey.start(); // Status changes from NOT_STARTED to IN_PROGRESS
```

---

### Method: `pause()`

**What it does**: Pauses an active journey.

**When it works**: Only if status is currently `IN_PROGRESS`.

**Example use case**: User taps "pause" button in the UI while watching the simulation.

---

### Method: `resume()`

**What it does**: Resumes a paused journey.

**When it works**: Only if status is currently `PAUSED`.

---

### Method: `advance(double elapsedSeconds)` - The Core Simulation Logic

**What it does**: Moves the car forward based on how much time has passed.

**Parameters**: `elapsedSeconds` - Time since last update (e.g., 0.1 seconds for 10 FPS simulation)

**Returns**: `true` if the journey is complete, `false` otherwise.

**How it works** (step-by-step):

1. **Check if active**: If not `IN_PROGRESS`, does nothing
2. **Calculate distance**: `distance = speed × time`
   - Example: 20 m/s × 0.5 seconds = 10 meters to travel
3. **Move through waypoints**:
   - Calculate distance to next waypoint
   - If we can reach it with remaining distance:
     - Move to that waypoint
     - Subtract the distance traveled
     - Move to next waypoint segment
   - If we can't reach the next waypoint:
     - Calculate how far along the segment we get
     - Interpolate position on that segment
     - Stop for this update
4. **Update progress**: Calculate what % of total route is complete
5. **Check completion**: If reached final waypoint, mark as `COMPLETED`

**Example**:
```java
JourneyState journey = new JourneyState("j1", route, 20.0); // 20 m/s
journey.start();

// In a game loop running 10 times per second:
while (!journey.advance(0.1)) {
    // Car moves forward, position updates
    System.out.println("Progress: " + journey.getProgressPercentage() + "%");
}
System.out.println("Arrived at dealership!");
```

---

### Method: `updateProgress()` (private)

**What it does**: Calculates what percentage of the route is complete.

**How it works**:
1. Sums up the distance of all completed segments
2. Adds the partial distance in the current segment
3. Divides by total route distance and converts to percentage

**Why it's private**: This is an internal calculation that happens automatically during `advance()`. Other classes don't need to call it directly.

---

### Method: `setSpeedMetersPerSecond(double speed)`

**What it does**: Changes the car's speed mid-journey.

**Example use case**: User adjusts simulation speed with a slider in the UI.

**Validation**: Speed must be positive.

---

## Summary

### Coordinate (Record)
Immutable geographic point with methods to calculate distance and interpolate between points.

### DrivingRoute (Record)
Immutable route definition containing metadata and a list of waypoints that form the path.

### JourneyState (Class)
Mutable journey tracker that simulates a car moving along a route, updating position and status over time.

### How They Work Together
1. Create `Coordinate` objects for each point on a route
2. Bundle them into a `DrivingRoute` with metadata
3. Create a `JourneyState` to simulate a car driving that route
4. Call `advance()` repeatedly to animate the journey

