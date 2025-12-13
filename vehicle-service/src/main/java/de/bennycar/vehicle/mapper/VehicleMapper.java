package de.bennycar.vehicle.mapper;

import de.bennycar.vehicle.domain.Vehicle;
import de.bennycar.vehicle.dto.CreateVehicleRequest;
import de.bennycar.vehicle.dto.UpdateVehicleRequest;
import de.bennycar.vehicle.dto.VehicleResponse;
import org.mapstruct.*;

/**
 * MapStruct mapper for Vehicle entity to DTO conversions.
 */
@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mapping(target = "brand", source = "brand", qualifiedByName = "toBrandSummary")
    @Mapping(target = "vehicleType", source = "vehicleType", qualifiedByName = "toVehicleTypeSummary")
    @Mapping(target = "available", expression = "java(vehicle.isAvailable())")
    VehicleResponse toVehicleResponse(Vehicle vehicle);

    @Named("toBrandSummary")
    default VehicleResponse.BrandSummary toBrandSummary(de.bennycar.vehicle.domain.Brand brand) {
        if (brand == null) return null;
        return VehicleResponse.BrandSummary.builder()
                .id(brand.getId())
                .name(brand.getName())
                .logoUrl(brand.getLogoUrl())
                .build();
    }

    @Named("toVehicleTypeSummary")
    default VehicleResponse.VehicleTypeSummary toVehicleTypeSummary(de.bennycar.vehicle.domain.VehicleType vehicleType) {
        if (vehicleType == null) return null;
        return VehicleResponse.VehicleTypeSummary.builder()
                .id(vehicleType.getId())
                .name(vehicleType.getName())
                .iconUrl(vehicleType.getIconUrl())
                .build();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "vehicleType", ignore = true)
    @Mapping(target = "availableCustomizations", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", constant = "AVAILABLE")
    @Mapping(target = "stockQuantity", defaultValue = "0")
    Vehicle toEntity(CreateVehicleRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "vehicleType", ignore = true)
    @Mapping(target = "availableCustomizations", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateVehicleRequest request, @MappingTarget Vehicle vehicle);
}

