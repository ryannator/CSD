package csd.tariff.backend.model;

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

@DisplayName("Country Model Branch Coverage Tests")
class CountryBranchCoverageTest {

    private Country country1;
    private Country country2;

    @BeforeEach
    void setUp() {
        country1 = new Country();
        country1.setCountryCode("US");
        country1.setCountryName("United States");
        country1.setCountryNameShort("USA");
        country1.setContinent("North America");
        country1.setRegion("North America");
        country1.setCurrency("USD");

        country2 = new Country();
        country2.setCountryCode("CA");
        country2.setCountryName("Canada");
        country2.setCountryNameShort("CAN");
        country2.setContinent("North America");
        country2.setRegion("North America");
        country2.setCurrency("CAD");
    }

    @Nested
    @DisplayName("equals Method Branch Coverage")
    class EqualsBranchCoverage {

        @Test
        @DisplayName("Should return true when comparing same object")
        void equals_ShouldReturnTrueWhenComparingSameObject() {
            // Act & Assert
            assertTrue(country1.equals(country1));
        }

        @Test
        @DisplayName("Should return false when comparing with null")
        void equals_ShouldReturnFalseWhenComparingWithNull() {
            // Act & Assert
            assertFalse(country1.equals(null));
        }

        @Test
        @DisplayName("Should return false when comparing with different class")
        void equals_ShouldReturnFalseWhenComparingWithDifferentClass() {
            // Act & Assert
            assertFalse(country1.equals("not a country"));
        }

        @Test
        @DisplayName("Should return false when comparing different countries")
        void equals_ShouldReturnFalseWhenComparingDifferentCountries() {
            // Act & Assert
            assertFalse(country1.equals(country2));
        }
    }

    @Nested
    @DisplayName("hashCode Method Branch Coverage")
    class HashCodeBranchCoverage {

        @Test
        @DisplayName("Should return different hashCode for different countries")
        void hashCode_ShouldReturnDifferentHashCodeForDifferentCountries() {
            // Act
            int hashCode1 = country1.hashCode();
            int hashCode2 = country2.hashCode();

            // Assert
            assertNotEquals(hashCode1, hashCode2);
        }

        @Test
        @DisplayName("Should return same hashCode for same country")
        void hashCode_ShouldReturnSameHashCodeForSameCountry() {
            // Act
            int hashCode1 = country1.hashCode();
            int hashCode2 = country1.hashCode();

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
            String toString = country1.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("Country"));
            assertTrue(toString.contains("countryCode=US"));
            assertTrue(toString.contains("countryName=United States"));
            assertTrue(toString.contains("countryNameShort=USA"));
            assertTrue(toString.contains("continent=North America"));
            assertTrue(toString.contains("region=North America"));
            assertTrue(toString.contains("currency=USD"));
        }

        @Test
        @DisplayName("Should handle null fields in toString")
        void toString_ShouldHandleNullFieldsInToString() {
            // Arrange
            Country countryWithNulls = new Country();
            countryWithNulls.setCountryCode(null);
            countryWithNulls.setCountryName(null);
            countryWithNulls.setCountryNameShort(null);
            countryWithNulls.setContinent(null);
            countryWithNulls.setRegion(null);
            countryWithNulls.setCurrency(null);

            // Act
            String toString = countryWithNulls.toString();

            // Assert
            assertNotNull(toString);
            assertTrue(toString.contains("Country"));
            assertTrue(toString.contains("countryCode=null"));
            assertTrue(toString.contains("countryName=null"));
            assertTrue(toString.contains("countryNameShort=null"));
            assertTrue(toString.contains("continent=null"));
            assertTrue(toString.contains("region=null"));
            assertTrue(toString.contains("currency=null"));
        }
    }

    @Nested
    @DisplayName("Setters and Getters Branch Coverage")
    class SettersAndGettersBranchCoverage {

        @Test
        @DisplayName("Should handle null values in setters")
        void setters_ShouldHandleNullValuesInSetters() {
            // Arrange
            Country country = new Country();

            // Act
            country.setCountryCode(null);
            country.setCountryName(null);
            country.setCountryNameShort(null);
            country.setContinent(null);
            country.setRegion(null);
            country.setCurrency(null);

            // Assert
            assertNull(country.getCountryCode());
            assertNull(country.getCountryName());
            assertNull(country.getCountryNameShort());
            assertNull(country.getContinent());
            assertNull(country.getRegion());
            assertNull(country.getCurrency());
        }

        @Test
        @DisplayName("Should handle empty strings in setters")
        void setters_ShouldHandleEmptyStringsInSetters() {
            // Arrange
            Country country = new Country();

            // Act
            country.setCountryCode("");
            country.setCountryName("");
            country.setCountryNameShort("");
            country.setContinent("");
            country.setRegion("");
            country.setCurrency("");

            // Assert
            assertEquals("", country.getCountryCode());
            assertEquals("", country.getCountryName());
            assertEquals("", country.getCountryNameShort());
            assertEquals("", country.getContinent());
            assertEquals("", country.getRegion());
            assertEquals("", country.getCurrency());
        }

        @Test
        @DisplayName("Should handle whitespace strings in setters")
        void setters_ShouldHandleWhitespaceStringsInSetters() {
            // Arrange
            Country country = new Country();

            // Act
            country.setCountryCode("   ");
            country.setCountryName("   ");
            country.setCountryNameShort("   ");
            country.setContinent("   ");
            country.setRegion("   ");
            country.setCurrency("   ");

            // Assert
            assertEquals("   ", country.getCountryCode());
            assertEquals("   ", country.getCountryName());
            assertEquals("   ", country.getCountryNameShort());
            assertEquals("   ", country.getContinent());
            assertEquals("   ", country.getRegion());
            assertEquals("   ", country.getCurrency());
        }

        @Test
        @DisplayName("Should handle very long strings in setters")
        void setters_ShouldHandleVeryLongStringsInSetters() {
            // Arrange
            Country country = new Country();
            String longString = "A".repeat(1000);

            // Act
            country.setCountryName(longString);
            country.setContinent(longString);
            country.setRegion(longString);

            // Assert
            assertEquals(longString, country.getCountryName());
            assertEquals(longString, country.getContinent());
            assertEquals(longString, country.getRegion());
        }

        @Test
        @DisplayName("Should handle special characters in setters")
        void setters_ShouldHandleSpecialCharactersInSetters() {
            // Arrange
            Country country = new Country();
            String specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?";

            // Act
            country.setCountryCode(specialChars);
            country.setCountryName(specialChars);
            country.setCountryNameShort(specialChars);
            country.setContinent(specialChars);
            country.setRegion(specialChars);
            country.setCurrency(specialChars);

            // Assert
            assertEquals(specialChars, country.getCountryCode());
            assertEquals(specialChars, country.getCountryName());
            assertEquals(specialChars, country.getCountryNameShort());
            assertEquals(specialChars, country.getContinent());
            assertEquals(specialChars, country.getRegion());
            assertEquals(specialChars, country.getCurrency());
        }

        @Test
        @DisplayName("Should handle unicode characters in setters")
        void setters_ShouldHandleUnicodeCharactersInSetters() {
            // Arrange
            Country country = new Country();
            String unicodeString = "测试国家 🏴󠁧󠁢󠁥󠁮󠁧󠁿 αβγδε";

            // Act
            country.setCountryName(unicodeString);
            country.setCountryNameShort(unicodeString);

            // Assert
            assertEquals(unicodeString, country.getCountryName());
            assertEquals(unicodeString, country.getCountryNameShort());
        }

        @Test
        @DisplayName("Should handle ISO country codes")
        void setters_ShouldHandleIsoCountryCodes() {
            // Arrange
            Country country = new Country();
            String[] isoCodes = {"US", "CA", "MX", "GB", "FR", "DE", "JP", "CN", "IN", "BR"};

            // Act & Assert
            for (String code : isoCodes) {
                country.setCountryCode(code);
                assertEquals(code, country.getCountryCode());
            }
        }

        @Test
        @DisplayName("Should handle currency codes")
        void setters_ShouldHandleCurrencyCodes() {
            // Arrange
            Country country = new Country();
            String[] currencies = {"USD", "CAD", "EUR", "GBP", "JPY", "CNY", "INR", "BRL", "MXN"};

            // Act & Assert
            for (String currency : currencies) {
                country.setCurrency(currency);
                assertEquals(currency, country.getCurrency());
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases Branch Coverage")
    class EdgeCasesBranchCoverage {

        @Test
        @DisplayName("Should handle country with minimum valid data")
        void shouldHandleCountryWithMinimumValidData() {
            // Arrange
            Country minimalCountry = new Country();
            minimalCountry.setCountryCode("XX");
            minimalCountry.setCountryName("Test Country");

            // Act & Assert
            assertNotNull(minimalCountry);
            assertEquals("XX", minimalCountry.getCountryCode());
            assertEquals("Test Country", minimalCountry.getCountryName());
        }

        @Test
        @DisplayName("Should handle country with maximum valid data")
        void shouldHandleCountryWithMaximumValidData() {
            // Arrange
            Country maxCountry = new Country();
            maxCountry.setCountryCode("ZZ");
            maxCountry.setCountryName("A".repeat(100));
            maxCountry.setCountryNameShort("A".repeat(50));
            maxCountry.setContinent("A".repeat(50));
            maxCountry.setRegion("A".repeat(50));
            maxCountry.setCurrency("ZZZ");

            // Act & Assert
            assertNotNull(maxCountry);
            assertEquals("ZZ", maxCountry.getCountryCode());
            assertEquals("A".repeat(100), maxCountry.getCountryName());
            assertEquals("A".repeat(50), maxCountry.getCountryNameShort());
            assertEquals("A".repeat(50), maxCountry.getContinent());
            assertEquals("A".repeat(50), maxCountry.getRegion());
            assertEquals("ZZZ", maxCountry.getCurrency());
        }

        @Test
        @DisplayName("Should handle country with mixed case data")
        void shouldHandleCountryWithMixedCaseData() {
            // Arrange
            Country country = new Country();
            country.setCountryCode("us");
            country.setCountryName("united states");
            country.setCountryNameShort("usa");
            country.setContinent("north america");
            country.setRegion("NORTH AMERICA");
            country.setCurrency("usd");

            // Act & Assert
            assertEquals("us", country.getCountryCode());
            assertEquals("united states", country.getCountryName());
            assertEquals("usa", country.getCountryNameShort());
            assertEquals("north america", country.getContinent());
            assertEquals("NORTH AMERICA", country.getRegion());
            assertEquals("usd", country.getCurrency());
        }

        @Test
        @DisplayName("Should handle country with numeric strings")
        void shouldHandleCountryWithNumericStrings() {
            // Arrange
            Country country = new Country();
            country.setCountryCode("123");
            country.setCountryName("123456");
            country.setCountryNameShort("123");
            country.setContinent("123");
            country.setRegion("123");
            country.setCurrency("123");

            // Act & Assert
            assertEquals("123", country.getCountryCode());
            assertEquals("123456", country.getCountryName());
            assertEquals("123", country.getCountryNameShort());
            assertEquals("123", country.getContinent());
            assertEquals("123", country.getRegion());
            assertEquals("123", country.getCurrency());
        }
    }
}
