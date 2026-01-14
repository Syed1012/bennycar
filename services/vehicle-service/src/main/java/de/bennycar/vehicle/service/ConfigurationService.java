package de.bennycar.vehicle.service;

import de.bennycar.api.vehicle.dto.request.CreateConfigurationRequest;
import de.bennycar.api.vehicle.dto.request.UpdateConfigurationRequest;
import de.bennycar.api.vehicle.dto.response.ConfigurationResponse;
import de.bennycar.vehicle.constants.AppConstants;
import de.bennycar.vehicle.domain.CustomizationOption;
import de.bennycar.vehicle.domain.Vehicle;
import de.bennycar.vehicle.domain.VehicleConfiguration;
import de.bennycar.vehicle.exception.InvalidConfigurationException;
import de.bennycar.vehicle.exception.ResourceNotFoundException;
import de.bennycar.vehicle.exception.VehicleNotAvailableException;
import de.bennycar.vehicle.repository.CustomizationOptionRepository;
import de.bennycar.vehicle.repository.VehicleConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for vehicle configuration operations.
 * Handles user's customized vehicle builds.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConfigurationService {

    private final VehicleConfigurationRepository configurationRepository;
    private final CustomizationOptionRepository optionRepository;
    private final VehicleService vehicleService;

    /**
     * Retrieves all configurations for a user.
     */
    public Page<ConfigurationResponse> getUserConfigurations(UUID userId, Pageable pageable) {
        log.debug("Fetching configurations for user: {}", userId);
        return configurationRepository.findByUserId(userId, pageable)
                .map(this::toConfigurationResponse);
    }

    /**
     * Retrieves a specific configuration by ID for a user.
     */
    public ConfigurationResponse getConfigurationById(UUID configurationId, UUID userId) {
        log.debug("Fetching configuration {} for user {}", configurationId, userId);

        VehicleConfiguration configuration = configurationRepository.findByIdAndUserId(configurationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleConfiguration", configurationId.toString()));

        return toConfigurationResponse(configuration);
    }

    /**
     * Creates a new vehicle configuration.
     */
    @Transactional
    public ConfigurationResponse createConfiguration(CreateConfigurationRequest request, UUID userId) {
        log.info("Creating configuration for user {} with vehicle {}", userId, request.getVehicleId());

        Vehicle vehicle = vehicleService.findVehicleById(request.getVehicleId());

        if (!vehicle.isAvailable()) {
            throw new VehicleNotAvailableException(vehicle.getId().toString());
        }

        VehicleConfiguration configuration = VehicleConfiguration.builder()
                .name(request.getName())
                .notes(request.getNotes())
                .status(AppConstants.ConfigurationStatus.DRAFT)
                .userId(userId)
                .vehicle(vehicle)
                .build();

        // Set selected options
        if (request.getSelectedOptionIds() != null && !request.getSelectedOptionIds().isEmpty()) {
            Objects.requireNonNull(request.getSelectedOptionIds(), "Selected option IDs cannot be null");
            Set<CustomizationOption> selectedOptions = new HashSet<>(
                    optionRepository.findAllById(request.getSelectedOptionIds()));

            // Validate that selected options are available for this vehicle
            Set<UUID> availableOptionIds = vehicle.getAvailableCustomizations().stream()
                    .map(CustomizationOption::getId)
                    .collect(Collectors.toSet());

            for (CustomizationOption option : selectedOptions) {
                if (!availableOptionIds.contains(option.getId())) {
                    throw new InvalidConfigurationException(
                            "Option '" + option.getName() + "' is not available for this vehicle");
                }
            }

            configuration.setSelectedOptions(selectedOptions);
        }

        configuration.updateTotalPrice();

        VehicleConfiguration saved = configurationRepository.save(configuration);

        log.info("Created configuration with ID: {} for user: {}", saved.getId(), userId);
        return toConfigurationResponse(saved);
    }

    /**
     * Updates an existing configuration.
     */
    @Transactional
    public ConfigurationResponse updateConfiguration(UUID configurationId, UpdateConfigurationRequest request, UUID userId) {
        log.info("Updating configuration {} for user {}", configurationId, userId);

        VehicleConfiguration configuration = configurationRepository.findByIdAndUserId(configurationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleConfiguration", configurationId.toString()));

        // Can only update DRAFT or SAVED configurations
        if (AppConstants.ConfigurationStatus.ORDERED.equals(configuration.getStatus()) ||
            AppConstants.ConfigurationStatus.COMPLETED.equals(configuration.getStatus())) {
            throw new InvalidConfigurationException("Cannot modify a configuration that has been ordered");
        }

        if (request.getName() != null) {
            configuration.setName(request.getName());
        }
        if (request.getNotes() != null) {
            configuration.setNotes(request.getNotes());
        }
        if (request.getStatus() != null) {
            configuration.setStatus(request.getStatus());
        }

        // Update selected options if provided
        if (request.getSelectedOptionIds() != null) {
            Objects.requireNonNull(request.getSelectedOptionIds(), "Selected option IDs cannot be null");
            Set<CustomizationOption> selectedOptions = new HashSet<>(
                    optionRepository.findAllById(request.getSelectedOptionIds()));

            // Validate options are available for the vehicle
            Set<UUID> availableOptionIds = configuration.getVehicle().getAvailableCustomizations().stream()
                    .map(CustomizationOption::getId)
                    .collect(Collectors.toSet());

            for (CustomizationOption option : selectedOptions) {
                if (!availableOptionIds.contains(option.getId())) {
                    throw new InvalidConfigurationException(
                            "Option '" + option.getName() + "' is not available for this vehicle");
                }
            }

            configuration.setSelectedOptions(selectedOptions);
        }

        configuration.updateTotalPrice();

        VehicleConfiguration saved = configurationRepository.save(configuration);

        log.info("Updated configuration with ID: {}", saved.getId());
        return toConfigurationResponse(saved);
    }

    /**
     * Marks a configuration as ordered (purchase initiation).
     */
    @Transactional
    public ConfigurationResponse orderConfiguration(UUID configurationId, UUID userId) {
        log.info("Ordering configuration {} for user {}", configurationId, userId);

        VehicleConfiguration configuration = configurationRepository.findByIdAndUserId(configurationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleConfiguration", configurationId.toString()));

        if (!AppConstants.ConfigurationStatus.DRAFT.equals(configuration.getStatus()) &&
            !AppConstants.ConfigurationStatus.SAVED.equals(configuration.getStatus())) {
            throw new InvalidConfigurationException("Configuration cannot be ordered in its current state");
        }

        Vehicle vehicle = configuration.getVehicle();
        if (!vehicle.isAvailable()) {
            throw new VehicleNotAvailableException(vehicle.getId().toString());
        }

        configuration.setStatus(AppConstants.ConfigurationStatus.ORDERED);
        configuration.setOrderedAt(Instant.now());
        configuration.updateTotalPrice();

        VehicleConfiguration saved = configurationRepository.save(configuration);

        log.info("Ordered configuration with ID: {} for user: {}", saved.getId(), userId);
        return toConfigurationResponse(saved);
    }

    /**
     * Deletes a configuration.
     */
    @Transactional
    public void deleteConfiguration(UUID configurationId, UUID userId) {
        log.info("Deleting configuration {} for user {}", configurationId, userId);

        VehicleConfiguration configuration = configurationRepository.findByIdAndUserId(configurationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleConfiguration", configurationId.toString()));

        // Cannot delete ordered configurations
        if (AppConstants.ConfigurationStatus.ORDERED.equals(configuration.getStatus()) ||
            AppConstants.ConfigurationStatus.COMPLETED.equals(configuration.getStatus())) {
            throw new InvalidConfigurationException("Cannot delete a configuration that has been ordered");
        }

        configurationRepository.delete(configuration);
        log.info("Deleted configuration with ID: {}", configurationId);
    }

    /**
     * Gets the count of configurations for a user.
     */
    public long getConfigurationCount(UUID userId) {
        return configurationRepository.countByUserId(userId);
    }

    private ConfigurationResponse toConfigurationResponse(VehicleConfiguration configuration) {
        if (configuration == null) return null;
        
        ConfigurationResponse.VehicleSummary vehicleSummary = null;
        if (configuration.getVehicle() != null) {
            Vehicle vehicle = configuration.getVehicle();
            vehicleSummary = ConfigurationResponse.VehicleSummary.builder()
                    .id(vehicle.getId())
                    .brandName(vehicle.getBrand() != null ? vehicle.getBrand().getName() : null)
                    .brandLogoUrl(vehicle.getBrand() != null ? vehicle.getBrand().getLogoUrl() : null)
                    .model(vehicle.getModel())
                    .modelYear(vehicle.getModelYear())
                    .vehicleTypeName(vehicle.getVehicleType() != null ? vehicle.getVehicleType().getName() : null)
                    .basePrice(vehicle.getBasePrice())
                    .mainImageUrl(vehicle.getMainImageUrl())
                    .build();
        }
        
        List<ConfigurationResponse.SelectedOption> selectedOptions = null;
        if (configuration.getSelectedOptions() != null) {
            selectedOptions = configuration.getSelectedOptions().stream()
                    .map(opt -> ConfigurationResponse.SelectedOption.builder()
                            .id(opt.getId())
                            .categoryName(opt.getCategory() != null ? opt.getCategory().getName() : null)
                            .optionName(opt.getName())
                            .priceAdjustment(opt.getPriceAdjustment())
                            .imageUrl(opt.getImageUrl())
                            .colorCode(opt.getColorCode())
                            .build())
                    .collect(Collectors.toList());
        }
        
        // Calculate customization total from selected options
        BigDecimal customizationTotal = BigDecimal.ZERO;
        if (configuration.getSelectedOptions() != null) {
            customizationTotal = configuration.getSelectedOptions().stream()
                    .map(CustomizationOption::getPriceAdjustment)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        
        return ConfigurationResponse.builder()
                .id(configuration.getId())
                .userId(configuration.getUserId())
                .vehicle(vehicleSummary)
                .name(configuration.getName())
                .notes(configuration.getNotes())
                .status(configuration.getStatus())
                .selectedOptions(selectedOptions)
                .basePrice(configuration.getVehicle() != null ? configuration.getVehicle().getBasePrice() : null)
                .customizationTotal(customizationTotal)
                .totalPrice(configuration.getTotalPrice())
                .orderedAt(configuration.getOrderedAt())
                .createdAt(configuration.getCreatedAt())
                .updatedAt(configuration.getUpdatedAt())
                .build();
    }
}

