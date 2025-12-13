package de.bennycar.vehicle.mapper;

import de.bennycar.vehicle.domain.CustomizationOption;
import de.bennycar.vehicle.domain.Vehicle;
import de.bennycar.vehicle.domain.VehicleConfiguration;
import de.bennycar.vehicle.dto.ConfigurationResponse;
import de.bennycar.vehicle.dto.CreateConfigurationRequest;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for VehicleConfiguration entity to DTO conversions.
 */
@Mapper(componentModel = "spring")
public interface ConfigurationMapper {

    @Mapping(target = "vehicle", source = "vehicle", qualifiedByName = "toVehicleSummary")
    @Mapping(target = "selectedOptions", source = "selectedOptions", qualifiedByName = "toSelectedOptions")
    @Mapping(target = "basePrice", source = "vehicle.basePrice")
    @Mapping(target = "customizationTotal", expression = "java(calculateCustomizationTotal(configuration.getSelectedOptions()))")
    ConfigurationResponse toConfigurationResponse(VehicleConfiguration configuration);

    @Named("toVehicleSummary")
    default ConfigurationResponse.VehicleSummary toVehicleSummary(Vehicle vehicle) {
        if (vehicle == null) return null;
        return ConfigurationResponse.VehicleSummary.builder()
                .id(vehicle.getId())
                .brandName(vehicle.getBrand() != null ? vehicle.getBrand().getName() : null)
                .brandLogoUrl(vehicle.getBrand() != null ? vehicle.getBrand().getLogoUrl() : null)
                .model(vehicle.getModel())
                .modelYear(vehicle.getModelYear())
                .vehicleTypeName(vehicle.getVehicleType() != null ? vehicle.getVehicleType().getName() : null)
                .basePrice(vehicle.getBasePrice())
                .mainImageUrl(vehicle.getMainImageUrl())
                .build();
    }

    @Named("toSelectedOptions")
    default List<ConfigurationResponse.SelectedOption> toSelectedOptions(Set<CustomizationOption> options) {
        if (options == null) return List.of();
        return options.stream()
                .map(option -> ConfigurationResponse.SelectedOption.builder()
                        .id(option.getId())
                        .categoryName(option.getCategory() != null ? option.getCategory().getName() : null)
                        .optionName(option.getName())
                        .priceAdjustment(option.getPriceAdjustment())
                        .imageUrl(option.getImageUrl())
                        .colorCode(option.getColorCode())
                        .build())
                .collect(Collectors.toList());
    }

    default BigDecimal calculateCustomizationTotal(Set<CustomizationOption> options) {
        if (options == null) return BigDecimal.ZERO;
        return options.stream()
                .map(CustomizationOption::getPriceAdjustment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "selectedOptions", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "orderedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", constant = "DRAFT")
    VehicleConfiguration toEntity(CreateConfigurationRequest request);
}

