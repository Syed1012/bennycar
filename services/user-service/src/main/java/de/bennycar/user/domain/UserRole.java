package de.bennycar.user.domain;

/**
 * Enumeration of user roles in the system.
 * Provides type safety and prevents typos when working with roles.
 * 
 * Note: This enum is used for type safety, but roles are still stored
 * in the database for flexibility and auditability.
 */
public enum UserRole {
    USER("USER", "Default user role"),
    ADMIN("ADMIN", "Administrator role");

    private final String name;
    private final String description;

    UserRole(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Converts a string role name to UserRole enum.
     *
     * @param roleName role name string
     * @return UserRole enum value
     * @throws IllegalArgumentException if role name is invalid
     */
    public static UserRole fromString(String roleName) {
        if (roleName == null) {
            throw new IllegalArgumentException("Role name cannot be null");
        }
        for (UserRole role : UserRole.values()) {
            if (role.name.equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + roleName);
    }
}
