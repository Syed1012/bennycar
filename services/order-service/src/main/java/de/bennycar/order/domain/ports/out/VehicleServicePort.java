package de.bennycar.order.domain.ports.out;

import java.util.UUID;

public interface VehicleServicePort {
    boolean isVehicleAvailable(UUID vehicleId);
    void markVehicleAsOrdered(UUID vehicleId);
}

