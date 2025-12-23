# User Service API Contract Documentation

## Overview

The User Service API Contract defines a comprehensive set of endpoints for user authentication, profile management, and authorization. This document serves as the specification that the User Service implementation must strictly follow for inter-service communication and client integration.

**Version:** 1.0  
**Last Updated:** December 2025  
**Status:** Production Ready

---

## Table of Contents

1. [API Design Principles](#api-design-principles)
2. [Authentication & Authorization](#authentication--authorization)
3. [Request/Response Standards](#requestresponse-standards)
4. [Endpoints](#endpoints)
5. [Error Handling](#error-handling)
6. [Security Considerations](#security-considerations)
7. [Implementation Guide](#implementation-guide)

---

## API Design Principles

### RESTful Architecture

- **Resource-Oriented:** All endpoints represent resources (users, tokens, sessions)
- **HTTP Methods:** Proper use of GET, POST, PUT, DELETE semantics
- **Idempotency:** Safe operations are idempotent where applicable
- **Statelessness:** All requests are stateless; state is managed via tokens

### Versioning Strategy

```
Base Path: /api
v1: /api/v1 (Current Production Version)
v2: /api/v2 (Future Version)
```

The API uses URL path versioning to support multiple versions simultaneously, enabling backward compatibility.

### Naming Conventions

- **Endpoints:** Lowercase, hyphen-separated (e.g., `/refresh-token`)
- **Fields:** camelCase in JSON payloads
- **Status Codes:** Standard HTTP status codes
- **Error Codes:** UPPER_SNAKE_CASE programmatic codes

---

## Authentication & Authorization

### Token-Based Authentication (JWT)

The API uses JWT (JSON Web Tokens) for stateless authentication.

#### Token Types

1. **Access Token**
   - Short-lived (default: 10 minutes / 600 seconds)
   - Used for API request authentication
   - Included in `Authorization: Bearer <token>` header
   - Contains user claims (userId, email, roles)

2. **Refresh Token**
   - Long-lived (default: 7 days)
   - Used to obtain new access tokens
   - Stored securely on client
   - Can be revoked

#### Token Claims

Access tokens include:

```json
{
  "sub": "user-id",
  "email": "user@example.com",
  "roles": ["USER", "ADMIN"],
  "iat": 1702987200,
  "exp": 1702987800,
  "iss": "bennycar-auth",
  "aud": "bennycar-api"
}
```

#### Authorization Header

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Security Requirements

Endpoints marked with `@SecurityRequirement(name = "Bearer Token")` require:

1. Valid JWT token in Authorization header
2. Token must not be expired
3. Token must not be blacklisted
4. Token signature must be valid

---

## Request/Response Standards

### Request Format

All request bodies use JSON format with Content-Type: `application/json`

```json
{
  "email": "user@example.com",
  "password": "SecurePass@123",
  "firstName": "John",
  "lastName": "Doe"
}
```

### Response Format

#### Success Response

```json
{
  "status": 200,
  "message": "Operation completed successfully",
  "data": { /* payload */ },
  "timestamp": "2025-12-23T10:30:00"
}
```

#### Token Response

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 600,
  "expiresAt": "2025-12-23T10:45:30Z",
  "refreshExpiresAt": "2025-12-30T10:30:00Z"
}
```

#### Error Response

```json
{
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Email must be valid",
  "detail": "The provided email address format is invalid",
  "timestamp": "2025-12-23T10:30:00",
  "path": "/api/v1/auth/register",
  "fieldErrors": {
    "email": "Email must be valid",
    "password": "Password must be between 12 and 128 characters"
  }
}
```

### Status Codes

| Code | Meaning | Usage |
|------|---------|-------|
| 200 | OK | Successful GET, POST, PUT |
| 201 | Created | Resource created successfully |
| 400 | Bad Request | Invalid input validation errors |
| 401 | Unauthorized | Authentication failed, invalid token |
| 403 | Forbidden | Authorization failed, insufficient permissions |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Business rule violation (e.g., duplicate email) |
| 500 | Internal Server Error | Unexpected server error |

---

## Endpoints

### Authentication Endpoints

#### 1. Register User

```
POST /api/v1/auth/register
```

**Purpose:** Create a new user account

**Request:**

```json
{
  "email": "john.doe@example.com",
  "password": "SecurePass@123",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1-555-555-5555",
  "profilePictureUrl": "https://cdn.example.com/avatar.png",
  "address": "221B Baker Street, London"
}
```

**Validation Rules:**

- `email`: Required, must be valid email format, must be unique
- `password`: Required, 12-128 characters, must contain uppercase, lowercase, number, special character
- `firstName`: Required, max 100 characters
- `lastName`: Required, max 100 characters
- `phoneNumber`: Optional, max 20 characters
- `profilePictureUrl`: Optional, max 512 characters
- `address`: Optional, max 500 characters

**Response (201 Created):**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 600,
  "expiresAt": "2025-12-23T10:45:30Z",
  "refreshExpiresAt": "2025-12-30T10:30:00Z"
}
```

**Business Logic:**

1. Validate all input fields
2. Check email uniqueness in database
3. Hash password using bcrypt
4. Create user with ACTIVE status
5. Generate JWT tokens
6. Log registration event
7. Return tokens for immediate authentication

**Error Scenarios:**

| Code | Reason |
|------|--------|
| 400 | Validation error (missing/invalid fields) |
| 409 | Email already exists |
| 500 | Unexpected server error |

**Example cURL:**

```bash
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "SecurePass@123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

---

#### 2. Login

```
POST /api/v1/auth/login
```

**Purpose:** Authenticate user and obtain tokens

**Request:**

```json
{
  "email": "john.doe@example.com",
  "password": "SecurePass@123"
}
```

**Validation Rules:**

- `email`: Required, must be valid email
- `password`: Required

**Response (200 OK):**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 600,
  "expiresAt": "2025-12-23T10:45:30Z",
  "refreshExpiresAt": "2025-12-30T10:30:00Z"
}
```

**Business Logic:**

1. Validate input format
2. Find user by email
3. Verify password hash matches
4. Check user is ACTIVE (not suspended/deleted)
5. Generate new JWT tokens
6. Create refresh token record
7. Log login event with timestamp
8. Return tokens

**Error Scenarios:**

| Code | Reason |
|------|--------|
| 400 | Validation error |
| 401 | Invalid email or password |
| 403 | User account suspended |
| 500 | Server error |

**Example cURL:**

```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "SecurePass@123"
  }'
```

---

#### 3. Refresh Token

```
POST /api/v1/auth/refresh
```

**Purpose:** Obtain new access token using refresh token

**Request:**

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK):**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 600,
  "expiresAt": "2025-12-23T10:45:30Z",
  "refreshExpiresAt": "2025-12-30T10:30:00Z"
}
```

**Business Logic:**

1. Validate refresh token format
2. Verify token signature
3. Check token expiration
4. Verify token is not revoked
5. Extract user from token
6. Generate new access token
7. Optionally rotate refresh token
8. Return new tokens

**Error Scenarios:**

| Code | Reason |
|------|--------|
| 401 | Invalid or expired refresh token |
| 401 | Refresh token revoked |
| 500 | Server error |

---

#### 4. Validate Token

```
GET /api/v1/auth/validate?token=<token>
```

**Purpose:** Validate JWT token validity

**Query Parameters:**

- `token` (required): JWT token to validate

**Response (200 OK):**

```json
{
  "valid": true,
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "john.doe@example.com",
  "expiresAt": "2025-12-23T10:45:30Z"
}
```

**Business Logic:**

1. Parse JWT token
2. Verify signature
3. Check expiration
4. Verify claims
5. Return validation status

**Error Scenarios:**

| Code | Reason |
|------|--------|
| 401 | Token invalid or expired |

---

#### 5. Logout

```
POST /api/v1/auth/logout
Authorization: Bearer <token>
```

**Purpose:** Invalidate user tokens and end session

**Response (200 OK):**

```json
{
  "message": "Logout successful"
}
```

**Business Logic:**

1. Extract user from token
2. Revoke refresh token
3. Optionally blacklist access token
4. Clear user session
5. Log logout event
6. Return success

---

### User Profile Endpoints

#### 6. Get Current User Profile

```
GET /api/v1/users/me
Authorization: Bearer <token>
```

**Purpose:** Retrieve authenticated user's profile

**Response (200 OK):**

```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1-555-555-5555",
  "profilePictureUrl": "https://cdn.example.com/avatar.png",
  "address": "221B Baker Street, London",
  "createdAt": "2025-01-01T10:30:00",
  "updatedAt": "2025-12-23T15:45:30",
  "status": "ACTIVE",
  "emailVerified": true
}
```

---

#### 7. Get User by ID

```
GET /api/v1/users/{userId}
Authorization: Bearer <token>
```

**Purpose:** Retrieve specific user's profile (restricted access)

**Path Parameters:**

- `userId`: UUID of user to retrieve

**Authorization:**

- User can access their own profile
- Admins can access any user's profile

**Response (200 OK):** Same as endpoint 6

**Error Scenarios:**

| Code | Reason |
|------|--------|
| 403 | Insufficient permissions |
| 404 | User not found |

---

#### 8. Update User Profile

```
PUT /api/v1/users/me
Authorization: Bearer <token>
```

**Purpose:** Update authenticated user's profile

**Request (all fields optional):**

```json
{
  "firstName": "Jonathan",
  "lastName": "Smith",
  "email": "jonathan.smith@example.com",
  "phoneNumber": "+1-555-666-7777",
  "profilePictureUrl": "https://cdn.example.com/new-avatar.png",
  "address": "10 Downing Street, London"
}
```

**Validation:**

- Email must be unique if updated
- Phone format must be valid if provided
- URL fields must be valid URLs

**Response (200 OK):** Updated user profile

**Business Logic:**

1. Extract user from token
2. Validate input fields
3. Check email uniqueness if changed
4. Update only provided fields
5. Save changes to database
6. Audit the update
7. Return updated profile

**Error Scenarios:**

| Code | Reason |
|------|--------|
| 400 | Validation error |
| 409 | Email already exists |

---

#### 9. Change Password

```
POST /api/v1/users/me/password
Authorization: Bearer <token>
```

**Purpose:** Change user's password

**Request:**

```json
{
  "currentPassword": "OldPass@123",
  "newPassword": "NewPass@456",
  "confirmPassword": "NewPass@456"
}
```

**Validation:**

- Current password must match
- New password must be 12-128 characters
- New password must not equal current password
- Confirm password must match new password

**Response (200 OK):**

```json
{
  "message": "Password changed successfully"
}
```

**Business Logic:**

1. Extract user from token
2. Verify current password
3. Validate new password
4. Check password confirmation
5. Hash new password
6. Update user password
7. Revoke all refresh tokens
8. Log password change
9. Return success

**Security Note:** Changing password invalidates all active sessions, requiring re-login

**Error Scenarios:**

| Code | Reason |
|------|--------|
| 400 | Validation error |
| 401 | Current password incorrect |

---

#### 10. Delete Account

```
DELETE /api/v1/users/me
Authorization: Bearer <token>
```

**Purpose:** Delete user account

**Response (200 OK):**

```json
{
  "message": "Account deleted successfully"
}
```

**Business Logic:**

1. Extract user from token
2. Set user status to DELETED
3. Revoke all tokens
4. Preserve audit trail
5. Clear user sessions
6. Log account deletion
7. Return success

**Security Note:** This operation is irreversible

**Error Scenarios:**

| Code | Reason |
|------|--------|
| 400 | Validation error |

---

## Error Handling

### Error Response Structure

All error responses follow a standardized format:

```json
{
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Email must be valid",
  "detail": "The provided email address format is invalid",
  "timestamp": "2025-12-23T10:30:00",
  "path": "/api/v1/auth/register",
  "fieldErrors": {
    "email": "Email must be valid",
    "password": "Password must be between 12 and 128 characters"
  }
}
```

### Error Codes

| Code | Description | HTTP Status |
|------|-------------|------------|
| VALIDATION_ERROR | Input validation failed | 400 |
| INVALID_REQUEST | Malformed request | 400 |
| RESOURCE_NOT_FOUND | Requested resource not found | 404 |
| RESOURCE_CONFLICT | Resource conflict (e.g., duplicate) | 409 |
| UNAUTHORIZED | Authentication failed | 401 |
| FORBIDDEN | Authorization failed | 403 |
| INVALID_TOKEN | Token invalid or expired | 401 |
| TOKEN_EXPIRED | Token has expired | 401 |
| INTERNAL_ERROR | Unexpected server error | 500 |

### Validation Error Details

When validation fails, the response includes field-level errors:

```json
{
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Validation failed",
  "fieldErrors": {
    "email": "Email is required",
    "password": "Password must be at least 12 characters",
    "firstName": "First name is required"
  }
}
```

---

## Security Considerations

### Password Requirements

Passwords must meet the following criteria:

- Length: 12-128 characters
- Must contain at least one uppercase letter (A-Z)
- Must contain at least one lowercase letter (a-z)
- Must contain at least one digit (0-9)
- Must contain at least one special character (!@#$%^&*)

### Password Storage

- Passwords are hashed using bcrypt with salt rounds of 10+
- Original passwords are never stored or logged
- Password comparisons use constant-time algorithms

### Token Security

- Access tokens are signed with HMAC-SHA256
- Refresh tokens are encrypted before storage
- Token expiration is enforced on every validation
- Tokens can be revoked/blacklisted

### HTTPS Requirement

All API endpoints MUST be accessed over HTTPS in production. HTTP is only allowed for local development.

### CORS Policy

```
Allowed Origins: https://app.bennycar.com, https://bennycar.com
Allowed Methods: GET, POST, PUT, DELETE, OPTIONS
Allowed Headers: Content-Type, Authorization
Exposed Headers: X-Total-Count, X-Page-Number
Max Age: 3600
```

### Rate Limiting

Recommended rate limits:

- Authentication endpoints: 5 requests per minute per IP
- General endpoints: 100 requests per minute per user
- Token refresh: 10 requests per minute per user

### CSRF Protection

Include CSRF tokens in state-changing operations (POST, PUT, DELETE) when using cookies.

---

## Implementation Guide

### Required Dependencies

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>

<!-- Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### Configuration Example

Create a configuration file:

```properties
# JWT Configuration
jwt.secret=your-secret-key-change-in-production
jwt.access-token-expiration=600000  # 10 minutes in ms
jwt.refresh-token-expiration=604800000  # 7 days in ms
```

### Implementation Steps

1. **Create API Package:**
   - Add user-service-api as a dependency to user-service

2. **Implement UserServiceContract:**
   - Create AuthController extending UserServiceContract
   - Implement all endpoints

3. **Add Service Layer:**
   - Create UserService for business logic
   - Create AuthService for authentication

4. **Configure Security:**
   - Add JWT filter
   - Configure Spring Security
   - Set up CORS

5. **Database Schema:**
   - Create users table
   - Create refresh_tokens table
   - Add indexes for performance

### Testing the API

```bash
# Register
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"SecurePass@123","firstName":"Test","lastName":"User"}'

# Login
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"SecurePass@123"}'

# Get Profile
curl -X GET http://localhost:8081/api/v1/users/me \
  -H "Authorization: Bearer <access_token>"

# Refresh Token
curl -X POST http://localhost:8081/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<refresh_token>"}'
```

---

## Best Practices for Implementing This Contract

### DO's ✅

1. **Validate All Inputs:** Always validate request data using the provided DTOs
2. **Use Constants:** Reference `UserApiConstants` for all magic strings
3. **Log Audit Events:** Log all authentication and authorization events
4. **Use Transactions:** Wrap multi-step operations in transactions
5. **Hash Passwords:** Always use bcrypt for password hashing
6. **Encrypt Tokens:** Encrypt refresh tokens before database storage
7. **Implement Caching:** Cache user profiles with appropriate TTL
8. **Use HTTPS:** Always use HTTPS in production
9. **Document Exceptions:** Document custom exception handling
10. **Monitor Performance:** Monitor endpoint response times

### DON'Ts ❌

1. **Don't Log Passwords:** Never log passwords or tokens
2. **Don't Modify Contract:** Don't change request/response structure
3. **Don't Skip Validation:** Never skip input validation
4. **Don't Hardcode Secrets:** Never hardcode JWT secrets
5. **Don't Return Sensitive Data:** Don't expose password hashes or internal IDs
6. **Don't Store Plain Passwords:** Never store passwords in plain text
7. **Don't Ignore Token Expiry:** Always validate token expiration
8. **Don't Use Weak Hashing:** Don't use weak algorithms like MD5 or SHA1
9. **Don't Expose Stack Traces:** Never return stack traces to clients
10. **Don't Disable Security:** Never disable security checks for convenience

---

## Versioning and Breaking Changes

### Semantic Versioning

- **Major.Minor.Patch** (e.g., 1.0.0)
- Major: Breaking changes
- Minor: New features (backward compatible)
- Patch: Bug fixes

### Handling Breaking Changes

1. Create new API version (v2)
2. Maintain v1 for backward compatibility
3. Provide migration guide
4. Set deprecation timeline

---

## Support and Questions

For questions or clarifications regarding this API contract:

1. Review the [API_CONTRACT_IMPLEMENTATION_GUIDE.md](API_CONTRACT_IMPLEMENTATION_GUIDE.md)
2. Check [API_CONTRACTS.md](API_CONTRACTS.md) for all contracts
3. Refer to [API_SEGREGATION_STRATEGY.md](API_SEGREGATION_STRATEGY.md) for architecture

---

**Document Version:** 1.0  
**Last Modified:** December 2025  
**Status:** Production Ready  
**Owner:** Bennycar Development Team

