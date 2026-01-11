# Fix Vehicle Service Compilation Errors

## Problem
The vehicle-service cannot compile because it's using an outdated version of the `vehicle-service-api` module. The API module needs to be rebuilt and installed to the local Maven repository first.

## Solution

### Step 1: Rebuild the API Module

Navigate to the API module and rebuild it:

```bash
cd api/vehicle-service-api
mvn clean install -DskipTests
```

If you get permission errors about read-only files, try:

```bash
# Remove the target directory manually if needed
rm -rf target
mvn install -DskipTests
```

### Step 2: Rebuild the Vehicle Service

After the API module is installed, rebuild the vehicle service:

```bash
cd ../../services/vehicle-service
mvn clean compile
```

## Alternative: Build from Root

You can also build everything from the root:

```bash
# From project root
mvn clean install -DskipTests -pl api/vehicle-service-api -am
mvn clean compile -pl services/vehicle-service
```

## What Changed

The `VehicleSearchParams` DTO was updated to include a `search` field:
- Added `search` field to support text search
- The field is used to search across brand name, model, vehicle type, and description

The `VehicleServiceContract` interface was updated to include the `search` parameter in the `searchVehicles` method signature.

## Verification

After rebuilding, you should see:
- ✅ No compilation errors
- ✅ Warnings about unmapped properties (these are safe to ignore for now)

## If Issues Persist

1. **Check Maven local repository**: Ensure the API module JAR is updated in `~/.m2/repository/de/bennycar/vehicle-service-api/`
2. **Invalidate IDE caches**: If using IntelliJ/Eclipse, invalidate caches and restart
3. **Force update**: Use `mvn clean install -U` to force Maven to update dependencies
