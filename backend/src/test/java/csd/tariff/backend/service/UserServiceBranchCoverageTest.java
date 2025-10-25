package csd.tariff.backend.service;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import csd.tariff.backend.model.User;
import csd.tariff.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Branch Coverage Tests")
class UserServiceBranchCoverageTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
        
        testUser = new User();
        setUserId(testUser, 1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(User.Role.USER);
    }

    private void setUserId(User user, Long id) {
        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set User ID", e);
        }
    }

    @Nested
    @DisplayName("createUser Branch Coverage")
    class CreateUserBranchCoverage {

        @Test
        @DisplayName("Should throw exception when email already exists")
        void createUser_ShouldThrowExceptionWhenEmailAlreadyExists() {
            // Arrange
            when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(testUser));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser("newuser", "existing@example.com", "password", User.Role.USER));
            
            assertEquals("User with email existing@example.com already exists", exception.getMessage());
            verify(userRepository, never()).findByUsername(any());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when username already exists")
        void createUser_ShouldThrowExceptionWhenUsernameAlreadyExists() {
            // Arrange
            when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
            when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(testUser));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser("existinguser", "new@example.com", "password", User.Role.USER));
            
            assertEquals("User with username existinguser already exists", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should create user successfully when both email and username are unique")
        void createUser_ShouldCreateUserSuccessfullyWhenBothEmailAndUsernameAreUnique() {
            // Arrange
            when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
            when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            User result = userService.createUser("newuser", "new@example.com", "password", User.Role.USER);

            // Assert
            assertNotNull(result);
            assertEquals("newuser", result.getUsername());
            assertEquals("new@example.com", result.getEmail());
            assertEquals("encodedPassword", result.getPassword());
            assertEquals(User.Role.USER, result.getRole());
            verify(userRepository).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("updateUserUsername Branch Coverage")
    class UpdateUserUsernameBranchCoverage {

        @Test
        @DisplayName("Should throw exception when user not found")
        void updateUserUsername_ShouldThrowExceptionWhenUserNotFound() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUserUsername(999L, "newusername", User.Role.USER));
            
            assertEquals("User with id 999 not found", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when username already exists for different user")
        void updateUserUsername_ShouldThrowExceptionWhenUsernameExistsForDifferentUser() {
            // Arrange
            User differentUser = new User();
            setUserId(differentUser, 2L);
            differentUser.setUsername("differentuser");
            
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(differentUser));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUserUsername(1L, "existinguser", User.Role.USER));
            
            assertEquals("Username existinguser already exists", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should allow updating to same username")
        void updateUserUsername_ShouldAllowUpdatingToSameUsername() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            User result = userService.updateUserUsername(1L, "testuser", User.Role.ADMIN);

            // Assert
            assertNotNull(result);
            assertEquals("testuser", result.getUsername());
            assertEquals(User.Role.ADMIN, result.getRole());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should update username successfully when username is available")
        void updateUserUsername_ShouldUpdateUsernameSuccessfullyWhenUsernameIsAvailable() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.findByUsername("newusername")).thenReturn(Optional.empty());
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            User result = userService.updateUserUsername(1L, "newusername", User.Role.ADMIN);

            // Assert
            assertNotNull(result);
            assertEquals("newusername", result.getUsername());
            assertEquals(User.Role.ADMIN, result.getRole());
            verify(userRepository).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("updateUserUsernameWithPassword Branch Coverage")
    class UpdateUserUsernameWithPasswordBranchCoverage {

        @Test
        @DisplayName("Should throw exception when user not found")
        void updateUserUsernameWithPassword_ShouldThrowExceptionWhenUserNotFound() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUserUsernameWithPassword(999L, "newusername", "newpassword", User.Role.USER));
            
            assertEquals("User with id 999 not found", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when username already exists for different user")
        void updateUserUsernameWithPassword_ShouldThrowExceptionWhenUsernameExistsForDifferentUser() {
            // Arrange
            User differentUser = new User();
            setUserId(differentUser, 2L);
            differentUser.setUsername("differentuser");
            
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(differentUser));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUserUsernameWithPassword(1L, "existinguser", "newpassword", User.Role.USER));
            
            assertEquals("Username existinguser already exists", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should allow updating to same username with new password")
        void updateUserUsernameWithPassword_ShouldAllowUpdatingToSameUsernameWithNewPassword() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.encode("newpassword")).thenReturn("newEncodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            User result = userService.updateUserUsernameWithPassword(1L, "testuser", "newpassword", User.Role.ADMIN);

            // Assert
            assertNotNull(result);
            assertEquals("testuser", result.getUsername());
            assertEquals("newEncodedPassword", result.getPassword());
            assertEquals(User.Role.ADMIN, result.getRole());
            verify(userRepository).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("updateProfile Branch Coverage")
    class UpdateProfileBranchCoverage {

        @Test
        @DisplayName("Should throw exception when user not found by email")
        void updateProfile_ShouldThrowExceptionWhenUserNotFoundByEmail() {
            // Arrange
            when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateProfile("nonexistent@example.com", "newusername"));
            
            assertEquals("User with email nonexistent@example.com not found", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when username already exists for different user")
        void updateProfile_ShouldThrowExceptionWhenUsernameExistsForDifferentUser() {
            // Arrange
            User differentUser = new User();
            setUserId(differentUser, 2L);
            differentUser.setUsername("differentuser");
            
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(differentUser));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateProfile("test@example.com", "existinguser"));
            
            assertEquals("Username existinguser already exists", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should allow updating to same username")
        void updateProfile_ShouldAllowUpdatingToSameUsername() {
            // Arrange
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            User result = userService.updateProfile("test@example.com", "testuser");

            // Assert
            assertNotNull(result);
            assertEquals("testuser", result.getUsername());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should update profile successfully when username is available")
        void updateProfile_ShouldUpdateProfileSuccessfullyWhenUsernameIsAvailable() {
            // Arrange
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(userRepository.findByUsername("newusername")).thenReturn(Optional.empty());
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            User result = userService.updateProfile("test@example.com", "newusername");

            // Assert
            assertNotNull(result);
            assertEquals("newusername", result.getUsername());
            verify(userRepository).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("changePassword Branch Coverage")
    class ChangePasswordBranchCoverage {

        @Test
        @DisplayName("Should throw exception when user not found by email")
        void changePassword_ShouldThrowExceptionWhenUserNotFoundByEmail() {
            // Arrange
            when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.changePassword("nonexistent@example.com", "current", "new"));
            
            assertEquals("User with email nonexistent@example.com not found", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when current password is incorrect")
        void changePassword_ShouldThrowExceptionWhenCurrentPasswordIsIncorrect() {
            // Arrange
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.changePassword("test@example.com", "wrongpassword", "newpassword"));
            
            assertEquals("Current password is incorrect", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should change password successfully when current password is correct")
        void changePassword_ShouldChangePasswordSuccessfullyWhenCurrentPasswordIsCorrect() {
            // Arrange
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("currentpassword", "encodedPassword")).thenReturn(true);
            when(passwordEncoder.encode("newpassword")).thenReturn("newEncodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            userService.changePassword("test@example.com", "currentpassword", "newpassword");

            // Assert
            verify(passwordEncoder).matches("currentpassword", "encodedPassword");
            verify(passwordEncoder).encode("newpassword");
            verify(userRepository).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("deleteUser Branch Coverage")
    class DeleteUserBranchCoverage {

        @Test
        @DisplayName("Should throw exception when user does not exist")
        void deleteUser_ShouldThrowExceptionWhenUserDoesNotExist() {
            // Arrange
            when(userRepository.existsById(999L)).thenReturn(false);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.deleteUser(999L));
            
            assertEquals("User with id 999 not found", exception.getMessage());
            verify(userRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Should delete user successfully when user exists")
        void deleteUser_ShouldDeleteUserSuccessfullyWhenUserExists() {
            // Arrange
            when(userRepository.existsById(1L)).thenReturn(true);

            // Act
            userService.deleteUser(1L);

            // Assert
            verify(userRepository).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("deleteAccount Branch Coverage")
    class DeleteAccountBranchCoverage {

        @Test
        @DisplayName("Should throw exception when user not found by email")
        void deleteAccount_ShouldThrowExceptionWhenUserNotFoundByEmail() {
            // Arrange
            when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.deleteAccount("nonexistent@example.com"));
            
            assertEquals("User with email nonexistent@example.com not found", exception.getMessage());
            verify(userRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Should delete account successfully when user exists")
        void deleteAccount_ShouldDeleteAccountSuccessfullyWhenUserExists() {
            // Arrange
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            // Act
            userService.deleteAccount("test@example.com");

            // Assert
            verify(userRepository).delete(testUser);
        }
    }
}
