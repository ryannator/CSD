package csd.tariff.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Comprehensive unit tests for TariffCalculationRequest and TariffCalculationResponse DTOs
 * Tests validation, constructors, getters/setters, and business logic
 */
@DisplayName("Tariff Calculation DTOs Unit Tests")
class TariffCalculationDtosTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ===== TARIFF CALCULATION REQUEST TESTS =====
    
    @Test
    @DisplayName("Should create TariffCalculationRequest with valid data")
    void TariffCalculationRequest_ShouldCreate_WithValidData() {
        // Arrange
        String htsCode = "12345678";
        String originCountry = "CA";
        String destinationCountry = "US";
        BigDecimal productValue = new BigDecimal("1000.00");
        Integer quantity = 10;
        String currency = "USD";

        // Act
        TariffCalculationRequest request = new TariffCalculationRequest(
            htsCode, originCountry, destinationCountry, productValue, quantity, currency);

        // Assert
        assertEquals(htsCode, request.getHtsCode());
        assertEquals(originCountry, request.getOriginCountry());
        assertEquals(destinationCountry, request.getDestinationCountry());
        assertEquals(productValue, request.getProductValue());
        assertEquals(quantity, request.getQuantity());
        assertEquals(currency, request.getCurrency());
        assertNull(request.getTariffEffectiveDate());
        assertNull(request.getTariffExpirationDate());
    }

    @Test
    @DisplayName("Should create TariffCalculationRequest with tariff date range")
    void TariffCalculationRequest_ShouldCreate_WithTariffDateRange() {
        // Arrange
        String htsCode = "12345678";
        String originCountry = "CA";
        String destinationCountry = "US";
        BigDecimal productValue = new BigDecimal("1000.00");
        Integer quantity = 10;
        String currency = "USD";
        LocalDate effectiveDate = LocalDate.of(2024, 1, 1);
        LocalDate expirationDate = LocalDate.of(2024, 12, 31);

        // Act
        TariffCalculationRequest request = new TariffCalculationRequest(
            htsCode, originCountry, destinationCountry, productValue, quantity, currency, 
            effectiveDate, expirationDate);

        // Assert
        assertEquals(htsCode, request.getHtsCode());
        assertEquals(originCountry, request.getOriginCountry());
        assertEquals(destinationCountry, request.getDestinationCountry());
        assertEquals(productValue, request.getProductValue());
        assertEquals(quantity, request.getQuantity());
        assertEquals(currency, request.getCurrency());
        assertEquals(effectiveDate, request.getTariffEffectiveDate());
        assertEquals(expirationDate, request.getTariffExpirationDate());
    }

    @Test
    @DisplayName("Should create TariffCalculationRequest with default currency")
    void TariffCalculationRequest_ShouldCreate_WithDefaultCurrency() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();

        // Act
        request.setHtsCode("12345678");
        request.setOriginCountry("CA");
        request.setDestinationCountry("US");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(10);

        // Assert
        assertEquals("USD", request.getCurrency()); // Default currency
    }

    @Test
    @DisplayName("Should validate TariffCalculationRequest successfully with valid data")
    void TariffCalculationRequest_ShouldValidateSuccessfully_WithValidData() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("CA");
        request.setDestinationCountry("US");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(10);
        request.setCurrency("USD");

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when HTS code is blank")
    void TariffCalculationRequest_ShouldFailValidation_WhenHtsCodeIsBlank() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("   "); // Blank HTS code
        request.setOriginCountry("CA");
        request.setDestinationCountry("US");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(10);
        request.setCurrency("USD");

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("htsCode") && 
            v.getMessage().equals("HTS code is required")));
    }

    @Test
    @DisplayName("Should fail validation when HTS code is null")
    void TariffCalculationRequest_ShouldFailValidation_WhenHtsCodeIsNull() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode(null); // Null HTS code
        request.setOriginCountry("CA");
        request.setDestinationCountry("US");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(10);
        request.setCurrency("USD");

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("htsCode") && 
            v.getMessage().equals("HTS code is required")));
    }

    @Test
    @DisplayName("Should fail validation when origin country is blank")
    void TariffCalculationRequest_ShouldFailValidation_WhenOriginCountryIsBlank() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("   "); // Blank origin country
        request.setDestinationCountry("US");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(10);
        request.setCurrency("USD");

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("originCountry") && 
            v.getMessage().equals("Origin country is required")));
    }

    @Test
    @DisplayName("Should fail validation when destination country is blank")
    void TariffCalculationRequest_ShouldFailValidation_WhenDestinationCountryIsBlank() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("CA");
        request.setDestinationCountry("   "); // Blank destination country
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(10);
        request.setCurrency("USD");

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("destinationCountry") && 
            v.getMessage().equals("Destination country is required")));
    }

    @Test
    @DisplayName("Should fail validation when product value is null")
    void TariffCalculationRequest_ShouldFailValidation_WhenProductValueIsNull() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("CA");
        request.setDestinationCountry("US");
        request.setProductValue(null); // Null product value
        request.setQuantity(10);
        request.setCurrency("USD");

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("productValue") && 
            v.getMessage().equals("Product value is required")));
    }

    @Test
    @DisplayName("Should fail validation when product value is negative")
    void TariffCalculationRequest_ShouldFailValidation_WhenProductValueIsNegative() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("CA");
        request.setDestinationCountry("US");
        request.setProductValue(new BigDecimal("-100.00")); // Negative product value
        request.setQuantity(10);
        request.setCurrency("USD");

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("productValue") && 
            v.getMessage().equals("Product value must be positive")));
    }

    @Test
    @DisplayName("Should fail validation when product value is zero")
    void TariffCalculationRequest_ShouldFailValidation_WhenProductValueIsZero() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("CA");
        request.setDestinationCountry("US");
        request.setProductValue(BigDecimal.ZERO); // Zero product value
        request.setQuantity(10);
        request.setCurrency("USD");

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("productValue") && 
            v.getMessage().equals("Product value must be positive")));
    }

    @Test
    @DisplayName("Should fail validation when quantity is null")
    void TariffCalculationRequest_ShouldFailValidation_WhenQuantityIsNull() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("CA");
        request.setDestinationCountry("US");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(null); // Null quantity
        request.setCurrency("USD");

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("quantity") && 
            v.getMessage().equals("Quantity is required")));
    }

    @Test
    @DisplayName("Should fail validation when quantity is negative")
    void TariffCalculationRequest_ShouldFailValidation_WhenQuantityIsNegative() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("CA");
        request.setDestinationCountry("US");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(-5); // Negative quantity
        request.setCurrency("USD");

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("quantity") && 
            v.getMessage().equals("Quantity must be positive")));
    }

    @Test
    @DisplayName("Should fail validation when quantity is zero")
    void TariffCalculationRequest_ShouldFailValidation_WhenQuantityIsZero() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("CA");
        request.setDestinationCountry("US");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(0); // Zero quantity
        request.setCurrency("USD");

        // Act
        var violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("quantity") && 
            v.getMessage().equals("Quantity must be positive")));
    }

    @Test
    @DisplayName("Should validate successfully with tariff date range")
    void TariffCalculationRequest_ShouldValidateSuccessfully_WithTariffDateRange() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("CA");
        request.setDestinationCountry("US");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(10);
        request.setCurrency("USD");
        request.setTariffEffectiveDate(LocalDate.of(2024, 1, 1));
        request.setTariffExpirationDate(LocalDate.of(2024, 12, 31));

        // Act
        var violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should test toString method of TariffCalculationRequest")
    void TariffCalculationRequest_ShouldHaveCorrectToString() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("CA");
        request.setDestinationCountry("US");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(10);
        request.setCurrency("USD");
        request.setTariffEffectiveDate(LocalDate.of(2024, 1, 1));
        request.setTariffExpirationDate(LocalDate.of(2024, 12, 31));

        // Act
        String toString = request.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("htsCode='12345678'"));
        assertTrue(toString.contains("originCountry='CA'"));
        assertTrue(toString.contains("destinationCountry='US'"));
        assertTrue(toString.contains("productValue=1000.00"));
        assertTrue(toString.contains("quantity=10"));
        assertTrue(toString.contains("currency='USD'"));
        assertTrue(toString.contains("tariffEffectiveDate=2024-01-01"));
        assertTrue(toString.contains("tariffExpirationDate=2024-12-31"));
    }

    // ===== TARIFF CALCULATION RESPONSE TESTS =====
    
    @Test
    @DisplayName("Should create TariffCalculationResponse with valid data")
    void TariffCalculationResponse_ShouldCreate_WithValidData() {
        // Arrange
        String htsCode = "12345678";
        String productDescription = "Test Product Description";
        String originCountry = "CA";
        String destinationCountry = "US";
        BigDecimal productValue = new BigDecimal("1000.00");
        Integer quantity = 10;
        String currency = "USD";

        // Act
        TariffCalculationResponse response = new TariffCalculationResponse(
            htsCode, productDescription, originCountry, destinationCountry, 
            productValue, quantity, currency);

        // Assert
        assertEquals(htsCode, response.getHtsCode());
        assertEquals(productDescription, response.getProductDescription());
        assertEquals(originCountry, response.getOriginCountry());
        assertEquals(destinationCountry, response.getDestinationCountry());
        assertEquals(productValue, response.getProductValue());
        assertEquals(quantity, response.getQuantity());
        assertEquals(currency, response.getCurrency());
        assertNotNull(response.getCalculationTimestamp());
    }

    @Test
    @DisplayName("Should create TariffCalculationResponse with default constructor")
    void TariffCalculationResponse_ShouldCreate_WithDefaultConstructor() {
        // Act
        TariffCalculationResponse response = new TariffCalculationResponse();

        // Assert
        assertNull(response.getHtsCode());
        assertNull(response.getProductDescription());
        assertNull(response.getOriginCountry());
        assertNull(response.getDestinationCountry());
        assertNull(response.getProductValue());
        assertNull(response.getQuantity());
        assertNull(response.getCurrency());
        assertNotNull(response.getCalculationTimestamp());
    }

    @Test
    @DisplayName("Should set and get all TariffCalculationResponse fields correctly")
    void TariffCalculationResponse_ShouldSetAndGetAllFields_Correctly() {
        // Arrange
        TariffCalculationResponse response = new TariffCalculationResponse();
        LocalDateTime timestamp = LocalDateTime.now();

        // Act
        response.setHtsCode("12345678");
        response.setProductDescription("Test Product Description");
        response.setOriginCountry("CA");
        response.setDestinationCountry("US");
        response.setProductValue(new BigDecimal("1000.00"));
        response.setQuantity(10);
        response.setCurrency("USD");
        response.setProgramType("MFN");
        response.setProgramName("MFN");
        response.setAppliedRateLabel("10% + $5.00 per unit");
        response.setTotalTariffAmount(new BigDecimal("150.00"));
        response.setTotalImportPrice(new BigDecimal("1150.00"));
        response.setSavingsVsMfn(BigDecimal.ZERO);
        response.setApplicableAgreements(Arrays.asList("USMCA - US-Mexico-Canada Agreement"));
        response.setEffectiveDate("2024-01-01");
        response.setNotes("Ensure proper documentation for preferential treatment");
        response.setCalculationTimestamp(timestamp);

        // Assert
        assertEquals("12345678", response.getHtsCode());
        assertEquals("Test Product Description", response.getProductDescription());
        assertEquals("CA", response.getOriginCountry());
        assertEquals("US", response.getDestinationCountry());
        assertEquals(new BigDecimal("1000.00"), response.getProductValue());
        assertEquals(10, response.getQuantity());
        assertEquals("USD", response.getCurrency());
        assertEquals("MFN", response.getProgramType());
        assertEquals("MFN", response.getProgramName());
        assertEquals("10% + $5.00 per unit", response.getAppliedRateLabel());
        assertEquals(new BigDecimal("150.00"), response.getTotalTariffAmount());
        assertEquals(new BigDecimal("1150.00"), response.getTotalImportPrice());
        assertEquals(BigDecimal.ZERO, response.getSavingsVsMfn());
        assertEquals(Arrays.asList("USMCA - US-Mexico-Canada Agreement"), response.getApplicableAgreements());
        assertEquals("2024-01-01", response.getEffectiveDate());
        assertEquals("Ensure proper documentation for preferential treatment", response.getNotes());
        assertEquals(timestamp, response.getCalculationTimestamp());
    }

    @Test
    @DisplayName("Should handle null values in TariffCalculationResponse")
    void TariffCalculationResponse_ShouldHandleNullValues_Correctly() {
        // Arrange
        TariffCalculationResponse response = new TariffCalculationResponse();

        // Act
        response.setHtsCode(null);
        response.setProductDescription(null);
        response.setOriginCountry(null);
        response.setDestinationCountry(null);
        response.setProductValue(null);
        response.setQuantity(null);
        response.setCurrency(null);
        response.setProgramType(null);
        response.setProgramName(null);
        response.setAppliedRateLabel(null);
        response.setTotalTariffAmount(null);
        response.setTotalImportPrice(null);
        response.setSavingsVsMfn(null);
        response.setApplicableAgreements(null);
        response.setEffectiveDate(null);
        response.setNotes(null);
        response.setCalculationTimestamp(null);

        // Assert
        assertNull(response.getHtsCode());
        assertNull(response.getProductDescription());
        assertNull(response.getOriginCountry());
        assertNull(response.getDestinationCountry());
        assertNull(response.getProductValue());
        assertNull(response.getQuantity());
        assertNull(response.getCurrency());
        assertNull(response.getProgramType());
        assertNull(response.getProgramName());
        assertNull(response.getAppliedRateLabel());
        assertNull(response.getTotalTariffAmount());
        assertNull(response.getTotalImportPrice());
        assertNull(response.getSavingsVsMfn());
        assertNull(response.getApplicableAgreements());
        assertNull(response.getEffectiveDate());
        assertNull(response.getNotes());
        assertNull(response.getCalculationTimestamp());
    }

    @Test
    @DisplayName("Should handle empty list in applicable agreements")
    void TariffCalculationResponse_ShouldHandleEmptyList_InApplicableAgreements() {
        // Arrange
        TariffCalculationResponse response = new TariffCalculationResponse();
        List<String> emptyList = Arrays.asList();

        // Act
        response.setApplicableAgreements(emptyList);

        // Assert
        assertNotNull(response.getApplicableAgreements());
        assertTrue(response.getApplicableAgreements().isEmpty());
    }

    @Test
    @DisplayName("Should handle multiple applicable agreements")
    void TariffCalculationResponse_ShouldHandleMultipleAgreements_Correctly() {
        // Arrange
        TariffCalculationResponse response = new TariffCalculationResponse();
        List<String> agreements = Arrays.asList(
            "USMCA - US-Mexico-Canada Agreement",
            "GSP - Generalized System of Preferences",
            "CUSMA - Canada-United States-Mexico Agreement"
        );

        // Act
        response.setApplicableAgreements(agreements);

        // Assert
        assertNotNull(response.getApplicableAgreements());
        assertEquals(3, response.getApplicableAgreements().size());
        assertTrue(response.getApplicableAgreements().contains("USMCA - US-Mexico-Canada Agreement"));
        assertTrue(response.getApplicableAgreements().contains("GSP - Generalized System of Preferences"));
        assertTrue(response.getApplicableAgreements().contains("CUSMA - Canada-United States-Mexico Agreement"));
    }

    @Test
    @DisplayName("Should calculate savings correctly when preferential rate is used")
    void TariffCalculationResponse_ShouldCalculateSavings_WhenPreferentialRateUsed() {
        // Arrange
        TariffCalculationResponse response = new TariffCalculationResponse();
        BigDecimal mfnTariff = new BigDecimal("200.00");
        BigDecimal preferentialTariff = new BigDecimal("150.00");
        BigDecimal expectedSavings = mfnTariff.subtract(preferentialTariff);

        // Act
        response.setTotalTariffAmount(preferentialTariff);
        response.setSavingsVsMfn(expectedSavings);

        // Assert
        assertEquals(preferentialTariff, response.getTotalTariffAmount());
        assertEquals(expectedSavings, response.getSavingsVsMfn());
        assertEquals(new BigDecimal("50.00"), response.getSavingsVsMfn());
    }

    @Test
    @DisplayName("Should test toString method of TariffCalculationResponse")
    void TariffCalculationResponse_ShouldHaveCorrectToString() {
        // Arrange
        TariffCalculationResponse response = new TariffCalculationResponse();
        response.setHtsCode("12345678");
        response.setOriginCountry("CA");
        response.setDestinationCountry("US");
        response.setProductValue(new BigDecimal("1000.00"));
        response.setQuantity(10);
        response.setCurrency("USD");
        response.setProgramType("MFN");
        response.setProgramName("MFN");
        response.setAppliedRateLabel("10% + $5.00 per unit");
        response.setTotalTariffAmount(new BigDecimal("150.00"));
        response.setSavingsVsMfn(BigDecimal.ZERO);
        response.setTotalImportPrice(new BigDecimal("1150.00"));
        response.setApplicableAgreements(Arrays.asList("USMCA - US-Mexico-Canada Agreement"));
        response.setEffectiveDate("2024-01-01");
        response.setNotes("Ensure proper documentation for preferential treatment");

        // Act
        String toString = response.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("htsCode='12345678'"));
        assertTrue(toString.contains("originCountry='CA'"));
        assertTrue(toString.contains("destinationCountry='US'"));
        assertTrue(toString.contains("productValue=1000.00"));
        assertTrue(toString.contains("quantity=10"));
        assertTrue(toString.contains("currency='USD'"));
        assertTrue(toString.contains("programType='MFN'"));
        assertTrue(toString.contains("programName='MFN'"));
        assertTrue(toString.contains("appliedRateLabel='10% + $5.00 per unit'"));
        assertTrue(toString.contains("totalTariffAmount=150.00"));
        assertTrue(toString.contains("savingsVsMfn=0"));
        assertTrue(toString.contains("totalImportPrice=1150.00"));
        assertTrue(toString.contains("applicableAgreements=[USMCA - US-Mexico-Canada Agreement]"));
        assertTrue(toString.contains("effectiveDate='2024-01-01'"));
        assertTrue(toString.contains("notes='Ensure proper documentation for preferential treatment'"));
    }

    @Test
    @DisplayName("Should handle BigDecimal precision correctly")
    void TariffCalculationResponse_ShouldHandleBigDecimalPrecision_Correctly() {
        // Arrange
        TariffCalculationResponse response = new TariffCalculationResponse();
        BigDecimal preciseValue = new BigDecimal("1234.5678");
        BigDecimal roundedValue = preciseValue.setScale(2, java.math.RoundingMode.HALF_UP);

        // Act
        response.setProductValue(preciseValue);
        response.setTotalTariffAmount(roundedValue);

        // Assert
        assertEquals(preciseValue, response.getProductValue());
        assertEquals(roundedValue, response.getTotalTariffAmount());
        assertEquals(new BigDecimal("1234.57"), response.getTotalTariffAmount());
    }

    @Test
    @DisplayName("Should handle currency conversion scenarios")
    void TariffCalculationResponse_ShouldHandleCurrencyConversion_Correctly() {
        // Arrange
        TariffCalculationResponse response = new TariffCalculationResponse();
        BigDecimal usdValue = new BigDecimal("1000.00");
        BigDecimal cadValue = new BigDecimal("1350.00"); // Assuming 1 USD = 1.35 CAD

        // Act
        response.setCurrency("USD");
        response.setProductValue(usdValue);
        response.setTotalImportPrice(usdValue);

        // Simulate currency conversion
        response.setCurrency("CAD");
        response.setProductValue(cadValue);
        response.setTotalImportPrice(cadValue);

        // Assert
        assertEquals("CAD", response.getCurrency());
        assertEquals(cadValue, response.getProductValue());
        assertEquals(cadValue, response.getTotalImportPrice());
    }
}
