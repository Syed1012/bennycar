package de.bennycar.api.vehicle.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleConfigurationResponse {
    private UUID id;
    private UUID userId;
    private UUID vehicleId;
    private String vehicleModel;
    private String configurationName;
    private String status;
    private BigDecimal totalPrice;
    private List<String> selectedOptions;
}