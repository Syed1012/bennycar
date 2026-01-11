package de.bennycar.user.service;

import de.bennycar.api.user.dto.response.UserProfileResponse;
import de.bennycar.user.domain.Role;
import de.bennycar.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Simple service for mapping User entity to API DTOs.
 * Replaces MapStruct mapper with straightforward manual conversion.
 */
@Slf4j
@Service
public class UserMapperService {

    /**
     * Converts a User entity to UserProfileResponse DTO.
     *
     * @param user User entity to convert
     * @return UserProfileResponse DTO
     */
    public UserProfileResponse toUserProfileResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserProfileResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .profilePictureUrl(user.getProfilePictureUrl())
                .address(user.getAddress())
                .status(user.getStatus())
                .roles(rolesToStrings(user.getRoles()))
                .createdAt(instantToLocalDateTime(user.getCreatedAt()))
                .updatedAt(instantToLocalDateTime(user.getUpdatedAt()))
                .build();
    }

    /**
     * Converts a set of Role entities to a set of role name strings.
     *
     * @param roles Set of Role entities
     * @return Set of role name strings
     */
    private Set<String> rolesToStrings(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    /**
     * Converts an Instant to LocalDateTime using the system default timezone.
     *
     * @param instant Instant to convert
     * @return LocalDateTime or null if instant is null
     */
    private LocalDateTime instantToLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
