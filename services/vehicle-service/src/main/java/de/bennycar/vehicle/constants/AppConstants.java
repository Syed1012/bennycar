package de.bennycar.vehicle.constants;

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

    public static final class VehicleStatus {
        public static final String AVAILABLE = "AVAILABLE";
        public static final String RESERVED = "RESERVED";
        public static final String SOLD = "SOLD";
        public static final String DISCONTINUED = "DISCONTINUED";

        private VehicleStatus() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated");
        }
    }

    public static final class ConfigurationStatus {
        public static final String DRAFT = "DRAFT";
        public static final String SAVED = "SAVED";
        public static final String ORDERED = "ORDERED";
        public static final String COMPLETED = "COMPLETED";
        public static final String CANCELLED = "CANCELLED";

        private ConfigurationStatus() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated");
        }
    }

    public static final class Security {
        public static final String AUTHORIZATION_HEADER = "Authorization";
        public static final String BEARER_PREFIX = "Bearer ";
        public static final int BEARER_PREFIX_LENGTH = 7;
        public static final int MIN_JWT_SECRET_LENGTH = 32;

        private Security() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated");
        }
    }

    public static final class Api {
        public static final String V1 = "/api/v1";
        public static final String BRANDS = V1 + "/brands";
        public static final String VEHICLE_TYPES = V1 + "/vehicle-types";
        public static final String VEHICLES = V1 + "/vehicles";
        public static final String CUSTOMIZATION_CATEGORIES = V1 + "/customization-categories";
        public static final String CUSTOMIZATION_OPTIONS = V1 + "/customization-options";
        public static final String CONFIGURATIONS = V1 + "/configurations";

        private Api() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated");
        }
    }

    public static final class ErrorCode {
        public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
        public static final String VEHICLE_NOT_FOUND = "VEHICLE_NOT_FOUND";
        public static final String BRAND_NOT_FOUND = "BRAND_NOT_FOUND";
        public static final String CONFIGURATION_NOT_FOUND = "CONFIGURATION_NOT_FOUND";
        public static final String INVALID_CONFIGURATION = "INVALID_CONFIGURATION";
        public static final String DUPLICATE_RESOURCE = "DUPLICATE_RESOURCE";
        public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
        public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
        public static final String UNAUTHORIZED = "UNAUTHORIZED";
        public static final String FORBIDDEN = "FORBIDDEN";
        public static final String VEHICLE_NOT_AVAILABLE = "VEHICLE_NOT_AVAILABLE";

        private ErrorCode() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated");
        }
    }

    public static final class Pagination {
        public static final int DEFAULT_PAGE_SIZE = 20;
        public static final int MAX_PAGE_SIZE = 100;
        public static final String DEFAULT_SORT_FIELD = "createdAt";

        private Pagination() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated");
        }
    }
}

