# API Segregation Strategy

## Overview

This document outlines how to segregate APIs from service implementations in a microservices architecture, with practical examples from the BennyCar project.

---

## Why Segregate APIs?

### Without Segregation (Current Issue)
```
user-service/
  ├── controllers/      ← API definition mixed with implementation
  ├── services/         ← Business logic
  ├── repositories/     ← Data access
  └── dto/              ← Shared DTOs

Problem: Other services need to duplicate DTOs, no clear contract boundary
```

### With Segregation (Recommended)
```
user-service-api/          ← Shared API contracts (lightweight JAR)
  ├── dto/
  ├── exceptions/
  └── interfaces/          ← Optional: Feign interfaces

user-service/              ← Implementation (heavy JAR)
  ├── controllers/         ← Implements API contracts
  ├── services/
  ├── repositories/
  └── config/
  
order-service/
  └── pom.xml              ← depends on: user-service-api (NOT user-service!)
```

**Benefits:**
1. Other services only depend on API contracts (lightweight)
2. No accidental coupling to implementation
3. Clear separation of concerns
4. Easy to version APIs independently

---

## Implementation Approaches

### Approach 1: Separate API Module (Recommended for Production)

**Structure:**
```
bennycar/
├── user-service-api/               ← New module
│   ├── pom.xml                     ← Minimal dependencies (validation, jackson)
│   └── src/main/java/
│       └── de/bennycar/user/api/
│           ├── dto/                ← Request/Response DTOs
│           │   ├── RegistrationRequest.java
│           │   ├── TokenResponse.java
│           │   └── ErrorResponse.java
│           ├── exception/          ← API-level exceptions
│           │   ├── UserAlreadyExistsException.java
│           │   └── InvalidCredentialsException.java
│           └── client/             ← Feign client interfaces (optional)
│               └── UserServiceClient.java
│
├── user-service/                   ← Existing implementation
│   ├── pom.xml                     ← depends on: user-service-api
│   └── src/main/java/
│       └── de/bennycar/user/
│           ├── controller/         ← Uses DTOs from API module
│           ├── service/
│           ├── repository/
│           └── domain/             ← Internal entities
│
└── order-service/
    ├── pom.xml                     ← depends on: user-service-api
    └── src/main/java/
        └── de/bennycar/order/
            └── integration/
                └── UserServiceIntegration.java  ← Uses user-service-api
```

**Dependency Flow:**
```
user-service-api (DTOs, exceptions)
    ↑                ↑
    |                |
user-service    order-service
(implementation)  (consumer)
```

---

### Approach 2: OpenAPI Contract-First (Alternative)

**Structure:**
```
bennycar/
├── api-contracts/                  ← Centralized contracts
│   ├── user-service/
│   │   ├── openapi.yaml            ← Source of truth
│   │   └── pom.xml                 ← Generates Java DTOs
│   ├── vehicle-service/
│   │   └── openapi.yaml
│   └── order-service/
│       └── openapi.yaml
│
├── user-service/
│   ├── pom.xml                     ← depends on: generated DTOs
│   └── src/main/java/              ← Controllers implement contract
│
└── order-service/
    └── pom.xml                     ← depends on: generated user-service client
```

**Pros:**
- Contract is explicit and version-controlled
- Can generate clients in any language
- Great for cross-team coordination

**Cons:**
- More tooling complexity
- Requires code generation step
- Learning curve for OpenAPI spec

---

## Practical Implementation: User Service API Module

### Step 1: Create API Module

Create `user-service-api/pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>de.bennycar</groupId>
    <artifactId>user-service-api</artifactId>
    <version>1.0.0</version>
    <name>User Service API Contracts</name>
    <description>API contracts for User Service - DTOs and client interfaces</description>

    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>

    <dependencies>
        <!-- Minimal dependencies for API contracts -->
        
        <!-- Validation API -->
        <dependency>
            <groupId>jakarta.validation</groupId>
            <artifactId>jakarta.validation-api</artifactId>
            <version>3.0.2</version>
        </dependency>

        <!-- Jackson for JSON (de)serialization -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>2.16.0</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
            <version>2.16.0</version>
        </dependency>

        <!-- Lombok (optional, for cleaner code) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
            <scope>provided</scope>
        </dependency>

        <!-- OpenAPI annotations (for documentation) -->
        <dependency>
            <groupId>io.swagger.core.v3</groupId>
            <artifactId>swagger-annotations-jakarta</artifactId>
            <version>2.2.19</version>
        </dependency>

        <!-- Spring Cloud OpenFeign (optional, for client interfaces) -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
            <version>4.1.0</version>
            <optional>true</optional>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 2: Move DTOs to API Module

Create `user-service-api/src/main/java/de/bennycar/user/api/dto/`:

**RegistrationRequestDto.java:**
```java
package de.bennycar.user.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * API Contract: User registration request
 * Version: 1.0.0
 * 
 * This is a stable API contract. Breaking changes require a version bump.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User registration request")
public class RegistrationRequestDto {

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    @Schema(
        description = "User's email address",
        example = "john.doe@example.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 12, max = 128, message = "Password must be between 12 and 128 characters")
    @Schema(
        description = "User's password (minimum 12 characters)",
        example = "SecurePassword123!",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @JsonProperty("password")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    @Schema(description = "User's first name", example = "John")
    @JsonProperty("firstName")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    @Schema(description = "User's last name", example = "Doe")
    @JsonProperty("lastName")
    private String lastName;

    @Size(max = 20)
    @Schema(description = "Optional phone number", example = "+15555555555")
    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @Size(max = 512)
    @Schema(description = "Optional profile picture URL")
    @JsonProperty("profilePictureUrl")
    private String profilePictureUrl;

    @Size(max = 500)
    @Schema(description = "Optional mailing address")
    @JsonProperty("address")
    private String address;
}
```

**TokenResponseDto.java:**
```java
package de.bennycar.user.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication token response")
public class TokenResponseDto {

    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIs...")
    @JsonProperty("accessToken")
    private String accessToken;

    @Schema(description = "Refresh token", example = "abc123def456...")
    @JsonProperty("refreshToken")
    private String refreshToken;

    @Schema(description = "Token expiration time in seconds", example = "600")
    @JsonProperty("expiresIn")
    private long expiresIn;

    @Builder.Default
    @Schema(description = "Token type", example = "Bearer")
    @JsonProperty("tokenType")
    private String tokenType = "Bearer";
}
```

**UserProfileResponseDto.java:**
```java
package de.bennycar.user.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User profile information")
public class UserProfileResponseDto {

    @Schema(description = "Unique user identifier")
    @JsonProperty("userId")
    private UUID userId;

    @Schema(description = "User's email address")
    @JsonProperty("email")
    private String email;

    @Schema(description = "User's first name")
    @JsonProperty("firstName")
    private String firstName;

    @Schema(description = "User's last name")
    @JsonProperty("lastName")
    private String lastName;

    @Schema(description = "Phone number")
    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @Schema(description = "Profile picture URL")
    @JsonProperty("profilePictureUrl")
    private String profilePictureUrl;

    @Schema(description = "Mailing address")
    @JsonProperty("address")
    private String address;

    @Schema(description = "Account creation timestamp")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
```

**ErrorResponseDto.java:**
```java
package de.bennycar.user.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response")
public class ErrorResponseDto {

    @Schema(description = "HTTP status code", example = "400")
    @JsonProperty("status")
    private int status;

    @Schema(description = "Error type", example = "Bad Request")
    @JsonProperty("error")
    private String error;

    @Schema(description = "Error message", example = "Validation failed")
    @JsonProperty("message")
    private String message;

    @Schema(description = "Timestamp", example = "2024-01-15T10:30:00Z")
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @Schema(description = "Request path", example = "/api/v1/auth/register")
    @JsonProperty("path")
    private String path;

    @Schema(description = "Field-level validation errors")
    @JsonProperty("fieldErrors")
    private List<FieldErrorDto> fieldErrors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldErrorDto {
        @JsonProperty("field")
        private String field;

        @JsonProperty("rejectedValue")
        private Object rejectedValue;

        @JsonProperty("message")
        private String message;
    }
}
```

### Step 3: Create Feign Client Interface (Optional)

Create `user-service-api/src/main/java/de/bennycar/user/api/client/UserServiceClient.java`:

```java
package de.bennycar.user.api.client;

import de.bennycar.user.api.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Feign client interface for User Service
 * 
 * Usage in other services:
 * 1. Add dependency: user-service-api
 * 2. Enable Feign: @EnableFeignClients(basePackages = "de.bennycar.user.api.client")
 * 3. Inject: @Autowired UserServiceClient userServiceClient
 */
@FeignClient(
    name = "user-service",
    url = "${user-service.url:http://localhost:8081}",
    path = "/api/v1"
)
public interface UserServiceClient {

    /**
     * Register a new user
     */
    @PostMapping("/auth/register")
    ResponseEntity<TokenResponseDto> register(@RequestBody RegistrationRequestDto request);

    /**
     * User login
     */
    @PostMapping("/auth/login")
    ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto request);

    /**
     * Get current user profile (requires authentication)
     */
    @GetMapping("/users/me")
    ResponseEntity<UserProfileResponseDto> getCurrentUser(
        @RequestHeader("Authorization") String authorizationHeader
    );

    /**
     * Validate if a user exists by ID
     * (This would be a new endpoint for inter-service communication)
     */
    @GetMapping("/users/{userId}/exists")
    ResponseEntity<Boolean> userExists(@PathVariable("userId") String userId);
}
```

### Step 4: Update User Service to Use API Module

Update `user-service/pom.xml`:

```xml
<dependencies>
    <!-- Add dependency on API module -->
    <dependency>
        <groupId>de.bennycar</groupId>
        <artifactId>user-service-api</artifactId>
        <version>1.0.0</version>
    </dependency>

    <!-- Existing dependencies... -->
</dependencies>
```

Update controllers to use DTOs from API module:

```java
package de.bennycar.user.controller;

// Import from API module instead of local dto package
import de.bennycar.user.api.dto.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    @PostMapping("/auth/register")
    public ResponseEntity<TokenResponseDto> register(
        @RequestBody @Valid RegistrationRequestDto request
    ) {
        // Implementation unchanged, just using DTOs from API module
        // ...
    }
}
```

### Step 5: Use API Module in Order Service

Update `order-service/pom.xml`:

```xml
<dependencies>
    <!-- Only depend on API module, NOT the full user-service -->
    <dependency>
        <groupId>de.bennycar</groupId>
        <artifactId>user-service-api</artifactId>
        <version>1.0.0</version>
    </dependency>

    <!-- Spring Cloud OpenFeign for client -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
</dependencies>
```

Enable Feign in Order Service:

```java
package de.bennycar.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "de.bennycar.user.api.client")
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

Use the client:

```java
package de.bennycar.order.service;

import de.bennycar.user.api.client.UserServiceClient;
import de.bennycar.user.api.dto.UserProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserServiceClient userServiceClient;

    public Order createOrder(CreateOrderRequest request, String accessToken) {
        // Type-safe call to User Service
        UserProfileResponseDto user = userServiceClient
            .getCurrentUser("Bearer " + accessToken)
            .getBody();

        // If User Service API changes, this breaks at compile time!
        String userEmail = user.getEmail();
        UUID userId = user.getUserId();

        // Create order with user information
        Order order = Order.builder()
            .userId(userId)
            .userEmail(userEmail)
            .vehicleId(request.getVehicleId())
            .build();

        return orderRepository.save(order);
    }
}
```

---

## Migration Strategy

### Phase 1: Create API Module (Week 1)
1. Create `user-service-api` module
2. Copy DTOs to API module
3. Build and publish to local Maven repository

### Phase 2: Migrate User Service (Week 1-2)
1. Add dependency on `user-service-api`
2. Update imports in controllers
3. Delete local DTO copies
4. Test thoroughly

### Phase 3: Migrate Consuming Services (Week 2-3)
1. Add `user-service-api` dependency to Order Service
2. Remove any duplicated DTOs
3. Implement Feign client usage
4. Test integration

### Phase 4: Repeat for Other Services (Week 3-4)
1. Create `vehicle-service-api`
2. Create `order-service-api`
3. Follow same pattern

---

## API Versioning Strategy

### Semantic Versioning for API Module

```
user-service-api:1.0.0
                 │ │ │
                 │ │ └─ Patch: Bug fixes, documentation
                 │ └─── Minor: New optional fields, new endpoints
                 └───── Major: Breaking changes (removed fields, changed types)
```

### Examples:

**Patch (1.0.0 → 1.0.1):**
- Fix typo in documentation
- Update example values
- Fix Jackson serialization issue

**Minor (1.0.1 → 1.1.0):**
- Add optional field to RegistrationRequestDto
- Add new endpoint for password reset
- Add new DTO for admin operations

**Major (1.1.0 → 2.0.0):**
- Rename field `userId` to `id`
- Remove deprecated endpoint
- Change `createdAt` from String to LocalDateTime

---

## Testing Contracts

### Contract Test in User Service

```java
@SpringBootTest
class UserServiceContractTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registrationRequestDto_shouldDeserialize_withValidJson() throws Exception {
        String json = """
            {
                "email": "test@example.com",
                "password": "SecurePassword123!",
                "firstName": "John",
                "lastName": "Doe"
            }
            """;

        RegistrationRequestDto dto = objectMapper.readValue(json, RegistrationRequestDto.class);

        assertThat(dto.getEmail()).isEqualTo("test@example.com");
        assertThat(dto.getFirstName()).isEqualTo("John");
    }

    @Test
    void tokenResponseDto_shouldSerialize_correctly() throws Exception {
        TokenResponseDto dto = TokenResponseDto.builder()
            .accessToken("token123")
            .refreshToken("refresh456")
            .expiresIn(600)
            .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"accessToken\":\"token123\"");
        assertThat(json).contains("\"expiresIn\":600");
    }
}
```

### Consumer Contract Test (Order Service)

```java
@SpringBootTest
@AutoConfigureMockMvc
class OrderServiceContractTest {

    @Autowired
    private WireMockServer wireMockServer;

    @Test
    void userServiceClient_shouldCallCorrectEndpoint() {
        // Mock User Service response
        wireMockServer.stubFor(get("/api/v1/users/me")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "userId": "123e4567-e89b-12d3-a456-426614174000",
                        "email": "user@example.com",
                        "firstName": "John",
                        "lastName": "Doe"
                    }
                    """)));

        // Test that client can deserialize response
        UserProfileResponseDto profile = userServiceClient
            .getCurrentUser("Bearer token")
            .getBody();

        assertThat(profile.getEmail()).isEqualTo("user@example.com");
    }
}
```

---

## Best Practices Summary

### ✅ DO:
1. **Keep API modules lightweight** (minimal dependencies)
2. **Version API modules semantically** (major.minor.patch)
3. **Document breaking changes** in CHANGELOG.md
4. **Test contract serialization** (JSON ↔ DTO)
5. **Use DTOs, never domain entities** in API modules
6. **Add OpenAPI annotations** for documentation
7. **Include validation constraints** in API DTOs
8. **Use meaningful package names** (`.api.dto`, `.api.client`)

### ❌ DON'T:
1. **Don't include implementation** in API modules (no services, repositories)
2. **Don't add heavy dependencies** (no Spring Boot, no database drivers)
3. **Don't break backward compatibility** without major version bump
4. **Don't expose internal structures** (database entities)
5. **Don't couple API to implementation** details
6. **Don't mix versions** (use consistent API version across services)

---

## Conclusion

API segregation provides:
- ✅ **Clear boundaries** between services
- ✅ **Type safety** across service boundaries
- ✅ **Independent versioning** of contracts
- ✅ **Reduced coupling** (depend on contracts, not implementations)
- ✅ **Easier testing** (mock contracts, not implementations)
- ✅ **Better documentation** (contracts are self-documenting)

This approach is essential for microservices architecture and supports:
- Parallel development
- Independent deployments
- Contract-driven development
- Consumer-driven contracts

