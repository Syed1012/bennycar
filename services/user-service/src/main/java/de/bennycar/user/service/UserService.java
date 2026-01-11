package de.bennycar.user.service;

import de.bennycar.api.user.dto.request.ChangePasswordRequest;
import de.bennycar.api.user.dto.request.UpdateUserProfileRequest;
import de.bennycar.api.user.dto.response.UserProfileResponse;
import de.bennycar.user.domain.User;
import de.bennycar.user.exception.InvalidCredentialsException;
import de.bennycar.user.exception.ResourceNotFoundException;
import de.bennycar.user.mapper.UserMapper;
import de.bennycar.user.repository.RefreshTokenRepository;
import de.bennycar.user.repository.UserRepository;
import de.bennycar.user.util.EmailNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * Service for user-related operations.
 * Handles user retrieval, profile management, and account operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Retrieves a user's profile by their ID.
     *
     * @param userId User's unique identifier
     * @return User profile information
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(UUID userId) {
        log.debug("Fetching user profile for userId: {}", userId);
        User user = findById(userId);
        return userMapper.toUserProfileResponse(user);
    }

    /**
     * Updates a user's profile information.
     *
     * @param userId  User's unique identifier
     * @param request Update profile request
     * @return Updated user profile
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UpdateUserProfileRequest request) {
        log.debug("Updating profile for userId: {}", userId);

        User user = findById(userId);

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(request.getProfilePictureUrl());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }

        @SuppressWarnings("null")
        User savedUser = userRepository.save(user);
        log.info("Successfully updated profile for userId: {}", userId);

        return userMapper.toUserProfileResponse(savedUser);
    }

    /**
     * Changes a user's password.
     *
     * @param userId  User's unique identifier
     * @param request Change password request
     * @throws ResourceNotFoundException   if user not found
     * @throws InvalidCredentialsException if current password is incorrect
     */
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        log.debug("Changing password for userId: {}", userId);

        User user = findById(userId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            log.warn("Password change failed: incorrect current password for userId: {}", userId);
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Successfully changed password for userId: {}", userId);
    }

    /**
     * Deletes a user's account and associated data.
     *
     * @param userId User's unique identifier
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional
    public void deleteAccount(UUID userId) {
        log.debug("Deleting account for userId: {}", userId);

        User user = findById(userId);

        // Delete all refresh tokens for this user
        refreshTokenRepository.findAllByUserIdAndRevokedFalse(userId)
                .forEach(token -> {
                    token.setRevoked(true);
                    token.setRevokedAt(java.time.Instant.now());
                });

        userRepository.delete(user);
        log.info("Successfully deleted account for userId: {}", userId);
    }

    /**
     * Retrieves a user entity by ID.
     *
     * @param userId User's unique identifier
     * @return User entity
     * @throws ResourceNotFoundException if user not found
     */
    public User findById(UUID userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
    }

    /**
     * Retrieves a user by email.
     *
     * @param email User's email address
     * @return User entity
     * @throws ResourceNotFoundException if user not found
     */
    public User findByEmail(String email) {
        String normalizedEmail = EmailNormalizer.normalize(email);
        return userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    /**
     * Checks if a user exists with the given email.
     *
     * @param email Email address to check
     * @return true if user exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        String normalizedEmail = EmailNormalizer.normalize(email);
        return userRepository.existsByEmail(normalizedEmail);
    }
}