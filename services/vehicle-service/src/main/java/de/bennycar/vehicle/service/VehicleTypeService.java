package de.bennycar.vehicle.service;

import de.bennycar.api.vehicle.dto.request.CreateVehicleTypeRequest;
import de.bennycar.api.vehicle.dto.response.VehicleTypeResponse;
import de.bennycar.vehicle.domain.VehicleType;
import de.bennycar.vehicle.exception.DuplicateResourceException;
import de.bennycar.vehicle.exception.ResourceNotFoundException;
import de.bennycar.vehicle.repository.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for vehicle type operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleTypeService {

    private final VehicleTypeRepository vehicleTypeRepository;
    private final MapperService mapperService;

    /**
     * Retrieves all vehicle types.
     */
    public List<VehicleTypeResponse> getAllVehicleTypes() {
        log.debug("Fetching all vehicle types");
        return vehicleTypeRepository.findAllOrderByName().stream()
                .map(mapperService::toVehicleTypeResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a vehicle type by ID.
     */
    public VehicleTypeResponse getVehicleTypeById(UUID id) {
        log.debug("Fetching vehicle type by ID: {}", id);
        VehicleType vehicleType = findVehicleTypeById(id);
        return mapperService.toVehicleTypeResponse(vehicleType);
    }

    /**
     * Creates a new vehicle type.
     */
    @Transactional
    public VehicleTypeResponse createVehicleType(CreateVehicleTypeRequest request) {
        log.info("Creating new vehicle type: {}", request.getName());

        if (vehicleTypeRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("VehicleType", request.getName());
        }

        VehicleType vehicleType = mapperService.toVehicleTypeEntity(request);
        VehicleType saved = vehicleTypeRepository.save(vehicleType);

        log.info("Created vehicle type with ID: {}", saved.getId());
        return mapperService.toVehicleTypeResponse(saved);
    }

    /**
     * Updates an existing vehicle type.
     */
    @Transactional
    public VehicleTypeResponse updateVehicleType(UUID id, CreateVehicleTypeRequest request) {
        log.info("Updating vehicle type with ID: {}", id);

        VehicleType vehicleType = findVehicleTypeById(id);

        vehicleTypeRepository.findByNameIgnoreCase(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("VehicleType", request.getName());
                });

        mapperService.updateVehicleTypeEntity(request, vehicleType);
        VehicleType saved = vehicleTypeRepository.save(vehicleType);

        log.info("Updated vehicle type with ID: {}", saved.getId());
        return mapperService.toVehicleTypeResponse(saved);
    }

    /**
     * Deletes a vehicle type.
     */
    @Transactional
    public void deleteVehicleType(UUID id) {
        log.info("Deleting vehicle type with ID: {}", id);
        VehicleType vehicleType = findVehicleTypeById(id);
        vehicleTypeRepository.delete(vehicleType);
        log.info("Deleted vehicle type with ID: {}", id);
    }

    /**
     * Internal method to find a vehicle type by ID or throw exception.
     */
    public VehicleType findVehicleTypeById(UUID id) {
        return vehicleTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleType", id.toString()));
    }
}

