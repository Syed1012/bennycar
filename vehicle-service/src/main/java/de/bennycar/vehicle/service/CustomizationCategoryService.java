package de.bennycar.vehicle.service;

import de.bennycar.vehicle.domain.CustomizationCategory;
import de.bennycar.vehicle.dto.CreateCustomizationCategoryRequest;
import de.bennycar.vehicle.dto.CustomizationCategoryResponse;
import de.bennycar.vehicle.exception.DuplicateResourceException;
import de.bennycar.vehicle.exception.ResourceNotFoundException;
import de.bennycar.vehicle.mapper.CustomizationCategoryMapper;
import de.bennycar.vehicle.repository.CustomizationCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private final CustomizationCategoryMapper categoryMapper;

    /**
     * Retrieves all active customization categories.
     */
    public List<CustomizationCategoryResponse> getAllActiveCategories() {
        log.debug("Fetching all active customization categories");
        return categoryRepository.findAllActiveOrderByDisplayOrder().stream()
                .map(categoryMapper::toCustomizationCategoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a customization category by ID.
     */
    public CustomizationCategoryResponse getCategoryById(UUID id) {
        log.debug("Fetching customization category by ID: {}", id);
        CustomizationCategory category = findCategoryById(id);
        return categoryMapper.toCustomizationCategoryResponse(category);
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

        CustomizationCategory category = categoryMapper.toEntity(request);
        CustomizationCategory saved = categoryRepository.save(category);

        log.info("Created customization category with ID: {}", saved.getId());
        return categoryMapper.toCustomizationCategoryResponse(saved);
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

        categoryMapper.updateEntity(request, category);
        CustomizationCategory saved = categoryRepository.save(category);

        log.info("Updated customization category with ID: {}", saved.getId());
        return categoryMapper.toCustomizationCategoryResponse(saved);
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
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomizationCategory", id.toString()));
    }
}

