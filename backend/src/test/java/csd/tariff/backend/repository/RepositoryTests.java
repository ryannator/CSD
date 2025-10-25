package csd.tariff.backend.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import csd.tariff.backend.model.Product;
import csd.tariff.backend.model.TariffCalculation;
import csd.tariff.backend.model.User;

/**
 * Repository integration tests using JPA Test
 * Tests database operations and queries
 */
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("Repository Tests")
class RepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TariffCalculationRepository tariffCalculationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MfnTariffRateRepository mfnTariffRateRepository;

    @Autowired
    private TradeAgreementRepository tradeAgreementRepository;

    @Autowired
    private AgreementRateRepository agreementRateRepository;

    @Autowired
    private UserRepository userRepository;

    private Product testProduct;
    private TariffCalculation testCalculation;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Create test product
        testProduct = new Product();
        testProduct.setHts8("12345678");
        testProduct.setBriefDescription("Test Product");
        testProduct = entityManager.persistAndFlush(testProduct);

        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(User.Role.USER);
        testUser = entityManager.persistAndFlush(testUser);

        // Create test calculation
        testCalculation = new TariffCalculation();
        testCalculation.setHtsCode("12345678");
        testCalculation.setCountryCode("US");
        testCalculation.setProductValue(new BigDecimal("1000.00"));
        testCalculation.setQuantity(1);
        testCalculation.setCalculationType("MFN");
        testCalculation.setCalculationResult(new BigDecimal("50.00"));
        testCalculation = entityManager.persistAndFlush(testCalculation);
    }

    @Test
    @DisplayName("Should find tariff calculation by ID")
    void findTariffCalculationById_ShouldReturnCalculation() {
        // When
        Optional<TariffCalculation> result = tariffCalculationRepository.findById(testCalculation.getId());

        // Then
        assertTrue(result.isPresent());
        assertEquals(testCalculation.getId(), result.get().getId());
        assertEquals("12345678", result.get().getHtsCode());
        assertEquals("US", result.get().getCountryCode());
    }

    @Test
    @DisplayName("Should find all tariff calculations")
    void findAllTariffCalculations_ShouldReturnAllCalculations() {
        // When
        List<TariffCalculation> results = tariffCalculationRepository.findAll();

        // Then
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(c -> c.getId().equals(testCalculation.getId())));
    }

    @Test
    @DisplayName("Should find tariff calculations by HTS code")
    void findTariffCalculationsByHtsCode_ShouldReturnMatchingCalculations() {
        // When
        List<TariffCalculation> results = tariffCalculationRepository.findByHtsCode("12345678");

        // Then
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(c -> "12345678".equals(c.getHtsCode())));
    }

    @Test
    @DisplayName("Should find tariff calculations by country code")
    void findTariffCalculationsByCountryCode_ShouldReturnMatchingCalculations() {
        // When
        List<TariffCalculation> results = tariffCalculationRepository.findByCountryCode("US");

        // Then
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(c -> "US".equals(c.getCountryCode())));
    }

    @Test
    @DisplayName("Should save new tariff calculation")
    void saveTariffCalculation_ShouldPersistNewCalculation() {
        // Given
        TariffCalculation newCalculation = new TariffCalculation();
        newCalculation.setHtsCode("87654321");
        newCalculation.setCountryCode("CA");
        newCalculation.setProductValue(new BigDecimal("2000.00"));
        newCalculation.setQuantity(2);
        newCalculation.setCalculationType("MFN");
        newCalculation.setCalculationResult(new BigDecimal("100.00"));

        // When
        TariffCalculation saved = tariffCalculationRepository.save(newCalculation);
        entityManager.flush();

        // Then
        assertNotNull(saved.getId());
        assertEquals("87654321", saved.getHtsCode());
        assertEquals("CA", saved.getCountryCode());
        assertEquals(new BigDecimal("2000.00"), saved.getProductValue());
    }

    @Test
    @DisplayName("Should delete tariff calculation")
    void deleteTariffCalculation_ShouldRemoveFromDatabase() {
        // Given
        Long calculationId = testCalculation.getId();

        // When
        tariffCalculationRepository.deleteById(calculationId);
        entityManager.flush();

        // Then
        Optional<TariffCalculation> result = tariffCalculationRepository.findById(calculationId);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find product by HTS8 code")
    void findProductByHts8_ShouldReturnProduct() {
        // When
        Optional<Product> result = productRepository.findByHts8("12345678");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testProduct.getId(), result.get().getId());
        assertEquals("12345678", result.get().getHts8());
        assertEquals("Test Product", result.get().getBriefDescription());
    }

    @Test
    @DisplayName("Should find all products")
    void findAllProducts_ShouldReturnAllProducts() {
        // When
        List<Product> results = productRepository.findAll();

        // Then
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(p -> p.getId().equals(testProduct.getId())));
    }

    @Test
    @DisplayName("Should save new product")
    void saveProduct_ShouldPersistNewProduct() {
        // Given
        Product newProduct = new Product();
        newProduct.setHts8("87654321");
        newProduct.setBriefDescription("New Test Product");

        // When
        Product saved = productRepository.save(newProduct);
        entityManager.flush();

        // Then
        assertNotNull(saved.getId());
        assertEquals("87654321", saved.getHts8());
        assertEquals("New Test Product", saved.getBriefDescription());
    }

    @Test
    @DisplayName("Should find user by username")
    void findUserByUsername_ShouldReturnUser() {
        // When
        Optional<User> result = userRepository.findByUsername("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test@example.com", result.get().getEmail());
        assertEquals(User.Role.USER, result.get().getRole());
    }

    @Test
    @DisplayName("Should find user by email")
    void findUserByEmail_ShouldReturnUser() {
        // When
        Optional<User> result = userRepository.findByEmail("test@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    @DisplayName("Should find user by username")
    void findByUsername_ShouldReturnUser_WhenUserExists() {
        // When
        Optional<User> user = userRepository.findByUsername("testuser");

        // Then
        assertTrue(user.isPresent());
        assertEquals("testuser", user.get().getUsername());
    }

    @Test
    @DisplayName("Should find user by email")
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        // When
        Optional<User> user = userRepository.findByEmail("test@example.com");

        // Then
        assertTrue(user.isPresent());
        assertEquals("test@example.com", user.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty when user does not exist by username")
    void findByUsername_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // When
        Optional<User> user = userRepository.findByUsername("nonexistent");

        // Then
        assertFalse(user.isPresent());
    }

    @Test
    @DisplayName("Should return empty when user does not exist by email")
    void findByEmail_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // When
        Optional<User> user = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(user.isPresent());
    }

    @Test
    @DisplayName("Should save new user")
    void saveUser_ShouldPersistNewUser() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("newPassword");
        newUser.setRole(User.Role.ADMIN);

        // When
        User saved = userRepository.save(newUser);
        entityManager.flush();

        // Then
        assertNotNull(saved.getId());
        assertEquals("newuser", saved.getUsername());
        assertEquals("new@example.com", saved.getEmail());
        assertEquals(User.Role.ADMIN, saved.getRole());
    }

    @Test
    @DisplayName("Should handle null values in queries")
    void shouldHandleNullValuesInQueries() {
        // When & Then
        assertDoesNotThrow(() -> {
            tariffCalculationRepository.findByHtsCode(null);
            tariffCalculationRepository.findByCountryCode(null);
            productRepository.findByHts8(null);
            userRepository.findByUsername(null);
            userRepository.findByEmail(null);
        });
    }
}
