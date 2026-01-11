package de.bennycar.vehicle.service;

import de.bennycar.api.vehicle.dto.request.CreateBrandRequest;
import de.bennycar.api.vehicle.dto.request.CreateVehicleRequest;
import de.bennycar.api.vehicle.dto.request.CreateVehicleTypeRequest;
import de.bennycar.api.vehicle.dto.response.*;
import de.bennycar.vehicle.domain.*;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manual mapper service for entity-to-DTO conversions.
 * Replaces MapStruct for simpler, more explicit mapping.
 */
@Service
public class MapperService {

    // ==================== Brand Mappers ====================

    public BrandResponse toBrandResponse(Brand brand) {
        if (brand == null) return null;
        // Safely get vehicle count - avoid lazy loading exception
        int vehicleCount = 0;
        if (Hibernate.isInitialized(brand.getVehicles()) && brand.getVehicles() != null) {
            vehicleCount = brand.getVehicles().size();
        }
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .description(brand.getDescription())
                .logoUrl(brand.getLogoUrl())
                .countryOfOrigin(brand.getCountryOfOrigin())
                .foundedYear(brand.getFoundedYear())
                .active(brand.getActive())
                .vehicleCount(vehicleCount)
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .build();
    }

    public Brand toBrandEntity(CreateBrandRequest request) {
        if (request == null) return null;
        return Brand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .logoUrl(request.getLogoUrl())
                .countryOfOrigin(request.getCountryOfOrigin())
                .foundedYear(request.getFoundedYear())
                .active(true)
                .build();
    }

    public void updateBrandEntity(CreateBrandRequest request, Brand brand) {
        if (request == null || brand == null) return;
        if (request.getName() != null) brand.setName(request.getName());
        if (request.getDescription() != null) brand.setDescription(request.getDescription());
        if (request.getLogoUrl() != null) brand.setLogoUrl(request.getLogoUrl());
        if (request.getCountryOfOrigin() != null) brand.setCountryOfOrigin(request.getCountryOfOrigin());
        if (request.getFoundedYear() != null) brand.setFoundedYear(request.getFoundedYear());
    }

    // ==================== VehicleType Mappers ====================

    public VehicleTypeResponse toVehicleTypeResponse(VehicleType vehicleType) {
        if (vehicleType == null) return null;
        // Safely get vehicle count - avoid lazy loading exception
        int vehicleCount = 0;
        if (Hibernate.isInitialized(vehicleType.getVehicles()) && vehicleType.getVehicles() != null) {
            vehicleCount = vehicleType.getVehicles().size();
        }
        return VehicleTypeResponse.builder()
                .id(vehicleType.getId())
                .name(vehicleType.getName())
                .description(vehicleType.getDescription())
                .iconUrl(vehicleType.getIconUrl())
                .vehicleCount(vehicleCount)
                .createdAt(vehicleType.getCreatedAt())
                .updatedAt(vehicleType.getUpdatedAt())
                .build();
    }

    public VehicleType toVehicleTypeEntity(CreateVehicleTypeRequest request) {
        if (request == null) return null;
        return VehicleType.builder()
                .name(request.getName())
                .description(request.getDescription())
                .iconUrl(request.getIconUrl())
                .build();
    }

    public void updateVehicleTypeEntity(CreateVehicleTypeRequest request, VehicleType vehicleType) {
        if (request == null || vehicleType == null) return;
        if (request.getName() != null) vehicleType.setName(request.getName());
        if (request.getDescription() != null) vehicleType.setDescription(request.getDescription());
        if (request.getIconUrl() != null) vehicleType.setIconUrl(request.getIconUrl());
    }

    // ==================== Vehicle Mappers ====================

    public VehicleResponse toVehicleResponse(Vehicle vehicle) {
        if (vehicle == null) return null;
        
        VehicleResponse.BrandSummary brandSummary = null;
        if (vehicle.getBrand() != null) {
            brandSummary = VehicleResponse.BrandSummary.builder()
                    .id(vehicle.getBrand().getId())
                    .name(vehicle.getBrand().getName())
                    .logoUrl(vehicle.getBrand().getLogoUrl())
                    .build();
        }
        
        VehicleResponse.VehicleTypeSummary typeSummary = null;
        if (vehicle.getVehicleType() != null) {
            typeSummary = VehicleResponse.VehicleTypeSummary.builder()
                    .id(vehicle.getVehicleType().getId())
                    .name(vehicle.getVehicleType().getName())
                    .iconUrl(vehicle.getVehicleType().getIconUrl())
                    .build();
        }
        
        // Safely get additional images - avoid lazy loading exception
        Set<String> additionalImages = new HashSet<>();
        if (Hibernate.isInitialized(vehicle.getAdditionalImages()) && vehicle.getAdditionalImages() != null) {
            additionalImages = vehicle.getAdditionalImages();
        }
        
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .brand(brandSummary)
                .vehicleType(typeSummary)
                .model(vehicle.getModel())
                .modelYear(vehicle.getModelYear())
                .description(vehicle.getDescription())
                .basePrice(vehicle.getBasePrice())
                .engine(vehicle.getEngine())
                .transmission(vehicle.getTransmission())
                .fuelType(vehicle.getFuelType())
                .horsepower(vehicle.getHorsepower())
                .seatingCapacity(vehicle.getSeatingCapacity())
                .cargoCapacityLiters(vehicle.getCargoCapacityLiters())
                .fuelEfficiency(vehicle.getFuelEfficiency())
                .mainImageUrl(vehicle.getMainImageUrl())
                .additionalImages(additionalImages)
                .status(vehicle.getStatus())
                .stockQuantity(vehicle.getStockQuantity())
                .available(vehicle.isAvailable())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }

    public Vehicle toVehicleEntity(CreateVehicleRequest request, Brand brand, VehicleType vehicleType) {
        if (request == null) return null;
        return Vehicle.builder()
                .brand(brand)
                .vehicleType(vehicleType)
                .model(request.getModel())
                .modelYear(request.getModelYear())
                .description(request.getDescription())
                .basePrice(request.getBasePrice())
                .engine(request.getEngine())
                .transmission(request.getTransmission())
                .fuelType(request.getFuelType())
                .horsepower(request.getHorsepower())
                .seatingCapacity(request.getSeatingCapacity())
                .cargoCapacityLiters(request.getCargoCapacityLiters())
                .fuelEfficiency(request.getFuelEfficiency())
                .mainImageUrl(request.getMainImageUrl())
                .additionalImages(request.getAdditionalImages())
                .status("AVAILABLE")
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 1)
                .build();
    }

    // ==================== CustomizationCategory Mappers ====================

    public CustomizationCategoryResponse toCustomizationCategoryResponse(CustomizationCategory category) {
        if (category == null) return null;
        // Safely get option count - avoid lazy loading exception
        int optionCount = 0;
        if (Hibernate.isInitialized(category.getOptions()) && category.getOptions() != null) {
            optionCount = category.getOptions().size();
        }
        return CustomizationCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .displayOrder(category.getDisplayOrder())
                .allowsMultiple(category.getAllowsMultiple())
                .active(category.getActive())
                .optionCount(optionCount)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    // ==================== CustomizationOption Mappers ====================

    public CustomizationOptionResponse toCustomizationOptionResponse(CustomizationOption option) {
        if (option == null) return null;
        return CustomizationOptionResponse.builder()
                .id(option.getId())
                .categoryId(option.getCategory() != null ? option.getCategory().getId() : null)
                .categoryName(option.getCategory() != null ? option.getCategory().getName() : null)
                .name(option.getName())
                .description(option.getDescription())
                .priceAdjustment(option.getPriceAdjustment())
                .imageUrl(option.getImageUrl())
                .colorCode(option.getColorCode())
                .displayOrder(option.getDisplayOrder())
                .active(option.getActive())
                .createdAt(option.getCreatedAt())
                .updatedAt(option.getUpdatedAt())
                .build();
    }
}
