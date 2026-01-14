package de.bennycar.vehicle.service;

import de.bennycar.api.vehicle.dto.request.CreateVehicleTypeRequest;
import de.bennycar.api.vehicle.dto.response.VehicleTypeResponse;
import de.bennycar.vehicle.domain.VehicleType;
import de.bennycar.vehicle.exception.DuplicateResourceException;
import de.bennycar.vehicle.exception.ResourceNotFoundException;
import de.bennycar.vehicle.repository.VehicleTypeRepository;
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
 * Comprehensive test suite for VehicleTypeService.
 * Tests all vehicle type-related operations including CRUD operations.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleTypeService Tests")
class VehicleTypeServiceTest {

    @Mock
    private VehicleTypeRepository vehicleTypeRepository;

    @Mock
    private MapperService mapperService;

    @InjectMocks
    private VehicleTypeService vehicleTypeService;

    private VehicleType testVehicleType;
    private UUID testVehicleTypeId;
    private VehicleTypeResponse testVehicleTypeResponse;
    private CreateVehicleTypeRequest createVehicleTypeRequest;

    @BeforeEach
    void setUp() {
        testVehicleTypeId = Objects.requireNonNull(UUID.randomUUID(), "testVehicleTypeId cannot be null");
        testVehicleType = Objects.requireNonNull(VehicleType.builder()
                .id(testVehicleTypeId)
                .name("Sedan")
                .description("Four-door passenger car")
                .iconUrl("https://example.com/sedan-icon.png")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build(), "testVehicleType cannot be null");

        testVehicleTypeResponse = VehicleTypeResponse.builder()
                .id(testVehicleTypeId)
                .name("Sedan")
                .description("Four-door passenger car")
                .iconUrl("https://example.com/sedan-icon.png")
                .build();

        createVehicleTypeRequest = CreateVehicleTypeRequest.builder()
                .name("SUV")
                .description("Sport Utility Vehicle")
                .iconUrl("https://example.com/suv-icon.png")
                .build();
    }

    // ==================== GET ALL VEHICLE TYPES ====================

    @Test
    @DisplayName("Should retrieve all vehicle types successfully")
    void getAllVehicleTypes_shouldReturnVehicleTypes_whenTypesExist() {
        // Given
        VehicleType type1 = VehicleType.builder().id(UUID.randomUUID()).name("Sedan").build();
        VehicleType type2 = VehicleType.builder().id(UUID.randomUUID()).name("SUV").build();
        List<VehicleType> types = Arrays.asList(type1, type2);

        VehicleTypeResponse response1 = VehicleTypeResponse.builder().id(type1.getId()).name("Sedan").build();
        VehicleTypeResponse response2 = VehicleTypeResponse.builder().id(type2.getId()).name("SUV").build();

        when(vehicleTypeRepository.findAllOrderByName()).thenReturn(types);
        when(mapperService.toVehicleTypeResponse(type1)).thenReturn(response1);
        when(mapperService.toVehicleTypeResponse(type2)).thenReturn(response2);

        // When
        List<VehicleTypeResponse> result = vehicleTypeService.getAllVehicleTypes();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(vehicleTypeRepository, times(1)).findAllOrderByName();
        verify(mapperService, times(2)).toVehicleTypeResponse(any(VehicleType.class));
    }

    @Test
    @DisplayName("Should return empty list when no vehicle types exist")
    void getAllVehicleTypes_shouldReturnEmptyList_whenNoTypesExist() {
        // Given
        when(vehicleTypeRepository.findAllOrderByName()).thenReturn(Collections.emptyList());

        // When
        List<VehicleTypeResponse> result = vehicleTypeService.getAllVehicleTypes();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(vehicleTypeRepository, times(1)).findAllOrderByName();
        verify(mapperService, never()).toVehicleTypeResponse(any());
    }

    // ==================== GET VEHICLE TYPE BY ID ====================

    @Test
    @DisplayName("Should retrieve vehicle type by ID successfully")
    void getVehicleTypeById_shouldReturnVehicleType_whenTypeExists() {
        // Given
        UUID typeId = Objects.requireNonNull(testVehicleTypeId, "testVehicleTypeId cannot be null");
        VehicleType vehicleType = Objects.requireNonNull(testVehicleType, "testVehicleType cannot be null");
        when(vehicleTypeRepository.findById(typeId)).thenReturn(Optional.of(vehicleType));
        when(mapperService.toVehicleTypeResponse(vehicleType)).thenReturn(testVehicleTypeResponse);

        // When
        VehicleTypeResponse result = vehicleTypeService.getVehicleTypeById(typeId);

        // Then
        assertNotNull(result);
        assertEquals(typeId, result.getId());
        assertEquals("Sedan", result.getName());
        verify(vehicleTypeRepository, times(1)).findById(typeId);
        verify(mapperService, times(1)).toVehicleTypeResponse(vehicleType);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when vehicle type not found")
    void getVehicleTypeById_shouldThrowException_whenTypeNotFound() {
        // Given
        UUID typeId = Objects.requireNonNull(testVehicleTypeId, "testVehicleTypeId cannot be null");
        when(vehicleTypeRepository.findById(typeId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> vehicleTypeService.getVehicleTypeById(typeId)
        );

        assertTrue(exception.getMessage().contains("VehicleType"));
        assertTrue(exception.getMessage().contains(typeId.toString()));
        verify(vehicleTypeRepository, times(1)).findById(typeId);
        verify(mapperService, never()).toVehicleTypeResponse(any());
    }

    // ==================== CREATE VEHICLE TYPE ====================

    @Test
    @DisplayName("Should create vehicle type successfully when name is unique")
    void createVehicleType_shouldCreateType_whenNameIsUnique() {
        // Given
        when(vehicleTypeRepository.existsByNameIgnoreCase(createVehicleTypeRequest.getName())).thenReturn(false);
        
        VehicleType newType = VehicleType.builder()
                .name(createVehicleTypeRequest.getName())
                .description(createVehicleTypeRequest.getDescription())
                .build();
        
        VehicleType savedType = VehicleType.builder()
                .id(UUID.randomUUID())
                .name(createVehicleTypeRequest.getName())
                .description(createVehicleTypeRequest.getDescription())
                .build();

        VehicleTypeResponse savedResponse = VehicleTypeResponse.builder()
                .id(savedType.getId())
                .name(savedType.getName())
                .description(savedType.getDescription())
                .build();

        when(mapperService.toVehicleTypeEntity(createVehicleTypeRequest)).thenReturn(newType);
        when(vehicleTypeRepository.save(any(VehicleType.class))).thenReturn(savedType);
        when(mapperService.toVehicleTypeResponse(savedType)).thenReturn(savedResponse);

        // When
        VehicleTypeResponse result = vehicleTypeService.createVehicleType(createVehicleTypeRequest);

        // Then
        assertNotNull(result);
        assertEquals(createVehicleTypeRequest.getName(), result.getName());
        verify(vehicleTypeRepository, times(1)).existsByNameIgnoreCase(createVehicleTypeRequest.getName());
        verify(vehicleTypeRepository, times(1)).save(any(VehicleType.class));
        verify(mapperService, times(1)).toVehicleTypeEntity(createVehicleTypeRequest);
        verify(mapperService, times(1)).toVehicleTypeResponse(savedType);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when vehicle type name already exists")
    void createVehicleType_shouldThrowException_whenNameExists() {
        // Given
        when(vehicleTypeRepository.existsByNameIgnoreCase(createVehicleTypeRequest.getName())).thenReturn(true);

        // When & Then
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> vehicleTypeService.createVehicleType(createVehicleTypeRequest)
        );

        assertTrue(exception.getMessage().contains("VehicleType"));
        assertTrue(exception.getMessage().contains(createVehicleTypeRequest.getName()));
        verify(vehicleTypeRepository, times(1)).existsByNameIgnoreCase(createVehicleTypeRequest.getName());
        verify(vehicleTypeRepository, never()).save(any());
        verify(mapperService, never()).toVehicleTypeEntity(any());
    }

    // ==================== UPDATE VEHICLE TYPE ====================

    @Test
    @DisplayName("Should update vehicle type successfully when name is unique")
    @SuppressWarnings("null")
    void updateVehicleType_shouldUpdateType_whenNameIsUnique() {
        // Given
        UUID typeId = Objects.requireNonNull(testVehicleTypeId, "testVehicleTypeId cannot be null");
        VehicleType vehicleType = Objects.requireNonNull(testVehicleType, "testVehicleType cannot be null");
        CreateVehicleTypeRequest updateRequest = CreateVehicleTypeRequest.builder()
                .name("Sedan Plus")
                .description("Updated description")
                .build();

        VehicleType updatedType = VehicleType.builder()
                .id(typeId)
                .name(updateRequest.getName())
                .description(updateRequest.getDescription())
                .build();

        VehicleTypeResponse updatedResponse = VehicleTypeResponse.builder()
                .id(typeId)
                .name(updateRequest.getName())
                .description(updateRequest.getDescription())
                .build();

        when(vehicleTypeRepository.findById(typeId)).thenReturn(Optional.of(vehicleType));
        when(vehicleTypeRepository.findByNameIgnoreCase(updateRequest.getName())).thenReturn(Optional.empty());
        when(vehicleTypeRepository.save(any(VehicleType.class))).thenReturn(updatedType);
        when(mapperService.toVehicleTypeResponse(updatedType)).thenReturn(updatedResponse);

        // When
        VehicleTypeResponse result = vehicleTypeService.updateVehicleType(typeId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(updateRequest.getName(), result.getName());
        verify(vehicleTypeRepository, times(1)).findById(typeId);
        verify(vehicleTypeRepository, times(1)).findByNameIgnoreCase(updateRequest.getName());
        verify(mapperService, times(1)).updateVehicleTypeEntity(updateRequest, vehicleType);
        verify(vehicleTypeRepository, times(1)).save(vehicleType);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when updated name conflicts with another type")
    void updateVehicleType_shouldThrowException_whenNameConflicts() {
        // Given
        UUID typeId = Objects.requireNonNull(testVehicleTypeId, "testVehicleTypeId cannot be null");
        VehicleType vehicleType = Objects.requireNonNull(testVehicleType, "testVehicleType cannot be null");
        CreateVehicleTypeRequest updateRequest = CreateVehicleTypeRequest.builder()
                .name("SUV")
                .build();

        VehicleType conflictingType = VehicleType.builder()
                .id(UUID.randomUUID())
                .name("SUV")
                .build();

        when(vehicleTypeRepository.findById(typeId)).thenReturn(Optional.of(vehicleType));
        when(vehicleTypeRepository.findByNameIgnoreCase(updateRequest.getName()))
                .thenReturn(Optional.of(conflictingType));

        // When & Then
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> vehicleTypeService.updateVehicleType(typeId, updateRequest)
        );

        assertTrue(exception.getMessage().contains("VehicleType"));
        assertTrue(exception.getMessage().contains(updateRequest.getName()));
        verify(vehicleTypeRepository, times(1)).findById(typeId);
        verify(vehicleTypeRepository, times(1)).findByNameIgnoreCase(updateRequest.getName());
        verify(vehicleTypeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent vehicle type")
    void updateVehicleType_shouldThrowException_whenTypeNotFound() {
        // Given
        UUID typeId = Objects.requireNonNull(testVehicleTypeId, "testVehicleTypeId cannot be null");
        when(vehicleTypeRepository.findById(typeId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> vehicleTypeService.updateVehicleType(typeId, createVehicleTypeRequest));
        verify(vehicleTypeRepository, times(1)).findById(typeId);
        verify(vehicleTypeRepository, never()).save(any());
    }

    // ==================== DELETE VEHICLE TYPE ====================

    @Test
    @DisplayName("Should delete vehicle type successfully")
    @SuppressWarnings("null")
    void deleteVehicleType_shouldDeleteType_whenTypeExists() {
        // Given
        UUID typeId = Objects.requireNonNull(testVehicleTypeId, "testVehicleTypeId cannot be null");
        VehicleType vehicleType = Objects.requireNonNull(testVehicleType, "testVehicleType cannot be null");
        when(vehicleTypeRepository.findById(typeId)).thenReturn(Optional.of(vehicleType));
        doNothing().when(vehicleTypeRepository).delete(vehicleType);

        // When
        assertDoesNotThrow(() -> vehicleTypeService.deleteVehicleType(typeId));

        // Then
        verify(vehicleTypeRepository, times(1)).findById(typeId);
        verify(vehicleTypeRepository, times(1)).delete(vehicleType);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent vehicle type")
    void deleteVehicleType_shouldThrowException_whenTypeNotFound() {
        // Given
        UUID typeId = Objects.requireNonNull(testVehicleTypeId, "testVehicleTypeId cannot be null");
        when(vehicleTypeRepository.findById(typeId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> vehicleTypeService.deleteVehicleType(typeId));
        verify(vehicleTypeRepository, times(1)).findById(typeId);
        verify(vehicleTypeRepository, never()).delete(any());
    }
}
