package de.bennycar.user.dto;

import java.time.Instant;
import java.util.List;

public class ErrorResponse {
    private Instant timestamp = Instant.now();
    private String path;
    private String code;
    private String message;
    private List<String> details;

    public ErrorResponse(String path, String code, String message, List<String> details) {
        this.path = path;
        this.code = code;
        this.message = message;
        this.details = details;
    }

    public Instant getTimestamp() { return timestamp; }
    public String getPath() { return path; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public List<String> getDetails() { return details; }
}

