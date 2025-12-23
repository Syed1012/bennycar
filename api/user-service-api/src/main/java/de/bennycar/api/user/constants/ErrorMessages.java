package de.bennycar.api.user.constants;

/**
 * Business error message constants for the User Service API.
 */
@SuppressWarnings("unused")
public final class ErrorMessages {

    private ErrorMessages() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String USER_ALREADY_EXISTS = "A user with this email already exists";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String INVALID_CREDENTIALS = "Invalid email or password";
    public static final String INVALID_TOKEN = "Invalid or expired token";
    public static final String TOKEN_EXPIRED = "Token has expired";
    public static final String UNAUTHORIZED = "Unauthorized access";
    public static final String FORBIDDEN = "Access forbidden";
    public static final String INTERNAL_ERROR = "An unexpected error occurred";
}
