package de.bennycar.user.exception;

/**
 * Base exception for all business logic exceptions.
 * This is a runtime exception to avoid checked exception boilerplate.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

