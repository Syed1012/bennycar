package de.bennycar.user.mapper;

import de.bennycar.user.dto.UserProfileResponse;
import de.bennycar.user.domain.Role;
import de.bennycar.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for User entity to DTO conversions.
 * Automatically generates implementation at compile time.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToStrings")
    UserProfileResponse toUserProfileResponse(User user);

    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}