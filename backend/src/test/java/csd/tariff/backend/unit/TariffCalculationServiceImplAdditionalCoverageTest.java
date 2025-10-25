package csd.tariff.backend.unit;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import csd.tariff.backend.model.AgreementRate;
import csd.tariff.backend.model.MfnTariffRate;
import csd.tariff.backend.model.Product;
import csd.tariff.backend.model.TradeAgreement;
import csd.tariff.backend.repository.ProductRepository;
import csd.tariff.backend.repository.TariffCalculationRepository;
import csd.tariff.backend.service.CurrencyService;
import csd.tariff.backend.service.MfnService;
import csd.tariff.backend.service.ProductService;
import csd.tariff.backend.service.TariffCalculationServiceImpl;
import csd.tariff.backend.service.TradeAgreementService;

/**
 * Additional TariffCalculationServiceImpl tests to improve coverage
 * Focuses on edge cases and scenarios that might not be fully covered
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TariffCalculationService Additional Coverage Tests")
class TariffCalculationServiceImplAdditionalCoverageTest {

    private ProductRepository productRepository;
    private TariffCalculationRepository tariffCalculationRepository;
    private MfnService mfnService;
    private ProductService productService;
    private TradeAgreementService tradeAgreementService;
    private CurrencyService currencyService;
    private TariffCalculationServiceImpl tariffCalculationService;

    @BeforeEach
    void setUp() {
        productRepository = org.mockito.Mockito.mock(ProductRepository.class);
        tariffCalculationRepository = org.mockito.Mockito.mock(TariffCalculationRepository.class);
        mfnService = org.mockito.Mockito.mock(MfnService.class);
        productService = org.mockito.Mockito.mock(ProductService.class);
        tradeAgreementService = org.mockito.Mockito.mock(TradeAgreementService.class);
        currencyService = org.mockito.Mockito.mock(CurrencyService.class);
        
        tariffCalculationService = new TariffCalculationServiceImpl(
            productRepository, tariffCalculationRepository, mfnService, 
            productService, tradeAgreementService, currencyService);
    }

    // ===== EDGE CASES FOR FIND BY HTS CODE =====

    @Test
    @DisplayName("Should handle findByHtsCode with null input")
    void findByHtsCode_ShouldHandleNullInput() {
        // Act
        Optional<Product> result = tariffCalculationService.findByHtsCode(null);

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, never()).findByHts8(anyString());
    }

    @Test
    @DisplayName("Should handle findByHtsCode with empty string")
    void findByHtsCode_ShouldHandleEmptyString() {
        // Act
        Optional<Product> result = tariffCalculationService.findByHtsCode("");

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, never()).findByHts8(anyString());
    }

    @Test
    @DisplayName("Should handle findByHtsCode with whitespace only")
    void findByHtsCode_ShouldHandleWhitespaceOnly() {
        // Act
        Optional<Product> result = tariffCalculationService.findByHtsCode("   ");

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, never()).findByHts8(anyString());
    }

    @Test
    @DisplayName("Should handle findByHtsCode with short HTS code")
    void findByHtsCode_ShouldHandleShortHtsCode() {
        // Act
        Optional<Product> result = tariffCalculationService.findByHtsCode("1234567");

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, never()).findByHts8(anyString());
    }

    @Test
    @DisplayName("Should handle findByHtsCode with long HTS code")
    void findByHtsCode_ShouldHandleLongHtsCode() {
        // Act
        Optional<Product> result = tariffCalculationService.findByHtsCode("123456789");

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, never()).findByHts8(anyString());
    }

    @Test
    @DisplayName("Should handle findByHtsCode with special characters")
    void findByHtsCode_ShouldHandleSpecialCharacters() {
        // Act
        Optional<Product> result = tariffCalculationService.findByHtsCode("1234-5678");

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, never()).findByHts8(anyString());
    }

    // ===== EDGE CASES FOR CALCULATE TARIFF =====

    @Test
    @DisplayName("Should handle calculateTariff with null HTS code")
    void calculateTariff_ShouldHandleNullHtsCode() {
        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            null, "US", 1000.0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("error"));
        assertEquals("HTS code is required", result.get("error"));
    }

    @Test
    @DisplayName("Should handle calculateTariff with null product value")
    void calculateTariff_ShouldHandleNullProductValue() {
        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", "US", null, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("error"));
        assertEquals("Product value is required", result.get("error"));
    }

    @Test
    @DisplayName("Should handle calculateTariff with null quantity")
    void calculateTariff_ShouldHandleNullQuantity() {
        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", "US", 1000.0, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("error"));
        assertEquals("Quantity is required", result.get("error"));
    }

    @Test
    @DisplayName("Should handle calculateTariff with zero product value")
    void calculateTariff_ShouldHandleZeroProductValue() {
        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", "US", 0.0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("error"));
        assertEquals("Product value must be positive", result.get("error"));
    }

    @Test
    @DisplayName("Should handle calculateTariff with negative product value")
    void calculateTariff_ShouldHandleNegativeProductValue() {
        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", "US", -1000.0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("error"));
        assertEquals("Product value must be positive", result.get("error"));
    }

    @Test
    @DisplayName("Should handle calculateTariff with zero quantity")
    void calculateTariff_ShouldHandleZeroQuantity() {
        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", "US", 1000.0, 0);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("error"));
        assertEquals("Quantity must be positive", result.get("error"));
    }

    @Test
    @DisplayName("Should handle calculateTariff with negative quantity")
    void calculateTariff_ShouldHandleNegativeQuantity() {
        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", "US", 1000.0, -10);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("error"));
        assertEquals("Quantity must be positive", result.get("error"));
    }

    @Test
    @DisplayName("Should handle calculateTariff with very large product value")
    void calculateTariff_ShouldHandleVeryLargeProductValue() {
        // Arrange
        Product product = new Product();
        product.setHts8("12345678");
        product.setBriefDescription("Test Product");

        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(product));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.empty());
        when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", "US", Double.MAX_VALUE, 10);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        assertEquals("12345678", result.get("htsCode"));
    }

    @Test
    @DisplayName("Should handle calculateTariff with very large quantity")
    void calculateTariff_ShouldHandleVeryLargeQuantity() {
        // Arrange
        Product product = new Product();
        product.setHts8("12345678");
        product.setBriefDescription("Test Product");

        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(product));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.empty());
        when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", "US", 1000.0, Integer.MAX_VALUE);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        assertEquals("12345678", result.get("htsCode"));
    }

    @Test
    @DisplayName("Should handle calculateTariff with non-existent HTS code")
    void calculateTariff_ShouldHandleNonExistentHtsCode() {
        // Arrange
        when(productRepository.findByHts8("12345678")).thenReturn(Optional.empty());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", "US", 1000.0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("error"));
        assertEquals("HTS code not found", result.get("error"));
    }

    @Test
    @DisplayName("Should handle calculateTariff with null origin country")
    void calculateTariff_ShouldHandleNullOriginCountry() {
        // Arrange
        Product product = new Product();
        product.setHts8("12345678");
        product.setBriefDescription("Test Product");

        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(product));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.empty());
        when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", null, "US", 1000.0, 10);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        assertNull(result.get("countryOfOrigin"));
    }

    @Test
    @DisplayName("Should handle calculateTariff with null destination country")
    void calculateTariff_ShouldHandleNullDestinationCountry() {
        // Arrange
        Product product = new Product();
        product.setHts8("12345678");
        product.setBriefDescription("Test Product");

        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(product));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.empty());
        when(productService.getAgreementRates("12345678", null)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", "CN", null, 1000.0, 10);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        assertNull(result.get("destinationCountry"));
    }

    // ===== EDGE CASES FOR MFN RATES =====

    @Test
    @DisplayName("Should handle MFN rate with null ad valorem rate")
    void calculateTariff_ShouldHandleMfnRateWithNullAdValoremRate() {
        // Arrange
        Product product = new Product();
        product.setHts8("12345678");
        product.setBriefDescription("Test Product");

        MfnTariffRate mfnRate = new MfnTariffRate();
        mfnRate.setMfnadValoremRate(null);
        mfnRate.setMfnSpecificRate(new BigDecimal("2.50"));
        mfnRate.setMfnTextRate("$2.50 per unit");
        mfnRate.setMfnRateTypeCode("SPC");

        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(product));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(mfnRate));
        when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", "CN", "US", 1000.0, 10);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> mfnInfo = (Map<String, Object>) result.get("mfnRate");
        assertNotNull(mfnInfo);
        assertNull(mfnInfo.get("adValoremRate"));
        assertEquals(new BigDecimal("2.50"), mfnInfo.get("specificRate"));
    }

    @Test
    @DisplayName("Should handle MFN rate with null specific rate")
    void calculateTariff_ShouldHandleMfnRateWithNullSpecificRate() {
        // Arrange
        Product product = new Product();
        product.setHts8("12345678");
        product.setBriefDescription("Test Product");

        MfnTariffRate mfnRate = new MfnTariffRate();
        mfnRate.setMfnadValoremRate(new BigDecimal("0.05"));
        mfnRate.setMfnSpecificRate(null);
        mfnRate.setMfnTextRate("5%");
        mfnRate.setMfnRateTypeCode("ADV");

        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(product));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(mfnRate));
        when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", "CN", "US", 1000.0, 10);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> mfnInfo = (Map<String, Object>) result.get("mfnRate");
        assertNotNull(mfnInfo);
        assertEquals(new BigDecimal("0.05"), mfnInfo.get("adValoremRate"));
        assertNull(mfnInfo.get("specificRate"));
    }

    @Test
    @DisplayName("Should handle MFN rate with both rates null")
    void calculateTariff_ShouldHandleMfnRateWithBothRatesNull() {
        // Arrange
        Product product = new Product();
        product.setHts8("12345678");
        product.setBriefDescription("Test Product");

        MfnTariffRate mfnRate = new MfnTariffRate();
        mfnRate.setMfnadValoremRate(null);
        mfnRate.setMfnSpecificRate(null);
        mfnRate.setMfnTextRate("Free");
        mfnRate.setMfnRateTypeCode("FREE");

        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(product));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(mfnRate));
        when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", "CN", "US", 1000.0, 10);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> mfnInfo = (Map<String, Object>) result.get("mfnRate");
        assertNotNull(mfnInfo);
        assertNull(mfnInfo.get("adValoremRate"));
        assertNull(mfnInfo.get("specificRate"));
        assertEquals(BigDecimal.ZERO, result.get("mfnTariffAmount"));
    }

    // ===== EDGE CASES FOR PREFERENTIAL RATES =====

    @Test
    @DisplayName("Should handle preferential rates with null agreement")
    void calculateTariff_ShouldHandlePreferentialRatesWithNullAgreement() {
        // Arrange
        Product product = new Product();
        product.setHts8("12345678");
        product.setBriefDescription("Test Product");

        AgreementRate agreementRate = new AgreementRate();
        agreementRate.setadValoremRate(new BigDecimal("0.03"));
        agreementRate.setSpecificRate(new BigDecimal("1.50"));
        agreementRate.setTextRate("3% + $1.50 per unit");
        agreementRate.setRateTypeCode("ADV");
        agreementRate.setAgreement(null); // Null agreement

        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(product));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.empty());
        when(productService.getAgreementRates("12345678", "US")).thenReturn(Arrays.asList(agreementRate));

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", "CN", "US", 1000.0, 10);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> preferentialRates = (List<Map<String, Object>>) result.get("preferentialRates");
        assertNotNull(preferentialRates);
        assertEquals(1, preferentialRates.size());
        
        Map<String, Object> prefInfo = preferentialRates.get(0);
        assertNull(prefInfo.get("agreementCode"));
        assertNull(prefInfo.get("agreementName"));
    }

    @Test
    @DisplayName("Should handle preferential rates with null rates")
    void calculateTariff_ShouldHandlePreferentialRatesWithNullRates() {
        // Arrange
        Product product = new Product();
        product.setHts8("12345678");
        product.setBriefDescription("Test Product");

        TradeAgreement agreement = new TradeAgreement();
        agreement.setAgreementCode("USMCA");
        agreement.setAgreementName("US-Mexico-Canada Agreement");

        AgreementRate agreementRate = new AgreementRate();
        agreementRate.setadValoremRate(null);
        agreementRate.setSpecificRate(null);
        agreementRate.setTextRate("Free");
        agreementRate.setRateTypeCode("FREE");
        agreementRate.setAgreement(agreement);

        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(product));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.empty());
        when(productService.getAgreementRates("12345678", "US")).thenReturn(Arrays.asList(agreementRate));

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", "CN", "US", 1000.0, 10);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> preferentialRates = (List<Map<String, Object>>) result.get("preferentialRates");
        assertNotNull(preferentialRates);
        assertEquals(1, preferentialRates.size());
        
        Map<String, Object> prefInfo = preferentialRates.get(0);
        assertNull(prefInfo.get("adValoremRate"));
        assertNull(prefInfo.get("specificRate"));
        assertEquals(BigDecimal.ZERO, prefInfo.get("calculatedDuty"));
    }

    // ===== EDGE CASES FOR TRADE AGREEMENTS =====

    @Test
    @DisplayName("Should handle getApplicableTradePrograms with null countries")
    void getApplicableTradePrograms_ShouldHandleNullCountries() {
        // Arrange
        when(tradeAgreementService.getTradeAgreementsBetweenCountries(null, null))
            .thenReturn(Collections.emptyList());

        // Act
        List<String> result = tariffCalculationService.getApplicableTradePrograms(null, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle getApplicableTradePrograms with empty countries")
    void getApplicableTradePrograms_ShouldHandleEmptyCountries() {
        // Arrange
        when(tradeAgreementService.getTradeAgreementsBetweenCountries("", ""))
            .thenReturn(Collections.emptyList());

        // Act
        List<String> result = tariffCalculationService.getApplicableTradePrograms("", "");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle getApplicableTradePrograms with same countries")
    void getApplicableTradePrograms_ShouldHandleSameCountries() {
        // Arrange
        when(tradeAgreementService.getTradeAgreementsBetweenCountries("US", "US"))
            .thenReturn(Collections.emptyList());

        // Act
        List<String> result = tariffCalculationService.getApplicableTradePrograms("US", "US");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ===== EDGE CASES FOR COMPLIANCE NOTES =====

    @Test
    @DisplayName("Should handle compliance notes with GSP programs")
    void calculateTariff_ShouldHandleComplianceNotesWithGSP() {
        // Arrange
        Product product = new Product();
        product.setHts8("12345678");
        product.setBriefDescription("Test Product");

        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(product));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.empty());
        when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());
        when(tradeAgreementService.getTradeAgreementsBetweenCountries("CN", "US"))
            .thenReturn(Arrays.asList(createTradeAgreement("GSP")));

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", "CN", "US", 1000.0, 10);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        
        @SuppressWarnings("unchecked")
        List<String> notes = (List<String>) result.get("complianceNotes");
        assertNotNull(notes);
        assertTrue(notes.stream().anyMatch(note -> note.contains("GSP")));
    }

    @Test
    @DisplayName("Should handle compliance notes with USMCA programs")
    void calculateTariff_ShouldHandleComplianceNotesWithUSMCA() {
        // Arrange
        Product product = new Product();
        product.setHts8("12345678");
        product.setBriefDescription("Test Product");

        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(product));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.empty());
        when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());
        when(tradeAgreementService.getTradeAgreementsBetweenCountries("CA", "US"))
            .thenReturn(Arrays.asList(createTradeAgreement("USMCA")));

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", "CA", "US", 1000.0, 10);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        
        @SuppressWarnings("unchecked")
        List<String> notes = (List<String>) result.get("complianceNotes");
        assertNotNull(notes);
        assertTrue(notes.stream().anyMatch(note -> note.contains("USMCA")));
    }

    // ===== EDGE CASES FOR EXCEPTION HANDLING =====

    @Test
    @DisplayName("Should handle repository exception gracefully")
    void calculateTariff_ShouldHandleRepositoryException() {
        // Arrange
        when(productRepository.findByHts8("12345678"))
            .thenThrow(new RuntimeException("Database connection error"));

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", "CN", "US", 1000.0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("error"));
        assertTrue(result.get("error").toString().contains("Calculation failed"));
    }

    @Test
    @DisplayName("Should handle service exception gracefully")
    void calculateTariff_ShouldHandleServiceException() {
        // Arrange
        Product product = new Product();
        product.setHts8("12345678");
        product.setBriefDescription("Test Product");

        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(product));
        when(mfnService.getMfnTariffRate("12345678"))
            .thenThrow(new RuntimeException("MFN service error"));

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            "12345678", "CN", "US", 1000.0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("error"));
        assertTrue(result.get("error").toString().contains("Calculation failed"));
    }

    // ===== HELPER METHODS =====

    private TradeAgreement createTradeAgreement(String code) {
        TradeAgreement agreement = new TradeAgreement();
        agreement.setAgreementCode(code);
        agreement.setAgreementName(code + " Agreement");
        agreement.setAgreementType("FTA");
        return agreement;
    }
}
