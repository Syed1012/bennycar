package de.bennycar.vehicle.controller;

import de.bennycar.api.vehicle.dto.request.CreateCustomizationCategoryRequest;
import de.bennycar.api.vehicle.dto.response.CustomizationCategoryResponse;
import de.bennycar.api.vehicle.dto.response.ErrorResponse;
import de.bennycar.vehicle.constants.AppConstants;
import de.bennycar.vehicle.service.CustomizationCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for customization category operations.
 */
@RestController
@RequestMapping(AppConstants.Api.CUSTOMIZATION_CATEGORIES)
@RequiredArgsConstructor
@Tag(name = "Customization Categories", description = "Customization category management endpoints")
public class CustomizationCategoryController {

    private final CustomizationCategoryService categoryService;

    @Operation(summary = "Get all customization categories", description = "Retrieves all active customization categories")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Categories retrieved successfully")})
    @GetMapping
    public ResponseEntity<List<CustomizationCategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllActiveCategories());
    }

    @Operation(summary = "Get category by ID", description = "Retrieves a specific customization category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomizationCategoryResponse> getCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @Operation(summary = "Create a new category", description = "Creates a new customization category (Admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Category already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<CustomizationCategoryResponse> createCategory(@RequestBody @Valid CreateCustomizationCategoryRequest request) {
        CustomizationCategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update a category", description = "Updates an existing customization category (Admin only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category updated successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public ResponseEntity<CustomizationCategoryResponse> updateCategory(
            @PathVariable UUID id,
            @RequestBody @Valid CreateCustomizationCategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @Operation(summary = "Delete a category", description = "Soft deletes a customization category (Admin only)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
