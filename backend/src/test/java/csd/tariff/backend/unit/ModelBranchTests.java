package csd.tariff.backend.unit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import csd.tariff.backend.model.AgreementParticipant;
import csd.tariff.backend.model.AgreementRate;
import csd.tariff.backend.model.Country;
import csd.tariff.backend.model.CurrencyExchangeRate;
import csd.tariff.backend.model.MfnTariffRate;
import csd.tariff.backend.model.Product;
import csd.tariff.backend.model.ProductIndicator;
import csd.tariff.backend.model.ProductNote;
import csd.tariff.backend.model.TariffCalculation;
import csd.tariff.backend.model.TradeAgreement;
import csd.tariff.backend.model.User;

@DisplayName("Model Branch Coverage Tests")
class ModelBranchTests {

    @Nested
    @DisplayName("Product Branch Tests")
    class ProductBranchTests {

        @Test
        @DisplayName("Should handle different constructor branches")
        void product_ShouldHandleDifferentConstructorBranches() {
            // Test default constructor branch
            Product product1 = new Product();
            assertNotNull(product1.getCreatedAt());
            assertNotNull(product1.getUpdatedAt());
            assertNull(product1.getHts8());
            assertNull(product1.getBriefDescription());

            // Test parameterized constructor branch
            Product product2 = new Product("12345678", "Test Product");
            assertNotNull(product2.getCreatedAt());
            assertNotNull(product2.getUpdatedAt());
            assertEquals("12345678", product2.getHts8());
            assertEquals("Test Product", product2.getBriefDescription());
        }

        @Test
        @DisplayName("Should handle null and non-null field branches")
        void product_ShouldHandleNullAndNonNullFieldBranches() {
            Product product = new Product();
            
            // Test null branches
            assertNull(product.getHts8());
            assertNull(product.getBriefDescription());
            assertNull(product.getQuantity1Code());
            assertNull(product.getQuantity2Code());
            assertNull(product.getWtoBindingCode());
            assertNull(product.getMfnTariffRates());
            assertNull(product.getAgreementRates());
            assertNull(product.getProductIndicators());
            assertNull(product.getProductNotes());

            // Test non-null branches
            product.setHts8("87654321");
            product.setBriefDescription("Updated Product");
            product.setQuantity1Code("KG");
            product.setQuantity2Code("L");
            product.setWtoBindingCode("A");

            assertEquals("87654321", product.getHts8());
            assertEquals("Updated Product", product.getBriefDescription());
            assertEquals("KG", product.getQuantity1Code());
            assertEquals("L", product.getQuantity2Code());
            assertEquals("A", product.getWtoBindingCode());
        }
    }

    @Nested
    @DisplayName("User Branch Tests")
    class UserBranchTests {

        @Test
        @DisplayName("Should handle different constructor branches")
        void user_ShouldHandleDifferentConstructorBranches() {
            // Test default constructor branch
            User user1 = new User();
            assertNull(user1.getEmail());
            assertNull(user1.getPassword());
            assertNull(user1.getRole());
            assertNull(user1.getUsername());

            // Test parameterized constructor branch
            User user2 = new User("test@example.com", "password123", User.Role.USER);
            assertEquals("test@example.com", user2.getEmail());
            assertEquals("password123", user2.getPassword());
            assertEquals(User.Role.USER, user2.getRole());
            assertNull(user2.getUsername()); // Not set in constructor
        }

        @Test
        @DisplayName("Should handle different role enum branches")
        void user_ShouldHandleDifferentRoleEnumBranches() {
            User user = new User();

            // Test USER role branch
            user.setRole(User.Role.USER);
            assertEquals(User.Role.USER, user.getRole());

            // Test ADMIN role branch
            user.setRole(User.Role.ADMIN);
            assertEquals(User.Role.ADMIN, user.getRole());
        }

        @Test
        @DisplayName("Should handle null and non-null field branches")
        void user_ShouldHandleNullAndNonNullFieldBranches() {
            User user = new User();

            // Test null branches
            assertNull(user.getId());
            assertNull(user.getUsername());
            assertNull(user.getEmail());
            assertNull(user.getPassword());
            assertNull(user.getRole());

            // Test non-null branches
            user.setUsername("testuser");
            user.setEmail("test@example.com");
            user.setPassword("password123");
            user.setRole(User.Role.ADMIN);

            assertEquals("testuser", user.getUsername());
            assertEquals("test@example.com", user.getEmail());
            assertEquals("password123", user.getPassword());
            assertEquals(User.Role.ADMIN, user.getRole());
        }

        @Test
        @DisplayName("Should handle email validation branches")
        void user_ShouldHandleEmailValidationBranches() {
            User user = new User();

            // Test various email formats
            String[] validEmails = {
                "user@example.com",
                "test.user@domain.org",
                "user123@test-domain.co.uk",
                "a@b.c",
                "user+tag@example.com"
            };

            String[] invalidEmails = {
                "invalid-email",
                "@example.com",
                "user@",
                "user@.com",
                "user..double@example.com"
            };

            // Test valid emails
            for (String email : validEmails) {
                user.setEmail(email);
                assertEquals(email, user.getEmail());
            }

            // Test invalid emails (should still be set, validation happens at persistence level)
            for (String email : invalidEmails) {
                user.setEmail(email);
                assertEquals(email, user.getEmail());
            }
        }

        @Test
        @DisplayName("Should handle username length branches")
        void user_ShouldHandleUsernameLengthBranches() {
            User user = new User();

            // Test various username lengths (max 8 characters)
            String[] usernames = {
                "a",           // 1 char
                "ab",          // 2 chars
                "abc",         // 3 chars
                "abcd",        // 4 chars
                "abcde",       // 5 chars
                "abcdef",      // 6 chars
                "abcdefg",     // 7 chars
                "abcdefgh",    // 8 chars (max)
                "abcdefghi"    // 9 chars (over limit)
            };

            for (String username : usernames) {
                user.setUsername(username);
                assertEquals(username, user.getUsername());
            }
        }

        @Test
        @DisplayName("Should handle password complexity branches")
        void user_ShouldHandlePasswordComplexityBranches() {
            User user = new User();

            // Test various password types
            String[] passwords = {
                "simple",
                "password123",
                "P@ssw0rd!",
                "very-long-password-with-special-chars-123!@#",
                "",
                "   ",  // whitespace
                "password with spaces",
                "password\nwith\nnewlines",
                "password\twith\ttabs"
            };

            for (String password : passwords) {
                user.setPassword(password);
                assertEquals(password, user.getPassword());
            }
        }

        @Test
        @DisplayName("Should handle role enum branches")
        void user_ShouldHandleRoleEnumBranches() {
            User user = new User();

            // Test all role enum values
            User.Role[] roles = { User.Role.USER, User.Role.ADMIN };

            for (User.Role role : roles) {
                user.setRole(role);
                assertEquals(role, user.getRole());
            }

            // Test role switching
            user.setRole(User.Role.USER);
            assertEquals(User.Role.USER, user.getRole());

            user.setRole(User.Role.ADMIN);
            assertEquals(User.Role.ADMIN, user.getRole());

            user.setRole(User.Role.USER);
            assertEquals(User.Role.USER, user.getRole());
        }

        @Test
        @DisplayName("Should handle field updates and resets")
        void user_ShouldHandleFieldUpdatesAndResets() {
            User user = new User();

            // Set initial values
            user.setUsername("initial");
            user.setEmail("initial@example.com");
            user.setPassword("initial123");
            user.setRole(User.Role.USER);

            assertEquals("initial", user.getUsername());
            assertEquals("initial@example.com", user.getEmail());
            assertEquals("initial123", user.getPassword());
            assertEquals(User.Role.USER, user.getRole());

            // Update values
            user.setUsername("updated");
            user.setEmail("updated@example.com");
            user.setPassword("updated456");
            user.setRole(User.Role.ADMIN);

            assertEquals("updated", user.getUsername());
            assertEquals("updated@example.com", user.getEmail());
            assertEquals("updated456", user.getPassword());
            assertEquals(User.Role.ADMIN, user.getRole());

            // Reset to null
            user.setUsername(null);
            user.setEmail(null);
            user.setPassword(null);
            user.setRole(null);

            assertNull(user.getUsername());
            assertNull(user.getEmail());
            assertNull(user.getPassword());
            assertNull(user.getRole());
        }

        @Test
        @DisplayName("Should handle constructor with all parameters")
        void user_ShouldHandleConstructorWithAllParameters() {
            // Test constructor with email, password, and role
            User user = new User("constructor@example.com", "constructor123", User.Role.ADMIN);

            assertNull(user.getId()); // ID is auto-generated
            assertNull(user.getUsername()); // Not set in constructor
            assertEquals("constructor@example.com", user.getEmail());
            assertEquals("constructor123", user.getPassword());
            assertEquals(User.Role.ADMIN, user.getRole());
        }

        @Test
        @DisplayName("Should handle constructor with null parameters")
        void user_ShouldHandleConstructorWithNullParameters() {
            // Test constructor with null parameters
            User user = new User(null, null, null);

            assertNull(user.getId());
            assertNull(user.getUsername());
            assertNull(user.getEmail());
            assertNull(user.getPassword());
            assertNull(user.getRole());
        }

        @Test
        @DisplayName("Should handle multiple user instances")
        void user_ShouldHandleMultipleUserInstances() {
            User user1 = new User("user1@example.com", "password1", User.Role.USER);
            User user2 = new User("user2@example.com", "password2", User.Role.ADMIN);

            // Verify they are independent instances
            assertNotNull(user1);
            assertNotNull(user2);

            assertEquals("user1@example.com", user1.getEmail());
            assertEquals("password1", user1.getPassword());
            assertEquals(User.Role.USER, user1.getRole());

            assertEquals("user2@example.com", user2.getEmail());
            assertEquals("password2", user2.getPassword());
            assertEquals(User.Role.ADMIN, user2.getRole());

            // Modify one instance and verify the other is unaffected
            user1.setUsername("user1name");
            user2.setUsername("user2name");

            assertEquals("user1name", user1.getUsername());
            assertEquals("user2name", user2.getUsername());
        }

        @Test
        @DisplayName("Should handle edge case values")
        void user_ShouldHandleEdgeCaseValues() {
            User user = new User();

            // Test edge case values
            user.setUsername("");  // Empty string
            user.setEmail("");     // Empty string
            user.setPassword("");  // Empty string

            assertEquals("", user.getUsername());
            assertEquals("", user.getEmail());
            assertEquals("", user.getPassword());

            // Test whitespace-only values
            user.setUsername("   ");
            user.setEmail("   ");
            user.setPassword("   ");

            assertEquals("   ", user.getUsername());
            assertEquals("   ", user.getEmail());
            assertEquals("   ", user.getPassword());
        }
    }

    @Nested
    @DisplayName("TariffCalculation Branch Tests")
    class TariffCalculationBranchTests {

        @Test
        @DisplayName("Should handle different constructor branches")
        void tariffCalculation_ShouldHandleDifferentConstructorBranches() {
            // Test default constructor branch
            TariffCalculation calc1 = new TariffCalculation();
            assertNull(calc1.getHtsCode());
            assertNull(calc1.getCountryCode());

            // Test parameterized constructor branch
            TariffCalculation calc2 = new TariffCalculation("12345678", "US", "US", "CA", 
                new BigDecimal("1000.00"), 10, "MFN", new BigDecimal("100.00"), 
                new BigDecimal("90.00"), LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31), "USD");
            assertEquals("12345678", calc2.getHtsCode());
            assertEquals("US", calc2.getCountryCode());
            assertEquals("CA", calc2.getDestinationCountry());
            assertEquals(new BigDecimal("1000.00"), calc2.getProductValue());
            assertEquals(10, calc2.getQuantity());
            assertEquals("USD", calc2.getCurrency());
        }

        @Test
        @DisplayName("Should handle null and non-null field branches")
        void tariffCalculation_ShouldHandleNullAndNonNullFieldBranches() {
            TariffCalculation calc = new TariffCalculation();

            // Test null branches
            assertNull(calc.getHtsCode());
            assertNull(calc.getCountryCode());
            assertNull(calc.getOriginCountry());
            assertNull(calc.getDestinationCountry());
            assertNull(calc.getProductValue());
            assertNull(calc.getQuantity());
            assertNull(calc.getCalculationType());
            assertNull(calc.getCalculationResult());
            assertNull(calc.getTotalTariffAmount());
            assertNull(calc.getTariffEffectiveDate());
            assertNull(calc.getTariffExpirationDate());
            assertNull(calc.getCurrency());

            // Test non-null branches
            calc.setHtsCode("87654321");
            calc.setCountryCode("CA");
            calc.setOriginCountry("US");
            calc.setDestinationCountry("MX");
            calc.setProductValue(new BigDecimal("2000.00"));
            calc.setQuantity(5);
            calc.setCalculationType("MFN");
            calc.setCalculationResult(new BigDecimal("100.00"));
            calc.setTotalTariffAmount(new BigDecimal("150.00"));
            calc.setTariffEffectiveDate(LocalDate.of(2023, 1, 1));
            calc.setTariffExpirationDate(LocalDate.of(2023, 12, 31));
            calc.setCurrency("CAD");

            assertEquals("87654321", calc.getHtsCode());
            assertEquals("CA", calc.getCountryCode());
            assertEquals("US", calc.getOriginCountry());
            assertEquals("MX", calc.getDestinationCountry());
            assertEquals(new BigDecimal("2000.00"), calc.getProductValue());
            assertEquals(5, calc.getQuantity());
            assertEquals("MFN", calc.getCalculationType());
            assertEquals(new BigDecimal("100.00"), calc.getCalculationResult());
            assertEquals(new BigDecimal("150.00"), calc.getTotalTariffAmount());
            assertEquals(LocalDate.of(2023, 1, 1), calc.getTariffEffectiveDate());
            assertEquals(LocalDate.of(2023, 12, 31), calc.getTariffExpirationDate());
            assertEquals("CAD", calc.getCurrency());
        }
    }

    @Nested
    @DisplayName("TradeAgreement Branch Tests")
    class TradeAgreementBranchTests {

        @Test
        @DisplayName("Should handle different constructor branches")
        void tradeAgreement_ShouldHandleDifferentConstructorBranches() {
            // Test default constructor branch
            TradeAgreement agreement1 = new TradeAgreement();
            assertNotNull(agreement1.getCreatedAt());
            assertNotNull(agreement1.getUpdatedAt());
            assertNull(agreement1.getAgreementCode());
            assertNull(agreement1.getAgreementName());
            assertNull(agreement1.getAgreementType());
            assertFalse(agreement1.getIsMultilateral()); // Default value

            // Test parameterized constructor branch
            TradeAgreement agreement2 = new TradeAgreement("USMCA", "US-Mexico-Canada Agreement", "FTA");
            assertNotNull(agreement2.getCreatedAt());
            assertNotNull(agreement2.getUpdatedAt());
            assertEquals("USMCA", agreement2.getAgreementCode());
            assertEquals("US-Mexico-Canada Agreement", agreement2.getAgreementName());
            assertEquals("FTA", agreement2.getAgreementType());
            assertFalse(agreement2.getIsMultilateral()); // Default value
        }

        @Test
        @DisplayName("Should handle boolean field branches")
        void tradeAgreement_ShouldHandleBooleanFieldBranches() {
            TradeAgreement agreement = new TradeAgreement();

            // Test false branch
            assertFalse(agreement.getIsMultilateral());

            // Test true branch
            agreement.setIsMultilateral(true);
            assertTrue(agreement.getIsMultilateral());

            // Test false branch again
            agreement.setIsMultilateral(false);
            assertFalse(agreement.getIsMultilateral());
        }

        @Test
        @DisplayName("Should handle null and non-null field branches")
        void tradeAgreement_ShouldHandleNullAndNonNullFieldBranches() {
            TradeAgreement agreement = new TradeAgreement();

            // Test null branches
            assertNull(agreement.getAgreementCode());
            assertNull(agreement.getAgreementName());
            assertNull(agreement.getAgreementType());
            assertNull(agreement.getEffectiveDate());
            assertNull(agreement.getExpirationDate());
            assertNull(agreement.getParticipants());
            assertNull(agreement.getAgreementRates());

            // Test non-null branches
            agreement.setAgreementCode("CPTPP");
            agreement.setAgreementName("Comprehensive and Progressive Agreement");
            agreement.setAgreementType("FTA");
            agreement.setEffectiveDate(LocalDate.of(2020, 1, 1));
            agreement.setExpirationDate(LocalDate.of(2030, 12, 31));

            assertEquals("CPTPP", agreement.getAgreementCode());
            assertEquals("Comprehensive and Progressive Agreement", agreement.getAgreementName());
            assertEquals("FTA", agreement.getAgreementType());
            assertEquals(LocalDate.of(2020, 1, 1), agreement.getEffectiveDate());
            assertEquals(LocalDate.of(2030, 12, 31), agreement.getExpirationDate());
        }
    }

    @Nested
    @DisplayName("AgreementRate Branch Tests")
    class AgreementRateBranchTests {

        @Test
        @DisplayName("Should handle different constructor branches")
        void agreementRate_ShouldHandleDifferentConstructorBranches() {
            // Test default constructor branch
            AgreementRate rate1 = new AgreementRate();
            assertNotNull(rate1.getCreatedAt());
            assertNotNull(rate1.getUpdatedAt());
            assertNull(rate1.getProduct());
            assertNull(rate1.getAgreement());
            assertNull(rate1.getCountry());

            // Test parameterized constructor branch
            Product product = new Product("12345678", "Test Product");
            TradeAgreement agreement = new TradeAgreement("USMCA", "Test Agreement", "FTA");
            Country country = new Country();
            country.setCountryCode("US");

            AgreementRate rate2 = new AgreementRate(product, agreement, country);
            assertNotNull(rate2.getCreatedAt());
            assertNotNull(rate2.getUpdatedAt());
            assertEquals(product, rate2.getProduct());
            assertEquals(agreement, rate2.getAgreement());
            assertEquals(country, rate2.getCountry());
            assertNotNull(rate2.getEffectiveDate());
        }

        @Test
        @DisplayName("Should handle null and non-null field branches")
        void agreementRate_ShouldHandleNullAndNonNullFieldBranches() {
            AgreementRate rate = new AgreementRate();

            // Test null branches
            assertNull(rate.getProduct());
            assertNull(rate.getAgreement());
            assertNull(rate.getCountry());
            assertNull(rate.getRateTypeCode());
            assertNull(rate.getSpecificRate());
            assertNull(rate.getOtherRate());
            assertNull(rate.getEffectiveDate());
            assertNull(rate.getExpirationDate());
            assertNull(rate.getadValoremRate());
            assertNull(rate.getIndicator());
            assertNull(rate.getTextRate());

            // Test non-null branches
            Product product = new Product("87654321", "Test Product");
            TradeAgreement agreement = new TradeAgreement("CPTPP", "Test Agreement", "FTA");
            Country country = new Country();
            country.setCountryCode("CA");

            rate.setProduct(product);
            rate.setAgreement(agreement);
            rate.setCountry(country);
            rate.setRateTypeCode("ADV");
            rate.setSpecificRate(new BigDecimal("2.50"));
            rate.setOtherRate(new BigDecimal("0.10"));
            rate.setEffectiveDate(LocalDate.of(2023, 1, 1));
            rate.setExpirationDate(LocalDate.of(2023, 12, 31));
            rate.setadValoremRate(new BigDecimal("0.08"));
            rate.setIndicator("FREE");
            rate.setTextRate("Free");

            assertEquals(product, rate.getProduct());
            assertEquals(agreement, rate.getAgreement());
            assertEquals(country, rate.getCountry());
            assertEquals("ADV", rate.getRateTypeCode());
            assertEquals(new BigDecimal("2.50"), rate.getSpecificRate());
            assertEquals(new BigDecimal("0.10"), rate.getOtherRate());
            assertEquals(LocalDate.of(2023, 1, 1), rate.getEffectiveDate());
            assertEquals(LocalDate.of(2023, 12, 31), rate.getExpirationDate());
            assertEquals(new BigDecimal("0.08"), rate.getadValoremRate());
            assertEquals("FREE", rate.getIndicator());
            assertEquals("Free", rate.getTextRate());
        }
    }

    @Nested
    @DisplayName("ProductIndicator Branch Tests")
    class ProductIndicatorBranchTests {

        @Test
        @DisplayName("Should handle different constructor branches")
        void productIndicator_ShouldHandleDifferentConstructorBranches() {
            // Test default constructor branch
            ProductIndicator indicator1 = new ProductIndicator();
            assertNotNull(indicator1.getCreatedAt());
            assertNotNull(indicator1.getUpdatedAt());
            assertNull(indicator1.getProduct());
            assertNull(indicator1.getIndicatorType());
            assertNull(indicator1.getIndicatorValue());

            // Test parameterized constructor branch
            Product product = new Product("12345678", "Test Product");
            ProductIndicator indicator2 = new ProductIndicator(product, "SAFETY", "REQUIRED");
            assertNotNull(indicator2.getCreatedAt());
            assertNotNull(indicator2.getUpdatedAt());
            assertEquals(product, indicator2.getProduct());
            assertEquals("SAFETY", indicator2.getIndicatorType());
            assertEquals("REQUIRED", indicator2.getIndicatorValue());
        }

        @Test
        @DisplayName("Should handle null and non-null field branches")
        void productIndicator_ShouldHandleNullAndNonNullFieldBranches() {
            ProductIndicator indicator = new ProductIndicator();

            // Test null branches
            assertNull(indicator.getProduct());
            assertNull(indicator.getIndicatorType());
            assertNull(indicator.getIndicatorValue());
            assertNull(indicator.getExcludedCountries());
            assertNull(indicator.getEffectiveDate());
            assertNull(indicator.getExpirationDate());

            // Test non-null branches
            Product product = new Product("87654321", "Test Product");
            indicator.setProduct(product);
            indicator.setIndicatorType("ENVIRONMENTAL");
            indicator.setIndicatorValue("OPTIONAL");
            indicator.setExcludedCountries("US,CA");
            indicator.setEffectiveDate(LocalDate.of(2023, 1, 1));
            indicator.setExpirationDate(LocalDate.of(2023, 12, 31));

            assertEquals(product, indicator.getProduct());
            assertEquals("ENVIRONMENTAL", indicator.getIndicatorType());
            assertEquals("OPTIONAL", indicator.getIndicatorValue());
            assertEquals("US,CA", indicator.getExcludedCountries());
            assertEquals(LocalDate.of(2023, 1, 1), indicator.getEffectiveDate());
            assertEquals(LocalDate.of(2023, 12, 31), indicator.getExpirationDate());
        }
    }

    @Nested
    @DisplayName("MfnTariffRate Branch Tests")
    class MfnTariffRateBranchTests {

        @Test
        @DisplayName("Should handle different constructor branches")
        void mfnTariffRate_ShouldHandleDifferentConstructorBranches() {
            // Test default constructor branch
            MfnTariffRate rate1 = new MfnTariffRate();
            assertNotNull(rate1.getCreatedAt());
            assertNotNull(rate1.getUpdatedAt());
            assertNull(rate1.getProduct());
            assertNull(rate1.getMfnadValoremRate());
            assertNull(rate1.getMfnSpecificRate());

            // Test parameterized constructor branch
            Product product = new Product("12345678", "Test Product");
            MfnTariffRate rate2 = new MfnTariffRate();
            rate2.setProduct(product);
            rate2.setMfnadValoremRate(new BigDecimal("0.10"));
            rate2.setMfnSpecificRate(new BigDecimal("2.00"));
            assertNotNull(rate2.getCreatedAt());
            assertNotNull(rate2.getUpdatedAt());
            assertEquals(product, rate2.getProduct());
            assertEquals(new BigDecimal("0.10"), rate2.getMfnadValoremRate());
            assertEquals(new BigDecimal("2.00"), rate2.getMfnSpecificRate());
        }

        @Test
        @DisplayName("Should handle null and non-null field branches")
        void mfnTariffRate_ShouldHandleNullAndNonNullFieldBranches() {
            MfnTariffRate rate = new MfnTariffRate();

            // Test null branches
            assertNull(rate.getProduct());
            assertNull(rate.getMfnadValoremRate());
            assertNull(rate.getMfnSpecificRate());
            assertNull(rate.getMfnOtherRate());
            assertNull(rate.getMfnAve());
            assertNull(rate.getBeginEffectDate());
            assertNull(rate.getEndEffectiveDate());
            assertNull(rate.getMfnRateTypeCode());
            assertNull(rate.getMfnTextRate());
            assertNull(rate.getCol1SpecialText());
            assertNull(rate.getCol1SpecialMod());
            assertNull(rate.getCol2TextRate());
            assertNull(rate.getCol2RateTypeCode());
            assertNull(rate.getCol2adValoremRate());
            assertNull(rate.getCol2SpecificRate());
            assertNull(rate.getCol2OtherRate());

            // Test non-null branches
            Product product = new Product("12345678", "Test Product");
            rate.setProduct(product);
            rate.setMfnadValoremRate(new BigDecimal("0.10"));
            rate.setMfnSpecificRate(new BigDecimal("2.00"));
            rate.setMfnOtherRate(new BigDecimal("1.50"));
            rate.setMfnAve(new BigDecimal("0.15"));
            rate.setBeginEffectDate(LocalDate.of(2023, 1, 1));
            rate.setEndEffectiveDate(LocalDate.of(2023, 12, 31));
            rate.setMfnRateTypeCode("ADV");
            rate.setMfnTextRate("10% ad valorem");
            rate.setCol1SpecialText("Special text");
            rate.setCol1SpecialMod("Special mod");
            rate.setCol2TextRate("Col2 text");
            rate.setCol2RateTypeCode("COL2");
            rate.setCol2adValoremRate(new BigDecimal("0.08"));
            rate.setCol2SpecificRate(new BigDecimal("1.50"));
            rate.setCol2OtherRate(new BigDecimal("1.00"));

            assertEquals(product, rate.getProduct());
            assertEquals(new BigDecimal("0.10"), rate.getMfnadValoremRate());
            assertEquals(new BigDecimal("2.00"), rate.getMfnSpecificRate());
            assertEquals(new BigDecimal("1.50"), rate.getMfnOtherRate());
            assertEquals(new BigDecimal("0.15"), rate.getMfnAve());
            assertEquals(LocalDate.of(2023, 1, 1), rate.getBeginEffectDate());
            assertEquals(LocalDate.of(2023, 12, 31), rate.getEndEffectiveDate());
            assertEquals("ADV", rate.getMfnRateTypeCode());
            assertEquals("10% ad valorem", rate.getMfnTextRate());
            assertEquals("Special text", rate.getCol1SpecialText());
            assertEquals("Special mod", rate.getCol1SpecialMod());
            assertEquals("Col2 text", rate.getCol2TextRate());
            assertEquals("COL2", rate.getCol2RateTypeCode());
            assertEquals(new BigDecimal("0.08"), rate.getCol2adValoremRate());
            assertEquals(new BigDecimal("1.50"), rate.getCol2SpecificRate());
            assertEquals(new BigDecimal("1.00"), rate.getCol2OtherRate());
        }

        @Test
        @DisplayName("Should handle BigDecimal precision branches")
        void mfnTariffRate_ShouldHandleBigDecimalPrecisionBranches() {
            MfnTariffRate rate = new MfnTariffRate();

            // Test different BigDecimal scales
            BigDecimal rate1 = new BigDecimal("0.123456");
            BigDecimal rate2 = new BigDecimal("1.234567");
            BigDecimal rate3 = new BigDecimal("12.345678");

            rate.setMfnadValoremRate(rate1);
            rate.setMfnSpecificRate(rate2);
            rate.setMfnOtherRate(rate3);
            rate.setMfnAve(rate1);
            rate.setCol2adValoremRate(rate2);
            rate.setCol2SpecificRate(rate3);
            rate.setCol2OtherRate(rate1);

            assertEquals(rate1, rate.getMfnadValoremRate());
            assertEquals(rate2, rate.getMfnSpecificRate());
            assertEquals(rate3, rate.getMfnOtherRate());
            assertEquals(rate1, rate.getMfnAve());
            assertEquals(rate2, rate.getCol2adValoremRate());
            assertEquals(rate3, rate.getCol2SpecificRate());
            assertEquals(rate1, rate.getCol2OtherRate());
        }

        @Test
        @DisplayName("Should handle date range branches")
        void mfnTariffRate_ShouldHandleDateRangeBranches() {
            MfnTariffRate rate = new MfnTariffRate();

            // Test different date scenarios
            LocalDate startDate = LocalDate.of(2023, 1, 1);
            LocalDate endDate = LocalDate.of(2023, 12, 31);
            LocalDate futureDate = LocalDate.of(2025, 1, 1);
            LocalDate pastDate = LocalDate.of(2020, 1, 1);

            rate.setBeginEffectDate(startDate);
            rate.setEndEffectiveDate(endDate);

            assertEquals(startDate, rate.getBeginEffectDate());
            assertEquals(endDate, rate.getEndEffectiveDate());

            // Test future dates
            rate.setBeginEffectDate(futureDate);
            rate.setEndEffectiveDate(futureDate.plusYears(1));
            assertEquals(futureDate, rate.getBeginEffectDate());
            assertEquals(futureDate.plusYears(1), rate.getEndEffectiveDate());

            // Test past dates
            rate.setBeginEffectDate(pastDate);
            rate.setEndEffectiveDate(pastDate.plusYears(1));
            assertEquals(pastDate, rate.getBeginEffectDate());
            assertEquals(pastDate.plusYears(1), rate.getEndEffectiveDate());
        }

        @Test
        @DisplayName("Should handle string field branches")
        void mfnTariffRate_ShouldHandleStringFieldBranches() {
            MfnTariffRate rate = new MfnTariffRate();

            // Test different string scenarios
            String shortText = "Short";
            String longText = "This is a very long text that might exceed normal limits";
            String emptyText = "";
            String nullText = null;

            rate.setMfnTextRate(shortText);
            rate.setCol1SpecialText(longText);
            rate.setCol1SpecialMod(emptyText);
            rate.setCol2TextRate(nullText);

            assertEquals(shortText, rate.getMfnTextRate());
            assertEquals(longText, rate.getCol1SpecialText());
            assertEquals(emptyText, rate.getCol1SpecialMod());
            assertEquals(nullText, rate.getCol2TextRate());
        }

        @Test
        @DisplayName("Should handle rate type code branches")
        void mfnTariffRate_ShouldHandleRateTypeCodeBranches() {
            MfnTariffRate rate = new MfnTariffRate();

            // Test different rate type codes
            String[] rateTypeCodes = {"ADV", "SPC", "OTH", "COL2", "MFN", "PREF"};
            
            for (String code : rateTypeCodes) {
                rate.setMfnRateTypeCode(code);
                rate.setCol2RateTypeCode(code + "_COL2");
                
                assertEquals(code, rate.getMfnRateTypeCode());
                assertEquals(code + "_COL2", rate.getCol2RateTypeCode());
            }
        }

        @Test
        @DisplayName("Should handle updatedAt timestamp branches")
        void mfnTariffRate_ShouldHandleUpdatedAtTimestampBranches() {
            MfnTariffRate rate = new MfnTariffRate();

            // Test initial timestamp
            LocalDateTime initialCreatedAt = rate.getCreatedAt();
            LocalDateTime initialUpdatedAt = rate.getUpdatedAt();
            
            assertNotNull(initialCreatedAt);
            assertNotNull(initialUpdatedAt);

            // Test updating timestamp
            LocalDateTime newUpdatedAt = LocalDateTime.now().plusHours(1);
            rate.setUpdatedAt(newUpdatedAt);
            
            assertEquals(initialCreatedAt, rate.getCreatedAt()); // Should not change
            assertEquals(newUpdatedAt, rate.getUpdatedAt()); // Should be updated
        }

        @Test
        @DisplayName("Should handle product relationship branches")
        void mfnTariffRate_ShouldHandleProductRelationshipBranches() {
            MfnTariffRate rate = new MfnTariffRate();

            // Test null product
            assertNull(rate.getProduct());

            // Test setting product
            Product product1 = new Product("12345678", "Product 1");
            Product product2 = new Product("87654321", "Product 2");

            rate.setProduct(product1);
            assertEquals(product1, rate.getProduct());

            // Test changing product
            rate.setProduct(product2);
            assertEquals(product2, rate.getProduct());

            // Test setting back to null
            rate.setProduct(null);
            assertNull(rate.getProduct());
        }
    }

    @Nested
    @DisplayName("Country Branch Tests")
    class CountryBranchTests {

        @Test
        @DisplayName("Should handle different constructor branches")
        void country_ShouldHandleDifferentConstructorBranches() {
            // Test default constructor branch
            Country country1 = new Country();
            assertNotNull(country1.getCreatedAt());
            assertNull(country1.getCountryCode());
            assertNull(country1.getCountryName());

            // Test parameterized constructor branch
            Country country2 = new Country("US", "United States");
            assertNotNull(country2.getCreatedAt());
            assertEquals("US", country2.getCountryCode());
            assertEquals("United States", country2.getCountryName());
        }

        @Test
        @DisplayName("Should handle null and non-null field branches")
        void country_ShouldHandleNullAndNonNullFieldBranches() {
            Country country = new Country();

            // Test null branches
            assertNull(country.getCountryCode());
            assertNull(country.getCountryName());
            assertNull(country.getCountryNameShort());
            assertNull(country.getContinent());
            assertNull(country.getRegion());
            assertNull(country.getCurrency());

            // Test non-null branches
            country.setCountryCode("CA");
            country.setCountryName("Canada");
            country.setCountryNameShort("CAN");
            country.setContinent("North America");
            country.setRegion("North America");
            country.setCurrency("CAD");

            assertEquals("CA", country.getCountryCode());
            assertEquals("Canada", country.getCountryName());
            assertEquals("CAN", country.getCountryNameShort());
            assertEquals("North America", country.getContinent());
            assertEquals("North America", country.getRegion());
            assertEquals("CAD", country.getCurrency());
        }
    }

    @Nested
    @DisplayName("ProductNote Branch Tests")
    class ProductNoteBranchTests {

        @Test
        @DisplayName("Should handle different constructor branches")
        void productNote_ShouldHandleDifferentConstructorBranches() {
            // Test default constructor branch
            ProductNote note1 = new ProductNote();
            assertNotNull(note1.getCreatedAt());
            assertNotNull(note1.getUpdatedAt());
            assertNull(note1.getProduct());
            assertNull(note1.getNoteType());
            assertNull(note1.getNoteContent());

            // Test parameterized constructor branch
            Product product = new Product("12345678", "Test Product");
            ProductNote note2 = new ProductNote(product, "FOOTNOTE", "Test note content");
            assertNotNull(note2.getCreatedAt());
            assertNotNull(note2.getUpdatedAt());
            assertEquals(product, note2.getProduct());
            assertEquals("FOOTNOTE", note2.getNoteType());
            assertEquals("Test note content", note2.getNoteContent());
        }

        @Test
        @DisplayName("Should handle null and non-null field branches")
        void productNote_ShouldHandleNullAndNonNullFieldBranches() {
            ProductNote note = new ProductNote();

            // Test null branches
            assertNull(note.getProduct());
            assertNull(note.getNoteType());
            assertNull(note.getNoteContent());
            assertNull(note.getEffectiveDate());
            assertNull(note.getExpirationDate());

            // Test non-null branches
            Product product = new Product("87654321", "Test Product");
            LocalDate effectiveDate = LocalDate.of(2023, 1, 1);
            LocalDate expirationDate = LocalDate.of(2023, 12, 31);

            note.setProduct(product);
            note.setNoteType("ADDITIONAL_DUTY");
            note.setNoteContent("Additional duty applies");
            note.setEffectiveDate(effectiveDate);
            note.setExpirationDate(expirationDate);

            assertEquals(product, note.getProduct());
            assertEquals("ADDITIONAL_DUTY", note.getNoteType());
            assertEquals("Additional duty applies", note.getNoteContent());
            assertEquals(effectiveDate, note.getEffectiveDate());
            assertEquals(expirationDate, note.getExpirationDate());
        }

        @Test
        @DisplayName("Should handle different note types")
        void productNote_ShouldHandleDifferentNoteTypes() {
            ProductNote note = new ProductNote();

            // Test different note type branches
            note.setNoteType("FOOTNOTE");
            assertEquals("FOOTNOTE", note.getNoteType());

            note.setNoteType("ADDITIONAL_DUTY");
            assertEquals("ADDITIONAL_DUTY", note.getNoteType());

            note.setNoteType("QUOTA");
            assertEquals("QUOTA", note.getNoteType());

            note.setNoteType("RESTRICTION");
            assertEquals("RESTRICTION", note.getNoteType());
        }

        @Test
        @DisplayName("Should handle updatedAt field")
        void productNote_ShouldHandleUpdatedAtField() {
            ProductNote note = new ProductNote();
            assertNotNull(note.getUpdatedAt());

            // Test setting updatedAt
            note.setUpdatedAt(note.getCreatedAt().plusHours(1));
            assertNotNull(note.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("AgreementParticipant Branch Tests")
    class AgreementParticipantBranchTests {

        @Test
        @DisplayName("Should handle different constructor branches")
        void agreementParticipant_ShouldHandleDifferentConstructorBranches() {
            // Test default constructor branch
            AgreementParticipant participant1 = new AgreementParticipant();
            assertNotNull(participant1.getCreatedAt());
            assertNull(participant1.getAgreement());
            assertNull(participant1.getCountry());
            assertNull(participant1.getParticipantType());

            // Test parameterized constructor branch
            TradeAgreement agreement = new TradeAgreement("USMCA", "US-Mexico-Canada Agreement", "FTA");
            Country country = new Country("US", "United States");
            AgreementParticipant participant2 = new AgreementParticipant(agreement, country, "PARTNER");
            assertNotNull(participant2.getCreatedAt());
            assertEquals(agreement, participant2.getAgreement());
            assertEquals(country, participant2.getCountry());
            assertEquals("PARTNER", participant2.getParticipantType());
        }

        @Test
        @DisplayName("Should handle null and non-null field branches")
        void agreementParticipant_ShouldHandleNullAndNonNullFieldBranches() {
            AgreementParticipant participant = new AgreementParticipant();

            // Test null branches
            assertNull(participant.getAgreement());
            assertNull(participant.getCountry());
            assertNull(participant.getParticipantType());

            // Test non-null branches
            TradeAgreement agreement = new TradeAgreement("CPTPP", "Comprehensive and Progressive Agreement for Trans-Pacific Partnership", "FTA");
            Country country = new Country("JP", "Japan");

            participant.setAgreement(agreement);
            participant.setCountry(country);
            participant.setParticipantType("EXCLUDED");

            assertEquals(agreement, participant.getAgreement());
            assertEquals(country, participant.getCountry());
            assertEquals("EXCLUDED", participant.getParticipantType());
        }

        @Test
        @DisplayName("Should handle different participant types")
        void agreementParticipant_ShouldHandleDifferentParticipantTypes() {
            AgreementParticipant participant = new AgreementParticipant();

            // Test different participant type branches
            participant.setParticipantType("PARTNER");
            assertEquals("PARTNER", participant.getParticipantType());

            participant.setParticipantType("EXCLUDED");
            assertEquals("EXCLUDED", participant.getParticipantType());

            participant.setParticipantType("OBSERVER");
            assertEquals("OBSERVER", participant.getParticipantType());
        }
    }

    @Nested
    @DisplayName("CurrencyExchangeRate Branch Tests")
    class CurrencyExchangeRateBranchTests {

        @Test
        @DisplayName("Should handle default constructor branch")
        void currencyExchangeRate_ShouldHandleDefaultConstructorBranch() {
            CurrencyExchangeRate rate = new CurrencyExchangeRate();
            assertNotNull(rate.getCreatedAt());
            assertNotNull(rate.getUpdatedAt());
            assertNull(rate.getBaseCurrencyCode());
            assertNull(rate.getTargetCurrencyCode());
            assertNull(rate.getExchangeRate());
            assertEquals("SPOT", rate.getRateType()); // Default value
            assertNull(rate.getSource());
            assertNull(rate.getEffectiveDate());
        }

        @Test
        @DisplayName("Should handle null and non-null field branches")
        void currencyExchangeRate_ShouldHandleNullAndNonNullFieldBranches() {
            CurrencyExchangeRate rate = new CurrencyExchangeRate();

            // Test null branches
            assertNull(rate.getBaseCurrencyCode());
            assertNull(rate.getTargetCurrencyCode());
            assertNull(rate.getExchangeRate());
            assertNull(rate.getSource());
            assertNull(rate.getEffectiveDate());

            // Test non-null branches
            LocalDate effectiveDate = LocalDate.of(2023, 1, 1);

            rate.setBaseCurrencyCode("USD");
            rate.setTargetCurrencyCode("EUR");
            rate.setExchangeRate(new BigDecimal("0.85"));
            rate.setRateType("FORWARD");
            rate.setSource("ECB");
            rate.setEffectiveDate(effectiveDate);

            assertEquals("USD", rate.getBaseCurrencyCode());
            assertEquals("EUR", rate.getTargetCurrencyCode());
            assertEquals(new BigDecimal("0.85"), rate.getExchangeRate());
            assertEquals("FORWARD", rate.getRateType());
            assertEquals("ECB", rate.getSource());
            assertEquals(effectiveDate, rate.getEffectiveDate());
        }

        @Test
        @DisplayName("Should handle different rate types")
        void currencyExchangeRate_ShouldHandleDifferentRateTypes() {
            CurrencyExchangeRate rate = new CurrencyExchangeRate();

            // Test different rate type branches
            rate.setRateType("SPOT");
            assertEquals("SPOT", rate.getRateType());

            rate.setRateType("FORWARD");
            assertEquals("FORWARD", rate.getRateType());

            rate.setRateType("SWAP");
            assertEquals("SWAP", rate.getRateType());

            rate.setRateType("CROSS");
            assertEquals("CROSS", rate.getRateType());
        }

        @Test
        @DisplayName("Should handle ID field")
        void currencyExchangeRate_ShouldHandleIdField() {
            CurrencyExchangeRate rate = new CurrencyExchangeRate();
            assertNull(rate.getId());

            // Test setting ID
            rate.setId(1L);
            assertEquals(1L, rate.getId());
        }

        @Test
        @DisplayName("Should handle createdAt and updatedAt fields")
        void currencyExchangeRate_ShouldHandleTimestampFields() {
            CurrencyExchangeRate rate = new CurrencyExchangeRate();
            assertNotNull(rate.getCreatedAt());
            assertNotNull(rate.getUpdatedAt());

            // Test setting timestamps
            rate.setCreatedAt(rate.getCreatedAt().minusHours(1));
            rate.setUpdatedAt(rate.getUpdatedAt().plusHours(1));
            assertNotNull(rate.getCreatedAt());
            assertNotNull(rate.getUpdatedAt());
        }

        @Test
        @DisplayName("Should handle different currency codes")
        void currencyExchangeRate_ShouldHandleDifferentCurrencyCodes() {
            CurrencyExchangeRate rate = new CurrencyExchangeRate();

            // Test different currency code combinations
            rate.setBaseCurrencyCode("USD");
            rate.setTargetCurrencyCode("CAD");
            assertEquals("USD", rate.getBaseCurrencyCode());
            assertEquals("CAD", rate.getTargetCurrencyCode());

            rate.setBaseCurrencyCode("EUR");
            rate.setTargetCurrencyCode("GBP");
            assertEquals("EUR", rate.getBaseCurrencyCode());
            assertEquals("GBP", rate.getTargetCurrencyCode());

            rate.setBaseCurrencyCode("JPY");
            rate.setTargetCurrencyCode("CNY");
            assertEquals("JPY", rate.getBaseCurrencyCode());
            assertEquals("CNY", rate.getTargetCurrencyCode());
        }

        @Test
        @DisplayName("Should handle different exchange rates")
        void currencyExchangeRate_ShouldHandleDifferentExchangeRates() {
            CurrencyExchangeRate rate = new CurrencyExchangeRate();

            // Test different exchange rate values
            rate.setExchangeRate(new BigDecimal("1.0"));
            assertEquals(new BigDecimal("1.0"), rate.getExchangeRate());

            rate.setExchangeRate(new BigDecimal("0.85"));
            assertEquals(new BigDecimal("0.85"), rate.getExchangeRate());

            rate.setExchangeRate(new BigDecimal("110.50"));
            assertEquals(new BigDecimal("110.50"), rate.getExchangeRate());

            rate.setExchangeRate(new BigDecimal("0.0001"));
            assertEquals(new BigDecimal("0.0001"), rate.getExchangeRate());
        }
    }

}
