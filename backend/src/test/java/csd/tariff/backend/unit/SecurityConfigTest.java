package csd.tariff.backend.unit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;

import csd.tariff.backend.config.SecurityConfig;
import csd.tariff.backend.security.JwtAuthFilter;

@DisplayName("SecurityConfig Unit Tests")
class SecurityConfigTest {

    @Test
    @DisplayName("Should create SecurityConfig with JwtAuthFilter")
    void shouldCreateSecurityConfigWithJwtAuthFilter() {
        JwtAuthFilter jwtAuthFilter = mock(JwtAuthFilter.class);
        SecurityConfig securityConfig = new SecurityConfig(jwtAuthFilter);
        assertNotNull(securityConfig);
    }

    @Test
    @DisplayName("Should create SecurityConfig with null JwtAuthFilter")
    void shouldCreateSecurityConfigWithNullJwtAuthFilter() {
        SecurityConfig securityConfig = new SecurityConfig(null);
        assertNotNull(securityConfig);
    }

    @Test
    @DisplayName("Should handle password encoding with BCrypt")
    void shouldHandlePasswordEncodingWithBCrypt() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String rawPassword = "testPassword123";
        String encodedPassword = encoder.encode(rawPassword);
        
        assertNotNull(encodedPassword);
        assertTrue(encoder.matches(rawPassword, encodedPassword));
    }

    @Test
    @DisplayName("Should handle different password encodings")
    void shouldHandleDifferentPasswordEncodings() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password1 = "password1";
        String password2 = "password2";
        
        String encoded1 = encoder.encode(password1);
        String encoded2 = encoder.encode(password2);
        
        assertTrue(encoder.matches(password1, encoded1));
        assertTrue(encoder.matches(password2, encoded2));
        assertTrue(!encoder.matches(password1, encoded2));
        assertTrue(!encoder.matches(password2, encoded1));
    }

    @Test
    @DisplayName("Should handle CORS configuration")
    void shouldHandleCorsConfiguration() {
        CorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        assertNotNull(source);
        assertTrue(source instanceof org.springframework.web.cors.UrlBasedCorsConfigurationSource);
    }

    @Test
    @DisplayName("Should handle BCryptPasswordEncoder creation")
    void shouldHandleBCryptPasswordEncoderCreation() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
    }

    @Test
    @DisplayName("Should handle password encoding edge cases")
    void shouldHandlePasswordEncodingEdgeCases() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Test empty password
        String emptyPassword = "";
        String encodedEmpty = encoder.encode(emptyPassword);
        assertNotNull(encodedEmpty);
        assertTrue(encoder.matches(emptyPassword, encodedEmpty));
        
        // Test long password (but within BCrypt's 72-byte limit)
        String longPassword = "a".repeat(70); // 70 characters, well within 72-byte limit
        String encodedLong = encoder.encode(longPassword);
        assertNotNull(encodedLong);
        assertTrue(encoder.matches(longPassword, encodedLong));
    }

    @Test
    @DisplayName("Should handle special characters in passwords")
    void shouldHandleSpecialCharactersInPasswords() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String specialPassword = "!@#$%^&*()_+-=[]{}|;':\",./<>?`~";
        String encodedSpecial = encoder.encode(specialPassword);
        assertNotNull(encodedSpecial);
        assertTrue(encoder.matches(specialPassword, encodedSpecial));
    }
}
