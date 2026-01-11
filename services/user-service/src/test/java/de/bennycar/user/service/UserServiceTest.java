package de.bennycar.user.service;

import de.bennycar.api.user.dto.request.ChangePasswordRequest;
import de.bennycar.api.user.dto.request.UpdateUserProfileRequest;
import de.bennycar.api.user.dto.response.UserProfileResponse;
import de.bennycar.user.domain.RefreshToken;
import de.bennycar.user.domain.User;
import de.bennycar.user.exception.InvalidCredentialsException;
import de.bennycar.user.exception.ResourceNotFoundException;
import de.bennycar.user.mapper.UserMapper;
import de.bennycar.user.repository.RefreshTokenRepository;
import de.bennycar.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for UserService.
 * Tests all user-related operations including profile management, password changes, and account deletion.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UUID testUserId;
    private UserProfileResponse testUserProfileResponse;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = User.builder()
                .id(testUserId)
                .email("test@example.com")
                .passwordHash("$2a$12$hashedPassword")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+1234567890")
                .address("123 Main St")
                .status("ACTIVE")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        testUserProfileResponse = UserProfileResponse.builder()
                .userId(testUserId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+1234567890")
                .address("123 Main St")
                .build();
    }

    @Test
    @DisplayName("Should retrieve user profile successfully")
    void getUserProfile_shouldReturnUserProfile_whenUserExists() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userMapper.toUserProfileResponse(testUser)).thenReturn(testUserProfileResponse);

        // When
        UserProfileResponse result = userService.getUserProfile(testUserId);

        // Then
        assertNotNull(result);
        assertEquals(testUserId, result.getUserId());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(testUserId);
        verify(userMapper, times(1)).toUserProfileResponse(testUser);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found")
    void getUserProfile_shouldThrowException_whenUserNotFound() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserProfile(testUserId)
        );

        assertTrue(exception.getMessage().contains("User"));
        assertTrue(exception.getMessage().contains(testUserId.toString()));
        verify(userRepository, times(1)).findById(testUserId);
        verify(userMapper, never()).toUserProfileResponse(any());
    }

    @Test
    @DisplayName("Should update user profile with all fields")
    void updateProfile_shouldUpdateAllFields_whenAllFieldsProvided() {
        // Given
        UpdateUserProfileRequest request = UpdateUserProfileRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .phoneNumber("+9876543210")
                .profilePictureUrl("https://example.com/photo.jpg")
                .address("456 Oak Ave")
                .build();

        User updatedUser = User.builder()
                .id(testUserId)
                .email("test@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .phoneNumber("+9876543210")
                .profilePictureUrl("https://example.com/photo.jpg")
                .address("456 Oak Ave")
                .build();

        UserProfileResponse updatedResponse = UserProfileResponse.builder()
                .userId(testUserId)
                .firstName("Jane")
                .lastName("Smith")
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toUserProfileResponse(updatedUser)).thenReturn(updatedResponse);

        // When
        UserProfileResponse result = userService.updateProfile(testUserId, request);

        // Then
        assertNotNull(result);
        verify(userRepository, times(1)).findById(testUserId);
        verify(userRepository, times(1)).save(testUser);
        verify(userMapper, times(1)).toUserProfileResponse(updatedUser);
    }

    @Test
    @DisplayName("Should update profile with partial fields")
    void updateProfile_shouldUpdatePartialFields_whenOnlySomeFieldsProvided() {
        // Given
        UpdateUserProfileRequest request = UpdateUserProfileRequest.builder()
                .firstName("Jane")
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toUserProfileResponse(testUser)).thenReturn(testUserProfileResponse);

        // When
        UserProfileResponse result = userService.updateProfile(testUserId, request);

        // Then
        assertNotNull(result);
        assertEquals("Jane", testUser.getFirstName());
        assertEquals("Doe", testUser.getLastName()); // Unchanged
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent user")
    void updateProfile_shouldThrowException_whenUserNotFound() {
        // Given
        UpdateUserProfileRequest request = UpdateUserProfileRequest.builder()
                .firstName("Jane")
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateProfile(testUserId, request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should change password successfully with correct current password")
    void changePassword_shouldSucceed_whenCurrentPasswordIsCorrect() {
        // Given
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("oldPassword123")
                .newPassword("newPassword456")
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword123", testUser.getPasswordHash())).thenReturn(true);
        when(passwordEncoder.encode("newPassword456")).thenReturn("$2a$12$newHashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        assertDoesNotThrow(() -> userService.changePassword(testUserId, request));

        // Then
        verify(passwordEncoder, times(1)).matches("oldPassword123", testUser.getPasswordHash());
        verify(passwordEncoder, times(1)).encode("newPassword456");
        verify(userRepository, times(1)).save(testUser);
        assertEquals("$2a$12$newHashedPassword", testUser.getPasswordHash());
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when current password is incorrect")
    void changePassword_shouldThrowException_whenCurrentPasswordIsIncorrect() {
        // Given
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("wrongPassword")
                .newPassword("newPassword456")
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", testUser.getPasswordHash())).thenReturn(false);

        // When & Then
        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userService.changePassword(testUserId, request)
        );

        assertEquals("Current password is incorrect", exception.getMessage());
        verify(passwordEncoder, times(1)).matches("wrongPassword", testUser.getPasswordHash());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when changing password for non-existent user")
    void changePassword_shouldThrowException_whenUserNotFound() {
        // Given
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("oldPassword123")
                .newPassword("newPassword456")
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> userService.changePassword(testUserId, request));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should delete account and revoke all refresh tokens")
    void deleteAccount_shouldDeleteUserAndRevokeTokens_whenUserExists() {
        // Given
        RefreshToken token1 = RefreshToken.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .revoked(false)
                .build();
        RefreshToken token2 = RefreshToken.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .revoked(false)
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.findAllByUserIdAndRevokedFalse(testUserId))
                .thenReturn(Arrays.asList(token1, token2));

        // When
        assertDoesNotThrow(() -> userService.deleteAccount(testUserId));

        // Then
        assertTrue(token1.isRevoked());
        assertTrue(token2.isRevoked());
        assertNotNull(token1.getRevokedAt());
        assertNotNull(token2.getRevokedAt());
        verify(refreshTokenRepository, times(1)).findAllByUserIdAndRevokedFalse(testUserId);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    @DisplayName("Should delete account even when no refresh tokens exist")
    void deleteAccount_shouldDeleteUser_whenNoRefreshTokensExist() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.findAllByUserIdAndRevokedFalse(testUserId))
                .thenReturn(Collections.emptyList());

        // When
        assertDoesNotThrow(() -> userService.deleteAccount(testUserId));

        // Then
        verify(refreshTokenRepository, times(1)).findAllByUserIdAndRevokedFalse(testUserId);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent user")
    void deleteAccount_shouldThrowException_whenUserNotFound() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> userService.deleteAccount(testUserId));
        verify(refreshTokenRepository, never()).findAllByUserIdAndRevokedFalse(any());
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should find user by ID successfully")
    void findById_shouldReturnUser_whenUserExists() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.findById(testUserId);

        // Then
        assertNotNull(result);
        assertEquals(testUserId, result.getId());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(testUserId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found by ID")
    void findById_shouldThrowException_whenUserNotFound() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findById(testUserId)
        );

        assertTrue(exception.getMessage().contains("User"));
        assertTrue(exception.getMessage().contains(testUserId.toString()));
    }

    @Test
    @DisplayName("Should throw NullPointerException when userId is null")
    void findById_shouldThrowException_whenUserIdIsNull() {
        // When & Then
        assertThrows(NullPointerException.class,
                () -> userService.findById(null));
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should find user by email successfully")
    void findByEmail_shouldReturnUser_whenUserExists() {
        // Given
        String email = "test@example.com";
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(testUser));

        // When
        User result = userService.findByEmail(email);

        // Then
        assertNotNull(result);
        assertEquals(email.toLowerCase(), result.getEmail());
        verify(userRepository, times(1)).findByEmail(email.toLowerCase());
    }

    @Test
    @DisplayName("Should normalize email when finding by email")
    void findByEmail_shouldNormalizeEmail() {
        // Given
        String email = "  TEST@EXAMPLE.COM  ";
        String normalizedEmail = "test@example.com";
        when(userRepository.findByEmail(normalizedEmail)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.findByEmail(email);

        // Then
        assertNotNull(result);
        verify(userRepository, times(1)).findByEmail(normalizedEmail);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found by email")
    void findByEmail_shouldThrowException_whenUserNotFound() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findByEmail(email)
        );

        assertTrue(exception.getMessage().contains("User"));
        assertTrue(exception.getMessage().contains(email));
    }

    @Test
    @DisplayName("Should return true when user exists by email")
    void existsByEmail_shouldReturnTrue_whenUserExists() {
        // Given
        String email = "test@example.com";
        when(userRepository.existsByEmail(email.toLowerCase())).thenReturn(true);

        // When
        boolean result = userService.existsByEmail(email);

        // Then
        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail(email.toLowerCase());
    }

    @Test
    @DisplayName("Should return false when user does not exist by email")
    void existsByEmail_shouldReturnFalse_whenUserDoesNotExist() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.existsByEmail(email.toLowerCase())).thenReturn(false);

        // When
        boolean result = userService.existsByEmail(email);

        // Then
        assertFalse(result);
        verify(userRepository, times(1)).existsByEmail(email.toLowerCase());
    }

    @Test
    @DisplayName("Should normalize email when checking existence")
    void existsByEmail_shouldNormalizeEmail() {
        // Given
        String email = "  TEST@EXAMPLE.COM  ";
        String normalizedEmail = "test@example.com";
        when(userRepository.existsByEmail(normalizedEmail)).thenReturn(true);

        // When
        boolean result = userService.existsByEmail(email);

        // Then
        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail(normalizedEmail);
    }

    @Test
    @DisplayName("Should handle null fields in update request gracefully")
    void updateProfile_shouldHandleNullFields() {
        // Given
        UpdateUserProfileRequest request = UpdateUserProfileRequest.builder()
                .firstName(null)
                .lastName(null)
                .phoneNumber(null)
                .profilePictureUrl(null)
                .address(null)
                .build();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toUserProfileResponse(testUser)).thenReturn(testUserProfileResponse);

        // When
        UserProfileResponse result = userService.updateProfile(testUserId, request);

        // Then
        assertNotNull(result);
        // Original values should remain unchanged
        assertEquals("John", testUser.getFirstName());
        assertEquals("Doe", testUser.getLastName());
        verify(userRepository, times(1)).save(testUser);
    }
}
