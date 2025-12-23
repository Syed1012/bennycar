package de.bennycar.api.user.constants;

/**
 * API constants for User Service.
 * Defines common endpoint paths, versions, and message constants.
 */
public class UserApiConstants {

    private UserApiConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }

    /**
     * API versioning and base path
     */
    public static final class Api {
        public static final String BASE_PATH = "/api";
        public static final String V1 = BASE_PATH + "/v1";
        public static final String V2 = BASE_PATH + "/v2";

        private Api() {
        }
    }

    /**
     * API endpoint paths
     */
    public static final class Endpoints {
        public static final String AUTH = "/auth";
        public static final String USERS = "/users";

        // Authentication endpoints
        public static final String REGISTER = AUTH + "/register";
        public static final String LOGIN = AUTH + "/login";
        public static final String REFRESH_TOKEN = AUTH + "/refresh";
        public static final String LOGOUT = AUTH + "/logout";
        public static final String VALIDATE_TOKEN = AUTH + "/validate";

        // User endpoints
        public static final String GET_PROFILE = USERS + "/me";
        public static final String GET_USER_BY_ID = USERS + "/{userId}";
        public static final String UPDATE_PROFILE = USERS + "/me";
        public static final String CHANGE_PASSWORD = USERS + "/me/password";
        public static final String DELETE_ACCOUNT = USERS + "/me";

        private Endpoints() {
        }
    }

    /**
     * Validation error messages
     */
    public static final class ValidationMessages {
        public static final String EMAIL_REQUIRED = "Email is required";
        public static final String EMAIL_INVALID = "Email must be valid";
        public static final String PASSWORD_REQUIRED = "Password is required";
        public static final String PASSWORD_MIN_LENGTH = "Password must be at least 12 characters";
        public static final String PASSWORD_MAX_LENGTH = "Password must not exceed 128 characters";
        public static final String FIRST_NAME_REQUIRED = "First name is required";
        public static final String LAST_NAME_REQUIRED = "Last name is required";
        public static final String PHONE_FORMAT = "Phone number format is invalid";
        public static final String BIRTH_DATE_PAST = "Birth date must be in the past";

        private ValidationMessages() {
        }
    }

    /**
     * Business error messages
     */
    public static final class ErrorMessages {
        public static final String USER_ALREADY_EXISTS = "A user with this email already exists";
        public static final String USER_NOT_FOUND = "User not found";
        public static final String INVALID_CREDENTIALS = "Invalid email or password";
        public static final String INVALID_TOKEN = "Invalid or expired token";
        public static final String TOKEN_EXPIRED = "Token has expired";
        public static final String UNAUTHORIZED = "Unauthorized access";
        public static final String FORBIDDEN = "Access forbidden";
        public static final String INTERNAL_ERROR = "An unexpected error occurred";

        private ErrorMessages() {
        }
    }

    /**
     * HTTP status code descriptions
     */
    public static final class StatusDescriptions {
        public static final String SUCCESS = "Request processed successfully";
        public static final String CREATED = "Resource created successfully";
        public static final String BAD_REQUEST = "Invalid request parameters or data";
        public static final String UNAUTHORIZED_DESC = "Authentication required or failed";
        public static final String FORBIDDEN_DESC = "Access forbidden";
        public static final String NOT_FOUND = "Resource not found";
        public static final String CONFLICT = "Business rule violation or resource conflict";
        public static final String INTERNAL_ERROR_DESC = "Internal server error";

        private StatusDescriptions() {
        }
    }
}

