# API Contracts Documentation

## Table of Contents
1. [What are API Contracts?](#what-are-api-contracts)
2. [Why Use API Contracts?](#why-use-api-contracts)
3. [Implementation Approaches](#implementation-approaches)
4. [User Service API Contracts](#user-service-api-contracts)
5. [Professional Do's and Don'ts](#professional-dos-and-donts)
6. [Contract Testing](#contract-testing)

---

## What are API Contracts?

An **API Contract** is a formal agreement that defines:

- **Request Format**: What data the client must send (structure, types, validations)
- **Response Format**: What data the server will return (structure, types, status codes)
- **Behavior**: How the API behaves under different conditions (success, failures, edge cases)
- **Versioning**: How the API evolves over time without breaking existing clients

Think of it as a "promise" between the service provider (backend) and the service consumer (frontend, other microservices).

### Key Components:
1. **Endpoints**: URLs and HTTP methods
2. **Request Schemas**: DTOs, validation rules
3. **Response Schemas**: Return types, status codes
4. **Error Responses**: Standardized error format
5. **Authentication/Authorization**: Security requirements
6. **Documentation**: Human-readable descriptions

---

## Why Use API Contracts?

### 1. **Parallel Development**
- Frontend and backend teams can work independently
- Mock servers can be created from contracts
- Reduces integration issues

### 2. **Type Safety**
- Shared contracts between services (especially in microservices)
- Compile-time validation in typed languages
- Auto-generated client libraries

### 3. **Breaking Change Detection**
- Identify when changes break existing clients
- Contract tests fail before deployment
- Enforces backward compatibility

### 4. **Documentation**
- Single source of truth
- Always up-to-date (generated from code)
- Interactive documentation (Swagger/OpenAPI)

### 5. **Testing**
- Contract testing validates both provider and consumer
- Prevents integration failures
- Reduces end-to-end test dependency

---

## Implementation Approaches

### Approach 1: **Code-First (What We're Using)**
✅ Current implementation in User Service

**Tools:**
- Spring Boot annotations (`@RestController`, `@RequestMapping`)
- Validation annotations (`@Valid`, `@NotBlank`, `@Email`)
- OpenAPI/Swagger annotations (`@Operation`, `@ApiResponse`)
- SpringDoc OpenAPI (auto-generates OpenAPI spec from code)

**Pros:**
- Natural for Java developers
- Annotations close to code
- Auto-generated documentation
- Less ceremony

**Cons:**
- Contract is derived from implementation
- Can accidentally break contracts
- Documentation might lag behind code

---

### Approach 2: **Contract-First (OpenAPI/Swagger)**
Define contract in YAML/JSON, generate code

**Tools:**
- OpenAPI specification files
- OpenAPI Generator (generates DTOs and interfaces)
- Swagger Editor

**Pros:**
- Contract is explicit
- Great for cross-team coordination
- Can generate clients for multiple languages
- Contract changes are visible in VCS

**Cons:**
- More upfront work
- Requires code generation step
- Can feel disconnected from implementation

---

### Approach 3: **Consumer-Driven Contracts (CDC)**
Consumers define what they need, providers validate

**Tools:**
- Pact (JVM, .NET, JavaScript)
- Spring Cloud Contract

**Pros:**
- Prevents over-engineering
- Catches breaking changes early
- Focuses on actual consumer needs

**Cons:**
- Complex setup
- Requires consumer participation
- More tooling and infrastructure

---

## User Service API Contracts

### Contract Structure

Our User Service follows these contract principles:

#### 1. **Versioning**
```java
@RequestMapping(AppConstants.Api.V1) // "/api/v1"
```
- All endpoints are versioned
- Future breaking changes → new version (e.g., `/api/v2`)
- Old versions can be deprecated gracefully

#### 2. **Standardized Request DTOs**
```java
@Valid @RequestBody RegistrationRequest request
```
- Input validation at contract level
- Clear expectations for clients
- Fails fast with 400 Bad Request

#### 3. **Standardized Response DTOs**
```java
ResponseEntity<TokenResponse>
ResponseEntity<UserProfileResponse>
```
- Type-safe responses
- Consistent structure across endpoints
- No raw maps or generic objects

#### 4. **Error Handling**
```java
@ApiResponse(
    responseCode = "400",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
)
```
- Consistent error format
- Detailed error information
- HTTP status codes follow REST conventions

#### 5. **OpenAPI Documentation**
```java
@Operation(summary = "Register a new user", description = "...")
@ApiResponses({...})
@Tag(name = "Authentication")
```
- Self-documenting APIs
- Interactive Swagger UI
- Can generate client SDKs

---

## Professional Do's and Don'ts

### ✅ DO's

#### 1. **Version Your APIs**
```java
// Good
@RequestMapping("/api/v1/users")

// Bad
@RequestMapping("/users") // No version!
```

#### 2. **Use DTOs, Never Domain Entities**
```java
// Good
@GetMapping("/users/me")
public ResponseEntity<UserProfileResponse> getCurrentUser() { }

// Bad - Exposes internal structure
@GetMapping("/users/me")
public ResponseEntity<User> getCurrentUser() { }
```
**Why?** Domain entities contain implementation details (e.g., password hashes, audit fields, relationships) that shouldn't be exposed.

#### 3. **Validate All Inputs**
```java
// Good
@PostMapping("/auth/register")
public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest request) { }

// Bad - No validation
public ResponseEntity<?> register(@RequestBody RegistrationRequest request) { }
```

#### 4. **Use Consistent HTTP Status Codes**
```
- 200 OK: Successful GET/PUT
- 201 Created: Successful POST (resource created)
- 204 No Content: Successful DELETE
- 400 Bad Request: Validation failure
- 401 Unauthorized: Authentication required/failed
- 403 Forbidden: Authenticated but not allowed
- 404 Not Found: Resource doesn't exist
- 409 Conflict: Business rule violation (e.g., duplicate email)
- 500 Internal Server Error: Unexpected errors
```

#### 5. **Document Everything**
```java
@Operation(
    summary = "Short summary",
    description = "Detailed description of behavior, side effects, etc."
)
@ApiResponse(responseCode = "200", description = "Success case")
@ApiResponse(responseCode = "400", description = "What causes this error")
```

#### 6. **Use Semantic Field Names**
```java
// Good
private String firstName;
private String phoneNumber;
private LocalDateTime createdAt;

// Bad
private String fn;
private String phone; // phone what? number? type?
private String date; // which date?
```

#### 7. **Design for Evolution**
```java
// Good - can add optional fields without breaking clients
@Schema(description = "Optional field", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
private String newOptionalField;

// Bad - adding required fields breaks existing clients
```

#### 8. **Standardize Error Responses**
```java
@Data
@Builder
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private List<FieldError> fieldErrors; // For validation errors
}
```

#### 9. **Use Enums for Fixed Values**
```java
// Good
public enum UserRole {
    CUSTOMER, ADMIN, SUPPORT
}

// Bad - String field with no constraints
private String role; // Could be anything!
```

#### 10. **Implement HATEOAS (for mature APIs)**
```java
// Advanced: Include links to related resources
{
    "userId": "123",
    "email": "user@example.com",
    "_links": {
        "self": "/api/v1/users/123",
        "orders": "/api/v1/users/123/orders"
    }
}
```

---

### ❌ DON'Ts

#### 1. **Don't Return Internal Exceptions**
```java
// Bad - Exposes internal structure
{
    "error": "org.hibernate.exception.ConstraintViolationException: could not execute statement"
}

// Good - User-friendly message
{
    "status": 409,
    "error": "Conflict",
    "message": "A user with this email already exists"
}
```

#### 2. **Don't Change Response Structure**
```java
// Bad - Breaking change
// Before: { "userId": "123", "name": "John" }
// After:  { "id": "123", "fullName": "John Doe" } // ❌ Breaks clients!

// Good - Add new fields, keep old ones (deprecated)
// After:  { 
//   "userId": "123",      // @Deprecated
//   "id": "123",
//   "name": "John",       // @Deprecated
//   "fullName": "John Doe"
// }
```

#### 3. **Don't Use Generic Collections**
```java
// Bad
public ResponseEntity<List<Map<String, Object>>> getUsers() { }

// Good
public ResponseEntity<List<UserSummaryResponse>> getUsers() { }
```

#### 4. **Don't Skip Error Responses in Documentation**
```java
// Bad
@Operation(summary = "Login")
@ApiResponse(responseCode = "200", description = "Success")
// Missing error cases!

// Good
@Operation(summary = "Login")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Login successful"),
    @ApiResponse(responseCode = "401", description = "Invalid credentials"),
    @ApiResponse(responseCode = "400", description = "Invalid request format")
})
```

#### 5. **Don't Use Verbs in URLs**
```java
// Bad
@PostMapping("/api/v1/createUser")
@GetMapping("/api/v1/getUser/{id}")

// Good - HTTP verbs define the action
@PostMapping("/api/v1/users")
@GetMapping("/api/v1/users/{id}")
```

#### 6. **Don't Ignore Null Handling**
```java
// Bad - Inconsistent null behavior
// Sometimes null, sometimes missing field, sometimes empty string

// Good - Explicit null handling
@JsonInclude(JsonInclude.Include.NON_NULL) // Omit null fields
@Schema(nullable = true, description = "Optional field")
```

#### 7. **Don't Use Cryptic Field Names**
```java
// Bad
private String usr_eml_addr;
private int qty;
private String dt;

// Good
private String emailAddress;
private int quantity;
private LocalDateTime orderDate;
```

#### 8. **Don't Leak Sensitive Data**
```java
// Bad - Returning password in response
{
    "userId": "123",
    "password": "hashed_password_here" // ❌
}

// Good - Exclude sensitive fields
@JsonIgnore
private String password;
```

#### 9. **Don't Use Different Naming Conventions**
```java
// Bad - Mixed conventions
{
    "user_id": "123",        // snake_case
    "firstName": "John",     // camelCase
    "LastName": "Doe"        // PascalCase
}

// Good - Consistent camelCase (standard for JSON)
{
    "userId": "123",
    "firstName": "John",
    "lastName": "Doe"
}
```

#### 10. **Don't Make Everything Required**
```java
// Bad - Forces clients to send dummy data
@NotNull
private String middleName; // Not everyone has one!

// Good - Make optional fields optional
@Schema(nullable = true)
private String middleName;
```

---

## Contract Testing

### Unit Testing (Current)
✅ Already implemented in User Service

```java
@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Test
    void register_ShouldReturn201_WhenValidRequest() { }
    
    @Test
    void register_ShouldReturn400_WhenInvalidEmail() { }
}
```

### Integration Testing
```java
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {
    @Test
    void fullRegistrationFlow_ShouldWork() { }
}
```

### Contract Testing (Advanced)
For microservices communication:

```java
// Provider side (User Service)
@PactVerification(value = "order-service")
@PactFolder("pacts")
public class UserServiceContractTest { }

// Consumer side (Order Service)
@PactTestFor(providerName = "user-service")
public class OrderServiceContractTest { }
```

---

## OpenAPI Specification

### Accessing the Generated Contract

When User Service is running, the OpenAPI spec is available at:

```
# JSON format
http://localhost:8081/api/v1/api-docs

# YAML format
http://localhost:8081/api/v1/api-docs.yaml

# Swagger UI (interactive)
http://localhost:8081/swagger-ui.html
```

### Example Generated Contract

```yaml
openapi: 3.0.1
info:
  title: User Service API
  version: v1
paths:
  /api/v1/auth/register:
    post:
      tags:
        - Authentication
      summary: Register a new user
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/RegistrationRequest'
      responses:
        '201':
          description: User successfully registered
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/TokenResponse'
        '400':
          description: Invalid request data
        '409':
          description: User already exists
components:
  schemas:
    RegistrationRequest:
      required:
        - email
        - password
        - firstName
        - lastName
      type: object
      properties:
        email:
          type: string
          format: email
        password:
          type: string
          minLength: 12
          maxLength: 128
        firstName:
          type: string
          maxLength: 100
        # ... other fields
```

---

## Inter-Service Communication

### Scenario: Order Service calls User Service

#### Option 1: OpenAPI Generator (Recommended)

**Step 1**: Export User Service contract
```bash
curl http://localhost:8081/api/v1/api-docs > user-service-api.json
```

**Step 2**: Generate client in Order Service
```xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <configuration>
        <inputSpec>${project.basedir}/api-specs/user-service-api.json</inputSpec>
        <generatorName>java</generatorName>
        <apiPackage>de.bennycar.order.client.user.api</apiPackage>
        <modelPackage>de.bennycar.order.client.user.model</modelPackage>
    </configuration>
</plugin>
```

**Step 3**: Use type-safe client
```java
@Service
public class OrderService {
    private final UserServiceApi userServiceApi;
    
    public Order createOrder(CreateOrderRequest request) {
        // Type-safe call to User Service
        UserProfileResponse user = userServiceApi.getCurrentUser(token);
        // Contract ensures UserProfileResponse structure matches
    }
}
```

#### Option 2: Spring Cloud OpenFeign

```java
@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserServiceClient {
    
    @GetMapping("/api/v1/users/{id}")
    UserProfileResponse getUserProfile(@PathVariable UUID id);
    
    // Contract: If User Service changes response, compile error here
}
```

---

## Evolution Strategy

### Adding New Features (Non-Breaking)

✅ **Safe Changes:**
- Add optional query parameters
- Add optional request fields
- Add new response fields
- Add new endpoints

```java
// Before
@PostMapping("/users")
public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest req) { }

// After - Added optional field
@PostMapping("/users")
public ResponseEntity<UserResponse> create(@RequestBody CreateUserRequest req) { }

// CreateUserRequest now has: String middleName (optional)
// UserResponse now has: LocalDateTime lastLoginAt
```

### Breaking Changes (Requires New Version)

❌ **Breaking Changes:**
- Remove/rename fields
- Change field types
- Change validation rules (make stricter)
- Remove endpoints
- Change URL structure

**Solution: New API version**

```java
// Keep v1 for existing clients
@RestController
@RequestMapping("/api/v1/users")
public class UserControllerV1 { }

// New version with breaking changes
@RestController
@RequestMapping("/api/v2/users")
public class UserControllerV2 { }
```

### Deprecation Strategy

```java
@Deprecated
@Operation(
    summary = "Get user profile (DEPRECATED)",
    description = "Use GET /api/v2/users/me instead. Will be removed on 2024-12-31"
)
@GetMapping("/users/profile")
public ResponseEntity<UserProfileResponse> getProfile() { }
```

---

## Best Practices Summary

### For Request DTOs
- Use `@Valid` for validation
- Make fields `final` (immutability)
- Use builder pattern
- Include example values in `@Schema`
- Validate at contract boundary

### For Response DTOs
- Never return domain entities
- Use `@JsonInclude` for null handling
- Include timestamp fields
- Consider pagination for lists
- Version the response format

### For Controllers
- Single responsibility per endpoint
- Consistent naming
- Proper HTTP status codes
- Exception handling with `@ControllerAdvice`
- Comprehensive documentation

### For Security
- Never return sensitive data
- Validate and sanitize inputs
- Use HTTPS in production
- Implement rate limiting
- Log security events

---

## Resources

- [OpenAPI Specification](https://swagger.io/specification/)
- [Spring Boot REST Best Practices](https://spring.io/guides/tutorials/rest/)
- [Richardson Maturity Model](https://martinfowler.com/articles/richardsonMaturityModel.html)
- [API Design Patterns](https://microservice-api-patterns.org/)
- [Contract Testing with Pact](https://docs.pact.io/)

---

## Conclusion

API Contracts are essential for:
- **Reliability**: Preventing breaking changes
- **Scalability**: Enabling parallel development
- **Maintainability**: Clear documentation and expectations
- **Testing**: Automated validation of API behavior

Our User Service implements contracts through:
1. ✅ Versioned endpoints (`/api/v1`)
2. ✅ Validated DTOs (`@Valid`, Jakarta Validation)
3. ✅ OpenAPI documentation (`@Operation`, `@ApiResponse`)
4. ✅ Consistent error handling (`ErrorResponse`)
5. ✅ Type-safe responses (DTOs, not entities)

This foundation allows other services (Order Service, Vehicle Service) to safely integrate with User Service, knowing exactly what to expect.

