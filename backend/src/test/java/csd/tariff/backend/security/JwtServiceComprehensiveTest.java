package csd.tariff.backend.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import csd.tariff.backend.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

/**
 * Comprehensive JwtService tests
 * Combines basic tests, edge cases, and additional coverage tests
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Comprehensive Tests")
class JwtServiceComprehensiveTest {

    @InjectMocks
    private JwtService jwtService;

    private String testSecret;
    private long testTtlSeconds;
    private Collection<? extends GrantedAuthority> testAuthorities;
    private UserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        // Set up test configuration
        testSecret = "testSecretKeyForJwtServiceTestingPurposesOnlyMustBeLongEnough";
        testTtlSeconds = 3600L; // 1 hour
        
        // Set private fields using reflection
        ReflectionTestUtils.setField(jwtService, "secret", testSecret);
        ReflectionTestUtils.setField(jwtService, "ttlSeconds", testTtlSeconds);

        // Setup test authorities
        testAuthorities = Arrays.asList(
            new TestGrantedAuthority("ROLE_USER"),
            new TestGrantedAuthority("ROLE_ADMIN")
        );

        // Setup test user details
        testUserDetails = User.builder()
            .username("test@example.com")
            .password("password")
            .authorities(testAuthorities)
            .build();
    }

    // ===== GENERATE TOKEN TESTS =====

    @Test
    @DisplayName("Should generate valid JWT token with subject and authorities")
    void generate_ShouldGenerateValidToken_WithSubjectAndAuthorities() {
        // Arrange
        String subjectEmail = "test@example.com";

        // Act
        String token = jwtService.generate(subjectEmail, testAuthorities);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token can be parsed
        Claims claims = jwtService.parseClaims(token);
        assertEquals(subjectEmail, claims.getSubject());
        assertEquals("ROLE_USER,ROLE_ADMIN", claims.get("roles"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        
        // Verify expiration is approximately correct
        long expectedExpiration = System.currentTimeMillis() + (testTtlSeconds * 1000);
        long actualExpiration = claims.getExpiration().getTime();
        assertTrue(Math.abs(expectedExpiration - actualExpiration) < 1000); // Within 1 second
    }

    @Test
    @DisplayName("Should generate valid JWT token with null authorities")
    void generate_ShouldGenerateValidToken_WithNullAuthorities() {
        // Arrange
        String subjectEmail = "test@example.com";

        // Act
        String token = jwtService.generate(subjectEmail, null);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token can be parsed
        Claims claims = jwtService.parseClaims(token);
        assertEquals(subjectEmail, claims.getSubject());
        assertEquals("", claims.get("roles"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    @DisplayName("Should generate valid JWT token with empty authorities")
    void generate_ShouldGenerateValidToken_WithEmptyAuthorities() {
        // Act
        String token = jwtService.generate("test@example.com", Collections.emptyList());

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token can be parsed
        Claims claims = jwtService.parseClaims(token);
        assertEquals("test@example.com", claims.getSubject());
        assertEquals("", claims.get("roles"));
    }

    @Test
    @DisplayName("Should handle multiple authorities in generate")
    void generate_ShouldHandleMultipleAuthorities() {
        // Arrange
        Collection<? extends GrantedAuthority> multipleAuthorities = Arrays.asList(
            new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"),
            new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN"),
            new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_MANAGER")
        );

        // Act
        String token = jwtService.generate("test@example.com", multipleAuthorities);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token can be parsed
        Claims claims = jwtService.parseClaims(token);
        assertEquals("test@example.com", claims.getSubject());
        assertEquals("ROLE_USER,ROLE_ADMIN,ROLE_MANAGER", claims.get("roles"));
    }

    @Test
    @DisplayName("Should handle generate with empty string subject")
    void generate_ShouldHandleEmptyStringSubject() {
        // Act
        String token = jwtService.generate("", null);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token can be parsed
        Claims claims = jwtService.parseClaims(token);
        assertEquals("", claims.getSubject());
    }

    @Test
    @DisplayName("Should handle generate with very long subject")
    void generate_ShouldHandleVeryLongSubject() {
        // Arrange
        String longSubject = "a".repeat(1000) + "@example.com";

        // Act
        String token = jwtService.generate(longSubject, null);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token can be parsed
        Claims claims = jwtService.parseClaims(token);
        assertEquals(longSubject, claims.getSubject());
    }

    @Test
    @DisplayName("Should handle generate with special characters in subject")
    void generate_ShouldHandleSpecialCharactersInSubject() {
        // Arrange
        String specialSubject = "test+user@example.com";

        // Act
        String token = jwtService.generate(specialSubject, null);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token can be parsed
        Claims claims = jwtService.parseClaims(token);
        assertEquals(specialSubject, claims.getSubject());
    }

    @Test
    @DisplayName("Should handle generate with unicode characters in subject")
    void generate_ShouldHandleUnicodeCharactersInSubject() {
        // Arrange
        String unicodeSubject = "tëst@ëxämplë.com";

        // Act
        String token = jwtService.generate(unicodeSubject, null);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token can be parsed
        Claims claims = jwtService.parseClaims(token);
        assertEquals(unicodeSubject, claims.getSubject());
    }

    @Test
    @DisplayName("Should handle generate with zero TTL")
    void generate_ShouldHandleZeroTtl() {
        // Arrange
        ReflectionTestUtils.setField(jwtService, "ttlSeconds", 0L);

        // Act
        String token = jwtService.generate("test@example.com", null);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token can be parsed
        Claims claims = jwtService.parseClaims(token);
        assertEquals("test@example.com", claims.getSubject());
        assertNotNull(claims.getExpiration());
    }

    @Test
    @DisplayName("Should handle generate with very large TTL")
    void generate_ShouldHandleVeryLargeTtl() {
        // Arrange
        ReflectionTestUtils.setField(jwtService, "ttlSeconds", Long.MAX_VALUE / 1000);

        // Act
        String token = jwtService.generate("test@example.com", null);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token can be parsed
        Claims claims = jwtService.parseClaims(token);
        assertEquals("test@example.com", claims.getSubject());
        assertNotNull(claims.getExpiration());
    }

    // ===== PARSE CLAIMS TESTS =====

    @Test
    @DisplayName("Should parse valid JWT token successfully")
    void parseClaims_ShouldParseSuccessfully_WithValidToken() {
        // Arrange
        String subjectEmail = "test@example.com";
        String token = jwtService.generate(subjectEmail, testAuthorities);

        // Act
        Claims claims = jwtService.parseClaims(token);

        // Assert
        assertNotNull(claims);
        assertEquals(subjectEmail, claims.getSubject());
        assertEquals("ROLE_USER,ROLE_ADMIN", claims.get("roles"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    @DisplayName("Should throw JwtException when parsing invalid token")
    void parseClaims_ShouldThrowException_WithInvalidToken() {
        // Arrange
        String invalidToken = "invalid.jwt.token";

        // Act & Assert
        assertThrows(JwtException.class, () -> jwtService.parseClaims(invalidToken));
    }

    @Test
    @DisplayName("Should throw JwtException when parsing malformed token")
    void parseClaims_ShouldThrowException_WithMalformedToken() {
        // Arrange
        String malformedToken = "not.a.valid.jwt.token.structure";

        // Act & Assert
        assertThrows(JwtException.class, () -> jwtService.parseClaims(malformedToken));
    }

    @Test
    @DisplayName("Should throw JwtException when parsing empty token")
    void parseClaims_ShouldThrowException_WithEmptyToken() {
        // Arrange
        String emptyToken = "";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> jwtService.parseClaims(emptyToken));
    }

    // ===== EXTRACT EMAIL TESTS =====

    @Test
    @DisplayName("Should extract email from valid token")
    void extractEmail_ShouldExtractEmail_FromValidToken() {
        // Arrange
        String subjectEmail = "test@example.com";
        String token = jwtService.generate(subjectEmail, testAuthorities);

        // Act
        String extractedEmail = jwtService.extractEmail(token);

        // Assert
        assertEquals(subjectEmail, extractedEmail);
    }

    @Test
    @DisplayName("Should throw JwtException when extracting email from invalid token")
    void extractEmail_ShouldThrowException_WithInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token";

        // Act & Assert
        assertThrows(JwtException.class, () -> jwtService.extractEmail(invalidToken));
    }

    // ===== VALIDATION TESTS =====

    @Test
    @DisplayName("Should return true for valid token and user")
    void isValid_ShouldReturnTrue_ForValidTokenAndUser() {
        // Arrange
        String subjectEmail = "test@example.com";
        String token = jwtService.generate(subjectEmail, testAuthorities);

        // Act
        boolean isValid = jwtService.isValid(token, testUserDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should return false for null token")
    void isValid_ShouldReturnFalse_ForNullToken() {
        // Act
        boolean isValid = jwtService.isValid(null, testUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should return false for null user")
    void isValid_ShouldReturnFalse_ForNullUser() {
        // Arrange
        String token = jwtService.generate("test@example.com", testAuthorities);

        // Act
        boolean isValid = jwtService.isValid(token, null);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should return false for empty token")
    void isValid_ShouldReturnFalse_ForEmptyToken() {
        // Act
        boolean isValid = jwtService.isValid("", testUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should return false for token with whitespace only")
    void isValid_ShouldReturnFalse_ForWhitespaceOnlyToken() {
        // Act
        boolean isValid = jwtService.isValid("   ", testUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should return false for token with only spaces")
    void isValid_ShouldReturnFalse_ForSpacesOnlyToken() {
        // Act
        boolean isValid = jwtService.isValid(" ", testUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should return false for token with tabs and newlines")
    void isValid_ShouldReturnFalse_ForWhitespaceCharactersToken() {
        // Act
        boolean isValid = jwtService.isValid("\t\n\r ", testUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should return false for token with different subject")
    void isValid_ShouldReturnFalse_ForTokenWithDifferentSubject() {
        // Arrange
        String differentSubject = "different@example.com";
        String token = jwtService.generate(differentSubject, testAuthorities);

        // Act
        boolean isValid = jwtService.isValid(token, testUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should return false for expired token")
    void isValid_ShouldReturnFalse_ForExpiredToken() {
        // Arrange
        String subjectEmail = "test@example.com";
        
        // Set very short TTL for testing expiration
        ReflectionTestUtils.setField(jwtService, "ttlSeconds", 1L);
        String token = jwtService.generate(subjectEmail, testAuthorities);
        
        // Wait for token to expire
        try {
            Thread.sleep(1100); // Wait 1.1 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        boolean isValid = jwtService.isValid(token, testUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle case insensitive email comparison")
    void isValid_ShouldHandleCaseInsensitive_EmailComparison() {
        // Arrange
        String subjectEmail = "TEST@EXAMPLE.COM"; // Uppercase
        String token = jwtService.generate(subjectEmail, testAuthorities);

        // Act
        boolean isValid = jwtService.isValid(token, testUserDetails); // testUserDetails has lowercase email

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should handle case insensitive comparison with different cases")
    void isValid_ShouldHandleCaseInsensitiveComparison() {
        // Arrange
        String token = jwtService.generate("Test@Example.COM", null);
        UserDetails testUser = new User("test@example.com", "password", Collections.emptyList());

        // Act
        boolean isValid = jwtService.isValid(token, testUser);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should handle case insensitive comparison with mixed cases")
    void isValid_ShouldHandleMixedCaseComparison() {
        // Arrange
        String token = jwtService.generate("tEsT@eXaMpLe.CoM", null);
        UserDetails testUser = new User("TEST@EXAMPLE.COM", "password", Collections.emptyList());

        // Act
        boolean isValid = jwtService.isValid(token, testUser);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should handle JwtException in isValid method")
    void isValid_ShouldHandleJwtException() {
        // Arrange
        UserDetails testUser = new User("test@example.com", "password", Collections.emptyList());
        String invalidToken = "invalid.jwt.token";

        // Act
        boolean isValid = jwtService.isValid(invalidToken, testUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle malformed JWT token")
    void isValid_ShouldHandleMalformedToken() {
        // Arrange
        UserDetails testUser = new User("test@example.com", "password", Collections.emptyList());
        String malformedToken = "not.a.valid.jwt";

        // Act
        boolean isValid = jwtService.isValid(malformedToken, testUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle token with wrong signature")
    void isValid_ShouldHandleWrongSignatureToken() {
        // Arrange
        UserDetails testUser = new User("test@example.com", "password", Collections.emptyList());
        
        // Create a token with different secret
        JwtService differentJwtService = new JwtService();
        ReflectionTestUtils.setField(differentJwtService, "secret", "differentSecretKeyForJwtServiceTestingPurposesOnlyMustBeLongEnough");
        ReflectionTestUtils.setField(differentJwtService, "ttlSeconds", testTtlSeconds);
        String tokenWithDifferentSecret = differentJwtService.generate("test@example.com", null);

        // Act
        boolean isValid = jwtService.isValid(tokenWithDifferentSecret, testUser);

        // Assert
        assertFalse(isValid);
    }

    // ===== HELPER CLASS =====

    private static class TestGrantedAuthority implements GrantedAuthority {
        private final String authority;

        public TestGrantedAuthority(String authority) {
            this.authority = authority;
        }

        @Override
        public String getAuthority() {
            return authority;
        }
    }
}
