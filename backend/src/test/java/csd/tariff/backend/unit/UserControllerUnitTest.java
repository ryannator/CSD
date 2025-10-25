package csd.tariff.backend.unit;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import csd.tariff.backend.controller.UserController;
import csd.tariff.backend.model.User;
import csd.tariff.backend.service.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Unit Tests")
class UserControllerUnitTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    private UserController userController;

    private User testUser;
    private User testAdminUser;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
        
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setRole(User.Role.USER);

        testAdminUser = new User();
        testAdminUser.setUsername("admin");
        testAdminUser.setEmail("admin@example.com");
        testAdminUser.setPassword("admin123");
        testAdminUser.setRole(User.Role.ADMIN);
    }

    // ===== ADMIN ENDPOINTS TESTS =====

    @Test
    @DisplayName("Should get all users successfully")
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser, testAdminUser);
        when(userService.getAllUsers()).thenReturn(users);

        // Act
        ResponseEntity<List<UserController.UserSummary>> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        
        UserController.UserSummary firstUser = response.getBody().get(0);
        assertEquals(testUser.getUsername(), firstUser.username());
        assertEquals(testUser.getEmail(), firstUser.email());
        assertEquals(testUser.getRole(), firstUser.role());

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void getUserById_ShouldReturnUser() {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<UserController.UserSummary> response = userController.getUserById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testUser.getUsername(), response.getBody().username());
        assertEquals(testUser.getEmail(), response.getBody().email());
        assertEquals(testUser.getRole(), response.getBody().role());

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("Should return not found when user does not exist")
    void getUserById_ShouldReturnNotFound() {
        // Arrange
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<UserController.UserSummary> response = userController.getUserById(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    @DisplayName("Should create user successfully")
    void createUser_ShouldCreateUser() {
        // Arrange
        UserController.CreateUserRequest request = new UserController.CreateUserRequest(
            "newuser", "newuser@example.com", "password123", User.Role.USER
        );
        when(userService.createUser("newuser", "newuser@example.com", "password123", User.Role.USER))
            .thenReturn(testUser);

        // Act
        ResponseEntity<?> response = userController.createUser(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserController.UserSummary);
        
        UserController.UserSummary createdUser = (UserController.UserSummary) response.getBody();
        assertEquals(testUser.getUsername(), createdUser.username());
        assertEquals(testUser.getEmail(), createdUser.email());
        assertEquals(testUser.getRole(), createdUser.role());

        verify(userService, times(1)).createUser("newuser", "newuser@example.com", "password123", User.Role.USER);
    }

    @Test
    @DisplayName("Should return bad request when user creation fails")
    void createUser_ShouldReturnBadRequest_WhenCreationFails() {
        // Arrange
        UserController.CreateUserRequest request = new UserController.CreateUserRequest(
            "newuser", "newuser@example.com", "password123", User.Role.USER
        );
        when(userService.createUser(anyString(), anyString(), anyString(), any()))
            .thenThrow(new IllegalArgumentException("Username already exists"));

        // Act
        ResponseEntity<?> response = userController.createUser(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals("Username already exists", errorResponse.get("error"));

        verify(userService, times(1)).createUser("newuser", "newuser@example.com", "password123", User.Role.USER);
    }

    @Test
    @DisplayName("Should return internal server error when unexpected exception occurs")
    void createUser_ShouldReturnInternalServerError_WhenUnexpectedException() {
        // Arrange
        UserController.CreateUserRequest request = new UserController.CreateUserRequest(
            "newuser", "newuser@example.com", "password123", User.Role.USER
        );
        when(userService.createUser(anyString(), anyString(), anyString(), any()))
            .thenThrow(new RuntimeException("Database connection failed"));

        // Act
        ResponseEntity<?> response = userController.createUser(request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertTrue(errorResponse.get("error").contains("Failed to create user"));

        verify(userService, times(1)).createUser("newuser", "newuser@example.com", "password123", User.Role.USER);
    }

    @Test
    @DisplayName("Should update user with password successfully")
    void updateUser_ShouldUpdateUserWithPassword() {
        // Arrange
        UserController.UpdateUserRequest request = new UserController.UpdateUserRequest(
            "updateduser", "newpassword123", User.Role.ADMIN
        );
        when(userService.updateUserUsernameWithPassword(1L, "updateduser", "newpassword123", User.Role.ADMIN))
            .thenReturn(testUser);

        // Act
        ResponseEntity<?> response = userController.updateUser(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserController.UserSummary);

        verify(userService, times(1)).updateUserUsernameWithPassword(1L, "updateduser", "newpassword123", User.Role.ADMIN);
    }

    @Test
    @DisplayName("Should update user without password successfully")
    void updateUser_ShouldUpdateUserWithoutPassword() {
        // Arrange
        UserController.UpdateUserRequest request = new UserController.UpdateUserRequest(
            "updateduser", null, User.Role.ADMIN
        );
        when(userService.updateUserUsername(1L, "updateduser", User.Role.ADMIN))
            .thenReturn(testUser);

        // Act
        ResponseEntity<?> response = userController.updateUser(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserController.UserSummary);

        verify(userService, times(1)).updateUserUsername(1L, "updateduser", User.Role.ADMIN);
    }

    @Test
    @DisplayName("Should update user with empty password successfully")
    void updateUser_ShouldUpdateUserWithEmptyPassword() {
        // Arrange
        UserController.UpdateUserRequest request = new UserController.UpdateUserRequest(
            "updateduser", "", User.Role.ADMIN
        );
        when(userService.updateUserUsername(1L, "updateduser", User.Role.ADMIN))
            .thenReturn(testUser);

        // Act
        ResponseEntity<?> response = userController.updateUser(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserController.UserSummary);

        verify(userService, times(1)).updateUserUsername(1L, "updateduser", User.Role.ADMIN);
    }

    @Test
    @DisplayName("Should return bad request when user update fails")
    void updateUser_ShouldReturnBadRequest_WhenUpdateFails() {
        // Arrange
        UserController.UpdateUserRequest request = new UserController.UpdateUserRequest(
            "updateduser", "newpassword123", User.Role.ADMIN
        );
        when(userService.updateUserUsernameWithPassword(anyLong(), anyString(), anyString(), any()))
            .thenThrow(new IllegalArgumentException("User not found"));

        // Act
        ResponseEntity<?> response = userController.updateUser(1L, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals("User not found", errorResponse.get("error"));

        verify(userService, times(1)).updateUserUsernameWithPassword(1L, "updateduser", "newpassword123", User.Role.ADMIN);
    }

    @Test
    @DisplayName("Should delete user successfully")
    void deleteUser_ShouldDeleteUser() {
        // Arrange
        doNothing().when(userService).deleteUser(1L);

        // Act
        ResponseEntity<?> response = userController.deleteUser(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> successResponse = (Map<String, String>) response.getBody();
        assertEquals("User deleted successfully", successResponse.get("message"));

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent user")
    void deleteUser_ShouldReturnNotFound_WhenUserNotFound() {
        // Arrange
        doThrow(new IllegalArgumentException("User not found")).when(userService).deleteUser(999L);

        // Act
        ResponseEntity<?> response = userController.deleteUser(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals("User not found", errorResponse.get("error"));

        verify(userService, times(1)).deleteUser(999L);
    }

    // ===== PROFILE MANAGEMENT ENDPOINTS TESTS =====

    @Test
    @DisplayName("Should get current user successfully")
    void getCurrentUser_ShouldReturnCurrentUser() {
        // Arrange
        when(authentication.getName()).thenReturn("test@example.com");
        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<?> response = userController.getCurrentUser(authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserController.UserResponse);
        
        UserController.UserResponse userResponse = (UserController.UserResponse) response.getBody();
        assertEquals(testUser.getUsername(), userResponse.username());
        assertEquals(testUser.getEmail(), userResponse.email());
        assertEquals(testUser.getRole(), userResponse.role());

        verify(userService, times(1)).getUserByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should return not found when current user does not exist")
    void getCurrentUser_ShouldReturnNotFound_WhenUserNotFound() {
        // Arrange
        when(authentication.getName()).thenReturn("nonexistent@example.com");
        when(userService.getUserByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = userController.getCurrentUser(authentication);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals("User not found", errorResponse.get("error"));

        verify(userService, times(1)).getUserByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Should update profile successfully")
    void updateProfile_ShouldUpdateProfile() {
        // Arrange
        UserController.UpdateProfileRequest request = new UserController.UpdateProfileRequest("newusername");
        when(authentication.getName()).thenReturn("test@example.com");
        when(userService.updateProfile("test@example.com", "newusername")).thenReturn(testUser);

        // Act
        ResponseEntity<?> response = userController.updateProfile(authentication, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserController.UserResponse);

        verify(userService, times(1)).updateProfile("test@example.com", "newusername");
    }

    @Test
    @DisplayName("Should return bad request when profile update fails")
    void updateProfile_ShouldReturnBadRequest_WhenUpdateFails() {
        // Arrange
        UserController.UpdateProfileRequest request = new UserController.UpdateProfileRequest("newusername");
        when(authentication.getName()).thenReturn("test@example.com");
        when(userService.updateProfile("test@example.com", "newusername"))
            .thenThrow(new IllegalArgumentException("Username already exists"));

        // Act
        ResponseEntity<?> response = userController.updateProfile(authentication, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals("Username already exists", errorResponse.get("error"));

        verify(userService, times(1)).updateProfile("test@example.com", "newusername");
    }

    @Test
    @DisplayName("Should change password successfully")
    void changePassword_ShouldChangePassword() {
        // Arrange
        UserController.ChangePasswordRequest request = new UserController.ChangePasswordRequest(
            "oldpassword", "newpassword123"
        );
        when(authentication.getName()).thenReturn("test@example.com");
        doNothing().when(userService).changePassword("test@example.com", "oldpassword", "newpassword123");

        // Act
        ResponseEntity<?> response = userController.changePassword(authentication, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> successResponse = (Map<String, String>) response.getBody();
        assertEquals("Password updated successfully", successResponse.get("message"));

        verify(userService, times(1)).changePassword("test@example.com", "oldpassword", "newpassword123");
    }

    @Test
    @DisplayName("Should return bad request when password change fails")
    void changePassword_ShouldReturnBadRequest_WhenChangeFails() {
        // Arrange
        UserController.ChangePasswordRequest request = new UserController.ChangePasswordRequest(
            "wrongpassword", "newpassword123"
        );
        when(authentication.getName()).thenReturn("test@example.com");
        doThrow(new IllegalArgumentException("Current password is incorrect"))
            .when(userService).changePassword("test@example.com", "wrongpassword", "newpassword123");

        // Act
        ResponseEntity<?> response = userController.changePassword(authentication, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertEquals("Current password is incorrect", errorResponse.get("error"));

        verify(userService, times(1)).changePassword("test@example.com", "wrongpassword", "newpassword123");
    }

    @Test
    @DisplayName("Should delete account successfully")
    void deleteAccount_ShouldDeleteAccount() {
        // Arrange
        when(authentication.getName()).thenReturn("test@example.com");
        doNothing().when(userService).deleteAccount("test@example.com");

        // Act
        ResponseEntity<?> response = userController.deleteAccount(authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> successResponse = (Map<String, String>) response.getBody();
        assertEquals("Account deleted successfully", successResponse.get("message"));

        verify(userService, times(1)).deleteAccount("test@example.com");
    }

    @Test
    @DisplayName("Should return internal server error when account deletion fails")
    void deleteAccount_ShouldReturnInternalServerError_WhenDeletionFails() {
        // Arrange
        when(authentication.getName()).thenReturn("test@example.com");
        doThrow(new RuntimeException("Database error")).when(userService).deleteAccount("test@example.com");

        // Act
        ResponseEntity<?> response = userController.deleteAccount(authentication);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertTrue(errorResponse.get("error").contains("Failed to delete account"));

        verify(userService, times(1)).deleteAccount("test@example.com");
    }

    // ===== ADDITIONAL EDGE CASE TESTS =====

    @Test
    @DisplayName("Should return internal server error when user update fails with unexpected exception")
    void updateUser_ShouldReturnInternalServerError_WhenUnexpectedException() {
        // Arrange
        UserController.UpdateUserRequest request = new UserController.UpdateUserRequest(
            "updateduser", "newpassword123", User.Role.ADMIN
        );
        when(userService.updateUserUsernameWithPassword(anyLong(), anyString(), anyString(), any()))
            .thenThrow(new RuntimeException("Database connection failed"));

        // Act
        ResponseEntity<?> response = userController.updateUser(1L, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertTrue(errorResponse.get("error").contains("Failed to update user"));

        verify(userService, times(1)).updateUserUsernameWithPassword(1L, "updateduser", "newpassword123", User.Role.ADMIN);
    }

    @Test
    @DisplayName("Should return internal server error when user deletion fails with unexpected exception")
    void deleteUser_ShouldReturnInternalServerError_WhenUnexpectedException() {
        // Arrange
        doThrow(new RuntimeException("Database connection failed")).when(userService).deleteUser(1L);

        // Act
        ResponseEntity<?> response = userController.deleteUser(1L);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertTrue(errorResponse.get("error").contains("Failed to delete user"));

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @DisplayName("Should return internal server error when profile update fails with unexpected exception")
    void updateProfile_ShouldReturnInternalServerError_WhenUnexpectedException() {
        // Arrange
        UserController.UpdateProfileRequest request = new UserController.UpdateProfileRequest("newusername");
        when(authentication.getName()).thenReturn("test@example.com");
        when(userService.updateProfile("test@example.com", "newusername"))
            .thenThrow(new RuntimeException("Database connection failed"));

        // Act
        ResponseEntity<?> response = userController.updateProfile(authentication, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertTrue(errorResponse.get("error").contains("Failed to update profile"));

        verify(userService, times(1)).updateProfile("test@example.com", "newusername");
    }

    @Test
    @DisplayName("Should return internal server error when password change fails with unexpected exception")
    void changePassword_ShouldReturnInternalServerError_WhenUnexpectedException() {
        // Arrange
        UserController.ChangePasswordRequest request = new UserController.ChangePasswordRequest(
            "oldpassword", "newpassword123"
        );
        when(authentication.getName()).thenReturn("test@example.com");
        doThrow(new RuntimeException("Database connection failed"))
            .when(userService).changePassword("test@example.com", "oldpassword", "newpassword123");

        // Act
        ResponseEntity<?> response = userController.changePassword(authentication, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertTrue(errorResponse.get("error").contains("Failed to change password"));

        verify(userService, times(1)).changePassword("test@example.com", "oldpassword", "newpassword123");
    }

    @Test
    @DisplayName("Should handle updateUser with whitespace-only password")
    void updateUser_ShouldHandleWhitespaceOnlyPassword() {
        // Arrange
        UserController.UpdateUserRequest request = new UserController.UpdateUserRequest(
            "updateduser", "   ", User.Role.ADMIN
        );
        when(userService.updateUserUsername(1L, "updateduser", User.Role.ADMIN))
            .thenReturn(testUser);

        // Act
        ResponseEntity<?> response = userController.updateUser(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserController.UserSummary);

        verify(userService, times(1)).updateUserUsername(1L, "updateduser", User.Role.ADMIN);
    }

    @Test
    @DisplayName("Should handle updateUser with tab-only password")
    void updateUser_ShouldHandleTabOnlyPassword() {
        // Arrange
        UserController.UpdateUserRequest request = new UserController.UpdateUserRequest(
            "updateduser", "\t\t", User.Role.ADMIN
        );
        when(userService.updateUserUsername(1L, "updateduser", User.Role.ADMIN))
            .thenReturn(testUser);

        // Act
        ResponseEntity<?> response = userController.updateUser(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserController.UserSummary);

        verify(userService, times(1)).updateUserUsername(1L, "updateduser", User.Role.ADMIN);
    }

    @Test
    @DisplayName("Should handle updateUser with newline-only password")
    void updateUser_ShouldHandleNewlineOnlyPassword() {
        // Arrange
        UserController.UpdateUserRequest request = new UserController.UpdateUserRequest(
            "updateduser", "\n\n", User.Role.ADMIN
        );
        when(userService.updateUserUsername(1L, "updateduser", User.Role.ADMIN))
            .thenReturn(testUser);

        // Act
        ResponseEntity<?> response = userController.updateUser(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserController.UserSummary);

        verify(userService, times(1)).updateUserUsername(1L, "updateduser", User.Role.ADMIN);
    }

    @Test
    @DisplayName("Should handle updateUser with mixed whitespace password")
    void updateUser_ShouldHandleMixedWhitespacePassword() {
        // Arrange
        UserController.UpdateUserRequest request = new UserController.UpdateUserRequest(
            "updateduser", " \t\n ", User.Role.ADMIN
        );
        when(userService.updateUserUsername(1L, "updateduser", User.Role.ADMIN))
            .thenReturn(testUser);

        // Act
        ResponseEntity<?> response = userController.updateUser(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserController.UserSummary);

        verify(userService, times(1)).updateUserUsername(1L, "updateduser", User.Role.ADMIN);
    }

    @Test
    @DisplayName("Should handle updateUser with valid password containing whitespace")
    void updateUser_ShouldHandleValidPasswordWithWhitespace() {
        // Arrange
        UserController.UpdateUserRequest request = new UserController.UpdateUserRequest(
            "updateduser", " valid password ", User.Role.ADMIN
        );
        when(userService.updateUserUsernameWithPassword(1L, "updateduser", " valid password ", User.Role.ADMIN))
            .thenReturn(testUser);

        // Act
        ResponseEntity<?> response = userController.updateUser(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof UserController.UserSummary);

        verify(userService, times(1)).updateUserUsernameWithPassword(1L, "updateduser", " valid password ", User.Role.ADMIN);
    }
}
