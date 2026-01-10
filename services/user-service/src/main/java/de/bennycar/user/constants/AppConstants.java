package de.bennycar.user.constants;

/**
 * Internal application constants for the user service.
 * Note: API paths are defined in user-service-api module (ApiPaths class).
 */
public final class AppConstants {

    private AppConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Role name constants.
     */
    public static final class Role {
        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";

        private Role() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated");
        }
    }

    /**
     * User status constants.
     */
    public static final class UserStatus {
        public static final String ACTIVE = "ACTIVE";
        public static final String INACTIVE = "INACTIVE";
        public static final String SUSPENDED = "SUSPENDED";

        private UserStatus() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated");
        }
    }

    /**
     * Security-related constants.
     */
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

    /**
     * Token time-to-live constants.
     */
    public static final class TokenTtl {
        /** Default access token TTL: 10 minutes */
        public static final long DEFAULT_ACCESS_TOKEN_SECONDS = 600L;
        /** Default refresh token TTL: 7 days */
        public static final long REFRESH_TOKEN_SECONDS = 60L * 60L * 24L * 7L;

        private TokenTtl() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated");
        }
    }
}