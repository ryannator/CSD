package csd.tariff.backend.security;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import csd.tariff.backend.model.User;
import csd.tariff.backend.repository.UserRepository;
import csd.tariff.backend.service.EmailUserDetailsService;

/**
 * Tests for EmailUserDetailsService
 * Covers Spring Security UserDetailsService implementation
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmailUserDetailsService Tests")
class EmailUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmailUserDetailsService emailUserDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(User.Role.USER);
    }

    // ===== Load User By Username Tests =====

    @Test
    @DisplayName("Should load user by email successfully")
    void loadUserByUsername_ShouldLoadUserSuccessfully() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        UserDetails result = emailUserDetailsService.loadUserByUsername("test@example.com");

        // Then
        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should load admin user by email successfully")
    void loadUserByUsername_ShouldLoadAdminUserSuccessfully() {
        // Given
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("encodedAdminPassword");
        adminUser.setRole(User.Role.ADMIN);

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        // When
        UserDetails result = emailUserDetailsService.loadUserByUsername("admin@example.com");

        // Then
        assertNotNull(result);
        assertEquals("admin@example.com", result.getUsername());
        assertEquals("encodedAdminPassword", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        verify(userRepository, times(1)).findByEmail("admin@example.com");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void loadUserByUsername_ShouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            emailUserDetailsService.loadUserByUsername("nonexistent@example.com");
        });

        assertEquals("No user with email nonexistent@example.com", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Should handle null email input")
    void loadUserByUsername_ShouldHandleNullEmail() {
        // Given
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            emailUserDetailsService.loadUserByUsername(null);
        });

        assertEquals("No user with email null", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(null);
    }

    @Test
    @DisplayName("Should handle empty email input")
    void loadUserByUsername_ShouldHandleEmptyEmail() {
        // Given
        when(userRepository.findByEmail("")).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            emailUserDetailsService.loadUserByUsername("");
        });

        assertEquals("No user with email ", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("");
    }

    @Test
    @DisplayName("Should handle whitespace-only email input")
    void loadUserByUsername_ShouldHandleWhitespaceEmail() {
        // Given
        when(userRepository.findByEmail("   ")).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            emailUserDetailsService.loadUserByUsername("   ");
        });

        assertEquals("No user with email    ", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("   ");
    }

    // ===== UserDetails Properties Tests =====

    @Test
    @DisplayName("Should create UserDetails with correct properties")
    void loadUserByUsername_ShouldCreateUserDetailsWithCorrectProperties() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        UserDetails result = emailUserDetailsService.loadUserByUsername("test@example.com");

        // Then
        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        
        // Check authorities
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertEquals(1, result.getAuthorities().size());
        
        // Check account status (should be enabled by default)
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
        assertTrue(result.isEnabled());
        
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should handle different user roles correctly")
    void loadUserByUsername_ShouldHandleDifferentRolesCorrectly() {
        // Test USER role
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        UserDetails userDetails = emailUserDetailsService.loadUserByUsername("user@example.com");
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

        // Test ADMIN role
        User adminUser = new User();
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("adminPassword");
        adminUser.setRole(User.Role.ADMIN);
        
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        UserDetails adminDetails = emailUserDetailsService.loadUserByUsername("admin@example.com");
        assertTrue(adminDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));

        verify(userRepository, times(1)).findByEmail("user@example.com");
        verify(userRepository, times(1)).findByEmail("admin@example.com");
    }

    // ===== Edge Cases and Error Handling =====

    @Test
    @DisplayName("Should handle repository exception gracefully")
    void loadUserByUsername_ShouldHandleRepositoryException() {
        // Given
        when(userRepository.findByEmail("test@example.com"))
                .thenThrow(new RuntimeException("Database connection error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailUserDetailsService.loadUserByUsername("test@example.com");
        });

        assertEquals("Database connection error", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should handle user with null role")
    void loadUserByUsername_ShouldHandleUserWithNullRole() {
        // Given
        User userWithNullRole = new User();
        userWithNullRole.setEmail("test@example.com");
        userWithNullRole.setPassword("password");
        userWithNullRole.setRole(null);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userWithNullRole));

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            emailUserDetailsService.loadUserByUsername("test@example.com");
        });

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should handle user with null password")
    void loadUserByUsername_ShouldHandleUserWithNullPassword() {
        // Given
        User userWithNullPassword = new User();
        userWithNullPassword.setEmail("test@example.com");
        userWithNullPassword.setPassword(null);
        userWithNullPassword.setRole(User.Role.USER);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userWithNullPassword));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailUserDetailsService.loadUserByUsername("test@example.com");
        });

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should handle user with null email")
    void loadUserByUsername_ShouldHandleUserWithNullEmail() {
        // Given
        User userWithNullEmail = new User();
        userWithNullEmail.setEmail(null);
        userWithNullEmail.setPassword("password");
        userWithNullEmail.setRole(User.Role.USER);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userWithNullEmail));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            emailUserDetailsService.loadUserByUsername("test@example.com");
        });

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should handle case sensitivity in email")
    void loadUserByUsername_ShouldHandleCaseSensitivityInEmail() {
        // Given
        when(userRepository.findByEmail("TEST@EXAMPLE.COM")).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            emailUserDetailsService.loadUserByUsername("TEST@EXAMPLE.COM");
        });

        assertEquals("No user with email TEST@EXAMPLE.COM", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("TEST@EXAMPLE.COM");
    }

    @Test
    @DisplayName("Should handle special characters in email")
    void loadUserByUsername_ShouldHandleSpecialCharactersInEmail() {
        // Given
        String specialEmail = "test+tag@example-domain.co.uk";
        User specialUser = new User();
        specialUser.setEmail(specialEmail);
        specialUser.setPassword("password");
        specialUser.setRole(User.Role.USER);
        
        when(userRepository.findByEmail(specialEmail)).thenReturn(Optional.of(specialUser));

        // When
        UserDetails result = emailUserDetailsService.loadUserByUsername(specialEmail);

        // Then
        assertNotNull(result);
        assertEquals(specialEmail, result.getUsername());
        verify(userRepository, times(1)).findByEmail(specialEmail);
    }
}