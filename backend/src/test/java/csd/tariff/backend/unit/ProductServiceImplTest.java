package csd.tariff.backend.unit;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import csd.tariff.backend.model.AgreementRate;
import csd.tariff.backend.model.Product;
import csd.tariff.backend.model.TradeAgreement;
import csd.tariff.backend.repository.AgreementRateRepository;
import csd.tariff.backend.repository.ProductRepository;
import csd.tariff.backend.service.ProductServiceImpl;

/**
 * Comprehensive unit tests for ProductServiceImpl
 * Tests all CRUD operations, business logic, and exception handling
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AgreementRateRepository agreementRateRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct1;
    private Product testProduct2;
    private AgreementRate testAgreementRate1;
    private AgreementRate testAgreementRate2;
    private TradeAgreement testTradeAgreement;

    @BeforeEach
    void setUp() {
        // Setup test product 1
        testProduct1 = new Product();
        testProduct1.setHts8("12345678");
        testProduct1.setBriefDescription("Test Product 1 Description");
        setProductId(testProduct1, 1L);

        // Setup test product 2
        testProduct2 = new Product();
        testProduct2.setHts8("87654321");
        testProduct2.setBriefDescription("Test Product 2 Description");
        setProductId(testProduct2, 2L);

        // Setup test trade agreement
        testTradeAgreement = new TradeAgreement();
        testTradeAgreement.setAgreementCode("USMCA");
        testTradeAgreement.setAgreementName("US-Mexico-Canada Agreement");

        // Setup test agreement rate 1
        testAgreementRate1 = new AgreementRate();
        testAgreementRate1.setProduct(testProduct1);
        testAgreementRate1.setadValoremRate(new java.math.BigDecimal("0.05")); // 5%
        testAgreementRate1.setSpecificRate(new java.math.BigDecimal("2.50")); // $2.50 per unit
        testAgreementRate1.setTextRate("5% + $2.50 per unit");
        testAgreementRate1.setRateTypeCode("ADV");
        testAgreementRate1.setAgreement(testTradeAgreement);

        // Setup test agreement rate 2
        testAgreementRate2 = new AgreementRate();
        testAgreementRate2.setProduct(testProduct1);
        testAgreementRate2.setadValoremRate(new java.math.BigDecimal("0.03")); // 3%
        testAgreementRate2.setSpecificRate(new java.math.BigDecimal("1.50")); // $1.50 per unit
        testAgreementRate2.setTextRate("3% + $1.50 per unit");
        testAgreementRate2.setRateTypeCode("ADV");
        testAgreementRate2.setAgreement(testTradeAgreement);
    }

    // ===== GET ALL PRODUCTS TESTS =====
    
    @Test
    @DisplayName("Should return all products when products exist")
    void getAllProducts_ShouldReturnAllProducts_WhenProductsExist() {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(testProduct1, testProduct2);
        when(productRepository.findAllWithMfnRates()).thenReturn(expectedProducts);

        // Act
        List<Product> actualProducts = productService.getAllProducts();

        // Assert
        assertNotNull(actualProducts);
        assertEquals(2, actualProducts.size());
        assertEquals(expectedProducts, actualProducts);
        verify(productRepository, times(1)).findAllWithMfnRates();
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void getAllProducts_ShouldReturnEmptyList_WhenNoProductsExist() {
        // Arrange
        when(productRepository.findAllWithMfnRates()).thenReturn(Collections.emptyList());

        // Act
        List<Product> actualProducts = productService.getAllProducts();

        // Assert
        assertNotNull(actualProducts);
        assertTrue(actualProducts.isEmpty());
        verify(productRepository, times(1)).findAllWithMfnRates();
    }

    // ===== GET AGREEMENT RATES TESTS =====
    
    @Test
    @DisplayName("Should return agreement rates for product and country when rates exist")
    void getAgreementRates_ShouldReturnRates_WhenRatesExist() {
        // Arrange
        String htsCode = "12345678";
        String countryCode = "US";
        List<AgreementRate> expectedRates = Arrays.asList(testAgreementRate1, testAgreementRate2);
        when(agreementRateRepository.findByHts8AndCountryCode(htsCode, countryCode))
            .thenReturn(expectedRates);

        // Act
        List<AgreementRate> actualRates = productService.getAgreementRates(htsCode, countryCode);

        // Assert
        assertNotNull(actualRates);
        assertEquals(2, actualRates.size());
        assertEquals(expectedRates, actualRates);
        verify(agreementRateRepository, times(1)).findByHts8AndCountryCode(htsCode, countryCode);
    }

    @Test
    @DisplayName("Should return empty list when no agreement rates exist for product and country")
    void getAgreementRates_ShouldReturnEmptyList_WhenNoRatesExist() {
        // Arrange
        String htsCode = "99999999";
        String countryCode = "XX";
        when(agreementRateRepository.findByHts8AndCountryCode(htsCode, countryCode))
            .thenReturn(Collections.emptyList());

        // Act
        List<AgreementRate> actualRates = productService.getAgreementRates(htsCode, countryCode);

        // Assert
        assertNotNull(actualRates);
        assertTrue(actualRates.isEmpty());
        verify(agreementRateRepository, times(1)).findByHts8AndCountryCode(htsCode, countryCode);
    }

    @Test
    @DisplayName("Should return agreement rates for product when rates exist")
    void getAgreementRates_ShouldReturnRatesForProduct_WhenRatesExist() {
        // Arrange
        String htsCode = "12345678";
        List<AgreementRate> expectedRates = Arrays.asList(testAgreementRate1, testAgreementRate2);
        when(agreementRateRepository.findByHts8(htsCode)).thenReturn(expectedRates);

        // Act
        List<AgreementRate> actualRates = productService.getAgreementRates(htsCode);

        // Assert
        assertNotNull(actualRates);
        assertEquals(2, actualRates.size());
        assertEquals(expectedRates, actualRates);
        verify(agreementRateRepository, times(1)).findByHts8(htsCode);
    }

    @Test
    @DisplayName("Should return empty list when no agreement rates exist for product")
    void getAgreementRates_ShouldReturnEmptyListForProduct_WhenNoRatesExist() {
        // Arrange
        String htsCode = "99999999";
        when(agreementRateRepository.findByHts8(htsCode)).thenReturn(Collections.emptyList());

        // Act
        List<AgreementRate> actualRates = productService.getAgreementRates(htsCode);

        // Assert
        assertNotNull(actualRates);
        assertTrue(actualRates.isEmpty());
        verify(agreementRateRepository, times(1)).findByHts8(htsCode);
    }

    // ===== CREATE PRODUCT TESTS =====
    
    @Test
    @DisplayName("Should create product successfully with valid data")
    void createProduct_ShouldCreateSuccessfully_WithValidData() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setHts8("11111111");
        newProduct.setBriefDescription("New Product Description");
        newProduct.setBriefDescription("Brief description for new product");
        
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return product;
        });

        // Act
        Product result = productService.createProduct(newProduct);

        // Assert
        assertNotNull(result);
        assertEquals("11111111", result.getHts8());
        assertEquals("Brief description for new product", result.getBriefDescription());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should create product with minimal data")
    void createProduct_ShouldCreateWithMinimalData() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setHts8("22222222");
        newProduct.setBriefDescription("Minimal Product");
        
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return product;
        });

        // Act
        Product result = productService.createProduct(newProduct);

        // Assert
        assertNotNull(result);
        assertEquals("22222222", result.getHts8());
        assertEquals("Minimal Product", result.getBriefDescription());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // ===== UPDATE PRODUCT TESTS =====
    
    @Test
    @DisplayName("Should update product successfully when product exists")
    void updateProduct_ShouldUpdateSuccessfully_WhenProductExists() {
        // Arrange
        Long productId = 1L;
        Product updateData = new Product();
        updateData.setHts8("12345678");
        updateData.setBriefDescription("Updated Product Description");
        updateData.setBriefDescription("Updated brief description");
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct1));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return product;
        });

        // Act
        Product result = productService.updateProduct(productId, updateData);

        // Assert
        assertNotNull(result);
        assertEquals("Updated brief description", result.getBriefDescription());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should return null when updating non-existent product")
    void updateProduct_ShouldReturnNull_WhenProductDoesNotExist() {
        // Arrange
        Long nonExistentId = 999L;
        Product updateData = new Product();
        updateData.setBriefDescription("Updated Description");
        
        when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            productService.updateProduct(nonExistentId, updateData));
        verify(productRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should update product with partial data")
    void updateProduct_ShouldUpdateWithPartialData() {
        // Arrange
        Long productId = 1L;
        Product updateData = new Product();
        updateData.setHts8("12345678");
        updateData.setBriefDescription("Partially Updated Description");
        // Only updating brief description, leaving other fields unchanged
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct1));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return product;
        });

        // Act
        Product result = productService.updateProduct(productId, updateData);

        // Assert
        assertNotNull(result);
        assertEquals("Partially Updated Description", result.getBriefDescription());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // ===== DELETE PRODUCT TESTS =====
    
    @Test
    @DisplayName("Should delete product successfully when product exists")
    void deleteProduct_ShouldDeleteSuccessfully_WhenProductExists() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct1));

        // Act
        productService.deleteProduct(productId);

        // Assert
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).delete(testProduct1);
    }

    @Test
    @DisplayName("Should handle deletion of non-existent product gracefully")
    void deleteProduct_ShouldHandleGracefully_WhenProductDoesNotExist() {
        // Arrange
        Long nonExistentId = 999L;
        when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            productService.deleteProduct(nonExistentId));
        verify(productRepository, times(1)).findById(nonExistentId);
    }

    // ===== EDGE CASES AND ERROR HANDLING TESTS =====
    
    @Test
    @DisplayName("Should handle null HTS code in getAgreementRates")
    void getAgreementRates_ShouldHandleNullHtsCode() {
        // Arrange
        String countryCode = "US";
        when(agreementRateRepository.findByHts8AndCountryCode(null, countryCode))
            .thenReturn(Collections.emptyList());

        // Act
        List<AgreementRate> actualRates = productService.getAgreementRates(null, countryCode);

        // Assert
        assertNotNull(actualRates);
        assertTrue(actualRates.isEmpty());
        verify(agreementRateRepository, times(1)).findByHts8AndCountryCode(null, countryCode);
    }

    @Test
    @DisplayName("Should handle null country code in getAgreementRates")
    void getAgreementRates_ShouldHandleNullCountryCode() {
        // Arrange
        String htsCode = "12345678";
        when(agreementRateRepository.findByHts8AndCountryCode(htsCode, null))
            .thenReturn(Collections.emptyList());

        // Act
        List<AgreementRate> actualRates = productService.getAgreementRates(htsCode, null);

        // Assert
        assertNotNull(actualRates);
        assertTrue(actualRates.isEmpty());
        verify(agreementRateRepository, times(1)).findByHts8AndCountryCode(htsCode, null);
    }

    @Test
    @DisplayName("Should handle empty HTS code in getAgreementRates")
    void getAgreementRates_ShouldHandleEmptyHtsCode() {
        // Arrange
        String emptyHtsCode = "";
        String countryCode = "US";
        when(agreementRateRepository.findByHts8AndCountryCode(emptyHtsCode, countryCode))
            .thenReturn(Collections.emptyList());

        // Act
        List<AgreementRate> actualRates = productService.getAgreementRates(emptyHtsCode, countryCode);

        // Assert
        assertNotNull(actualRates);
        assertTrue(actualRates.isEmpty());
        verify(agreementRateRepository, times(1)).findByHts8AndCountryCode(emptyHtsCode, countryCode);
    }

    @Test
    @DisplayName("Should handle repository exception gracefully")
    void getAllProducts_ShouldHandleRepositoryException_Gracefully() {
        // Arrange
        when(productRepository.findAllWithMfnRates()).thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> productService.getAllProducts());
        verify(productRepository, times(1)).findAllWithMfnRates();
    }

    @Test
    @DisplayName("Should handle save exception gracefully")
    void createProduct_ShouldHandleSaveException_Gracefully() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setHts8("11111111");
        newProduct.setBriefDescription("New Product Description");
        
        when(productRepository.save(any(Product.class)))
            .thenThrow(new RuntimeException("Database save error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> productService.createProduct(newProduct));
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // ===== ADDITIONAL ERROR HANDLING TESTS =====
    
    @Test
    @DisplayName("Should throw exception when creating product with null request")
    void createProduct_ShouldThrowException_WhenRequestIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            productService.createProduct(null));
    }

    @Test
    @DisplayName("Should throw exception when creating product with null HTS code")
    void createProduct_ShouldThrowException_WhenHtsCodeIsNull() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setHts8(null);
        newProduct.setBriefDescription("Test Description");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            productService.createProduct(newProduct));
    }

    @Test
    @DisplayName("Should throw exception when creating product with blank HTS code")
    void createProduct_ShouldThrowException_WhenHtsCodeIsBlank() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setHts8("   ");
        newProduct.setBriefDescription("Test Description");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            productService.createProduct(newProduct));
    }

    @Test
    @DisplayName("Should throw exception when creating product with null description")
    void createProduct_ShouldThrowException_WhenDescriptionIsNull() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setHts8("11111111");
        newProduct.setBriefDescription(null);

        when(productRepository.findByHts8("11111111")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            productService.createProduct(newProduct));
    }

    @Test
    @DisplayName("Should throw exception when creating product with blank description")
    void createProduct_ShouldThrowException_WhenDescriptionIsBlank() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setHts8("11111111");
        newProduct.setBriefDescription("   ");

        when(productRepository.findByHts8("11111111")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            productService.createProduct(newProduct));
    }

    @Test
    @DisplayName("Should throw exception when HTS code already exists")
    void createProduct_ShouldThrowException_WhenHtsCodeExists() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setHts8("12345678");
        newProduct.setBriefDescription("Test Description");

        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct1));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> 
            productService.createProduct(newProduct));
        verify(productRepository, times(1)).findByHts8("12345678");
    }

    @Test
    @DisplayName("Should normalize HTS code to uppercase")
    void createProduct_ShouldNormalizeHtsCode_ToUppercase() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setHts8("abc12345");
        newProduct.setBriefDescription("Test Description");

        when(productRepository.findByHts8("ABC12345")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return product;
        });

        // Act
        Product result = productService.createProduct(newProduct);

        // Assert
        assertNotNull(result);
        assertEquals("ABC12345", result.getHts8());
        verify(productRepository, times(1)).findByHts8("ABC12345");
    }

    @Test
    @DisplayName("Should handle null quantity codes")
    void createProduct_ShouldHandleNullQuantityCodes() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setHts8("11111111");
        newProduct.setBriefDescription("Test Description");
        newProduct.setQuantity1Code(null);
        newProduct.setQuantity2Code(null);

        when(productRepository.findByHts8("11111111")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return product;
        });

        // Act
        Product result = productService.createProduct(newProduct);

        // Assert
        assertNotNull(result);
        assertNull(result.getQuantity1Code());
        assertNull(result.getQuantity2Code());
    }

    @Test
    @DisplayName("Should handle blank quantity codes")
    void createProduct_ShouldHandleBlankQuantityCodes() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setHts8("11111111");
        newProduct.setBriefDescription("Test Description");
        newProduct.setQuantity1Code("   ");
        newProduct.setQuantity2Code("   ");

        when(productRepository.findByHts8("11111111")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return product;
        });

        // Act
        Product result = productService.createProduct(newProduct);

        // Assert
        assertNotNull(result);
        assertNull(result.getQuantity1Code());
        assertNull(result.getQuantity2Code());
    }

    @Test
    @DisplayName("Should normalize WTO binding code to uppercase")
    void createProduct_ShouldNormalizeWtoBindingCode_ToUppercase() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setHts8("11111111");
        newProduct.setBriefDescription("Test Description");
        newProduct.setWtoBindingCode("abc");

        when(productRepository.findByHts8("11111111")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return product;
        });

        // Act
        Product result = productService.createProduct(newProduct);

        // Assert
        assertNotNull(result);
        assertEquals("ABC", result.getWtoBindingCode());
    }

    @Test
    @DisplayName("Should handle null WTO binding code")
    void createProduct_ShouldHandleNullWtoBindingCode() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setHts8("11111111");
        newProduct.setBriefDescription("Test Description");
        newProduct.setWtoBindingCode(null);

        when(productRepository.findByHts8("11111111")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return product;
        });

        // Act
        Product result = productService.createProduct(newProduct);

        // Assert
        assertNotNull(result);
        assertNull(result.getWtoBindingCode());
    }

    @Test
    @DisplayName("Should handle blank WTO binding code")
    void createProduct_ShouldHandleBlankWtoBindingCode() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setHts8("11111111");
        newProduct.setBriefDescription("Test Description");
        newProduct.setWtoBindingCode("   ");

        when(productRepository.findByHts8("11111111")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return product;
        });

        // Act
        Product result = productService.createProduct(newProduct);

        // Assert
        assertNotNull(result);
        assertNull(result.getWtoBindingCode());
    }

    // ===== UPDATE PRODUCT ERROR HANDLING TESTS =====
    
    @Test
    @DisplayName("Should throw exception when updating product with null ID")
    void updateProduct_ShouldThrowException_WhenIdIsNull() {
        // Arrange
        Product updateData = new Product();
        updateData.setHts8("12345678");
        updateData.setBriefDescription("Updated Description");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            productService.updateProduct(null, updateData));
    }

    @Test
    @DisplayName("Should throw exception when updating product with null request")
    void updateProduct_ShouldThrowException_WhenRequestIsNull() {
        // Arrange
        Long productId = 1L;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            productService.updateProduct(productId, null));
    }

    @Test
    @DisplayName("Should throw exception when updating product with null HTS code")
    void updateProduct_ShouldThrowException_WhenHtsCodeIsNull() {
        // Arrange
        Long productId = 1L;
        Product updateData = new Product();
        updateData.setHts8(null);
        updateData.setBriefDescription("Updated Description");

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct1));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            productService.updateProduct(productId, updateData));
    }

    @Test
    @DisplayName("Should throw exception when updating product with blank HTS code")
    void updateProduct_ShouldThrowException_WhenHtsCodeIsBlank() {
        // Arrange
        Long productId = 1L;
        Product updateData = new Product();
        updateData.setHts8("   ");
        updateData.setBriefDescription("Updated Description");

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct1));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            productService.updateProduct(productId, updateData));
    }

    @Test
    @DisplayName("Should throw exception when updating product with null description")
    void updateProduct_ShouldThrowException_WhenDescriptionIsNull() {
        // Arrange
        Long productId = 1L;
        Product updateData = new Product();
        updateData.setHts8("12345678");
        updateData.setBriefDescription(null);

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct1));
        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct1));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            productService.updateProduct(productId, updateData));
    }

    @Test
    @DisplayName("Should throw exception when updating product with blank description")
    void updateProduct_ShouldThrowException_WhenDescriptionIsBlank() {
        // Arrange
        Long productId = 1L;
        Product updateData = new Product();
        updateData.setHts8("12345678");
        updateData.setBriefDescription("   ");

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct1));
        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct1));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            productService.updateProduct(productId, updateData));
    }

    @Test
    @DisplayName("Should throw exception when HTS code exists for different product")
    void updateProduct_ShouldThrowException_WhenHtsCodeExistsForDifferentProduct() {
        // Arrange
        Long productId = 1L;
        Product updateData = new Product();
        updateData.setHts8("87654321"); // Different HTS code
        updateData.setBriefDescription("Updated Description");

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct1));
        when(productRepository.findByHts8("87654321")).thenReturn(Optional.of(testProduct2));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> 
            productService.updateProduct(productId, updateData));
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).findByHts8("87654321");
    }

    @Test
    @DisplayName("Should allow updating product with same HTS code")
    void updateProduct_ShouldAllowSameHtsCode() {
        // Arrange
        Long productId = 1L;
        Product updateData = new Product();
        updateData.setHts8("12345678"); // Same HTS code
        updateData.setBriefDescription("Updated Description");

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct1));
        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(testProduct1));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return product;
        });

        // Act
        Product result = productService.updateProduct(productId, updateData);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Description", result.getBriefDescription());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).findByHts8("12345678");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // ===== DELETE PRODUCT ERROR HANDLING TESTS =====
    
    @Test
    @DisplayName("Should throw exception when deleting product with null ID")
    void deleteProduct_ShouldThrowException_WhenIdIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            productService.deleteProduct(null));
    }

    // ===== HELPER METHOD TESTS =====
    
    @Test
    @DisplayName("Should test nullIfBlank helper method with null input")
    void nullIfBlank_ShouldReturnNull_WhenInputIsNull() {
        // This tests the private nullIfBlank method indirectly through createProduct
        Product newProduct = new Product();
        newProduct.setHts8("11111111");
        newProduct.setBriefDescription("Test Description");
        newProduct.setQuantity1Code(null);
        newProduct.setQuantity2Code(null);
        newProduct.setWtoBindingCode(null);

        when(productRepository.findByHts8("11111111")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return product;
        });

        // Act
        Product result = productService.createProduct(newProduct);

        // Assert
        assertNotNull(result);
        assertNull(result.getQuantity1Code());
        assertNull(result.getQuantity2Code());
        assertNull(result.getWtoBindingCode());
    }

    @Test
    @DisplayName("Should test nullIfBlank helper method with blank input")
    void nullIfBlank_ShouldReturnNull_WhenInputIsBlank() {
        // This tests the private nullIfBlank method indirectly through createProduct
        Product newProduct = new Product();
        newProduct.setHts8("11111111");
        newProduct.setBriefDescription("Test Description");
        newProduct.setQuantity1Code("   ");
        newProduct.setQuantity2Code("   ");
        newProduct.setWtoBindingCode("   ");

        when(productRepository.findByHts8("11111111")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return product;
        });

        // Act
        Product result = productService.createProduct(newProduct);

        // Assert
        assertNotNull(result);
        assertNull(result.getQuantity1Code());
        assertNull(result.getQuantity2Code());
        assertNull(result.getWtoBindingCode());
    }

    @Test
    @DisplayName("Should test nullIfBlank helper method with valid input")
    void nullIfBlank_ShouldReturnTrimmedValue_WhenInputIsValid() {
        // This tests the private nullIfBlank method indirectly through createProduct
        Product newProduct = new Product();
        newProduct.setHts8("11111111");
        newProduct.setBriefDescription("Test Description");
        newProduct.setQuantity1Code("  KG  ");
        newProduct.setQuantity2Code("  LBS  ");
        newProduct.setWtoBindingCode("  A  ");

        when(productRepository.findByHts8("11111111")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return product;
        });

        // Act
        Product result = productService.createProduct(newProduct);

        // Assert
        assertNotNull(result);
        assertEquals("KG", result.getQuantity1Code());
        assertEquals("LBS", result.getQuantity2Code());
        assertEquals("A", result.getWtoBindingCode());
    }

    // Helper method to set Product ID using reflection
    private void setProductId(Product product, Long id) {
        try {
            Field idField = Product.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(product, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set Product ID", e);
        }
    }
}
