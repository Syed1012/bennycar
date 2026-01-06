package de.bennycar.api.user.constants;

/**
 * HTTP status descriptions for API responses.
 */
@SuppressWarnings("unused")
public final class StatusDescriptions {

    private StatusDescriptions() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String SUCCESS = "Request processed successfully";
    public static final String CREATED = "Resource created successfully";
    public static final String BAD_REQUEST = "Invalid request parameters or data";
    public static final String UNAUTHORIZED_DESC = "Authentication required or failed";
    public static final String FORBIDDEN_DESC = "Access forbidden";
    public static final String NOT_FOUND = "Resource not found";
    public static final String CONFLICT = "Business rule violation or resource conflict";
    public static final String INTERNAL_ERROR_DESC = "Internal server error";
}
