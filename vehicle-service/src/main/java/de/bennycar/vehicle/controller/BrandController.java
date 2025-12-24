package de.bennycar.vehicle.controller;

import de.bennycar.api.vehicle.dto.request.CreateBrandRequest;
import de.bennycar.api.vehicle.dto.response.BrandResponse;
import de.bennycar.api.vehicle.dto.response.ErrorResponse;
import de.bennycar.vehicle.constants.AppConstants;
import de.bennycar.vehicle.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for brand operations.
 */
@Slf4j
@RestController
@RequestMapping(AppConstants.Api.BRANDS)
@RequiredArgsConstructor
@Tag(name = "Brands", description = "Vehicle brand management endpoints")
public class BrandController {

    private final BrandService brandService;

    @Operation(summary = "Get all active brands", description = "Retrieves all active vehicle brands")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Brands retrieved successfully")})
    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        return ResponseEntity.ok(brandService.getAllActiveBrands());
    }

    @Operation(summary = "Get brands with pagination", description = "Retrieves brands with pagination support")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Brands retrieved successfully")})
    @GetMapping("/page")
    public ResponseEntity<Page<BrandResponse>> getBrandsPaginated(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(brandService.getBrands(pageable));
    }

    @Operation(summary = "Get brand by ID", description = "Retrieves a specific brand by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Brand retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Brand not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable UUID id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    @Operation(summary = "Search brands", description = "Searches brands by name")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Search completed successfully")})
    @GetMapping("/search")
    public ResponseEntity<List<BrandResponse>> searchBrands(@RequestParam String query) {
        return ResponseEntity.ok(brandService.searchBrands(query));
    }

    @Operation(summary = "Create a new brand", description = "Creates a new vehicle brand (Admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Brand created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Brand already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<BrandResponse> createBrand(@RequestBody @Valid CreateBrandRequest request) {
        BrandResponse response = brandService.createBrand(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update a brand", description = "Updates an existing brand (Admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Brand updated successfully"),
            @ApiResponse(responseCode = "404", description = "Brand not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Brand name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public ResponseEntity<BrandResponse> updateBrand(@PathVariable UUID id, @RequestBody @Valid CreateBrandRequest request) {
        return ResponseEntity.ok(brandService.updateBrand(id, request));
    }

    @Operation(summary = "Delete a brand", description = "Soft deletes a brand (Admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Brand deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Brand not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable UUID id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
