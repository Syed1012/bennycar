package de.bennycar.api.user.constants;

/**
 * API endpoint paths for the User Service API.
 */
@SuppressWarnings("unused")
public final class EndpointPaths {

    private EndpointPaths() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

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
}
