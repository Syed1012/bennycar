# API Contract Implementation Guide

## Overview

This guide demonstrates how to implement and use API contracts in the BennyCar microservices architecture using the User Service as an example.

---

## Architecture Approach

### Current: Code-First with OpenAPI

```
┌─────────────────────────────────────────────────────────┐
│                    User Service                         │
│                                                         │
│  ┌──────────────┐      ┌──────────────┐               │
│  │ Controllers  │──────│     DTOs     │               │
│  │  + OpenAPI   │      │  + Validation│               │
│  │  Annotations │      │  + Schema    │               │
│  └──────────────┘      └──────────────┘               │
│         │                      │                       │
│         └──────────┬───────────┘                       │
│                    │                                   │
│         ┌──────────▼──────────┐                        │
│         │   SpringDoc OpenAPI │                        │
│         │    Auto-generates   │                        │
│         └──────────┬──────────┘                        │
│                    │                                   │
│         ┌──────────▼──────────┐                        │
│         │ OpenAPI Spec (JSON) │                        │
│         │  /api/v1/api-docs   │                        │
│         └─────────────────────┘                        │
└─────────────────────────────────────────────────────────┘
                    │
                    │ Can be consumed by:
                    │
    ┌───────────────┼───────────────┐
    │               │               │
    ▼               ▼               ▼
┌─────────┐  ┌─────────────┐  ┌──────────┐
│Frontend │  │Order Service│  │  Postman │
│(Axios)  │  │  (Feign)    │  │ /Swagger │
└─────────┘  └─────────────┘  └──────────┘
```

---

## Step-by-Step Implementation

### Step 1: Define DTOs with Validation

#### Request DTO Example

```java
package de.bennycar.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contract: Registration request for new users
 * Version: 1.0
 * Breaking changes require version bump
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "User registration request",
    example = """
        {
            "email": "john.doe@example.com",
            "password": "SecurePassword123!",
            "firstName": "John",
            "lastName": "Doe"
        }
        """
)
public class RegistrationRequest {

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    @Schema(
        description = "User's email address (must be unique)",
        example = "john.doe@example.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 12, max = 128, message = "Password must be between 12 and 128 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,}$",
        message = "Password must contain uppercase, lowercase, digit, and special character"
    )
    @Schema(
        description = "User's password (min 12 chars, must contain uppercase, lowercase, digit, special char)",
        example = "SecurePassword123!",
        requiredMode = Schema.RequiredMode.REQUIRED,
        format = "password"
    )
    @JsonProperty("password")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Schema(
        description = "User's first name",
        example = "John",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("firstName")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Schema(
        description = "User's last name",
        example = "Doe",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("lastName")
    private String lastName;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    @Schema(
        description = "Optional phone number (E.164 format recommended)",
        example = "+15555555555",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @Size(max = 512, message = "Profile picture URL must not exceed 512 characters")
    @Schema(
        description = "Optional profile picture URL",
        example = "https://cdn.example.com/avatar.png",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("profilePictureUrl")
    private String profilePictureUrl;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Schema(
        description = "Optional mailing address",
        example = "221B Baker Street, London",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("address")
    private String address;
}
```

#### Response DTO Example

```java
package de.bennycar.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Contract: User profile response
 * Version: 1.0
 * 
 * RULES:
 * - Never expose password or sensitive auth data
 * - All timestamps in ISO-8601 format
 * - Null fields are omitted from JSON
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User profile information")
public class UserProfileResponse {

    @Schema(
        description = "Unique user identifier",
        example = "123e4567-e89b-12d3-a456-426614174000",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("userId")
    private UUID userId;

    @Schema(
        description = "User's email address",
        example = "john.doe@example.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("email")
    private String email;

    @Schema(
        description = "User's first name",
        example = "John",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("firstName")
    private String firstName;

    @Schema(
        description = "User's last name",
        example = "Doe",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("lastName")
    private String lastName;

    @Schema(
        description = "User's phone number",
        example = "+15555555555",
        nullable = true
    )
    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @Schema(
        description = "Profile picture URL",
        example = "https://cdn.example.com/avatar.png",
        nullable = true
    )
    @JsonProperty("profilePictureUrl")
    private String profilePictureUrl;

    @Schema(
        description = "User's address",
        example = "221B Baker Street, London",
        nullable = true
    )
    @JsonProperty("address")
    private String address;

    @Schema(
        description = "Account creation timestamp (ISO-8601)",
        example = "2024-01-15T10:30:00Z",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(
        description = "Last profile update timestamp (ISO-8601)",
        example = "2024-01-20T14:45:00Z",
        nullable = true
    )
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
```

#### Error Response (Standardized)

```java
package de.bennycar.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Contract: Standard error response for all endpoints
 * Version: 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response")
public class ErrorResponse {

    @Schema(
        description = "HTTP status code",
        example = "400",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("status")
    private int status;

    @Schema(
        description = "Error type",
        example = "Bad Request",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("error")
    private String error;

    @Schema(
        description = "Human-readable error message",
        example = "Validation failed for request",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("message")
    private String message;

    @Schema(
        description = "Timestamp when error occurred (ISO-8601)",
        example = "2024-01-15T10:30:00Z",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @Schema(
        description = "Request path that caused the error",
        example = "/api/v1/auth/register"
    )
    @JsonProperty("path")
    private String path;

    @Schema(
        description = "Field-level validation errors (only for 400 errors)",
        nullable = true
    )
    @JsonProperty("fieldErrors")
    private List<FieldError> fieldErrors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Field-level validation error")
    public static class FieldError {
        
        @Schema(description = "Field name", example = "email")
        @JsonProperty("field")
        private String field;

        @Schema(description = "Rejected value", example = "invalid-email")
        @JsonProperty("rejectedValue")
        private Object rejectedValue;

        @Schema(description = "Error message", example = "Email must be valid")
        @JsonProperty("message")
        private String message;
    }
}
```

---

### Step 2: Document Controllers with OpenAPI

```java
package de.bennycar.user.controller;

import de.bennycar.user.dto.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication API Contract v1
 * 
 * This controller defines the contract for:
 * - User registration
 * - User login
 * - Token refresh
 * - Logout
 * - Profile retrieval
 */
@RestController
@RequestMapping("/api/v1")
@Tag(
    name = "Authentication",
    description = "User authentication and authorization endpoints"
)
public class AuthController {

    @Operation(
        summary = "Register a new user",
        description = """
            Creates a new user account with the provided registration details.
            
            **Business Rules:**
            - Email must be unique (409 if duplicate)
            - Password must meet complexity requirements
            - All required fields must be provided
            
            **Returns:**
            - 201 Created: User registered successfully with JWT tokens
            - 400 Bad Request: Validation failure
            - 409 Conflict: Email already exists
            """,
        tags = {"Authentication"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "User successfully registered",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = TokenResponse.class),
                examples = @ExampleObject(
                    name = "Successful registration",
                    value = """
                        {
                            "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                            "refreshToken": "abc123def456...",
                            "expiresIn": 600,
                            "tokenType": "Bearer"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "Validation error",
                    value = """
                        {
                            "status": 400,
                            "error": "Bad Request",
                            "message": "Validation failed",
                            "timestamp": "2024-01-15T10:30:00Z",
                            "path": "/api/v1/auth/register",
                            "fieldErrors": [
                                {
                                    "field": "email",
                                    "rejectedValue": "invalid-email",
                                    "message": "Email must be valid"
                                }
                            ]
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "User already exists",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "Duplicate email",
                    value = """
                        {
                            "status": 409,
                            "error": "Conflict",
                            "message": "A user with this email already exists",
                            "timestamp": "2024-01-15T10:30:00Z",
                            "path": "/api/v1/auth/register"
                        }
                        """
                )
            )
        )
    })
    @PostMapping("/auth/register")
    public ResponseEntity<TokenResponse> register(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User registration details",
            required = true,
            content = @Content(
                schema = @Schema(implementation = RegistrationRequest.class),
                examples = @ExampleObject(
                    name = "Example registration",
                    value = """
                        {
                            "email": "john.doe@example.com",
                            "password": "SecurePassword123!",
                            "firstName": "John",
                            "lastName": "Doe",
                            "phoneNumber": "+15555555555"
                        }
                        """
                )
            )
        )
        @RequestBody @Valid RegistrationRequest request
    ) {
        // Implementation
        return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponse);
    }

    @Operation(
        summary = "Get current user profile",
        description = """
            Retrieves the profile information of the currently authenticated user.
            
            **Requirements:**
            - Valid JWT access token in Authorization header
            - Token must not be expired
            
            **Returns:**
            - 200 OK: Profile retrieved successfully
            - 401 Unauthorized: Invalid or missing token
            """,
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Profile retrieved successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UserProfileResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @GetMapping("/users/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(
        @Parameter(hidden = true) // Extracted from JWT, not a parameter
        Authentication authentication
    ) {
        // Implementation
        return ResponseEntity.ok(profile);
    }
}
```

---

### Step 3: Configure SpringDoc OpenAPI

```java
package de.bennycar.user.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI documentation configuration
 * Generates API contract documentation
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("User Service API")
                .version("1.0.0")
                .description("""
                    # User Service API Contract
                    
                    ## Overview
                    The User Service handles user authentication, registration, and profile management.
                    
                    ## Authentication
                    Most endpoints require JWT authentication. Include the token in the Authorization header:
                    ```
                    Authorization: Bearer <access_token>
                    ```
                    
                    ## Versioning
                    This API uses URL versioning. Current version: **v1**
                    
                    Breaking changes will result in a new version (v2, v3, etc.)
                    
                    ## Rate Limiting
                    - Authentication endpoints: 10 requests per minute per IP
                    - Other endpoints: 100 requests per minute per user
                    
                    ## Error Handling
                    All errors follow a standard format (see ErrorResponse schema)
                    
                    ## Support
                    For API support, contact: api-support@bennycar.de
                    """)
                .contact(new Contact()
                    .name("BennyCar API Team")
                    .email("api-support@bennycar.de")
                    .url("https://github.com/bennycar/user-service"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8081")
                    .description("Development server"),
                new Server()
                    .url("https://api.bennycar.de")
                    .description("Production server")
            ))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token obtained from /auth/login or /auth/register")));
    }
}
```

---

### Step 4: Extract Contract for Other Services

#### Create a Contract Export Script

```bash
#!/bin/bash
# scripts/export-api-contract.sh

# Export OpenAPI contract from running service
curl http://localhost:8081/api/v1/api-docs \
  -o user-service/api-contract/openapi.json

curl http://localhost:8081/api/v1/api-docs.yaml \
  -o user-service/api-contract/openapi.yaml

echo "✅ API contract exported successfully"
```

#### Generate Client for Order Service

**Add to Order Service pom.xml:**

```xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>7.0.1</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/../user-service/api-contract/openapi.json</inputSpec>
                <generatorName>java</generatorName>
                <library>resttemplate</library>
                <apiPackage>de.bennycar.order.client.user.api</apiPackage>
                <modelPackage>de.bennycar.order.client.user.model</modelPackage>
                <configOptions>
                    <dateLibrary>java8</dateLibrary>
                    <useJakartaEe>true</useJakartaEe>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

**Use in Order Service:**

```java
package de.bennycar.order.service;

import de.bennycar.order.client.user.api.AuthenticationApi;
import de.bennycar.order.client.user.model.UserProfileResponse;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    
    private final AuthenticationApi userServiceClient;
    
    public Order createOrder(CreateOrderRequest request, String accessToken) {
        // Call User Service using generated client
        // Contract ensures type safety!
        UserProfileResponse user = userServiceClient.getCurrentUser("Bearer " + accessToken);
        
        // If User Service changes its response format, this will fail at compile time
        String userEmail = user.getEmail();
        
        // Create order logic...
    }
}
```

---

## Contract Testing

### Test Request Validation

```java
package de.bennycar.user.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void register_shouldReject_whenEmailInvalid() throws Exception {
        String invalidRequest = """
            {
                "email": "not-an-email",
                "password": "SecurePassword123!",
                "firstName": "John",
                "lastName": "Doe"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType("application/json")
                .content(invalidRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("email"));
    }

    @Test
    void register_shouldReject_whenPasswordTooShort() throws Exception {
        String invalidRequest = """
            {
                "email": "john@example.com",
                "password": "Short1!",
                "firstName": "John",
                "lastName": "Doe"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType("application/json")
                .content(invalidRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors[0].field").value("password"));
    }

    @Test
    void register_shouldAccept_whenRequestValid() throws Exception {
        String validRequest = """
            {
                "email": "john@example.com",
                "password": "SecurePassword123!",
                "firstName": "John",
                "lastName": "Doe"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType("application/json")
                .content(validRequest))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists());
    }
}
```

---

## Consuming the Contract

### Frontend (React/Vue/Angular)

#### Option 1: Use OpenAPI Generator

```bash
# Generate TypeScript client
npx @openapitools/openapi-generator-cli generate \
  -i http://localhost:8081/api/v1/api-docs \
  -g typescript-axios \
  -o src/api/generated
```

```typescript
// TypeScript usage with type safety
import { AuthenticationApi, RegistrationRequest } from './api/generated';

const authApi = new AuthenticationApi();

const registerUser = async (userData: RegistrationRequest) => {
  try {
    const response = await authApi.register(userData);
    // response is typed as TokenResponse!
    localStorage.setItem('accessToken', response.data.accessToken);
  } catch (error) {
    // error is typed as ErrorResponse!
    console.error(error.response.data.message);
  }
};
```

#### Option 2: Manual Axios with Contract Knowledge

```typescript
// types/user-service.ts (manually written based on contract)
export interface RegistrationRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  profilePictureUrl?: string;
  address?: string;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  tokenType: string;
}

// api/user-service.ts
import axios from 'axios';
import { RegistrationRequest, TokenResponse } from '../types/user-service';

const API_BASE = 'http://localhost:8081/api/v1';

export const register = async (data: RegistrationRequest): Promise<TokenResponse> => {
  const response = await axios.post<TokenResponse>(`${API_BASE}/auth/register`, data);
  return response.data;
};
```

---

## Summary

### What We've Implemented

1. ✅ **Versioned API** (`/api/v1`)
2. ✅ **Validated Request DTOs** (Jakarta Validation)
3. ✅ **Type-Safe Response DTOs** (Lombok, Jackson)
4. ✅ **Standardized Error Responses** (ErrorResponse)
5. ✅ **Comprehensive OpenAPI Documentation** (SpringDoc)
6. ✅ **Auto-Generated Contract** (JSON/YAML)
7. ✅ **Contract Testing** (MockMvc tests)

### Benefits

- **For Developers**: Type safety, auto-completion, clear expectations
- **For Teams**: Parallel development, reduced integration issues
- **For Clients**: Self-documenting APIs, generated SDKs
- **For Testing**: Automated contract validation, breaking change detection

### Next Steps

1. Export contracts regularly (CI/CD)
2. Implement contract tests in consuming services
3. Set up contract versioning strategy
4. Create client SDKs for common languages
5. Monitor contract compatibility across deployments

