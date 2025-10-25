package csd.tariff.backend.unit;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import csd.tariff.backend.model.Product;
import csd.tariff.backend.repository.AgreementRateRepository;
import csd.tariff.backend.repository.ProductRepository;
import csd.tariff.backend.service.ProductServiceImpl;

/**
 * Additional ProductServiceImpl tests to improve coverage
 * Focuses on edge cases and scenarios that might not be fully covered
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Additional Coverage Tests")
class ProductServiceImplAdditionalCoverageTest {

    private ProductRepository productRepository;
    private AgreementRateRepository agreementRateRepository;
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productRepository = org.mockito.Mockito.mock(ProductRepository.class);
        agreementRateRepository = org.mockito.Mockito.mock(AgreementRateRepository.class);
        productService = new ProductServiceImpl();
        
        // Use reflection to inject dependencies
        try {
            java.lang.reflect.Field productRepoField = ProductServiceImpl.class.getDeclaredField("productRepository");
            productRepoField.setAccessible(true);
            productRepoField.set(productService, productRepository);
            
            java.lang.reflect.Field agreementRepoField = ProductServiceImpl.class.getDeclaredField("agreementRateRepository");
            agreementRepoField.setAccessible(true);
            agreementRepoField.set(productService, agreementRateRepository);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject dependencies", e);
        }
    }

    @Test
    @DisplayName("Should handle nullIfBlank method with various inputs")
    void nullIfBlank_ShouldHandleVariousInputs() throws Exception {
        // Use reflection to test the private nullIfBlank method
        java.lang.reflect.Method nullIfBlankMethod = ProductServiceImpl.class.getDeclaredMethod("nullIfBlank", String.class);
        nullIfBlankMethod.setAccessible(true);
        
        // Test null input
        String result1 = (String) nullIfBlankMethod.invoke(productService, (String) null);
        assertNull(result1);
        
        // Test empty string
        String result2 = (String) nullIfBlankMethod.invoke(productService, "");
        assertNull(result2);
        
        // Test whitespace only
        String result3 = (String) nullIfBlankMethod.invoke(productService, "   ");
        assertNull(result3);
        
        // Test tab and newline whitespace
        String result4 = (String) nullIfBlankMethod.invoke(productService, "\t\n\r ");
        assertNull(result4);
        
        // Test valid string
        String result5 = (String) nullIfBlankMethod.invoke(productService, "valid");
        assertEquals("valid", result5);
        
        // Test string with leading/trailing whitespace
        String result6 = (String) nullIfBlankMethod.invoke(productService, "  valid  ");
        assertEquals("valid", result6);
    }

    @Test
    @DisplayName("Should handle updateProduct with null ID")
    void updateProduct_ShouldThrowException_WhenIdIsNull() {
        // Arrange
        Product updateData = new Product();
        updateData.setHts8("12345678");
        updateData.setBriefDescription("Updated Description");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            productService.updateProduct(null, updateData));
        
        assertEquals("Product ID is required.", exception.getMessage());
        verify(productRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should handle updateProduct with null request")
    void updateProduct_ShouldThrowException_WhenRequestIsNull() {
        // Arrange
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setHts8("12345678");
        existingProduct.setBriefDescription("Existing Description");
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            productService.updateProduct(productId, null));
        
        assertEquals("Product payload is required.", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should handle updateProduct with blank HTS code")
    void updateProduct_ShouldThrowException_WhenHtsCodeIsBlank() {
        // Arrange
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setHts8("12345678");
        existingProduct.setBriefDescription("Existing Description");
        
        Product updateData = new Product();
        updateData.setHts8("   ");
        updateData.setBriefDescription("Updated Description");
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            productService.updateProduct(productId, updateData));
        
        assertEquals("HTS code is required.", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should handle updateProduct with blank description")
    void updateProduct_ShouldThrowException_WhenDescriptionIsBlank() {
        // Arrange
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setHts8("12345678");
        existingProduct.setBriefDescription("Existing Description");
        
        Product updateData = new Product();
        updateData.setHts8("12345678");
        updateData.setBriefDescription("   ");
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            productService.updateProduct(productId, updateData));
        
        assertEquals("Product description is required.", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should handle updateProduct with existing HTS code from different product")
    void updateProduct_ShouldThrowException_WhenHtsCodeExistsForDifferentProduct() {
        // Arrange
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setHts8("12345678");
        existingProduct.setBriefDescription("Existing Description");
        
        Product otherProduct = new Product();
        otherProduct.setHts8("12345678");
        otherProduct.setBriefDescription("Other Product");
        
        Product updateData = new Product();
        updateData.setHts8("12345678");
        updateData.setBriefDescription("Updated Description");
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(otherProduct));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> 
            productService.updateProduct(productId, updateData));
        
        assertEquals("A product with this HTS code already exists.", exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).findByHts8("12345678");
    }

    @Test
    @DisplayName("Should handle updateProduct with same HTS code for same product")
    void updateProduct_ShouldAllowSameHtsCode_ForSameProduct() {
        // Arrange
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setHts8("12345678");
        existingProduct.setBriefDescription("Existing Description");
        
        Product updateData = new Product();
        updateData.setHts8("12345678");
        updateData.setBriefDescription("Updated Description");
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        // Act
        Product result = productService.updateProduct(productId, updateData);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Description", result.getBriefDescription());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).findByHts8("12345678");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should handle deleteProduct with null ID")
    void deleteProduct_ShouldThrowException_WhenIdIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            productService.deleteProduct(null));
        
        assertEquals("Product ID is required.", exception.getMessage());
        verify(productRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should handle createProduct with existing HTS code case insensitive")
    void createProduct_ShouldThrowException_WhenHtsCodeExistsCaseInsensitive() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setHts8("abc12345");
        newProduct.setBriefDescription("Test Description");

        Product existingProduct = new Product();
        existingProduct.setHts8("ABC12345");
        existingProduct.setBriefDescription("Existing Product");

        when(productRepository.findByHts8("ABC12345")).thenReturn(Optional.of(existingProduct));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> 
            productService.createProduct(newProduct));
        
        assertEquals("A product with this HTS code already exists.", exception.getMessage());
        verify(productRepository, times(1)).findByHts8("ABC12345");
    }

    @Test
    @DisplayName("Should handle createProduct with WTO binding code normalization")
    void createProduct_ShouldNormalizeWtoBindingCode() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setHts8("11111111");
        newProduct.setBriefDescription("Test Description");
        newProduct.setWtoBindingCode("a");

        when(productRepository.findByHts8("11111111")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            return product;
        });

        // Act
        Product result = productService.createProduct(newProduct);

        // Assert
        assertNotNull(result);
        assertEquals("A", result.getWtoBindingCode());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should handle createProduct with null WTO binding code")
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
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should handle createProduct with blank WTO binding code")
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
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should handle updateProduct with WTO binding code normalization")
    void updateProduct_ShouldNormalizeWtoBindingCode() {
        // Arrange
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setHts8("12345678");
        existingProduct.setBriefDescription("Existing Description");
        
        Product updateData = new Product();
        updateData.setHts8("12345678");
        updateData.setBriefDescription("Updated Description");
        updateData.setWtoBindingCode("b");
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        // Act
        Product result = productService.updateProduct(productId, updateData);

        // Assert
        assertNotNull(result);
        assertEquals("B", result.getWtoBindingCode());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should handle updateProduct with null WTO binding code")
    void updateProduct_ShouldHandleNullWtoBindingCode() {
        // Arrange
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setHts8("12345678");
        existingProduct.setBriefDescription("Existing Description");
        
        Product updateData = new Product();
        updateData.setHts8("12345678");
        updateData.setBriefDescription("Updated Description");
        updateData.setWtoBindingCode(null);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        // Act
        Product result = productService.updateProduct(productId, updateData);

        // Assert
        assertNotNull(result);
        assertNull(result.getWtoBindingCode());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should handle updateProduct with blank WTO binding code")
    void updateProduct_ShouldHandleBlankWtoBindingCode() {
        // Arrange
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setHts8("12345678");
        existingProduct.setBriefDescription("Existing Description");
        
        Product updateData = new Product();
        updateData.setHts8("12345678");
        updateData.setBriefDescription("Updated Description");
        updateData.setWtoBindingCode("   ");
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.findByHts8("12345678")).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        // Act
        Product result = productService.updateProduct(productId, updateData);

        // Assert
        assertNotNull(result);
        assertNull(result.getWtoBindingCode());
        verify(productRepository, times(1)).save(any(Product.class));
    }
}
