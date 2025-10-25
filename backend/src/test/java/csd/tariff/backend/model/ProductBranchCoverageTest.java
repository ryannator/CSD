package csd.tariff.backend.model;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Product Model Branch Coverage Tests")
class ProductBranchCoverageTest {

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        setProductId(product1, 1L);
        product1.setHts8("12345678");
        product1.setBriefDescription("Test Product 1");
        product1.setQuantity1Code("KG");
        product1.setQuantity2Code("LBS");
        product1.setWtoBindingCode("A");
        setProductCreatedAt(product1, LocalDateTime.of(2024, 1, 1, 10, 0));
        setProductUpdatedAt(product1, LocalDateTime.of(2024, 1, 1, 10, 0));

        product2 = new Product();
        setProductId(product2, 2L);
        product2.setHts8("87654321");
        product2.setBriefDescription("Test Product 2");
        product2.setQuantity1Code("MT");
        product2.setQuantity2Code("TON");
        product2.setWtoBindingCode("B");
        setProductCreatedAt(product2, LocalDateTime.of(2024, 1, 2, 10, 0));
        setProductUpdatedAt(product2, LocalDateTime.of(2024, 1, 2, 10, 0));
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

    private void setProductCreatedAt(Product product, LocalDateTime createdAt) {
        try {
            Field createdAtField = Product.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(product, createdAt);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set Product createdAt", e);
        }
    }

    private void setProductUpdatedAt(Product product, LocalDateTime updatedAt) {
        try {
            Field updatedAtField = Product.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(product, updatedAt);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set Product updatedAt", e);
        }
    }

    @Nested
    @DisplayName("equals Method Branch Coverage")
    class EqualsBranchCoverage {

        @Test
        @DisplayName("Should return true when comparing same object")
        void equals_ShouldReturnTrueWhenComparingSameObject() {
            // Act & Assert
            assertTrue(product1.equals(product1));
        }

        @Test
        @DisplayName("Should return false when comparing with null")
        void equals_ShouldReturnFalseWhenComparingWithNull() {
            // Act & Assert
            assertFalse(product1.equals(null));
        }

        @Test
        @DisplayName("Should return false when comparing with different class")
        void equals_ShouldReturnFalseWhenComparingWithDifferentClass() {
            // Act & Assert
            assertFalse(product1.equals("not a product"));
        }

        @Test
        @DisplayName("Should return true when comparing products with same ID")
        void equals_ShouldReturnTrueWhenComparingProductsWithSameId() {
            // Arrange
            Product productWithSameId = new Product();
            setProductId(productWithSameId, 1L);
            productWithSameId.setHts8("99999999");
            productWithSameId.setBriefDescription("Different Product");

            // Act & Assert
            assertTrue(product1.equals(productWithSameId));
        }

        @Test
        @DisplayName("Should return false when comparing products with different IDs")
        void equals_ShouldReturnFalseWhenComparingProductsWithDifferentIds() {
            // Act & Assert
            assertFalse(product1.equals(product2));
        }

        @Test
        @DisplayName("Should return false when comparing product with null ID")
        void equals_ShouldReturnFalseWhenComparingProductWithNullId() {
            // Arrange
            Product productWithNullId = new Product();
            setProductId(productWithNullId, null);
            productWithNullId.setHts8("12345678");

            // Act & Assert
            assertFalse(product1.equals(productWithNullId));
        }

        @Test
        @DisplayName("Should return false when comparing null ID with product")
        void equals_ShouldReturnFalseWhenComparingNullIdWithProduct() {
            // Arrange
            Product productWithNullId = new Product();
            setProductId(productWithNullId, null);
            productWithNullId.setHts8("12345678");

            // Act & Assert
            assertFalse(productWithNullId.equals(product1));
        }

        @Test
        @DisplayName("Should return true when comparing products with both null IDs")
        void equals_ShouldReturnTrueWhenComparingProductsWithBothNullIds() {
            // Arrange
            Product product1WithNullId = new Product();
            setProductId(product1WithNullId, null);
            product1WithNullId.setHts8("12345678");

            Product product2WithNullId = new Product();
            setProductId(product2WithNullId, null);
            product2WithNullId.setHts8("87654321");

            // Act & Assert
            assertTrue(product1WithNullId.equals(product2WithNullId));
        }
    }

    @Nested
    @DisplayName("hashCode Method Branch Coverage")
    class HashCodeBranchCoverage {

        @Test
        @DisplayName("Should return same hashCode for products with same ID")
        void hashCode_ShouldReturnSameHashCodeForProductsWithSameId() {
            // Arrange
            Product productWithSameId = new Product();
            setProductId(productWithSameId, 1L);
            productWithSameId.setHts8("99999999");

            // Act
            int hashCode1 = product1.hashCode();
            int hashCode2 = productWithSameId.hashCode();

            // Assert
            assertEquals(hashCode1, hashCode2);
        }

        @Test
        @DisplayName("Should return different hashCode for products with different IDs")
        void hashCode_ShouldReturnDifferentHashCodeForProductsWithDifferentIds() {
            // Act
            int hashCode1 = product1.hashCode();
            int hashCode2 = product2.hashCode();

            // Assert
            assertNotEquals(hashCode1, hashCode2);
        }

        @Test
        @DisplayName("Should return same hashCode for products with null IDs")
        void hashCode_ShouldReturnSameHashCodeForProductsWithNullIds() {
            // Arrange
            Product product1WithNullId = new Product();
            setProductId(product1WithNullId, null);

            Product product2WithNullId = new Product();
            setProductId(product2WithNullId, null);

            // Act
            int hashCode1 = product1WithNullId.hashCode();
            int hashCode2 = product2WithNullId.hashCode();

            // Assert
            assertEquals(hashCode1, hashCode2);
        }
    }

    @Nested
    @DisplayName("toString Method Branch Coverage")
    class ToStringBranchCoverage {

        @Test
        @DisplayName("Should include all fields in toString")
        void toString_ShouldIncludeAllFieldsInToString() {
            // Act
            String toString = product1.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("Product"));
            assertTrue(toString.contains("id=1"));
            assertTrue(toString.contains("hts8=12345678"));
            assertTrue(toString.contains("briefDescription=Test Product 1"));
            assertTrue(toString.contains("quantity1Code=KG"));
            assertTrue(toString.contains("quantity2Code=LBS"));
            assertTrue(toString.contains("wtoBindingCode=A"));
        }

        @Test
        @DisplayName("Should handle null fields in toString")
        void toString_ShouldHandleNullFieldsInToString() {
            // Arrange
            Product productWithNulls = new Product();
            setProductId(productWithNulls, 1L);
            productWithNulls.setHts8(null);
            productWithNulls.setBriefDescription(null);
            productWithNulls.setQuantity1Code(null);
            productWithNulls.setQuantity2Code(null);
            productWithNulls.setWtoBindingCode(null);

            // Act
            String toString = productWithNulls.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("Product"));
            assertTrue(toString.contains("id=1"));
            assertTrue(toString.contains("hts8=null"));
            assertTrue(toString.contains("briefDescription=null"));
            assertTrue(toString.contains("quantity1Code=null"));
            assertTrue(toString.contains("quantity2Code=null"));
            assertTrue(toString.contains("wtoBindingCode=null"));
        }
    }

    @Nested
    @DisplayName("Setters and Getters Branch Coverage")
    class SettersAndGettersBranchCoverage {

        @Test
        @DisplayName("Should handle null values in setters")
        void setters_ShouldHandleNullValuesInSetters() {
            // Arrange
            Product product = new Product();

            // Act
            product.setHts8(null);
            product.setBriefDescription(null);
            product.setQuantity1Code(null);
            product.setQuantity2Code(null);
            product.setWtoBindingCode(null);

            // Assert
            assertNull(product.getHts8());
            assertNull(product.getBriefDescription());
            assertNull(product.getQuantity1Code());
            assertNull(product.getQuantity2Code());
            assertNull(product.getWtoBindingCode());
        }

        @Test
        @DisplayName("Should handle empty strings in setters")
        void setters_ShouldHandleEmptyStringsInSetters() {
            // Arrange
            Product product = new Product();

            // Act
            product.setHts8("");
            product.setBriefDescription("");
            product.setQuantity1Code("");
            product.setQuantity2Code("");
            product.setWtoBindingCode("");

            // Assert
            assertEquals("", product.getHts8());
            assertEquals("", product.getBriefDescription());
            assertEquals("", product.getQuantity1Code());
            assertEquals("", product.getQuantity2Code());
            assertEquals("", product.getWtoBindingCode());
        }

        @Test
        @DisplayName("Should handle whitespace strings in setters")
        void setters_ShouldHandleWhitespaceStringsInSetters() {
            // Arrange
            Product product = new Product();

            // Act
            product.setHts8("   ");
            product.setBriefDescription("   ");
            product.setQuantity1Code("   ");
            product.setQuantity2Code("   ");
            product.setWtoBindingCode("   ");

            // Assert
            assertEquals("   ", product.getHts8());
            assertEquals("   ", product.getBriefDescription());
            assertEquals("   ", product.getQuantity1Code());
            assertEquals("   ", product.getQuantity2Code());
            assertEquals("   ", product.getWtoBindingCode());
        }

        @Test
        @DisplayName("Should handle very long strings in setters")
        void setters_ShouldHandleVeryLongStringsInSetters() {
            // Arrange
            Product product = new Product();
            String longString = "A".repeat(1000);

            // Act
            product.setBriefDescription(longString);

            // Assert
            assertEquals(longString, product.getBriefDescription());
        }

        @Test
        @DisplayName("Should handle special characters in setters")
        void setters_ShouldHandleSpecialCharactersInSetters() {
            // Arrange
            Product product = new Product();
            String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?";

            // Act
            product.setHts8(specialChars);
            product.setBriefDescription(specialChars);
            product.setQuantity1Code(specialChars);
            product.setQuantity2Code(specialChars);
            product.setWtoBindingCode(specialChars);

            // Assert
            assertEquals(specialChars, product.getHts8());
            assertEquals(specialChars, product.getBriefDescription());
            assertEquals(specialChars, product.getQuantity1Code());
            assertEquals(specialChars, product.getQuantity2Code());
            assertEquals(specialChars, product.getWtoBindingCode());
        }

        @Test
        @DisplayName("Should handle unicode characters in setters")
        void setters_ShouldHandleUnicodeCharactersInSetters() {
            // Arrange
            Product product = new Product();
            String unicodeString = "测试产品 🚀 αβγδε";

            // Act
            product.setBriefDescription(unicodeString);

            // Assert
            assertEquals(unicodeString, product.getBriefDescription());
        }
    }

    @Nested
    @DisplayName("Edge Cases Branch Coverage")
    class EdgeCasesBranchCoverage {

        @Test
        @DisplayName("Should handle product with minimum valid data")
        void shouldHandleProductWithMinimumValidData() {
            // Arrange
            Product minimalProduct = new Product();
            setProductId(minimalProduct, 1L);
            minimalProduct.setHts8("00000000");
            minimalProduct.setBriefDescription("Minimal");

            // Act & Assert
            assertNotNull(minimalProduct);
            assertEquals(1L, minimalProduct.getId());
            assertEquals("00000000", minimalProduct.getHts8());
            assertEquals("Minimal", minimalProduct.getBriefDescription());
        }

        @Test
        @DisplayName("Should handle product with maximum valid data")
        void shouldHandleProductWithMaximumValidData() {
            // Arrange
            Product maxProduct = new Product();
            setProductId(maxProduct, Long.MAX_VALUE);
            maxProduct.setHts8("99999999");
            maxProduct.setBriefDescription("A".repeat(255));
            maxProduct.setQuantity1Code("A".repeat(10));
            maxProduct.setQuantity2Code("A".repeat(10));
            maxProduct.setWtoBindingCode("Z");

            // Act & Assert
            assertNotNull(maxProduct);
            assertEquals(Long.MAX_VALUE, maxProduct.getId());
            assertEquals("99999999", maxProduct.getHts8());
            assertEquals("A".repeat(255), maxProduct.getBriefDescription());
            assertEquals("A".repeat(10), maxProduct.getQuantity1Code());
            assertEquals("A".repeat(10), maxProduct.getQuantity2Code());
            assertEquals("Z", maxProduct.getWtoBindingCode());
        }

        @Test
        @DisplayName("Should handle product with boundary ID values")
        void shouldHandleProductWithBoundaryIdValues() {
            // Arrange
            Product productWithZeroId = new Product();
            setProductId(productWithZeroId, 0L);

            Product productWithNegativeId = new Product();
            setProductId(productWithNegativeId, -1L);

            // Act & Assert
            assertEquals(0L, productWithZeroId.getId());
            assertEquals(-1L, productWithNegativeId.getId());
        }
    }
}
