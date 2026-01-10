package de.bennycar.user.exception;

/**
 * Exception thrown when a token (JWT or refresh token) is invalid.
 */
public class InvalidTokenException extends BusinessException {

    private static final String DEFAULT_MESSAGE = "Invalid or expired token";

    public InvalidTokenException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}