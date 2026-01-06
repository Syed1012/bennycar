package de.bennycar.api.user.constants;

/**
 * Validation error message constants for the User Service API.
 */
@SuppressWarnings("unused")
public final class ValidationMessages {

    private ValidationMessages() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String EMAIL_REQUIRED = "Email is required";
    public static final String EMAIL_INVALID = "Email must be valid";
    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String PASSWORD_MIN_LENGTH = "Password must be at least 12 characters";
    public static final String PASSWORD_MAX_LENGTH = "Password must not exceed 128 characters";
    public static final String FIRST_NAME_REQUIRED = "First name is required";
    public static final String LAST_NAME_REQUIRED = "Last name is required";
    public static final String PHONE_FORMAT = "Phone number format is invalid";
    public static final String BIRTH_DATE_PAST = "Birth date must be in the past";
}
