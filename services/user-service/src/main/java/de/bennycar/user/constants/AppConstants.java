package de.bennycar.user.constants;

/**
 * Central location for application-wide constant values.
 */
public final class AppConstants {
    private AppConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final class Role {
        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";

        private Role() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated");
        }
    }

    public static final class UserStatus {
        public static final String ACTIVE = "ACTIVE";
        public static final String INACTIVE = "INACTIVE";

        private UserStatus() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated");
        }
    }

    public static final class Security {
        public static final String AUTHORIZATION_HEADER = "Authorization";
        public static final String BEARER_PREFIX = "Bearer ";
        public static final int BEARER_PREFIX_LENGTH = 7;
        public static final int MIN_JWT_SECRET_LENGTH = 32;
        public static final int REFRESH_TOKEN_LENGTH = 32;

        private Security() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated");
        }
    }

    public static final class Time {
        public static final long DEFAULT_ACCESS_TOKEN_TTL_SECONDS = 600L;
        public static final long REFRESH_TOKEN_TTL_SECONDS = 60L * 60L * 24L * 7L;

        private Time() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated");
        }
    }

    public static final class Api {
        public static final String V1 = "/api/v1";
        public static final String AUTH_ENDPOINT = V1 + "/auth";
        public static final String USERS_ENDPOINT = V1 + "/users";

        private Api() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated");
        }
    }

    public static final class ErrorCode {
        public static final String USER_EXISTS = "USER_EXISTS";
        public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
        public static final String INVALID_TOKEN = "INVALID_TOKEN";
        public static final String TOKEN_EXPIRED = "TOKEN_EXPIRED";
        public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
        public static final String REFRESH_TOKEN_INVALID = "REFRESH_TOKEN_INVALID";
        public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
        public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
        public static final String UNAUTHORIZED = "UNAUTHORIZED";
        public static final String FORBIDDEN = "FORBIDDEN";

        private ErrorCode() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated");
        }
    }
}