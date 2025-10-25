package csd.tariff.backend.model;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Simple unit tests for model classes
 * Tests basic getter/setter functionality
 */
@DisplayName("Model Tests")
class ModelTests {

    @Test
    @DisplayName("Should create TariffCalculation with basic fields")
    void shouldCreateTariffCalculationWithBasicFields() {
        // Given
        TariffCalculation calculation = new TariffCalculation();
        
        // When
        calculation.setHtsCode("12345678");
        calculation.setCountryCode("US");
        calculation.setProductValue(new BigDecimal("1000.00"));
        calculation.setQuantity(1);
        calculation.setCalculationResult(new BigDecimal("50.00"));
        
        // Then
        assertEquals("12345678", calculation.getHtsCode());
        assertEquals("US", calculation.getCountryCode());
        assertEquals(new BigDecimal("1000.00"), calculation.getProductValue());
        assertEquals(1, calculation.getQuantity());
        assertEquals(new BigDecimal("50.00"), calculation.getCalculationResult());
    }

    @Test
    @DisplayName("Should create Product with basic fields")
    void shouldCreateProductWithBasicFields() {
        // Given
        Product product = new Product();
        
        // When
        product.setHts8("12345678");
        product.setBriefDescription("Test Product");
        
        // Then
        assertEquals("12345678", product.getHts8());
        assertEquals("Test Product", product.getBriefDescription());
    }

    @Test
    @DisplayName("Should create User with basic fields")
    void shouldCreateUserWithBasicFields() {
        // Given
        User user = new User();
        
        // When
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole(User.Role.USER);
        
        // Then
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals(User.Role.USER, user.getRole());
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void shouldHandleNullValuesGracefully() {
        // When & Then
        assertDoesNotThrow(() -> {
            TariffCalculation calculation = new TariffCalculation();
            calculation.setHtsCode(null);
            calculation.setCountryCode(null);
            
            Product product = new Product();
            product.setHts8(null);
            product.setBriefDescription(null);
            
            User user = new User();
            user.setUsername(null);
            user.setEmail(null);
        });
    }
}
