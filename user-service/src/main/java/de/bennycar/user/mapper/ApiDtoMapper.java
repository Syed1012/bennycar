package de.bennycar.user.mapper;

import de.bennycar.api.user.dto.request.ChangePasswordRequest;
import de.bennycar.api.user.dto.request.LoginRequest;
import de.bennycar.api.user.dto.request.RefreshTokenRequest;
import de.bennycar.api.user.dto.request.RegisterUserRequest;
import de.bennycar.api.user.dto.request.UpdateUserProfileRequest;
import de.bennycar.api.user.dto.response.ErrorResponse;
import de.bennycar.api.user.dto.response.TokenResponse;
import de.bennycar.api.user.dto.response.UserProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * MapStruct mapper for converting between API DTOs and internal DTOs.
 * This ensures separation between external API contracts and internal implementations.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ApiDtoMapper {

    // Request mappings
    de.bennycar.user.dto.RegistrationRequest toInternalRegistrationRequest(RegisterUserRequest apiRequest);

    de.bennycar.user.dto.LoginRequest toInternalLoginRequest(LoginRequest apiRequest);

    de.bennycar.user.dto.RefreshTokenRequest toInternalRefreshTokenRequest(RefreshTokenRequest apiRequest);

    // Response mappings
    TokenResponse toApiTokenResponse(de.bennycar.user.dto.TokenResponse internalResponse);

    UserProfileResponse toApiUserProfileResponse(de.bennycar.user.dto.UserProfileResponse internalResponse);

    ErrorResponse toApiErrorResponse(de.bennycar.user.dto.ErrorResponse internalResponse);

    // Custom mapping method for Instant to LocalDateTime
    default LocalDateTime map(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}

