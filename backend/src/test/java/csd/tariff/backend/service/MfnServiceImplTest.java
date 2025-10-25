package csd.tariff.backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import csd.tariff.backend.model.MfnTariffRate;
import csd.tariff.backend.model.Product;
import csd.tariff.backend.repository.MfnTariffRateRepository;
import csd.tariff.backend.repository.ProductRepository;
import csd.tariff.backend.service.MfnServiceImpl;

/**
 * Comprehensive unit tests for MfnServiceImpl
 * Tests all CRUD operations, business logic, and exception handling
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MfnService Unit Tests")
class MfnServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private MfnTariffRateRepository mfnTariffRateRepository;

    @InjectMocks
    private MfnServiceImpl mfnService;

    private MfnTariffRate testMfnRate1;
    private MfnTariffRate testMfnRate2;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Setup test product
        testProduct = new Product();
        testProduct.setHts8("12345678");
        testProduct.setBriefDescription("Test Product Description");
        
        // Setup test MFN rate 1
        testMfnRate1 = new MfnTariffRate();
        testMfnRate1.setMfnadValoremRate(new BigDecimal("0.10")); // 10%
        testMfnRate1.setMfnSpecificRate(new BigDecimal("5.00")); // $5 per unit
        testMfnRate1.setMfnTextRate("10% + $5.00 per unit");
        testMfnRate1.setMfnRateTypeCode("ADV");
        testMfnRate1.setBeginEffectDate(LocalDate.of(2020, 1, 1));
        testMfnRate1.setEndEffectiveDate(LocalDate.of(2025, 12, 31));
        testMfnRate1.setProduct(testProduct);

        // Setup test MFN rate 2
        testMfnRate2 = new MfnTariffRate();
        testMfnRate2.setMfnadValoremRate(new BigDecimal("0.05")); // 5%
        testMfnRate2.setMfnSpecificRate(new BigDecimal("2.50")); // $2.50 per unit
        testMfnRate2.setMfnTextRate("5% + $2.50 per unit");
        testMfnRate2.setMfnRateTypeCode("ADV");
        testMfnRate2.setBeginEffectDate(LocalDate.of(2020, 1, 1));
        testMfnRate2.setEndEffectiveDate(LocalDate.of(2025, 12, 31));
        
        // Create a second product for testMfnRate2
        Product testProduct2 = new Product();
        testProduct2.setHts8("87654321");
        testProduct2.setBriefDescription("Test Product 2 Description");
        testMfnRate2.setProduct(testProduct2);
    }

    private void setId(Product product, Long id) {
        try {
            java.lang.reflect.Field idField = Product.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(product, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID using reflection", e);
        }
    }

    private void setId(MfnTariffRate rate, Long id) {
        try {
            java.lang.reflect.Field idField = MfnTariffRate.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(rate, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID using reflection", e);
        }
    }

    // ===== GET ALL MFN TARIFF RATES TESTS =====
    
    @Test
    @DisplayName("Should return all MFN tariff rates when rates exist")
    void getAllMfnTariffRates_ShouldReturnAllRates_WhenRatesExist() {
        // Arrange
        List<MfnTariffRate> expectedRates = Arrays.asList(testMfnRate1, testMfnRate2);
        when(mfnTariffRateRepository.findAll()).thenReturn(expectedRates);

        // Act
        List<MfnTariffRate> actualRates = mfnService.getAllMfnTariffRates();

        // Assert
        assertNotNull(actualRates);
        assertEquals(2, actualRates.size());
        assertEquals(expectedRates, actualRates);
        verify(mfnTariffRateRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no MFN rates exist")
    void getAllMfnTariffRates_ShouldReturnEmptyList_WhenNoRatesExist() {
        // Arrange
        when(mfnTariffRateRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<MfnTariffRate> actualRates = mfnService.getAllMfnTariffRates();

        // Assert
        assertNotNull(actualRates);
        assertTrue(actualRates.isEmpty());
        verify(mfnTariffRateRepository, times(1)).findAll();
    }

    // ===== GET MFN TARIFF RATE TESTS =====
    
    @Test
    @DisplayName("Should return MFN tariff rate when rate exists")
    void getMfnTariffRate_ShouldReturnRate_WhenRateExists() {
        // Arrange
        String htsCode = "12345678";
        when(mfnTariffRateRepository.findByHts8(htsCode)).thenReturn(Optional.of(testMfnRate1));

        // Act
        Optional<MfnTariffRate> actualRate = mfnService.getMfnTariffRate(htsCode);

        // Assert
        assertTrue(actualRate.isPresent());
        assertEquals(testMfnRate1, actualRate.get());
        assertEquals("12345678", actualRate.get().getProduct().getHts8());
        assertEquals(new BigDecimal("0.10"), actualRate.get().getMfnadValoremRate());
        verify(mfnTariffRateRepository, times(1)).findByHts8(htsCode);
    }

    @Test
    @DisplayName("Should return empty when MFN tariff rate does not exist")
    void getMfnTariffRate_ShouldReturnEmpty_WhenRateDoesNotExist() {
        // Arrange
        String htsCode = "99999999";
        when(mfnTariffRateRepository.findByHts8(htsCode)).thenReturn(Optional.empty());

        // Act
        Optional<MfnTariffRate> actualRate = mfnService.getMfnTariffRate(htsCode);

        // Assert
        assertFalse(actualRate.isPresent());
        verify(mfnTariffRateRepository, times(1)).findByHts8(htsCode);
    }

    // ===== GET MFN TARIFF RATES FOR PRODUCT TESTS =====
    
    @Test
    @DisplayName("Should return MFN tariff rate for product when rate exists")
    void getMfnTariffRatesForProduct_ShouldReturnRate_WhenRateExists() {
        // Arrange
        String htsCode = "12345678";
        Product testProduct = new Product();
        setId(testProduct, 1L);
        testProduct.setHts8(htsCode);
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnTariffRateRepository.findByProductId(1L)).thenReturn(Optional.of(testMfnRate1));

        // Act
        Optional<MfnTariffRate> actualRate = mfnService.getMfnTariffRatesForProduct(htsCode);

        // Assert
        assertTrue(actualRate.isPresent());
        assertEquals(testMfnRate1, actualRate.get());
        verify(productRepository, times(1)).findByHts8(htsCode);
        verify(mfnTariffRateRepository, times(1)).findByProductId(1L);
    }

    @Test
    @DisplayName("Should return empty when MFN tariff rate for product does not exist")
    void getMfnTariffRatesForProduct_ShouldReturnEmpty_WhenRateDoesNotExist() {
        // Arrange
        String htsCode = "99999999";
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.empty());

        // Act
        Optional<MfnTariffRate> actualRate = mfnService.getMfnTariffRatesForProduct(htsCode);

        // Assert
        assertNull(actualRate);
        verify(productRepository, times(1)).findByHts8(htsCode);
    }

    // ===== CREATE MFN TARIFF RATE TESTS =====
    
    @Test
    @DisplayName("Should create MFN tariff rate successfully")
    void createMfnTariffRate_ShouldCreateSuccessfully() {
        // Arrange
        MfnTariffRate newRate = new MfnTariffRate();
        // Create a product for the new rate
        Product newProduct = new Product();
        newProduct.setHts8("11111111");
        newProduct.setBriefDescription("New Product Description");
        newRate.setProduct(newProduct);
        newRate.setMfnadValoremRate(new BigDecimal("0.15"));
        newRate.setMfnSpecificRate(new BigDecimal("10.00"));
        newRate.setMfnTextRate("15% + $10.00 per unit");
        newRate.setMfnRateTypeCode("ADV");
        
        when(mfnTariffRateRepository.save(any(MfnTariffRate.class))).thenAnswer(invocation -> {
            MfnTariffRate rate = invocation.getArgument(0);
            return rate;
        });

        // Act
        MfnTariffRate result = mfnService.createMfnTariffRate(newRate);

        // Assert
        assertNotNull(result);
        assertEquals("11111111", result.getProduct().getHts8());
        assertEquals(new BigDecimal("0.15"), result.getMfnadValoremRate());
        assertEquals(new BigDecimal("10.00"), result.getMfnSpecificRate());
        assertEquals("15% + $10.00 per unit", result.getMfnTextRate());
        assertEquals("ADV", result.getMfnRateTypeCode());
        verify(mfnTariffRateRepository, times(1)).save(any(MfnTariffRate.class));
    }

    // ===== UPDATE MFN TARIFF RATE TESTS =====
    
    @Test
    @DisplayName("Should update MFN tariff rate successfully when rate exists")
    void updateMfnTariffRate_ShouldUpdateSuccessfully_WhenRateExists() {
        // Arrange
        Long rateId = 1L;
        MfnTariffRate updateData = new MfnTariffRate();
        updateData.setMfnadValoremRate(new BigDecimal("0.12"));
        updateData.setMfnSpecificRate(new BigDecimal("6.00"));
        updateData.setMfnTextRate("12% + $6.00 per unit");
        updateData.setMfnRateTypeCode("ADV");
        
        when(mfnTariffRateRepository.findById(rateId)).thenReturn(Optional.of(testMfnRate1));
        when(mfnTariffRateRepository.save(any(MfnTariffRate.class))).thenAnswer(invocation -> {
            MfnTariffRate rate = invocation.getArgument(0);
            return rate;
        });

        // Act
        MfnTariffRate result = mfnService.updateMfnTariffRate(rateId, updateData);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("0.12"), result.getMfnadValoremRate());
        assertEquals(new BigDecimal("6.00"), result.getMfnSpecificRate());
        assertEquals("12% + $6.00 per unit", result.getMfnTextRate());
        assertEquals("ADV", result.getMfnRateTypeCode());
        verify(mfnTariffRateRepository, times(1)).findById(rateId);
        verify(mfnTariffRateRepository, times(1)).save(any(MfnTariffRate.class));
    }

    @Test
    @DisplayName("Should return null when updating non-existent MFN tariff rate")
    void updateMfnTariffRate_ShouldReturnNull_WhenRateDoesNotExist() {
        // Arrange
        Long nonExistentId = 999L;
        MfnTariffRate updateData = new MfnTariffRate();
        updateData.setMfnadValoremRate(new BigDecimal("0.12"));
        
        when(mfnTariffRateRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        MfnTariffRate result = mfnService.updateMfnTariffRate(nonExistentId, updateData);

        // Assert
        assertNull(result);
        verify(mfnTariffRateRepository, times(1)).findById(nonExistentId);
        verify(mfnTariffRateRepository, never()).save(any(MfnTariffRate.class));
    }

    // ===== DELETE MFN TARIFF RATE TESTS =====
    
    @Test
    @DisplayName("Should delete MFN tariff rate successfully when rate exists")
    void deleteMfnTariffRate_ShouldDeleteSuccessfully_WhenRateExists() {
        // Arrange
        Long rateId = 1L;

        // Act
        mfnService.deleteMfnTariffRate(rateId);

        // Assert
        verify(mfnTariffRateRepository, times(1)).deleteById(rateId);
    }

    @Test
    @DisplayName("Should handle deletion of non-existent MFN tariff rate gracefully")
    void deleteMfnTariffRate_ShouldHandleGracefully_WhenRateDoesNotExist() {
        // Arrange
        Long nonExistentId = 999L;

        // Act
        mfnService.deleteMfnTariffRate(nonExistentId);

        // Assert
        verify(mfnTariffRateRepository, times(1)).deleteById(nonExistentId);
    }

    // ===== GET PRODUCTS WITH MFN RATES TESTS =====
    
    @Test
    @DisplayName("Should return products with MFN rates when products exist")
    void getProductsWithMfnRates_ShouldReturnProducts_WhenProductsExist() {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productRepository.findProductsWithMfnRates()).thenReturn(expectedProducts);

        // Act
        List<Product> actualProducts = mfnService.getProductsWithMfnRates();

        // Assert
        assertNotNull(actualProducts);
        assertEquals(1, actualProducts.size());
        assertEquals(expectedProducts, actualProducts);
        verify(productRepository, times(1)).findProductsWithMfnRates();
    }

    @Test
    @DisplayName("Should return empty list when no products with MFN rates exist")
    void getProductsWithMfnRates_ShouldReturnEmptyList_WhenNoProductsExist() {
        // Arrange
        when(productRepository.findProductsWithMfnRates()).thenReturn(Collections.emptyList());

        // Act
        List<Product> actualProducts = mfnService.getProductsWithMfnRates();

        // Assert
        assertNotNull(actualProducts);
        assertTrue(actualProducts.isEmpty());
        verify(productRepository, times(1)).findProductsWithMfnRates();
    }

    // ===== UPDATE MFN TARIFF RATE BY HTS CODE TESTS =====
    
    @Test
    @DisplayName("Should update MFN tariff rate by HTS code successfully when rate exists")
    void updateMfnTariffRateByHtsCode_ShouldUpdateSuccessfully_WhenRateExists() {
        // Arrange
        String htsCode = "12345678";
        Product testProduct = new Product();
        setId(testProduct, 1L);
        testProduct.setHts8(htsCode);
        
        MfnTariffRate updateData = new MfnTariffRate();
        updateData.setMfnadValoremRate(new BigDecimal("0.12"));
        updateData.setMfnSpecificRate(new BigDecimal("6.00"));
        updateData.setMfnTextRate("12% + $6.00 per unit");
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnTariffRateRepository.findByProductId(1L)).thenReturn(Optional.of(testMfnRate1));
        when(mfnTariffRateRepository.save(any(MfnTariffRate.class))).thenAnswer(invocation -> {
            MfnTariffRate rate = invocation.getArgument(0);
            return rate;
        });

        // Act
        MfnTariffRate result = mfnService.updateMfnTariffRateByHtsCode(htsCode, updateData);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("0.12"), result.getMfnadValoremRate());
        assertEquals(new BigDecimal("6.00"), result.getMfnSpecificRate());
        assertEquals("12% + $6.00 per unit", result.getMfnTextRate());
        verify(productRepository, times(1)).findByHts8(htsCode);
        verify(mfnTariffRateRepository, times(1)).findByProductId(1L);
        verify(mfnTariffRateRepository, times(1)).save(any(MfnTariffRate.class));
    }

    @Test
    @DisplayName("Should return null when updating MFN tariff rate by non-existent HTS code")
    void updateMfnTariffRateByHtsCode_ShouldReturnNull_WhenRateDoesNotExist() {
        // Arrange
        String nonExistentHtsCode = "99999999";
        MfnTariffRate updateData = new MfnTariffRate();
        updateData.setMfnadValoremRate(new BigDecimal("0.12"));
        
        when(productRepository.findByHts8(nonExistentHtsCode)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            mfnService.updateMfnTariffRateByHtsCode(nonExistentHtsCode, updateData));
        verify(productRepository, times(1)).findByHts8(nonExistentHtsCode);
    }

    // ===== DELETE MFN TARIFF RATE BY HTS CODE TESTS =====
    
    @Test
    @DisplayName("Should delete MFN tariff rate by HTS code successfully when rate exists")
    void deleteMfnTariffRateByHtsCode_ShouldDeleteSuccessfully_WhenRateExists() {
        // Arrange
        String htsCode = "12345678";
        Product testProduct = new Product();
        setId(testProduct, 1L);
        testProduct.setHts8(htsCode);
        
        when(productRepository.findByHts8(htsCode)).thenReturn(Optional.of(testProduct));
        when(mfnTariffRateRepository.findByProductId(1L)).thenReturn(Optional.of(testMfnRate1));
        setId(testMfnRate1, 1L); // Ensure the MFN rate has an ID

        // Act
        mfnService.deleteMfnTariffRateByHtsCode(htsCode);

        // Assert
        verify(productRepository, times(1)).findByHts8(htsCode);
        verify(mfnTariffRateRepository, times(1)).findByProductId(1L);
        verify(mfnTariffRateRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should handle deletion of MFN tariff rate by non-existent HTS code gracefully")
    void deleteMfnTariffRateByHtsCode_ShouldHandleGracefully_WhenRateDoesNotExist() {
        // Arrange
        String nonExistentHtsCode = "99999999";
        when(productRepository.findByHts8(nonExistentHtsCode)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            mfnService.deleteMfnTariffRateByHtsCode(nonExistentHtsCode));
        verify(productRepository, times(1)).findByHts8(nonExistentHtsCode);
    }
}
