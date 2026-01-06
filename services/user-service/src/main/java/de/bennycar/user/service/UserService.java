package de.bennycar.user.service;

import de.bennycar.api.user.dto.response.UserProfileResponse;
import de.bennycar.user.exception.ResourceNotFoundException;
import de.bennycar.user.mapper.UserMapper;
import de.bennycar.user.domain.User;
import de.bennycar.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for user-related operations.
 * Handles user retrieval and profile management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Retrieves a user's profile by their ID.
     *
     * @param userId User's unique identifier
     * @return User profile information
     * @throws ResourceNotFoundException if user not found
     */
    public UserProfileResponse getUserProfile(UUID userId) {
        log.debug("Fetching user profile for userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

        return userMapper.toUserProfileResponse(user);
    }

    /**
     * Retrieves a user entity by ID.
     *
     * @param userId User's unique identifier
     * @return User entity
     * @throws ResourceNotFoundException if user not found
     */
    public User findById(UUID userId) {
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
        return userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    /**
     * Checks if a user exists with the given email.
     *
     * @param email Email address to check
     * @return true if user exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email.toLowerCase());
    }
}

