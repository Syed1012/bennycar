# User Service API - Implementation Guide

## Table of Contents

1. [Overview](#overview)
2. [Pre-Implementation Checklist](#pre-implementation-checklist)
3. [Step-by-Step Implementation](#step-by-step-implementation)
4. [Database Schema](#database-schema)
5. [Configuration](#configuration)
6. [Testing](#testing)
7. [Troubleshooting](#troubleshooting)
8. [Best Practices](#best-practices)

---

## Overview

This guide provides step-by-step instructions for implementing the User Service API according to the contract defined in `UserServiceContract` interface.

### What You'll Build

- REST API endpoints following REST principles
- JWT-based authentication and authorization
- User profile management
- Password security with bcrypt
- Comprehensive error handling
- Complete validation and error responses

### Technology Stack

- Java 17
- Spring Boot 3.x
- Spring Security
- JWT (JJWT)
- PostgreSQL
- Spring Data JPA
- Lombok
- OpenAPI/Swagger

### Time Estimate

- Basic implementation: 4-6 hours
- With testing: 8-10 hours
- With all features: 12-15 hours

---

## Pre-Implementation Checklist

Before starting, ensure you have:

- [ ] Java 17 or higher installed
- [ ] Maven 3.6+
- [ ] PostgreSQL database
- [ ] IDE (IntelliJ IDEA, VS Code, or Eclipse)
- [ ] Postman or similar API testing tool
- [ ] Git for version control
- [ ] `user-service-api` package as dependency
- [ ] Read the API contract thoroughly
- [ ] Understood JWT authentication flow

---

## Step-by-Step Implementation

### Step 1: Add Dependency to user-service

Edit `user-service/pom.xml`:

```xml
<dependency>
    <groupId>de.bennycar</groupId>
    <artifactId>user-service-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

Then run: `mvn clean install`

### Step 2: Create the Database Schema

Create migration file: `src/main/resources/db/migration/V1__init.sql`

```sql
-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    profile_picture_url VARCHAR(512),
    address VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    email_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Refresh tokens table
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255)
);

-- Audit log table
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    action VARCHAR(50) NOT NULL,
    entity VARCHAR(50) NOT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    user_agent VARCHAR(512),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
```

### Step 3: Create Domain Model

File: `src/main/java/de/bennycar/user/domain/User.java`

```java
package de.bennycar.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    private String phoneNumber;
    private String profilePictureUrl;
    private String address;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    private String createdBy;
    private String updatedBy;
    
    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED, DELETED
    }
}
```

File: `src/main/java/de/bennycar/user/domain/RefreshToken.java`

```java
package de.bennycar.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private UUID userId;
    
    @Column(nullable = false, unique = true)
    private String tokenHash;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean revoked = false;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private String createdBy;
}
```

### Step 4: Create Repository Interfaces

File: `src/main/java/de/bennycar/user/repository/UserRepository.java`

```java
package de.bennycar.user.repository;

import de.bennycar.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndStatus(UUID id, User.UserStatus status);
    boolean existsByEmail(String email);
}
```

File: `src/main/java/de/bennycar/user/repository/RefreshTokenRepository.java`

```java
package de.bennycar.user.repository;

import de.bennycar.user.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void deleteByUserId(UUID userId);
}
```

### Step 5: Create Service Layer

File: `src/main/java/de/bennycar/user/service/UserService.java`

```java
package de.bennycar.user.service;

import de.bennycar.api.user.dto.request.UpdateUserProfileRequest;
import de.bennycar.api.user.dto.response.UserProfileResponse;
import de.bennycar.user.domain.User;
import de.bennycar.user.repository.UserRepository;
import de.bennycar.user.exception.UserNotFoundException;
import de.bennycar.user.exception.EmailAlreadyExistsException;
import de.bennycar.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    
    /**
     * Get user by ID
     */
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }
    
    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }
    
    /**
     * Create new user
     */
    public User createUser(User user, String plainPassword) {
        // Check email uniqueness
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + user.getEmail());
        }
        
        // Hash password
        String hashedPassword = passwordEncoder.encode(plainPassword);
        user.setPasswordHash(hashedPassword);
        
        // Save and log
        User savedUser = userRepository.save(user);
        log.info("User created: {}", savedUser.getEmail());
        
        return savedUser;
    }
    
    /**
     * Update user profile
     */
    public User updateUserProfile(UUID userId, UpdateUserProfileRequest request) {
        User user = getUserById(userId);
        
        // Check email uniqueness if email is being updated
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        
        // Update allowed fields
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getProfilePictureUrl() != null) user.setProfilePictureUrl(request.getProfilePictureUrl());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        
        User updatedUser = userRepository.save(user);
        log.info("User profile updated: {}", updatedUser.getId());
        
        return updatedUser;
    }
    
    /**
     * Change user password
     */
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        // Update password
        String hashedNewPassword = passwordEncoder.encode(newPassword);
        user.setPasswordHash(hashedNewPassword);
        userRepository.save(user);
        
        log.info("Password changed for user: {}", userId);
    }
    
    /**
     * Delete user account
     */
    public void deleteUser(UUID userId) {
        User user = getUserById(userId);
        user.setStatus(User.UserStatus.DELETED);
        userRepository.save(user);
        log.info("User account deleted: {}", userId);
    }
    
    /**
     * Get user profile
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(UUID userId) {
        User user = getUserById(userId);
        return userMapper.toProfileResponse(user);
    }
}
```

File: `src/main/java/de/bennycar/user/service/AuthService.java`

```java
package de.bennycar.user.service;

import de.bennycar.api.user.dto.request.RegisterUserRequest;
import de.bennycar.api.user.dto.response.TokenResponse;
import de.bennycar.user.domain.User;
import de.bennycar.user.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    
    /**
     * Register new user
     */
    public User register(RegisterUserRequest request) {
        User user = User.builder()
            .email(request.getEmail())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .phoneNumber(request.getPhoneNumber())
            .profilePictureUrl(request.getProfilePictureUrl())
            .address(request.getAddress())
            .status(User.UserStatus.ACTIVE)
            .emailVerified(false)
            .build();
        
        return userService.createUser(user, request.getPassword());
    }
    
    /**
     * Generate token response
     */
    public TokenResponse generateTokenResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        
        // Store refresh token
        refreshTokenService.createRefreshToken(user.getId(), refreshToken);
        
        return TokenResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(jwtUtil.getAccessTokenExpiration() / 1000) // Convert to seconds
            .expiresAt(jwtUtil.getAccessTokenExpiresAt(accessToken))
            .refreshExpiresAt(jwtUtil.getRefreshTokenExpiresAt(refreshToken))
            .build();
    }
}
```

### Step 6: Create JWT Utility

File: `src/main/java/de/bennycar/user/security/JwtUtil.java`

```java
package de.bennycar.user.security;

import de.bennycar.user.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {
    
    @Value("${jwt.secret:your-secret-key-change-in-production}")
    private String jwtSecret;
    
    @Value("${jwt.access-token-expiration:600000}")
    private Long accessTokenExpiration;
    
    @Value("${jwt.refresh-token-expiration:604800000}")
    private Long refreshTokenExpiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    /**
     * Generate access token
     */
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("userId", user.getId().toString());
        
        return createToken(claims, user.getId().toString(), accessTokenExpiration);
    }
    
    /**
     * Generate refresh token
     */
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("type", "refresh");
        
        return createToken(claims, user.getId().toString(), refreshTokenExpiration);
    }
    
    /**
     * Create JWT token
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    /**
     * Get user ID from token
     */
    public String getUserIdFromToken(String token) {
        return extractAllClaims(token).get("userId", String.class);
    }
    
    /**
     * Validate token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all claims from token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
    
    public Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
    
    public Instant getAccessTokenExpiresAt(String token) {
        return extractAllClaims(token).getExpiration().toInstant();
    }
    
    public Instant getRefreshTokenExpiresAt(String token) {
        return extractAllClaims(token).getExpiration().toInstant();
    }
}
```

### Step 7: Create Controller

File: `src/main/java/de/bennycar/user/controller/AuthController.java`

```java
package de.bennycar.user.controller;

import de.bennycar.api.user.contract.UserServiceContract;
import de.bennycar.api.user.constants.UserApiConstants;
import de.bennycar.api.user.dto.request.*;
import de.bennycar.api.user.dto.response.TokenResponse;
import de.bennycar.api.user.dto.response.UserProfileResponse;
import de.bennycar.user.domain.User;
import de.bennycar.user.security.JwtUtil;
import de.bennycar.user.service.AuthService;
import de.bennycar.user.service.RefreshTokenService;
import de.bennycar.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(UserApiConstants.Api.V1)
@RequiredArgsConstructor
public class AuthController implements UserServiceContract {
    
    private final AuthService authService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    
    @Override
    public ResponseEntity<TokenResponse> register(@Valid RegisterUserRequest request) {
        log.info("Registration request for email: {}", request.getEmail());
        
        User user = authService.register(request);
        TokenResponse tokenResponse = authService.generateTokenResponse(user);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponse);
    }
    
    @Override
    public ResponseEntity<TokenResponse> login(@Valid LoginRequest request) {
        log.info("Login request for email: {}", request.getEmail());
        
        User user = userService.getUserByEmail(request.getEmail());
        
        // Verify password (implement password verification)
        // This should use PasswordEncoder
        
        TokenResponse tokenResponse = authService.generateTokenResponse(user);
        
        return ResponseEntity.ok(tokenResponse);
    }
    
    @Override
    public ResponseEntity<TokenResponse> refreshToken(@Valid RefreshTokenRequest request) {
        String userId = jwtUtil.getUserIdFromToken(request.getRefreshToken());
        User user = userService.getUserById(UUID.fromString(userId));
        
        TokenResponse tokenResponse = authService.generateTokenResponse(user);
        
        return ResponseEntity.ok(tokenResponse);
    }
    
    @Override
    public ResponseEntity<?> validateToken(String token) {
        boolean isValid = jwtUtil.validateToken(token);
        return ResponseEntity.ok(isValid);
    }
    
    @Override
    public ResponseEntity<?> logout() {
        UUID userId = getCurrentUserId();
        refreshTokenService.revokeAllRefreshTokens(userId);
        return ResponseEntity.ok("Logout successful");
    }
    
    @Override
    public ResponseEntity<UserProfileResponse> getProfile() {
        UUID userId = getCurrentUserId();
        UserProfileResponse profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }
    
    @Override
    public ResponseEntity<UserProfileResponse> getUserById(String userId) {
        UserProfileResponse profile = userService.getUserProfile(UUID.fromString(userId));
        return ResponseEntity.ok(profile);
    }
    
    @Override
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid UpdateUserProfileRequest request) {
        UUID userId = getCurrentUserId();
        User updated = userService.updateUserProfile(userId, request);
        UserProfileResponse profile = userService.getUserProfile(updated.getId());
        return ResponseEntity.ok(profile);
    }
    
    @Override
    public ResponseEntity<?> changePassword(@Valid ChangePasswordRequest request) {
        UUID userId = getCurrentUserId();
        
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }
        
        userService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
        refreshTokenService.revokeAllRefreshTokens(userId);
        
        return ResponseEntity.ok("Password changed successfully");
    }
    
    @Override
    public ResponseEntity<?> deleteAccount() {
        UUID userId = getCurrentUserId();
        userService.deleteUser(userId);
        refreshTokenService.revokeAllRefreshTokens(userId);
        return ResponseEntity.ok("Account deleted successfully");
    }
    
    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString((String) auth.getPrincipal());
    }
}
```

### Step 8: Create Exception Classes

File: `src/main/java/de/bennycar/user/exception/UserNotFoundException.java`

```java
package de.bennycar.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
```

File: `src/main/java/de/bennycar/user/exception/EmailAlreadyExistsException.java`

```java
package de.bennycar.user.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
```

### Step 9: Create Mapper

File: `src/main/java/de/bennycar/user/mapper/UserMapper.java`

```java
package de.bennycar.user.mapper;

import de.bennycar.api.user.dto.response.UserProfileResponse;
import de.bennycar.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public UserProfileResponse toProfileResponse(User user) {
        return UserProfileResponse.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .phoneNumber(user.getPhoneNumber())
            .profilePictureUrl(user.getProfilePictureUrl())
            .address(user.getAddress())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .status(user.getStatus().toString())
            .emailVerified(user.getEmailVerified())
            .build();
    }
}
```

### Step 10: Configure Application Properties

File: `src/main/resources/application-dev.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${POSTGRES_HOST:localhost}:${POSTGRES_PORT:5432}/${POSTGRES_DB}
    username: ${POSTGRES_USER}
    password: ${POSTGRES_PASSWORD}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
  
  flyway:
    locations: classpath:db/migration

server:
  port: 8081

jwt:
  secret: ${JWT_SECRET:dev-secret-key-change-in-production}
  access-token-expiration: 600000  # 10 minutes
  refresh-token-expiration: 604800000  # 7 days
```

---

## Database Schema

### Users Table

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    profile_picture_url VARCHAR(512),
    address VARCHAR(500),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    email_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);
```

---

## Configuration

### JWT Configuration

```properties
jwt.secret=your-secret-key-min-32-characters
jwt.access-token-expiration=600000
jwt.refresh-token-expiration=604800000
```

### Database Configuration

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bennycar_user
spring.datasource.username=postgres
spring.datasource.password=password
```

---

## Testing

### Unit Tests

```java
@SpringBootTest
class AuthControllerTest {
    
    @MockBean
    private AuthService authService;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testRegisterSuccess() throws Exception {
        RegisterUserRequest request = RegisterUserRequest.builder()
            .email("test@example.com")
            .password("SecurePass@123")
            .firstName("Test")
            .lastName("User")
            .build();
        
        mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }
}
```

---

## Troubleshooting

### Common Issues

1. **JWT Secret Not Configured**
   - Set environment variable or application properties

2. **Password Encoding Fails**
   - Ensure PasswordEncoder bean is defined

3. **Database Connection Fails**
   - Verify PostgreSQL is running
   - Check connection string

4. **Token Validation Fails**
   - Verify JWT secret matches
   - Check token expiration

---

## Best Practices

1. Always use environment variables for secrets
2. Implement comprehensive logging
3. Add rate limiting to authentication endpoints
4. Monitor token usage
5. Regularly audit password changes
6. Keep dependencies updated

---

**Document Version:** 1.0  
**Last Updated:** December 2025  
**Status:** Complete

