package de.bennycar.vehicle.service;

import de.bennycar.api.vehicle.dto.request.CreateCustomizationCategoryRequest;
import de.bennycar.api.vehicle.dto.response.CustomizationCategoryResponse;
import de.bennycar.vehicle.domain.CustomizationCategory;
import de.bennycar.vehicle.exception.DuplicateResourceException;
import de.bennycar.vehicle.exception.ResourceNotFoundException;
import de.bennycar.vehicle.repository.CustomizationCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for CustomizationCategoryService.
 * Tests all customization category-related operations including CRUD operations.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomizationCategoryService Tests")
class CustomizationCategoryServiceTest {

    @Mock
    private CustomizationCategoryRepository categoryRepository;

    @Mock
    private MapperService mapperService;

    @InjectMocks
    private CustomizationCategoryService categoryService;

    private CustomizationCategory testCategory;
    private UUID testCategoryId;
    private CustomizationCategoryResponse testCategoryResponse;
    private CreateCustomizationCategoryRequest createCategoryRequest;

    @BeforeEach
    void setUp() {
        testCategoryId = Objects.requireNonNull(UUID.randomUUID(), "testCategoryId cannot be null");
        testCategory = Objects.requireNonNull(CustomizationCategory.builder()
                .id(testCategoryId)
                .name("Exterior Color")
                .description("Choose the exterior paint color")
                .displayOrder(1)
                .allowsMultiple(false)
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build(), "testCategory cannot be null");

        testCategoryResponse = CustomizationCategoryResponse.builder()
                .id(testCategoryId)
                .name("Exterior Color")
                .description("Choose the exterior paint color")
                .displayOrder(1)
                .allowsMultiple(false)
                .active(true)
                .build();

        createCategoryRequest = CreateCustomizationCategoryRequest.builder()
                .name("Interior Color")
                .description("Choose the interior color")
                .displayOrder(2)
                .allowsMultiple(false)
                .build();
    }

    // ==================== GET ALL ACTIVE CATEGORIES ====================

    @Test
    @DisplayName("Should retrieve all active categories successfully")
    void getAllActiveCategories_shouldReturnActiveCategories_whenCategoriesExist() {
        // Given
        CustomizationCategory category1 = CustomizationCategory.builder()
                .id(UUID.randomUUID())
                .name("Exterior Color")
                .active(true)
                .build();
        CustomizationCategory category2 = CustomizationCategory.builder()
                .id(UUID.randomUUID())
                .name("Interior Color")
                .active(true)
                .build();
        List<CustomizationCategory> categories = Arrays.asList(category1, category2);

        CustomizationCategoryResponse response1 = CustomizationCategoryResponse.builder()
                .id(category1.getId())
                .name("Exterior Color")
                .build();
        CustomizationCategoryResponse response2 = CustomizationCategoryResponse.builder()
                .id(category2.getId())
                .name("Interior Color")
                .build();

        when(categoryRepository.findAllActiveOrderByDisplayOrder()).thenReturn(categories);
        when(mapperService.toCustomizationCategoryResponse(category1)).thenReturn(response1);
        when(mapperService.toCustomizationCategoryResponse(category2)).thenReturn(response2);

        // When
        List<CustomizationCategoryResponse> result = categoryService.getAllActiveCategories();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAllActiveOrderByDisplayOrder();
        verify(mapperService, times(2)).toCustomizationCategoryResponse(any(CustomizationCategory.class));
    }

    @Test
    @DisplayName("Should return empty list when no active categories exist")
    void getAllActiveCategories_shouldReturnEmptyList_whenNoCategoriesExist() {
        // Given
        when(categoryRepository.findAllActiveOrderByDisplayOrder()).thenReturn(Collections.emptyList());

        // When
        List<CustomizationCategoryResponse> result = categoryService.getAllActiveCategories();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findAllActiveOrderByDisplayOrder();
        verify(mapperService, never()).toCustomizationCategoryResponse(any());
    }

    // ==================== GET CATEGORY BY ID ====================

    @Test
    @DisplayName("Should retrieve category by ID successfully")
    void getCategoryById_shouldReturnCategory_whenCategoryExists() {
        // Given
        UUID categoryId = Objects.requireNonNull(testCategoryId, "testCategoryId cannot be null");
        CustomizationCategory category = Objects.requireNonNull(testCategory, "testCategory cannot be null");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(mapperService.toCustomizationCategoryResponse(category)).thenReturn(testCategoryResponse);

        // When
        CustomizationCategoryResponse result = categoryService.getCategoryById(categoryId);

        // Then
        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals("Exterior Color", result.getName());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(mapperService, times(1)).toCustomizationCategoryResponse(category);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when category not found")
    void getCategoryById_shouldThrowException_whenCategoryNotFound() {
        // Given
        UUID categoryId = Objects.requireNonNull(testCategoryId, "testCategoryId cannot be null");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.getCategoryById(categoryId)
        );

        assertTrue(exception.getMessage().contains("CustomizationCategory"));
        assertTrue(exception.getMessage().contains(categoryId.toString()));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(mapperService, never()).toCustomizationCategoryResponse(any());
    }

    // ==================== CREATE CATEGORY ====================

    @Test
    @DisplayName("Should create category successfully when name is unique")
    void createCategory_shouldCreateCategory_whenNameIsUnique() {
        // Given
        when(categoryRepository.existsByNameIgnoreCase(createCategoryRequest.getName())).thenReturn(false);
        
        CustomizationCategory newCategory = CustomizationCategory.builder()
                .name(createCategoryRequest.getName())
                .description(createCategoryRequest.getDescription())
                .displayOrder(createCategoryRequest.getDisplayOrder())
                .allowsMultiple(createCategoryRequest.getAllowsMultiple())
                .active(true)
                .build();
        
        CustomizationCategory savedCategory = CustomizationCategory.builder()
                .id(UUID.randomUUID())
                .name(createCategoryRequest.getName())
                .description(createCategoryRequest.getDescription())
                .active(true)
                .build();

        CustomizationCategoryResponse savedResponse = CustomizationCategoryResponse.builder()
                .id(savedCategory.getId())
                .name(savedCategory.getName())
                .description(savedCategory.getDescription())
                .build();

        when(categoryRepository.save(any(CustomizationCategory.class))).thenReturn(savedCategory);
        when(mapperService.toCustomizationCategoryResponse(savedCategory)).thenReturn(savedResponse);

        // When
        CustomizationCategoryResponse result = categoryService.createCategory(createCategoryRequest);

        // Then
        assertNotNull(result);
        assertEquals(createCategoryRequest.getName(), result.getName());
        verify(categoryRepository, times(1)).existsByNameIgnoreCase(createCategoryRequest.getName());
        verify(categoryRepository, times(1)).save(any(CustomizationCategory.class));
        verify(mapperService, times(1)).toCustomizationCategoryResponse(savedCategory);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when category name already exists")
    void createCategory_shouldThrowException_whenNameExists() {
        // Given
        when(categoryRepository.existsByNameIgnoreCase(createCategoryRequest.getName())).thenReturn(true);

        // When & Then
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> categoryService.createCategory(createCategoryRequest)
        );

        assertTrue(exception.getMessage().contains("CustomizationCategory"));
        assertTrue(exception.getMessage().contains(createCategoryRequest.getName()));
        verify(categoryRepository, times(1)).existsByNameIgnoreCase(createCategoryRequest.getName());
        verify(categoryRepository, never()).save(any());
    }

    // ==================== UPDATE CATEGORY ====================

    @Test
    @DisplayName("Should update category successfully when name is unique")
    @SuppressWarnings("null")
    void updateCategory_shouldUpdateCategory_whenNameIsUnique() {
        // Given
        UUID categoryId = Objects.requireNonNull(testCategoryId, "testCategoryId cannot be null");
        CustomizationCategory category = Objects.requireNonNull(testCategory, "testCategory cannot be null");
        CreateCustomizationCategoryRequest updateRequest = CreateCustomizationCategoryRequest.builder()
                .name("Exterior Paint")
                .description("Updated description")
                .displayOrder(1)
                .build();

        CustomizationCategory updatedCategory = CustomizationCategory.builder()
                .id(categoryId)
                .name(updateRequest.getName())
                .description(updateRequest.getDescription())
                .active(true)
                .build();

        CustomizationCategoryResponse updatedResponse = CustomizationCategoryResponse.builder()
                .id(categoryId)
                .name(updateRequest.getName())
                .description(updateRequest.getDescription())
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.findByNameIgnoreCase(updateRequest.getName())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(CustomizationCategory.class))).thenReturn(updatedCategory);
        when(mapperService.toCustomizationCategoryResponse(updatedCategory)).thenReturn(updatedResponse);

        // When
        CustomizationCategoryResponse result = categoryService.updateCategory(categoryId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(updateRequest.getName(), result.getName());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).findByNameIgnoreCase(updateRequest.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when updated name conflicts with another category")
    void updateCategory_shouldThrowException_whenNameConflicts() {
        // Given
        UUID categoryId = Objects.requireNonNull(testCategoryId, "testCategoryId cannot be null");
        CustomizationCategory category = Objects.requireNonNull(testCategory, "testCategory cannot be null");
        CreateCustomizationCategoryRequest updateRequest = CreateCustomizationCategoryRequest.builder()
                .name("Interior Color")
                .build();

        CustomizationCategory conflictingCategory = CustomizationCategory.builder()
                .id(UUID.randomUUID())
                .name("Interior Color")
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.findByNameIgnoreCase(updateRequest.getName()))
                .thenReturn(Optional.of(conflictingCategory));

        // When & Then
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> categoryService.updateCategory(categoryId, updateRequest)
        );

        assertTrue(exception.getMessage().contains("CustomizationCategory"));
        assertTrue(exception.getMessage().contains(updateRequest.getName()));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).findByNameIgnoreCase(updateRequest.getName());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent category")
    void updateCategory_shouldThrowException_whenCategoryNotFound() {
        // Given
        UUID categoryId = Objects.requireNonNull(testCategoryId, "testCategoryId cannot be null");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategory(categoryId, createCategoryRequest));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).save(any());
    }

    // ==================== DELETE CATEGORY ====================

    @Test
    @DisplayName("Should soft delete category successfully")
    @SuppressWarnings("null")
    void deleteCategory_shouldSoftDeleteCategory_whenCategoryExists() {
        // Given
        UUID categoryId = Objects.requireNonNull(testCategoryId, "testCategoryId cannot be null");
        CustomizationCategory category = Objects.requireNonNull(testCategory, "testCategory cannot be null");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);

        // When
        assertDoesNotThrow(() -> categoryService.deleteCategory(categoryId));

        // Then
        assertFalse(category.getActive());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent category")
    void deleteCategory_shouldThrowException_whenCategoryNotFound() {
        // Given
        UUID categoryId = Objects.requireNonNull(testCategoryId, "testCategoryId cannot be null");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(categoryId));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).save(any());
    }
}
