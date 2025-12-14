package de.bennycar.vehicle.controller;

import de.bennycar.vehicle.constants.AppConstants;
import de.bennycar.vehicle.dto.CreateCustomizationOptionRequest;
import de.bennycar.vehicle.dto.CustomizationOptionResponse;
import de.bennycar.vehicle.dto.ErrorResponse;
import de.bennycar.vehicle.service.CustomizationOptionService;
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
 * REST controller for customization option operations.
 */
@Slf4j
@RestController
@RequestMapping(AppConstants.Api.CUSTOMIZATION_OPTIONS)
@RequiredArgsConstructor
@Tag(name = "Customization Options", description = "Customization option management endpoints")
public class CustomizationOptionController {

    private final CustomizationOptionService optionService;

    @Operation(summary = "Get all customization options",
            description = "Retrieves all active customization options")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Options retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<CustomizationOptionResponse>> getAllOptions() {
        log.debug("GET /customization-options - Fetching all options");
        return ResponseEntity.ok(optionService.getAllActiveOptions());
    }

    @Operation(summary = "Get options by category", description = "Retrieves options for a specific category")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Options retrieved successfully")
    })
    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<List<CustomizationOptionResponse>> getOptionsByCategory(@PathVariable UUID categoryId) {
        log.debug("GET /customization-options/by-category/{} - Fetching options", categoryId);
        return ResponseEntity.ok(optionService.getOptionsByCategory(categoryId));
    }

    @Operation(summary = "Get options for vehicle", description = "Retrieves options available for a specific vehicle")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Options retrieved successfully")
    })
    @GetMapping("/by-vehicle/{vehicleId}")
    public ResponseEntity<List<CustomizationOptionResponse>> getOptionsForVehicle(@PathVariable UUID vehicleId) {
        log.debug("GET /customization-options/by-vehicle/{} - Fetching options", vehicleId);
        return ResponseEntity.ok(optionService.getOptionsForVehicle(vehicleId));
    }

    @Operation(summary = "Get option by ID", description = "Retrieves a specific customization option")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Option retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Option not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomizationOptionResponse> getOptionById(@PathVariable UUID id) {
        log.debug("GET /customization-options/{} - Fetching option", id);
        return ResponseEntity.ok(optionService.getOptionById(id));
    }

    @Operation(summary = "Create a new option", description = "Creates a new customization option (Admin only)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Option created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Option already exists in category",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<CustomizationOptionResponse> createOption(
            @RequestBody @Valid CreateCustomizationOptionRequest request) {
        log.info("POST /customization-options - Creating option: {}", request.getName());
        CustomizationOptionResponse response = optionService.createOption(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update an option", description = "Updates an existing customization option (Admin only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Option updated successfully"),
        @ApiResponse(responseCode = "404", description = "Option not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public ResponseEntity<CustomizationOptionResponse> updateOption(
            @PathVariable UUID id,
            @RequestBody @Valid CreateCustomizationOptionRequest request) {
        log.info("PUT /customization-options/{} - Updating option", id);
        return ResponseEntity.ok(optionService.updateOption(id, request));
    }

    @Operation(summary = "Delete an option", description = "Soft deletes a customization option (Admin only)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Option deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Option not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOption(@PathVariable UUID id) {
        log.info("DELETE /customization-options/{} - Deleting option", id);
        optionService.deleteOption(id);
        return ResponseEntity.noContent().build();
    }
}

