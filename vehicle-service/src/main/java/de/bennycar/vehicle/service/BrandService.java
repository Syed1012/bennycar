package de.bennycar.vehicle.service;

import de.bennycar.vehicle.domain.Brand;
import de.bennycar.vehicle.dto.BrandResponse;
import de.bennycar.vehicle.dto.CreateBrandRequest;
import de.bennycar.vehicle.exception.DuplicateResourceException;
import de.bennycar.vehicle.exception.ResourceNotFoundException;
import de.bennycar.vehicle.mapper.BrandMapper;
import de.bennycar.vehicle.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for brand-related operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    /**
     * Retrieves all active brands.
     */
    public List<BrandResponse> getAllActiveBrands() {
        log.debug("Fetching all active brands");
        return brandRepository.findAllActiveOrderByName().stream()
                .map(brandMapper::toBrandResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves brands with pagination.
     */
    public Page<BrandResponse> getBrands(Pageable pageable) {
        log.debug("Fetching brands with pagination: {}", pageable);
        return brandRepository.findByActiveTrue(pageable)
                .map(brandMapper::toBrandResponse);
    }

    /**
     * Retrieves a brand by ID.
     */
    public BrandResponse getBrandById(UUID id) {
        log.debug("Fetching brand by ID: {}", id);
        Brand brand = findBrandById(id);
        return brandMapper.toBrandResponse(brand);
    }

    /**
     * Creates a new brand.
     */
    @Transactional
    public BrandResponse createBrand(CreateBrandRequest request) {
        log.info("Creating new brand: {}", request.getName());

        if (brandRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Brand", request.getName());
        }

        Brand brand = brandMapper.toEntity(request);
        Brand saved = brandRepository.save(brand);

        log.info("Created brand with ID: {}", saved.getId());
        return brandMapper.toBrandResponse(saved);
    }

    /**
     * Updates an existing brand.
     */
    @Transactional
    public BrandResponse updateBrand(UUID id, CreateBrandRequest request) {
        log.info("Updating brand with ID: {}", id);

        Brand brand = findBrandById(id);

        // Check for duplicate name (excluding current brand)
        brandRepository.findByNameIgnoreCase(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Brand", request.getName());
                });

        brandMapper.updateEntity(request, brand);
        Brand saved = brandRepository.save(brand);

        log.info("Updated brand with ID: {}", saved.getId());
        return brandMapper.toBrandResponse(saved);
    }

    /**
     * Soft deletes a brand (sets active to false).
     */
    @Transactional
    public void deleteBrand(UUID id) {
        log.info("Deleting brand with ID: {}", id);
        Brand brand = findBrandById(id);
        brand.setActive(false);
        brandRepository.save(brand);
        log.info("Deleted brand with ID: {}", id);
    }

    /**
     * Searches brands by name.
     */
    public List<BrandResponse> searchBrands(String query) {
        log.debug("Searching brands with query: {}", query);
        return brandRepository.searchByName(query).stream()
                .map(brandMapper::toBrandResponse)
                .collect(Collectors.toList());
    }

    /**
     * Internal method to find a brand by ID or throw exception.
     */
    public Brand findBrandById(UUID id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", id.toString()));
    }
}
