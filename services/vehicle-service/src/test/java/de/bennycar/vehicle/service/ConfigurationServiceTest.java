package de.bennycar.vehicle.service;

import de.bennycar.api.vehicle.dto.request.CreateConfigurationRequest;
import de.bennycar.api.vehicle.dto.request.UpdateConfigurationRequest;
import de.bennycar.api.vehicle.dto.response.ConfigurationResponse;
import de.bennycar.vehicle.constants.AppConstants;
import de.bennycar.vehicle.domain.CustomizationCategory;
import de.bennycar.vehicle.domain.CustomizationOption;
import de.bennycar.vehicle.domain.Vehicle;
import de.bennycar.vehicle.domain.VehicleConfiguration;
import de.bennycar.vehicle.exception.InvalidConfigurationException;
import de.bennycar.vehicle.exception.ResourceNotFoundException;
import de.bennycar.vehicle.exception.VehicleNotAvailableException;
import de.bennycar.vehicle.repository.CustomizationOptionRepository;
import de.bennycar.vehicle.repository.VehicleConfigurationRepository;
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
 * Comprehensive test suite for ConfigurationService.
 * Tests all vehicle configuration-related operations including creation, updates, ordering, and validation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ConfigurationService Tests")
class ConfigurationServiceTest {

    @Mock
    private VehicleConfigurationRepository configurationRepository;

    @Mock
    private CustomizationOptionRepository optionRepository;

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private ConfigurationService configurationService;

    private VehicleConfiguration testConfiguration;
    private UUID testConfigurationId;
    private UUID testUserId;
    private UUID testVehicleId;
    private Vehicle testVehicle;
    private CustomizationOption testOption;
    private CreateConfigurationRequest createConfigurationRequest;
    private UpdateConfigurationRequest updateConfigurationRequest;

    @BeforeEach
    void setUp() {
        testConfigurationId = Objects.requireNonNull(UUID.randomUUID(), "testConfigurationId cannot be null");
        testUserId = Objects.requireNonNull(UUID.randomUUID(), "testUserId cannot be null");
        testVehicleId = Objects.requireNonNull(UUID.randomUUID(), "testVehicleId cannot be null");

        testVehicle = Objects.requireNonNull(Vehicle.builder()
                .id(testVehicleId)
                .model("Model 3")
                .modelYear(2024)
                .basePrice(new BigDecimal("45000.00"))
                .status(AppConstants.VehicleStatus.AVAILABLE)
                .stockQuantity(10)
                .availableCustomizations(new HashSet<>())
                .build(), "testVehicle cannot be null");

        CustomizationCategory category = CustomizationCategory.builder()
                .id(UUID.randomUUID())
                .name("Exterior Color")
                .build();

        testOption = Objects.requireNonNull(CustomizationOption.builder()
                .id(UUID.randomUUID())
                .category(category)
                .name("Alpine White")
                .priceAdjustment(BigDecimal.ZERO)
                .build(), "testOption cannot be null");

        testVehicle.getAvailableCustomizations().add(testOption);

        testConfiguration = Objects.requireNonNull(VehicleConfiguration.builder()
                .id(testConfigurationId)
                .userId(testUserId)
                .vehicle(testVehicle)
                .name("My Configuration")
                .notes("Test notes")
                .status(AppConstants.ConfigurationStatus.DRAFT)
                .selectedOptions(new HashSet<>())
                .totalPrice(new BigDecimal("45000.00"))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build(), "testConfiguration cannot be null");

        createConfigurationRequest = CreateConfigurationRequest.builder()
                .vehicleId(testVehicleId)
                .name("New Configuration")
                .notes("Configuration notes")
                .selectedOptionIds(Set.of(testOption.getId()))
                .build();

        updateConfigurationRequest = UpdateConfigurationRequest.builder()
                .name("Updated Configuration")
                .notes("Updated notes")
                .selectedOptionIds(Set.of(testOption.getId()))
                .build();
    }

    // ==================== GET USER CONFIGURATIONS ====================

    @Test
    @DisplayName("Should retrieve user configurations with pagination successfully")
    void getUserConfigurations_shouldReturnPaginatedConfigurations_whenConfigurationsExist() {
        // Given
        UUID userId = Objects.requireNonNull(testUserId, "testUserId cannot be null");
        Pageable pageable = PageRequest.of(0, 10);
        Page<VehicleConfiguration> configPage = new PageImpl<>(
                Collections.singletonList(testConfiguration), pageable, 1);

        when(configurationRepository.findByUserId(userId, pageable)).thenReturn(configPage);

        // When
        Page<ConfigurationResponse> result = configurationService.getUserConfigurations(userId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(configurationRepository, times(1)).findByUserId(userId, pageable);
    }

    // ==================== GET CONFIGURATION BY ID ====================

    @Test
    @DisplayName("Should retrieve configuration by ID successfully")
    void getConfigurationById_shouldReturnConfiguration_whenConfigurationExists() {
        // Given
        UUID configId = Objects.requireNonNull(testConfigurationId, "testConfigurationId cannot be null");
        UUID userId = Objects.requireNonNull(testUserId, "testUserId cannot be null");
        VehicleConfiguration config = Objects.requireNonNull(testConfiguration, "testConfiguration cannot be null");
        when(configurationRepository.findByIdAndUserId(configId, userId)).thenReturn(Optional.of(config));

        // When
        ConfigurationResponse result = configurationService.getConfigurationById(configId, userId);

        // Then
        assertNotNull(result);
        assertEquals(configId, result.getId());
        assertEquals(userId, result.getUserId());
        verify(configurationRepository, times(1)).findByIdAndUserId(configId, userId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when configuration not found")
    void getConfigurationById_shouldThrowException_whenConfigurationNotFound() {
        // Given
        UUID configId = Objects.requireNonNull(testConfigurationId, "testConfigurationId cannot be null");
        UUID userId = Objects.requireNonNull(testUserId, "testUserId cannot be null");
        when(configurationRepository.findByIdAndUserId(configId, userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> configurationService.getConfigurationById(configId, userId)
        );

        assertTrue(exception.getMessage().contains("VehicleConfiguration"));
        assertTrue(exception.getMessage().contains(configId.toString()));
        verify(configurationRepository, times(1)).findByIdAndUserId(configId, userId);
    }

    // ==================== CREATE CONFIGURATION ====================

    @Test
    @DisplayName("Should create configuration successfully when vehicle is available")
    void createConfiguration_shouldCreateConfiguration_whenVehicleIsAvailable() {
        // Given
        UUID userId = Objects.requireNonNull(testUserId, "testUserId cannot be null");
        UUID vehicleId = Objects.requireNonNull(testVehicleId, "testVehicleId cannot be null");
        Vehicle vehicle = Objects.requireNonNull(testVehicle, "testVehicle cannot be null");
        CustomizationOption option = Objects.requireNonNull(testOption, "testOption cannot be null");

        VehicleConfiguration newConfiguration = VehicleConfiguration.builder()
                .userId(userId)
                .vehicle(vehicle)
                .name(createConfigurationRequest.getName())
                .notes(createConfigurationRequest.getNotes())
                .status(AppConstants.ConfigurationStatus.DRAFT)
                .selectedOptions(Set.of(option))
                .build();

        VehicleConfiguration savedConfiguration = VehicleConfiguration.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .vehicle(vehicle)
                .name(createConfigurationRequest.getName())
                .status(AppConstants.ConfigurationStatus.DRAFT)
                .selectedOptions(Set.of(option))
                .totalPrice(new BigDecimal("45000.00"))
                .build();

        when(vehicleService.findVehicleById(vehicleId)).thenReturn(vehicle);
        when(optionRepository.findAllById(createConfigurationRequest.getSelectedOptionIds()))
                .thenReturn(Collections.singletonList(option));
        when(configurationRepository.save(any(VehicleConfiguration.class))).thenReturn(savedConfiguration);

        // When
        ConfigurationResponse result = configurationService.createConfiguration(createConfigurationRequest, userId);

        // Then
        assertNotNull(result);
        assertEquals(createConfigurationRequest.getName(), result.getName());
        verify(vehicleService, times(1)).findVehicleById(vehicleId);
        verify(optionRepository, times(1)).findAllById(createConfigurationRequest.getSelectedOptionIds());
        verify(configurationRepository, times(1)).save(any(VehicleConfiguration.class));
    }

    @Test
    @DisplayName("Should throw VehicleNotAvailableException when vehicle is not available")
    void createConfiguration_shouldThrowException_whenVehicleNotAvailable() {
        // Given
        UUID userId = Objects.requireNonNull(testUserId, "testUserId cannot be null");
        UUID vehicleId = Objects.requireNonNull(testVehicleId, "testVehicleId cannot be null");
        Vehicle vehicle = Objects.requireNonNull(testVehicle, "testVehicle cannot be null");
        vehicle.setStatus(AppConstants.VehicleStatus.SOLD);
        vehicle.setStockQuantity(0);

        when(vehicleService.findVehicleById(vehicleId)).thenReturn(vehicle);

        // When & Then
        VehicleNotAvailableException exception = assertThrows(
                VehicleNotAvailableException.class,
                () -> configurationService.createConfiguration(createConfigurationRequest, userId)
        );

        assertTrue(exception.getMessage().contains(testVehicleId.toString()));
        verify(vehicleService, times(1)).findVehicleById(vehicleId);
        verify(configurationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidConfigurationException when selected option is not available for vehicle")
    void createConfiguration_shouldThrowException_whenOptionNotAvailableForVehicle() {
        // Given
        UUID userId = Objects.requireNonNull(testUserId, "testUserId cannot be null");
        UUID vehicleId = Objects.requireNonNull(testVehicleId, "testVehicleId cannot be null");
        Vehicle vehicle = Objects.requireNonNull(testVehicle, "testVehicle cannot be null");
        
        UUID invalidOptionId = UUID.randomUUID();
        CustomizationOption invalidOption = CustomizationOption.builder()
                .id(invalidOptionId)
                .name("Invalid Option")
                .build();

        createConfigurationRequest.setSelectedOptionIds(Set.of(invalidOptionId));

        when(vehicleService.findVehicleById(vehicleId)).thenReturn(vehicle);
        when(optionRepository.findAllById(Set.of(invalidOptionId)))
                .thenReturn(Collections.singletonList(invalidOption));

        // When & Then
        InvalidConfigurationException exception = assertThrows(
                InvalidConfigurationException.class,
                () -> configurationService.createConfiguration(createConfigurationRequest, userId)
        );

        assertTrue(exception.getMessage().contains("not available for this vehicle"));
        verify(vehicleService, times(1)).findVehicleById(vehicleId);
        verify(optionRepository, times(1)).findAllById(Set.of(invalidOptionId));
        verify(configurationRepository, never()).save(any());
    }

    // ==================== UPDATE CONFIGURATION ====================

    @Test
    @DisplayName("Should update configuration successfully when status is DRAFT")
    @SuppressWarnings("null")
    void updateConfiguration_shouldUpdateConfiguration_whenStatusIsDraft() {
        // Given
        UUID configId = Objects.requireNonNull(testConfigurationId, "testConfigurationId cannot be null");
        UUID userId = Objects.requireNonNull(testUserId, "testUserId cannot be null");
        VehicleConfiguration config = Objects.requireNonNull(testConfiguration, "testConfiguration cannot be null");
        CustomizationOption option = Objects.requireNonNull(testOption, "testOption cannot be null");

        config.setStatus(AppConstants.ConfigurationStatus.DRAFT);

        when(configurationRepository.findByIdAndUserId(configId, userId)).thenReturn(Optional.of(config));
        when(optionRepository.findAllById(updateConfigurationRequest.getSelectedOptionIds()))
                .thenReturn(Collections.singletonList(option));
        when(configurationRepository.save(config)).thenReturn(config);

        // When
        ConfigurationResponse result = configurationService.updateConfiguration(
                configId, updateConfigurationRequest, userId);

        // Then
        assertNotNull(result);
        verify(configurationRepository, times(1)).findByIdAndUserId(configId, userId);
        verify(configurationRepository, times(1)).save(config);
    }

    @Test
    @DisplayName("Should throw InvalidConfigurationException when updating ordered configuration")
    void updateConfiguration_shouldThrowException_whenConfigurationIsOrdered() {
        // Given
        UUID configId = Objects.requireNonNull(testConfigurationId, "testConfigurationId cannot be null");
        UUID userId = Objects.requireNonNull(testUserId, "testUserId cannot be null");
        VehicleConfiguration config = Objects.requireNonNull(testConfiguration, "testConfiguration cannot be null");
        config.setStatus(AppConstants.ConfigurationStatus.ORDERED);

        when(configurationRepository.findByIdAndUserId(configId, userId)).thenReturn(Optional.of(config));

        // When & Then
        InvalidConfigurationException exception = assertThrows(
                InvalidConfigurationException.class,
                () -> configurationService.updateConfiguration(configId, updateConfigurationRequest, userId)
        );

        assertTrue(exception.getMessage().contains("Cannot modify"));
        verify(configurationRepository, times(1)).findByIdAndUserId(configId, userId);
        verify(configurationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent configuration")
    void updateConfiguration_shouldThrowException_whenConfigurationNotFound() {
        // Given
        UUID configId = Objects.requireNonNull(testConfigurationId, "testConfigurationId cannot be null");
        UUID userId = Objects.requireNonNull(testUserId, "testUserId cannot be null");
        when(configurationRepository.findByIdAndUserId(configId, userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> configurationService.updateConfiguration(configId, updateConfigurationRequest, userId));
        verify(configurationRepository, times(1)).findByIdAndUserId(configId, userId);
        verify(configurationRepository, never()).save(any());
    }

    // ==================== ORDER CONFIGURATION ====================

    @Test
    @DisplayName("Should order configuration successfully when status is DRAFT")
    @SuppressWarnings("null")
    void orderConfiguration_shouldMarkAsOrdered_whenStatusIsDraft() {
        // Given
        UUID configId = Objects.requireNonNull(testConfigurationId, "testConfigurationId cannot be null");
        UUID userId = Objects.requireNonNull(testUserId, "testUserId cannot be null");
        VehicleConfiguration config = Objects.requireNonNull(testConfiguration, "testConfiguration cannot be null");
        Vehicle vehicle = Objects.requireNonNull(testVehicle, "testVehicle cannot be null");

        config.setStatus(AppConstants.ConfigurationStatus.DRAFT);
        config.setVehicle(vehicle);

        when(configurationRepository.findByIdAndUserId(configId, userId)).thenReturn(Optional.of(config));
        when(configurationRepository.save(config)).thenReturn(config);

        // When
        ConfigurationResponse result = configurationService.orderConfiguration(configId, userId);

        // Then
        assertNotNull(result);
        assertEquals(AppConstants.ConfigurationStatus.ORDERED, config.getStatus());
        assertNotNull(config.getOrderedAt());
        verify(configurationRepository, times(1)).findByIdAndUserId(configId, userId);
        verify(configurationRepository, times(1)).save(config);
    }

    @Test
    @DisplayName("Should throw InvalidConfigurationException when ordering configuration in invalid state")
    void orderConfiguration_shouldThrowException_whenStatusIsOrdered() {
        // Given
        UUID configId = Objects.requireNonNull(testConfigurationId, "testConfigurationId cannot be null");
        UUID userId = Objects.requireNonNull(testUserId, "testUserId cannot be null");
        VehicleConfiguration config = Objects.requireNonNull(testConfiguration, "testConfiguration cannot be null");
        config.setStatus(AppConstants.ConfigurationStatus.ORDERED);

        when(configurationRepository.findByIdAndUserId(configId, userId)).thenReturn(Optional.of(config));

        // When & Then
        InvalidConfigurationException exception = assertThrows(
                InvalidConfigurationException.class,
                () -> configurationService.orderConfiguration(configId, userId)
        );

        assertTrue(exception.getMessage().contains("cannot be ordered"));
        verify(configurationRepository, times(1)).findByIdAndUserId(configId, userId);
        verify(configurationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw VehicleNotAvailableException when vehicle is not available for ordering")
    void orderConfiguration_shouldThrowException_whenVehicleNotAvailable() {
        // Given
        UUID configId = Objects.requireNonNull(testConfigurationId, "testConfigurationId cannot be null");
        UUID userId = Objects.requireNonNull(testUserId, "testUserId cannot be null");
        VehicleConfiguration config = Objects.requireNonNull(testConfiguration, "testConfiguration cannot be null");
        Vehicle vehicle = Objects.requireNonNull(testVehicle, "testVehicle cannot be null");
        
        config.setStatus(AppConstants.ConfigurationStatus.DRAFT);
        config.setVehicle(vehicle);
        vehicle.setStatus(AppConstants.VehicleStatus.SOLD);
        vehicle.setStockQuantity(0);

        when(configurationRepository.findByIdAndUserId(configId, userId)).thenReturn(Optional.of(config));

        // When & Then
        VehicleNotAvailableException exception = assertThrows(
                VehicleNotAvailableException.class,
                () -> configurationService.orderConfiguration(configId, userId)
        );

        assertTrue(exception.getMessage().contains(testVehicleId.toString()));
        verify(configurationRepository, times(1)).findByIdAndUserId(configId, userId);
        verify(configurationRepository, never()).save(any());
    }

    // ==================== DELETE CONFIGURATION ====================

    @Test
    @DisplayName("Should delete configuration successfully when status is DRAFT")
    @SuppressWarnings("null")
    void deleteConfiguration_shouldDeleteConfiguration_whenStatusIsDraft() {
        // Given
        UUID configId = Objects.requireNonNull(testConfigurationId, "testConfigurationId cannot be null");
        UUID userId = Objects.requireNonNull(testUserId, "testUserId cannot be null");
        VehicleConfiguration config = Objects.requireNonNull(testConfiguration, "testConfiguration cannot be null");
        config.setStatus(AppConstants.ConfigurationStatus.DRAFT);

        when(configurationRepository.findByIdAndUserId(configId, userId)).thenReturn(Optional.of(config));
        doNothing().when(configurationRepository).delete(config);

        // When
        assertDoesNotThrow(() -> configurationService.deleteConfiguration(configId, userId));

        // Then
        verify(configurationRepository, times(1)).findByIdAndUserId(configId, userId);
        verify(configurationRepository, times(1)).delete(config);
    }

    @Test
    @DisplayName("Should throw InvalidConfigurationException when deleting ordered configuration")
    void deleteConfiguration_shouldThrowException_whenConfigurationIsOrdered() {
        // Given
        UUID configId = Objects.requireNonNull(testConfigurationId, "testConfigurationId cannot be null");
        UUID userId = Objects.requireNonNull(testUserId, "testUserId cannot be null");
        VehicleConfiguration config = Objects.requireNonNull(testConfiguration, "testConfiguration cannot be null");
        config.setStatus(AppConstants.ConfigurationStatus.ORDERED);

        when(configurationRepository.findByIdAndUserId(configId, userId)).thenReturn(Optional.of(config));

        // When & Then
        InvalidConfigurationException exception = assertThrows(
                InvalidConfigurationException.class,
                () -> configurationService.deleteConfiguration(configId, userId)
        );

        assertTrue(exception.getMessage().contains("Cannot delete"));
        verify(configurationRepository, times(1)).findByIdAndUserId(configId, userId);
        verify(configurationRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent configuration")
    void deleteConfiguration_shouldThrowException_whenConfigurationNotFound() {
        // Given
        UUID configId = Objects.requireNonNull(testConfigurationId, "testConfigurationId cannot be null");
        UUID userId = Objects.requireNonNull(testUserId, "testUserId cannot be null");
        when(configurationRepository.findByIdAndUserId(configId, userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> configurationService.deleteConfiguration(configId, userId));
        verify(configurationRepository, times(1)).findByIdAndUserId(configId, userId);
        verify(configurationRepository, never()).delete(any());
    }

    // ==================== GET CONFIGURATION COUNT ====================

    @Test
    @DisplayName("Should return configuration count for user successfully")
    void getConfigurationCount_shouldReturnCount_whenConfigurationsExist() {
        // Given
        UUID userId = Objects.requireNonNull(testUserId, "testUserId cannot be null");
        when(configurationRepository.countByUserId(userId)).thenReturn(5L);

        // When
        long result = configurationService.getConfigurationCount(userId);

        // Then
        assertEquals(5L, result);
        verify(configurationRepository, times(1)).countByUserId(userId);
    }

    @Test
    @DisplayName("Should return zero when user has no configurations")
    void getConfigurationCount_shouldReturnZero_whenNoConfigurationsExist() {
        // Given
        UUID userId = Objects.requireNonNull(testUserId, "testUserId cannot be null");
        when(configurationRepository.countByUserId(userId)).thenReturn(0L);

        // When
        long result = configurationService.getConfigurationCount(userId);

        // Then
        assertEquals(0L, result);
        verify(configurationRepository, times(1)).countByUserId(userId);
    }
}
