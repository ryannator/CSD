package csd.tariff.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * DTO validation tests
 * Tests validation annotations and constraints
 */
@DisplayName("DTO Validation Tests")
class DtoValidationTests {

    private Validator validator;

    public DtoValidationTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should validate valid TariffCalculationRequest")
    void validateTariffCalculationRequest_ShouldPassForValidRequest() {
        // Given
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("US");
        request.setDestinationCountry("CA");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(1);

        // When
        Set<ConstraintViolation<TariffCalculationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation for missing HTS code")
    void validateTariffCalculationRequest_ShouldFailForMissingHtsCode() {
        // Given
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setOriginCountry("US");
        request.setDestinationCountry("CA");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(1);

        // When
        Set<ConstraintViolation<TariffCalculationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("htsCode")));
    }

    @Test
    @DisplayName("Should fail validation for missing origin country")
    void validateTariffCalculationRequest_ShouldFailForMissingOriginCountry() {
        // Given
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setDestinationCountry("CA");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(1);

        // When
        Set<ConstraintViolation<TariffCalculationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("originCountry")));
    }

    @Test
    @DisplayName("Should fail validation for missing destination country")
    void validateTariffCalculationRequest_ShouldFailForMissingDestinationCountry() {
        // Given
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("US");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(1);

        // When
        Set<ConstraintViolation<TariffCalculationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("destinationCountry")));
    }

    @Test
    @DisplayName("Should fail validation for missing product value")
    void validateTariffCalculationRequest_ShouldFailForMissingProductValue() {
        // Given
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("US");
        request.setDestinationCountry("CA");
        request.setQuantity(1);

        // When
        Set<ConstraintViolation<TariffCalculationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("productValue")));
    }

    @Test
    @DisplayName("Should fail validation for missing quantity")
    void validateTariffCalculationRequest_ShouldFailForMissingQuantity() {
        // Given
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("US");
        request.setDestinationCountry("CA");
        request.setProductValue(new BigDecimal("1000.00"));

        // When
        Set<ConstraintViolation<TariffCalculationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }

    @Test
    @DisplayName("Should fail validation for negative product value")
    void validateTariffCalculationRequest_ShouldFailForNegativeProductValue() {
        // Given
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("US");
        request.setDestinationCountry("CA");
        request.setProductValue(new BigDecimal("-1000.00"));
        request.setQuantity(1);

        // When
        Set<ConstraintViolation<TariffCalculationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("productValue")));
    }

    @Test
    @DisplayName("Should fail validation for negative quantity")
    void validateTariffCalculationRequest_ShouldFailForNegativeQuantity() {
        // Given
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("US");
        request.setDestinationCountry("CA");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(-1);

        // When
        Set<ConstraintViolation<TariffCalculationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }

    @Test
    @DisplayName("Should fail validation for zero product value")
    void validateTariffCalculationRequest_ShouldFailForZeroProductValue() {
        // Given
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("US");
        request.setDestinationCountry("CA");
        request.setProductValue(BigDecimal.ZERO);
        request.setQuantity(1);

        // When
        Set<ConstraintViolation<TariffCalculationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("productValue")));
    }

    @Test
    @DisplayName("Should fail validation for zero quantity")
    void validateTariffCalculationRequest_ShouldFailForZeroQuantity() {
        // Given
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("US");
        request.setDestinationCountry("CA");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(0);

        // When
        Set<ConstraintViolation<TariffCalculationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }

    @Test
    @DisplayName("Should validate request with date range")
    void validateTariffCalculationRequest_ShouldPassWithDateRange() {
        // Given
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("US");
        request.setDestinationCountry("CA");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(1);
        request.setTariffEffectiveDate(LocalDate.of(2024, 1, 1));
        request.setTariffExpirationDate(LocalDate.of(2024, 12, 31));

        // When
        Set<ConstraintViolation<TariffCalculationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should validate request with currency")
    void validateTariffCalculationRequest_ShouldPassWithCurrency() {
        // Given
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("US");
        request.setDestinationCountry("CA");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(1);
        request.setCurrency("EUR");

        // When
        Set<ConstraintViolation<TariffCalculationRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void validateTariffCalculationRequest_ShouldHandleNullValues() {
        // Given
        TariffCalculationRequest request = new TariffCalculationRequest();

        // When
        Set<ConstraintViolation<TariffCalculationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        // Should have violations for all required fields
        assertTrue(violations.size() >= 5);
    }

    @Test
    @DisplayName("Should validate empty strings")
    void validateTariffCalculationRequest_ShouldFailForEmptyStrings() {
        // Given
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("");
        request.setOriginCountry("");
        request.setDestinationCountry("");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(1);

        // When
        Set<ConstraintViolation<TariffCalculationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("htsCode")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("originCountry")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("destinationCountry")));
    }

    @Test
    @DisplayName("Should validate whitespace-only strings")
    void validateTariffCalculationRequest_ShouldFailForWhitespaceStrings() {
        // Given
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("   ");
        request.setOriginCountry("   ");
        request.setDestinationCountry("   ");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(1);

        // When
        Set<ConstraintViolation<TariffCalculationRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("htsCode")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("originCountry")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("destinationCountry")));
    }
}
