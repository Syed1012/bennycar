package de.bennycar.user.service;

import de.bennycar.user.constants.AppConstants;
import de.bennycar.user.dto.RegistrationRequest;
import de.bennycar.user.dto.TokenResponse;
import de.bennycar.user.exception.InvalidCredentialsException;
import de.bennycar.user.exception.UserAlreadyExistsException;
import de.bennycar.user.domain.Role;
import de.bennycar.user.domain.User;
import de.bennycar.user.repository.RoleRepository;
import de.bennycar.user.repository.UserRepository;
import de.bennycar.user.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service handling authentication and authorization operations.
 * Manages user registration, login, and token generation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Registers a new user in the system.
     *
     * @param request Registration details
     * @return Newly created user
     * @throws UserAlreadyExistsException if email already registered
     */
    @Transactional
    public User register(RegistrationRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        String normalizedEmail = request.getEmail().toLowerCase().trim();

        if (userRepository.existsByEmail(normalizedEmail)) {
            log.warn("Registration failed: User already exists with email: {}", normalizedEmail);
            throw new UserAlreadyExistsException(normalizedEmail);
        }

        User user = User.builder()
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .profilePictureUrl(request.getProfilePictureUrl())
                .address(request.getAddress())
                .status(AppConstants.UserStatus.ACTIVE)
                .build();

        // Assign default USER role
        Role defaultRole = roleRepository.findByName(AppConstants.Role.USER)
                .orElseGet(() -> createRole(AppConstants.Role.USER, "Default user role"));

        user.getRoles().add(defaultRole);

        User savedUser = userRepository.save(user);
        log.info("Successfully registered user with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());

        return savedUser;
    }

    /**
     * Authenticates a user with email and password.
     *
     * @param email User's email
     * @param password User's password
     * @return Authenticated user
     * @throws InvalidCredentialsException if credentials are invalid
     */
    @Transactional
    public User authenticate(String email, String password) {
        log.debug("Attempting to authenticate user with email: {}", email);

        String normalizedEmail = email.toLowerCase().trim();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> {
                    log.warn("Authentication failed: User not found with email: {}", normalizedEmail);
                    return new InvalidCredentialsException();
                });

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("Authentication failed: Invalid password for email: {}", normalizedEmail);
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            userRepository.save(user);
            throw new InvalidCredentialsException();
        }

        // Reset failed login attempts and update last login time
        user.setFailedLoginAttempts(0);
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        log.info("Successfully authenticated user: {}", normalizedEmail);
        return user;
    }

    /**
     * Generates a complete token response including access and refresh tokens.
     *
     * @param user User for whom to generate tokens
     * @return Token response with both access and refresh tokens
     */
    @Transactional
    public TokenResponse generateTokenResponse(User user) {
        log.debug("Generating token response for user: {}", user.getEmail());

        String accessToken = generateAccessToken(user);
        RefreshTokenService.RefreshTokenPair refreshTokenPair = refreshTokenService.createRefreshToken(user);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenPair.rawToken())
                .expiresIn(jwtUtil.getAccessTokenTtlSeconds())
                .tokenType("Bearer")
                .build();
    }

    /**
     * Generates a JWT access token for the user.
     *
     * @param user User for whom to generate the token
     * @return JWT access token
     */
    public String generateAccessToken(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return jwtUtil.generateAccessToken(user.getId(), user.getEmail(), roles);
    }

    /**
     * Creates a new role if it doesn't exist.
     */
    private Role createRole(String name, String description) {
        log.info("Creating new role: {}", name);
        Role role = Role.builder()
                .name(name)
                .description(description)
                .build();
        return roleRepository.save(role);
    }
}
