package de.bennycar.vehicle.mapper;

import de.bennycar.api.vehicle.dto.request.CreateCustomizationCategoryRequest;
import de.bennycar.api.vehicle.dto.response.CustomizationCategoryResponse;
import de.bennycar.vehicle.domain.CustomizationCategory;
import org.mapstruct.*;

/**
 * MapStruct mapper for CustomizationCategory entity to DTO conversions.
 */
@Mapper(componentModel = "spring")
public interface CustomizationCategoryMapper {

    @Mapping(target = "optionCount", expression = "java(category.getOptions() != null ? category.getOptions().size() : 0)")
    CustomizationCategoryResponse toCustomizationCategoryResponse(CustomizationCategory category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "options", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "displayOrder", defaultValue = "0")
    @Mapping(target = "allowsMultiple", defaultValue = "false")
    CustomizationCategory toEntity(CreateCustomizationCategoryRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "options", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(CreateCustomizationCategoryRequest request, @MappingTarget CustomizationCategory category);
}

