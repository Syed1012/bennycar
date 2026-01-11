package de.bennycar.vehicle.controller;

import de.bennycar.api.vehicle.contract.VehicleServiceContract;
import de.bennycar.api.vehicle.dto.request.CreateVehicleRequest;
import de.bennycar.api.vehicle.dto.request.UpdateVehicleRequest;
import de.bennycar.api.vehicle.dto.request.VehicleSearchParams;
import de.bennycar.api.vehicle.dto.response.ErrorResponse;
import de.bennycar.api.vehicle.dto.response.VehicleResponse;
import de.bennycar.api.vehicle.dto.response.VehicleWithCustomizationsResponse;
import de.bennycar.vehicle.constants.AppConstants;
import de.bennycar.vehicle.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for vehicle operations.
 */
@Slf4j
@RestController
@RequestMapping(AppConstants.Api.VEHICLES)
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "Vehicle catalog management endpoints")
public class VehicleController implements VehicleServiceContract {

    private final VehicleService vehicleService;

    @Operation(summary = "Search vehicles", description = "Search and filter vehicles with pagination")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully")
    })
    @GetMapping
    @Override
    public ResponseEntity<Page<VehicleResponse>> searchVehicles(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID brandId,
            @RequestParam(required = false) UUID vehicleTypeId,
            @RequestParam(required = false) Integer modelYear,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 12) Pageable pageable) {

        log.debug("GET /vehicles - Searching vehicles with search: {}", search);

        VehicleSearchParams params = VehicleSearchParams.builder()
                .search(search)
                .brandId(brandId)
                .vehicleTypeId(vehicleTypeId)
                .modelYear(modelYear)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .status(status)
                .build();

        return ResponseEntity.ok(vehicleService.searchVehicles(params, pageable));
    }

    @Operation(summary = "Get vehicle by ID", description = "Retrieves a specific vehicle by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehicle retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable UUID id) {
        log.debug("GET /vehicles/{} - Fetching vehicle", id);
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @Operation(summary = "Get vehicle with customizations",
            description = "Retrieves a vehicle with all available customization options")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehicle with customizations retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/customizations")
    @Override
    public ResponseEntity<VehicleWithCustomizationsResponse> getVehicleWithCustomizations(@PathVariable UUID id) {
        log.debug("GET /vehicles/{}/customizations - Fetching vehicle with customizations", id);
        return ResponseEntity.ok(vehicleService.getVehicleWithCustomizations(id));
    }

    @Operation(summary = "Get available model years", description = "Retrieves distinct model years of available vehicles")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Model years retrieved successfully")
    })
    @GetMapping("/model-years")
    @Override
    public ResponseEntity<List<Integer>> getAvailableModelYears() {
        log.debug("GET /vehicles/model-years - Fetching available model years");
        return ResponseEntity.ok(vehicleService.getAvailableModelYears());
    }

    @Operation(summary = "Create a new vehicle", description = "Creates a new vehicle in the catalog (Admin only)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Vehicle created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Vehicle already exists",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    @Override
    public ResponseEntity<VehicleResponse> createVehicle(@RequestBody @Valid CreateVehicleRequest request) {
        log.info("POST /vehicles - Creating vehicle: {} {}", request.getModel(), request.getModelYear());
        VehicleResponse response = vehicleService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update a vehicle", description = "Updates an existing vehicle (Admin only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehicle updated successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateVehicleRequest request) {
        log.info("PUT /vehicles/{} - Updating vehicle", id);
        return ResponseEntity.ok(vehicleService.updateVehicle(id, request));
    }

    @Operation(summary = "Delete a vehicle", description = "Soft deletes a vehicle (Admin only)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteVehicle(@PathVariable UUID id) {
        log.info("DELETE /vehicles/{} - Deleting vehicle", id);
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Check vehicle availability", description = "Checks if a vehicle is available for order")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Availability status retrieved successfully")
    })
    @GetMapping("/{id}/availability")
    @Override
    public ResponseEntity<Boolean> checkAvailability(@PathVariable UUID id) {
        log.debug("GET /vehicles/{}/availability - Checking availability", id);
        return ResponseEntity.ok(vehicleService.checkAvailability(id));
    }

    @Operation(summary = "Mark vehicle as ordered", description = "Updates vehicle status to SOLD")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehicle marked as ordered successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle not found"),
        @ApiResponse(responseCode = "409", description = "Vehicle not available")
    })
    @PutMapping("/{id}/status/ordered")
    @Override
    public ResponseEntity<Void> markAsOrdered(@PathVariable UUID id) {
        log.debug("PUT /vehicles/{}/status/ordered - Marking as ordered", id);
        try {
            vehicleService.markAsOrdered(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
