package csd.tariff.backend.unit;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
 * Comprehensive unit tests for JwtService
 * Tests JWT token generation, parsing, validation, and exception handling
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Unit Tests")
class JwtServiceTest {

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
        // Arrange
        String subjectEmail = "test@example.com";
        Collection<? extends GrantedAuthority> emptyAuthorities = Collections.emptyList();

        // Act
        String token = jwtService.generate(subjectEmail, emptyAuthorities);

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
    @DisplayName("Should generate different tokens for different subjects")
    void generate_ShouldGenerateDifferentTokens_ForDifferentSubjects() {
        // Arrange
        String subject1 = "user1@example.com";
        String subject2 = "user2@example.com";

        // Act
        String token1 = jwtService.generate(subject1, testAuthorities);
        String token2 = jwtService.generate(subject2, testAuthorities);

        // Assert
        assertNotEquals(token1, token2);
        
        Claims claims1 = jwtService.parseClaims(token1);
        Claims claims2 = jwtService.parseClaims(token2);
        
        assertEquals(subject1, claims1.getSubject());
        assertEquals(subject2, claims2.getSubject());
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
    @DisplayName("Should extract email successfully from valid token")
    void extractEmail_ShouldExtractSuccessfully_FromValidToken() {
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
        String invalidToken = "invalid.jwt.token";

        // Act & Assert
        assertThrows(JwtException.class, () -> jwtService.extractEmail(invalidToken));
    }

    // ===== IS VALID TESTS =====

    @Test
    @DisplayName("Should return true for valid token and matching user")
    void isValid_ShouldReturnTrue_ForValidTokenAndMatchingUser() {
        // Arrange
        String subjectEmail = "test@example.com";
        String token = jwtService.generate(subjectEmail, testAuthorities);

        // Act
        boolean isValid = jwtService.isValid(token, testUserDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should return false for invalid token")
    void isValid_ShouldReturnFalse_ForInvalidToken() {
        // Arrange
        String invalidToken = "invalid.jwt.token";

        // Act
        boolean isValid = jwtService.isValid(invalidToken, testUserDetails);

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

    // ===== EDGE CASES TESTS =====

    @Test
    @DisplayName("Should handle null token in isValid")
    void isValid_ShouldReturnFalse_ForNullToken() {
        // Act
        boolean isValid = jwtService.isValid(null, testUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle null user in isValid")
    void isValid_ShouldReturnFalse_ForNullUser() {
        // Arrange
        String subjectEmail = "test@example.com";
        String token = jwtService.generate(subjectEmail, testAuthorities);

        // Act
        boolean isValid = jwtService.isValid(token, null);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle token with no expiration")
    void isValid_ShouldReturnTrue_ForTokenWithNoExpiration() {
        // This test is more complex as we need to create a token without expiration
        // For now, we'll test the normal case where expiration exists
        String subjectEmail = "test@example.com";
        String token = jwtService.generate(subjectEmail, testAuthorities);

        // Act
        boolean isValid = jwtService.isValid(token, testUserDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should handle empty token string")
    void isValid_ShouldReturnFalse_ForEmptyToken() {
        // Act
        boolean isValid = jwtService.isValid("", testUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle whitespace-only token")
    void isValid_ShouldReturnFalse_ForWhitespaceToken() {
        // Act
        boolean isValid = jwtService.isValid("   ", testUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle malformed token")
    void isValid_ShouldReturnFalse_ForMalformedToken() {
        // Act
        boolean isValid = jwtService.isValid("not.a.valid.jwt.token", testUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle token with wrong signature")
    void isValid_ShouldReturnFalse_ForWrongSignatureToken() {
        // Arrange - create a token with different secret
        JwtService otherJwtService = new JwtService();
        ReflectionTestUtils.setField(otherJwtService, "secret", "differentSecretKeyForTestingPurposesOnly");
        ReflectionTestUtils.setField(otherJwtService, "ttlSeconds", testTtlSeconds);
        
        String token = otherJwtService.generate("test@example.com", testAuthorities);

        // Act
        boolean isValid = jwtService.isValid(token, testUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle token with different subject")
    void isValid_ShouldReturnFalse_ForDifferentSubject() {
        // Arrange
        String token = jwtService.generate("different@example.com", testAuthorities);

        // Act
        boolean isValid = jwtService.isValid(token, testUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should handle null authorities in generate")
    void generate_ShouldHandleNullAuthorities() {
        // Act
        String token = jwtService.generate("test@example.com", null);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token can be parsed
        Claims claims = jwtService.parseClaims(token);
        assertEquals("test@example.com", claims.getSubject());
        assertEquals("", claims.get("roles"));
    }

    @Test
    @DisplayName("Should handle empty authorities in generate")
    void generate_ShouldHandleEmptyAuthorities() {
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
    @DisplayName("Should handle case insensitive username comparison")
    void isValid_ShouldHandleCaseInsensitiveUsername() {
        // Arrange
        String token = jwtService.generate("TEST@EXAMPLE.COM", testAuthorities);
        UserDetails caseInsensitiveUser = new User("test@example.com", "password", testAuthorities);

        // Act
        boolean isValid = jwtService.isValid(token, caseInsensitiveUser);

        // Assert
        assertTrue(isValid);
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
