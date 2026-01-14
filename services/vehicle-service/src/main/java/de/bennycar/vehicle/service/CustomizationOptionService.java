package de.bennycar.vehicle.service;

import de.bennycar.api.vehicle.dto.request.CreateCustomizationOptionRequest;
import de.bennycar.api.vehicle.dto.response.CustomizationOptionResponse;
import de.bennycar.vehicle.domain.CustomizationCategory;
import de.bennycar.vehicle.domain.CustomizationOption;
import de.bennycar.vehicle.exception.DuplicateResourceException;
import de.bennycar.vehicle.exception.ResourceNotFoundException;
import de.bennycar.vehicle.repository.CustomizationOptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for customization option operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomizationOptionService {

    private final CustomizationOptionRepository optionRepository;
    private final MapperService mapperService;
    private final CustomizationCategoryService categoryService;

    /**
     * Retrieves all active customization options.
     */
    public List<CustomizationOptionResponse> getAllActiveOptions() {
        log.debug("Fetching all active customization options");
        return optionRepository.findAllActiveOrderByCategory().stream()
                .map(mapperService::toCustomizationOptionResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves options by category ID.
     */
    public List<CustomizationOptionResponse> getOptionsByCategory(UUID categoryId) {
        log.debug("Fetching options for category: {}", categoryId);
        return optionRepository.findActiveByCategoryOrderByDisplayOrder(categoryId).stream()
                .map(mapperService::toCustomizationOptionResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves options available for a specific vehicle.
     */
    public List<CustomizationOptionResponse> getOptionsForVehicle(UUID vehicleId) {
        log.debug("Fetching options for vehicle: {}", vehicleId);
        return optionRepository.findActiveByVehicleId(vehicleId).stream()
                .map(mapperService::toCustomizationOptionResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a customization option by ID.
     */
    public CustomizationOptionResponse getOptionById(UUID id) {
        log.debug("Fetching customization option by ID: {}", id);
        CustomizationOption option = findOptionById(id);
        return mapperService.toCustomizationOptionResponse(option);
    }

    /**
     * Creates a new customization option.
     */
    @Transactional
    public CustomizationOptionResponse createOption(CreateCustomizationOptionRequest request) {
        log.info("Creating new customization option: {}", request.getName());

        CustomizationCategory category = categoryService.findCategoryById(request.getCategoryId());

        if (optionRepository.existsByNameAndCategoryId(request.getName(), request.getCategoryId())) {
            throw new DuplicateResourceException("CustomizationOption",
                    request.getName() + " in category " + category.getName());
        }

        CustomizationOption option = toCustomizationOptionEntity(request, category);
        CustomizationOption saved = Objects.requireNonNull(
                optionRepository.save(option),
                "Failed to save customization option"
        );

        log.info("Created customization option with ID: {}", saved.getId());
        return mapperService.toCustomizationOptionResponse(saved);
    }

    /**
     * Updates an existing customization option.
     */
    @Transactional
    public CustomizationOptionResponse updateOption(UUID id, CreateCustomizationOptionRequest request) {
        log.info("Updating customization option with ID: {}", id);

        CustomizationOption option = findOptionById(id);
        CustomizationCategory category = categoryService.findCategoryById(request.getCategoryId());

        updateCustomizationOptionEntity(request, option);
        option.setCategory(category);
        CustomizationOption saved = optionRepository.save(option);

        log.info("Updated customization option with ID: {}", saved.getId());
        return mapperService.toCustomizationOptionResponse(saved);
    }

    /**
     * Soft deletes a customization option.
     */
    @Transactional
    public void deleteOption(UUID id) {
        log.info("Deleting customization option with ID: {}", id);
        CustomizationOption option = findOptionById(id);
        option.setActive(false);
        optionRepository.save(option);
        log.info("Deleted customization option with ID: {}", id);
    }

    /**
     * Internal method to find an option by ID or throw exception.
     */
    public CustomizationOption findOptionById(UUID id) {
        Objects.requireNonNull(id, "Option ID cannot be null");
        return optionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomizationOption", id.toString()));
    }

    private CustomizationOption toCustomizationOptionEntity(CreateCustomizationOptionRequest request, CustomizationCategory category) {
        return CustomizationOption.builder()
                .category(category)
                .name(request.getName())
                .description(request.getDescription())
                .priceAdjustment(request.getPriceAdjustment())
                .imageUrl(request.getImageUrl())
                .colorCode(request.getColorCode())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .active(true)
                .build();
    }

    private void updateCustomizationOptionEntity(CreateCustomizationOptionRequest request, CustomizationOption option) {
        if (request.getName() != null) option.setName(request.getName());
        if (request.getDescription() != null) option.setDescription(request.getDescription());
        if (request.getPriceAdjustment() != null) option.setPriceAdjustment(request.getPriceAdjustment());
        if (request.getImageUrl() != null) option.setImageUrl(request.getImageUrl());
        if (request.getColorCode() != null) option.setColorCode(request.getColorCode());
        if (request.getDisplayOrder() != null) option.setDisplayOrder(request.getDisplayOrder());
    }
}

