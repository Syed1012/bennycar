package de.bennycar.vehicle.controller;

import de.bennycar.api.vehicle.dto.request.CreateConfigurationRequest;
import de.bennycar.api.vehicle.dto.request.UpdateConfigurationRequest;
import de.bennycar.api.vehicle.dto.response.ConfigurationResponse;
import de.bennycar.api.vehicle.dto.response.ErrorResponse;
import de.bennycar.vehicle.constants.AppConstants;
import de.bennycar.vehicle.service.ConfigurationService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for vehicle configuration operations.
 * Handles user's customized vehicle builds.
 */
@Slf4j
@RestController
@RequestMapping(AppConstants.Api.CONFIGURATIONS)
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Configurations", description = "Vehicle configuration management endpoints")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @Operation(summary = "Get my configurations",
            description = "Retrieves all vehicle configurations for the authenticated user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configurations retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<Page<ConfigurationResponse>> getMyConfigurations(
            @PageableDefault(size = 10) Pageable pageable) {
        UUID userId = getCurrentUserId();
        log.debug("GET /configurations - Fetching configurations for user: {}", userId);
        return ResponseEntity.ok(configurationService.getUserConfigurations(userId, pageable));
    }

    @Operation(summary = "Get configuration by ID",
            description = "Retrieves a specific configuration for the authenticated user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configuration retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Configuration not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ConfigurationResponse> getConfigurationById(@PathVariable UUID id) {
        UUID userId = getCurrentUserId();
        log.debug("GET /configurations/{} - Fetching configuration for user: {}", id, userId);
        return ResponseEntity.ok(configurationService.getConfigurationById(id, userId));
    }

    @Operation(summary = "Create a new configuration",
            description = "Creates a new vehicle configuration for the authenticated user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Configuration created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Vehicle not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Vehicle not available",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ConfigurationResponse> createConfiguration(
            @RequestBody @Valid CreateConfigurationRequest request) {
        UUID userId = getCurrentUserId();
        log.info("POST /configurations - Creating configuration for user: {}", userId);
        ConfigurationResponse response = configurationService.createConfiguration(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update a configuration",
            description = "Updates an existing configuration for the authenticated user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configuration updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or configuration cannot be modified",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Configuration not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ConfigurationResponse> updateConfiguration(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateConfigurationRequest request) {
        UUID userId = getCurrentUserId();
        log.info("PUT /configurations/{} - Updating configuration for user: {}", id, userId);
        return ResponseEntity.ok(configurationService.updateConfiguration(id, request, userId));
    }

    @Operation(summary = "Order a configuration",
            description = "Initiates the order process for a configuration")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configuration ordered successfully"),
        @ApiResponse(responseCode = "400", description = "Configuration cannot be ordered",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Configuration not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Vehicle not available",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/order")
    public ResponseEntity<ConfigurationResponse> orderConfiguration(@PathVariable UUID id) {
        UUID userId = getCurrentUserId();
        log.info("POST /configurations/{}/order - Ordering configuration for user: {}", id, userId);
        return ResponseEntity.ok(configurationService.orderConfiguration(id, userId));
    }

    @Operation(summary = "Delete a configuration",
            description = "Deletes a configuration (only DRAFT or SAVED)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Configuration deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Configuration cannot be deleted",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Configuration not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConfiguration(@PathVariable UUID id) {
        UUID userId = getCurrentUserId();
        log.info("DELETE /configurations/{} - Deleting configuration for user: {}", id, userId);
        configurationService.deleteConfiguration(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Extracts the current user ID from the security context.
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString((String) authentication.getPrincipal());
    }
}

