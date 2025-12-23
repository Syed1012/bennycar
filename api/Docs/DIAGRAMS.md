# API Architecture Visualization Guide

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        API Gateway / Load Balancer              │
└────────────────────────────────┬────────────────────────────────┘
                                 │
                    ┌────────────┴──────────────┐
                    │                           │
        ┌───────────▼───────────┐   ┌──────────▼──────────┐
        │   User Service        │   │ Vehicle Service     │
        │   (Port 8081)         │   │ (Port 8082)         │
        │                       │   │                     │
        │  ┌─────────────────┐  │   │  ┌───────────────┐  │
        │  │  AuthController │  │   │  │  CarController│  │
        │  └────────┬────────┘  │   │  └───────┬───────┘  │
        │           │            │   │          │           │
        │  Implements            │   │  Implements         │
        │  UserServiceContract   │   │  VehicleServiceAPI  │
        │           │            │   │          │           │
        │           ▼            │   │          ▼           │
        │  ┌─────────────────┐  │   │  ┌───────────────┐  │
        │  │  UserService    │  │   │  │  CarService   │  │
        │  │  AuthService    │  │   │  │               │  │
        │  └────────┬────────┘  │   │  └───────┬───────┘  │
        │           │            │   │          │           │
        │           ▼            │   │          ▼           │
        │  ┌─────────────────┐  │   │  ┌───────────────┐  │
        │  │   PostgreSQL    │  │   │  │   PostgreSQL  │  │
        │  │  (user_schema)  │  │   │  │ (vehicle_schema)  │
        │  └─────────────────┘  │   │  └───────────────┘  │
        └───────────────────────┘   └──────────────────────┘
                    │                           │
                    └───────────┬───────────────┘
                                │
                    ┌───────────▼──────────────┐
                    │   Order Service         │
                    │   (Port 8083)           │
                    │                         │
                    │  Uses:                  │
                    │  - user-service-api     │
                    │  - vehicle-service-api  │
                    └─────────────────────────┘
```

## Dependency Flow

```
┌──────────────────────────────────────────────────────────────────┐
│                        Dependencies                              │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Root POM (bennycar)                                             │
│  │                                                               │
│  ├─ /api (Parent for all API modules)                          │
│  │  │                                                            │
│  │  ├─ user-service-api (CREATED)                              │
│  │  │  ├─ UserApiConstants.java                                │
│  │  │  ├─ UserServiceContract.java                             │
│  │  │  ├─ DTOs (5 request, 4 response)                         │
│  │  │  └─ Documentation                                         │
│  │  │                                                            │
│  │  ├─ vehicle-service-api (TO CREATE)                         │
│  │  │  ├─ VehicleApiConstants.java                             │
│  │  │  ├─ VehicleServiceContract.java                          │
│  │  │  └─ DTOs                                                  │
│  │  │                                                            │
│  │  └─ order-service-api (TO CREATE)                           │
│  │     ├─ OrderApiConstants.java                               │
│  │     ├─ OrderServiceContract.java                            │
│  │     └─ DTOs                                                  │
│  │                                                               │
│  ├─ user-service (TO IMPLEMENT)                                │
│  │  └─ Depends on: user-service-api                            │
│  │     └─ Implements: UserServiceContract                      │
│  │                                                               │
│  ├─ vehicle-service (TO IMPLEMENT)                             │
│  │  └─ Depends on:                                              │
│  │     ├─ vehicle-service-api                                  │
│  │     └─ user-service-api (for inter-service calls)           │
│  │                                                               │
│  ├─ order-service (TO IMPLEMENT)                               │
│  │  └─ Depends on:                                              │
│  │     ├─ order-service-api                                    │
│  │     ├─ user-service-api                                     │
│  │     └─ vehicle-service-api                                  │
│  │                                                               │
│  └─ frontend                                                    │
│     └─ Calls: All service APIs                                  │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

## API Request Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                    1. User Registration Flow                    │
└─────────────────────────────────────────────────────────────────┘

Client Request
    │
    ├─ POST /api/v1/auth/register
    ├─ Content: RegisterUserRequest (JSON)
    │   {
    │     email, password, firstName, lastName,
    │     phoneNumber, profilePictureUrl, address
    │   }
    │
    ▼
AuthController.register()
    │
    ├─ Validates input (DTO validation)
    │
    ├─ Calls AuthService.register()
    │   └─ Calls UserService.createUser()
    │       ├─ Checks email uniqueness
    │       ├─ Hashes password (bcrypt)
    │       └─ Saves to database
    │
    ├─ Generates JWT tokens
    │   ├─ Access Token (10 min)
    │   └─ Refresh Token (7 days)
    │
    ├─ Stores refresh token in DB
    │
    └─ Returns TokenResponse (201 Created)
        {
          accessToken, refreshToken,
          tokenType, expiresIn,
          expiresAt, refreshExpiresAt
        }

┌─────────────────────────────────────────────────────────────────┐
│                    2. Protected Endpoint Flow                   │
└─────────────────────────────────────────────────────────────────┘

Client Request
    │
    ├─ GET /api/v1/users/me
    ├─ Header: Authorization: Bearer <access_token>
    │
    ▼
JWT Filter / Security Filter
    │
    ├─ Extracts token from header
    ├─ Validates signature
    ├─ Checks expiration
    └─ Sets user context
    │
    ▼
AuthController.getProfile()
    │
    ├─ Gets user from security context
    ├─ Calls UserService.getUserProfile()
    ├─ Returns UserProfileResponse (200 OK)
    │
    └─ Client receives profile data

┌─────────────────────────────────────────────────────────────────┐
│                    3. Token Refresh Flow                        │
└─────────────────────────────────────────────────────────────────┘

Client (Token about to expire)
    │
    ├─ POST /api/v1/auth/refresh
    ├─ Body: RefreshTokenRequest
    │   { refreshToken: "..." }
    │
    ▼
AuthController.refreshToken()
    │
    ├─ Validates refresh token
    ├─ Checks if revoked
    ├─ Extracts user from token
    ├─ Generates new access token
    └─ Returns TokenResponse
        { accessToken, refreshToken, ... }
    │
    ▼
Client stores new tokens
    └─ Continues API calls with new accessToken
```

## DTO Validation Flow

```
┌──────────────────────────────────────────────────────────┐
│              Request Validation Pipeline                 │
└──────────────────────────────────────────────────────────┘

Client JSON Request
    │
    ├─ POST /api/v1/auth/register
    └─ { email, password, firstName, lastName, ... }
       │
       ▼
Spring Deserialization
    │
    └─ Convert JSON to RegisterUserRequest object
       │
       ▼
Jakarta Validation Annotations
    │
    ├─ @Email: Validate email format
    ├─ @NotBlank: Ensure required fields
    ├─ @Size: Check length constraints
    ├─ @Pattern: Custom format validation
    └─ Custom validators
       │
       ├─ Email format: john@example.com
       ├─ Password: 12-128 chars with uppercase, lowercase, number, special char
       ├─ Names: 1-100 characters
       ├─ Phone: Max 20 characters
       └─ URLs: Max 512 characters
       │
       ▼
Validation Success?
    │
    ├─ YES → Proceed to endpoint
    │        ├─ Endpoint logic
    │        └─ Database operations
    │
    └─ NO → Spring generates ErrorResponse
            {
              status: 400,
              code: "VALIDATION_ERROR",
              message: "Validation failed",
              fieldErrors: {
                email: "Email must be valid",
                password: "Password must be..."
              }
            }
            │
            └─ Return to client (400 Bad Request)
```

## Database Schema Diagram

```
┌──────────────────────────────────────────────────────────┐
│                    PostgreSQL Database                   │
└──────────────────────────────────────────────────────────┘

┌─────────────────────────┐          ┌──────────────────────┐
│       users table       │          │  refresh_tokens table│
├─────────────────────────┤          ├──────────────────────┤
│ id (UUID) - PK          │          │ id (UUID) - PK       │
│ email (VARCHAR) - UQ    │◄─────────│ user_id (UUID) - FK  │
│ password_hash (VARCHAR) │          │ token_hash (VARCHAR) │
│ first_name (VARCHAR)    │          │ expires_at (TIMESTAMP)
│ last_name (VARCHAR)     │          │ revoked (BOOLEAN)    │
│ phone_number (VARCHAR)  │          │ created_at           │
│ profile_picture_url     │          │ created_by           │
│ address (VARCHAR)       │          └──────────────────────┘
│ status (ENUM)           │
│ email_verified (BOOL)   │
│ created_at (TIMESTAMP)  │
│ updated_at (TIMESTAMP)  │
│ created_by (VARCHAR)    │
│ updated_by (VARCHAR)    │
└─────────────────────────┘

Relationship:
One User ──────► Many Refresh Tokens
(1 to N)
```

## Authentication Token Structure

```
┌──────────────────────────────────────────────────────────┐
│              JWT Token Structure                         │
└──────────────────────────────────────────────────────────┘

Access Token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Decoded:
┌─────────────┐┌──────────────┐┌────────────────────┐
│   HEADER    ││    PAYLOAD   ││    SIGNATURE       │
├─────────────┼┼──────────────┼┼────────────────────┤
│ {           ││ {            ││ HMACSHA256(        │
│   "alg":    ││   "sub":     ││   base64Header +   │
│   "HS256",  ││   "userId",  ││   base64Payload,   │
│   "typ":    ││   "email":   ││   secret)          │
│   "JWT"     ││   "roles":   ││                    │
│ }           ││   "iat":     ││                    │
│             ││   "exp":     ││                    │
│             ││   "iss":     ││                    │
│             ││   "aud":     ││                    │
│             ││ }            ││                    │
└─────────────┘└──────────────┘└────────────────────┘

Validity Check:
    Signature valid? → Token authentic
    Expiration time passed? → Token expired
    Revoked? → Token blacklisted
```

## Error Handling Flow

```
┌──────────────────────────────────────────────────────────┐
│              Error Response Generation                   │
└──────────────────────────────────────────────────────────┘

Exception Occurs
    │
    ├─ Validation Error (400)
    │  └─ ConstraintViolationException
    │     └─ ErrorResponse with fieldErrors
    │
    ├─ Authentication Error (401)
    │  └─ InvalidTokenException
    │     └─ ErrorResponse with INVALID_TOKEN code
    │
    ├─ Authorization Error (403)
    │  └─ AccessDeniedException
    │     └─ ErrorResponse with FORBIDDEN code
    │
    ├─ Business Logic Error (409)
    │  └─ EmailAlreadyExistsException
    │     └─ ErrorResponse with CONFLICT code
    │
    ├─ Not Found (404)
    │  └─ UserNotFoundException
    │     └─ ErrorResponse with NOT_FOUND code
    │
    └─ Server Error (500)
       └─ Unexpected exception
          └─ ErrorResponse with INTERNAL_ERROR code

ErrorResponse Structure:
{
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Validation failed",
  "detail": "One or more fields are invalid",
  "timestamp": "2025-12-23T10:30:00",
  "path": "/api/v1/auth/register",
  "fieldErrors": {
    "email": "Email must be valid"
  }
}
```

## Service Communication Pattern

```
┌──────────────────────────────────────────────────────────┐
│         Inter-Service Communication Pattern              │
└──────────────────────────────────────────────────────────┘

Vehicle Service needs to call User Service:

1. Add Dependency
   └─ <dependency>
        <groupId>de.bennycar</groupId>
        <artifactId>user-service-api</artifactId>
      </dependency>

2. Create Request
   └─ RegisterUserRequest request = 
        RegisterUserRequest.builder()
          .email("user@example.com")
          .password("SecurePass@123")
          .firstName("John")
          .lastName("Doe")
          .build();

3. Call Service
   └─ ResponseEntity<TokenResponse> response =
        restTemplate.postForEntity(
          "http://user-service/api/v1/auth/register",
          request,
          TokenResponse.class  // From user-service-api
        );

4. Handle Response
   └─ TokenResponse tokens = response.getBody();
      String accessToken = tokens.getAccessToken();
      // Use accessToken for subsequent calls

5. Call Protected Endpoints
   └─ HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(accessToken);
      // Include headers in subsequent requests
```

## API Versioning Strategy

```
┌──────────────────────────────────────────────────────────┐
│              API Versioning Timeline                     │
└──────────────────────────────────────────────────────────┘

Phase 1: v1 Active (Current)
┌─────────────────────────────────────────┐
│ /api/v1/auth/register                   │ ← All requests here
│ /api/v1/auth/login                      │
│ /api/v1/users/me                        │
│ ... (all endpoints)                     │
└─────────────────────────────────────────┘

Phase 2: v2 Introduced (New features)
┌─────────────────────────────────────────┐
│ /api/v1/... (continued support)         │
└─────────────────────────────────────────┘
┌─────────────────────────────────────────┐
│ /api/v2/... (new endpoints)             │ ← New clients here
│ (Breaking changes go here)              │
└─────────────────────────────────────────┘

Migration Period:
- v1 fully supported (1-2 releases)
- Clients migrate to v2
- v1 deprecated
- v1 sunset after migration period

When to Version:
- Breaking changes → New version
- New features → Same version
- Bug fixes → Same version
- Enhancements → Same version
```

## Code Organization Diagram

```
┌──────────────────────────────────────────────────────────┐
│              Java Package Structure                      │
└──────────────────────────────────────────────────────────┘

user-service/
│
├── controller/
│   └── AuthController
│       └─ @RestController
│       └─ implements UserServiceContract
│       └─ Handles HTTP requests
│
├── service/
│   ├── AuthService
│   │   └─ Handles authentication logic
│   └── UserService
│       └─ Handles user operations
│
├── domain/
│   ├── User
│   │   └─ JPA entity
│   └── RefreshToken
│       └─ JPA entity
│
├── repository/
│   ├── UserRepository
│   │   └─ JPA repository for User
│   └── RefreshTokenRepository
│       └─ JPA repository for RefreshToken
│
├── security/
│   ├── JwtUtil
│   │   └─ JWT token generation/validation
│   └── SecurityConfig
│       └─ Spring Security configuration
│
├── exception/
│   ├── UserNotFoundException
│   └── EmailAlreadyExistsException
│
├── mapper/
│   └── UserMapper
│       └─ Maps User to UserProfileResponse
│
└── resources/
    ├── application.yml
    ├── application-dev.yml
    └── db/migration/

user-service-api/
│
├── constants/
│   └── UserApiConstants
│       ├─ Api paths
│       ├─ Endpoints
│       ├─ Messages
│       └─ Error codes
│
├── contract/
│   └── UserServiceContract
│       └─ Interface defining all endpoints
│
└── dto/
    ├── request/
    │   ├─ RegisterUserRequest
    │   ├─ LoginRequest
    │   ├─ RefreshTokenRequest
    │   ├─ ChangePasswordRequest
    │   └─ UpdateUserProfileRequest
    │
    └── response/
        ├─ TokenResponse
        ├─ UserProfileResponse
        ├─ ErrorResponse
        └─ SuccessResponse
```

---

**Last Updated:** December 23, 2025  
**Status:** Complete  
**Diagrams:** 12 comprehensive visualizations

