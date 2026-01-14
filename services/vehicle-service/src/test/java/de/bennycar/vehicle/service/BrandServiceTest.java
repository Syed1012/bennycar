package de.bennycar.vehicle.service;

import de.bennycar.api.vehicle.dto.request.CreateBrandRequest;
import de.bennycar.api.vehicle.dto.response.BrandResponse;
import de.bennycar.vehicle.domain.Brand;
import de.bennycar.vehicle.exception.DuplicateResourceException;
import de.bennycar.vehicle.exception.ResourceNotFoundException;
import de.bennycar.vehicle.repository.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
 * Comprehensive test suite for BrandService.
 * Tests all brand-related operations including CRUD operations and search functionality.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BrandService Tests")
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private MapperService mapperService;

    @InjectMocks
    private BrandService brandService;

    private Brand testBrand;
    private UUID testBrandId;
    private BrandResponse testBrandResponse;
    private CreateBrandRequest createBrandRequest;

    @BeforeEach
    void setUp() {
        testBrandId = Objects.requireNonNull(UUID.randomUUID(), "testBrandId cannot be null");
        testBrand = Objects.requireNonNull(Brand.builder()
                .id(testBrandId)
                .name("Tesla")
                .description("Electric vehicle manufacturer")
                .logoUrl("https://example.com/tesla-logo.png")
                .countryOfOrigin("United States")
                .foundedYear(2003)
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build(), "testBrand cannot be null");

        testBrandResponse = BrandResponse.builder()
                .id(testBrandId)
                .name("Tesla")
                .description("Electric vehicle manufacturer")
                .logoUrl("https://example.com/tesla-logo.png")
                .countryOfOrigin("United States")
                .foundedYear(2003)
                .active(true)
                .build();

        createBrandRequest = CreateBrandRequest.builder()
                .name("BMW")
                .description("Bavarian Motor Works")
                .logoUrl("https://example.com/bmw-logo.png")
                .countryOfOrigin("Germany")
                .foundedYear(1916)
                .build();
    }

    // ==================== GET ALL ACTIVE BRANDS ====================

    @Test
    @DisplayName("Should retrieve all active brands successfully")
    void getAllActiveBrands_shouldReturnActiveBrands_whenBrandsExist() {
        // Given
        Brand brand1 = Brand.builder().id(UUID.randomUUID()).name("Tesla").active(true).build();
        Brand brand2 = Brand.builder().id(UUID.randomUUID()).name("BMW").active(true).build();
        List<Brand> brands = Arrays.asList(brand1, brand2);

        BrandResponse response1 = BrandResponse.builder().id(brand1.getId()).name("Tesla").build();
        BrandResponse response2 = BrandResponse.builder().id(brand2.getId()).name("BMW").build();

        when(brandRepository.findAllActiveOrderByName()).thenReturn(brands);
        when(mapperService.toBrandResponse(brand1)).thenReturn(response1);
        when(mapperService.toBrandResponse(brand2)).thenReturn(response2);

        // When
        List<BrandResponse> result = brandService.getAllActiveBrands();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(brandRepository, times(1)).findAllActiveOrderByName();
        verify(mapperService, times(2)).toBrandResponse(any(Brand.class));
    }

    @Test
    @DisplayName("Should return empty list when no active brands exist")
    void getAllActiveBrands_shouldReturnEmptyList_whenNoBrandsExist() {
        // Given
        when(brandRepository.findAllActiveOrderByName()).thenReturn(Collections.emptyList());

        // When
        List<BrandResponse> result = brandService.getAllActiveBrands();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(brandRepository, times(1)).findAllActiveOrderByName();
        verify(mapperService, never()).toBrandResponse(any());
    }

    // ==================== GET BRANDS WITH PAGINATION ====================

    @Test
    @DisplayName("Should retrieve brands with pagination successfully")
    void getBrands_shouldReturnPaginatedBrands_whenBrandsExist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Brand brand = Brand.builder().id(testBrandId).name("Tesla").active(true).build();
        Page<Brand> brandPage = new PageImpl<>(Collections.singletonList(brand), pageable, 1);

        when(brandRepository.findByActiveTrue(pageable)).thenReturn(brandPage);
        when(mapperService.toBrandResponse(brand)).thenReturn(testBrandResponse);

        // When
        Page<BrandResponse> result = brandService.getBrands(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(brandRepository, times(1)).findByActiveTrue(pageable);
        verify(mapperService, times(1)).toBrandResponse(brand);
    }

    // ==================== GET BRAND BY ID ====================

    @Test
    @DisplayName("Should retrieve brand by ID successfully")
    void getBrandById_shouldReturnBrand_whenBrandExists() {
        // Given
        UUID brandId = Objects.requireNonNull(testBrandId, "testBrandId cannot be null");
        Brand brand = Objects.requireNonNull(testBrand, "testBrand cannot be null");
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(mapperService.toBrandResponse(brand)).thenReturn(testBrandResponse);

        // When
        BrandResponse result = brandService.getBrandById(brandId);

        // Then
        assertNotNull(result);
        assertEquals(brandId, result.getId());
        assertEquals("Tesla", result.getName());
        verify(brandRepository, times(1)).findById(brandId);
        verify(mapperService, times(1)).toBrandResponse(brand);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when brand not found")
    void getBrandById_shouldThrowException_whenBrandNotFound() {
        // Given
        UUID brandId = Objects.requireNonNull(testBrandId, "testBrandId cannot be null");
        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> brandService.getBrandById(brandId)
        );

        assertTrue(exception.getMessage().contains("Brand"));
        assertTrue(exception.getMessage().contains(brandId.toString()));
        verify(brandRepository, times(1)).findById(brandId);
        verify(mapperService, never()).toBrandResponse(any());
    }

    // ==================== CREATE BRAND ====================

    @Test
    @DisplayName("Should create brand successfully when name is unique")
    void createBrand_shouldCreateBrand_whenNameIsUnique() {
        // Given
        when(brandRepository.existsByNameIgnoreCase(createBrandRequest.getName())).thenReturn(false);
        
        Brand newBrand = Brand.builder()
                .name(createBrandRequest.getName())
                .description(createBrandRequest.getDescription())
                .active(true)
                .build();
        
        Brand savedBrand = Brand.builder()
                .id(UUID.randomUUID())
                .name(createBrandRequest.getName())
                .description(createBrandRequest.getDescription())
                .active(true)
                .build();

        BrandResponse savedResponse = BrandResponse.builder()
                .id(savedBrand.getId())
                .name(savedBrand.getName())
                .description(savedBrand.getDescription())
                .build();

        when(mapperService.toBrandEntity(createBrandRequest)).thenReturn(newBrand);
        when(brandRepository.save(any(Brand.class))).thenReturn(savedBrand);
        when(mapperService.toBrandResponse(savedBrand)).thenReturn(savedResponse);

        // When
        BrandResponse result = brandService.createBrand(createBrandRequest);

        // Then
        assertNotNull(result);
        assertEquals(createBrandRequest.getName(), result.getName());
        verify(brandRepository, times(1)).existsByNameIgnoreCase(createBrandRequest.getName());
        verify(brandRepository, times(1)).save(any(Brand.class));
        verify(mapperService, times(1)).toBrandEntity(createBrandRequest);
        verify(mapperService, times(1)).toBrandResponse(savedBrand);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when brand name already exists")
    void createBrand_shouldThrowException_whenNameExists() {
        // Given
        when(brandRepository.existsByNameIgnoreCase(createBrandRequest.getName())).thenReturn(true);

        // When & Then
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> brandService.createBrand(createBrandRequest)
        );

        assertTrue(exception.getMessage().contains("Brand"));
        assertTrue(exception.getMessage().contains(createBrandRequest.getName()));
        verify(brandRepository, times(1)).existsByNameIgnoreCase(createBrandRequest.getName());
        verify(brandRepository, never()).save(any());
        verify(mapperService, never()).toBrandEntity(any());
    }

    // ==================== UPDATE BRAND ====================

    @Test
    @DisplayName("Should update brand successfully when name is unique")
    @SuppressWarnings("null")
    void updateBrand_shouldUpdateBrand_whenNameIsUnique() {
        // Given
        UUID brandId = Objects.requireNonNull(testBrandId, "testBrandId cannot be null");
        Brand brand = Objects.requireNonNull(testBrand, "testBrand cannot be null");
        CreateBrandRequest updateRequest = CreateBrandRequest.builder()
                .name("Tesla Motors")
                .description("Updated description")
                .build();

        Brand updatedBrand = Brand.builder()
                .id(brandId)
                .name(updateRequest.getName())
                .description(updateRequest.getDescription())
                .active(true)
                .build();

        BrandResponse updatedResponse = BrandResponse.builder()
                .id(brandId)
                .name(updateRequest.getName())
                .description(updateRequest.getDescription())
                .build();

        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(brandRepository.findByNameIgnoreCase(updateRequest.getName())).thenReturn(Optional.empty());
        when(brandRepository.save(any(Brand.class))).thenReturn(updatedBrand);
        when(mapperService.toBrandResponse(updatedBrand)).thenReturn(updatedResponse);

        // When
        BrandResponse result = brandService.updateBrand(brandId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(updateRequest.getName(), result.getName());
        verify(brandRepository, times(1)).findById(brandId);
        verify(brandRepository, times(1)).findByNameIgnoreCase(updateRequest.getName());
        verify(mapperService, times(1)).updateBrandEntity(updateRequest, brand);
        verify(brandRepository, times(1)).save(brand);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when updated name conflicts with another brand")
    void updateBrand_shouldThrowException_whenNameConflicts() {
        // Given
        UUID brandId = Objects.requireNonNull(testBrandId, "testBrandId cannot be null");
        Brand brand = Objects.requireNonNull(testBrand, "testBrand cannot be null");
        CreateBrandRequest updateRequest = CreateBrandRequest.builder()
                .name("BMW")
                .build();

        Brand conflictingBrand = Brand.builder()
                .id(UUID.randomUUID())
                .name("BMW")
                .build();

        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(brandRepository.findByNameIgnoreCase(updateRequest.getName()))
                .thenReturn(Optional.of(conflictingBrand));

        // When & Then
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> brandService.updateBrand(brandId, updateRequest)
        );

        assertTrue(exception.getMessage().contains("Brand"));
        assertTrue(exception.getMessage().contains(updateRequest.getName()));
        verify(brandRepository, times(1)).findById(brandId);
        verify(brandRepository, times(1)).findByNameIgnoreCase(updateRequest.getName());
        verify(brandRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent brand")
    void updateBrand_shouldThrowException_whenBrandNotFound() {
        // Given
        UUID brandId = Objects.requireNonNull(testBrandId, "testBrandId cannot be null");
        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> brandService.updateBrand(brandId, createBrandRequest));
        verify(brandRepository, times(1)).findById(brandId);
        verify(brandRepository, never()).save(any());
    }

    // ==================== DELETE BRAND ====================

    @Test
    @DisplayName("Should soft delete brand successfully")
    @SuppressWarnings("null")
    void deleteBrand_shouldSoftDeleteBrand_whenBrandExists() {
        // Given
        UUID brandId = Objects.requireNonNull(testBrandId, "testBrandId cannot be null");
        Brand brand = Objects.requireNonNull(testBrand, "testBrand cannot be null");
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(brandRepository.save(brand)).thenReturn(brand);

        // When
        assertDoesNotThrow(() -> brandService.deleteBrand(brandId));

        // Then
        assertFalse(brand.getActive());
        verify(brandRepository, times(1)).findById(brandId);
        verify(brandRepository, times(1)).save(brand);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent brand")
    void deleteBrand_shouldThrowException_whenBrandNotFound() {
        // Given
        UUID brandId = Objects.requireNonNull(testBrandId, "testBrandId cannot be null");
        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> brandService.deleteBrand(brandId));
        verify(brandRepository, times(1)).findById(brandId);
        verify(brandRepository, never()).save(any());
    }

    // ==================== SEARCH BRANDS ====================

    @Test
    @DisplayName("Should search brands by name successfully")
    void searchBrands_shouldReturnMatchingBrands_whenQueryMatches() {
        // Given
        String query = "tes";
        Brand brand1 = Brand.builder().id(UUID.randomUUID()).name("Tesla").active(true).build();
        Brand brand2 = Brand.builder().id(UUID.randomUUID()).name("Test Brand").active(true).build();
        List<Brand> brands = Arrays.asList(brand1, brand2);

        BrandResponse response1 = BrandResponse.builder().id(brand1.getId()).name("Tesla").build();
        BrandResponse response2 = BrandResponse.builder().id(brand2.getId()).name("Test Brand").build();

        when(brandRepository.searchByName(query)).thenReturn(brands);
        when(mapperService.toBrandResponse(brand1)).thenReturn(response1);
        when(mapperService.toBrandResponse(brand2)).thenReturn(response2);

        // When
        List<BrandResponse> result = brandService.searchBrands(query);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(brandRepository, times(1)).searchByName(query);
        verify(mapperService, times(2)).toBrandResponse(any(Brand.class));
    }

    @Test
    @DisplayName("Should return empty list when search query matches no brands")
    void searchBrands_shouldReturnEmptyList_whenNoMatches() {
        // Given
        String query = "nonexistent";
        when(brandRepository.searchByName(query)).thenReturn(Collections.emptyList());

        // When
        List<BrandResponse> result = brandService.searchBrands(query);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(brandRepository, times(1)).searchByName(query);
        verify(mapperService, never()).toBrandResponse(any());
    }
}
