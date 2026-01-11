package de.bennycar.user.util;

/**
 * Utility class for email normalization.
 * Ensures consistent email handling across the application (DRY principle).
 */
public final class EmailNormalizer {

    private EmailNormalizer() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Normalizes an email address by converting to lowercase and trimming whitespace.
     * This ensures consistent storage and lookup of email addresses.
     *
     * @param email Raw email address
     * @return Normalized email address
     * @throws IllegalArgumentException if email is null
     */
    public static String normalize(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        return email.toLowerCase().trim();
    }
}
