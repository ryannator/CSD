package csd.tariff.backend.unit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import csd.tariff.backend.model.AgreementRate;
import csd.tariff.backend.model.MfnTariffRate;
import csd.tariff.backend.model.Product;
import csd.tariff.backend.model.TariffCalculation;
import csd.tariff.backend.model.TradeAgreement;
import csd.tariff.backend.model.User;
import csd.tariff.backend.repository.AgreementRateRepository;
import csd.tariff.backend.repository.MfnTariffRateRepository;
import csd.tariff.backend.repository.ProductRepository;
import csd.tariff.backend.repository.TariffCalculationRepository;
import csd.tariff.backend.repository.TradeAgreementRepository;
import csd.tariff.backend.repository.UserRepository;
import csd.tariff.backend.service.CurrencyService;
import csd.tariff.backend.service.MfnService;
import csd.tariff.backend.service.ProductService;
import csd.tariff.backend.service.TariffCalculationServiceImpl;
import csd.tariff.backend.service.TradeAgreementService;

/**
 * Additional service tests to improve coverage
 * Tests edge cases and error scenarios
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Additional Service Tests")
class AdditionalServiceTests {

    @Mock
    private TariffCalculationRepository tariffCalculationRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MfnTariffRateRepository mfnTariffRateRepository;

    @Mock
    private TradeAgreementRepository tradeAgreementRepository;

    @Mock
    private AgreementRateRepository agreementRateRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MfnService mfnService;

    @Mock
    private ProductService productService;

    @Mock
    private TradeAgreementService tradeAgreementService;

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private TariffCalculationServiceImpl tariffCalculationService;

    private Product testProduct;
    private TariffCalculation testCalculation;
    private User testUser;
    private MfnTariffRate testMfnRate;
    private TradeAgreement testAgreement;
    private AgreementRate testAgreementRate;

    @BeforeEach
    void setUp() {
        // Setup test product
        testProduct = new Product();
        testProduct.setHts8("12345678");
        testProduct.setBriefDescription("Test Product");

        // Setup test calculation
        testCalculation = new TariffCalculation();
        testCalculation.setHtsCode("12345678");
        testCalculation.setCountryCode("US");
        testCalculation.setProductValue(new BigDecimal("1000.00"));
        testCalculation.setQuantity(1);
        testCalculation.setCalculationResult(new BigDecimal("50.00"));

        // Setup test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(User.Role.USER);

        // Setup test MFN rate
        testMfnRate = new MfnTariffRate();
        testMfnRate.setProduct(testProduct);
        testMfnRate.setMfnadValoremRate(new BigDecimal("5.0"));
        testMfnRate.setBeginEffectDate(LocalDate.of(2024, 1, 1));

        // Setup test agreement
        testAgreement = new TradeAgreement();
        testAgreement.setAgreementName("US-Mexico-Canada Agreement");
        testAgreement.setAgreementCode("USMCA");
        testAgreement.setEffectiveDate(LocalDate.of(2020, 7, 1));

        // Setup test agreement rate
        testAgreementRate = new AgreementRate();
        testAgreementRate.setProduct(testProduct);
        testAgreementRate.setAgreement(testAgreement);
        testAgreementRate.setadValoremRate(new BigDecimal("2.5"));
    }

    @Test
    @DisplayName("Should handle null input gracefully in tariff calculation")
    void calculateTariff_ShouldHandleNullInput() {
        // When & Then
        assertDoesNotThrow(() -> {
            tariffCalculationService.calculateTariff(null, "US", 1000.0, 1);
            tariffCalculationService.calculateTariff("12345678", null, 1000.0, 1);
            tariffCalculationService.calculateTariff("12345678", "US", null, 1);
            tariffCalculationService.calculateTariff("12345678", "US", 1000.0, null);
        });
    }

    @Test
    @DisplayName("Should handle negative values in tariff calculation")
    void calculateTariff_ShouldHandleNegativeValues() {
        // When
        Map<String, Object> result = tariffCalculationService.calculateTariff("12345678", "US", -1000.0, 1);

        // Then
        assertNotNull(result);
        // Should handle negative values gracefully
    }

    @Test
    @DisplayName("Should handle zero values in tariff calculation")
    void calculateTariff_ShouldHandleZeroValues() {
        // When
        Map<String, Object> result = tariffCalculationService.calculateTariff("12345678", "US", 0.0, 1);

        // Then
        assertNotNull(result);
        // Should handle zero values gracefully
    }

    @Test
    @DisplayName("Should validate HTS code format")
    void validateHtsCode_ShouldValidateFormat() {
        // Given
        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));

        // When
        Map<String, Object> result = tariffCalculationService.validateHtsCode("12345678");

        // Then
        assertNotNull(result);
        assertTrue((Boolean) result.get("valid"));

        verify(productRepository, times(1)).findByHts8("12345678");
    }

    @Test
    @DisplayName("Should invalidate HTS code when product not found")
    void validateHtsCode_ShouldInvalidateWhenProductNotFound() {
        // Given
        when(productRepository.findByHts8("99999999")).thenReturn(Optional.empty());

        // When
        Map<String, Object> result = tariffCalculationService.validateHtsCode("99999999");

        // Then
        assertNotNull(result);
        assertFalse((Boolean) result.get("valid"));

        verify(productRepository, times(1)).findByHts8("99999999");
    }

    @Test
    @DisplayName("Should handle null HTS code in validation")
    void validateHtsCode_ShouldHandleNullHtsCode() {
        // When
        Map<String, Object> result = tariffCalculationService.validateHtsCode(null);

        // Then
        assertNotNull(result);
        assertFalse((Boolean) result.get("valid"));
    }

    @Test
    @DisplayName("Should handle empty HTS code in validation")
    void validateHtsCode_ShouldHandleEmptyHtsCode() {
        // When
        Map<String, Object> result = tariffCalculationService.validateHtsCode("");

        // Then
        assertNotNull(result);
        assertFalse((Boolean) result.get("valid"));
    }

    @Test
    @DisplayName("Should handle invalid HTS code length")
    void validateHtsCode_ShouldHandleInvalidLength() {
        // When
        Map<String, Object> result = tariffCalculationService.validateHtsCode("123");

        // Then
        assertNotNull(result);
        assertFalse((Boolean) result.get("valid"));
    }

    @Test
    @DisplayName("Should calculate duty with ad valorem rate")
    void calculateDuty_ShouldCalculateWithAdValoremRate() {
        // Given
        BigDecimal adValoremRate = new BigDecimal("0.05"); // 5% as fraction
        BigDecimal specificRate = BigDecimal.ZERO;
        Double productValue = 1000.0;
        Integer quantity = 1;

        // When
        BigDecimal result = tariffCalculationService.calculateDuty(adValoremRate, specificRate, productValue, quantity);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("50.00"), result);
    }

    @Test
    @DisplayName("Should calculate duty with specific rate")
    void calculateDuty_ShouldCalculateWithSpecificRate() {
        // Given
        BigDecimal adValoremRate = BigDecimal.ZERO;
        BigDecimal specificRate = new BigDecimal("10.0");
        Double productValue = 1000.0;
        Integer quantity = 2;

        // When
        BigDecimal result = tariffCalculationService.calculateDuty(adValoremRate, specificRate, productValue, quantity);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("20.00"), result);
    }

    @Test
    @DisplayName("Should calculate duty with both rates")
    void calculateDuty_ShouldCalculateWithBothRates() {
        // Given
        BigDecimal adValoremRate = new BigDecimal("0.05"); // 5% as fraction
        BigDecimal specificRate = new BigDecimal("10.0");
        Double productValue = 1000.0;
        Integer quantity = 1;

        // When
        BigDecimal result = tariffCalculationService.calculateDuty(adValoremRate, specificRate, productValue, quantity);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("60.00"), result);
    }

    @Test
    @DisplayName("Should handle null rates in duty calculation")
    void calculateDuty_ShouldHandleNullRates() {
        // When
        BigDecimal result = tariffCalculationService.calculateDuty(null, null, 1000.0, 1);

        // Then
        assertNotNull(result);
        assertEquals(0, result.compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Should handle zero values in duty calculation")
    void calculateDuty_ShouldHandleZeroValues() {
        // When
        BigDecimal result = tariffCalculationService.calculateDuty(BigDecimal.ZERO, BigDecimal.ZERO, 0.0, 0);

        // Then
        assertNotNull(result);
        assertEquals(0, result.compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Should get cost breakdown")
    void getCostBreakdown_ShouldReturnBreakdown() {
        // Given
        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));

        // When
        Map<String, Object> result = tariffCalculationService.getCostBreakdown("12345678", "US", 1000.0, 1);

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("htsCode"));
        assertTrue(result.containsKey("tariffAmount"));
        assertTrue(result.containsKey("totalImportPrice"));

        verify(productRepository, times(1)).findByHts8("12345678");
        verify(mfnService, times(1)).getMfnTariffRate("12345678");
    }

    @Test
    @DisplayName("Should handle product not found in cost breakdown")
    void getCostBreakdown_ShouldHandleProductNotFound() {
        // Given
        when(productRepository.findByHts8("99999999")).thenReturn(Optional.empty());

        // When
        Map<String, Object> result = tariffCalculationService.getCostBreakdown("99999999", "US", 1000.0, 1);

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("error"));

        verify(productRepository, times(1)).findByHts8("99999999");
    }

    @Test
    @DisplayName("Should get applicable trade programs")
    void getApplicableTradePrograms_ShouldReturnPrograms() {
        // Given
        when(tradeAgreementService.getTradeAgreementsBetweenCountries("US", "CA"))
                .thenReturn(Arrays.asList(testAgreement));

        // When
        List<String> result = tariffCalculationService.getApplicableTradePrograms("US", "CA");

        // Then
        assertNotNull(result);
        assertTrue(result.contains("USMCA - US-Mexico-Canada Agreement"));

        verify(tradeAgreementService, times(1)).getTradeAgreementsBetweenCountries("US", "CA");
    }

    @Test
    @DisplayName("Should return empty list when no trade programs found")
    void getApplicableTradePrograms_ShouldReturnEmptyList() {
        // Given
        when(tradeAgreementService.getTradeAgreementsBetweenCountries("XX", "YY"))
                .thenReturn(Collections.emptyList());

        // When
        List<String> result = tariffCalculationService.getApplicableTradePrograms("XX", "YY");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(tradeAgreementService, times(1)).getTradeAgreementsBetweenCountries("XX", "YY");
    }

    @Test
    @DisplayName("Should handle null countries in trade programs")
    void getApplicableTradePrograms_ShouldHandleNullCountries() {
        // When
        List<String> result = tariffCalculationService.getApplicableTradePrograms(null, null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle service exceptions gracefully")
    void shouldHandleServiceExceptions() {
        // Given
        when(productRepository.findByHts8("12345678")).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            tariffCalculationService.validateHtsCode("12345678");
        });

        verify(productRepository, times(1)).findByHts8("12345678");
    }
}
