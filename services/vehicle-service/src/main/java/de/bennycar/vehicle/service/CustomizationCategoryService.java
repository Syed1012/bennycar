package de.bennycar.vehicle.service;

import de.bennycar.api.vehicle.dto.request.CreateCustomizationCategoryRequest;
import de.bennycar.api.vehicle.dto.response.CustomizationCategoryResponse;
import de.bennycar.vehicle.domain.CustomizationCategory;
import de.bennycar.vehicle.exception.DuplicateResourceException;
import de.bennycar.vehicle.exception.ResourceNotFoundException;
import de.bennycar.vehicle.repository.CustomizationCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for customization category operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomizationCategoryService {

    private final CustomizationCategoryRepository categoryRepository;
    private final MapperService mapperService;

    /**
     * Retrieves all active customization categories.
     */
    public List<CustomizationCategoryResponse> getAllActiveCategories() {
        log.debug("Fetching all active customization categories");
        return categoryRepository.findAllActiveOrderByDisplayOrder().stream()
                .map(mapperService::toCustomizationCategoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a customization category by ID.
     */
    public CustomizationCategoryResponse getCategoryById(UUID id) {
        log.debug("Fetching customization category by ID: {}", id);
        CustomizationCategory category = findCategoryById(id);
        return mapperService.toCustomizationCategoryResponse(category);
    }

    /**
     * Creates a new customization category.
     */
    @Transactional
    public CustomizationCategoryResponse createCategory(CreateCustomizationCategoryRequest request) {
        log.info("Creating new customization category: {}", request.getName());

        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("CustomizationCategory", request.getName());
        }

        CustomizationCategory category = toCustomizationCategoryEntity(request);
        CustomizationCategory saved = Objects.requireNonNull(
                categoryRepository.save(category),
                "Failed to save customization category"
        );

        log.info("Created customization category with ID: {}", saved.getId());
        return mapperService.toCustomizationCategoryResponse(saved);
    }

    /**
     * Updates an existing customization category.
     */
    @Transactional
    public CustomizationCategoryResponse updateCategory(UUID id, CreateCustomizationCategoryRequest request) {
        log.info("Updating customization category with ID: {}", id);

        CustomizationCategory category = findCategoryById(id);

        categoryRepository.findByNameIgnoreCase(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("CustomizationCategory", request.getName());
                });

        updateCustomizationCategoryEntity(request, category);
        CustomizationCategory saved = Objects.requireNonNull(
                categoryRepository.save(category),
                "Failed to save customization category"
        );

        log.info("Updated customization category with ID: {}", saved.getId());
        return mapperService.toCustomizationCategoryResponse(saved);
    }

    /**
     * Soft deletes a customization category.
     */
    @Transactional
    public void deleteCategory(UUID id) {
        log.info("Deleting customization category with ID: {}", id);
        CustomizationCategory category = findCategoryById(id);
        category.setActive(false);
        categoryRepository.save(category);
        log.info("Deleted customization category with ID: {}", id);
    }

    /**
     * Internal method to find a category by ID or throw exception.
     */
    public CustomizationCategory findCategoryById(UUID id) {
        Objects.requireNonNull(id, "Category ID cannot be null");
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomizationCategory", id.toString()));
    }

    private CustomizationCategory toCustomizationCategoryEntity(CreateCustomizationCategoryRequest request) {
        return CustomizationCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .allowsMultiple(request.getAllowsMultiple() != null ? request.getAllowsMultiple() : false)
                .active(true)
                .build();
    }

    private void updateCustomizationCategoryEntity(CreateCustomizationCategoryRequest request, CustomizationCategory category) {
        if (request.getName() != null) category.setName(request.getName());
        if (request.getDescription() != null) category.setDescription(request.getDescription());
        if (request.getDisplayOrder() != null) category.setDisplayOrder(request.getDisplayOrder());
        if (request.getAllowsMultiple() != null) category.setAllowsMultiple(request.getAllowsMultiple());
    }
}

