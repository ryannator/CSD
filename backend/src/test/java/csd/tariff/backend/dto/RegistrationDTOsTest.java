package csd.tariff.backend.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import csd.tariff.backend.dto.RegistrationDTOs;
import csd.tariff.backend.dto.RegistrationDTOs.RegisterRequest;
import csd.tariff.backend.dto.RegistrationDTOs.RegisterResponse;
import csd.tariff.backend.dto.RegistrationDTOs.SigninRequest;
import csd.tariff.backend.dto.RegistrationDTOs.SigninResponse;
import csd.tariff.backend.dto.RegistrationDTOs.UserResponse;
import csd.tariff.backend.model.User;

/**
 * Comprehensive unit tests for RegistrationDTOs
 * Tests all record classes and their constructors, getters, and toString methods
 */
@DisplayName("RegistrationDTOs Tests")
class RegistrationDTOsTest {

    @Nested
    @DisplayName("SigninRequest Tests")
    class SigninRequestTests {

        @Test
        @DisplayName("Should create SigninRequest with valid parameters")
        void signinRequest_ShouldCreateWithValidParameters() {
            // Arrange & Act
            SigninRequest request = new SigninRequest("test@example.com", "password123");

            // Assert
            assertNotNull(request);
            assertEquals("test@example.com", request.email());
            assertEquals("password123", request.password());
        }

        @Test
        @DisplayName("Should create SigninRequest with null email")
        void signinRequest_ShouldCreateWithNullEmail() {
            // Arrange & Act
            SigninRequest request = new SigninRequest(null, "password123");

            // Assert
            assertNotNull(request);
            assertNull(request.email());
            assertEquals("password123", request.password());
        }

        @Test
        @DisplayName("Should create SigninRequest with null password")
        void signinRequest_ShouldCreateWithNullPassword() {
            // Arrange & Act
            SigninRequest request = new SigninRequest("test@example.com", null);

            // Assert
            assertNotNull(request);
            assertEquals("test@example.com", request.email());
            assertNull(request.password());
        }

        @Test
        @DisplayName("Should create SigninRequest with both null parameters")
        void signinRequest_ShouldCreateWithBothNullParameters() {
            // Arrange & Act
            SigninRequest request = new SigninRequest(null, null);

            // Assert
            assertNotNull(request);
            assertNull(request.email());
            assertNull(request.password());
        }

        @Test
        @DisplayName("Should create SigninRequest with empty strings")
        void signinRequest_ShouldCreateWithEmptyStrings() {
            // Arrange & Act
            SigninRequest request = new SigninRequest("", "");

            // Assert
            assertNotNull(request);
            assertEquals("", request.email());
            assertEquals("", request.password());
        }

        @Test
        @DisplayName("Should create SigninRequest with whitespace strings")
        void signinRequest_ShouldCreateWithWhitespaceStrings() {
            // Arrange & Act
            SigninRequest request = new SigninRequest("   ", "   ");

            // Assert
            assertNotNull(request);
            assertEquals("   ", request.email());
            assertEquals("   ", request.password());
        }

        @Test
        @DisplayName("Should create SigninRequest with special characters")
        void signinRequest_ShouldCreateWithSpecialCharacters() {
            // Arrange & Act
            SigninRequest request = new SigninRequest("test+tag@example-domain.co.uk", "p@ssw0rd!@#$%");

            // Assert
            assertNotNull(request);
            assertEquals("test+tag@example-domain.co.uk", request.email());
            assertEquals("p@ssw0rd!@#$%", request.password());
        }

        @Test
        @DisplayName("Should test SigninRequest toString method")
        void signinRequest_ShouldTestToStringMethod() {
            // Arrange
            SigninRequest request = new SigninRequest("test@example.com", "password123");

            // Act
            String toString = request.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("test@example.com"));
            assertTrue(toString.contains("password123"));
        }

        @Test
        @DisplayName("Should test SigninRequest toString with null values")
        void signinRequest_ShouldTestToStringWithNullValues() {
            // Arrange
            SigninRequest request = new SigninRequest(null, null);

            // Act
            String toString = request.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("null"));
        }
    }

    @Nested
    @DisplayName("SigninResponse Tests")
    class SigninResponseTests {

        @Test
        @DisplayName("Should create SigninResponse with valid token")
        void signinResponse_ShouldCreateWithValidToken() {
            // Arrange & Act
            SigninResponse response = new SigninResponse("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9");

            // Assert
            assertNotNull(response);
            assertEquals("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9", response.token());
        }

        @Test
        @DisplayName("Should create SigninResponse with null token")
        void signinResponse_ShouldCreateWithNullToken() {
            // Arrange & Act
            SigninResponse response = new SigninResponse(null);

            // Assert
            assertNotNull(response);
            assertNull(response.token());
        }

        @Test
        @DisplayName("Should create SigninResponse with empty token")
        void signinResponse_ShouldCreateWithEmptyToken() {
            // Arrange & Act
            SigninResponse response = new SigninResponse("");

            // Assert
            assertNotNull(response);
            assertEquals("", response.token());
        }

        @Test
        @DisplayName("Should create SigninResponse with long token")
        void signinResponse_ShouldCreateWithLongToken() {
            // Arrange
            String longToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwicm9sZXMiOiJST0xFX1VTRVIiLCJpYXQiOjE2MzQ1Njc4OTAsImV4cCI6MTYzNDU3MTQ5MH0.signature";

            // Act
            SigninResponse response = new SigninResponse(longToken);

            // Assert
            assertNotNull(response);
            assertEquals(longToken, response.token());
        }

        @Test
        @DisplayName("Should test SigninResponse toString method")
        void signinResponse_ShouldTestToStringMethod() {
            // Arrange
            SigninResponse response = new SigninResponse("test-token");

            // Act
            String toString = response.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("test-token"));
        }

        @Test
        @DisplayName("Should test SigninResponse toString with null token")
        void signinResponse_ShouldTestToStringWithNullToken() {
            // Arrange
            SigninResponse response = new SigninResponse(null);

            // Act
            String toString = response.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("null"));
        }
    }

    @Nested
    @DisplayName("RegisterRequest Tests")
    class RegisterRequestTests {

        @Test
        @DisplayName("Should create RegisterRequest with valid parameters")
        void registerRequest_ShouldCreateWithValidParameters() {
            // Arrange & Act
            RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "password123");

            // Assert
            assertNotNull(request);
            assertEquals("testuser", request.username());
            assertEquals("test@example.com", request.email());
            assertEquals("password123", request.password());
        }

        @Test
        @DisplayName("Should create RegisterRequest with null username")
        void registerRequest_ShouldCreateWithNullUsername() {
            // Arrange & Act
            RegisterRequest request = new RegisterRequest(null, "test@example.com", "password123");

            // Assert
            assertNotNull(request);
            assertNull(request.username());
            assertEquals("test@example.com", request.email());
            assertEquals("password123", request.password());
        }

        @Test
        @DisplayName("Should create RegisterRequest with null email")
        void registerRequest_ShouldCreateWithNullEmail() {
            // Arrange & Act
            RegisterRequest request = new RegisterRequest("testuser", null, "password123");

            // Assert
            assertNotNull(request);
            assertEquals("testuser", request.username());
            assertNull(request.email());
            assertEquals("password123", request.password());
        }

        @Test
        @DisplayName("Should create RegisterRequest with null password")
        void registerRequest_ShouldCreateWithNullPassword() {
            // Arrange & Act
            RegisterRequest request = new RegisterRequest("testuser", "test@example.com", null);

            // Assert
            assertNotNull(request);
            assertEquals("testuser", request.username());
            assertEquals("test@example.com", request.email());
            assertNull(request.password());
        }

        @Test
        @DisplayName("Should create RegisterRequest with all null parameters")
        void registerRequest_ShouldCreateWithAllNullParameters() {
            // Arrange & Act
            RegisterRequest request = new RegisterRequest(null, null, null);

            // Assert
            assertNotNull(request);
            assertNull(request.username());
            assertNull(request.email());
            assertNull(request.password());
        }

        @Test
        @DisplayName("Should create RegisterRequest with empty strings")
        void registerRequest_ShouldCreateWithEmptyStrings() {
            // Arrange & Act
            RegisterRequest request = new RegisterRequest("", "", "");

            // Assert
            assertNotNull(request);
            assertEquals("", request.username());
            assertEquals("", request.email());
            assertEquals("", request.password());
        }

        @Test
        @DisplayName("Should create RegisterRequest with special characters")
        void registerRequest_ShouldCreateWithSpecialCharacters() {
            // Arrange & Act
            RegisterRequest request = new RegisterRequest("user@123", "test+tag@example-domain.co.uk", "p@ssw0rd!@#$%");

            // Assert
            assertNotNull(request);
            assertEquals("user@123", request.username());
            assertEquals("test+tag@example-domain.co.uk", request.email());
            assertEquals("p@ssw0rd!@#$%", request.password());
        }

        @Test
        @DisplayName("Should test RegisterRequest toString method")
        void registerRequest_ShouldTestToStringMethod() {
            // Arrange
            RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "password123");

            // Act
            String toString = request.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("testuser"));
            assertTrue(toString.contains("test@example.com"));
            assertTrue(toString.contains("password123"));
        }

        @Test
        @DisplayName("Should test RegisterRequest toString with null values")
        void registerRequest_ShouldTestToStringWithNullValues() {
            // Arrange
            RegisterRequest request = new RegisterRequest(null, null, null);

            // Act
            String toString = request.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("null"));
        }
    }

    @Nested
    @DisplayName("UserResponse Tests")
    class UserResponseTests {

        @Test
        @DisplayName("Should create UserResponse with valid parameters")
        void userResponse_ShouldCreateWithValidParameters() {
            // Arrange & Act
            UserResponse response = new UserResponse(1L, "testuser", "test@example.com", User.Role.USER);

            // Assert
            assertNotNull(response);
            assertEquals(1L, response.id());
            assertEquals("testuser", response.username());
            assertEquals("test@example.com", response.email());
            assertEquals(User.Role.USER, response.role());
        }

        @Test
        @DisplayName("Should create UserResponse with ADMIN role")
        void userResponse_ShouldCreateWithAdminRole() {
            // Arrange & Act
            UserResponse response = new UserResponse(2L, "admin", "admin@example.com", User.Role.ADMIN);

            // Assert
            assertNotNull(response);
            assertEquals(2L, response.id());
            assertEquals("admin", response.username());
            assertEquals("admin@example.com", response.email());
            assertEquals(User.Role.ADMIN, response.role());
        }

        @Test
        @DisplayName("Should create UserResponse with null id")
        void userResponse_ShouldCreateWithNullId() {
            // Arrange & Act
            UserResponse response = new UserResponse(null, "testuser", "test@example.com", User.Role.USER);

            // Assert
            assertNotNull(response);
            assertNull(response.id());
            assertEquals("testuser", response.username());
            assertEquals("test@example.com", response.email());
            assertEquals(User.Role.USER, response.role());
        }

        @Test
        @DisplayName("Should create UserResponse with null username")
        void userResponse_ShouldCreateWithNullUsername() {
            // Arrange & Act
            UserResponse response = new UserResponse(1L, null, "test@example.com", User.Role.USER);

            // Assert
            assertNotNull(response);
            assertEquals(1L, response.id());
            assertNull(response.username());
            assertEquals("test@example.com", response.email());
            assertEquals(User.Role.USER, response.role());
        }

        @Test
        @DisplayName("Should create UserResponse with null email")
        void userResponse_ShouldCreateWithNullEmail() {
            // Arrange & Act
            UserResponse response = new UserResponse(1L, "testuser", null, User.Role.USER);

            // Assert
            assertNotNull(response);
            assertEquals(1L, response.id());
            assertEquals("testuser", response.username());
            assertNull(response.email());
            assertEquals(User.Role.USER, response.role());
        }

        @Test
        @DisplayName("Should create UserResponse with null role")
        void userResponse_ShouldCreateWithNullRole() {
            // Arrange & Act
            UserResponse response = new UserResponse(1L, "testuser", "test@example.com", null);

            // Assert
            assertNotNull(response);
            assertEquals(1L, response.id());
            assertEquals("testuser", response.username());
            assertEquals("test@example.com", response.email());
            assertNull(response.role());
        }

        @Test
        @DisplayName("Should create UserResponse with all null parameters")
        void userResponse_ShouldCreateWithAllNullParameters() {
            // Arrange & Act
            UserResponse response = new UserResponse(null, null, null, null);

            // Assert
            assertNotNull(response);
            assertNull(response.id());
            assertNull(response.username());
            assertNull(response.email());
            assertNull(response.role());
        }

        @Test
        @DisplayName("Should create UserResponse with empty strings")
        void userResponse_ShouldCreateWithEmptyStrings() {
            // Arrange & Act
            UserResponse response = new UserResponse(1L, "", "", User.Role.USER);

            // Assert
            assertNotNull(response);
            assertEquals(1L, response.id());
            assertEquals("", response.username());
            assertEquals("", response.email());
            assertEquals(User.Role.USER, response.role());
        }

        @Test
        @DisplayName("Should create UserResponse with special characters")
        void userResponse_ShouldCreateWithSpecialCharacters() {
            // Arrange & Act
            UserResponse response = new UserResponse(999L, "user@123", "test+tag@example-domain.co.uk", User.Role.ADMIN);

            // Assert
            assertNotNull(response);
            assertEquals(999L, response.id());
            assertEquals("user@123", response.username());
            assertEquals("test+tag@example-domain.co.uk", response.email());
            assertEquals(User.Role.ADMIN, response.role());
        }

        @Test
        @DisplayName("Should test UserResponse toString method")
        void userResponse_ShouldTestToStringMethod() {
            // Arrange
            UserResponse response = new UserResponse(1L, "testuser", "test@example.com", User.Role.USER);

            // Act
            String toString = response.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("1"));
            assertTrue(toString.contains("testuser"));
            assertTrue(toString.contains("test@example.com"));
            assertTrue(toString.contains("USER"));
        }

        @Test
        @DisplayName("Should test UserResponse toString with null values")
        void userResponse_ShouldTestToStringWithNullValues() {
            // Arrange
            UserResponse response = new UserResponse(null, null, null, null);

            // Act
            String toString = response.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("null"));
        }
    }

    @Nested
    @DisplayName("RegisterResponse Tests")
    class RegisterResponseTests {

        @Test
        @DisplayName("Should create RegisterResponse with valid UserResponse")
        void registerResponse_ShouldCreateWithValidUserResponse() {
            // Arrange
            UserResponse userResponse = new UserResponse(1L, "testuser", "test@example.com", User.Role.USER);

            // Act
            RegisterResponse response = new RegisterResponse(userResponse);

            // Assert
            assertNotNull(response);
            assertNotNull(response.user());
            assertEquals(userResponse, response.user());
            assertEquals(1L, response.user().id());
            assertEquals("testuser", response.user().username());
            assertEquals("test@example.com", response.user().email());
            assertEquals(User.Role.USER, response.user().role());
        }

        @Test
        @DisplayName("Should create RegisterResponse with null UserResponse")
        void registerResponse_ShouldCreateWithNullUserResponse() {
            // Arrange & Act
            RegisterResponse response = new RegisterResponse(null);

            // Assert
            assertNotNull(response);
            assertNull(response.user());
        }

        @Test
        @DisplayName("Should create RegisterResponse with UserResponse containing null values")
        void registerResponse_ShouldCreateWithUserResponseContainingNullValues() {
            // Arrange
            UserResponse userResponse = new UserResponse(null, null, null, null);

            // Act
            RegisterResponse response = new RegisterResponse(userResponse);

            // Assert
            assertNotNull(response);
            assertNotNull(response.user());
            assertNull(response.user().id());
            assertNull(response.user().username());
            assertNull(response.user().email());
            assertNull(response.user().role());
        }

        @Test
        @DisplayName("Should create RegisterResponse with ADMIN UserResponse")
        void registerResponse_ShouldCreateWithAdminUserResponse() {
            // Arrange
            UserResponse userResponse = new UserResponse(2L, "admin", "admin@example.com", User.Role.ADMIN);

            // Act
            RegisterResponse response = new RegisterResponse(userResponse);

            // Assert
            assertNotNull(response);
            assertNotNull(response.user());
            assertEquals(2L, response.user().id());
            assertEquals("admin", response.user().username());
            assertEquals("admin@example.com", response.user().email());
            assertEquals(User.Role.ADMIN, response.user().role());
        }

        @Test
        @DisplayName("Should test RegisterResponse toString method")
        void registerResponse_ShouldTestToStringMethod() {
            // Arrange
            UserResponse userResponse = new UserResponse(1L, "testuser", "test@example.com", User.Role.USER);
            RegisterResponse response = new RegisterResponse(userResponse);

            // Act
            String toString = response.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("testuser"));
            assertTrue(toString.contains("test@example.com"));
        }

        @Test
        @DisplayName("Should test RegisterResponse toString with null UserResponse")
        void registerResponse_ShouldTestToStringWithNullUserResponse() {
            // Arrange
            RegisterResponse response = new RegisterResponse(null);

            // Act
            String toString = response.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("null"));
        }
    }

    @Nested
    @DisplayName("RegistrationDTOs Outer Class Tests")
    class RegistrationDTOsOuterClassTests {

        @Test
        @DisplayName("Should instantiate RegistrationDTOs outer class")
        void registrationDTOs_ShouldInstantiateOuterClass() {
            // Arrange & Act
            RegistrationDTOs registrationDTOs = new RegistrationDTOs();

            // Assert
            assertNotNull(registrationDTOs);
        }

        @Test
        @DisplayName("Should test RegistrationDTOs toString method")
        void registrationDTOs_ShouldTestToStringMethod() {
            // Arrange
            RegistrationDTOs registrationDTOs = new RegistrationDTOs();

            // Act
            String toString = registrationDTOs.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("RegistrationDTOs"));
        }

        @Test
        @DisplayName("Should test RegistrationDTOs hashCode method")
        void registrationDTOs_ShouldTestHashCodeMethod() {
            // Arrange
            RegistrationDTOs registrationDTOs1 = new RegistrationDTOs();
            RegistrationDTOs registrationDTOs2 = new RegistrationDTOs();

            // Act
            int hashCode1 = registrationDTOs1.hashCode();
            int hashCode2 = registrationDTOs2.hashCode();

            // Assert
            assertNotNull(hashCode1);
            assertNotNull(hashCode2);
            // Hash codes should be different for different instances (default Object behavior)
            assertNotEquals(hashCode1, hashCode2);
        }

        @Test
        @DisplayName("Should test RegistrationDTOs equals method")
        void registrationDTOs_ShouldTestEqualsMethod() {
            // Arrange
            RegistrationDTOs registrationDTOs1 = new RegistrationDTOs();
            RegistrationDTOs registrationDTOs2 = new RegistrationDTOs();

            // Act & Assert
            assertNotEquals(registrationDTOs1, registrationDTOs2); // Different instances
            assertEquals(registrationDTOs1, registrationDTOs1); // Reflexive
            assertEquals(registrationDTOs2, registrationDTOs2); // Reflexive
        }

        @Test
        @DisplayName("Should test RegistrationDTOs equals with null")
        void registrationDTOs_ShouldTestEqualsWithNull() {
            // Arrange
            RegistrationDTOs registrationDTOs = new RegistrationDTOs();

            // Act & Assert
            assertNotNull(registrationDTOs);
            assertNotNull(registrationDTOs.equals(null));
        }

        @Test
        @DisplayName("Should test RegistrationDTOs equals with different class")
        void registrationDTOs_ShouldTestEqualsWithDifferentClass() {
            // Arrange
            RegistrationDTOs registrationDTOs = new RegistrationDTOs();
            String differentObject = "different";

            // Act & Assert
            assertNotNull(registrationDTOs);
            assertNotNull(registrationDTOs.equals(differentObject));
        }
    }

    @Nested
    @DisplayName("Edge Cases and Integration Tests")
    class EdgeCasesAndIntegrationTests {

        @Test
        @DisplayName("Should handle all DTOs with maximum length strings")
        void shouldHandleAllDTOsWithMaximumLengthStrings() {
            // Arrange
            String longString = "a".repeat(1000);

            // Act
            SigninRequest signinRequest = new SigninRequest(longString, longString);
            SigninResponse signinResponse = new SigninResponse(longString);
            RegisterRequest registerRequest = new RegisterRequest(longString, longString, longString);
            UserResponse userResponse = new UserResponse(Long.MAX_VALUE, longString, longString, User.Role.USER);
            RegisterResponse registerResponse = new RegisterResponse(userResponse);

            // Assert
            assertNotNull(signinRequest);
            assertNotNull(signinResponse);
            assertNotNull(registerRequest);
            assertNotNull(userResponse);
            assertNotNull(registerResponse);
            
            assertEquals(longString, signinRequest.email());
            assertEquals(longString, signinRequest.password());
            assertEquals(longString, signinResponse.token());
            assertEquals(longString, registerRequest.username());
            assertEquals(longString, registerRequest.email());
            assertEquals(longString, registerRequest.password());
            assertEquals(Long.MAX_VALUE, userResponse.id());
            assertEquals(longString, userResponse.username());
            assertEquals(longString, userResponse.email());
            assertEquals(User.Role.USER, userResponse.role());
            assertEquals(userResponse, registerResponse.user());
        }

        @Test
        @DisplayName("Should handle all DTOs with special Unicode characters")
        void shouldHandleAllDTOsWithSpecialUnicodeCharacters() {
            // Arrange
            String unicodeString = "测试用户@example.com 🚀";

            // Act
            SigninRequest signinRequest = new SigninRequest(unicodeString, unicodeString);
            SigninResponse signinResponse = new SigninResponse(unicodeString);
            RegisterRequest registerRequest = new RegisterRequest(unicodeString, unicodeString, unicodeString);
            UserResponse userResponse = new UserResponse(1L, unicodeString, unicodeString, User.Role.USER);
            RegisterResponse registerResponse = new RegisterResponse(userResponse);

            // Assert
            assertNotNull(signinRequest);
            assertNotNull(signinResponse);
            assertNotNull(registerRequest);
            assertNotNull(userResponse);
            assertNotNull(registerResponse);
            
            assertEquals(unicodeString, signinRequest.email());
            assertEquals(unicodeString, signinRequest.password());
            assertEquals(unicodeString, signinResponse.token());
            assertEquals(unicodeString, registerRequest.username());
            assertEquals(unicodeString, registerRequest.email());
            assertEquals(unicodeString, registerRequest.password());
            assertEquals(unicodeString, userResponse.username());
            assertEquals(unicodeString, userResponse.email());
            assertEquals(userResponse, registerResponse.user());
        }

        @Test
        @DisplayName("Should test toString methods for all DTOs")
        void shouldTestToStringMethodsForAllDTOs() {
            // Arrange
            SigninRequest signinRequest = new SigninRequest("test@example.com", "password");
            SigninResponse signinResponse = new SigninResponse("token");
            RegisterRequest registerRequest = new RegisterRequest("user", "test@example.com", "password");
            UserResponse userResponse = new UserResponse(1L, "user", "test@example.com", User.Role.USER);
            RegisterResponse registerResponse = new RegisterResponse(userResponse);

            // Act & Assert
            assertNotNull(signinRequest.toString());
            assertNotNull(signinResponse.toString());
            assertNotNull(registerRequest.toString());
            assertNotNull(userResponse.toString());
            assertNotNull(registerResponse.toString());
            
            assertTrue(signinRequest.toString().contains("test@example.com"));
            assertTrue(signinResponse.toString().contains("token"));
            assertTrue(registerRequest.toString().contains("user"));
            assertTrue(userResponse.toString().contains("user"));
            assertTrue(registerResponse.toString().contains("user"));
        }

        @Test
        @DisplayName("Should handle boundary values for Long ID")
        void shouldHandleBoundaryValuesForLongId() {
            // Test with Long.MIN_VALUE
            UserResponse minIdResponse = new UserResponse(Long.MIN_VALUE, "user", "test@example.com", User.Role.USER);
            assertEquals(Long.MIN_VALUE, minIdResponse.id());

            // Test with Long.MAX_VALUE
            UserResponse maxIdResponse = new UserResponse(Long.MAX_VALUE, "user", "test@example.com", User.Role.USER);
            assertEquals(Long.MAX_VALUE, maxIdResponse.id());

            // Test with 0
            UserResponse zeroIdResponse = new UserResponse(0L, "user", "test@example.com", User.Role.USER);
            assertEquals(0L, zeroIdResponse.id());

            // Test with negative value
            UserResponse negativeIdResponse = new UserResponse(-1L, "user", "test@example.com", User.Role.USER);
            assertEquals(-1L, negativeIdResponse.id());
        }
    }
}
