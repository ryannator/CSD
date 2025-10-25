package csd.tariff.backend.service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import csd.tariff.backend.dto.TariffCalculationRequest;
import csd.tariff.backend.dto.TariffCalculationResponse;
import csd.tariff.backend.model.AgreementRate;
import csd.tariff.backend.model.Country;
import csd.tariff.backend.model.MfnTariffRate;
import csd.tariff.backend.model.Product;
import csd.tariff.backend.model.TariffCalculation;
import csd.tariff.backend.model.TradeAgreement;
import csd.tariff.backend.repository.ProductRepository;
import csd.tariff.backend.repository.TariffCalculationRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("TariffCalculationServiceImpl Branch Coverage Tests")
class TariffCalculationServiceImplBranchCoverageTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TariffCalculationRepository tariffCalculationRepository;

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
    private MfnTariffRate testMfnRate;
    private AgreementRate testAgreementRate;
    private TradeAgreement testAgreement;
    private Country testCountry;

    @BeforeEach
    void setUp() {
        // Setup test product
        testProduct = new Product();
        setProductId(testProduct, 1L);
        testProduct.setHts8("12345678");
        testProduct.setBriefDescription("Test Product");

        // Setup test MFN rate
        testMfnRate = new MfnTariffRate();
        setMfnRateId(testMfnRate, 1L);
        testMfnRate.setProduct(testProduct);
        testMfnRate.setMfnadValoremRate(new BigDecimal("0.05"));
        testMfnRate.setMfnSpecificRate(new BigDecimal("2.50"));
        testMfnRate.setMfnTextRate("5% or $2.50 per unit");
        testMfnRate.setMfnRateTypeCode("ADV");
        testMfnRate.setBeginEffectDate(LocalDate.of(2024, 1, 1));
        testMfnRate.setEndEffectiveDate(LocalDate.of(2025, 12, 31));

        // Setup test agreement
        testAgreement = new TradeAgreement();
        setAgreementId(testAgreement, 1L);
        testAgreement.setAgreementCode("USMCA");
        testAgreement.setAgreementName("US-Mexico-Canada Agreement");
        testAgreement.setAgreementType("Free Trade Agreement");

        // Setup test country
        testCountry = new Country();
        setCountryId(testCountry, 1L);
        testCountry.setCountryCode("US");
        testCountry.setCountryName("United States");

        // Setup test agreement rate
        testAgreementRate = new AgreementRate();
        setAgreementRateId(testAgreementRate, 1L);
        testAgreementRate.setProduct(testProduct);
        testAgreementRate.setAgreement(testAgreement);
        testAgreementRate.setCountry(testCountry);
        testAgreementRate.setadValoremRate(new BigDecimal("0.03"));
        testAgreementRate.setSpecificRate(new BigDecimal("1.50"));
        testAgreementRate.setTextRate("3% or $1.50 per unit");
        testAgreementRate.setRateTypeCode("ADV");
    }

    private void setProductId(Product product, Long id) {
        try {
            Field idField = Product.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(product, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set Product ID", e);
        }
    }

    private void setMfnRateId(MfnTariffRate mfnRate, Long id) {
        try {
            Field idField = MfnTariffRate.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(mfnRate, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set MfnTariffRate ID", e);
        }
    }

    private void setAgreementId(TradeAgreement agreement, Long id) {
        try {
            Field idField = TradeAgreement.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(agreement, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set TradeAgreement ID", e);
        }
    }

    private void setCountryId(Country country, Long id) {
        try {
            Field idField = Country.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(country, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set Country ID", e);
        }
    }

    private void setAgreementRateId(AgreementRate agreementRate, Long id) {
        try {
            Field idField = AgreementRate.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(agreementRate, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set AgreementRate ID", e);
        }
    }

    private void setTariffCalculationId(TariffCalculation calc, Long id) {
        try {
            Field idField = TariffCalculation.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(calc, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set TariffCalculation ID", e);
        }
    }

    @Nested
    @DisplayName("calculateTariff Branch Coverage")
    class CalculateTariffBranchCoverage {

        @Test
        @DisplayName("Should handle empty preferential rates list")
        void calculateTariff_ShouldHandleEmptyPreferentialRates() {
            // Arrange
            when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
            when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
            when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());

            // Act
            Map<String, Object> result = tariffCalculationService.calculateTariff(
                "12345678", "CA", "US", 1000.0, 10);

            // Assert
            assertNotNull(result);
            assertFalse(result.containsKey("error"));
            assertEquals("12345678", result.get("htsCode"));
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> preferentialRates = (List<Map<String, Object>>) result.get("preferentialRates");
            assertTrue(preferentialRates.isEmpty());
            
            @SuppressWarnings("unchecked")
            Map<String, Object> recommended = (Map<String, Object>) result.get("recommendedRate");
            assertEquals("MFN", recommended.get("rateType"));
            assertEquals("MFN", recommended.get("programName"));
        }

        @Test
        @DisplayName("Should handle MFN rate with null ad valorem rate")
        void calculateTariff_ShouldHandleMfnRateWithNullAdValoremRate() {
            // Arrange
            testMfnRate.setMfnadValoremRate(null);
            when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
            when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
            when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());

            // Act
            Map<String, Object> result = tariffCalculationService.calculateTariff(
                "12345678", "CA", "US", 1000.0, 10);

            // Assert
            assertNotNull(result);
            assertFalse(result.containsKey("error"));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> mfnInfo = (Map<String, Object>) result.get("mfnRate");
            assertNull(mfnInfo.get("adValoremRate"));
            assertEquals(new BigDecimal("2.50"), mfnInfo.get("specificRate"));
        }

        @Test
        @DisplayName("Should handle MFN rate with null specific rate")
        void calculateTariff_ShouldHandleMfnRateWithNullSpecificRate() {
            // Arrange
            testMfnRate.setMfnSpecificRate(null);
            when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
            when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
            when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());

            // Act
            Map<String, Object> result = tariffCalculationService.calculateTariff(
                "12345678", "CA", "US", 1000.0, 10);

            // Assert
            assertNotNull(result);
            assertFalse(result.containsKey("error"));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> mfnInfo = (Map<String, Object>) result.get("mfnRate");
            assertEquals(new BigDecimal("0.05"), mfnInfo.get("adValoremRate"));
            assertNull(mfnInfo.get("specificRate"));
        }

        @Test
        @DisplayName("Should handle MFN rate with both rates null")
        void calculateTariff_ShouldHandleMfnRateWithBothRatesNull() {
            // Arrange
            testMfnRate.setMfnadValoremRate(null);
            testMfnRate.setMfnSpecificRate(null);
            when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
            when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
            when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());

            // Act
            Map<String, Object> result = tariffCalculationService.calculateTariff(
                "12345678", "CA", "US", 1000.0, 10);

            // Assert
            assertNotNull(result);
            assertFalse(result.containsKey("error"));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> mfnInfo = (Map<String, Object>) result.get("mfnRate");
            assertNull(mfnInfo.get("adValoremRate"));
            assertNull(mfnInfo.get("specificRate"));
            assertEquals(BigDecimal.ZERO, result.get("mfnTariffAmount"));
        }

        @Test
        @DisplayName("Should handle preferential rate better than MFN")
        void calculateTariff_ShouldHandlePreferentialRateBetterThanMfn() {
            // Arrange
            when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
            when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
            when(productService.getAgreementRates("12345678", "US")).thenReturn(List.of(testAgreementRate));

            // Act
            Map<String, Object> result = tariffCalculationService.calculateTariff(
                "12345678", "CA", "US", 1000.0, 10);

            // Assert
            assertNotNull(result);
            assertFalse(result.containsKey("error"));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> recommended = (Map<String, Object>) result.get("recommendedRate");
            assertEquals("Preferential", recommended.get("rateType"));
            assertEquals("US-Mexico-Canada Agreement", recommended.get("programName"));
            
            BigDecimal mfnDuty = (BigDecimal) result.get("mfnTariffAmount");
            BigDecimal bestDuty = (BigDecimal) recommended.get("calculatedDuty");
            assertTrue(bestDuty.compareTo(mfnDuty) < 0);
        }

        @Test
        @DisplayName("Should handle multiple preferential rates and select best")
        void calculateTariff_ShouldHandleMultiplePreferentialRatesAndSelectBest() {
            // Arrange
            AgreementRate betterRate = new AgreementRate();
            setAgreementRateId(betterRate, 2L);
            betterRate.setProduct(testProduct);
            betterRate.setAgreement(testAgreement);
            betterRate.setCountry(testCountry);
            betterRate.setadValoremRate(new BigDecimal("0.01"));
            betterRate.setSpecificRate(new BigDecimal("0.50"));
            betterRate.setTextRate("1% or $0.50 per unit");
            betterRate.setRateTypeCode("ADV");

            when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
            when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
            when(productService.getAgreementRates("12345678", "US")).thenReturn(List.of(testAgreementRate, betterRate));

            // Act
            Map<String, Object> result = tariffCalculationService.calculateTariff(
                "12345678", "CA", "US", 1000.0, 10);

            // Assert
            assertNotNull(result);
            assertFalse(result.containsKey("error"));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> recommended = (Map<String, Object>) result.get("recommendedRate");
            assertEquals("Preferential", recommended.get("rateType"));
            assertEquals("US-Mexico-Canada Agreement", recommended.get("programName"));
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> preferentialRates = (List<Map<String, Object>>) result.get("preferentialRates");
            assertEquals(2, preferentialRates.size());
        }

        @Test
        @DisplayName("Should handle null origin country")
        void calculateTariff_ShouldHandleNullOriginCountry() {
            // Arrange
            when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
            when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
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
        @DisplayName("Should handle null destination country")
        void calculateTariff_ShouldHandleNullDestinationCountry() {
            // Arrange
            when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
            when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
            when(productService.getAgreementRates("12345678", null)).thenReturn(Collections.emptyList());

            // Act
            Map<String, Object> result = tariffCalculationService.calculateTariff(
                "12345678", "CA", null, 1000.0, 10);

            // Assert
            assertNotNull(result);
            assertFalse(result.containsKey("error"));
            assertNull(result.get("destinationCountry"));
        }
    }

    @Nested
    @DisplayName("calculateTariffWithDateRange Branch Coverage")
    class CalculateTariffWithDateRangeBranchCoverage {

        @Test
        @DisplayName("Should handle tariff effective date before MFN rate effective date")
        void calculateTariffWithDateRange_ShouldHandleTariffEffectiveDateBeforeMfnEffectiveDate() {
            // Arrange
            LocalDate tariffEffectiveDate = LocalDate.of(2023, 12, 31);
            LocalDate tariffExpirationDate = LocalDate.of(2025, 12, 31);
            
            when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
            when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));

            // Act
            Map<String, Object> result = tariffCalculationService.calculateTariffWithDateRange(
                "12345678", "CA", "US", 1000.0, 10, tariffEffectiveDate, tariffExpirationDate);

            // Assert
            assertNotNull(result);
            assertTrue(result.containsKey("error"));
            assertTrue(result.get("error").toString().contains("Tariff effective date"));
            assertTrue(result.get("error").toString().contains("is before MFN rate effective date"));
        }

        @Test
        @DisplayName("Should handle tariff expiration date after MFN rate expiration date")
        void calculateTariffWithDateRange_ShouldHandleTariffExpirationDateAfterMfnExpirationDate() {
            // Arrange
            LocalDate tariffEffectiveDate = LocalDate.of(2024, 1, 1);
            LocalDate tariffExpirationDate = LocalDate.of(2026, 1, 1);
            
            when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
            when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));

            // Act
            Map<String, Object> result = tariffCalculationService.calculateTariffWithDateRange(
                "12345678", "CA", "US", 1000.0, 10, tariffEffectiveDate, tariffExpirationDate);

            // Assert
            assertNotNull(result);
            assertTrue(result.containsKey("error"));
            assertTrue(result.get("error").toString().contains("Tariff expiration date"));
            assertTrue(result.get("error").toString().contains("is after MFN rate expiration date"));
        }

        @Test
        @DisplayName("Should handle tariff effective date after MFN rate expiration date")
        void calculateTariffWithDateRange_ShouldHandleTariffEffectiveDateAfterMfnExpirationDate() {
            // Arrange
            LocalDate tariffEffectiveDate = LocalDate.of(2026, 1, 1);
            LocalDate tariffExpirationDate = LocalDate.of(2026, 12, 31);
            
            when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
            when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));

            // Act
            Map<String, Object> result = tariffCalculationService.calculateTariffWithDateRange(
                "12345678", "CA", "US", 1000.0, 10, tariffEffectiveDate, tariffExpirationDate);

            // Assert
            assertNotNull(result);
            assertTrue(result.containsKey("error"));
            assertTrue(result.get("error").toString().contains("Tariff effective date"));
            assertTrue(result.get("error").toString().contains("is after MFN rate expiration date"));
        }

        @Test
        @DisplayName("Should handle null tariff effective date")
        void calculateTariffWithDateRange_ShouldHandleNullTariffEffectiveDate() {
            // Arrange
            LocalDate tariffExpirationDate = LocalDate.of(2025, 12, 31);
            
            when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
            when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
            when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());

            // Act
            Map<String, Object> result = tariffCalculationService.calculateTariffWithDateRange(
                "12345678", "CA", "US", 1000.0, 10, null, tariffExpirationDate);

            // Assert
            assertNotNull(result);
            assertFalse(result.containsKey("error"));
        }

        @Test
        @DisplayName("Should handle null tariff expiration date")
        void calculateTariffWithDateRange_ShouldHandleNullTariffExpirationDate() {
            // Arrange
            LocalDate tariffEffectiveDate = LocalDate.of(2024, 1, 1);
            
            when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
            when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
            when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());

            // Act
            Map<String, Object> result = tariffCalculationService.calculateTariffWithDateRange(
                "12345678", "CA", "US", 1000.0, 10, tariffEffectiveDate, null);

            // Assert
            assertNotNull(result);
            assertFalse(result.containsKey("error"));
        }

        @Test
        @DisplayName("Should handle MFN rate with null effective dates")
        void calculateTariffWithDateRange_ShouldHandleMfnRateWithNullEffectiveDates() {
            // Arrange
            testMfnRate.setBeginEffectDate(null);
            testMfnRate.setEndEffectiveDate(null);
            
            LocalDate tariffEffectiveDate = LocalDate.of(2024, 1, 1);
            LocalDate tariffExpirationDate = LocalDate.of(2025, 12, 31);
            
            when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
            when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
            when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());

            // Act
            Map<String, Object> result = tariffCalculationService.calculateTariffWithDateRange(
                "12345678", "CA", "US", 1000.0, 10, tariffEffectiveDate, tariffExpirationDate);

            // Assert
            assertNotNull(result);
            assertFalse(result.containsKey("error"));
        }
    }

    @Nested
    @DisplayName("createTariffCalculation Branch Coverage")
    class CreateTariffCalculationBranchCoverage {

        @Test
        @DisplayName("Should use date range calculation when effective date provided")
        void createTariffCalculation_ShouldUseDateRangeCalculationWhenEffectiveDateProvided() {
            // Arrange
            TariffCalculationRequest request = new TariffCalculationRequest();
            request.setHtsCode("12345678");
            request.setOriginCountry("CA");
            request.setDestinationCountry("US");
            request.setProductValue(new BigDecimal("1000.00"));
            request.setQuantity(10);
            request.setTariffEffectiveDate(LocalDate.of(2024, 1, 1));
            request.setTariffExpirationDate(null);

            when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
            when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
            when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());
            when(tariffCalculationRepository.save(any(TariffCalculation.class))).thenAnswer(invocation -> {
                TariffCalculation calc = invocation.getArgument(0);
                setTariffCalculationId(calc, 1L);
                return calc;
            });

            // Act
            TariffCalculationResponse response = tariffCalculationService.createTariffCalculation(request);

            // Assert
            assertNotNull(response);
            assertEquals("12345678", response.getHtsCode());
            assertEquals("CA", response.getOriginCountry());
            assertEquals("US", response.getDestinationCountry());
        }

        @Test
        @DisplayName("Should use date range calculation when expiration date provided")
        void createTariffCalculation_ShouldUseDateRangeCalculationWhenExpirationDateProvided() {
            // Arrange
            TariffCalculationRequest request = new TariffCalculationRequest();
            request.setHtsCode("12345678");
            request.setOriginCountry("CA");
            request.setDestinationCountry("US");
            request.setProductValue(new BigDecimal("1000.00"));
            request.setQuantity(10);
            request.setTariffEffectiveDate(null);
            request.setTariffExpirationDate(LocalDate.of(2025, 12, 31));

            when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
            when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
            when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());
            when(tariffCalculationRepository.save(any(TariffCalculation.class))).thenAnswer(invocation -> {
                TariffCalculation calc = invocation.getArgument(0);
                setTariffCalculationId(calc, 1L);
                return calc;
            });

            // Act
            TariffCalculationResponse response = tariffCalculationService.createTariffCalculation(request);

            // Assert
            assertNotNull(response);
            assertEquals("12345678", response.getHtsCode());
        }

        @Test
        @DisplayName("Should use regular calculation when no dates provided")
        void createTariffCalculation_ShouldUseRegularCalculationWhenNoDatesProvided() {
            // Arrange
            TariffCalculationRequest request = new TariffCalculationRequest();
            request.setHtsCode("12345678");
            request.setOriginCountry("CA");
            request.setDestinationCountry("US");
            request.setProductValue(new BigDecimal("1000.00"));
            request.setQuantity(10);
            request.setTariffEffectiveDate(null);
            request.setTariffExpirationDate(null);

            when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
            when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
            when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());
            when(tariffCalculationRepository.save(any(TariffCalculation.class))).thenAnswer(invocation -> {
                TariffCalculation calc = invocation.getArgument(0);
                setTariffCalculationId(calc, 1L);
                return calc;
            });

            // Act
            TariffCalculationResponse response = tariffCalculationService.createTariffCalculation(request);

            // Assert
            assertNotNull(response);
            assertEquals("12345678", response.getHtsCode());
        }
    }

    @Nested
    @DisplayName("deleteTariffCalculation Branch Coverage")
    class DeleteTariffCalculationBranchCoverage {

        @Test
        @DisplayName("Should return true when calculation exists and is deleted")
        void deleteTariffCalculation_ShouldReturnTrueWhenCalculationExists() {
            // Arrange
            Long calculationId = 1L;
            when(tariffCalculationRepository.existsById(calculationId)).thenReturn(true);

            // Act
            boolean result = tariffCalculationService.deleteTariffCalculation(calculationId);

            // Assert
            assertTrue(result);
            verify(tariffCalculationRepository).deleteById(calculationId);
        }

        @Test
        @DisplayName("Should return false when calculation does not exist")
        void deleteTariffCalculation_ShouldReturnFalseWhenCalculationDoesNotExist() {
            // Arrange
            Long calculationId = 999L;
            when(tariffCalculationRepository.existsById(calculationId)).thenReturn(false);

            // Act
            boolean result = tariffCalculationService.deleteTariffCalculation(calculationId);

            // Assert
            assertFalse(result);
            verify(tariffCalculationRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("getApplicableTradePrograms Branch Coverage")
    class GetApplicableTradeProgramsBranchCoverage {

        @Test
        @DisplayName("Should return GSP programs for eligible countries")
        void getApplicableTradePrograms_ShouldReturnGspProgramsForEligibleCountries() {
            // Act
            List<String> programs = tariffCalculationService.getApplicableTradePrograms("BD", "US");

            // Assert
            assertNotNull(programs);
            assertTrue(programs.contains("GSP"));
        }

        @Test
        @DisplayName("Should return USMCA programs for North American countries")
        void getApplicableTradePrograms_ShouldReturnUsmcaProgramsForNorthAmericanCountries() {
            // Act
            List<String> programs = tariffCalculationService.getApplicableTradePrograms("CA", "US");

            // Assert
            assertNotNull(programs);
            assertTrue(programs.contains("USMCA"));
        }

        @Test
        @DisplayName("Should return empty list for non-eligible countries")
        void getApplicableTradePrograms_ShouldReturnEmptyListForNonEligibleCountries() {
            // Act
            List<String> programs = tariffCalculationService.getApplicableTradePrograms("XX", "YY");

            // Assert
            assertNotNull(programs);
            assertTrue(programs.isEmpty());
        }
    }
}
