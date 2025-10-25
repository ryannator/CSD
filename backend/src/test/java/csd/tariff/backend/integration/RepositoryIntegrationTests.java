package csd.tariff.backend.integration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import csd.tariff.backend.model.AgreementRate;
import csd.tariff.backend.model.Country;
import csd.tariff.backend.model.MfnTariffRate;
import csd.tariff.backend.model.Product;
import csd.tariff.backend.model.TariffCalculation;
import csd.tariff.backend.model.TradeAgreement;
import csd.tariff.backend.model.User;
import csd.tariff.backend.repository.AgreementRateRepository;
import csd.tariff.backend.repository.CountryRepository;
import csd.tariff.backend.repository.MfnTariffRateRepository;
import csd.tariff.backend.repository.ProductRepository;
import csd.tariff.backend.repository.TariffCalculationRepository;
import csd.tariff.backend.repository.TradeAgreementRepository;
import csd.tariff.backend.repository.UserRepository;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Repository Integration Tests")
class RepositoryIntegrationTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TariffCalculationRepository tariffCalculationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private MfnTariffRateRepository mfnTariffRateRepository;

    @Autowired
    private AgreementRateRepository agreementRateRepository;

    @Autowired
    private TradeAgreementRepository tradeAgreementRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should persist and retrieve tariff calculation with all relationships")
    void shouldPersistAndRetrieveTariffCalculationWithAllRelationships() {
        // Arrange - Create related entities
        Product product = createTestProduct("11111111", "Integration Test Product");
        Country country = createTestCountry("US", "United States");
        User user = createTestUser("test@example.com", "testuser");
        
        // Persist entities first
        entityManager.persistAndFlush(product);
        entityManager.persistAndFlush(country);
        entityManager.persistAndFlush(user);
        
        // Create MfnTariffRate with product relationship
        MfnTariffRate mfnRate = createTestMfnRate(product, 0.10, 1.50);
        entityManager.persistAndFlush(mfnRate);

        // Create tariff calculation
        TariffCalculation calculation = new TariffCalculation();
        calculation.setHtsCode("11111111");
        calculation.setCountryCode("US"); // Required field
        calculation.setOriginCountry("US");
        calculation.setDestinationCountry("CA");
        calculation.setProductValue(new BigDecimal("1000.00"));
        calculation.setQuantity(10);
        calculation.setCurrency("USD");
        calculation.setTariffEffectiveDate(LocalDate.of(2023, 1, 1));
        calculation.setTariffExpirationDate(LocalDate.of(2023, 12, 31));
        calculation.setTotalTariffAmount(new BigDecimal("250.00"));
        calculation.setCalculationType("MFN");
        calculation.setCalculationResult(new BigDecimal("200.00"));

        // Act - Persist and retrieve
        TariffCalculation saved = tariffCalculationRepository.save(calculation);
        entityManager.flush();
        entityManager.clear();

        Optional<TariffCalculation> retrieved = tariffCalculationRepository.findById(saved.getId());

        // Assert
        assertTrue(retrieved.isPresent());
        TariffCalculation result = retrieved.get();
        assertEquals("11111111", result.getHtsCode());
        assertEquals("US", result.getOriginCountry());
        assertEquals("CA", result.getDestinationCountry());
        assertEquals(new BigDecimal("1000.00"), result.getProductValue());
        assertEquals(10, result.getQuantity());
        assertEquals("USD", result.getCurrency());
        assertEquals(LocalDate.of(2023, 1, 1), result.getTariffEffectiveDate());
        assertEquals(LocalDate.of(2023, 12, 31), result.getTariffExpirationDate());
        assertEquals(new BigDecimal("250.00"), result.getTotalTariffAmount());
        assertEquals("MFN", result.getCalculationType());
        assertEquals(new BigDecimal("200.00"), result.getCalculationResult());
    }

    @Test
    @DisplayName("Should handle complex trade agreement relationships")
    void shouldHandleComplexTradeAgreementRelationships() {
        // Arrange - Create related entities first
        Product product = createTestProduct("11111111", "Test Product");
        Country country = createTestCountry("CA", "Canada");
        
        entityManager.persistAndFlush(product);
        entityManager.persistAndFlush(country);
        
        // Create trade agreement
        TradeAgreement agreement = new TradeAgreement();
        agreement.setAgreementCode("USMCA");
        agreement.setAgreementName("US-Mexico-Canada Agreement");
        agreement.setAgreementType("FTA"); // Required field
        agreement.setEffectiveDate(LocalDate.of(2020, 7, 1));
        agreement.setExpirationDate(LocalDate.of(2030, 6, 30));
        
        entityManager.persistAndFlush(agreement);

        // Create agreement rates
        AgreementRate rate1 = new AgreementRate();
        rate1.setProduct(product);
        rate1.setAgreement(agreement);
        rate1.setCountry(country);
        rate1.setadValoremRate(new BigDecimal("0.05"));
        rate1.setSpecificRate(new BigDecimal("0.50"));
        rate1.setEffectiveDate(LocalDate.of(2020, 7, 1));
        rate1.setExpirationDate(LocalDate.of(2030, 6, 30));

        AgreementRate rate2 = new AgreementRate();
        rate2.setProduct(product);
        rate2.setAgreement(agreement);
        rate2.setCountry(country);
        rate2.setadValoremRate(new BigDecimal("0.08"));
        rate2.setSpecificRate(new BigDecimal("1.00"));
        rate2.setEffectiveDate(LocalDate.of(2020, 7, 1));
        rate2.setExpirationDate(LocalDate.of(2030, 6, 30));

        // Act - Persist rates
        entityManager.persistAndFlush(rate1);
        entityManager.persistAndFlush(rate2);
        entityManager.flush();
        entityManager.clear();

        // Assert - Verify relationships
        List<AgreementRate> rates = agreementRateRepository.findAll().stream()
            .filter(r -> r.getProduct().getHts8().equals("11111111") && 
                        r.getCountry().getCountryCode().equals("CA"))
            .toList();
        assertFalse(rates.isEmpty());
        assertEquals(2, rates.size()); // Both rates use the same product and country
        
                // Verify the first rate
                assertEquals(new BigDecimal("0.05").setScale(8), rates.get(0).getadValoremRate());
                assertEquals(new BigDecimal("0.50").setScale(6), rates.get(0).getSpecificRate());
        assertNotNull(rates.get(0).getAgreement());
        assertEquals("USMCA", rates.get(0).getAgreement().getAgreementCode());
        
        // Verify the second rate
        assertEquals(new BigDecimal("0.08").setScale(8), rates.get(1).getadValoremRate());
        assertEquals(new BigDecimal("1.00").setScale(6), rates.get(1).getSpecificRate());
        assertNotNull(rates.get(1).getAgreement());
        assertEquals("USMCA", rates.get(1).getAgreement().getAgreementCode());

        List<AgreementRate> allRates = agreementRateRepository.findAll().stream()
            .filter(r -> r.getAgreement().getAgreementCode().equals("USMCA"))
            .toList();
        assertEquals(2, allRates.size());
    }

    @Test
    @DisplayName("Should maintain data consistency across multiple repositories")
    void shouldMaintainDataConsistencyAcrossMultipleRepositories() {
        // Arrange - Create test data
        Product product1 = createTestProduct("11111111", "Product 1");
        Product product2 = createTestProduct("22222222", "Product 2");
        Country country1 = createTestCountry("US", "United States");
        Country country2 = createTestCountry("CA", "Canada");
        User user1 = createTestUser("user1@example.com", "User 1");
        User user2 = createTestUser("user2@example.com", "User 2");

        // Act - Persist all entities
        entityManager.persistAndFlush(product1);
        entityManager.persistAndFlush(product2);
        entityManager.persistAndFlush(country1);
        entityManager.persistAndFlush(country2);
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.flush();
        entityManager.clear();

        // Assert - Verify data consistency
        List<Product> products = productRepository.findAll();
        assertEquals(2, products.size());
        assertTrue(products.stream().anyMatch(p -> "11111111".equals(p.getHts8())));
        assertTrue(products.stream().anyMatch(p -> "22222222".equals(p.getHts8())));

        List<Country> countries = countryRepository.findAll();
        assertEquals(2, countries.size());
        assertTrue(countries.stream().anyMatch(c -> "US".equals(c.getCountryCode())));
        assertTrue(countries.stream().anyMatch(c -> "CA".equals(c.getCountryCode())));

        List<User> users = userRepository.findAll();
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> "user1@example.com".equals(u.getEmail())));
        assertTrue(users.stream().anyMatch(u -> "user2@example.com".equals(u.getEmail())));
    }

    @Test
    @DisplayName("Should handle cascading operations correctly")
    void shouldHandleCascadingOperationsCorrectly() {
        // Arrange - Create tariff calculation
        TariffCalculation calculation = new TariffCalculation();
        calculation.setHtsCode("33333333");
        calculation.setCountryCode("US"); // Required field
        calculation.setOriginCountry("US");
        calculation.setDestinationCountry("MX");
        calculation.setProductValue(new BigDecimal("500.00"));
        calculation.setQuantity(5);
        calculation.setCurrency("USD");
        calculation.setTotalTariffAmount(new BigDecimal("50.00"));
        calculation.setCalculationType("MFN");
        calculation.setCalculationResult(new BigDecimal("40.00"));

        // Act - Save and then delete
        TariffCalculation saved = tariffCalculationRepository.save(calculation);
        entityManager.flush();
        
        Long id = saved.getId();
        assertNotNull(id);
        
        tariffCalculationRepository.delete(saved);
        entityManager.flush();
        entityManager.clear();

        // Assert - Verify deletion
        Optional<TariffCalculation> deleted = tariffCalculationRepository.findById(id);
        assertFalse(deleted.isPresent());
    }

    @Test
    @DisplayName("Should handle bulk operations efficiently")
    void shouldHandleBulkOperationsEfficiently() {
        // Arrange - Create multiple calculations
        for (int i = 1; i <= 10; i++) {
            TariffCalculation calculation = new TariffCalculation();
            calculation.setHtsCode(String.format("%08d", i));
            calculation.setCountryCode("US"); // Required field
            calculation.setOriginCountry("US");
            calculation.setDestinationCountry("CA");
            calculation.setProductValue(new BigDecimal("100.00"));
            calculation.setQuantity(1);
            calculation.setCurrency("USD");
            calculation.setTotalTariffAmount(new BigDecimal("10.00"));
            calculation.setCalculationType("MFN");
            calculation.setCalculationResult(new BigDecimal("8.00"));
            
            tariffCalculationRepository.save(calculation);
        }

        // Act - Flush and clear
        entityManager.flush();
        entityManager.clear();

        // Assert - Verify all were saved
        List<TariffCalculation> allCalculations = tariffCalculationRepository.findAll();
        assertEquals(10, allCalculations.size());
        
        // Verify specific calculations exist
        assertTrue(allCalculations.stream().anyMatch(c -> "00000001".equals(c.getHtsCode())));
        assertTrue(allCalculations.stream().anyMatch(c -> "00000010".equals(c.getHtsCode())));
    }

    @Test
    @DisplayName("Should handle date range queries correctly")
    void shouldHandleDateRangeQueriesCorrectly() {
        // Arrange - Create calculations with different dates
        TariffCalculation calculation1 = new TariffCalculation();
        calculation1.setHtsCode("11111111");
        calculation1.setCountryCode("US"); // Required field
        calculation1.setOriginCountry("US");
        calculation1.setDestinationCountry("CA");
        calculation1.setProductValue(new BigDecimal("100.00"));
        calculation1.setQuantity(1);
        calculation1.setCurrency("USD");
        calculation1.setTariffEffectiveDate(LocalDate.of(2023, 1, 1));
        calculation1.setTariffExpirationDate(LocalDate.of(2023, 6, 30));
        calculation1.setTotalTariffAmount(new BigDecimal("10.00"));
        calculation1.setCalculationType("MFN");
        calculation1.setCalculationResult(new BigDecimal("8.00"));

        TariffCalculation calculation2 = new TariffCalculation();
        calculation2.setHtsCode("22222222");
        calculation2.setCountryCode("US"); // Required field
        calculation2.setOriginCountry("US");
        calculation2.setDestinationCountry("CA");
        calculation2.setProductValue(new BigDecimal("200.00"));
        calculation2.setQuantity(2);
        calculation2.setCurrency("USD");
        calculation2.setTariffEffectiveDate(LocalDate.of(2023, 7, 1));
        calculation2.setTariffExpirationDate(LocalDate.of(2023, 12, 31));
        calculation2.setTotalTariffAmount(new BigDecimal("20.00"));
        calculation2.setCalculationType("MFN");
        calculation2.setCalculationResult(new BigDecimal("16.00"));

        // Act - Persist calculations
        tariffCalculationRepository.save(calculation1);
        tariffCalculationRepository.save(calculation2);
        entityManager.flush();
        entityManager.clear();

        // Assert - Verify date-based queries work
        List<TariffCalculation> firstHalf = tariffCalculationRepository.findAll().stream()
            .filter(c -> c.getTariffEffectiveDate().isBefore(LocalDate.of(2023, 7, 1)))
            .toList();
        assertEquals(1, firstHalf.size());
        assertEquals("11111111", firstHalf.get(0).getHtsCode());

        List<TariffCalculation> secondHalf = tariffCalculationRepository.findAll().stream()
            .filter(c -> c.getTariffEffectiveDate().isAfter(LocalDate.of(2023, 6, 30)))
            .toList();
        assertEquals(1, secondHalf.size());
        assertEquals("22222222", secondHalf.get(0).getHtsCode());
    }

    // Helper methods
    private Product createTestProduct(String hts8, String description) {
        Product product = new Product();
        product.setHts8(hts8);
        product.setBriefDescription(description);
        return product;
    }

    private Country createTestCountry(String countryCode, String countryName) {
        Country country = new Country();
        country.setCountryCode(countryCode);
        country.setCountryName(countryName);
        return country;
    }

    private User createTestUser(String email, String username) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword("password123");
        user.setRole(User.Role.USER);
        return user;
    }

    private MfnTariffRate createTestMfnRate(Product product, Double adValoremRate, Double specificRate) {
        MfnTariffRate rate = new MfnTariffRate();
        rate.setProduct(product); // Required relationship
        rate.setMfnadValoremRate(new BigDecimal(adValoremRate.toString()));
        rate.setMfnSpecificRate(new BigDecimal(specificRate.toString()));
        return rate;
    }
}
