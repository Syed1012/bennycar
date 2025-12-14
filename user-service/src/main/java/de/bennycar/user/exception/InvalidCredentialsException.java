package de.bennycar.user.exception;

/**
 * Exception thrown when authentication fails due to invalid credentials.
 */
public class InvalidCredentialsException extends BusinessException {

    private static final String DEFAULT_MESSAGE = "Invalid email or password";

    public InvalidCredentialsException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}

