package de.bennycar.vehicle.mapper;

import de.bennycar.api.vehicle.dto.request.CreateCustomizationOptionRequest;
import de.bennycar.api.vehicle.dto.response.CustomizationOptionResponse;
import de.bennycar.vehicle.domain.CustomizationOption;
import org.mapstruct.*;

/**
 * MapStruct mapper for CustomizationOption entity to DTO conversions.
 */
@Mapper(componentModel = "spring")
public interface CustomizationOptionMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    CustomizationOptionResponse toCustomizationOptionResponse(CustomizationOption option);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "compatibleVehicles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "displayOrder", defaultValue = "0")
    @Mapping(target = "priceAdjustment", defaultExpression = "java(java.math.BigDecimal.ZERO)")
    CustomizationOption toEntity(CreateCustomizationOptionRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "compatibleVehicles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(CreateCustomizationOptionRequest request, @MappingTarget CustomizationOption option);
}

