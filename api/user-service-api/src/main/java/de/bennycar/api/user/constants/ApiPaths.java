package de.bennycar.api.user.constants;

/**
 * API versioning and base path constants for the User Service API.
 */
@SuppressWarnings("unused")
public final class ApiPaths {

    private ApiPaths() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String BASE_PATH = "/api";
    public static final String V1 = BASE_PATH + "/v1";
    public static final String V2 = BASE_PATH + "/v2";
}
