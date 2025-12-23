# User Service API - Quick Reference

## 🚀 Quick Start

### Add as Dependency

```xml
<dependency>
    <groupId>de.bennycar</groupId>
    <artifactId>user-service-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Implement Contract

```java
@RestController
@RequestMapping(UserApiConstants.Api.V1)
public class AuthController implements UserServiceContract {
    // Implement all methods from interface
}
```

---

## 📋 Endpoints Overview

### Authentication

| Method | Path | Purpose | Auth |
|--------|------|---------|------|
| POST | `/api/v1/auth/register` | Register user | ❌ |
| POST | `/api/v1/auth/login` | Login | ❌ |
| POST | `/api/v1/auth/refresh` | Refresh token | ❌ |
| GET | `/api/v1/auth/validate` | Validate token | ❌ |
| POST | `/api/v1/auth/logout` | Logout | ✅ |

### User Profile

| Method | Path | Purpose | Auth |
|--------|------|---------|------|
| GET | `/api/v1/users/me` | Get profile | ✅ |
| GET | `/api/v1/users/{userId}` | Get user by ID | ✅ |
| PUT | `/api/v1/users/me` | Update profile | ✅ |
| POST | `/api/v1/users/me/password` | Change password | ✅ |
| DELETE | `/api/v1/users/me` | Delete account | ✅ |

---

## 📤 Request Examples

### Register

```bash
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "SecurePass@123",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1-555-555-5555",
  "profilePictureUrl": "https://...",
  "address": "221B Baker Street, London"
}
```

### Login

```bash
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "SecurePass@123"
}
```

### Refresh Token

```bash
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

### Update Profile

```bash
PUT /api/v1/users/me
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "firstName": "Jonathan",
  "phoneNumber": "+1-555-666-7777"
}
```

### Change Password

```bash
POST /api/v1/users/me/password
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "currentPassword": "OldPass@123",
  "newPassword": "NewPass@456",
  "confirmPassword": "NewPass@456"
}
```

### Get Profile

```bash
GET /api/v1/users/me
Authorization: Bearer <access_token>
```

### Logout

```bash
POST /api/v1/auth/logout
Authorization: Bearer <access_token>
```

### Delete Account

```bash
DELETE /api/v1/users/me
Authorization: Bearer <access_token>
```

---

## 📥 Response Examples

### Token Response (200/201)

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

### User Profile Response (200)

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

### Error Response (400/401/etc)

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

---

## 🔐 Authentication

### Header Format

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token Contents

```json
{
  "sub": "user-id",
  "email": "user@example.com",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "iat": 1702987200,
  "exp": 1702987800,
  "iss": "bennycar-auth",
  "aud": "bennycar-api"
}
```

### Token Lifecycle

| Action | Duration |
|--------|----------|
| Access Token TTL | 10 minutes (600s) |
| Refresh Token TTL | 7 days |
| Token Signing | HMAC-SHA256 |

---

## ✅ Validation Rules

### Email
- Required
- Must be valid format
- Must be unique

### Password
- Required
- 12-128 characters
- Must include: uppercase, lowercase, number, special character

### Names
- Required
- Max 100 characters

### Phone
- Optional
- Max 20 characters

### URLs
- Optional
- Must be valid URL format
- Max 512 characters

---

## 🔴 Common Error Codes

| Code | Status | Meaning |
|------|--------|---------|
| VALIDATION_ERROR | 400 | Input validation failed |
| INVALID_REQUEST | 400 | Malformed request |
| UNAUTHORIZED | 401 | Authentication failed |
| INVALID_TOKEN | 401 | Token invalid/expired |
| FORBIDDEN | 403 | Insufficient permissions |
| NOT_FOUND | 404 | Resource not found |
| CONFLICT | 409 | Resource conflict |
| INTERNAL_ERROR | 500 | Server error |

---

## 📝 Constants Usage

```java
// Endpoints
String registerPath = UserApiConstants.Endpoints.REGISTER;  // "/auth/register"
String loginPath = UserApiConstants.Endpoints.LOGIN;        // "/auth/login"

// Messages
String emailRequired = UserApiConstants.ValidationMessages.EMAIL_REQUIRED;
String invalidToken = UserApiConstants.ErrorMessages.INVALID_TOKEN;

// API Version
String v1 = UserApiConstants.Api.V1;  // "/api/v1"
```

---

## 🧪 cURL Examples

### Register User

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

### Login

```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "SecurePass@123"
  }'
```

### Get Profile

```bash
curl -X GET http://localhost:8081/api/v1/users/me \
  -H "Authorization: Bearer <access_token>"
```

### Refresh Token

```bash
curl -X POST http://localhost:8081/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "<refresh_token>"
  }'
```

### Update Profile

```bash
curl -X PUT http://localhost:8081/api/v1/users/me \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jonathan",
    "phoneNumber": "+1-555-666-7777"
  }'
```

### Change Password

```bash
curl -X POST http://localhost:8081/api/v1/users/me/password \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "OldPass@123",
    "newPassword": "NewPass@456",
    "confirmPassword": "NewPass@456"
  }'
```

### Logout

```bash
curl -X POST http://localhost:8081/api/v1/auth/logout \
  -H "Authorization: Bearer <access_token>"
```

### Delete Account

```bash
curl -X DELETE http://localhost:8081/api/v1/users/me \
  -H "Authorization: Bearer <access_token>"
```

---

## 📦 Java Usage Examples

### Register User Programmatically

```java
RegisterUserRequest request = RegisterUserRequest.builder()
    .email("john.doe@example.com")
    .password("SecurePass@123")
    .firstName("John")
    .lastName("Doe")
    .build();

ResponseEntity<TokenResponse> response = authController.register(request);
TokenResponse tokens = response.getBody();

String accessToken = tokens.getAccessToken();
String refreshToken = tokens.getRefreshToken();
```

### Call Protected Endpoint

```java
// Set up security context with token
SecurityContextHolder.getContext().setAuthentication(
    new BearerTokenAuthenticationToken(accessToken)
);

ResponseEntity<UserProfileResponse> profile = authController.getProfile();
```

### Refresh Token

```java
RefreshTokenRequest refreshRequest = RefreshTokenRequest.builder()
    .refreshToken(currentRefreshToken)
    .build();

ResponseEntity<TokenResponse> newTokens = authController.refreshToken(refreshRequest);
```

### Update Profile

```java
UpdateUserProfileRequest updateRequest = UpdateUserProfileRequest.builder()
    .firstName("Jonathan")
    .phoneNumber("+1-555-666-7777")
    .build();

ResponseEntity<UserProfileResponse> updated = authController.updateProfile(updateRequest);
```

---

## 🏗️ Package Structure

```
de.bennycar.api.user
├── constants
│   └── UserApiConstants.java
├── contract
│   └── UserServiceContract.java
└── dto
    ├── request
    │   ├── RegisterUserRequest
    │   ├── LoginRequest
    │   ├── RefreshTokenRequest
    │   ├── ChangePasswordRequest
    │   └── UpdateUserProfileRequest
    └── response
        ├── TokenResponse
        ├── UserProfileResponse
        ├── ErrorResponse
        └── SuccessResponse<T>
```

---

## 🔒 Security Checklist

- [ ] Use HTTPS in production
- [ ] Change JWT secret in production
- [ ] Set strong password policy
- [ ] Implement rate limiting
- [ ] Enable CORS only for trusted domains
- [ ] Use bcrypt for password hashing
- [ ] Validate all inputs
- [ ] Log audit events
- [ ] Monitor failed login attempts
- [ ] Rotate tokens regularly

---

## 📚 Documentation Files

| File | Purpose |
|------|---------|
| `README.md` | Package overview and usage |
| `USER_SERVICE_API_DOCUMENTATION.md` | Complete API specification |
| `IMPLEMENTATION_GUIDE.md` | Step-by-step implementation |
| `QUICK_REFERENCE.md` | This file |
| `ARCHITECTURE.md` | Architecture overview |

---

## 🚨 Troubleshooting

### Issue: "Invalid Token"
- **Cause:** Token expired or signature invalid
- **Solution:** Use refresh endpoint to get new token

### Issue: "Email already exists"
- **Cause:** User with email already registered
- **Solution:** Use different email or login with existing account

### Issue: "Password validation failed"
- **Cause:** Password doesn't meet requirements
- **Solution:** Ensure password has 12+ chars with uppercase, lowercase, number, special char

### Issue: "Unauthorized"
- **Cause:** Missing or invalid authorization header
- **Solution:** Include `Authorization: Bearer <token>` header

### Issue: 500 Internal Error
- **Cause:** Server-side error
- **Solution:** Check server logs for details

---

## 💡 Tips & Tricks

1. **Use Constants** - Always use `UserApiConstants` instead of hardcoding paths
2. **Save Tokens** - Store both access and refresh tokens on client
3. **Refresh Early** - Refresh token 5 minutes before expiration
4. **Handle Errors** - Always check response status and error codes
5. **Log Events** - Log all auth events for audit trail
6. **Cache Profiles** - Cache user profiles for performance
7. **Rate Limit** - Implement rate limiting on auth endpoints
8. **Monitor** - Monitor token usage and failed attempts

---

## 🔗 Related Resources

- **API Documentation:** See `USER_SERVICE_API_DOCUMENTATION.md`
- **Implementation Guide:** See `IMPLEMENTATION_GUIDE.md`
- **Architecture:** See `ARCHITECTURE.md`
- **Service README:** See main `README.md`

---

## 📞 Support

For questions or issues:

1. Check the comprehensive documentation
2. Review implementation examples
3. Check code comments in DTOs
4. Review contract interface documentation

---

**Version:** 1.0  
**Last Updated:** December 2025  
**Status:** Complete

