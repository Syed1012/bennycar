package de.bennycar.api.vehicle.contract;

import de.bennycar.api.vehicle.constants.ApiPaths;
import de.bennycar.api.vehicle.dto.response.VehicleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Vehicle API", description = "Vehicle catalog operations")
@RequestMapping(ApiPaths.V1)
public interface VehicleContract {

    @Operation(summary = "Get all vehicles", description = "Retrieve a list of all available vehicles with optional filtering")
    @GetMapping(ApiPaths.VEHICLES)
    ResponseEntity<Page<VehicleResponse>> getVehicles(
        @Parameter(description = "Filter by Brand ID") @RequestParam(required = false) UUID brandId,
        @Parameter(description = "Minimum Price") @RequestParam(required = false) BigDecimal minPrice,
        @Parameter(description = "Maximum Price") @RequestParam(required = false) BigDecimal maxPrice,
        @Parameter(hidden = true) Pageable pageable
    );

    @Operation(summary = "Get vehicle details", description = "Retrieve detailed information about a specific vehicle")
    @GetMapping(ApiPaths.VEHICLES + "/{vehicleId}")
    ResponseEntity<VehicleResponse> getVehicleById(@PathVariable UUID vehicleId);
}

