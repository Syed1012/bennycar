package de.bennycar.vehicle.service;

import de.bennycar.api.vehicle.dto.request.CreateCustomizationOptionRequest;
import de.bennycar.api.vehicle.dto.response.CustomizationOptionResponse;
import de.bennycar.vehicle.domain.CustomizationCategory;
import de.bennycar.vehicle.domain.CustomizationOption;
import de.bennycar.vehicle.exception.DuplicateResourceException;
import de.bennycar.vehicle.exception.ResourceNotFoundException;
import de.bennycar.vehicle.repository.CustomizationOptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
 * Comprehensive test suite for CustomizationOptionService.
 * Tests all customization option-related operations including CRUD operations.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomizationOptionService Tests")
class CustomizationOptionServiceTest {

    @Mock
    private CustomizationOptionRepository optionRepository;

    @Mock
    private MapperService mapperService;

    @Mock
    private CustomizationCategoryService categoryService;

    @InjectMocks
    private CustomizationOptionService optionService;

    private CustomizationOption testOption;
    private UUID testOptionId;
    private CustomizationCategory testCategory;
    private UUID testCategoryId;
    private CustomizationOptionResponse testOptionResponse;
    private CreateCustomizationOptionRequest createOptionRequest;

    @BeforeEach
    void setUp() {
        testCategoryId = Objects.requireNonNull(UUID.randomUUID(), "testCategoryId cannot be null");
        testCategory = Objects.requireNonNull(CustomizationCategory.builder()
                .id(testCategoryId)
                .name("Exterior Color")
                .description("Exterior paint colors")
                .active(true)
                .build(), "testCategory cannot be null");

        testOptionId = Objects.requireNonNull(UUID.randomUUID(), "testOptionId cannot be null");
        testOption = Objects.requireNonNull(CustomizationOption.builder()
                .id(testOptionId)
                .category(testCategory)
                .name("Alpine White")
                .description("Classic pure white finish")
                .priceAdjustment(BigDecimal.ZERO)
                .colorCode("#FFFFFF")
                .displayOrder(1)
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build(), "testOption cannot be null");

        testOptionResponse = CustomizationOptionResponse.builder()
                .id(testOptionId)
                .categoryId(testCategoryId)
                .categoryName("Exterior Color")
                .name("Alpine White")
                .description("Classic pure white finish")
                .priceAdjustment(BigDecimal.ZERO)
                .colorCode("#FFFFFF")
                .displayOrder(1)
                .active(true)
                .build();

        createOptionRequest = CreateCustomizationOptionRequest.builder()
                .categoryId(testCategoryId)
                .name("Midnight Black")
                .description("Deep black finish")
                .priceAdjustment(new BigDecimal("500.00"))
                .colorCode("#000000")
                .displayOrder(2)
                .build();
    }

    // ==================== GET ALL ACTIVE OPTIONS ====================

    @Test
    @DisplayName("Should retrieve all active options successfully")
    void getAllActiveOptions_shouldReturnActiveOptions_whenOptionsExist() {
        // Given
        CustomizationOption option1 = CustomizationOption.builder()
                .id(UUID.randomUUID())
                .name("Option 1")
                .active(true)
                .build();
        CustomizationOption option2 = CustomizationOption.builder()
                .id(UUID.randomUUID())
                .name("Option 2")
                .active(true)
                .build();
        List<CustomizationOption> options = Arrays.asList(option1, option2);

        CustomizationOptionResponse response1 = CustomizationOptionResponse.builder()
                .id(option1.getId())
                .name("Option 1")
                .build();
        CustomizationOptionResponse response2 = CustomizationOptionResponse.builder()
                .id(option2.getId())
                .name("Option 2")
                .build();

        when(optionRepository.findAllActiveOrderByCategory()).thenReturn(options);
        when(mapperService.toCustomizationOptionResponse(option1)).thenReturn(response1);
        when(mapperService.toCustomizationOptionResponse(option2)).thenReturn(response2);

        // When
        List<CustomizationOptionResponse> result = optionService.getAllActiveOptions();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(optionRepository, times(1)).findAllActiveOrderByCategory();
        verify(mapperService, times(2)).toCustomizationOptionResponse(any(CustomizationOption.class));
    }

    // ==================== GET OPTIONS BY CATEGORY ====================

    @Test
    @DisplayName("Should retrieve options by category ID successfully")
    void getOptionsByCategory_shouldReturnOptions_whenCategoryHasOptions() {
        // Given
        UUID categoryId = Objects.requireNonNull(testCategoryId, "testCategoryId cannot be null");
        CustomizationOption option = Objects.requireNonNull(testOption, "testOption cannot be null");
        List<CustomizationOption> options = Collections.singletonList(option);

        when(optionRepository.findActiveByCategoryOrderByDisplayOrder(categoryId)).thenReturn(options);
        when(mapperService.toCustomizationOptionResponse(option)).thenReturn(testOptionResponse);

        // When
        List<CustomizationOptionResponse> result = optionService.getOptionsByCategory(categoryId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOptionId, result.get(0).getId());
        verify(optionRepository, times(1)).findActiveByCategoryOrderByDisplayOrder(categoryId);
        verify(mapperService, times(1)).toCustomizationOptionResponse(option);
    }

    // ==================== GET OPTION BY ID ====================

    @Test
    @DisplayName("Should retrieve option by ID successfully")
    void getOptionById_shouldReturnOption_whenOptionExists() {
        // Given
        UUID optionId = Objects.requireNonNull(testOptionId, "testOptionId cannot be null");
        CustomizationOption option = Objects.requireNonNull(testOption, "testOption cannot be null");
        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));
        when(mapperService.toCustomizationOptionResponse(option)).thenReturn(testOptionResponse);

        // When
        CustomizationOptionResponse result = optionService.getOptionById(optionId);

        // Then
        assertNotNull(result);
        assertEquals(optionId, result.getId());
        assertEquals("Alpine White", result.getName());
        verify(optionRepository, times(1)).findById(optionId);
        verify(mapperService, times(1)).toCustomizationOptionResponse(option);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when option not found")
    void getOptionById_shouldThrowException_whenOptionNotFound() {
        // Given
        UUID optionId = Objects.requireNonNull(testOptionId, "testOptionId cannot be null");
        when(optionRepository.findById(optionId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> optionService.getOptionById(optionId)
        );

        assertTrue(exception.getMessage().contains("CustomizationOption"));
        assertTrue(exception.getMessage().contains(optionId.toString()));
        verify(optionRepository, times(1)).findById(optionId);
        verify(mapperService, never()).toCustomizationOptionResponse(any());
    }

    // ==================== CREATE OPTION ====================

    @Test
    @DisplayName("Should create option successfully when name is unique in category")
    void createOption_shouldCreateOption_whenNameIsUniqueInCategory() {
        // Given
        UUID categoryId = Objects.requireNonNull(testCategoryId, "testCategoryId cannot be null");
        CustomizationCategory category = Objects.requireNonNull(testCategory, "testCategory cannot be null");
        
        when(categoryService.findCategoryById(categoryId)).thenReturn(category);
        when(optionRepository.existsByNameAndCategoryId(createOptionRequest.getName(), categoryId))
                .thenReturn(false);
        
        CustomizationOption newOption = CustomizationOption.builder()
                .category(category)
                .name(createOptionRequest.getName())
                .description(createOptionRequest.getDescription())
                .priceAdjustment(createOptionRequest.getPriceAdjustment())
                .colorCode(createOptionRequest.getColorCode())
                .displayOrder(createOptionRequest.getDisplayOrder())
                .active(true)
                .build();
        
        CustomizationOption savedOption = CustomizationOption.builder()
                .id(UUID.randomUUID())
                .category(category)
                .name(createOptionRequest.getName())
                .active(true)
                .build();

        CustomizationOptionResponse savedResponse = CustomizationOptionResponse.builder()
                .id(savedOption.getId())
                .name(savedOption.getName())
                .build();

        when(optionRepository.save(any(CustomizationOption.class))).thenReturn(savedOption);
        when(mapperService.toCustomizationOptionResponse(savedOption)).thenReturn(savedResponse);

        // When
        CustomizationOptionResponse result = optionService.createOption(createOptionRequest);

        // Then
        assertNotNull(result);
        assertEquals(createOptionRequest.getName(), result.getName());
        verify(categoryService, times(1)).findCategoryById(categoryId);
        verify(optionRepository, times(1)).existsByNameAndCategoryId(createOptionRequest.getName(), categoryId);
        verify(optionRepository, times(1)).save(any(CustomizationOption.class));
        verify(mapperService, times(1)).toCustomizationOptionResponse(savedOption);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when option name exists in category")
    void createOption_shouldThrowException_whenNameExistsInCategory() {
        // Given
        UUID categoryId = Objects.requireNonNull(testCategoryId, "testCategoryId cannot be null");
        CustomizationCategory category = Objects.requireNonNull(testCategory, "testCategory cannot be null");
        
        when(categoryService.findCategoryById(categoryId)).thenReturn(category);
        when(optionRepository.existsByNameAndCategoryId(createOptionRequest.getName(), categoryId))
                .thenReturn(true);

        // When & Then
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> optionService.createOption(createOptionRequest)
        );

        assertTrue(exception.getMessage().contains("CustomizationOption"));
        assertTrue(exception.getMessage().contains(createOptionRequest.getName()));
        verify(categoryService, times(1)).findCategoryById(categoryId);
        verify(optionRepository, times(1)).existsByNameAndCategoryId(createOptionRequest.getName(), categoryId);
        verify(optionRepository, never()).save(any());
    }

    // ==================== UPDATE OPTION ====================

    @Test
    @DisplayName("Should update option successfully")
    @SuppressWarnings("null")
    void updateOption_shouldUpdateOption_whenOptionExists() {
        // Given
        UUID optionId = Objects.requireNonNull(testOptionId, "testOptionId cannot be null");
        CustomizationOption option = Objects.requireNonNull(testOption, "testOption cannot be null");
        UUID categoryId = Objects.requireNonNull(testCategoryId, "testCategoryId cannot be null");
        CustomizationCategory category = Objects.requireNonNull(testCategory, "testCategory cannot be null");
        
        CreateCustomizationOptionRequest updateRequest = CreateCustomizationOptionRequest.builder()
                .categoryId(categoryId)
                .name("Updated White")
                .description("Updated description")
                .priceAdjustment(new BigDecimal("200.00"))
                .build();

        CustomizationOption updatedOption = CustomizationOption.builder()
                .id(optionId)
                .category(category)
                .name(updateRequest.getName())
                .description(updateRequest.getDescription())
                .active(true)
                .build();

        CustomizationOptionResponse updatedResponse = CustomizationOptionResponse.builder()
                .id(optionId)
                .name(updateRequest.getName())
                .description(updateRequest.getDescription())
                .build();

        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));
        when(categoryService.findCategoryById(categoryId)).thenReturn(category);
        when(optionRepository.save(any(CustomizationOption.class))).thenReturn(updatedOption);
        when(mapperService.toCustomizationOptionResponse(updatedOption)).thenReturn(updatedResponse);

        // When
        CustomizationOptionResponse result = optionService.updateOption(optionId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(updateRequest.getName(), result.getName());
        verify(optionRepository, times(1)).findById(optionId);
        verify(categoryService, times(1)).findCategoryById(categoryId);
        verify(optionRepository, times(1)).save(option);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent option")
    void updateOption_shouldThrowException_whenOptionNotFound() {
        // Given
        UUID optionId = Objects.requireNonNull(testOptionId, "testOptionId cannot be null");
        when(optionRepository.findById(optionId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> optionService.updateOption(optionId, createOptionRequest));
        verify(optionRepository, times(1)).findById(optionId);
        verify(optionRepository, never()).save(any());
    }

    // ==================== DELETE OPTION ====================

    @Test
    @DisplayName("Should soft delete option successfully")
    @SuppressWarnings("null")
    void deleteOption_shouldSoftDeleteOption_whenOptionExists() {
        // Given
        UUID optionId = Objects.requireNonNull(testOptionId, "testOptionId cannot be null");
        CustomizationOption option = Objects.requireNonNull(testOption, "testOption cannot be null");
        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));
        when(optionRepository.save(option)).thenReturn(option);

        // When
        assertDoesNotThrow(() -> optionService.deleteOption(optionId));

        // Then
        assertFalse(option.getActive());
        verify(optionRepository, times(1)).findById(optionId);
        verify(optionRepository, times(1)).save(option);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent option")
    void deleteOption_shouldThrowException_whenOptionNotFound() {
        // Given
        UUID optionId = Objects.requireNonNull(testOptionId, "testOptionId cannot be null");
        when(optionRepository.findById(optionId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> optionService.deleteOption(optionId));
        verify(optionRepository, times(1)).findById(optionId);
        verify(optionRepository, never()).save(any());
    }
}
