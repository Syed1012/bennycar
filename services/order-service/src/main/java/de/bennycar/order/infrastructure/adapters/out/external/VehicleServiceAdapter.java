package de.bennycar.order.infrastructure.adapters.out.external;

import de.bennycar.order.domain.ports.out.VehicleServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class VehicleServiceAdapter implements VehicleServicePort {

    private final VehicleServiceClient vehicleServiceClient;

    @Override
    public boolean isVehicleAvailable(UUID vehicleId) {
        try {
            return vehicleServiceClient.checkAvailability(vehicleId);
        } catch (Exception e) {
            log.error("Error checking vehicle availability for id: {}", vehicleId, e);
            // In case of error, we assume it's not available to be safe, or rethrow
            return false;
        }
    }

    @Override
    public void markVehicleAsOrdered(UUID vehicleId) {
        try {
            vehicleServiceClient.markAsOrdered(vehicleId);
        } catch (Exception e) {
            log.error("Error marking vehicle as ordered for id: {}", vehicleId, e);
            throw new RuntimeException("Failed to update vehicle status", e);
        }
    }
}

