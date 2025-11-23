package de.bennycar.user.dto;

import java.util.Set;
import java.util.UUID;

public class UserProfileResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> roles;

    public UserProfileResponse(UUID id, String email, String firstName, String lastName, Set<String> roles) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Set<String> getRoles() { return roles; }
}

