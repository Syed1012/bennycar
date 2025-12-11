package de.bennycar.user.exception;

/**
 * Exception thrown when attempting to register a user with an email that already exists.
 */
public class UserAlreadyExistsException extends BusinessException {

    private static final String DEFAULT_MESSAGE = "User with this email already exists";

    public UserAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

    public UserAlreadyExistsException(String email) {
        super(String.format("User with email '%s' already exists", email));
    }
}

