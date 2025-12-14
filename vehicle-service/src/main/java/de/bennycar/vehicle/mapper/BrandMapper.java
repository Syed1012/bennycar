package de.bennycar.vehicle.mapper;

import de.bennycar.vehicle.domain.Brand;
import de.bennycar.vehicle.dto.BrandResponse;
import de.bennycar.vehicle.dto.CreateBrandRequest;
import org.mapstruct.*;

/**
 * MapStruct mapper for Brand entity to DTO conversions.
 */
@Mapper(componentModel = "spring")
public interface BrandMapper {

    @Mapping(target = "vehicleCount", expression = "java(brand.getVehicles() != null ? brand.getVehicles().size() : 0)")
    BrandResponse toBrandResponse(Brand brand);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    Brand toEntity(CreateBrandRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(CreateBrandRequest request, @MappingTarget Brand brand);
}
