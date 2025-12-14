package de.bennycar.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Standardized error response DTO.
 * Used by GlobalExceptionHandler to return consistent error information to clients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    @Builder.Default
    private Instant timestamp = Instant.now();

    private String path;
    private String code;
    private String message;
    private List<String> details;
}

