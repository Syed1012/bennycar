package de.bennycar.vehicle.controller;

import de.bennycar.api.vehicle.dto.request.CreateVehicleTypeRequest;
import de.bennycar.api.vehicle.dto.response.ErrorResponse;
import de.bennycar.api.vehicle.dto.response.VehicleTypeResponse;
import de.bennycar.vehicle.constants.AppConstants;
import de.bennycar.vehicle.service.VehicleTypeService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for vehicle type operations.
 */
@Slf4j
@RestController
@RequestMapping(AppConstants.Api.VEHICLE_TYPES)
@RequiredArgsConstructor
@Tag(name = "Vehicle Types", description = "Vehicle type/category management endpoints")
public class VehicleTypeController {

    private final VehicleTypeService vehicleTypeService;

    @Operation(summary = "Get all vehicle types", description = "Retrieves all vehicle types")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehicle types retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<VehicleTypeResponse>> getAllVehicleTypes() {
        log.debug("GET /vehicle-types - Fetching all vehicle types");
        return ResponseEntity.ok(vehicleTypeService.getAllVehicleTypes());
    }

    @Operation(summary = "Get vehicle type by ID", description = "Retrieves a specific vehicle type")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehicle type retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle type not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<VehicleTypeResponse> getVehicleTypeById(@PathVariable UUID id) {
        log.debug("GET /vehicle-types/{} - Fetching vehicle type", id);
        return ResponseEntity.ok(vehicleTypeService.getVehicleTypeById(id));
    }

    @Operation(summary = "Create a new vehicle type", description = "Creates a new vehicle type (Admin only)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Vehicle type created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Vehicle type already exists",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<VehicleTypeResponse> createVehicleType(@RequestBody @Valid CreateVehicleTypeRequest request) {
        log.info("POST /vehicle-types - Creating vehicle type: {}", request.getName());
        VehicleTypeResponse response = vehicleTypeService.createVehicleType(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update a vehicle type", description = "Updates an existing vehicle type (Admin only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vehicle type updated successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle type not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public ResponseEntity<VehicleTypeResponse> updateVehicleType(
            @PathVariable UUID id,
            @RequestBody @Valid CreateVehicleTypeRequest request) {
        log.info("PUT /vehicle-types/{} - Updating vehicle type", id);
        return ResponseEntity.ok(vehicleTypeService.updateVehicleType(id, request));
    }

    @Operation(summary = "Delete a vehicle type", description = "Deletes a vehicle type (Admin only)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Vehicle type deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Vehicle type not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicleType(@PathVariable UUID id) {
        log.info("DELETE /vehicle-types/{} - Deleting vehicle type", id);
        vehicleTypeService.deleteVehicleType(id);
        return ResponseEntity.noContent().build();
    }
}

