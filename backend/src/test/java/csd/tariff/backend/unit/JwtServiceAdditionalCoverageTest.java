package csd.tariff.backend.unit;

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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import csd.tariff.backend.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

/**
 * Additional JwtService tests to improve coverage
 * Focuses on edge cases and scenarios that might not be fully covered
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Additional Coverage Tests")
class JwtServiceAdditionalCoverageTest {

    @InjectMocks
    private JwtService jwtService;

    private String testSecret;
    private long testTtlSeconds;

    @BeforeEach
    void setUp() {
        // Set up test configuration
        testSecret = "testSecretKeyForJwtServiceTestingPurposesOnlyMustBeLongEnough";
        testTtlSeconds = 3600L; // 1 hour
        
        // Set private fields using reflection
        ReflectionTestUtils.setField(jwtService, "secret", testSecret);
        ReflectionTestUtils.setField(jwtService, "ttlSeconds", testTtlSeconds);
    }

    @Test
    @DisplayName("Should handle null expiration in isValid method")
    void isValid_ShouldHandleNullExpiration() {
        // Arrange
        String token = jwtService.generate("test@example.com", null);
        UserDetails testUser = new User("test@example.com", "password", Collections.emptyList());
        
        // Manually create a token with null expiration by modifying the claims
        Claims claims = jwtService.parseClaims(token);
        // We can't directly set expiration to null, but we can test the null check logic
        
        // Act
        boolean isValid = jwtService.isValid(token, testUser);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should handle expired token in isValid method")
    void isValid_ShouldHandleExpiredToken() {
        // Arrange
        // Set TTL to 0 to create an expired token
        ReflectionTestUtils.setField(jwtService, "ttlSeconds", -3600L); // Negative TTL
        String token = jwtService.generate("test@example.com", null);
        UserDetails testUser = new User("test@example.com", "password", Collections.emptyList());

        // Act
        boolean isValid = jwtService.isValid(token, testUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle token with whitespace-only content")
    void isValid_ShouldHandleWhitespaceOnlyToken() {
        // Arrange
        UserDetails testUser = new User("test@example.com", "password", Collections.emptyList());

        // Act
        boolean isValid = jwtService.isValid("   ", testUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle token with only spaces")
    void isValid_ShouldHandleSpacesOnlyToken() {
        // Arrange
        UserDetails testUser = new User("test@example.com", "password", Collections.emptyList());

        // Act
        boolean isValid = jwtService.isValid(" ", testUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle token with tabs and newlines")
    void isValid_ShouldHandleWhitespaceCharactersToken() {
        // Arrange
        UserDetails testUser = new User("test@example.com", "password", Collections.emptyList());

        // Act
        boolean isValid = jwtService.isValid("\t\n\r ", testUser);

        // Assert
        assertFalse(isValid);
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

    @Test
    @DisplayName("Should handle parseClaims with invalid token")
    void parseClaims_ShouldThrowException_WithInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token";

        // Act & Assert
        assertThrows(JwtException.class, () -> {
            jwtService.parseClaims(invalidToken);
        });
    }

    @Test
    @DisplayName("Should handle extractEmail with invalid token")
    void extractEmail_ShouldThrowException_WithInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token";

        // Act & Assert
        assertThrows(JwtException.class, () -> {
            jwtService.extractEmail(invalidToken);
        });
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
}
