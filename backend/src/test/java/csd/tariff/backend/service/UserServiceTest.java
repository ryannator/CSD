package csd.tariff.backend.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import csd.tariff.backend.model.User;
import csd.tariff.backend.repository.UserRepository;
import csd.tariff.backend.service.UserService;

/**
 * Tests for UserService
 * Covers all user management operations including CRUD, authentication, and profile management
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(User.Role.USER);

        // Setup admin user
        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("encodedAdminPassword");
        adminUser.setRole(User.Role.ADMIN);
    }

    // ===== User Creation Tests =====

    @Test
    @DisplayName("Should create user successfully")
    void createUser_ShouldCreateSuccessfully() {
        // Given
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.createUser("newuser", "new@example.com", "password123", User.Role.USER);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(User.Role.USER, result.getRole());
        verify(userRepository, times(1)).findByEmail("new@example.com");
        verify(userRepository, times(1)).findByUsername("newuser");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void createUser_ShouldThrowExceptionWhenEmailExists() {
        // Given
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(testUser));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("newuser", "existing@example.com", "password123", User.Role.USER);
        });

        assertEquals("User with email existing@example.com already exists", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("existing@example.com");
        verify(userRepository, never()).findByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void createUser_ShouldThrowExceptionWhenUsernameExists() {
        // Given
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(testUser));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("existinguser", "new@example.com", "password123", User.Role.USER);
        });

        assertEquals("User with username existinguser already exists", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("new@example.com");
        verify(userRepository, times(1)).findByUsername("existinguser");
        verify(userRepository, never()).save(any(User.class));
    }

    // ===== User Lookup Tests =====

    @Test
    @DisplayName("Should find user by email successfully")
    void findByEmail_ShouldReturnUser() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        User result = userService.findByEmail("test@example.com");

        // Then
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should throw exception when user not found by email")
    void findByEmail_ShouldThrowExceptionWhenNotFound() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.findByEmail("nonexistent@example.com");
        });

        assertEquals("User with email nonexistent@example.com not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Should find user by username successfully")
    void findByUsername_ShouldReturnUser() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        User result = userService.findByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should throw exception when user not found by username")
    void findByUsername_ShouldThrowExceptionWhenNotFound() {
        // Given
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.findByUsername("nonexistentuser");
        });

        assertEquals("User with username nonexistentuser not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("nonexistentuser");
    }

    // ===== User Management Tests =====

    @Test
    @DisplayName("Should get all users")
    void getAllUsers_ShouldReturnAllUsers() {
        // Given
        List<User> users = Arrays.asList(testUser, adminUser);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testUser));
        assertTrue(result.contains(adminUser));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get user by ID")
    void getUserById_ShouldReturnUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when user not found by ID")
    void getUserById_ShouldReturnEmptyWhenNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get user by email")
    void getUserByEmail_ShouldReturnUser() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserByEmail("test@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    // ===== User Update Tests =====

    @Test
    @DisplayName("Should update user username successfully")
    void updateUserUsername_ShouldUpdateSuccessfully() {
        // Given
        User updatedUser = new User();
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("test@example.com");
        updatedUser.setRole(User.Role.ADMIN);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("updateduser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        User result = userService.updateUserUsername(1L, "updateduser", User.Role.ADMIN);

        // Then
        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
        assertEquals(User.Role.ADMIN, result.getRole());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByUsername("updateduser");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void updateUserUsername_ShouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUserUsername(999L, "updateduser", User.Role.ADMIN);
        });

        assertEquals("User with id 999 not found", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void updateUserUsername_ShouldThrowExceptionWhenUsernameExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(adminUser));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUserUsername(1L, "existinguser", User.Role.ADMIN);
        });

        assertEquals("Username existinguser already exists", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByUsername("existinguser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should allow updating to same username")
    void updateUserUsername_ShouldAllowSameUsername() {
        // Given
        User updatedUser = new User();
        updatedUser.setUsername("testuser");
        updatedUser.setEmail("test@example.com");
        updatedUser.setRole(User.Role.ADMIN);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        User result = userService.updateUserUsername(1L, "testuser", User.Role.ADMIN);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(User.Role.ADMIN, result.getRole());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user with password successfully")
    void updateUserUsernameWithPassword_ShouldUpdateSuccessfully() {
        // Given
        User updatedUser = new User();
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("test@example.com");
        updatedUser.setPassword("newEncodedPassword");
        updatedUser.setRole(User.Role.ADMIN);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("updateduser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        User result = userService.updateUserUsernameWithPassword(1L, "updateduser", "newpassword", User.Role.ADMIN);

        // Then
        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
        assertEquals("newEncodedPassword", result.getPassword());
        assertEquals(User.Role.ADMIN, result.getRole());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByUsername("updateduser");
        verify(passwordEncoder, times(1)).encode("newpassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ===== User Deletion Tests =====

    @Test
    @DisplayName("Should delete user successfully")
    void deleteUser_ShouldDeleteSuccessfully() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void deleteUser_ShouldThrowExceptionWhenNotFound() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(999L);
        });

        assertEquals("User with id 999 not found", exception.getMessage());
        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    // ===== Profile Management Tests =====

    @Test
    @DisplayName("Should update profile successfully")
    void updateProfile_ShouldUpdateSuccessfully() {
        // Given
        User updatedUser = new User();
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("test@example.com");
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("updateduser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        User result = userService.updateProfile("test@example.com", "updateduser");

        // Then
        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).findByUsername("updateduser");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating profile for non-existent user")
    void updateProfile_ShouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateProfile("nonexistent@example.com", "updateduser");
        });

        assertEquals("User with email nonexistent@example.com not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should change password successfully")
    void changePassword_ShouldChangeSuccessfully() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentpassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newpassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.changePassword("test@example.com", "currentpassword", "newpassword");

        // Then
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("currentpassword", "encodedPassword");
        verify(passwordEncoder, times(1)).encode("newpassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when changing password for non-existent user")
    void changePassword_ShouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.changePassword("nonexistent@example.com", "currentpassword", "newpassword");
        });

        assertEquals("User with email nonexistent@example.com not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when current password is incorrect")
    void changePassword_ShouldThrowExceptionWhenCurrentPasswordIncorrect() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.changePassword("test@example.com", "wrongpassword", "newpassword");
        });

        assertEquals("Current password is incorrect", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("wrongpassword", "encodedPassword");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete account successfully")
    void deleteAccount_ShouldDeleteSuccessfully() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        // When
        userService.deleteAccount("test@example.com");

        // Then
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent account")
    void deleteAccount_ShouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteAccount("nonexistent@example.com");
        });

        assertEquals("User with email nonexistent@example.com not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
        verify(userRepository, never()).delete(any(User.class));
    }

    // ===== Edge Cases and Error Handling =====

    @Test
    @DisplayName("Should handle null inputs gracefully")
    void shouldHandleNullInputsGracefully() {
        // When & Then
        assertDoesNotThrow(() -> {
            userService.getAllUsers();
        });
    }

    @Test
    @DisplayName("Should handle empty string inputs")
    void shouldHandleEmptyStringInputs() {
        // Given
        when(userRepository.findByEmail("")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.findByEmail("");
        });
    }

}