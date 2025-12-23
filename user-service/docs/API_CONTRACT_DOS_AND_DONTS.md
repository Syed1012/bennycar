# Professional Do's and Don'ts for API Contracts

## Quick Reference Guide

This is a condensed, actionable reference for professional API contract development.

---

## 📋 Structure & Organization

### ✅ DO:

```java
// Good: Clear, versioned package structure
package de.bennycar.user.api.v1.dto;
package de.bennycar.user.api.v1.client;
package de.bennycar.user.api.v1.exception;

// Good: Descriptive naming
public class UserRegistrationRequestDto { }
public class UserProfileResponseDto { }
```

### ❌ DON'T:

```java
// Bad: No versioning
package de.bennycar.user.dto;

// Bad: Ambiguous names
public class Request { }
public class Data { }
public class UserData { }  // Request? Response? Both?
```

---

## 🔐 Data Exposure

### ✅ DO:

```java
// Good: Separate DTOs from entities
@Entity
public class User {
    private String password;  // Internal only
    private boolean deleted;  // Internal only
}

@Data
public class UserProfileResponseDto {
    private UUID userId;
    private String email;
    // NO password, NO internal fields!
}
```

### ❌ DON'T:

```java
// Bad: Exposing entity directly
@GetMapping("/users/{id}")
public ResponseEntity<User> getUser(@PathVariable UUID id) {
    return ResponseEntity.ok(userRepository.findById(id));
    // ❌ Exposes password, audit fields, internal state!
}

// Bad: Using @JsonIgnore as a band-aid
@Entity
public class User {
    @JsonIgnore  // ❌ Still couples API to entity structure
    private String password;
}
```

---

## ✓ Validation

### ✅ DO:

```java
// Good: Comprehensive validation at API boundary
@Data
public class RegistrationRequestDto {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email too long")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 12, max = 128, message = "Password must be 12-128 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).*$",
        message = "Password must contain uppercase, lowercase, digit, and special character"
    )
    private String password;
}

// Good: Validate in controller
@PostMapping("/auth/register")
public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequestDto request) {
    // @Valid triggers validation automatically
}
```

### ❌ DON'T:

```java
// Bad: No validation
@Data
public class RegistrationRequestDto {
    private String email;  // Could be null, empty, or "not-an-email"
    private String password;  // Could be "123"
}

// Bad: Validation only in service layer
@PostMapping("/auth/register")
public ResponseEntity<?> register(@RequestBody RegistrationRequestDto request) {
    // ❌ Invalid data reaches service layer
    userService.register(request);
}
```

---

## 📝 Documentation

### ✅ DO:

```java
// Good: Comprehensive OpenAPI documentation
@Operation(
    summary = "Register a new user",
    description = """
        Creates a new user account.
        
        **Business Rules:**
        - Email must be unique (returns 409 if duplicate)
        - Password must meet complexity requirements
        
        **Rate Limit:** 10 requests per minute per IP
        """,
    tags = {"Authentication"}
)
@ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "User created successfully",
        content = @Content(schema = @Schema(implementation = TokenResponseDto.class))
    ),
    @ApiResponse(
        responseCode = "400",
        description = "Validation error",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
    ),
    @ApiResponse(
        responseCode = "409",
        description = "Email already exists",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
    )
})
@PostMapping("/auth/register")
public ResponseEntity<TokenResponseDto> register(@Valid @RequestBody RegistrationRequestDto request) { }
```

### ❌ DON'T:

```java
// Bad: No documentation
@PostMapping("/auth/register")
public ResponseEntity<?> register(@RequestBody RegistrationRequestDto request) { }

// Bad: Incomplete documentation
@Operation(summary = "Register")  // ❌ No description, no error cases
@PostMapping("/auth/register")
public ResponseEntity<?> register(@RequestBody RegistrationRequestDto request) { }
```

---

## 🔄 Versioning

### ✅ DO:

```java
// Good: URL versioning
@RestController
@RequestMapping("/api/v1/users")
public class UserControllerV1 { }

// Good: When breaking changes needed
@RestController
@RequestMapping("/api/v2/users")
public class UserControllerV2 { }

// Good: Deprecation warnings
@Deprecated(since = "1.5.0", forRemoval = true)
@Operation(
    summary = "Get user profile (DEPRECATED)",
    description = "Use GET /api/v2/users/me instead. Removed in v3.0.0"
)
@GetMapping("/profile")
public ResponseEntity<?> getProfile() { }
```

### ❌ DON'T:

```java
// Bad: No versioning
@RestController
@RequestMapping("/users")
public class UserController { }

// Bad: Breaking changes without version bump
// Before (v1):
{ "userId": "123", "name": "John Doe" }

// After (still v1):  ❌ BREAKING CHANGE!
{ "id": "123", "fullName": "John Doe" }
```

---

## 🎯 HTTP Status Codes

### ✅ DO:

```java
// Good: Correct status codes
@PostMapping("/users")
public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest req) {
    UserDto created = userService.create(req);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);  // 201
}

@DeleteMapping("/users/{id}")
public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
    userService.delete(id);
    return ResponseEntity.noContent().build();  // 204
}

@GetMapping("/users/{id}")
public ResponseEntity<UserDto> getUser(@PathVariable UUID id) {
    return userService.findById(id)
        .map(ResponseEntity::ok)  // 200
        .orElse(ResponseEntity.notFound().build());  // 404
}

// Good: Meaningful error responses
@ExceptionHandler(UserAlreadyExistsException.class)
public ResponseEntity<ErrorResponseDto> handleDuplicateUser(UserAlreadyExistsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)  // 409
        .body(ErrorResponseDto.builder()
            .status(409)
            .error("Conflict")
            .message("User with this email already exists")
            .timestamp(LocalDateTime.now())
            .build());
}
```

### ❌ DON'T:

```java
// Bad: Wrong status codes
@PostMapping("/users")
public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest req) {
    UserDto created = userService.create(req);
    return ResponseEntity.ok(created);  // ❌ Should be 201, not 200
}

// Bad: Generic 200 for everything
@DeleteMapping("/users/{id}")
public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
    userService.delete(id);
    return ResponseEntity.ok("Deleted");  // ❌ Should be 204 No Content
}

// Bad: Using 200 for errors
@ExceptionHandler(Exception.class)
public ResponseEntity<String> handleError(Exception ex) {
    return ResponseEntity.ok("Error: " + ex.getMessage());  // ❌ Should be 4xx or 5xx
}
```

---

## 🏗️ Request/Response Design

### ✅ DO:

```java
// Good: Specific request/response types
@Data
@Builder
public class CreateOrderRequestDto {
    @NotNull
    private UUID userId;
    
    @NotNull
    private UUID vehicleId;
    
    @NotNull
    @Future
    private LocalDateTime startDate;
    
    @NotNull
    @Future
    private LocalDateTime endDate;
}

@Data
@Builder
public class OrderResponseDto {
    private UUID orderId;
    private UUID userId;
    private UUID vehicleId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private LocalDateTime createdAt;
}

// Good: Consistent field naming (camelCase)
@JsonProperty("userId")
private UUID userId;

@JsonProperty("createdAt")
private LocalDateTime createdAt;
```

### ❌ DON'T:

```java
// Bad: Generic types
@PostMapping("/orders")
public ResponseEntity<Map<String, Object>> createOrder(
    @RequestBody Map<String, Object> request  // ❌ No type safety!
) { }

// Bad: Inconsistent naming
@Data
public class UserDto {
    private UUID user_id;      // snake_case
    private String firstName;  // camelCase
    private String LastName;   // PascalCase
    // ❌ Pick ONE convention!
}

// Bad: Returning different structures
// Sometimes: { "data": {...} }
// Other times: { "result": {...} }
// ❌ Be consistent!
```

---

## 🔍 Error Handling

### ✅ DO:

```java
// Good: Standardized error response
@Data
@Builder
public class ErrorResponseDto {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    private List<FieldErrorDto> fieldErrors;  // For validation errors
}

// Good: Detailed validation errors
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponseDto> handleValidation(
    MethodArgumentNotValidException ex,
    HttpServletRequest request
) {
    List<FieldErrorDto> fieldErrors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> FieldErrorDto.builder()
            .field(error.getField())
            .rejectedValue(error.getRejectedValue())
            .message(error.getDefaultMessage())
            .build())
        .toList();

    ErrorResponseDto response = ErrorResponseDto.builder()
        .status(400)
        .error("Bad Request")
        .message("Validation failed")
        .timestamp(LocalDateTime.now())
        .path(request.getRequestURI())
        .fieldErrors(fieldErrors)
        .build();

    return ResponseEntity.badRequest().body(response);
}

// Good: User-friendly messages
throw new UserNotFoundException("User not found with ID: " + userId);
```

### ❌ DON'T:

```java
// Bad: Exposing stack traces
@ExceptionHandler(Exception.class)
public ResponseEntity<String> handleError(Exception ex) {
    return ResponseEntity.status(500).body(ex.toString());
    // ❌ Exposes internal implementation details
}

// Bad: Generic error messages
throw new RuntimeException("Error");  // ❌ Not helpful!

// Bad: Different error formats
// Sometimes: { "error": "message" }
// Other times: { "message": "error" }
// ❌ Be consistent!

// Bad: Logging sensitive data
log.error("Login failed for password: {}", password);  // ❌ SECURITY RISK!
```

---

## 🔒 Security

### ✅ DO:

```java
// Good: Never return sensitive data
@Data
public class UserProfileResponseDto {
    private UUID userId;
    private String email;
    private String firstName;
    // NO password, NO tokens in responses!
}

// Good: Validate and sanitize inputs
@NotBlank
@Size(max = 100)
@Pattern(regexp = "^[a-zA-Z0-9\\s-]+$")  // Prevent injection
private String firstName;

// Good: Use HTTPS in production
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) {
    http.requiresChannel(channel -> 
        channel.anyRequest().requiresSecure()
    );
}

// Good: Rate limiting annotations
@RateLimiter(name = "authLimiter", fallbackMethod = "rateLimitFallback")
@PostMapping("/auth/login")
public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) { }
```

### ❌ DON'T:

```java
// Bad: Returning sensitive data
@Data
public class UserResponseDto {
    private String password;  // ❌ NEVER!
    private String passwordHash;  // ❌ Still sensitive!
    private String ssn;  // ❌ PII!
}

// Bad: Detailed error messages (information leak)
catch (SQLException ex) {
    return ResponseEntity.status(500)
        .body("SQL Error: " + ex.getMessage());
    // ❌ Exposes database structure
}

// Bad: No input validation
@PostMapping("/users/{id}/profile")
public ResponseEntity<?> updateProfile(
    @PathVariable String id,  // ❌ Could be "; DROP TABLE users;--"
    @RequestBody String bio   // ❌ Could be "<script>alert('XSS')</script>"
) { }
```

---

## 📦 Dependencies

### ✅ DO:

```xml
<!-- Good: Minimal dependencies in API modules -->
<dependencies>
    <!-- Only what's needed for contracts -->
    <dependency>
        <groupId>jakarta.validation</groupId>
        <artifactId>jakarta.validation-api</artifactId>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-annotations</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### ❌ DON'T:

```xml
<!-- Bad: Heavy dependencies in API modules -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
        <!-- ❌ API module doesn't need database! -->
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <!-- ❌ API module doesn't need database driver! -->
    </dependency>
</dependencies>
```

---

## 🧪 Testing

### ✅ DO:

```java
// Good: Test contract serialization
@Test
void userResponseDto_shouldSerializeCorrectly() throws Exception {
    UserResponseDto dto = UserResponseDto.builder()
        .userId(UUID.randomUUID())
        .email("test@example.com")
        .firstName("John")
        .build();

    String json = objectMapper.writeValueAsString(dto);

    assertThat(json).contains("\"userId\"");
    assertThat(json).contains("\"email\"");
    assertThat(json).doesNotContain("\"password\"");  // ❌ Should never be in JSON
}

// Good: Test validation
@Test
void registrationRequest_shouldFailValidation_whenEmailInvalid() {
    RegistrationRequestDto dto = RegistrationRequestDto.builder()
        .email("not-an-email")  // Invalid
        .password("SecurePassword123!")
        .firstName("John")
        .lastName("Doe")
        .build();

    Set<ConstraintViolation<RegistrationRequestDto>> violations = 
        validator.validate(dto);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .contains("Email must be valid");
}

// Good: Test API endpoints
@Test
void register_shouldReturn201_whenValidRequest() throws Exception {
    String validRequest = """
        {
            "email": "test@example.com",
            "password": "SecurePassword123!",
            "firstName": "John",
            "lastName": "Doe"
        }
        """;

    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(validRequest))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.accessToken").exists());
}
```

### ❌ DON'T:

```java
// Bad: Not testing edge cases
@Test
void register_shouldWork() {
    // ❌ Only tests happy path, no validation, errors, edge cases
    UserDto result = userService.register(validRequest);
    assertNotNull(result);
}

// Bad: Testing implementation details
@Test
void register_shouldCallRepositorySave() {
    // ❌ Tests HOW it's done, not WHAT the contract guarantees
    verify(userRepository).save(any());
}
```

---

## 🔄 Evolution Strategy

### ✅ DO:

```java
// Good: Add optional fields (non-breaking)
@Data
public class UserProfileResponseDto {
    private UUID userId;
    private String email;
    
    @Schema(description = "Optional field added in v1.1.0")
    private LocalDateTime lastLoginAt;  // ✅ Optional, won't break existing clients
}

// Good: Deprecate before removing
@Deprecated(since = "1.5.0", forRemoval = true)
@Schema(description = "DEPRECATED: Use 'userId' instead. Will be removed in v2.0.0")
private UUID id;

private UUID userId;  // New field

// Good: Version bump for breaking changes
// user-service-api:1.x.x → 2.0.0
```

### ❌ DON'T:

```java
// Bad: Breaking changes without version bump
// Before (v1.5.0):
@Data
public class UserDto {
    private UUID userId;
    private String name;
}

// After (v1.6.0):  ❌ BREAKING!
@Data
public class UserDto {
    private UUID id;  // ❌ Renamed field breaks clients
    private String fullName;  // ❌ Renamed field breaks clients
}

// Bad: Removing fields without deprecation period
// v1.5.0: field exists
// v1.6.0: field removed  ❌ No warning!
```

---

## 📚 Documentation Best Practices

### ✅ DO:

```java
/**
 * API Contract: User registration request
 * 
 * Version: 1.0.0
 * Last Updated: 2024-01-15
 * 
 * This contract is stable. Breaking changes will increment major version.
 * 
 * Example:
 * <pre>
 * {
 *   "email": "user@example.com",
 *   "password": "SecurePassword123!",
 *   "firstName": "John",
 *   "lastName": "Doe"
 * }
 * </pre>
 * 
 * @see TokenResponseDto
 * @see AuthController#register(RegistrationRequestDto)
 */
@Data
@Schema(description = "User registration request")
public class RegistrationRequestDto { }
```

### ❌ DON'T:

```java
// Bad: No documentation
@Data
public class RegistrationRequestDto { }

// Bad: Outdated documentation
/**
 * User request  ← Generic
 * Created: 2020  ← Old
 * TODO: Update this  ← Never updated
 */
@Data
public class RegistrationRequestDto { }
```

---

## 🎯 Summary Checklist

Before merging API changes:

- [ ] DTOs are separate from domain entities
- [ ] All fields have validation constraints
- [ ] OpenAPI documentation is complete
- [ ] All endpoints are versioned (`/api/v1`)
- [ ] HTTP status codes are correct
- [ ] Error responses are standardized
- [ ] Sensitive data is not exposed
- [ ] Breaking changes have version bump
- [ ] Deprecations have removal date
- [ ] Contract tests are written
- [ ] Examples are provided
- [ ] CHANGELOG is updated

---

## 📖 Additional Resources

- [REST API Design Best Practices](https://stackoverflow.blog/2020/03/02/best-practices-for-rest-api-design/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Jakarta Bean Validation](https://beanvalidation.org/)
- [Semantic Versioning](https://semver.org/)
- [OWASP API Security](https://owasp.org/www-project-api-security/)

