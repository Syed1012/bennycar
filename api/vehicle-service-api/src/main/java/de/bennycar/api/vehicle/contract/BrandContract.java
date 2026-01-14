package de.bennycar.api.vehicle.contract;

import de.bennycar.api.vehicle.constants.ApiPaths;
import de.bennycar.api.vehicle.dto.response.BrandResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Brand API", description = "Vehicle brand operations")
@RequestMapping(ApiPaths.V1)
public interface BrandContract {

    @Operation(summary = "Get all brands", description = "Retrieve a list of all vehicle brands")
    @GetMapping(ApiPaths.BRANDS)
    ResponseEntity<List<BrandResponse>> getBrands();
}