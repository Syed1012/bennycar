package de.bennycar.api.vehicle.contract;

import de.bennycar.api.vehicle.constants.ApiPaths;
import de.bennycar.api.vehicle.dto.request.CreateConfigurationRequest;
import de.bennycar.api.vehicle.dto.response.VehicleConfigurationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Configuration API", description = "Vehicle configuration management")
@RequestMapping(ApiPaths.V1)
public interface ConfigurationContract {

    @Operation(summary = "Get user configurations", description = "Retrieve all vehicle configurations created by the current user")
    @GetMapping(ApiPaths.CONFIGURATIONS)
    ResponseEntity<List<VehicleConfigurationResponse>> getUserConfigurations();

    @Operation(summary = "Create configuration", description = "Create a new vehicle configuration")
    @PostMapping(ApiPaths.CONFIGURATIONS)
    ResponseEntity<VehicleConfigurationResponse> createConfiguration(
        @Valid @RequestBody CreateConfigurationRequest request
    );

    @Operation(summary = "Get configuration details", description = "Retrieve detailed information about a specific configuration")
    @GetMapping(ApiPaths.CONFIGURATIONS + "/{id}")
    ResponseEntity<VehicleConfigurationResponse> getConfigurationById(@PathVariable UUID id);

    @Operation(summary = "Order configuration", description = "Place an order for a specific configuration")
    @PostMapping(ApiPaths.CONFIGURATIONS + "/{id}/order")
    ResponseEntity<Void> orderConfiguration(@PathVariable UUID id);
}