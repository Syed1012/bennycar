package de.bennycar.order.infrastructure.adapters.out.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

@FeignClient(name = "vehicle-service", url = "${application.vehicle-service.url:http://localhost:8082}")
public interface VehicleServiceClient {

    @GetMapping("/api/v1/vehicles/{id}/availability")
    boolean checkAvailability(@PathVariable("id") UUID id);

    @PutMapping("/api/v1/vehicles/{id}/status/ordered")
    void markAsOrdered(@PathVariable("id") UUID id);
}

