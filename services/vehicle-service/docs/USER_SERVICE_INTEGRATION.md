# Vehicle Service & User Service Integration Guide

## Table of Contents
- [Integration Overview](#integration-overview)
- [Authentication Flow](#authentication-flow)
- [JWT Token Details](#jwt-token-details)
- [Cross-Service Communication](#cross-service-communication)
- [Configuration Setup](#configuration-setup)
- [Security Implementation](#security-implementation)
- [Testing Integration](#testing-integration)
- [Troubleshooting](#troubleshooting)

---

## Integration Overview

The Vehicle Service and User Service integrate through **JWT-based stateless authentication**. This approach provides:

✅ **Loose Coupling**: Services don't directly communicate  
✅ **Scalability**: No session state to manage  
✅ **Performance**: No additional service calls for auth  
✅ **Security**: Cryptographically signed tokens  

### Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                         Frontend (React)                          │
│                     http://localhost:3000                         │
└───────────────┬─────────────────────────────────┬────────────────┘
                │                                 │
                │ 1. Login                        │ 4. Browse/Configure
                │    (username, password)         │    (JWT token)
                │                                 │
                ▼                                 ▼
┌───────────────────────────┐      ┌────────────────────────────────┐
│      User Service         │      │      Vehicle Service           │
│    http://localhost:8081  │      │    http://localhost:8082       │
├───────────────────────────┤      ├────────────────────────────────┤
│                           │      │                                │
│  POST /api/auth/signup    │      │  GET  /api/vehicles            │
│  POST /api/auth/login ────┼──────┼──> (No auth required)          │
│  GET  /api/users/profile  │   2. │                                │
│       (JWT protected)     │  JWT │  POST /api/configurations      │
│                           │ Token│       (JWT required) ◄─────────┤
│  ┌─────────────────────┐ │      │                                │
│  │  JWT Generation     │ │      │  ┌──────────────────────────┐  │
│  │  - Sign with secret │ │      │  │  JWT Validation          │  │
│  │  - Add user claims  │ │      │  │  - Verify with secret    │  │
│  │  - Set expiration   │ │      │  │  - Extract user claims   │  │
│  └─────────────────────┘ │      │  │  - Set security context  │  │
│                           │      │  └──────────────────────────┘  │
└───────────────────────────┘      └────────────────────────────────┘
         │                                        │
         │                                        │
         ▼                                        ▼
┌────────────────────────────────────────────────────────────────┐
│                    PostgreSQL Database                          │
│                   http://localhost:5433                         │
├────────────────────────────────────────────────────────────────┤
│  Schema: user_service          │  Schema: vehicle_service       │
│  ├── users                     │  ├── vehicles                  │
│  ├── roles                     │  ├── brands                    │
│  ├── user_roles                │  ├── customization_options    │
│  └── refresh_tokens            │  └── vehicle_configurations   │
│                                │      (contains user_id UUID)   │
└────────────────────────────────────────────────────────────────┘
```

### Key Integration Points

| Aspect | User Service | Vehicle Service |
|--------|--------------|-----------------|
| **Authentication** | ✅ Provides | ❌ Consumes |
| **JWT Generation** | ✅ Creates & signs | ❌ Only validates |
| **User Data** | ✅ Full user profiles | ❌ Only user IDs |
| **JWT Secret** | ✅ Signs tokens | ✅ Verifies tokens |
| **Database** | `user_service` schema | `vehicle_service` schema |
| **Port** | 8081 | 8082 |

---

## Authentication Flow

### Complete Authentication Journey

#### Step 1: User Registration (User Service)

**Request:**
```http
POST http://localhost:8081/api/auth/signup
Content-Type: application/json

{
  "username": "john.doe@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1234567890"
}
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "roles": ["ROLE_USER"],
  "createdAt": "2025-12-12T08:00:00Z"
}
```

**What Happens:**
1. User Service validates input
2. Hashes password with BCrypt
3. Saves user to `user_service.users` table
4. Assigns default role (ROLE_USER)
5. Returns user details (no token yet)

---

#### Step 2: User Login (User Service)

**Request:**
```http
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "username": "john.doe@example.com",
  "password": "SecurePass123!"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsInVzZXJJZCI6IjU1MGU4NDAwLWUyOWItNDFkNC1hNzE2LTQ0NjY1NTQ0MDAwMCIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE3MDIzODAwMDAsImV4cCI6MTcwMjQ2NjQwMH0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
  "type": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "john.doe@example.com",
    "email": "john.doe@example.com",
    "roles": ["ROLE_USER"]
  }
}
```

**What Happens:**
1. User Service validates credentials
2. Retrieves user from database
3. Compares password hash
4. Generates JWT token with claims:
   - `sub`: username
   - `userId`: user UUID
   - `roles`: user roles
   - `iat`: issued at timestamp
   - `exp`: expiration timestamp (24 hours)
5. Signs token with shared secret key
6. Returns token to frontend

**Frontend Action:**
```javascript
// Store token in localStorage or sessionStorage
localStorage.setItem('authToken', response.token);
```

---

#### Step 3: Access Vehicle Service (With Token)

**Request:**
```http
GET http://localhost:8082/api/configurations
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**What Happens in Vehicle Service:**

1. **Request intercepted by `JwtAuthenticationFilter`**
   ```java
   // Extract Authorization header
   String authHeader = request.getHeader("Authorization");
   // authHeader = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
   ```

2. **Extract token**
   ```java
   String token = authHeader.substring(7); // Remove "Bearer " prefix
   ```

3. **Validate token using `JwtUtil`**
   ```java
   // Verify signature with shared secret
   Claims claims = Jwts.parserBuilder()
       .setSigningKey(secretKey)
       .build()
       .parseClaimsJws(token)
       .getBody();
   ```

4. **Extract user information**
   ```java
   String username = claims.getSubject(); // "john.doe@example.com"
   String userId = claims.get("userId", String.class); // UUID
   List<String> roles = claims.get("roles", List.class); // ["ROLE_USER"]
   ```

5. **Set Spring Security context**
   ```java
   List<SimpleGrantedAuthority> authorities = roles.stream()
       .map(SimpleGrantedAuthority::new)
       .collect(Collectors.toList());
   
   UsernamePasswordAuthenticationToken authentication = 
       new UsernamePasswordAuthenticationToken(
           userId,  // Principal (user ID)
           null,    // Credentials (not needed)
           authorities
       );
   
   SecurityContextHolder.getContext().setAuthentication(authentication);
   ```

6. **Controller accesses user ID**
   ```java
   @GetMapping
   public ResponseEntity<Page<ConfigurationResponse>> getMyConfigurations() {
       UUID userId = getCurrentUserId(); // From security context
       return ResponseEntity.ok(configurationService.getUserConfigurations(userId));
   }
   
   private UUID getCurrentUserId() {
       Authentication auth = SecurityContextHolder.getContext().getAuthentication();
       return UUID.fromString(auth.getName());
   }
   ```

7. **Service layer uses user ID**
   ```java
   public Page<ConfigurationResponse> getUserConfigurations(UUID userId, Pageable pageable) {
       // Query configurations WHERE user_id = userId
       return configurationRepository.findByUserId(userId, pageable);
   }
   ```

**Response:**
```json
{
  "content": [
    {
      "id": "config-123",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "vehicle": { ... },
      "selectedOptions": [ ... ],
      "totalPrice": 50000.00
    }
  ],
  "totalElements": 3
}
```

---

## JWT Token Details

### Token Structure

JWT consists of three parts separated by dots:

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsInVzZXJJZCI6IjU1MGU4NDAwLWUyOWItNDFkNC1hNzE2LTQ0NjY1NTQ0MDAwMCIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE3MDIzODAwMDAsImV4cCI6MTcwMjQ2NjQwMH0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
│                    HEADER                    │                                           PAYLOAD                                             │         SIGNATURE        │
```

### 1. Header (Base64Url Encoded)

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

- `alg`: Algorithm (HMAC-SHA256)
- `typ`: Token type (JWT)

### 2. Payload (Base64Url Encoded)

```json
{
  "sub": "john.doe@example.com",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "roles": ["ROLE_USER"],
  "iat": 1702380000,
  "exp": 1702466400
}
```

**Standard Claims:**
- `sub` (subject): User identifier (username/email)
- `iat` (issued at): Token creation timestamp
- `exp` (expiration): Token expiry timestamp

**Custom Claims:**
- `userId`: User's UUID (used by Vehicle Service)
- `roles`: User's authorities/permissions

### 3. Signature

```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret_key
)
```

**Purpose:**
- Ensures token hasn't been tampered with
- Only services with the secret key can validate

---

## Cross-Service Communication

### Current Implementation: JWT-Based

**Advantages:**
✅ No service-to-service calls needed  
✅ Stateless (horizontally scalable)  
✅ Fast (no network latency)  
✅ Fault-tolerant (services independent)  

**Limitations:**
❌ User details not immediately available  
❌ No real-time user validation  
❌ Token valid until expiration  

### Data Flow

```
┌─────────────┐                    ┌──────────────────┐
│  Frontend   │                    │  User Service    │
└──────┬──────┘                    └────────┬─────────┘
       │                                    │
       │ 1. Login                           │
       │────────────────────────────────────>
       │                                    │
       │ 2. JWT Token                       │
       │<────────────────────────────────────
       │                                    │
       │                                    │
       ▼                                    │
┌──────────────────┐                        │
│  localStorage    │                        │
│  stores token    │                        │
└──────────────────┘                        │
       │                                    │
       │                                    │
       │ 3. API Request + JWT               │
       │─────────────────────────────────────────────>
       │                                              │
       │                                    ┌─────────▼────────────┐
       │                                    │  Vehicle Service     │
       │                                    │                      │
       │                                    │  4. Validate JWT     │
       │                                    │  5. Extract userId   │
       │                                    │  6. Query by userId  │
       │                                    │                      │
       │ 7. Response                        │                      │
       │<─────────────────────────────────────────────────────────┘
```

### User ID Reference

**In Vehicle Service Database:**
```sql
-- vehicle_configurations table
CREATE TABLE vehicle_service.vehicle_configurations (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,  -- Reference to user_service.users.id
    vehicle_id UUID NOT NULL,
    status VARCHAR(20),
    total_price DECIMAL(12,2),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- No foreign key constraint (loose coupling)
```

**Why no foreign key?**
- Services can be deployed independently
- User Service can be restarted without affecting Vehicle Service
- Easier to scale and maintain
- Services don't need database connectivity to each other

---

## Configuration Setup

### Shared JWT Secret

**Critical: Both services MUST use the same secret key!**

#### In User Service (`user-service/src/main/resources/application.yml`):

```yaml
jwt:
  secret: ${JWT_SECRET:dev-secret-key-change-this-in-production-min-256-bits-for-security}
  expiration: ${JWT_EXPIRATION:86400000}  # 24 hours in milliseconds
```

#### In Vehicle Service (`vehicle-service/src/main/resources/application.yml`):

```yaml
jwt:
  secret: ${JWT_SECRET:dev-secret-key-change-this-in-production-min-256-bits-for-security}
```

#### Environment Configuration (`.env` file):

```bash
# Shared between both services
JWT_SECRET=dev-secret-key-change-this-in-production-min-256-bits-for-security
JWT_EXPIRATION=86400000
```

**Important Notes:**
- Secret must be at least 256 bits (32 characters)
- Use strong, random secret in production
- Never commit secret to version control
- Rotate secrets periodically

---

## Security Implementation

### User Service - Token Generation

```java
@Service
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private long expirationMs;
    
    public String generateToken(UserDetails userDetails, UUID userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("roles", userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

### Vehicle Service - Token Validation

```java
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
    
    public String extractUserId(String token) {
        Claims claims = extractClaims(token);
        return claims.get("userId", String.class);
    }
    
    public List<String> extractRoles(String token) {
        Claims claims = extractClaims(token);
        return claims.get("roles", List.class);
    }
    
    public boolean isTokenExpired(String token) {
        Claims claims = extractClaims(token);
        return claims.getExpiration().before(new Date());
    }
    
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
```

### Vehicle Service - Authentication Filter

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                if (jwtUtil.validateToken(token)) {
                    String userId = jwtUtil.extractUserId(token);
                    List<String> roles = jwtUtil.extractRoles(token);
                    
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                    
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            userId, null, authorities);
                    
                    authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
                }
            } catch (Exception e) {
                log.error("Cannot set user authentication: {}", e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### Vehicle Service - Security Configuration

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/vehicles/**").permitAll()
                .requestMatchers("/api/brands/**").permitAll()
                .requestMatchers("/api/vehicle-types/**").permitAll()
                .requestMatchers("/api/customization/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                
                // Protected endpoints
                .requestMatchers("/api/configurations/**").authenticated()
                
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, 
                UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

---

## Testing Integration

### Manual Testing with cURL

#### 1. Register User (User Service)

```bash
curl -X POST http://localhost:8081/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test@example.com",
    "password": "Test123!",
    "firstName": "Test",
    "lastName": "User",
    "phoneNumber": "+1234567890"
  }'
```

#### 2. Login (User Service)

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test@example.com",
    "password": "Test123!"
  }'
```

**Save the token from response:**
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### 3. Access Public Endpoint (Vehicle Service - No Token)

```bash
curl http://localhost:8082/api/vehicles
```

**Should work without authentication** ✅

#### 4. Access Protected Endpoint (Vehicle Service - With Token)

```bash
curl http://localhost:8082/api/configurations \
  -H "Authorization: Bearer $TOKEN"
```

**Should return user's configurations** ✅

#### 5. Access Protected Endpoint (Without Token)

```bash
curl http://localhost:8082/api/configurations
```

**Should return 401 Unauthorized** ❌

---

### Integration Test Example

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ConfigurationIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private String generateTestToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", "550e8400-e29b-41d4-a716-446655440000");
        claims.put("roles", List.of("ROLE_USER"));
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject("test@example.com")
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86400000))
            .signWith(SignatureAlgorithm.HS256, "test-secret-key")
            .compact();
    }
    
    @Test
    void shouldAccessConfigurationsWithValidToken() throws Exception {
        String token = generateTestToken();
        
        mockMvc.perform(get("/api/configurations")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }
    
    @Test
    void shouldDenyAccessWithoutToken() throws Exception {
        mockMvc.perform(get("/api/configurations"))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    void shouldDenyAccessWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/configurations")
                .header("Authorization", "Bearer invalid-token"))
            .andExpect(status().isUnauthorized());
    }
}
```

---

## Troubleshooting

### Problem 1: "Unauthorized" on Protected Endpoints

**Symptoms:**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required"
}
```

**Possible Causes:**
1. Missing Authorization header
2. Token not prefixed with "Bearer "
3. Token expired
4. Invalid token signature

**Solution:**
```bash
# Check token format
echo $TOKEN | cut -d '.' -f2 | base64 -d

# Verify expiration
# Check 'exp' claim is in the future

# Ensure Bearer prefix
Authorization: Bearer <token>  # Correct
Authorization: <token>         # Wrong
```

---

### Problem 2: JWT Signature Verification Failed

**Symptoms:**
```
Invalid JWT signature
```

**Cause:**
Different JWT secrets in User Service and Vehicle Service

**Solution:**
```bash
# Check .env file
cat .env | grep JWT_SECRET

# Verify both services use same secret
# User Service logs:
2025-12-12T08:00:00 INFO Using JWT secret: dev-secret-key...

# Vehicle Service logs:
2025-12-12T08:00:00 INFO JWT validation configured with secret: dev-secret-key...

# If different, update and restart services
```

---

### Problem 3: Claims Not Found

**Symptoms:**
```java
java.lang.ClassCastException: Cannot cast String to UUID
```

**Cause:**
Token missing expected claims (userId, roles)

**Solution:**
```java
// User Service must include claims
Map<String, Object> claims = new HashMap<>();
claims.put("userId", userId.toString());  // Important: toString()
claims.put("roles", roles);

// Vehicle Service extraction
String userId = claims.get("userId", String.class);  // Then parse
UUID userUuid = UUID.fromString(userId);
```

---

### Problem 4: User Configurations Not Found

**Symptoms:**
```json
{
  "content": [],
  "totalElements": 0
}
```

**Cause:**
userId mismatch or configurations belong to different user

**Debug:**
```java
@GetMapping
public ResponseEntity<Page<ConfigurationResponse>> getMyConfigurations() {
    UUID userId = getCurrentUserId();
    log.debug("Fetching configurations for userId: {}", userId);
    
    // Check if userId matches token
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    log.debug("Authentication principal: {}", auth.getName());
    
    return ResponseEntity.ok(service.getUserConfigurations(userId));
}
```

---

### Problem 5: Token Expiration

**Symptoms:**
Requests fail after 24 hours

**Solution:**
```bash
# Increase expiration (for development only)
JWT_EXPIRATION=604800000  # 7 days

# Production: Implement token refresh
POST /api/auth/refresh-token
```

---

## Best Practices

### Security

1. **Never log tokens**
   ```java
   // Bad
   log.info("Token: {}", token);
   
   // Good
   log.info("Token validation successful for user: {}", userId);
   ```

2. **Use HTTPS in production**
   ```yaml
   server:
     ssl:
       enabled: true
       key-store: classpath:keystore.p12
       key-store-password: ${SSL_PASSWORD}
   ```

3. **Rotate secrets regularly**
   - Change JWT secret every 3-6 months
   - Use environment-specific secrets
   - Never commit secrets to Git

4. **Validate token on every request**
   - Check signature
   - Check expiration
   - Check required claims

### Performance

1. **Cache user information** (future enhancement)
   ```java
   @Cacheable(value = "users", key = "#userId")
   public UserInfo getUserInfo(UUID userId) {
       // Call User Service API
   }
   ```

2. **Use connection pooling**
   ```yaml
   spring:
     datasource:
       hikari:
         maximum-pool-size: 10
         minimum-idle: 5
   ```

3. **Optimize JWT size**
   - Include only necessary claims
   - Avoid large payloads

---

**Integration Status**: ✅ Fully Implemented  
**Last Updated**: December 12, 2025  
**Version**: 1.0

