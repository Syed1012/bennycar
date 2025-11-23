package de.bennycar.user.service;

import de.bennycar.user.dto.RegistrationRequest;
import de.bennycar.user.model.RefreshToken;
import de.bennycar.user.model.Role;
import de.bennycar.user.model.User;
import de.bennycar.user.repository.RefreshTokenRepository;
import de.bennycar.user.repository.RoleRepository;
import de.bennycar.user.repository.UserRepository;
import de.bennycar.user.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User register(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("USER_EXISTS");
        }
        User user = new User();
        user.setEmail(request.getEmail().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        Role buyerRole = roleRepository.findByName("BUYER").orElseGet(() -> {
            Role r = new Role();
            r.setName("BUYER");
            r.setDescription("Default buyer role");
            return roleRepository.save(r);
        });
        user.getRoles().add(buyerRole);
        return userRepository.save(user);
    }

    public String generateAccessToken(User user) {
        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet());
        return jwtUtil.generateAccessToken(user.getId(), user.getEmail(), roles);
    }

    public record RefreshTokenPair(String rawToken, RefreshToken entity) {}

    public RefreshTokenPair createRefreshTokenPair(User user) {
        String raw = generateRawRefreshToken();
        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setTokenHash(hashRawToken(raw));
        rt.setExpiresAt(Instant.now().plusSeconds(60L * 60L * 24L * 7L));
        RefreshToken saved = refreshTokenRepository.save(rt);
        return new RefreshTokenPair(raw, saved);
    }

    public Optional<RefreshToken> findValidRefreshToken(String rawToken) {
        String hash = hashRawToken(rawToken);
        return refreshTokenRepository.findByTokenHash(hash).filter(t -> !t.isRevoked() && t.getExpiresAt().isAfter(Instant.now()));
    }

    public RefreshToken rotateRefreshToken(RefreshToken oldToken) {
        oldToken.setRevoked(true);
        oldToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(oldToken);
        RefreshToken newToken = new RefreshToken();
        newToken.setUser(oldToken.getUser());
        newToken.setRotatedFrom(oldToken.getId());
        newToken.setTokenHash(hashRawToken(generateRawRefreshToken()));
        newToken.setExpiresAt(Instant.now().plusSeconds(60L * 60L * 24L * 7L));
        return refreshTokenRepository.save(newToken);
    }

    public void revokeToken(RefreshToken token) {
        token.setRevoked(true);
        token.setRevokedAt(Instant.now());
        refreshTokenRepository.save(token);
    }

    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email.toLowerCase()).orElseThrow(() -> new IllegalArgumentException("INVALID_CREDENTIALS"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("INVALID_CREDENTIALS");
        }
        return user;
    }

    private String generateRawRefreshToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashRawToken(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashed);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash token", e);
        }
    }

    public JwtUtil getJwtUtil() { return jwtUtil; }

    public UserRepository getUserRepository() { return userRepository; }
}
