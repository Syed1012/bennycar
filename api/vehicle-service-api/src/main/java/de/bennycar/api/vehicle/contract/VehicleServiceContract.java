package de.bennycar.api.vehicle.contract;

import de.bennycar.api.vehicle.dto.request.CreateVehicleRequest;
import de.bennycar.api.vehicle.dto.request.UpdateVehicleRequest;
import de.bennycar.api.vehicle.dto.response.ErrorResponse;
import de.bennycar.api.vehicle.dto.response.VehicleResponse;
import de.bennycar.api.vehicle.dto.response.VehicleWithCustomizationsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Tag(name = "Vehicles", description = "Vehicle catalog management endpoints")
@RequestMapping("/api/v1/vehicles")
public interface VehicleServiceContract {

    @Operation(summary = "Search vehicles", description = "Search and filter vehicles with pagination")
    @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully")
    @GetMapping
    ResponseEntity<Page<VehicleResponse>> searchVehicles(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID brandId,
            @RequestParam(required = false) UUID vehicleTypeId,
            @RequestParam(required = false) Integer modelYear,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String status,
            Pageable pageable);

    @Operation(summary = "Get vehicle by ID", description = "Retrieves a specific vehicle by its ID")
    @ApiResponse(responseCode = "200", description = "Vehicle retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Vehicle not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    ResponseEntity<VehicleResponse> getVehicleById(@PathVariable UUID id);

    @Operation(summary = "Get vehicle with customizations",
            description = "Retrieves a vehicle with all available customization options")
    @ApiResponse(responseCode = "200", description = "Vehicle with customizations retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Vehicle not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}/customizations")
    ResponseEntity<VehicleWithCustomizationsResponse> getVehicleWithCustomizations(@PathVariable UUID id);

    @Operation(summary = "Get available model years", description = "Retrieves distinct model years of available vehicles")
    @ApiResponse(responseCode = "200", description = "Model years retrieved successfully")
    @GetMapping("/model-years")
    ResponseEntity<List<Integer>> getAvailableModelYears();

    @Operation(summary = "Create a new vehicle", description = "Creates a new vehicle in the catalog (Admin only)")
    @ApiResponse(responseCode = "201", description = "Vehicle created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Vehicle already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    ResponseEntity<VehicleResponse> createVehicle(@RequestBody @Valid CreateVehicleRequest request);

    @Operation(summary = "Update a vehicle", description = "Updates an existing vehicle (Admin only)")
    @ApiResponse(responseCode = "200", description = "Vehicle updated successfully")
    @ApiResponse(responseCode = "404", description = "Vehicle not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateVehicleRequest request);

    @Operation(summary = "Delete a vehicle", description = "Soft deletes a vehicle (Admin only)")
    @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully")
    @ApiResponse(responseCode = "404", description = "Vehicle not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteVehicle(@PathVariable UUID id);

    @Operation(summary = "Check vehicle availability", description = "Checks if a vehicle is available for order")
    @ApiResponse(responseCode = "200", description = "Availability status retrieved successfully")
    @GetMapping("/{id}/availability")
    ResponseEntity<Boolean> checkAvailability(@PathVariable UUID id);

    @Operation(summary = "Mark vehicle as ordered", description = "Updates vehicle status to SOLD")
    @ApiResponse(responseCode = "200", description = "Vehicle marked as ordered successfully")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    @ApiResponse(responseCode = "409", description = "Vehicle not available")
    @PutMapping("/{id}/status/ordered")
    ResponseEntity<Void> markAsOrdered(@PathVariable UUID id);
}
