package de.bennycar.user.controller;

import de.bennycar.user.dto.LoginRequest;
import de.bennycar.user.dto.RegistrationRequest;
import de.bennycar.user.dto.TokenResponse;
import de.bennycar.user.dto.UserProfileResponse;
import de.bennycar.user.model.RefreshToken;
import de.bennycar.user.model.User;
import de.bennycar.user.repository.RefreshTokenRepository;
import de.bennycar.user.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthController(AuthService authService, RefreshTokenRepository refreshTokenRepository) {
        this.authService = authService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<TokenResponse> register(@RequestBody @Valid RegistrationRequest request) {
        User user = authService.register(request);
        String access = authService.generateAccessToken(user);
        var pair = authService.createRefreshTokenPair(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TokenResponse(access, pair.rawToken(), authService.getJwtUtil().getAccessTokenTtlSeconds()));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        User user = authService.authenticate(request.getEmail(), request.getPassword());
        String access = authService.generateAccessToken(user);
        var pair = authService.createRefreshTokenPair(user);
        return ResponseEntity.ok(new TokenResponse(access, pair.rawToken(), authService.getJwtUtil().getAccessTokenTtlSeconds()));
    }

    @GetMapping("/users/me")
    public ResponseEntity<UserProfileResponse> me(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = auth.substring(7);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(authService.getJwtUtil().getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            java.util.UUID userId = java.util.UUID.fromString(claims.getSubject());
            var userOpt = authService.getUserRepository().findById(userId);
            if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            var user = userOpt.get();
            java.util.Set<String> roles = user.getRoles().stream().map(r -> r.getName()).collect(java.util.stream.Collectors.toSet());
            return ResponseEntity.ok(new UserProfileResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), roles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody Map<String, String> body) {
        String raw = body.get("refreshToken");
        if (raw == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        var existingOpt = authService.findValidRefreshToken(raw);
        if (existingOpt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        var existing = existingOpt.get();
        var user = existing.getUser();
        // rotate
        var rotated = authService.rotateRefreshToken(existing);
        String access = authService.generateAccessToken(user);
        // raw token for rotated is not returned by rotate method, create new pair instead for simplicity
        var pair = authService.createRefreshTokenPair(user);
        return ResponseEntity.ok(new TokenResponse(access, pair.rawToken(), authService.getJwtUtil().getAccessTokenTtlSeconds()));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(@RequestBody Map<String, String> body) {
        String raw = body.get("refreshToken");
        if (raw == null) return ResponseEntity.badRequest().build();
        var existingOpt = authService.findValidRefreshToken(raw);
        existingOpt.ifPresent(authService::revokeToken);
        return ResponseEntity.noContent().build();
    }
}
