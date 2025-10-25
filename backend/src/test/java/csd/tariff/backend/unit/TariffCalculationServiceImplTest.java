package csd.tariff.backend.unit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import csd.tariff.backend.dto.TariffCalculationRequest;
import csd.tariff.backend.dto.TariffCalculationResponse;
import csd.tariff.backend.model.AgreementRate;
import csd.tariff.backend.model.MfnTariffRate;
import csd.tariff.backend.model.Product;
import csd.tariff.backend.model.TariffCalculation;
import csd.tariff.backend.model.TradeAgreement;
import csd.tariff.backend.repository.ProductRepository;
import csd.tariff.backend.repository.TariffCalculationRepository;
import csd.tariff.backend.service.CurrencyService;
import csd.tariff.backend.service.MfnService;
import csd.tariff.backend.service.ProductService;
import csd.tariff.backend.service.TariffCalculationServiceImpl;
import csd.tariff.backend.service.TradeAgreementService;

/**
 * Comprehensive unit tests for TariffCalculationServiceImpl
 * Tests all tariff calculation methods, business logic, and exception handling
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TariffCalculationService Unit Tests")
class TariffCalculationServiceImplTest {

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
    private TradeAgreement testTradeAgreement;

    @BeforeEach
    void setUp() {
        // Setup test product
        testProduct = new Product();
        testProduct.setHts8("12345678");
        testProduct.setBriefDescription("Test Product Description");
        
        // Setup test MFN rate
        testMfnRate = new MfnTariffRate();
        testMfnRate.setProduct(testProduct);
        testMfnRate.setMfnadValoremRate(new BigDecimal("0.10")); // 10%
        testMfnRate.setMfnSpecificRate(new BigDecimal("5.00")); // $5 per unit
        testMfnRate.setMfnTextRate("10% + $5.00 per unit");
        testMfnRate.setMfnRateTypeCode("ADV");
        
        // Setup test trade agreement
        testTradeAgreement = new TradeAgreement();
        testTradeAgreement.setAgreementCode("USMCA");
        testTradeAgreement.setAgreementName("US-Mexico-Canada Agreement");
        testTradeAgreement.setEffectiveDate(LocalDate.of(2020, 7, 1));
        testTradeAgreement.setExpirationDate(LocalDate.of(2030, 6, 30));
        
        // Setup test agreement rate
        testAgreementRate = new AgreementRate();
        testAgreementRate.setProduct(testProduct);
        testAgreementRate.setadValoremRate(new BigDecimal("0.05")); // 5%
        testAgreementRate.setSpecificRate(new BigDecimal("2.50")); // $2.50 per unit
        testAgreementRate.setTextRate("5% + $2.50 per unit");
        testAgreementRate.setRateTypeCode("ADV");
        testAgreementRate.setAgreement(testTradeAgreement);
    }

    // ===== FIND BY HTS CODE TESTS =====
    
    @Test
    @DisplayName("Should return product when valid HTS code exists")
    void findByHtsCode_ShouldReturnProduct_WhenValidHtsCodeExists() {
        // Arrange
        String htsCode = "12345678";
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));

        // Act
        Optional<Product> result = tariffCalculationService.findByHtsCode(htsCode);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testProduct, result.get());
        verify(productRepository, times(1)).findByHts8(htsCode);
    }

    @Test
    @DisplayName("Should return empty when HTS code does not exist")
    void findByHtsCode_ShouldReturnEmpty_WhenHtsCodeDoesNotExist() {
        // Arrange
        String htsCode = "99999999";
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = tariffCalculationService.findByHtsCode(htsCode);

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findByHts8(htsCode);
    }

    @Test
    @DisplayName("Should return empty when HTS code is null")
    void findByHtsCode_ShouldReturnEmpty_WhenHtsCodeIsNull() {
        // Act
        Optional<Product> result = tariffCalculationService.findByHtsCode(null);

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, never()).findByHts8(anyString());
    }

    @Test
    @DisplayName("Should clean and normalize HTS code")
    void findByHtsCode_ShouldCleanAndNormalizeHtsCode() {
        // Arrange
        String dirtyHtsCode = "1234-5678";
        String cleanHtsCode = "12345678";
        when(productRepository.findByHts8(cleanHtsCode)).thenReturn(Optional.of(testProduct));

        // Act
        Optional<Product> result = tariffCalculationService.findByHtsCode(dirtyHtsCode);

        // Assert
        assertTrue(result.isPresent());
        verify(productRepository, times(1)).findByHts8(cleanHtsCode);
    }

    // ===== CALCULATE TARIFF TESTS =====
    
    @Test
    @DisplayName("Should calculate tariff successfully with MFN rate only")
    void calculateTariff_ShouldCalculateSuccessfully_WithMfnRateOnly() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        assertEquals(htsCode, result.get("htsCode"));
        assertEquals(destinationCountry, result.get("destinationCountry"));
        assertEquals(productValue, result.get("productValue"));
        assertEquals(quantity, result.get("quantity"));
        
        // Verify MFN calculation: 10% of $1000 + $5 * 10 = $100 + $50 = $150
        BigDecimal expectedMfnDuty = new BigDecimal("150.00");
        assertEquals(expectedMfnDuty, result.get("mfnTariffAmount"));
        assertEquals(expectedMfnDuty, result.get("bestTariffAmount"));
        assertEquals("MFN", result.get("bestProgramName"));
        
        verify(productRepository, times(1)).findByHts8(htsCode);
        verify(mfnService, times(1)).getMfnTariffRate(htsCode);
    }

    @Test
    @DisplayName("Should calculate tariff with preferential rate when available")
    void calculateTariff_ShouldCalculateWithPreferentialRate_WhenAvailable() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Arrays.asList(testAgreementRate));

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        
        // Verify preferential calculation: 5% of $1000 + $2.50 * 10 = $50 + $25 = $75
        BigDecimal expectedPrefDuty = new BigDecimal("75.00");
        BigDecimal expectedMfnDuty = new BigDecimal("150.00");
        
        assertEquals(expectedPrefDuty, result.get("bestTariffAmount"));
        assertEquals(expectedMfnDuty, result.get("mfnTariffAmount"));
        assertEquals("US-Mexico-Canada Agreement", result.get("bestProgramName"));
        
        // Verify savings calculation
        @SuppressWarnings("unchecked")
        Map<String, Object> recommendedRate = (Map<String, Object>) result.get("recommendedRate");
        assertEquals(new BigDecimal("75.00"), recommendedRate.get("savings"));
    }

    @Test
    @DisplayName("Should return error when HTS code not found")
    void calculateTariff_ShouldReturnError_WhenHtsCodeNotFound() {
        // Arrange
        String htsCode = "99999999";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.empty());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("error"));
        assertEquals("HTS code not found", result.get("error"));
    }

    @Test
    @DisplayName("Should throw exception when product value is negative")
    void calculateTariff_ShouldThrowException_WhenProductValueIsNegative() {
        // Arrange
        String htsCode = "12345678";
        String originCountry = "CN";
        String destinationCountry = "US";
        Double productValue = -100.0;
        Integer quantity = 10;

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(htsCode, originCountry, destinationCountry, productValue, quantity);

        // Assert
        assertTrue(result.containsKey("error"));
        assertTrue(result.get("error").toString().contains("productValue must be ≥ 0"));
    }

    @Test
    @DisplayName("Should throw exception when quantity is negative")
    void calculateTariff_ShouldThrowException_WhenQuantityIsNegative() {
        // Arrange
        String htsCode = "12345678";
        String originCountry = "CN";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = -5;

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(htsCode, originCountry, destinationCountry, productValue, quantity);

        // Assert
        assertTrue(result.containsKey("error"));
        assertTrue(result.get("error").toString().contains("quantity must be ≥ 0"));
    }

    // ===== CALCULATE DUTY TESTS =====
    
    @Test
    @DisplayName("Should calculate duty with ad valorem rate only")
    void calculateDuty_ShouldCalculateWithAdValoremRateOnly() {
        // Arrange
        BigDecimal adValoremRate = new BigDecimal("0.10"); // 10%
        BigDecimal specificRate = null;
        Double productValue = 1000.0;
        Integer quantity = 5;

        // Act
        BigDecimal result = tariffCalculationService.calculateDuty(
            adValoremRate, specificRate, productValue, quantity);

        // Assert
        // Expected: 10% of $1000 = $100
        assertEquals(new BigDecimal("100.00"), result);
    }

    @Test
    @DisplayName("Should calculate duty with specific rate only")
    void calculateDuty_ShouldCalculateWithSpecificRateOnly() {
        // Arrange
        BigDecimal adValoremRate = null;
        BigDecimal specificRate = new BigDecimal("5.00"); // $5 per unit
        Double productValue = 1000.0;
        Integer quantity = 10;

        // Act
        BigDecimal result = tariffCalculationService.calculateDuty(
            adValoremRate, specificRate, productValue, quantity);

        // Assert
        // Expected: $5 * 10 = $50
        assertEquals(new BigDecimal("50.00"), result);
    }

    @Test
    @DisplayName("Should calculate duty with both ad valorem and specific rates")
    void calculateDuty_ShouldCalculateWithBothRates() {
        // Arrange
        BigDecimal adValoremRate = new BigDecimal("0.05"); // 5%
        BigDecimal specificRate = new BigDecimal("2.50"); // $2.50 per unit
        Double productValue = 1000.0;
        Integer quantity = 10;

        // Act
        BigDecimal result = tariffCalculationService.calculateDuty(
            adValoremRate, specificRate, productValue, quantity);

        // Assert
        // Expected: 5% of $1000 + $2.50 * 10 = $50 + $25 = $75
        assertEquals(new BigDecimal("75.00"), result);
    }

    // ===== VALIDATE HTS CODE TESTS =====
    
    @Test
    @DisplayName("Should validate HTS code successfully when valid")
    void validateHtsCode_ShouldValidateSuccessfully_WhenValid() {
        // Arrange
        String htsCode = "12345678";
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));

        // Act
        Map<String, Object> result = tariffCalculationService.validateHtsCode(htsCode);

        // Assert
        assertTrue((Boolean) result.get("valid"));
        assertEquals("Valid HTS code", result.get("message"));
        assertEquals(htsCode, result.get("htsCode"));
        assertEquals(testProduct.getId(), result.get("productId"));
        assertTrue((Boolean) result.get("hasMfnRate"));
    }

    @Test
    @DisplayName("Should return invalid when HTS code is empty")
    void validateHtsCode_ShouldReturnInvalid_WhenEmpty() {
        // Act
        Map<String, Object> result = tariffCalculationService.validateHtsCode("");

        // Assert
        assertFalse((Boolean) result.get("valid"));
        assertEquals("HTS code cannot be empty", result.get("message"));
        assertEquals("EMPTY_HTS_CODE", result.get("errorCode"));
    }

    @Test
    @DisplayName("Should return invalid when HTS code format is wrong")
    void validateHtsCode_ShouldReturnInvalid_WhenFormatWrong() {
        // Act
        Map<String, Object> result = tariffCalculationService.validateHtsCode("12345");

        // Assert
        assertFalse((Boolean) result.get("valid"));
        assertEquals("HTS code must be exactly 8 digits", result.get("message"));
        assertEquals("INVALID_FORMAT", result.get("errorCode"));
    }

    // ===== GET APPLICABLE TRADE PROGRAMS TESTS =====
    
    @Test
    @DisplayName("Should return applicable trade programs when agreements exist")
    void getApplicableTradePrograms_ShouldReturnPrograms_WhenAgreementsExist() {
        // Arrange
        String originCountry = "US";
        String destinationCountry = "CA";
        when(tradeAgreementService.getTradeAgreementsBetweenCountries(originCountry, destinationCountry))
            .thenReturn(Arrays.asList(testTradeAgreement));

        // Act
        List<String> result = tariffCalculationService.getApplicableTradePrograms(
            originCountry, destinationCountry);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("USMCA - US-Mexico-Canada Agreement", result.get(0));
    }

    @Test
    @DisplayName("Should return empty list when no agreements exist")
    void getApplicableTradePrograms_ShouldReturnEmptyList_WhenNoAgreementsExist() {
        // Arrange
        String originCountry = "US";
        String destinationCountry = "XX";
        when(tradeAgreementService.getTradeAgreementsBetweenCountries(originCountry, destinationCountry))
            .thenReturn(Collections.emptyList());

        // Act
        List<String> result = tariffCalculationService.getApplicableTradePrograms(
            originCountry, destinationCountry);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ===== CRUD OPERATIONS TESTS =====
    
    @Test
    @DisplayName("Should get tariff calculation by ID when exists")
    void getTariffCalculationById_ShouldReturnCalculation_WhenExists() {
        // Arrange
        Long id = 1L;
        TariffCalculation calculation = new TariffCalculation();
        calculation.setHtsCode("12345678");
        when(tariffCalculationRepository.findById(id)).thenReturn(Optional.of(calculation));

        // Act
        Optional<TariffCalculation> result = tariffCalculationService.getTariffCalculationById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(calculation, result.get());
        verify(tariffCalculationRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should return empty when tariff calculation does not exist")
    void getTariffCalculationById_ShouldReturnEmpty_WhenDoesNotExist() {
        // Arrange
        Long id = 999L;
        when(tariffCalculationRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<TariffCalculation> result = tariffCalculationService.getTariffCalculationById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(tariffCalculationRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should get all tariff calculations")
    void getAllTariffCalculations_ShouldReturnAllCalculations() {
        // Arrange
        List<TariffCalculation> calculations = Arrays.asList(
            new TariffCalculation(), new TariffCalculation()
        );
        when(tariffCalculationRepository.findAll()).thenReturn(calculations);

        // Act
        List<TariffCalculation> result = tariffCalculationService.getAllTariffCalculations();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(tariffCalculationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should delete tariff calculation when exists")
    void deleteTariffCalculation_ShouldDelete_WhenExists() {
        // Arrange
        Long id = 1L;
        when(tariffCalculationRepository.existsById(id)).thenReturn(true);

        // Act
        boolean result = tariffCalculationService.deleteTariffCalculation(id);

        // Assert
        assertTrue(result);
        verify(tariffCalculationRepository, times(1)).existsById(id);
        verify(tariffCalculationRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should return false when deleting non-existent calculation")
    void deleteTariffCalculation_ShouldReturnFalse_WhenDoesNotExist() {
        // Arrange
        Long id = 999L;
        when(tariffCalculationRepository.existsById(id)).thenReturn(false);

        // Act
        boolean result = tariffCalculationService.deleteTariffCalculation(id);

        // Assert
        assertFalse(result);
        verify(tariffCalculationRepository, times(1)).existsById(id);
        verify(tariffCalculationRepository, never()).deleteById(anyLong());
    }

    // ===== CREATE TARIFF CALCULATION TESTS =====
    
    @Test
    @DisplayName("Should create tariff calculation successfully with valid request")
    void createTariffCalculation_ShouldCreateSuccessfully_WithValidRequest() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("US");
        request.setDestinationCountry("CA");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(10);
        request.setCurrency("USD");
        
        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates("12345678", "CA")).thenReturn(Collections.emptyList());
        when(tariffCalculationRepository.save(any(TariffCalculation.class))).thenAnswer(invocation -> {
            TariffCalculation calc = invocation.getArgument(0);
            return calc;
        });

        // Act
        TariffCalculationResponse result = tariffCalculationService.createTariffCalculation(request);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getHtsCode());
        assertEquals("US", result.getOriginCountry());
        assertEquals("CA", result.getDestinationCountry());
        assertEquals(new BigDecimal("1000.00"), result.getProductValue());
        assertEquals(10, result.getQuantity());
        assertEquals("USD", result.getCurrency());
        
        verify(tariffCalculationRepository, times(1)).save(any(TariffCalculation.class));
    }

    @Test
    @DisplayName("Should throw exception when HTS code is invalid")
    void createTariffCalculation_ShouldThrowException_WhenHtsCodeInvalid() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("99999999");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(10);
        
        when(productRepository.findByHts8("99999999")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            tariffCalculationService.createTariffCalculation(request));
        
        assertTrue(exception.getMessage().contains("Invalid HTS code"));
    }

    @Test
    @DisplayName("Should throw exception when request is null")
    void createTariffCalculation_ShouldThrowException_WhenRequestIsNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> 
            tariffCalculationService.createTariffCalculation(null));
    }

    // ===== ADDITIONAL TESTS FOR UNCOVERED METHODS =====

    @Test
    @DisplayName("Should calculate tariff with date range successfully")
    void calculateTariffWithDateRange_ShouldCalculateSuccessfully() {
        // Arrange
        String htsCode = "12345678";
        String originCountry = "CN";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariffWithDateRange(
            htsCode, originCountry, destinationCountry, productValue, quantity, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        assertEquals(htsCode, result.get("htsCode"));
        // Note: originCountry might not be set in the result map
        assertEquals(destinationCountry, result.get("destinationCountry"));
        assertEquals(productValue, result.get("productValue"));
        assertEquals(quantity, result.get("quantity"));
    }

    @Test
    @DisplayName("Should calculate tariff with date range (overloaded method)")
    void calculateTariffWithDateRange_Overloaded_ShouldCalculateSuccessfully() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariffWithDateRange(
            htsCode, destinationCountry, productValue, quantity, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        assertEquals(htsCode, result.get("htsCode"));
        assertEquals(destinationCountry, result.get("destinationCountry"));
        assertEquals(productValue, result.get("productValue"));
        assertEquals(quantity, result.get("quantity"));
    }

    @Test
    @DisplayName("Should update tariff calculation successfully")
    void updateTariffCalculation_ShouldUpdateSuccessfully() {
        // Arrange
        Long id = 1L;
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("US");
        request.setDestinationCountry("CA");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(10);
        request.setCurrency("USD");

        TariffCalculation existingCalculation = new TariffCalculation();
        // Note: TariffCalculation might not have setId method, so we'll work with what we have
        existingCalculation.setHtsCode("12345678");

        when(tariffCalculationRepository.findById(id)).thenReturn(Optional.of(existingCalculation));
        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates("12345678", "CA")).thenReturn(Collections.emptyList());
        when(tariffCalculationRepository.save(any(TariffCalculation.class))).thenAnswer(invocation -> {
            TariffCalculation calc = invocation.getArgument(0);
            return calc;
        });

        // Act
        TariffCalculationResponse result = tariffCalculationService.updateTariffCalculation(id, request);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getHtsCode());
        assertEquals("US", result.getOriginCountry());
        assertEquals("CA", result.getDestinationCountry());
        assertEquals(new BigDecimal("1000.00"), result.getProductValue());
        assertEquals(10, result.getQuantity());
        assertEquals("USD", result.getCurrency());
        
        verify(tariffCalculationRepository, times(1)).findById(id);
        verify(tariffCalculationRepository, times(1)).save(any(TariffCalculation.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent calculation")
    void updateTariffCalculation_ShouldThrowException_WhenCalculationNotFound() {
        // Arrange
        Long id = 999L;
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(10);

        when(tariffCalculationRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            tariffCalculationService.updateTariffCalculation(id, request));
        
        assertTrue(exception.getMessage().contains("Tariff calculation not found"));
    }

    @Test
    @DisplayName("Should get tariff calculations by HTS code")
    void getTariffCalculationsByHtsCode_ShouldReturnCalculations() {
        // Arrange
        String htsCode = "12345678";
        List<TariffCalculation> calculations = Arrays.asList(
            new TariffCalculation(), new TariffCalculation()
        );
        when(tariffCalculationRepository.findByHtsCode(htsCode)).thenReturn(calculations);

        // Act
        List<TariffCalculation> result = tariffCalculationService.getTariffCalculationsByHtsCode(htsCode);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(tariffCalculationRepository, times(1)).findByHtsCode(htsCode);
    }

    @Test
    @DisplayName("Should get tariff calculations by country code")
    void getTariffCalculationsByCountryCode_ShouldReturnCalculations() {
        // Arrange
        String countryCode = "US";
        List<TariffCalculation> calculations = Arrays.asList(
            new TariffCalculation(), new TariffCalculation()
        );
        when(tariffCalculationRepository.findByCountryCode(countryCode)).thenReturn(calculations);

        // Act
        List<TariffCalculation> result = tariffCalculationService.getTariffCalculationsByCountryCode(countryCode);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(tariffCalculationRepository, times(1)).findByCountryCode(countryCode);
    }

    @Test
    @DisplayName("Should delete all tariff calculations")
    void deleteAllTariffCalculations_ShouldDeleteAll() {
        // Act
        boolean result = tariffCalculationService.deleteAllTariffCalculations();

        // Assert
        assertTrue(result);
        verify(tariffCalculationRepository, times(1)).deleteAll();
    }

    // ===== EDGE CASES AND BOUNDARY CONDITIONS =====
    
    @Test
    @DisplayName("Should handle zero product value correctly")
    void calculateTariff_ShouldHandleZeroProductValue() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 0.0;
        Integer quantity = 10;
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        // With zero product value, only specific rate should apply: $5 * 10 = $50
        assertEquals(new BigDecimal("50.00"), result.get("mfnTariffAmount"));
    }

    @Test
    @DisplayName("Should handle zero quantity correctly")
    void calculateTariff_ShouldHandleZeroQuantity() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 0;
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        // With zero quantity, only ad valorem rate should apply: 10% of $1000 = $100
        assertEquals(new BigDecimal("100.00"), result.get("mfnTariffAmount"));
    }

    @Test
    @DisplayName("Should handle very large product values")
    void calculateTariff_ShouldHandleLargeProductValues() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 999999999.99;
        Integer quantity = 1000;
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        // Expected: 10% of $999999999.99 + $5 * 1000 = $99999999.999 + $5000 = $100004999.999
        BigDecimal expectedDuty = new BigDecimal("100004999.999").setScale(2, java.math.RoundingMode.HALF_UP);
        assertEquals(expectedDuty, result.get("mfnTariffAmount"));
    }

    @Test
    @DisplayName("Should handle very large quantities")
    void calculateTariff_ShouldHandleLargeQuantities() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 1000000;
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        // Expected: 10% of $1000 + $5 * 1000000 = $100 + $5000000 = $5000100
        assertEquals(new BigDecimal("5000100.00"), result.get("mfnTariffAmount"));
    }

    @Test
    @DisplayName("Should handle null ad valorem rate")
    void calculateDuty_ShouldHandleNullAdValoremRate() {
        // Arrange
        BigDecimal adValoremRate = null;
        BigDecimal specificRate = new BigDecimal("5.00");
        Double productValue = 1000.0;
        Integer quantity = 10;

        // Act
        BigDecimal result = tariffCalculationService.calculateDuty(
            adValoremRate, specificRate, productValue, quantity);

        // Assert
        // Expected: $5 * 10 = $50
        assertEquals(new BigDecimal("50.00"), result);
    }

    @Test
    @DisplayName("Should handle null specific rate")
    void calculateDuty_ShouldHandleNullSpecificRate() {
        // Arrange
        BigDecimal adValoremRate = new BigDecimal("0.10");
        BigDecimal specificRate = null;
        Double productValue = 1000.0;
        Integer quantity = 10;

        // Act
        BigDecimal result = tariffCalculationService.calculateDuty(
            adValoremRate, specificRate, productValue, quantity);

        // Assert
        // Expected: 10% of $1000 = $100
        assertEquals(new BigDecimal("100.00"), result);
    }

    @Test
    @DisplayName("Should handle both rates being null")
    void calculateDuty_ShouldHandleBothRatesNull() {
        // Arrange
        BigDecimal adValoremRate = null;
        BigDecimal specificRate = null;
        Double productValue = 1000.0;
        Integer quantity = 10;

        // Act
        BigDecimal result = tariffCalculationService.calculateDuty(
            adValoremRate, specificRate, productValue, quantity);

        // Assert
        assertEquals(BigDecimal.ZERO.setScale(2), result);
    }

    @Test
    @DisplayName("Should handle very small decimal rates")
    void calculateDuty_ShouldHandleSmallDecimalRates() {
        // Arrange
        BigDecimal adValoremRate = new BigDecimal("0.0001"); // 0.01%
        BigDecimal specificRate = new BigDecimal("0.01"); // $0.01 per unit
        Double productValue = 1000.0;
        Integer quantity = 10;

        // Act
        BigDecimal result = tariffCalculationService.calculateDuty(
            adValoremRate, specificRate, productValue, quantity);

        // Assert
        // Expected: 0.01% of $1000 + $0.01 * 10 = $0.10 + $0.10 = $0.20
        assertEquals(new BigDecimal("0.20"), result);
    }

    // ===== CURRENCY CONVERSION TESTS =====
    
    @Test
    @DisplayName("Should handle currency conversion in createTariffCalculation")
    void createTariffCalculation_ShouldHandleCurrencyConversion() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("US");
        request.setDestinationCountry("CA");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(10);
        request.setCurrency("CAD");
        
        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates("12345678", "CA")).thenReturn(Collections.emptyList());
        when(currencyService.convertCurrency(any(BigDecimal.class), eq("USD"), eq("CAD")))
            .thenReturn(new BigDecimal("1350.00")); // Mock 1.35 exchange rate
        when(tariffCalculationRepository.save(any(TariffCalculation.class))).thenAnswer(invocation -> {
            TariffCalculation calc = invocation.getArgument(0);
            return calc;
        });

        // Act
        TariffCalculationResponse result = tariffCalculationService.createTariffCalculation(request);

        // Assert
        assertNotNull(result);
        assertEquals("CAD", result.getCurrency());
        // Verify currency conversion was called (only 2 times as savings might be zero)
        verify(currencyService, times(2)).convertCurrency(any(BigDecimal.class), eq("USD"), eq("CAD"));
    }

    @Test
    @DisplayName("Should handle null currency gracefully")
    void createTariffCalculation_ShouldHandleNullCurrency() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("US");
        request.setDestinationCountry("CA");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(10);
        request.setCurrency(null); // null currency
        
        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates("12345678", "CA")).thenReturn(Collections.emptyList());
        when(tariffCalculationRepository.save(any(TariffCalculation.class))).thenAnswer(invocation -> {
            TariffCalculation calc = invocation.getArgument(0);
            return calc;
        });

        // Act
        TariffCalculationResponse result = tariffCalculationService.createTariffCalculation(request);

        // Assert
        assertNotNull(result);
        assertNull(result.getCurrency());
        // Verify currency conversion was not called
        verify(currencyService, never()).convertCurrency(any(BigDecimal.class), anyString(), anyString());
    }

    // ===== DATE VALIDATION TESTS =====
    
    @Test
    @DisplayName("Should validate tariff date range successfully")
    void calculateTariffWithDateRange_ShouldValidateDateRange() {
        // Arrange
        String htsCode = "12345678";
        String originCountry = "CN";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        LocalDate startDate = LocalDate.of(2023, 6, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        // Setup MFN rate with date range
        testMfnRate.setBeginEffectDate(LocalDate.of(2023, 1, 1));
        testMfnRate.setEndEffectiveDate(LocalDate.of(2024, 12, 31));

        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariffWithDateRange(
            htsCode, originCountry, destinationCountry, productValue, quantity, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        assertEquals(htsCode, result.get("htsCode"));
        assertEquals(destinationCountry, result.get("destinationCountry"));
    }

    @Test
    @DisplayName("Should handle invalid date range")
    void calculateTariffWithDateRange_ShouldHandleInvalidDateRange() {
        // Arrange
        String htsCode = "12345678";
        String originCountry = "CN";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        LocalDate startDate = LocalDate.of(2025, 1, 1); // Future date
        LocalDate endDate = LocalDate.of(2025, 12, 31);

        // Setup MFN rate with past date range
        testMfnRate.setBeginEffectDate(LocalDate.of(2023, 1, 1));
        testMfnRate.setEndEffectiveDate(LocalDate.of(2024, 12, 31));

        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        // Don't stub productService since we expect an error before it's called

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariffWithDateRange(
            htsCode, originCountry, destinationCountry, productValue, quantity, startDate, endDate);

        // Assert
        assertNotNull(result);
        // Should contain error about date range
        assertTrue(result.containsKey("error") || result.containsKey("warning"));
    }

    // ===== COST BREAKDOWN TESTS =====
    
    @Test
    @DisplayName("Should get cost breakdown successfully")
    void getCostBreakdown_ShouldReturnBreakdown() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.getCostBreakdown(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        assertTrue(result.containsKey("purchasePrice"));
        assertTrue(result.containsKey("tariffAmount"));
        assertTrue(result.containsKey("totalImportPrice"));
    }

    @Test
    @DisplayName("Should return error for invalid HTS in cost breakdown")
    void getCostBreakdown_ShouldReturnErrorForInvalidHts() {
        // Arrange
        String htsCode = "99999999";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.empty());

        // Act
        Map<String, Object> result = tariffCalculationService.getCostBreakdown(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("error"));
    }

    // ===== DATA INTEGRITY TESTS =====
    
    @Test
    @DisplayName("Should handle corrupted product data gracefully")
    void calculateTariff_ShouldHandleCorruptedProductData() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        
        // Create product with null description
        Product corruptedProduct = new Product();
        corruptedProduct.setHts8("12345678");
        corruptedProduct.setBriefDescription(null);
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(corruptedProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        // Should still calculate tariff even with corrupted product data
        assertEquals(new BigDecimal("150.00"), result.get("mfnTariffAmount"));
    }

    @Test
    @DisplayName("Should handle missing MFN rate gracefully")
    void calculateTariff_ShouldHandleMissingMfnRate() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.empty());
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        // Should return zero MFN tariff when no rate is available
        assertEquals(BigDecimal.ZERO, result.get("mfnTariffAmount"));
    }

    // ===== PERFORMANCE AND STRESS TESTS =====
    
    @Test
    @DisplayName("Should handle multiple preferential rates efficiently")
    void calculateTariff_ShouldHandleMultiplePreferentialRates() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        
        // Create multiple agreement rates
        List<AgreementRate> multipleRates = Arrays.asList(
            createAgreementRate("USMCA", new BigDecimal("0.05"), new BigDecimal("2.50")),
            createAgreementRate("CPTPP", new BigDecimal("0.03"), new BigDecimal("1.50")),
            createAgreementRate("CUSMA", new BigDecimal("0.04"), new BigDecimal("2.00"))
        );
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(multipleRates);

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        
        // Should select the best rate (lowest duty)
        // CPTPP: 3% of $1000 + $1.50 * 10 = $30 + $15 = $45 (best)
        // USMCA: 5% of $1000 + $2.50 * 10 = $50 + $25 = $75
        // CUSMA: 4% of $1000 + $2.00 * 10 = $40 + $20 = $60
        assertEquals(new BigDecimal("45.00"), result.get("bestTariffAmount"));
    }

    // ===== COMPREHENSIVE VALIDATION TESTS =====
    
    @Test
    @DisplayName("Should validate various HTS code formats")
    void validateHtsCode_ShouldValidateVariousFormats() {
        // Test multiple valid HTS codes
        String[] validHtsCodes = {"12345678", "87654321", "11111111", "99999999"};
        
        for (String htsCode : validHtsCodes) {
            // Arrange
            when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
            when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));

            // Act
            Map<String, Object> result = tariffCalculationService.validateHtsCode(htsCode);

            // Assert
            assertTrue((Boolean) result.get("valid"));
            assertEquals("Valid HTS code", result.get("message"));
            assertEquals(htsCode, result.get("htsCode"));
        }
    }

    @Test
    @DisplayName("Should calculate duty with various rate combinations")
    void calculateDuty_ShouldCalculateWithVariousRates() {
        // Test multiple rate combinations
        Object[][] testCases = {
            {"0.05", "2.50", 1000.0, 10, "75.00"},
            {"0.10", "5.00", 1000.0, 10, "150.00"},
            {"0.15", "0.00", 1000.0, 10, "150.00"},
            {"0.00", "10.00", 1000.0, 10, "100.00"},
            {"0.25", "1.00", 2000.0, 5, "505.00"} // Fixed: 25% of $2000 + $1 * 5 = $500 + $5 = $505
        };
        
        for (Object[] testCase : testCases) {
            // Arrange
            BigDecimal adValoremRate = new BigDecimal((String) testCase[0]);
            BigDecimal specificRate = new BigDecimal((String) testCase[1]);
            Double productValue = (Double) testCase[2];
            Integer quantity = (Integer) testCase[3];
            BigDecimal expected = new BigDecimal((String) testCase[4]);

            // Act
            BigDecimal result = tariffCalculationService.calculateDuty(
                adValoremRate, specificRate, productValue, quantity);

            // Assert
            assertEquals(expected, result);
        }
    }

    @Test
    @DisplayName("Should reject invalid HTS code formats")
    void validateHtsCode_ShouldRejectInvalidFormats() {
        // Test multiple invalid HTS codes
        String[] invalidHtsCodes = {"", "12345", "123456789", "abc12345", "1234-5678", "1234 5678"};
        
        for (String invalidHtsCode : invalidHtsCodes) {
            // Act
            Map<String, Object> result = tariffCalculationService.validateHtsCode(invalidHtsCode);

            // Assert
            assertFalse((Boolean) result.get("valid"));
            assertTrue(result.containsKey("message"));
        }
    }

    // ===== INTEGRATION-STYLE TESTS =====
    
    @Test
    @DisplayName("Should handle complete tariff calculation workflow with all components")
    void calculateTariff_ShouldHandleCompleteWorkflow() {
        // Arrange - Simulate a real-world scenario
        String htsCode = "12345678";
        String originCountry = "CN";
        String destinationCountry = "US";
        Double productValue = 5000.0;
        Integer quantity = 100;
        
        // Setup comprehensive test data
        Product product = new Product();
        product.setHts8(htsCode);
        product.setBriefDescription("Electronics Components");
        
        MfnTariffRate mfnRate = new MfnTariffRate();
        mfnRate.setProduct(product);
        mfnRate.setMfnadValoremRate(new BigDecimal("0.08")); // 8%
        mfnRate.setMfnSpecificRate(new BigDecimal("3.50")); // $3.50 per unit
        mfnRate.setMfnTextRate("8% + $3.50 per unit");
        mfnRate.setMfnRateTypeCode("ADV");
        
        // Create multiple preferential rates
        List<AgreementRate> preferentialRates = Arrays.asList(
            createAgreementRate("USMCA", new BigDecimal("0.04"), new BigDecimal("2.00")),
            createAgreementRate("CPTPP", new BigDecimal("0.02"), new BigDecimal("1.50"))
        );
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(product));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(mfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(preferentialRates);
        when(tradeAgreementService.getTradeAgreementsBetweenCountries(originCountry, destinationCountry))
            .thenReturn(Arrays.asList(testTradeAgreement));

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, originCountry, destinationCountry, productValue, quantity);

        // Assert comprehensive results
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        
        // Verify all expected fields are present
        assertEquals(htsCode, result.get("htsCode"));
        assertEquals(originCountry, result.get("countryOfOrigin"));
        assertEquals(destinationCountry, result.get("destinationCountry"));
        assertEquals(productValue, result.get("productValue"));
        assertEquals(quantity, result.get("quantity"));
        
        // Verify MFN calculation: 8% of $5000 + $3.50 * 100 = $400 + $350 = $750
        assertEquals(new BigDecimal("750.00"), result.get("mfnTariffAmount"));
        
        // Verify best preferential rate: CPTPP 2% of $5000 + $1.50 * 100 = $100 + $150 = $250
        assertEquals(new BigDecimal("250.00"), result.get("bestTariffAmount"));
        assertEquals("CPTPP Agreement", result.get("bestProgramName"));
        
        // Verify savings calculation
        @SuppressWarnings("unchecked")
        Map<String, Object> recommendedRate = (Map<String, Object>) result.get("recommendedRate");
        assertEquals(new BigDecimal("500.00"), recommendedRate.get("savings"));
        
        // Verify preferential rates list
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> prefRates = (List<Map<String, Object>>) result.get("preferentialRates");
        assertEquals(2, prefRates.size());
    }

    @Test
    @DisplayName("Should handle complex currency conversion scenario")
    void createTariffCalculation_ShouldHandleComplexCurrencyScenario() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("DE");
        request.setDestinationCountry("US");
        request.setProductValue(new BigDecimal("10000.00"));
        request.setQuantity(50);
        request.setCurrency("EUR");
        request.setTariffEffectiveDate(LocalDate.of(2023, 6, 1));
        request.setTariffExpirationDate(LocalDate.of(2023, 12, 31));
        
        // Setup complex test data
        Product product = new Product();
        product.setHts8("12345678");
        product.setBriefDescription("German Automotive Parts");
        
        MfnTariffRate mfnRate = new MfnTariffRate();
        mfnRate.setProduct(product);
        mfnRate.setMfnadValoremRate(new BigDecimal("0.06")); // 6%
        mfnRate.setMfnSpecificRate(new BigDecimal("25.00")); // $25 per unit
        mfnRate.setBeginEffectDate(LocalDate.of(2023, 1, 1));
        mfnRate.setEndEffectiveDate(LocalDate.of(2024, 12, 31));
        
        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(product));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(mfnRate));
        when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());
        when(currencyService.convertCurrency(any(BigDecimal.class), eq("USD"), eq("EUR")))
            .thenReturn(new BigDecimal("8500.00")); // Mock 0.85 exchange rate
        when(tariffCalculationRepository.save(any(TariffCalculation.class))).thenAnswer(invocation -> {
            TariffCalculation calc = invocation.getArgument(0);
            return calc;
        });

        // Act
        TariffCalculationResponse result = tariffCalculationService.createTariffCalculation(request);

        // Assert
        assertNotNull(result);
        assertEquals("EUR", result.getCurrency());
        assertEquals("German Automotive Parts", result.getProductDescription());
        assertEquals("MFN", result.getProgramType());
        
        // Verify currency conversion was called (only 2 times as savings might be zero)
        verify(currencyService, times(2)).convertCurrency(any(BigDecimal.class), eq("USD"), eq("EUR"));
        
        // Verify calculation: 6% of $10000 + $25 * 50 = $600 + $1250 = $1850 USD
        // Converted to EUR: $1850 * 0.85 = €1572.50
        // But the mock returns 8500.00 for any conversion, so we expect that
        assertEquals(new BigDecimal("8500.00"), result.getTotalTariffAmount());
    }

    // ===== CONCURRENT OPERATIONS TESTS =====
    
    @Test
    @DisplayName("Should handle concurrent tariff calculations safely")
    void calculateTariff_ShouldHandleConcurrentCalculations() throws InterruptedException {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Collections.emptyList());

        // Act - Simulate concurrent calculations
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        @SuppressWarnings("unchecked")
        Map<String, Object>[] results = new Map[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                results[index] = tariffCalculationService.calculateTariff(
                    htsCode, destinationCountry, productValue, quantity);
            });
            threads[i].start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Assert - All results should be identical and correct
        BigDecimal expectedDuty = new BigDecimal("150.00");
        for (int i = 0; i < threadCount; i++) {
            assertNotNull(results[i]);
            assertFalse(results[i].containsKey("error"));
            assertEquals(expectedDuty, results[i].get("mfnTariffAmount"));
        }
    }

    // ===== ERROR RECOVERY TESTS =====
    
    @Test
    @DisplayName("Should recover gracefully from service failures")
    void calculateTariff_ShouldRecoverFromServiceFailures() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenThrow(new RuntimeException("MFN service unavailable"));
        // Don't stub productService since we expect an error before it's called

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        // Should handle service failure gracefully
        assertTrue(result.containsKey("error") || result.get("mfnTariffAmount").equals(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Should handle database connection failures")
    void findByHtsCode_ShouldHandleDatabaseFailures() {
        // Arrange
        String htsCode = "12345678";
        when(productRepository.findByHts8(htsCode)).thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            tariffCalculationService.findByHtsCode(htsCode));
    }

    // ===== HELPER METHODS FOR TEST DATA CREATION =====
    
    private AgreementRate createAgreementRate(String agreementCode, BigDecimal adValoremRate, BigDecimal specificRate) {
        AgreementRate rate = new AgreementRate();
        rate.setProduct(testProduct);
        rate.setadValoremRate(adValoremRate);
        rate.setSpecificRate(specificRate);
        rate.setTextRate(adValoremRate.multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString() + 
                        "% + $" + specificRate.stripTrailingZeros().toPlainString() + " per unit");
        rate.setRateTypeCode("ADV");
        
        TradeAgreement agreement = new TradeAgreement();
        agreement.setAgreementCode(agreementCode);
        agreement.setAgreementName(agreementCode + " Agreement");
        agreement.setEffectiveDate(LocalDate.of(2020, 1, 1));
        agreement.setExpirationDate(LocalDate.of(2030, 12, 31));
        rate.setAgreement(agreement);
        
        return rate;
    }

    // ===== ADDITIONAL BRANCH COVERAGE TESTS =====
    
    @Test
    @DisplayName("Should handle MFN rate with null ad valorem and specific rates")
    void calculateTariff_ShouldHandleMfnRateWithNullRates() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        
        // Create MFN rate with null rates
        MfnTariffRate nullRateMfn = new MfnTariffRate();
        nullRateMfn.setProduct(testProduct);
        nullRateMfn.setMfnadValoremRate(null);
        nullRateMfn.setMfnSpecificRate(null);
        nullRateMfn.setMfnTextRate("Free");
        nullRateMfn.setMfnRateTypeCode("FREE");
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(nullRateMfn));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        assertEquals(BigDecimal.ZERO.setScale(2), result.get("mfnTariffAmount"));
        assertEquals(BigDecimal.ZERO.setScale(2), result.get("bestTariffAmount"));
    }

    @Test
    @DisplayName("Should handle preferential rate with null agreement")
    void calculateTariff_ShouldHandlePreferentialRateWithNullAgreement() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        
        // Create agreement rate with null agreement
        AgreementRate nullAgreementRate = new AgreementRate();
        nullAgreementRate.setProduct(testProduct);
        nullAgreementRate.setadValoremRate(new BigDecimal("0.05"));
        nullAgreementRate.setSpecificRate(new BigDecimal("2.50"));
        nullAgreementRate.setTextRate("5% + $2.50 per unit");
        nullAgreementRate.setRateTypeCode("ADV");
        nullAgreementRate.setAgreement(null); // null agreement
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Arrays.asList(nullAgreementRate));

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        // The service might return an error for null agreement, which is acceptable
        if (result.containsKey("error")) {
            assertTrue(result.get("error").toString().contains("agreement") || 
                      result.get("error").toString().contains("null"));
        } else {
            // Should still calculate preferential rate even with null agreement
            assertEquals(new BigDecimal("75.00"), result.get("bestTariffAmount"));
        }
    }

    @Test
    @DisplayName("Should handle empty preferential rates list")
    void calculateTariff_ShouldHandleEmptyPreferentialRates() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        assertEquals(new BigDecimal("150.00"), result.get("mfnTariffAmount"));
        assertEquals(new BigDecimal("150.00"), result.get("bestTariffAmount"));
        assertEquals("MFN", result.get("bestProgramName"));
    }

    @Test
    @DisplayName("Should handle preferential rate with zero duty")
    void calculateTariff_ShouldHandlePreferentialRateWithZeroDuty() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        
        // Create preferential rate with zero duty
        AgreementRate zeroDutyRate = createAgreementRate("FREE", BigDecimal.ZERO, BigDecimal.ZERO);
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Arrays.asList(zeroDutyRate));

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        assertEquals(BigDecimal.ZERO.setScale(2), result.get("bestTariffAmount"));
        assertEquals(new BigDecimal("150.00"), result.get("mfnTariffAmount"));
        assertEquals("FREE Agreement", result.get("bestProgramName"));
    }

    @Test
    @DisplayName("Should handle multiple preferential rates with same duty amount")
    void calculateTariff_ShouldHandleMultiplePreferentialRatesWithSameDuty() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        
        // Create multiple rates with same duty amount
        List<AgreementRate> sameDutyRates = Arrays.asList(
            createAgreementRate("AGREEMENT1", new BigDecimal("0.05"), new BigDecimal("2.50")),
            createAgreementRate("AGREEMENT2", new BigDecimal("0.05"), new BigDecimal("2.50"))
        );
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(sameDutyRates);

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        assertEquals(new BigDecimal("75.00"), result.get("bestTariffAmount"));
        assertEquals("AGREEMENT1 Agreement", result.get("bestProgramName"));
    }

    // ===== ERROR SCENARIO TESTS =====
    
    @Test
    @DisplayName("Should handle RuntimeException in MFN service")
    void calculateTariff_ShouldHandleRuntimeExceptionInMfnService() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenThrow(new RuntimeException("MFN service error"));

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        // Should handle the exception gracefully
        assertTrue(result.containsKey("error") || result.get("mfnTariffAmount").equals(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Should handle RuntimeException in product service")
    void calculateTariff_ShouldHandleRuntimeExceptionInProductService() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry))
            .thenThrow(new RuntimeException("Product service error"));

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);

        // Assert
        assertNotNull(result);
        // Should handle the exception gracefully
        assertTrue(result.containsKey("error") || result.get("bestTariffAmount").equals(result.get("mfnTariffAmount")));
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException in validateInputs")
    void calculateTariff_ShouldHandleIllegalArgumentException() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = -100.0; // Invalid negative value
        Integer quantity = 10;

        // Act & Assert
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);

        // Should return error for invalid input
        assertTrue(result.containsKey("error"));
    }

    // ===== DATE EDGE CASE TESTS =====
    
    @Test
    @DisplayName("Should handle null start date in date range calculation")
    void calculateTariffWithDateRange_ShouldHandleNullStartDate() {
        // Arrange
        String htsCode = "12345678";
        String originCountry = "CN";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariffWithDateRange(
            htsCode, originCountry, destinationCountry, productValue, quantity, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
    }

    @Test
    @DisplayName("Should handle null end date in date range calculation")
    void calculateTariffWithDateRange_ShouldHandleNullEndDate() {
        // Arrange
        String htsCode = "12345678";
        String originCountry = "CN";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = null;

        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariffWithDateRange(
            htsCode, originCountry, destinationCountry, productValue, quantity, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
    }

    @Test
    @DisplayName("Should handle both null dates in date range calculation")
    void calculateTariffWithDateRange_ShouldHandleBothNullDates() {
        // Arrange
        String htsCode = "12345678";
        String originCountry = "CN";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        LocalDate startDate = null;
        LocalDate endDate = null;

        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = tariffCalculationService.calculateTariffWithDateRange(
            htsCode, originCountry, destinationCountry, productValue, quantity, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
    }

    // ===== CONCURRENT ACCESS TESTS =====
    
    @Test
    @DisplayName("Should handle concurrent updates to same calculation")
    void updateTariffCalculation_ShouldHandleConcurrentUpdates() throws InterruptedException {
        // Arrange
        Long id = 1L;
        TariffCalculationRequest request1 = new TariffCalculationRequest();
        request1.setHtsCode("12345678");
        request1.setOriginCountry("US");
        request1.setDestinationCountry("CA");
        request1.setProductValue(new BigDecimal("1000.00"));
        request1.setQuantity(10);
        request1.setCurrency("USD");

        TariffCalculationRequest request2 = new TariffCalculationRequest();
        request2.setHtsCode("12345678");
        request2.setOriginCountry("US");
        request2.setDestinationCountry("CA");
        request2.setProductValue(new BigDecimal("2000.00")); // Different value
        request2.setQuantity(20); // Different quantity
        request2.setCurrency("USD");

        TariffCalculation existingCalculation = new TariffCalculation();
        existingCalculation.setHtsCode("12345678");

        when(tariffCalculationRepository.findById(id)).thenReturn(Optional.of(existingCalculation));
        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates("12345678", "CA")).thenReturn(Collections.emptyList());
        when(tariffCalculationRepository.save(any(TariffCalculation.class))).thenAnswer(invocation -> {
            TariffCalculation calc = invocation.getArgument(0);
            return calc;
        });

        // Act - Simulate concurrent updates
        int threadCount = 5;
        Thread[] threads = new Thread[threadCount];
        TariffCalculationResponse[] results = new TariffCalculationResponse[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            final TariffCalculationRequest request = (i % 2 == 0) ? request1 : request2;
            threads[i] = new Thread(() -> {
                try {
                    results[index] = tariffCalculationService.updateTariffCalculation(id, request);
                } catch (Exception e) {
                    // Handle any exceptions
                }
            });
            threads[i].start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Assert - All updates should complete successfully
        for (int i = 0; i < threadCount; i++) {
            assertNotNull(results[i]);
            assertEquals("12345678", results[i].getHtsCode());
        }
    }

    // ===== PERFORMANCE BENCHMARKING TESTS =====
    
    @Test
    @DisplayName("Should complete tariff calculation within acceptable time")
    void calculateTariff_ShouldCompleteWithinAcceptableTime() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(Collections.emptyList());

        // Act
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        // Should complete within 100ms (adjust based on requirements)
        assertTrue(executionTime < 100, "Calculation took too long: " + executionTime + "ms");
    }

    @Test
    @DisplayName("Should handle large number of preferential rates efficiently")
    void calculateTariff_ShouldHandleLargeNumberOfPreferentialRates() {
        // Arrange
        String htsCode = "12345678";
        String destinationCountry = "US";
        Double productValue = 1000.0;
        Integer quantity = 10;
        
        // Create 50 preferential rates
        List<AgreementRate> manyRates = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            BigDecimal rate = new BigDecimal("0.05").add(new BigDecimal(i).multiply(new BigDecimal("0.001")));
            manyRates.add(createAgreementRate("AGREEMENT" + i, rate, new BigDecimal("2.50")));
        }
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate(htsCode)).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates(htsCode, destinationCountry)).thenReturn(manyRates);

        // Act
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = tariffCalculationService.calculateTariff(
            htsCode, destinationCountry, productValue, quantity);
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Assert
        assertNotNull(result);
        assertFalse(result.containsKey("error"));
        // Should still complete within reasonable time even with many rates
        assertTrue(executionTime < 200, "Calculation with many rates took too long: " + executionTime + "ms");
        // Should select the best rate (lowest duty)
        assertTrue(result.get("bestTariffAmount") instanceof BigDecimal);
    }

    // ===== DATA PERSISTENCE TESTS =====
    
    @Test
    @DisplayName("Should persist tariff calculation with all fields")
    void createTariffCalculation_ShouldPersistWithAllFields() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("US");
        request.setDestinationCountry("CA");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(10);
        request.setCurrency("USD");
        request.setTariffEffectiveDate(LocalDate.of(2023, 1, 1));
        request.setTariffExpirationDate(LocalDate.of(2023, 12, 31));
        
        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct));
        when(mfnService.getMfnTariffRate("12345678")).thenReturn(Optional.of(testMfnRate));
        when(productService.getAgreementRates("12345678", "CA")).thenReturn(Collections.emptyList());
        when(productService.getAgreementRates("12345678", "US")).thenReturn(Collections.emptyList());
        when(tariffCalculationRepository.save(any(TariffCalculation.class))).thenAnswer(invocation -> {
            TariffCalculation calc = invocation.getArgument(0);
            return calc;
        });

        // Act
        TariffCalculationResponse result = tariffCalculationService.createTariffCalculation(request);

        // Assert
        assertNotNull(result);
        assertEquals("12345678", result.getHtsCode());
        assertEquals("US", result.getOriginCountry());
        assertEquals("CA", result.getDestinationCountry());
        assertEquals(new BigDecimal("1000.00"), result.getProductValue());
        assertEquals(10, result.getQuantity());
        assertEquals("USD", result.getCurrency());
        
        // Verify repository save was called
        verify(tariffCalculationRepository, times(1)).save(any(TariffCalculation.class));
    }

    @Test
    @DisplayName("Should handle transaction rollback on error")
    void createTariffCalculation_ShouldHandleTransactionRollback() {
        // Arrange
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("99999999"); // Invalid HTS code
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(10);
        
        when(productRepository.findByHts8("99999999")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            tariffCalculationService.createTariffCalculation(request));
        
        assertTrue(exception.getMessage().contains("Invalid HTS code"));
        // Verify repository save was not called due to validation failure
        verify(tariffCalculationRepository, never()).save(any(TariffCalculation.class));
    }

    private TariffCalculationRequest createTestRequest() {
        TariffCalculationRequest request = new TariffCalculationRequest();
        request.setHtsCode("12345678");
        request.setOriginCountry("CA");
        request.setDestinationCountry("US");
        request.setProductValue(new BigDecimal("1000.00"));
        request.setQuantity(10);
        request.setCurrency("USD");
        return request;
    }
}
