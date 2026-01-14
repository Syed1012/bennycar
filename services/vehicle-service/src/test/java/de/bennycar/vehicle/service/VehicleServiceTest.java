package de.bennycar.vehicle.service;

import de.bennycar.api.vehicle.dto.request.CreateVehicleRequest;
import de.bennycar.api.vehicle.dto.request.UpdateVehicleRequest;
import de.bennycar.api.vehicle.dto.request.VehicleSearchParams;
import de.bennycar.api.vehicle.dto.response.VehicleResponse;
import de.bennycar.api.vehicle.dto.response.VehicleWithCustomizationsResponse;
import de.bennycar.vehicle.constants.AppConstants;
import de.bennycar.vehicle.domain.Brand;
import de.bennycar.vehicle.domain.CustomizationCategory;
import de.bennycar.vehicle.domain.CustomizationOption;
import de.bennycar.vehicle.domain.Vehicle;
import de.bennycar.vehicle.domain.VehicleType;
import de.bennycar.vehicle.exception.DuplicateResourceException;
import de.bennycar.vehicle.exception.ResourceNotFoundException;
import de.bennycar.vehicle.repository.CustomizationOptionRepository;
import de.bennycar.vehicle.repository.VehicleRepository;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for VehicleService.
 * Tests all vehicle-related operations including search, CRUD, availability checks, and customization handling.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleService Tests")
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CustomizationOptionRepository optionRepository;

    @Mock
    private MapperService mapperService;

    @Mock
    private BrandService brandService;

    @Mock
    private VehicleTypeService vehicleTypeService;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle testVehicle;
    private UUID testVehicleId;
    private UUID testBrandId;
    private UUID testVehicleTypeId;
    private Brand testBrand;
    private VehicleType testVehicleType;
    private VehicleResponse testVehicleResponse;
    private CreateVehicleRequest createVehicleRequest;

    @BeforeEach
    void setUp() {
        testVehicleId = Objects.requireNonNull(UUID.randomUUID(), "testVehicleId cannot be null");
        testBrandId = Objects.requireNonNull(UUID.randomUUID(), "testBrandId cannot be null");
        testVehicleTypeId = Objects.requireNonNull(UUID.randomUUID(), "testVehicleTypeId cannot be null");

        testBrand = Objects.requireNonNull(Brand.builder()
                .id(testBrandId)
                .name("Tesla")
                .active(true)
                .build(), "testBrand cannot be null");

        testVehicleType = Objects.requireNonNull(VehicleType.builder()
                .id(testVehicleTypeId)
                .name("Sedan")
                .build(), "testVehicleType cannot be null");

        testVehicle = Objects.requireNonNull(Vehicle.builder()
                .id(testVehicleId)
                .brand(testBrand)
                .vehicleType(testVehicleType)
                .model("Model 3")
                .modelYear(2024)
                .description("Electric sedan")
                .basePrice(new BigDecimal("45000.00"))
                .engine("Electric")
                .transmission("Single Speed")
                .fuelType("Electric")
                .status(AppConstants.VehicleStatus.AVAILABLE)
                .stockQuantity(10)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build(), "testVehicle cannot be null");

        testVehicleResponse = VehicleResponse.builder()
                .id(testVehicleId)
                .brand(VehicleResponse.BrandSummary.builder()
                        .id(testBrandId)
                        .name("Tesla")
                        .build())
                .model("Model 3")
                .modelYear(2024)
                .basePrice(new BigDecimal("45000.00"))
                .status(AppConstants.VehicleStatus.AVAILABLE)
                .build();

        createVehicleRequest = CreateVehicleRequest.builder()
                .brandId(testBrandId)
                .vehicleTypeId(testVehicleTypeId)
                .model("Model S")
                .modelYear(2024)
                .description("Luxury electric sedan")
                .basePrice(new BigDecimal("95000.00"))
                .engine("Electric")
                .transmission("Single Speed")
                .fuelType("Electric")
                .stockQuantity(5)
                .build();
    }

    // ==================== SEARCH VEHICLES ====================

    @Test
    @DisplayName("Should search vehicles successfully with filters")
    void searchVehicles_shouldReturnFilteredVehicles_whenVehiclesMatch() {
        // Given
        VehicleSearchParams params = VehicleSearchParams.builder()
                .brandId(testBrandId)
                .status(AppConstants.VehicleStatus.AVAILABLE)
                .search("Model")
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vehicle> vehiclePage = new PageImpl<>(Collections.singletonList(testVehicle), pageable, 1);

        when(vehicleRepository.searchVehicles(anyString(), any(), any(), any(), any(), any(), anyString(), any(Pageable.class)))
                .thenReturn(vehiclePage);
        when(mapperService.toVehicleResponse(testVehicle)).thenReturn(testVehicleResponse);

        // When
        Page<VehicleResponse> result = vehicleService.searchVehicles(params, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(vehicleRepository, times(1)).searchVehicles(anyString(), any(), any(), any(), any(), any(), anyString(), any(Pageable.class));
        verify(mapperService, times(1)).toVehicleResponse(testVehicle);
    }

    @Test
    @DisplayName("Should handle empty search query gracefully")
    void searchVehicles_shouldHandleEmptySearch_whenSearchIsNull() {
        // Given
        VehicleSearchParams params = VehicleSearchParams.builder()
                .search(null)
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vehicle> vehiclePage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(vehicleRepository.searchVehicles(anyString(), any(), any(), any(), any(), any(), anyString(), any(Pageable.class)))
                .thenReturn(vehiclePage);

        // When
        Page<VehicleResponse> result = vehicleService.searchVehicles(params, pageable);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(vehicleRepository, times(1)).searchVehicles(eq(""), any(), any(), any(), any(), any(), anyString(), any(Pageable.class));
    }

    // ==================== GET VEHICLE BY ID ====================

    @Test
    @DisplayName("Should retrieve vehicle by ID successfully")
    void getVehicleById_shouldReturnVehicle_whenVehicleExists() {
        // Given
        UUID vehicleId = Objects.requireNonNull(testVehicleId, "testVehicleId cannot be null");
        Vehicle vehicle = Objects.requireNonNull(testVehicle, "testVehicle cannot be null");
        when(vehicleRepository.findByIdWithDetails(vehicleId)).thenReturn(Optional.of(vehicle));
        when(mapperService.toVehicleResponse(vehicle)).thenReturn(testVehicleResponse);

        // When
        VehicleResponse result = vehicleService.getVehicleById(vehicleId);

        // Then
        assertNotNull(result);
        assertEquals(vehicleId, result.getId());
        assertEquals("Model 3", result.getModel());
        verify(vehicleRepository, times(1)).findByIdWithDetails(vehicleId);
        verify(mapperService, times(1)).toVehicleResponse(vehicle);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when vehicle not found")
    void getVehicleById_shouldThrowException_whenVehicleNotFound() {
        // Given
        UUID vehicleId = Objects.requireNonNull(testVehicleId, "testVehicleId cannot be null");
        when(vehicleRepository.findByIdWithDetails(vehicleId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> vehicleService.getVehicleById(vehicleId)
        );

        assertTrue(exception.getMessage().contains("Vehicle"));
        assertTrue(exception.getMessage().contains(vehicleId.toString()));
        verify(vehicleRepository, times(1)).findByIdWithDetails(vehicleId);
        verify(mapperService, never()).toVehicleResponse(any());
    }

    // ==================== GET VEHICLE WITH CUSTOMIZATIONS ====================

    @Test
    @DisplayName("Should retrieve vehicle with customizations successfully")
    void getVehicleWithCustomizations_shouldReturnVehicleWithOptions_whenVehicleExists() {
        // Given
        UUID vehicleId = Objects.requireNonNull(testVehicleId, "testVehicleId cannot be null");
        Vehicle vehicle = Objects.requireNonNull(testVehicle, "testVehicle cannot be null");
        
        CustomizationCategory category = CustomizationCategory.builder()
                .id(UUID.randomUUID())
                .name("Exterior Color")
                .description("Paint colors")
                .allowsMultiple(false)
                .displayOrder(1)
                .build();

        CustomizationOption option1 = CustomizationOption.builder()
                .id(UUID.randomUUID())
                .category(category)
                .name("White")
                .priceAdjustment(BigDecimal.ZERO)
                .displayOrder(1)
                .build();

        CustomizationOption option2 = CustomizationOption.builder()
                .id(UUID.randomUUID())
                .category(category)
                .name("Black")
                .priceAdjustment(new BigDecimal("500.00"))
                .displayOrder(2)
                .build();

        vehicle.setAvailableCustomizations(Set.of(option1, option2));

        when(vehicleRepository.findByIdWithDetails(vehicleId)).thenReturn(Optional.of(vehicle));
        when(mapperService.toVehicleResponse(vehicle)).thenReturn(testVehicleResponse);

        // When
        VehicleWithCustomizationsResponse result = vehicleService.getVehicleWithCustomizations(vehicleId);

        // Then
        assertNotNull(result);
        assertNotNull(result.getVehicle());
        assertNotNull(result.getCustomizationCategories());
        assertEquals(1, result.getCustomizationCategories().size());
        assertEquals(2, result.getCustomizationCategories().get(0).getOptions().size());
        verify(vehicleRepository, times(1)).findByIdWithDetails(vehicleId);
    }

    // ==================== CREATE VEHICLE ====================

    @Test
    @DisplayName("Should create vehicle successfully when model is unique")
    void createVehicle_shouldCreateVehicle_whenModelIsUnique() {
        // Given
        UUID brandId = Objects.requireNonNull(testBrandId, "testBrandId cannot be null");
        UUID vehicleTypeId = Objects.requireNonNull(testVehicleTypeId, "testVehicleTypeId cannot be null");
        Brand brand = Objects.requireNonNull(testBrand, "testBrand cannot be null");
        VehicleType vehicleType = Objects.requireNonNull(testVehicleType, "testVehicleType cannot be null");

        Vehicle newVehicle = Vehicle.builder()
                .brand(brand)
                .vehicleType(vehicleType)
                .model(createVehicleRequest.getModel())
                .modelYear(createVehicleRequest.getModelYear())
                .basePrice(createVehicleRequest.getBasePrice())
                .status(AppConstants.VehicleStatus.AVAILABLE)
                .build();

        Vehicle savedVehicle = Vehicle.builder()
                .id(UUID.randomUUID())
                .brand(brand)
                .vehicleType(vehicleType)
                .model(createVehicleRequest.getModel())
                .modelYear(createVehicleRequest.getModelYear())
                .basePrice(createVehicleRequest.getBasePrice())
                .status(AppConstants.VehicleStatus.AVAILABLE)
                .build();

        VehicleResponse savedResponse = VehicleResponse.builder()
                .id(savedVehicle.getId())
                .model(savedVehicle.getModel())
                .modelYear(savedVehicle.getModelYear())
                .build();

        when(brandService.findBrandById(brandId)).thenReturn(brand);
        when(vehicleTypeService.findVehicleTypeById(vehicleTypeId)).thenReturn(vehicleType);
        when(vehicleRepository.existsByBrandIdAndModelAndModelYear(
                brandId, createVehicleRequest.getModel(), createVehicleRequest.getModelYear()))
                .thenReturn(false);
        when(mapperService.toVehicleEntity(createVehicleRequest, brand, vehicleType)).thenReturn(newVehicle);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);
        when(mapperService.toVehicleResponse(savedVehicle)).thenReturn(savedResponse);

        // When
        VehicleResponse result = vehicleService.createVehicle(createVehicleRequest);

        // Then
        assertNotNull(result);
        assertEquals(createVehicleRequest.getModel(), result.getModel());
        verify(brandService, times(1)).findBrandById(brandId);
        verify(vehicleTypeService, times(1)).findVehicleTypeById(vehicleTypeId);
        verify(vehicleRepository, times(1)).existsByBrandIdAndModelAndModelYear(
                brandId, createVehicleRequest.getModel(), createVehicleRequest.getModelYear());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when vehicle model already exists")
    void createVehicle_shouldThrowException_whenModelExists() {
        // Given
        UUID brandId = Objects.requireNonNull(testBrandId, "testBrandId cannot be null");
        UUID vehicleTypeId = Objects.requireNonNull(testVehicleTypeId, "testVehicleTypeId cannot be null");
        Brand brand = Objects.requireNonNull(testBrand, "testBrand cannot be null");
        VehicleType vehicleType = Objects.requireNonNull(testVehicleType, "testVehicleType cannot be null");

        when(brandService.findBrandById(brandId)).thenReturn(brand);
        when(vehicleTypeService.findVehicleTypeById(vehicleTypeId)).thenReturn(vehicleType);
        when(vehicleRepository.existsByBrandIdAndModelAndModelYear(
                brandId, createVehicleRequest.getModel(), createVehicleRequest.getModelYear()))
                .thenReturn(true);

        // When & Then
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> vehicleService.createVehicle(createVehicleRequest)
        );

        assertTrue(exception.getMessage().contains("Vehicle"));
        verify(brandService, times(1)).findBrandById(brandId);
        verify(vehicleTypeService, times(1)).findVehicleTypeById(vehicleTypeId);
        verify(vehicleRepository, times(1)).existsByBrandIdAndModelAndModelYear(
                brandId, createVehicleRequest.getModel(), createVehicleRequest.getModelYear());
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create vehicle with customization options when provided")
    void createVehicle_shouldSetCustomizationOptions_whenOptionIdsProvided() {
        // Given
        UUID brandId = Objects.requireNonNull(testBrandId, "testBrandId cannot be null");
        UUID vehicleTypeId = Objects.requireNonNull(testVehicleTypeId, "testVehicleTypeId cannot be null");
        Brand brand = Objects.requireNonNull(testBrand, "testBrand cannot be null");
        VehicleType vehicleType = Objects.requireNonNull(testVehicleType, "testVehicleType cannot be null");

        UUID optionId = UUID.randomUUID();
        CustomizationOption option = CustomizationOption.builder()
                .id(optionId)
                .name("Premium Paint")
                .build();

        createVehicleRequest.setCustomizationOptionIds(Set.of(optionId));

        Vehicle newVehicle = Vehicle.builder()
                .brand(brand)
                .vehicleType(vehicleType)
                .model(createVehicleRequest.getModel())
                .build();

        Vehicle savedVehicle = Vehicle.builder()
                .id(UUID.randomUUID())
                .brand(brand)
                .vehicleType(vehicleType)
                .model(createVehicleRequest.getModel())
                .availableCustomizations(Set.of(option))
                .build();

        VehicleResponse savedResponse = VehicleResponse.builder()
                .id(savedVehicle.getId())
                .model(savedVehicle.getModel())
                .build();

        when(brandService.findBrandById(brandId)).thenReturn(brand);
        when(vehicleTypeService.findVehicleTypeById(vehicleTypeId)).thenReturn(vehicleType);
        when(vehicleRepository.existsByBrandIdAndModelAndModelYear(any(), any(), any())).thenReturn(false);
        when(mapperService.toVehicleEntity(createVehicleRequest, brand, vehicleType)).thenReturn(newVehicle);
        when(optionRepository.findAllById(Set.of(optionId))).thenReturn(Collections.singletonList(option));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);
        when(mapperService.toVehicleResponse(savedVehicle)).thenReturn(savedResponse);

        // When
        VehicleResponse result = vehicleService.createVehicle(createVehicleRequest);

        // Then
        assertNotNull(result);
        verify(optionRepository, times(1)).findAllById(Set.of(optionId));
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    // ==================== UPDATE VEHICLE ====================

    @Test
    @DisplayName("Should update vehicle successfully")
    @SuppressWarnings("null")
    void updateVehicle_shouldUpdateVehicle_whenVehicleExists() {
        // Given
        UUID vehicleId = Objects.requireNonNull(testVehicleId, "testVehicleId cannot be null");
        Vehicle vehicle = Objects.requireNonNull(testVehicle, "testVehicle cannot be null");
        UpdateVehicleRequest updateRequest = UpdateVehicleRequest.builder()
                .model("Model 3 Performance")
                .basePrice(new BigDecimal("55000.00"))
                .build();

        Vehicle updatedVehicle = Vehicle.builder()
                .id(vehicleId)
                .brand(testBrand)
                .vehicleType(testVehicleType)
                .model(updateRequest.getModel())
                .basePrice(updateRequest.getBasePrice())
                .status(AppConstants.VehicleStatus.AVAILABLE)
                .build();

        VehicleResponse updatedResponse = VehicleResponse.builder()
                .id(vehicleId)
                .model(updateRequest.getModel())
                .basePrice(updateRequest.getBasePrice())
                .build();

        when(vehicleRepository.findByIdWithDetails(vehicleId)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(updatedVehicle);
        when(mapperService.toVehicleResponse(updatedVehicle)).thenReturn(updatedResponse);

        // When
        VehicleResponse result = vehicleService.updateVehicle(vehicleId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(updateRequest.getModel(), result.getModel());
        verify(vehicleRepository, times(1)).findByIdWithDetails(vehicleId);
        verify(vehicleRepository, times(1)).save(vehicle);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent vehicle")
    void updateVehicle_shouldThrowException_whenVehicleNotFound() {
        // Given
        UUID vehicleId = Objects.requireNonNull(testVehicleId, "testVehicleId cannot be null");
        when(vehicleRepository.findByIdWithDetails(vehicleId)).thenReturn(Optional.empty());

        UpdateVehicleRequest updateRequest = UpdateVehicleRequest.builder()
                .model("Updated Model")
                .build();

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> vehicleService.updateVehicle(vehicleId, updateRequest));
        verify(vehicleRepository, times(1)).findByIdWithDetails(vehicleId);
        verify(vehicleRepository, never()).save(any());
    }

    // ==================== DELETE VEHICLE ====================

    @Test
    @DisplayName("Should soft delete vehicle successfully")
    @SuppressWarnings("null")
    void deleteVehicle_shouldSoftDeleteVehicle_whenVehicleExists() {
        // Given
        UUID vehicleId = Objects.requireNonNull(testVehicleId, "testVehicleId cannot be null");
        Vehicle vehicle = Objects.requireNonNull(testVehicle, "testVehicle cannot be null");
        when(vehicleRepository.findByIdWithDetails(vehicleId)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);

        // When
        assertDoesNotThrow(() -> vehicleService.deleteVehicle(vehicleId));

        // Then
        assertEquals(AppConstants.VehicleStatus.DISCONTINUED, vehicle.getStatus());
        verify(vehicleRepository, times(1)).findByIdWithDetails(vehicleId);
        verify(vehicleRepository, times(1)).save(vehicle);
    }

    // ==================== CHECK AVAILABILITY ====================

    @Test
    @DisplayName("Should return true when vehicle is available")
    void checkAvailability_shouldReturnTrue_whenVehicleIsAvailable() {
        // Given
        UUID vehicleId = Objects.requireNonNull(testVehicleId, "testVehicleId cannot be null");
        Vehicle vehicle = Objects.requireNonNull(testVehicle, "testVehicle cannot be null");
        vehicle.setStatus(AppConstants.VehicleStatus.AVAILABLE);
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));

        // When
        boolean result = vehicleService.checkAvailability(vehicleId);

        // Then
        assertTrue(result);
        verify(vehicleRepository, times(1)).findById(vehicleId);
    }

    @Test
    @DisplayName("Should return false when vehicle is not available")
    void checkAvailability_shouldReturnFalse_whenVehicleIsNotAvailable() {
        // Given
        UUID vehicleId = Objects.requireNonNull(testVehicleId, "testVehicleId cannot be null");
        Vehicle vehicle = Objects.requireNonNull(testVehicle, "testVehicle cannot be null");
        vehicle.setStatus(AppConstants.VehicleStatus.SOLD);
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));

        // When
        boolean result = vehicleService.checkAvailability(vehicleId);

        // Then
        assertFalse(result);
        verify(vehicleRepository, times(1)).findById(vehicleId);
    }

    @Test
    @DisplayName("Should return false when vehicle not found")
    void checkAvailability_shouldReturnFalse_whenVehicleNotFound() {
        // Given
        UUID vehicleId = Objects.requireNonNull(testVehicleId, "testVehicleId cannot be null");
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());

        // When
        boolean result = vehicleService.checkAvailability(vehicleId);

        // Then
        assertFalse(result);
        verify(vehicleRepository, times(1)).findById(vehicleId);
    }

    // ==================== MARK AS ORDERED ====================

    @Test
    @DisplayName("Should mark vehicle as ordered successfully when available")
    void markAsOrdered_shouldMarkAsSold_whenVehicleIsAvailable() {
        // Given
        UUID vehicleId = Objects.requireNonNull(testVehicleId, "testVehicleId cannot be null");
        Vehicle vehicle = Objects.requireNonNull(testVehicle, "testVehicle cannot be null");
        vehicle.setStatus(AppConstants.VehicleStatus.AVAILABLE);
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);

        // When
        assertDoesNotThrow(() -> vehicleService.markAsOrdered(vehicleId));

        // Then
        assertEquals(AppConstants.VehicleStatus.SOLD, vehicle.getStatus());
        verify(vehicleRepository, times(1)).findById(vehicleId);
        verify(vehicleRepository, times(1)).save(vehicle);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when vehicle is not available for order")
    void markAsOrdered_shouldThrowException_whenVehicleNotAvailable() {
        // Given
        UUID vehicleId = Objects.requireNonNull(testVehicleId, "testVehicleId cannot be null");
        Vehicle vehicle = Objects.requireNonNull(testVehicle, "testVehicle cannot be null");
        vehicle.setStatus(AppConstants.VehicleStatus.SOLD);
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> vehicleService.markAsOrdered(vehicleId)
        );

        assertEquals("Vehicle is not available for order", exception.getMessage());
        verify(vehicleRepository, times(1)).findById(vehicleId);
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when vehicle not found for marking as ordered")
    void markAsOrdered_shouldThrowException_whenVehicleNotFound() {
        // Given
        UUID vehicleId = Objects.requireNonNull(testVehicleId, "testVehicleId cannot be null");
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> vehicleService.markAsOrdered(vehicleId));
        verify(vehicleRepository, times(1)).findById(vehicleId);
        verify(vehicleRepository, never()).save(any());
    }

    // ==================== GET AVAILABLE MODEL YEARS ====================

    @Test
    @DisplayName("Should retrieve available model years successfully")
    void getAvailableModelYears_shouldReturnModelYears_whenYearsExist() {
        // Given
        List<Integer> modelYears = Arrays.asList(2020, 2021, 2022, 2023, 2024);
        when(vehicleRepository.findDistinctAvailableModelYears()).thenReturn(modelYears);

        // When
        List<Integer> result = vehicleService.getAvailableModelYears();

        // Then
        assertNotNull(result);
        assertEquals(5, result.size());
        assertTrue(result.contains(2024));
        verify(vehicleRepository, times(1)).findDistinctAvailableModelYears();
    }
}
