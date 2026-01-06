# User Service API Package

## Overview

The `user-service-api` package contains production-ready API contracts, Data Transfer Objects (DTOs), and constants for the User Service microservice. This package serves as a formal contract between the User Service implementation and all consumers (other services, frontend clients, third-party integrators).

**Version:** 1.0.0-SNAPSHOT  
**Package Name:** `de.bennycar.user-service-api`  
**Status:** Production Ready  

---

## Purpose

This package provides:

1. **API Contract Interface** (`UserServiceContract`) - Defines all required endpoints
2. **Request DTOs** - Validated request payloads
3. **Response DTOs** - Standardized response objects
4. **Constants** - Centralized message and endpoint definitions
5. **Documentation** - Complete API specification

By separating API contracts from implementation, we enable:

- **Loose Coupling:** Services don't depend on implementation details
- **Contract Testing:** Verify implementations match the contract
- **API Versioning:** Easy to manage multiple API versions
- **Code Reuse:** Other services can import and depend on DTOs
- **Consistency:** Standardized request/response formats across all services

---

## Package Structure

```
user-service-api/
├── pom.xml                                  # Maven configuration
├── README.md                                # This file
├── USER_SERVICE_API_DOCUMENTATION.md       # Comprehensive API documentation
└── src/
    └── main/
        └── java/
            └── de/bennycar/api/user/
                ├── constants/
                │   └── UserApiConstants.java       # API constants
                ├── contract/
                │   └── UserServiceContract.java    # API contract interface
                ├── dto/
                │   ├── request/
                │   │   ├── RegisterUserRequest.java
                │   │   ├── LoginRequest.java
                │   │   ├── RefreshTokenRequest.java
                │   │   ├── ChangePasswordRequest.java
                │   │   └── UpdateUserProfileRequest.java
                │   └── response/
                │       ├── TokenResponse.java
                │       ├── UserProfileResponse.java
                │       ├── ErrorResponse.java
                │       └── SuccessResponse.java
```

---

## Key Components

### 1. UserApiConstants

Centralized constants for the API:

```java
public class UserApiConstants {
    public static final class Api {
        public static final String V1 = "/api/v1";
    }
    
    public static final class Endpoints {
        public static final String REGISTER = "/auth/register";
        public static final String LOGIN = "/auth/login";
        // ... more endpoints
    }
    
    public static final class ValidationMessages {
        public static final String EMAIL_REQUIRED = "Email is required";
        // ... more messages
    }
}
```

**Benefits:**
- Single source of truth for messages
- Easy to maintain and update
- Prevents hardcoded strings

### 2. UserServiceContract

Interface defining all API operations:

```java
public interface UserServiceContract {
    ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterUserRequest request);
    ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request);
    ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request);
    // ... more operations
}
```

**Benefits:**
- Clear contract specification
- Type-safe endpoint definitions
- Built-in Swagger/OpenAPI documentation
- Can be tested independently

### 3. Request DTOs

All request objects with validation annotations:

```java
@Data
@Valid
public class RegisterUserRequest {
    @Email(message = UserApiConstants.ValidationMessages.EMAIL_INVALID)
    @NotBlank(message = UserApiConstants.ValidationMessages.EMAIL_REQUIRED)
    private String email;
    
    @NotBlank
    @Size(min = 12, max = 128)
    private String password;
    // ... more fields
}
```

**Features:**
- Jakarta Validation annotations
- Swagger documentation
- Serializable for inter-service communication
- Builder pattern for easy construction

### 4. Response DTOs

Standardized response objects:

```java
@Data
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    // ... more fields
}

@Data
public class UserProfileResponse {
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
    // ... more fields
}

@Data
public class ErrorResponse {
    private Integer status;
    private String code;
    private String message;
    private String detail;
    private LocalDateTime timestamp;
    private String path;
    private Map<String, String> fieldErrors;
}
```

**Benefits:**
- Consistent error handling
- Rich metadata in responses
- Easy serialization/deserialization
- Clear field documentation

---

## How to Use This Package

### 1. As a Dependency

Add to your service's `pom.xml`:

```xml
<dependency>
    <groupId>de.bennycar</groupId>
    <artifactId>user-service-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. Implementing the Contract

In your service controller:

```java
@RestController
@RequestMapping(UserApiConstants.Api.V1)
public class AuthController implements UserServiceContract {
    
    @Override
    @PostMapping(UserApiConstants.Endpoints.REGISTER)
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        // Implementation
        return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponse);
    }
    
    // ... implement other endpoints
}
```

### 3. Using Request/Response Objects

```java
// Request validation is automatic via Spring
@PostMapping("/auth/login")
public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
    String email = request.getEmail();
    String password = request.getPassword();
    // Process login
}

// Building responses
TokenResponse response = TokenResponse.builder()
    .accessToken(token)
    .refreshToken(refreshToken)
    .tokenType("Bearer")
    .expiresIn(600L)
    .build();
```

### 4. Inter-Service Communication

When calling User Service from another service:

```java
// Using RestTemplate or Feign
RegisterUserRequest request = RegisterUserRequest.builder()
    .email("user@example.com")
    .password("SecurePass@123")
    .firstName("John")
    .lastName("Doe")
    .build();

ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
    "http://user-service/api/v1/auth/register",
    request,
    TokenResponse.class
);
```

---

## API Endpoints Summary

### Authentication Endpoints

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/v1/auth/register` | Register new user |
| POST | `/api/v1/auth/login` | Authenticate user |
| POST | `/api/v1/auth/refresh` | Refresh access token |
| GET | `/api/v1/auth/validate` | Validate token |
| POST | `/api/v1/auth/logout` | Logout user |

### User Profile Endpoints

| Method | Endpoint | Purpose | Auth Required |
|--------|----------|---------|---|
| GET | `/api/v1/users/me` | Get current user profile | Yes |
| GET | `/api/v1/users/{userId}` | Get user profile by ID | Yes |
| PUT | `/api/v1/users/me` | Update user profile | Yes |
| POST | `/api/v1/users/me/password` | Change password | Yes |
| DELETE | `/api/v1/users/me` | Delete account | Yes |

---

## Validation Rules

### Password Requirements

- Length: 12-128 characters
- Must include: uppercase, lowercase, number, special character
- Cannot reuse previous password
- Must be different from username/email

### Email Validation

- Must be valid email format
- Must be unique across system
- Case-insensitive uniqueness check

### Name Fields

- First/Last name: Required, max 100 characters
- Phone: Optional, max 20 characters
- Address: Optional, max 500 characters

---

## Error Handling

### Error Response Format

```json
{
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Email must be valid",
  "detail": "The provided email address format is invalid",
  "timestamp": "2025-12-23T10:30:00",
  "path": "/api/v1/auth/register",
  "fieldErrors": {
    "email": "Email must be valid"
  }
}
```

### Common Error Codes

| Code | HTTP Status | Meaning |
|------|------------|---------|
| VALIDATION_ERROR | 400 | Input validation failed |
| UNAUTHORIZED | 401 | Authentication required |
| FORBIDDEN | 403 | Access denied |
| NOT_FOUND | 404 | Resource not found |
| CONFLICT | 409 | Resource conflict |
| INTERNAL_ERROR | 500 | Server error |

---

## Security

### Token-Based Authentication

- **Access Token TTL:** 600 seconds (10 minutes)
- **Refresh Token TTL:** 7 days
- **Algorithm:** HMAC-SHA256
- **Format:** JWT (JSON Web Token)

### Password Security

- **Hashing:** bcrypt with salt rounds 10+
- **Encryption:** Use HTTPS in production
- **Storage:** Never log or store plain text passwords

### Authorization

- Users can access only their own data
- Admin roles can access any user data
- Token verification on every request

---

## Integration Example

### Complete Flow

```java
// 1. Register new user
RegisterUserRequest registerRequest = RegisterUserRequest.builder()
    .email("john.doe@example.com")
    .password("SecurePass@123")
    .firstName("John")
    .lastName("Doe")
    .build();

ResponseEntity<TokenResponse> registerResponse = authController.register(registerRequest);
TokenResponse tokens = registerResponse.getBody();

// 2. Use access token to call protected endpoints
String accessToken = tokens.getAccessToken();
// Add to Authorization header: "Bearer " + accessToken

// 3. Get user profile
ResponseEntity<UserProfileResponse> profileResponse = authController.getProfile();

// 4. Update profile
UpdateUserProfileRequest updateRequest = UpdateUserProfileRequest.builder()
    .firstName("Jonathan")
    .phoneNumber("+1-555-666-7777")
    .build();

ResponseEntity<UserProfileResponse> updateResponse = authController.updateProfile(updateRequest);

// 5. Refresh token when access token expires
RefreshTokenRequest refreshRequest = RefreshTokenRequest.builder()
    .refreshToken(tokens.getRefreshToken())
    .build();

ResponseEntity<TokenResponse> newTokens = authController.refreshToken(refreshRequest);
```

---

## Testing

### Unit Testing DTOs

```java
@Test
void testRegisterRequestValidation() {
    RegisterUserRequest request = RegisterUserRequest.builder()
        .email("invalid-email")  // Invalid
        .password("short")        // Too short
        .firstName("John")
        .lastName("Doe")
        .build();
    
    Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);
    assertFalse(violations.isEmpty());
}
```

### Integration Testing

```java
@Test
void testRegisterEndpoint() {
    RegisterUserRequest request = RegisterUserRequest.builder()
        .email("test@example.com")
        .password("SecurePass@123")
        .firstName("Test")
        .lastName("User")
        .build();
    
    ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
        "/api/v1/auth/register",
        request,
        TokenResponse.class
    );
    
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody().getAccessToken());
}
```

---

## Best Practices

### DO's ✅

1. Always validate requests using the DTO validators
2. Use constants from `UserApiConstants` (never hardcode strings)
3. Include detailed error messages in responses
4. Log all authentication events
5. Use transactions for multi-step operations
6. Implement proper exception handling
7. Document any custom behavior
8. Follow RESTful conventions
9. Version your API changes
10. Test all endpoint combinations

### DON'Ts ❌

1. Don't modify the contract interface
2. Don't change DTO structures without versioning
3. Don't return sensitive information in responses
4. Don't log passwords or tokens
5. Don't skip input validation
6. Don't hardcode configuration values
7. Don't ignore error scenarios
8. Don't expose internal implementation details
9. Don't violate HTTP method semantics
10. Don't modify response status codes

---

## Development Guidelines

### Adding New Endpoints

1. **Add constant to `UserApiConstants`**
   ```java
   public static final class Endpoints {
       public static final String NEW_ENDPOINT = "/path";
   }
   ```

2. **Create Request/Response DTOs**
   ```java
   @Data
   public class NewRequest {
       @NotBlank
       private String field;
   }
   ```

3. **Add to Contract**
   ```java
   @PostMapping(Endpoints.NEW_ENDPOINT)
   ResponseEntity<?> newEndpoint(@Valid @RequestBody NewRequest request);
   ```

4. **Implement in Controller**
   ```java
   @Override
   public ResponseEntity<?> newEndpoint(@Valid @RequestBody NewRequest request) {
       // Implementation
   }
   ```

5. **Update Documentation**
   - Add endpoint to API_DOCUMENTATION.md
   - Include request/response examples
   - Document business logic

### Versioning Strategy

When breaking changes are needed:

1. Create new API version (v2)
2. Keep v1 for backward compatibility
3. Copy and modify contracts
4. Maintain separate DTOs if needed
5. Update documentation

---

## Performance Considerations

### Caching

- Cache user profiles with 5-minute TTL
- Cache role/permission data
- Invalidate cache on updates

### Database Optimization

- Index email field for fast lookups
- Index user IDs for profile queries
- Use connection pooling
- Implement query pagination

### API Optimization

- Use response compression
- Implement request/response caching
- Add rate limiting
- Monitor slow endpoints

---

## Documentation

### Available Documents

1. **USER_SERVICE_API_DOCUMENTATION.md** - Complete API specification
2. **API_CONTRACT_IMPLEMENTATION_GUIDE.md** - Step-by-step implementation
3. **API_SEGREGATION_STRATEGY.md** - Architecture and segregation patterns
4. **API_CONTRACTS.md** - All service contracts

---

## Contributing

When contributing to this package:

1. Follow the existing code style
2. Add unit tests for new code
3. Update documentation
4. Don't break existing contracts
5. Use semantic versioning
6. Submit PR with clear description

---

## Support

For questions or issues:

1. Check the [USER_SERVICE_API_DOCUMENTATION.md](Docs/USER_SERVICE_API_DOCUMENTATION.md)
2. Review existing implementation in user-service
3. Check integration tests for examples
4. Contact the development team

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0.0 | Dec 2025 | Initial release - Complete API contract |

---

## License

This package is part of the Bennycar project and follows the project's licensing terms.

---

**Last Updated:** December 2025  
**Status:** Production Ready  
**Owner:** Bennycar Development Team  
**Maintainer:** Development Team

