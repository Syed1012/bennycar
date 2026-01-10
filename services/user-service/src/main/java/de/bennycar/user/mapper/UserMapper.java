package de.bennycar.user.mapper;

import de.bennycar.api.user.dto.response.UserProfileResponse;
import de.bennycar.user.domain.Role;
import de.bennycar.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for User entity to API DTO conversions.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToStrings")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "instantToLocalDateTime")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "instantToLocalDateTime")
    UserProfileResponse toUserProfileResponse(User user);

    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<Role> roles) {
        if (roles == null) {
            return Set.of();
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    @Named("instantToLocalDateTime")
    default LocalDateTime instantToLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}