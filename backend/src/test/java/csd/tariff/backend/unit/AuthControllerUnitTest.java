package csd.tariff.backend.unit;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import csd.tariff.backend.controller.AuthController;
import csd.tariff.backend.dto.RegistrationDTOs;
import csd.tariff.backend.model.User;
import csd.tariff.backend.service.JwtService;
import csd.tariff.backend.service.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerUnitTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private User testUser;
    private RegistrationDTOs.SigninRequest signinRequest;
    private RegistrationDTOs.RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(User.Role.USER);

        signinRequest = new RegistrationDTOs.SigninRequest("test@example.com", "password");
        registerRequest = new RegistrationDTOs.RegisterRequest("testuser", "test@example.com", "password");
    }

    @Test
    @DisplayName("Signin - Should return JWT token for valid credentials")
    void signin_ShouldReturnJwtToken_ForValidCredentials() {
        // Arrange
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getAuthorities()).thenReturn((Collection) Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(jwtService.generate(any(), any())).thenReturn("mock-jwt-token");

        // Act
        ResponseEntity<?> response = authController.signin(signinRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        if (response.getBody() instanceof RegistrationDTOs.SigninResponse signinResponse) {
            assertEquals("mock-jwt-token", signinResponse.token());
        }
    }

    @Test
    @DisplayName("Signin - Should return unauthorized for invalid credentials")
    void signin_ShouldReturnUnauthorized_ForInvalidCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.core.AuthenticationException("Invalid credentials") {});

        // Act
        ResponseEntity<?> response = authController.signin(signinRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Register - Should create new user successfully")
    void register_ShouldCreateNewUser_ForValidRequest() {
        // Arrange
        when(userService.createUser("testuser", "test@example.com", "password", User.Role.USER)).thenReturn(testUser);

        // Act
        ResponseEntity<?> response = authController.register(registerRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userService).createUser("testuser", "test@example.com", "password", User.Role.USER);
    }

    @Test
    @DisplayName("Register - Should return bad request when email already exists")
    void register_ShouldReturnBadRequest_WhenEmailExists() {
        // Arrange
        when(userService.createUser("testuser", "test@example.com", "password", User.Role.USER))
                .thenThrow(new IllegalArgumentException("User with email test@example.com already exists"));

        // Act
        ResponseEntity<?> response = authController.register(registerRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService).createUser("testuser", "test@example.com", "password", User.Role.USER);
    }

    @Test
    @DisplayName("Logout - Should return success message")
    void logout_ShouldReturnSuccessMessage() {
        // Act
        ResponseEntity<?> response = authController.logout();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // The response might be a Map with a message key
        if (response.getBody() instanceof java.util.Map<?, ?> map) {
            assertEquals("Logged out successfully", map.get("message"));
        } else {
            assertEquals("Logged out successfully", response.getBody());
        }
    }

    @Test
    @DisplayName("Register - Should return internal server error for unexpected exception")
    void register_ShouldReturnInternalServerError_ForUnexpectedException() {
        // Arrange
        when(userService.createUser("testuser", "test@example.com", "password", User.Role.USER))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act
        ResponseEntity<?> response = authController.register(registerRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(userService).createUser("testuser", "test@example.com", "password", User.Role.USER);
    }

    @Test
    @DisplayName("Register - Should return bad request when username already exists")
    void register_ShouldReturnBadRequest_WhenUsernameExists() {
        // Arrange
        when(userService.createUser("testuser", "test@example.com", "password", User.Role.USER))
                .thenThrow(new IllegalArgumentException("User with username testuser already exists"));

        // Act
        ResponseEntity<?> response = authController.register(registerRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService).createUser("testuser", "test@example.com", "password", User.Role.USER);
    }

    @Test
    @DisplayName("Signin - Should handle authentication manager exception")
    void signin_ShouldHandleAuthenticationManagerException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        // Act
        ResponseEntity<?> response = authController.signin(signinRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        if (response.getBody() instanceof java.util.Map<?, ?> map) {
            assertEquals("Invalid credentials", map.get("error"));
        }
    }

    @Test
    @DisplayName("Signin - Should handle JWT generation failure")
    void signin_ShouldHandleJwtGenerationFailure() {
        // Arrange
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("test@example.com");
        when(mockAuth.getAuthorities()).thenReturn((Collection) Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(jwtService.generate(any(), any())).thenThrow(new RuntimeException("JWT generation failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authController.signin(signinRequest);
        });
    }
}
