package de.bennycar.vehicle.service;

import de.bennycar.api.vehicle.dto.request.CreateVehicleRequest;
import de.bennycar.api.vehicle.dto.request.UpdateVehicleRequest;
import de.bennycar.api.vehicle.dto.request.VehicleSearchParams;
import de.bennycar.api.vehicle.dto.response.VehicleResponse;
import de.bennycar.api.vehicle.dto.response.VehicleWithCustomizationsResponse;
import de.bennycar.vehicle.constants.AppConstants;
import de.bennycar.vehicle.domain.Brand;
import de.bennycar.vehicle.domain.CustomizationOption;
import de.bennycar.vehicle.domain.Vehicle;
import de.bennycar.vehicle.domain.VehicleType;
import de.bennycar.vehicle.exception.DuplicateResourceException;
import de.bennycar.vehicle.exception.ResourceNotFoundException;
import de.bennycar.vehicle.mapper.VehicleMapper;
import de.bennycar.vehicle.repository.CustomizationOptionRepository;
import de.bennycar.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for vehicle-related operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CustomizationOptionRepository optionRepository;
    private final VehicleMapper vehicleMapper;
    private final BrandService brandService;
    private final VehicleTypeService vehicleTypeService;

    /**
     * Searches vehicles with filters and pagination.
     */
    public Page<VehicleResponse> searchVehicles(VehicleSearchParams params, Pageable pageable) {
        log.debug("Searching vehicles with params: {}", params);
        String status = params.getStatus() != null ? params.getStatus() : AppConstants.VehicleStatus.AVAILABLE;
        return vehicleRepository.searchVehicles(
                params.getBrandId(),
                params.getVehicleTypeId(),
                params.getModelYear(),
                params.getMinPrice(),
                params.getMaxPrice(),
                status,
                pageable
        ).map(vehicleMapper::toVehicleResponse);
    }

    /**
     * Retrieves a vehicle by ID.
     */
    public VehicleResponse getVehicleById(UUID id) {
        log.debug("Fetching vehicle by ID: {}", id);
        Vehicle vehicle = findVehicleById(id);
        return vehicleMapper.toVehicleResponse(vehicle);
    }

    /**
     * Retrieves a vehicle with all available customization options.
     */
    public VehicleWithCustomizationsResponse getVehicleWithCustomizations(UUID id) {
        log.debug("Fetching vehicle with customizations by ID: {}", id);
        Vehicle vehicle = vehicleRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id.toString()));

        VehicleResponse vehicleResponse = vehicleMapper.toVehicleResponse(vehicle);

        // Group customization options by category
        Set<CustomizationOption> options = vehicle.getAvailableCustomizations();
        Map<UUID, List<CustomizationOption>> optionsByCategory = options.stream()
                .collect(Collectors.groupingBy(opt -> opt.getCategory().getId()));

        List<VehicleWithCustomizationsResponse.CustomizationCategoryWithOptions> categories =
                optionsByCategory.entrySet().stream()
                        .map(entry -> {
                            CustomizationOption firstOption = entry.getValue().get(0);
                            return VehicleWithCustomizationsResponse.CustomizationCategoryWithOptions.builder()
                                    .categoryId(entry.getKey())
                                    .categoryName(firstOption.getCategory().getName())
                                    .categoryDescription(firstOption.getCategory().getDescription())
                                    .allowsMultiple(firstOption.getCategory().getAllowsMultiple())
                                    .displayOrder(firstOption.getCategory().getDisplayOrder())
                                    .options(entry.getValue().stream()
                                            .sorted(Comparator.comparing(CustomizationOption::getDisplayOrder))
                                            .map(opt -> VehicleWithCustomizationsResponse.CustomizationOptionSummary.builder()
                                                    .id(opt.getId())
                                                    .name(opt.getName())
                                                    .description(opt.getDescription())
                                                    .priceAdjustment(opt.getPriceAdjustment())
                                                    .imageUrl(opt.getImageUrl())
                                                    .colorCode(opt.getColorCode())
                                                    .displayOrder(opt.getDisplayOrder())
                                                    .build())
                                            .collect(Collectors.toList()))
                                    .build();
                        })
                        .sorted(Comparator.comparing(VehicleWithCustomizationsResponse.CustomizationCategoryWithOptions::getDisplayOrder))
                        .collect(Collectors.toList());

        return VehicleWithCustomizationsResponse.builder()
                .vehicle(vehicleResponse)
                .customizationCategories(categories)
                .build();
    }

    /**
     * Creates a new vehicle.
     */
    @Transactional
    public VehicleResponse createVehicle(CreateVehicleRequest request) {
        log.info("Creating new vehicle: {} {}", request.getModel(), request.getModelYear());

        Brand brand = brandService.findBrandById(request.getBrandId());
        VehicleType vehicleType = vehicleTypeService.findVehicleTypeById(request.getVehicleTypeId());

        if (vehicleRepository.existsByBrandIdAndModelAndModelYear(
                request.getBrandId(), request.getModel(), request.getModelYear())) {
            throw new DuplicateResourceException("Vehicle",
                    brand.getName() + " " + request.getModel() + " " + request.getModelYear());
        }

        Vehicle vehicle = vehicleMapper.toEntity(request);
        vehicle.setBrand(brand);
        vehicle.setVehicleType(vehicleType);

        // Set customization options if provided
        if (request.getCustomizationOptionIds() != null && !request.getCustomizationOptionIds().isEmpty()) {
            Set<CustomizationOption> options = new HashSet<>(
                    optionRepository.findAllById(request.getCustomizationOptionIds()));
            vehicle.setAvailableCustomizations(options);
        }

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Created vehicle with ID: {}", saved.getId());
        return vehicleMapper.toVehicleResponse(saved);
    }

    /**
     * Updates an existing vehicle.
     */
    @Transactional
    public VehicleResponse updateVehicle(UUID id, UpdateVehicleRequest request) {
        log.info("Updating vehicle with ID: {}", id);
        Vehicle vehicle = findVehicleById(id);

        if (request.getBrandId() != null) {
            Brand brand = brandService.findBrandById(request.getBrandId());
            vehicle.setBrand(brand);
        }

        if (request.getVehicleTypeId() != null) {
            VehicleType vehicleType = vehicleTypeService.findVehicleTypeById(request.getVehicleTypeId());
            vehicle.setVehicleType(vehicleType);
        }

        vehicleMapper.updateEntity(request, vehicle);

        // Update customization options if provided
        if (request.getCustomizationOptionIds() != null) {
            Set<CustomizationOption> options = new HashSet<>(
                    optionRepository.findAllById(request.getCustomizationOptionIds()));
            vehicle.setAvailableCustomizations(options);
        }

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Updated vehicle with ID: {}", saved.getId());
        return vehicleMapper.toVehicleResponse(saved);
    }

    /**
     * Soft deletes a vehicle (sets status to DISCONTINUED).
     */
    @Transactional
    public void deleteVehicle(UUID id) {
        log.info("Deleting vehicle with ID: {}", id);
        Vehicle vehicle = findVehicleById(id);
        vehicle.setStatus(AppConstants.VehicleStatus.DISCONTINUED);
        vehicleRepository.save(vehicle);
        log.info("Deleted vehicle with ID: {}", id);
    }

    /**
     * Gets available model years for filtering.
     */
    public List<Integer> getAvailableModelYears() {
        return vehicleRepository.findDistinctAvailableModelYears();
    }

    /**
     * Internal method to find a vehicle by ID or throw exception.
     */
    public Vehicle findVehicleById(UUID id) {
        return vehicleRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id.toString()));
    }

    /**
     * Checks if a vehicle is available for order.
     */
    public boolean checkAvailability(UUID id) {
        return vehicleRepository.findById(id)
                .map(vehicle -> AppConstants.VehicleStatus.AVAILABLE.equals(vehicle.getStatus()))
                .orElse(false);
    }

    /**
     * Marks a vehicle as ordered (SOLD).
     */
    @Transactional
    public void markAsOrdered(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        if (!AppConstants.VehicleStatus.AVAILABLE.equals(vehicle.getStatus())) {
            throw new IllegalStateException("Vehicle is not available for order");
        }

        vehicle.setStatus(AppConstants.VehicleStatus.SOLD);
        vehicleRepository.save(vehicle);
        log.info("Vehicle {} marked as SOLD", id);
    }
}
