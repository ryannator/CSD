package csd.tariff.backend.unit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import csd.tariff.backend.model.AgreementRate;
import csd.tariff.backend.model.Country;
import csd.tariff.backend.model.MfnTariffRate;
import csd.tariff.backend.model.Product;
import csd.tariff.backend.model.TariffCalculation;
import csd.tariff.backend.model.TradeAgreement;
import csd.tariff.backend.model.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("Model Tests")
class ModelTests {

    // ===== PRODUCT MODEL TESTS =====
    
    @Test
    @DisplayName("Product - Should create and access all fields correctly")
    void product_ShouldCreateAndAccessAllFields() {
        // Arrange
        Product product = new Product();
        String hts8 = "12345678";
        String briefDescription = "Test Product";
        String quantity1Code = "PCE";
        String quantity2Code = "KG";
        String wtoBindingCode = "WTO123";

        // Act
        product.setHts8(hts8);
        product.setBriefDescription(briefDescription);
        product.setQuantity1Code(quantity1Code);
        product.setQuantity2Code(quantity2Code);
        product.setWtoBindingCode(wtoBindingCode);

        // Assert
        assertEquals(hts8, product.getHts8());
        assertEquals(briefDescription, product.getBriefDescription());
        assertEquals(quantity1Code, product.getQuantity1Code());
        assertEquals(quantity2Code, product.getQuantity2Code());
        assertEquals(wtoBindingCode, product.getWtoBindingCode());
    }

    @Test
    @DisplayName("Product - Should handle null values correctly")
    void product_ShouldHandleNullValues() {
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
    @DisplayName("Product - Should test toString method")
    void product_ShouldTestToString() {
        // Arrange
        Product product = new Product();
        product.setHts8("12345678");
        product.setBriefDescription("Test Product");

        // Act
        String toString = product.toString();

        // Assert
        assertNotNull(toString);
        // toString() returns class name + hash code by default
        assertTrue(toString.contains("Product"));
    }

    // ===== USER MODEL TESTS =====
    
    @Test
    @DisplayName("User - Should create and access all fields correctly")
    void user_ShouldCreateAndAccessAllFields() {
        // Arrange
        User user = new User();
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        User.Role role = User.Role.USER;

        // Act
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        // Assert
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(role, user.getRole());
    }

    @Test
    @DisplayName("User - Should handle null values correctly")
    void user_ShouldHandleNullValues() {
        // Arrange
        User user = new User();

        // Act
        user.setUsername(null);
        user.setEmail(null);
        user.setPassword(null);
        user.setRole(null);

        // Assert
        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getRole());
    }

    @Test
    @DisplayName("User - Should test toString method")
    void user_ShouldTestToString() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        // Act
        String toString = user.toString();

        // Assert
        assertNotNull(toString);
        // toString() returns class name + hash code by default
        assertTrue(toString.contains("User"));
    }

    // ===== COUNTRY MODEL TESTS =====
    
    @Test
    @DisplayName("Country - Should create and access all fields correctly")
    void country_ShouldCreateAndAccessAllFields() {
        // Arrange
        Country country = new Country();
        String countryCode = "US";
        String countryName = "United States";
        String region = "North America";

        // Act
        country.setCountryCode(countryCode);
        country.setCountryName(countryName);
        country.setRegion(region);

        // Assert
        assertEquals(countryCode, country.getCountryCode());
        assertEquals(countryName, country.getCountryName());
        assertEquals(region, country.getRegion());
    }

    @Test
    @DisplayName("Country - Should handle null values correctly")
    void country_ShouldHandleNullValues() {
        // Arrange
        Country country = new Country();

        // Act
        country.setCountryCode(null);
        country.setCountryName(null);
        country.setRegion(null);

        // Assert
        assertNull(country.getCountryCode());
        assertNull(country.getCountryName());
        assertNull(country.getRegion());
    }

    // ===== TARIFF CALCULATION MODEL TESTS =====
    
    @Test
    @DisplayName("TariffCalculation - Should create and access all fields correctly")
    void tariffCalculation_ShouldCreateAndAccessAllFields() {
        // Arrange
        TariffCalculation calculation = new TariffCalculation();
        String htsCode = "12345678";
        String originCountry = "CN";
        String destinationCountry = "US";
        BigDecimal productValue = new BigDecimal("1000.00");
        Integer quantity = 10;
        String currency = "USD";
        BigDecimal totalTariffAmount = new BigDecimal("150.00");
        String calculationType = "STANDARD";
        BigDecimal calculationResult = new BigDecimal("150.00");
        String countryCode = "US";
        LocalDate tariffEffectiveDate = LocalDate.of(2023, 1, 1);
        LocalDate tariffExpirationDate = LocalDate.of(2023, 12, 31);

        // Act
        calculation.setHtsCode(htsCode);
        calculation.setOriginCountry(originCountry);
        calculation.setDestinationCountry(destinationCountry);
        calculation.setProductValue(productValue);
        calculation.setQuantity(quantity);
        calculation.setCurrency(currency);
        calculation.setTotalTariffAmount(totalTariffAmount);
        calculation.setCalculationType(calculationType);
        calculation.setCalculationResult(calculationResult);
        calculation.setCountryCode(countryCode);
        calculation.setTariffEffectiveDate(tariffEffectiveDate);
        calculation.setTariffExpirationDate(tariffExpirationDate);

        // Assert
        assertEquals(htsCode, calculation.getHtsCode());
        assertEquals(originCountry, calculation.getOriginCountry());
        assertEquals(destinationCountry, calculation.getDestinationCountry());
        assertEquals(productValue, calculation.getProductValue());
        assertEquals(quantity, calculation.getQuantity());
        assertEquals(currency, calculation.getCurrency());
        assertEquals(totalTariffAmount, calculation.getTotalTariffAmount());
        assertEquals(calculationType, calculation.getCalculationType());
        assertEquals(calculationResult, calculation.getCalculationResult());
        assertEquals(countryCode, calculation.getCountryCode());
        assertEquals(tariffEffectiveDate, calculation.getTariffEffectiveDate());
        assertEquals(tariffExpirationDate, calculation.getTariffExpirationDate());
    }

    @Test
    @DisplayName("TariffCalculation - Should handle null values correctly")
    void tariffCalculation_ShouldHandleNullValues() {
        // Arrange
        TariffCalculation calculation = new TariffCalculation();

        // Act
        calculation.setHtsCode(null);
        calculation.setOriginCountry(null);
        calculation.setDestinationCountry(null);
        calculation.setProductValue(null);
        calculation.setQuantity(null);
        calculation.setCurrency(null);
        calculation.setTotalTariffAmount(null);
        calculation.setCalculationType(null);
        calculation.setCalculationResult(null);
        calculation.setCountryCode(null);
        calculation.setTariffEffectiveDate(null);
        calculation.setTariffExpirationDate(null);

        // Assert
        assertNull(calculation.getHtsCode());
        assertNull(calculation.getOriginCountry());
        assertNull(calculation.getDestinationCountry());
        assertNull(calculation.getProductValue());
        assertNull(calculation.getQuantity());
        assertNull(calculation.getCurrency());
        assertNull(calculation.getTotalTariffAmount());
        assertNull(calculation.getCalculationType());
        assertNull(calculation.getCalculationResult());
        assertNull(calculation.getCountryCode());
        assertNull(calculation.getTariffEffectiveDate());
        assertNull(calculation.getTariffExpirationDate());
    }

    // ===== MFN TARIFF RATE MODEL TESTS =====
    
    @Test
    @DisplayName("MfnTariffRate - Should create and access all fields correctly")
    void mfnTariffRate_ShouldCreateAndAccessAllFields() {
        // Arrange
        MfnTariffRate mfnRate = new MfnTariffRate();
        Product product = new Product();
        BigDecimal adValoremRate = new BigDecimal("0.15");
        BigDecimal specificRate = new BigDecimal("5.00");
        String textRate = "15% + $5.00 per unit";
        String rateTypeCode = "ADV";

        // Act
        mfnRate.setProduct(product);
        mfnRate.setMfnadValoremRate(adValoremRate);
        mfnRate.setMfnSpecificRate(specificRate);
        mfnRate.setMfnTextRate(textRate);
        mfnRate.setMfnRateTypeCode(rateTypeCode);

        // Assert
        assertEquals(product, mfnRate.getProduct());
        assertEquals(adValoremRate, mfnRate.getMfnadValoremRate());
        assertEquals(specificRate, mfnRate.getMfnSpecificRate());
        assertEquals(textRate, mfnRate.getMfnTextRate());
        assertEquals(rateTypeCode, mfnRate.getMfnRateTypeCode());
    }

    @Test
    @DisplayName("MfnTariffRate - Should handle null values correctly")
    void mfnTariffRate_ShouldHandleNullValues() {
        // Arrange
        MfnTariffRate mfnRate = new MfnTariffRate();

        // Act
        mfnRate.setProduct(null);
        mfnRate.setMfnadValoremRate(null);
        mfnRate.setMfnSpecificRate(null);
        mfnRate.setMfnTextRate(null);
        mfnRate.setMfnRateTypeCode(null);

        // Assert
        assertNull(mfnRate.getProduct());
        assertNull(mfnRate.getMfnadValoremRate());
        assertNull(mfnRate.getMfnSpecificRate());
        assertNull(mfnRate.getMfnTextRate());
        assertNull(mfnRate.getMfnRateTypeCode());
    }

    // ===== AGREEMENT RATE MODEL TESTS =====
    
    @Test
    @DisplayName("AgreementRate - Should create and access all fields correctly")
    void agreementRate_ShouldCreateAndAccessAllFields() {
        // Arrange
        AgreementRate agreementRate = new AgreementRate();
        Product product = new Product();
        TradeAgreement agreement = new TradeAgreement();
        BigDecimal adValoremRate = new BigDecimal("0.05");
        BigDecimal specificRate = new BigDecimal("2.50");
        String textRate = "5% + $2.50 per unit";
        String rateTypeCode = "ADV";

        // Act
        agreementRate.setProduct(product);
        agreementRate.setAgreement(agreement);
        agreementRate.setadValoremRate(adValoremRate);
        agreementRate.setSpecificRate(specificRate);
        agreementRate.setTextRate(textRate);
        agreementRate.setRateTypeCode(rateTypeCode);

        // Assert
        assertEquals(product, agreementRate.getProduct());
        assertEquals(agreement, agreementRate.getAgreement());
        assertEquals(adValoremRate, agreementRate.getadValoremRate());
        assertEquals(specificRate, agreementRate.getSpecificRate());
        assertEquals(textRate, agreementRate.getTextRate());
        assertEquals(rateTypeCode, agreementRate.getRateTypeCode());
    }

    @Test
    @DisplayName("AgreementRate - Should handle null values correctly")
    void agreementRate_ShouldHandleNullValues() {
        // Arrange
        AgreementRate agreementRate = new AgreementRate();

        // Act
        agreementRate.setProduct(null);
        agreementRate.setAgreement(null);
        agreementRate.setadValoremRate(null);
        agreementRate.setSpecificRate(null);
        agreementRate.setTextRate(null);
        agreementRate.setRateTypeCode(null);

        // Assert
        assertNull(agreementRate.getProduct());
        assertNull(agreementRate.getAgreement());
        assertNull(agreementRate.getadValoremRate());
        assertNull(agreementRate.getSpecificRate());
        assertNull(agreementRate.getTextRate());
        assertNull(agreementRate.getRateTypeCode());
    }

    // ===== TRADE AGREEMENT MODEL TESTS =====
    
    @Test
    @DisplayName("TradeAgreement - Should create and access all fields correctly")
    void tradeAgreement_ShouldCreateAndAccessAllFields() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement();
        String agreementName = "USMCA";
        LocalDate effectiveDate = LocalDate.of(2020, 7, 1);
        LocalDate expirationDate = LocalDate.of(2030, 6, 30);
        List<AgreementRate> agreementRates = new ArrayList<>();

        // Act
        agreement.setAgreementName(agreementName);
        agreement.setEffectiveDate(effectiveDate);
        agreement.setExpirationDate(expirationDate);
        agreement.setAgreementRates(agreementRates);

        // Assert
        assertEquals(agreementName, agreement.getAgreementName());
        assertEquals(effectiveDate, agreement.getEffectiveDate());
        assertEquals(expirationDate, agreement.getExpirationDate());
        assertEquals(agreementRates, agreement.getAgreementRates());
    }

    @Test
    @DisplayName("TradeAgreement - Should handle null values correctly")
    void tradeAgreement_ShouldHandleNullValues() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement();

        // Act
        agreement.setAgreementName(null);
        agreement.setEffectiveDate(null);
        agreement.setExpirationDate(null);
        agreement.setAgreementRates(null);

        // Assert
        assertNull(agreement.getAgreementName());
        assertNull(agreement.getEffectiveDate());
        assertNull(agreement.getExpirationDate());
        assertNull(agreement.getAgreementRates());
    }

    // ===== EDGE CASE TESTS =====
    
    @Test
    @DisplayName("Product - Should handle empty string values")
    void product_ShouldHandleEmptyStringValues() {
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
    @DisplayName("User - Should handle empty string values")
    void user_ShouldHandleEmptyStringValues() {
        // Arrange
        User user = new User();

        // Act
        user.setUsername("");
        user.setEmail("");
        user.setPassword("");

        // Assert
        assertEquals("", user.getUsername());
        assertEquals("", user.getEmail());
        assertEquals("", user.getPassword());
    }

    @Test
    @DisplayName("TariffCalculation - Should handle zero values")
    void tariffCalculation_ShouldHandleZeroValues() {
        // Arrange
        TariffCalculation calculation = new TariffCalculation();

        // Act
        calculation.setProductValue(BigDecimal.ZERO);
        calculation.setQuantity(0);
        calculation.setTotalTariffAmount(BigDecimal.ZERO);

        // Assert
        assertEquals(BigDecimal.ZERO, calculation.getProductValue());
        assertEquals(0, calculation.getQuantity());
        assertEquals(BigDecimal.ZERO, calculation.getTotalTariffAmount());
    }

    @Test
    @DisplayName("MfnTariffRate - Should handle zero rate values")
    void mfnTariffRate_ShouldHandleZeroRateValues() {
        // Arrange
        MfnTariffRate mfnRate = new MfnTariffRate();

        // Act
        mfnRate.setMfnadValoremRate(BigDecimal.ZERO);
        mfnRate.setMfnSpecificRate(BigDecimal.ZERO);

        // Assert
        assertEquals(BigDecimal.ZERO, mfnRate.getMfnadValoremRate());
        assertEquals(BigDecimal.ZERO, mfnRate.getMfnSpecificRate());
    }

    @Test
    @DisplayName("AgreementRate - Should handle zero rate values")
    void agreementRate_ShouldHandleZeroRateValues() {
        // Arrange
        AgreementRate agreementRate = new AgreementRate();

        // Act
        agreementRate.setadValoremRate(BigDecimal.ZERO);
        agreementRate.setSpecificRate(BigDecimal.ZERO);

        // Assert
        assertEquals(BigDecimal.ZERO, agreementRate.getadValoremRate());
        assertEquals(BigDecimal.ZERO, agreementRate.getSpecificRate());
    }

    // ===== DATE HANDLING TESTS =====
    
    @Test
    @DisplayName("TariffCalculation - Should handle date edge cases")
    void tariffCalculation_ShouldHandleDateEdgeCases() {
        // Arrange
        TariffCalculation calculation = new TariffCalculation();
        LocalDate pastDate = LocalDate.of(1900, 1, 1);
        LocalDate futureDate = LocalDate.of(2100, 12, 31);
        LocalDate leapYearDate = LocalDate.of(2024, 2, 29);

        // Act
        calculation.setTariffEffectiveDate(pastDate);
        calculation.setTariffExpirationDate(futureDate);

        // Assert
        assertEquals(pastDate, calculation.getTariffEffectiveDate());
        assertEquals(futureDate, calculation.getTariffExpirationDate());
    }

    @Test
    @DisplayName("TradeAgreement - Should handle date edge cases")
    void tradeAgreement_ShouldHandleDateEdgeCases() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement();
        LocalDate pastDate = LocalDate.of(1900, 1, 1);
        LocalDate futureDate = LocalDate.of(2100, 12, 31);

        // Act
        agreement.setEffectiveDate(pastDate);
        agreement.setExpirationDate(futureDate);

        // Assert
        assertEquals(pastDate, agreement.getEffectiveDate());
        assertEquals(futureDate, agreement.getExpirationDate());
    }

    // ===== COLLECTION TESTS =====
    
    @Test
    @DisplayName("TradeAgreement - Should handle empty agreement rates list")
    void tradeAgreement_ShouldHandleEmptyAgreementRatesList() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement();
        List<AgreementRate> emptyList = new ArrayList<>();

        // Act
        agreement.setAgreementRates(emptyList);

        // Assert
        assertNotNull(agreement.getAgreementRates());
        assertTrue(agreement.getAgreementRates().isEmpty());
    }

    @Test
    @DisplayName("TradeAgreement - Should handle null agreement rates list")
    void tradeAgreement_ShouldHandleNullAgreementRatesList() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement();

        // Act
        agreement.setAgreementRates(null);

        // Assert
        assertNull(agreement.getAgreementRates());
    }

    // ===== ENUM TESTS =====
    
    @Test
    @DisplayName("User.Role - Should test all enum values")
    void userRole_ShouldTestAllEnumValues() {
        // Test all enum values exist
        assertNotNull(User.Role.USER);
        assertNotNull(User.Role.ADMIN);
        
        // Test enum values can be set
        User user = new User();
        user.setRole(User.Role.USER);
        assertEquals(User.Role.USER, user.getRole());
        
        user.setRole(User.Role.ADMIN);
        assertEquals(User.Role.ADMIN, user.getRole());
    }

    // ===== EQUALS AND HASHCODE TESTS =====
    
    @Test
    @DisplayName("Product - Should test equals and hashCode")
    void product_ShouldTestEqualsAndHashCode() {
        // Arrange
        Product product1 = new Product();
        product1.setHts8("12345678");
        product1.setBriefDescription("Test Product");
        
        Product product2 = new Product();
        product2.setHts8("12345678");
        product2.setBriefDescription("Test Product");
        
        Product product3 = new Product();
        product3.setHts8("87654321");
        product3.setBriefDescription("Different Product");

        // Assert - Default equals() compares object references
        assertNotEquals(product1, product2); // Different object instances
        assertNotEquals(product1, product3);
        assertNotEquals(product1.hashCode(), product2.hashCode());
        assertNotEquals(product1.hashCode(), product3.hashCode());
    }

    @Test
    @DisplayName("User - Should test equals and hashCode")
    void user_ShouldTestEqualsAndHashCode() {
        // Arrange
        User user1 = new User();
        user1.setUsername("testuser");
        user1.setEmail("test@example.com");
        
        User user2 = new User();
        user2.setUsername("testuser");
        user2.setEmail("test@example.com");
        
        User user3 = new User();
        user3.setUsername("differentuser");
        user3.setEmail("different@example.com");

        // Assert - Default equals() compares object references
        assertNotEquals(user1, user2); // Different object instances
        assertNotEquals(user1, user3);
        assertNotEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }
}
