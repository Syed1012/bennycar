package de.bennycar.vehicle.exception;

/**
 * Exception thrown when a vehicle configuration is invalid.
 */
public class InvalidConfigurationException extends BusinessException {

    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

