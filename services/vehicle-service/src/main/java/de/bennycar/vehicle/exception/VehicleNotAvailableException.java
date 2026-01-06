package de.bennycar.vehicle.exception;

/**
 * Exception thrown when a vehicle is not available for the requested operation.
 */
public class VehicleNotAvailableException extends BusinessException {

    public VehicleNotAvailableException(String vehicleId) {
        super(String.format("Vehicle with ID %s is not available", vehicleId));
    }

    public VehicleNotAvailableException(String vehicleId, String reason) {
        super(String.format("Vehicle with ID %s is not available: %s", vehicleId, reason));
    }
}

