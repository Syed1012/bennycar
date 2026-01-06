package de.bennycar.vehicle.mapper;

import de.bennycar.api.vehicle.dto.request.CreateVehicleTypeRequest;
import de.bennycar.api.vehicle.dto.response.VehicleTypeResponse;
import de.bennycar.vehicle.domain.VehicleType;
import org.mapstruct.*;

/**
 * MapStruct mapper for VehicleType entity to DTO conversions.
 */
@Mapper(componentModel = "spring")
public interface VehicleTypeMapper {

    @Mapping(target = "vehicleCount", expression = "java(vehicleType.getVehicles() != null ? vehicleType.getVehicles().size() : 0)")
    VehicleTypeResponse toVehicleTypeResponse(VehicleType vehicleType);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    VehicleType toEntity(CreateVehicleTypeRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(CreateVehicleTypeRequest request, @MappingTarget VehicleType vehicleType);
}