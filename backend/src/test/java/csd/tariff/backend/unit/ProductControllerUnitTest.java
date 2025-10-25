package csd.tariff.backend.unit;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import csd.tariff.backend.controller.ProductController;
import csd.tariff.backend.model.Product;
import csd.tariff.backend.service.ProductService;

/**
 * Comprehensive unit tests for ProductController
 * Tests all CRUD operations, error handling, and edge cases
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductController Unit Tests")
class ProductControllerUnitTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Setup test product
        testProduct = new Product();
        setId(testProduct, 1L);
        testProduct.setHts8("12345678");
        testProduct.setBriefDescription("Test Product Description");
        testProduct.setQuantity1Code("KG");
        testProduct.setQuantity2Code("L");
        testProduct.setWtoBindingCode("BIND");
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

    // ===== GET ALL PRODUCTS TESTS =====

    @Test
    @DisplayName("Should get all products successfully")
    void getAllProducts_ShouldReturnAllProducts() throws Exception {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].hts8").value("12345678"))
                .andExpect(jsonPath("$[0].briefDescription").value("Test Product Description"))
                .andExpect(jsonPath("$[0].quantity1Code").value("KG"))
                .andExpect(jsonPath("$[0].quantity2Code").value("L"))
                .andExpect(jsonPath("$[0].wtoBindingCode").value("BIND"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void getAllProducts_ShouldReturnEmptyList_WhenNoProducts() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("Should handle multiple products")
    void getAllProducts_ShouldHandleMultipleProducts() throws Exception {
        // Arrange
        Product product2 = new Product();
        setId(product2, 2L);
        product2.setHts8("87654321");
        product2.setBriefDescription("Second Product");
        product2.setQuantity1Code("MT");
        product2.setQuantity2Code("M3");

        List<Product> products = Arrays.asList(testProduct, product2);
        when(productService.getAllProducts()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].hts8").value("12345678"))
                .andExpect(jsonPath("$[1].hts8").value("87654321"));

        verify(productService, times(1)).getAllProducts();
    }

    // ===== CREATE PRODUCT TESTS =====

    @Test
    @DisplayName("Should create product successfully")
    void createProduct_ShouldCreateSuccessfully() throws Exception {
        // Arrange
        when(productService.createProduct(any(Product.class))).thenReturn(testProduct);

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.hts8").value("12345678"))
                .andExpect(jsonPath("$.briefDescription").value("Test Product Description"))
                .andExpect(jsonPath("$.quantity1Code").value("KG"))
                .andExpect(jsonPath("$.quantity2Code").value("L"))
                .andExpect(jsonPath("$.wtoBindingCode").value("BIND"));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("Should return bad request when product creation fails with IllegalArgumentException")
    void createProduct_ShouldReturnBadRequest_WhenIllegalArgumentException() throws Exception {
        // Arrange
        when(productService.createProduct(any(Product.class)))
            .thenThrow(new IllegalArgumentException("HTS code already exists"));

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("HTS code already exists"));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("Should return conflict when product creation fails with IllegalStateException")
    void createProduct_ShouldReturnConflict_WhenIllegalStateException() throws Exception {
        // Arrange
        when(productService.createProduct(any(Product.class)))
            .thenThrow(new IllegalStateException("Product already exists"));

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Product already exists"));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("Should return bad request for invalid product data")
    void createProduct_ShouldReturnBadRequest_ForInvalidData() throws Exception {
        // Arrange - Create invalid product with null values
        Product invalidProduct = new Product();
        invalidProduct.setHts8(null);
        invalidProduct.setBriefDescription(null);

        // Mock service to throw exception for invalid data
        when(productService.createProduct(any(Product.class)))
            .thenThrow(new IllegalArgumentException("Invalid product data"));

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid product data"));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("Should create product with minimal data")
    void createProduct_ShouldCreateWithMinimalData() throws Exception {
        // Arrange
        Product minimalProduct = new Product();
        minimalProduct.setHts8("11111111");
        minimalProduct.setBriefDescription("Minimal Product");

        Product createdProduct = new Product();
        setId(createdProduct, 1L);
        createdProduct.setHts8("11111111");
        createdProduct.setBriefDescription("Minimal Product");

        when(productService.createProduct(any(Product.class))).thenReturn(createdProduct);

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minimalProduct)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.hts8").value("11111111"))
                .andExpect(jsonPath("$.briefDescription").value("Minimal Product"));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    // ===== UPDATE PRODUCT TESTS =====

    @Test
    @DisplayName("Should update product successfully")
    void updateProduct_ShouldUpdateSuccessfully() throws Exception {
        // Arrange
        Product updatedProduct = new Product();
        setId(updatedProduct, 1L);
        updatedProduct.setHts8("12345678");
        updatedProduct.setBriefDescription("Updated Product Description");
        updatedProduct.setQuantity1Code("MT");
        updatedProduct.setQuantity2Code("M3");
        updatedProduct.setWtoBindingCode("UPDATED");

        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        // Act & Assert
        mockMvc.perform(put("/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.hts8").value("12345678"))
                .andExpect(jsonPath("$.briefDescription").value("Updated Product Description"))
                .andExpect(jsonPath("$.quantity1Code").value("MT"))
                .andExpect(jsonPath("$.quantity2Code").value("M3"))
                .andExpect(jsonPath("$.wtoBindingCode").value("UPDATED"));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    @DisplayName("Should return bad request when product update fails with IllegalArgumentException")
    void updateProduct_ShouldReturnBadRequest_WhenIllegalArgumentException() throws Exception {
        // Arrange
        when(productService.updateProduct(eq(1L), any(Product.class)))
            .thenThrow(new IllegalArgumentException("Product not found"));

        // Act & Assert
        mockMvc.perform(put("/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Product not found"));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    @DisplayName("Should return conflict when product update fails with IllegalStateException")
    void updateProduct_ShouldReturnConflict_WhenIllegalStateException() throws Exception {
        // Arrange
        when(productService.updateProduct(eq(1L), any(Product.class)))
            .thenThrow(new IllegalStateException("HTS code conflict"));

        // Act & Assert
        mockMvc.perform(put("/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isConflict())
                .andExpect(content().string("HTS code conflict"));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    @DisplayName("Should return bad request for invalid product update data")
    void updateProduct_ShouldReturnBadRequest_ForInvalidData() throws Exception {
        // Arrange - Create invalid product with null values
        Product invalidProduct = new Product();
        invalidProduct.setHts8(null);
        invalidProduct.setBriefDescription(null);

        // Mock service to throw exception for invalid data
        when(productService.updateProduct(eq(1L), any(Product.class)))
            .thenThrow(new IllegalArgumentException("Invalid product data"));

        // Act & Assert
        mockMvc.perform(put("/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid product data"));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    // ===== DELETE PRODUCT TESTS =====

    @Test
    @DisplayName("Should delete product successfully")
    void deleteProduct_ShouldDeleteSuccessfully() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct(1L);

        // Act & Assert
        mockMvc.perform(delete("/products/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("Should return bad request when product deletion fails")
    void deleteProduct_ShouldReturnBadRequest_WhenDeletionFails() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Product not found"))
            .when(productService).deleteProduct(999L);

        // Act & Assert
        mockMvc.perform(delete("/products/{id}", 999L))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Product not found"));

        verify(productService, times(1)).deleteProduct(999L);
    }

    // ===== EDGE CASES AND ERROR HANDLING TESTS =====

    @Test
    @DisplayName("Should handle null request body gracefully")
    void createProduct_ShouldHandleNullRequestBody() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid JSON format")
    void createProduct_ShouldHandleInvalidJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle negative ID in path variable")
    void updateProduct_ShouldHandleNegativeId() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/products/{id}", -1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk()); // Controller should still process the request
    }

    @Test
    @DisplayName("Should handle zero ID in path variable")
    void updateProduct_ShouldHandleZeroId() throws Exception {
        // Arrange
        when(productService.updateProduct(eq(0L), any(Product.class)))
            .thenThrow(new IllegalArgumentException("Invalid ID"));

        // Act & Assert
        mockMvc.perform(put("/products/{id}", 0L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid ID"));

        verify(productService, times(1)).updateProduct(eq(0L), any(Product.class));
    }

    @Test
    @DisplayName("Should handle product with all optional fields null")
    void createProduct_ShouldHandleAllOptionalFieldsNull() throws Exception {
        // Arrange
        Product productWithNulls = new Product();
        productWithNulls.setHts8("22222222");
        productWithNulls.setBriefDescription("Product with nulls");
        productWithNulls.setQuantity1Code(null);
        productWithNulls.setQuantity2Code(null);
        productWithNulls.setWtoBindingCode(null);

        Product createdProduct = new Product();
        setId(createdProduct, 1L);
        createdProduct.setHts8("22222222");
        createdProduct.setBriefDescription("Product with nulls");

        when(productService.createProduct(any(Product.class))).thenReturn(createdProduct);

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productWithNulls)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.hts8").value("22222222"))
                .andExpect(jsonPath("$.briefDescription").value("Product with nulls"));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("Should handle very long HTS code")
    void createProduct_ShouldHandleLongHtsCode() throws Exception {
        // Arrange
        Product productWithLongHts = new Product();
        productWithLongHts.setHts8("12345678901234567890");
        productWithLongHts.setBriefDescription("Product with long HTS");

        when(productService.createProduct(any(Product.class)))
            .thenThrow(new IllegalArgumentException("HTS code too long"));

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productWithLongHts)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("HTS code too long"));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("Should handle empty string values")
    void createProduct_ShouldHandleEmptyStringValues() throws Exception {
        // Arrange
        Product productWithEmptyStrings = new Product();
        productWithEmptyStrings.setHts8("");
        productWithEmptyStrings.setBriefDescription("");
        productWithEmptyStrings.setQuantity1Code("");
        productWithEmptyStrings.setQuantity2Code("");
        productWithEmptyStrings.setWtoBindingCode("");

        when(productService.createProduct(any(Product.class)))
            .thenThrow(new IllegalArgumentException("Empty values not allowed"));

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productWithEmptyStrings)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Empty values not allowed"));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("Should handle service exception during product deletion")
    void deleteProduct_ShouldHandleServiceException() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Product not found with ID: 999"))
                .when(productService).deleteProduct(999L);

        // Act & Assert
        mockMvc.perform(delete("/products/999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Product not found with ID: 999"));

        verify(productService, times(1)).deleteProduct(999L);
    }

    @Test
    @DisplayName("Should handle null product in create request")
    void createProduct_ShouldHandleNullProduct() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON in create request")
    void createProduct_ShouldHandleMalformedJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle empty product list")
    void getAllProducts_ShouldHandleEmptyList() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("Should handle concurrent modification exception")
    void updateProduct_ShouldHandleConcurrentModification() throws Exception {
        // Arrange
        doThrow(new IllegalStateException("Product was modified by another user"))
                .when(productService).updateProduct(eq(1L), any(Product.class));

        // Act & Assert
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Product was modified by another user"));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    @DisplayName("Should handle special characters in HTS code")
    void createProduct_ShouldHandleSpecialCharactersInHtsCode() throws Exception {
        // Arrange
        Product productWithSpecialChars = new Product();
        productWithSpecialChars.setHts8("ABC12345");
        productWithSpecialChars.setBriefDescription("Product with special HTS code");

        Product createdProduct = new Product();
        setId(createdProduct, 3L);
        createdProduct.setHts8("ABC12345");
        createdProduct.setBriefDescription("Product with special HTS code");

        when(productService.createProduct(any(Product.class))).thenReturn(createdProduct);

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productWithSpecialChars)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.hts8").value("ABC12345"))
                .andExpect(jsonPath("$.briefDescription").value("Product with special HTS code"));

        verify(productService, times(1)).createProduct(any(Product.class));
    }
}
