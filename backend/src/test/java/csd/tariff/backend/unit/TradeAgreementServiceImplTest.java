package csd.tariff.backend.unit;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import csd.tariff.backend.model.TradeAgreement;
import csd.tariff.backend.repository.TradeAgreementRepository;
import csd.tariff.backend.service.TradeAgreementServiceImpl;

/**
 * Comprehensive unit tests for TradeAgreementServiceImpl
 * Tests all CRUD operations, business logic, and exception handling
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TradeAgreementService Unit Tests")
class TradeAgreementServiceImplTest {

    @Mock
    private TradeAgreementRepository tradeAgreementRepository;

    @InjectMocks
    private TradeAgreementServiceImpl tradeAgreementService;

    private TradeAgreement testAgreement1;
    private TradeAgreement testAgreement2;
    private TradeAgreement testAgreement3;

    @BeforeEach
    void setUp() {
        // Setup test agreement 1 - Active USMCA
        testAgreement1 = new TradeAgreement();
        testAgreement1.setAgreementCode("USMCA");
        testAgreement1.setAgreementName("US-Mexico-Canada Agreement");
        testAgreement1.setAgreementType("FTA");
        testAgreement1.setEffectiveDate(LocalDate.of(2020, 7, 1));
        testAgreement1.setExpirationDate(LocalDate.of(2030, 6, 30));
        testAgreement1.setIsMultilateral(false);

        // Setup test agreement 2 - Active GSP
        testAgreement2 = new TradeAgreement();
        testAgreement2.setAgreementCode("GSP");
        testAgreement2.setAgreementName("Generalized System of Preferences");
        testAgreement2.setAgreementType("PREF");
        testAgreement2.setEffectiveDate(LocalDate.of(2018, 1, 1));
        testAgreement2.setExpirationDate(LocalDate.of(2025, 12, 31));
        testAgreement2.setIsMultilateral(true);

        // Setup test agreement 3 - Expired agreement
        testAgreement3 = new TradeAgreement();
        testAgreement3.setAgreementCode("NAFTA");
        testAgreement3.setAgreementName("North American Free Trade Agreement");
        testAgreement3.setAgreementType("FTA");
        testAgreement3.setEffectiveDate(LocalDate.of(1994, 1, 1));
        testAgreement3.setExpirationDate(LocalDate.of(2020, 6, 30));
        testAgreement3.setIsMultilateral(false);
    }

    // ===== GET ALL TRADE AGREEMENTS TESTS =====
    
    @Test
    @DisplayName("Should return all trade agreements when agreements exist")
    void getAllTradeAgreements_ShouldReturnAllAgreements_WhenAgreementsExist() {
        // Arrange
        List<TradeAgreement> expectedAgreements = Arrays.asList(testAgreement1, testAgreement2, testAgreement3);
        when(tradeAgreementRepository.findAll()).thenReturn(expectedAgreements);

        // Act
        List<TradeAgreement> actualAgreements = tradeAgreementService.getAllTradeAgreements();

        // Assert
        assertNotNull(actualAgreements);
        assertEquals(3, actualAgreements.size());
        assertEquals(expectedAgreements, actualAgreements);
        verify(tradeAgreementRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no agreements exist")
    void getAllTradeAgreements_ShouldReturnEmptyList_WhenNoAgreementsExist() {
        // Arrange
        when(tradeAgreementRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<TradeAgreement> actualAgreements = tradeAgreementService.getAllTradeAgreements();

        // Assert
        assertNotNull(actualAgreements);
        assertTrue(actualAgreements.isEmpty());
        verify(tradeAgreementRepository, times(1)).findAll();
    }

    // ===== GET TRADE AGREEMENT BY ID TESTS =====
    
    @Test
    @DisplayName("Should return trade agreement when agreement exists")
    void getTradeAgreementById_ShouldReturnAgreement_WhenAgreementExists() {
        // Arrange
        Long agreementId = 1L;
        when(tradeAgreementRepository.findById(agreementId)).thenReturn(Optional.of(testAgreement1));

        // Act
        Optional<TradeAgreement> actualAgreement = tradeAgreementService.getTradeAgreementById(agreementId);

        // Assert
        assertTrue(actualAgreement.isPresent());
        assertEquals(testAgreement1, actualAgreement.get());
        assertEquals("USMCA", actualAgreement.get().getAgreementCode());
        assertEquals("US-Mexico-Canada Agreement", actualAgreement.get().getAgreementName());
        verify(tradeAgreementRepository, times(1)).findById(agreementId);
    }

    @Test
    @DisplayName("Should return empty when trade agreement does not exist")
    void getTradeAgreementById_ShouldReturnEmpty_WhenAgreementDoesNotExist() {
        // Arrange
        Long nonExistentId = 999L;
        when(tradeAgreementRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        Optional<TradeAgreement> actualAgreement = tradeAgreementService.getTradeAgreementById(nonExistentId);

        // Assert
        assertFalse(actualAgreement.isPresent());
        verify(tradeAgreementRepository, times(1)).findById(nonExistentId);
    }

    // ===== GET TRADE AGREEMENT BY CODE TESTS =====
    
    @Test
    @DisplayName("Should return trade agreement when agreement code exists")
    void getTradeAgreementByCode_ShouldReturnAgreement_WhenCodeExists() {
        // Arrange
        String agreementCode = "USMCA";
        when(tradeAgreementRepository.findByAgreementCode(agreementCode)).thenReturn(Optional.of(testAgreement1));

        // Act
        Optional<TradeAgreement> actualAgreement = tradeAgreementService.getTradeAgreementByCode(agreementCode);

        // Assert
        assertTrue(actualAgreement.isPresent());
        assertEquals(testAgreement1, actualAgreement.get());
        verify(tradeAgreementRepository, times(1)).findByAgreementCode(agreementCode);
    }

    @Test
    @DisplayName("Should return empty when trade agreement code does not exist")
    void getTradeAgreementByCode_ShouldReturnEmpty_WhenCodeDoesNotExist() {
        // Arrange
        String nonExistentCode = "NONEXISTENT";
        when(tradeAgreementRepository.findByAgreementCode(nonExistentCode)).thenReturn(Optional.empty());

        // Act
        Optional<TradeAgreement> actualAgreement = tradeAgreementService.getTradeAgreementByCode(nonExistentCode);

        // Assert
        assertFalse(actualAgreement.isPresent());
        verify(tradeAgreementRepository, times(1)).findByAgreementCode(nonExistentCode);
    }

    // ===== GET TRADE AGREEMENT BY NAME TESTS =====
    
    @Test
    @DisplayName("Should return trade agreement when agreement name exists")
    void getTradeAgreementByName_ShouldReturnAgreement_WhenNameExists() {
        // Arrange
        String agreementName = "US-Mexico-Canada Agreement";
        when(tradeAgreementRepository.findByAgreementName(agreementName)).thenReturn(Optional.of(testAgreement1));

        // Act
        Optional<TradeAgreement> actualAgreement = tradeAgreementService.getTradeAgreementByName(agreementName);

        // Assert
        assertTrue(actualAgreement.isPresent());
        assertEquals(testAgreement1, actualAgreement.get());
        verify(tradeAgreementRepository, times(1)).findByAgreementName(agreementName);
    }

    @Test
    @DisplayName("Should return empty when trade agreement name does not exist")
    void getTradeAgreementByName_ShouldReturnEmpty_WhenNameDoesNotExist() {
        // Arrange
        String nonExistentName = "Non-existent Agreement";
        when(tradeAgreementRepository.findByAgreementName(nonExistentName)).thenReturn(Optional.empty());

        // Act
        Optional<TradeAgreement> actualAgreement = tradeAgreementService.getTradeAgreementByName(nonExistentName);

        // Assert
        assertFalse(actualAgreement.isPresent());
        verify(tradeAgreementRepository, times(1)).findByAgreementName(nonExistentName);
    }

    // ===== GET ACTIVE TRADE AGREEMENTS TESTS =====
    
    @Test
    @DisplayName("Should return active trade agreements when active agreements exist")
    void getActiveTradeAgreements_ShouldReturnActiveAgreements_WhenActiveAgreementsExist() {
        // Arrange
        List<TradeAgreement> activeAgreements = Arrays.asList(testAgreement1, testAgreement2);
        when(tradeAgreementRepository.findActiveAgreements()).thenReturn(activeAgreements);

        // Act
        List<TradeAgreement> actualAgreements = tradeAgreementService.getActiveTradeAgreements();

        // Assert
        assertNotNull(actualAgreements);
        assertEquals(2, actualAgreements.size());
        assertEquals(activeAgreements, actualAgreements);
        verify(tradeAgreementRepository, times(1)).findActiveAgreements();
    }

    @Test
    @DisplayName("Should return empty list when no active agreements exist")
    void getActiveTradeAgreements_ShouldReturnEmptyList_WhenNoActiveAgreementsExist() {
        // Arrange
        when(tradeAgreementRepository.findActiveAgreements()).thenReturn(Collections.emptyList());

        // Act
        List<TradeAgreement> actualAgreements = tradeAgreementService.getActiveTradeAgreements();

        // Assert
        assertNotNull(actualAgreements);
        assertTrue(actualAgreements.isEmpty());
        verify(tradeAgreementRepository, times(1)).findActiveAgreements();
    }

    // ===== GET TRADE AGREEMENTS BY COUNTRY TESTS =====
    
    @Test
    @DisplayName("Should return trade agreements for specific country")
    void getTradeAgreementsByCountry_ShouldReturnAgreements_ForSpecificCountry() {
        // Arrange
        String countryCode = "US";
        List<TradeAgreement> countryAgreements = Arrays.asList(testAgreement1, testAgreement2);
        when(tradeAgreementRepository.findByParticipatingCountry(countryCode.toUpperCase())).thenReturn(countryAgreements);

        // Act
        List<TradeAgreement> actualAgreements = tradeAgreementService.getTradeAgreementsByCountry(countryCode);

        // Assert
        assertNotNull(actualAgreements);
        assertEquals(2, actualAgreements.size());
        assertEquals(countryAgreements, actualAgreements);
        verify(tradeAgreementRepository, times(1)).findByParticipatingCountry("US");
    }

    @Test
    @DisplayName("Should return empty list when no agreements exist for country")
    void getTradeAgreementsByCountry_ShouldReturnEmptyList_WhenNoAgreementsForCountry() {
        // Arrange
        String countryCode = "XX";
        when(tradeAgreementRepository.findByParticipatingCountry(countryCode.toUpperCase())).thenReturn(Collections.emptyList());

        // Act
        List<TradeAgreement> actualAgreements = tradeAgreementService.getTradeAgreementsByCountry(countryCode);

        // Assert
        assertNotNull(actualAgreements);
        assertTrue(actualAgreements.isEmpty());
        verify(tradeAgreementRepository, times(1)).findByParticipatingCountry("XX");
    }

    // ===== GET TRADE AGREEMENTS BETWEEN COUNTRIES TESTS =====
    
    @Test
    @DisplayName("Should return trade agreements between two countries")
    void getTradeAgreementsBetweenCountries_ShouldReturnAgreements_BetweenTwoCountries() {
        // Arrange
        String country1 = "US";
        String country2 = "CA";
        List<TradeAgreement> betweenAgreements = Arrays.asList(testAgreement1);
        when(tradeAgreementRepository.findBetweenCountries(country1.toUpperCase(), country2.toUpperCase()))
            .thenReturn(betweenAgreements);

        // Act
        List<TradeAgreement> actualAgreements = tradeAgreementService.getTradeAgreementsBetweenCountries(country1, country2);

        // Assert
        assertNotNull(actualAgreements);
        assertEquals(1, actualAgreements.size());
        assertEquals(betweenAgreements, actualAgreements);
        verify(tradeAgreementRepository, times(1)).findBetweenCountries("US", "CA");
    }

    @Test
    @DisplayName("Should return empty list when no agreements exist between countries")
    void getTradeAgreementsBetweenCountries_ShouldReturnEmptyList_WhenNoAgreementsBetweenCountries() {
        // Arrange
        String country1 = "US";
        String country2 = "XX";
        when(tradeAgreementRepository.findBetweenCountries(country1.toUpperCase(), country2.toUpperCase()))
            .thenReturn(Collections.emptyList());

        // Act
        List<TradeAgreement> actualAgreements = tradeAgreementService.getTradeAgreementsBetweenCountries(country1, country2);

        // Assert
        assertNotNull(actualAgreements);
        assertTrue(actualAgreements.isEmpty());
        verify(tradeAgreementRepository, times(1)).findBetweenCountries("US", "XX");
    }

    // ===== GET TRADE AGREEMENTS EFFECTIVE ON DATE TESTS =====
    
    @Test
    @DisplayName("Should return trade agreements effective on specific date")
    void getTradeAgreementsEffectiveOnDate_ShouldReturnAgreements_EffectiveOnDate() {
        // Arrange
        LocalDate effectiveDate = LocalDate.of(2021, 1, 1);
        List<TradeAgreement> effectiveAgreements = Arrays.asList(testAgreement1, testAgreement2);
        when(tradeAgreementRepository.findEffectiveOnDate(effectiveDate)).thenReturn(effectiveAgreements);

        // Act
        List<TradeAgreement> actualAgreements = tradeAgreementService.getTradeAgreementsEffectiveOnDate(effectiveDate);

        // Assert
        assertNotNull(actualAgreements);
        assertEquals(2, actualAgreements.size());
        assertEquals(effectiveAgreements, actualAgreements);
        verify(tradeAgreementRepository, times(1)).findEffectiveOnDate(effectiveDate);
    }

    @Test
    @DisplayName("Should return empty list when no agreements effective on date")
    void getTradeAgreementsEffectiveOnDate_ShouldReturnEmptyList_WhenNoAgreementsEffectiveOnDate() {
        // Arrange
        LocalDate effectiveDate = LocalDate.of(1990, 1, 1);
        when(tradeAgreementRepository.findEffectiveOnDate(effectiveDate)).thenReturn(Collections.emptyList());

        // Act
        List<TradeAgreement> actualAgreements = tradeAgreementService.getTradeAgreementsEffectiveOnDate(effectiveDate);

        // Assert
        assertNotNull(actualAgreements);
        assertTrue(actualAgreements.isEmpty());
        verify(tradeAgreementRepository, times(1)).findEffectiveOnDate(effectiveDate);
    }

    // ===== GET APPLICABLE TRADE AGREEMENT TESTS =====
    
    @Test
    @DisplayName("Should return applicable trade agreement when active agreement exists")
    void getApplicableTradeAgreement_ShouldReturnAgreement_WhenActiveAgreementExists() {
        // Arrange
        String countryCode = "US";
        String htsCode = "12345678";
        List<TradeAgreement> countryAgreements = Arrays.asList(testAgreement1);
        when(tradeAgreementRepository.findByParticipatingCountry(countryCode.toUpperCase())).thenReturn(countryAgreements);

        // Act
        Optional<TradeAgreement> actualAgreement = tradeAgreementService.getApplicableTradeAgreement(countryCode, htsCode);

        // Assert
        assertTrue(actualAgreement.isPresent());
        assertEquals(testAgreement1, actualAgreement.get());
        verify(tradeAgreementRepository, times(1)).findByParticipatingCountry("US");
    }

    @Test
    @DisplayName("Should return empty when no active agreement exists")
    void getApplicableTradeAgreement_ShouldReturnEmpty_WhenNoActiveAgreementExists() {
        // Arrange
        String countryCode = "US";
        String htsCode = "12345678";
        List<TradeAgreement> countryAgreements = Arrays.asList(testAgreement3); // Expired agreement
        when(tradeAgreementRepository.findByParticipatingCountry(countryCode.toUpperCase())).thenReturn(countryAgreements);

        // Act
        Optional<TradeAgreement> actualAgreement = tradeAgreementService.getApplicableTradeAgreement(countryCode, htsCode);

        // Assert
        assertFalse(actualAgreement.isPresent());
        verify(tradeAgreementRepository, times(1)).findByParticipatingCountry("US");
    }

    // ===== GET TRADE AGREEMENTS EXPIRING ON DATE TESTS =====
    
    @Test
    @DisplayName("Should return trade agreements expiring on specific date")
    void getTradeAgreementsExpiringOnDate_ShouldReturnAgreements_ExpiringOnDate() {
        // Arrange
        LocalDate expirationDate = LocalDate.of(2025, 12, 31);
        List<TradeAgreement> expiringAgreements = Arrays.asList(testAgreement2);
        when(tradeAgreementRepository.findExpiringOnDate(expirationDate)).thenReturn(expiringAgreements);

        // Act
        List<TradeAgreement> actualAgreements = tradeAgreementService.getTradeAgreementsExpiringOnDate(expirationDate);

        // Assert
        assertNotNull(actualAgreements);
        assertEquals(1, actualAgreements.size());
        assertEquals(expiringAgreements, actualAgreements);
        verify(tradeAgreementRepository, times(1)).findExpiringOnDate(expirationDate);
    }

    // ===== GET TRADE AGREEMENTS ACTIVE BETWEEN DATES TESTS =====
    
    @Test
    @DisplayName("Should return trade agreements active between dates")
    void getTradeAgreementsActiveBetweenDates_ShouldReturnAgreements_ActiveBetweenDates() {
        // Arrange
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        List<TradeAgreement> activeAgreements = Arrays.asList(testAgreement1, testAgreement2);
        when(tradeAgreementRepository.findActiveBetweenDates(startDate, endDate)).thenReturn(activeAgreements);

        // Act
        List<TradeAgreement> actualAgreements = tradeAgreementService.getTradeAgreementsActiveBetweenDates(startDate, endDate);

        // Assert
        assertNotNull(actualAgreements);
        assertEquals(2, actualAgreements.size());
        assertEquals(activeAgreements, actualAgreements);
        verify(tradeAgreementRepository, times(1)).findActiveBetweenDates(startDate, endDate);
    }

    // ===== CREATE TRADE AGREEMENT TESTS =====
    
    @Test
    @DisplayName("Should create trade agreement successfully with valid data")
    void createTradeAgreement_ShouldCreateSuccessfully_WithValidData() {
        // Arrange
        TradeAgreement newAgreement = new TradeAgreement();
        newAgreement.setAgreementCode("NEWFTA");
        newAgreement.setAgreementName("New Free Trade Agreement");
        newAgreement.setAgreementType("FTA");
        newAgreement.setEffectiveDate(LocalDate.of(2024, 1, 1));
        newAgreement.setExpirationDate(LocalDate.of(2034, 12, 31));
        newAgreement.setIsMultilateral(false);
        
        when(tradeAgreementRepository.save(any(TradeAgreement.class))).thenAnswer(invocation -> {
            TradeAgreement agreement = invocation.getArgument(0);
            return agreement;
        });

        // Act
        TradeAgreement result = tradeAgreementService.createTradeAgreement(newAgreement);

        // Assert
        assertNotNull(result);
        assertEquals("NEWFTA", result.getAgreementCode());
        assertEquals("New Free Trade Agreement", result.getAgreementName());
        assertEquals("FTA", result.getAgreementType());
        assertFalse(result.getIsMultilateral());
        verify(tradeAgreementRepository, times(1)).save(any(TradeAgreement.class));
    }

    @Test
    @DisplayName("Should set default multilateral flag when not provided")
    void createTradeAgreement_ShouldSetDefaultMultilateralFlag_WhenNotProvided() {
        // Arrange
        TradeAgreement newAgreement = new TradeAgreement();
        newAgreement.setAgreementCode("NEWFTA");
        newAgreement.setAgreementName("New Free Trade Agreement");
        newAgreement.setIsMultilateral(null); // Not set
        
        when(tradeAgreementRepository.save(any(TradeAgreement.class))).thenAnswer(invocation -> {
            TradeAgreement agreement = invocation.getArgument(0);
            return agreement;
        });

        // Act
        TradeAgreement result = tradeAgreementService.createTradeAgreement(newAgreement);

        // Assert
        assertNotNull(result);
        assertFalse(result.getIsMultilateral()); // Should default to false
        verify(tradeAgreementRepository, times(1)).save(any(TradeAgreement.class));
    }

    @Test
    @DisplayName("Should throw exception when agreement name is null")
    void createTradeAgreement_ShouldThrowException_WhenAgreementNameIsNull() {
        // Arrange
        TradeAgreement newAgreement = new TradeAgreement();
        newAgreement.setAgreementCode("NEWFTA");
        newAgreement.setAgreementName(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            tradeAgreementService.createTradeAgreement(newAgreement));
        
        assertEquals("Agreement name is required", exception.getMessage());
        verify(tradeAgreementRepository, never()).save(any(TradeAgreement.class));
    }

    @Test
    @DisplayName("Should throw exception when agreement name is empty")
    void createTradeAgreement_ShouldThrowException_WhenAgreementNameIsEmpty() {
        // Arrange
        TradeAgreement newAgreement = new TradeAgreement();
        newAgreement.setAgreementCode("NEWFTA");
        newAgreement.setAgreementName("   "); // Empty string

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            tradeAgreementService.createTradeAgreement(newAgreement));
        
        assertEquals("Agreement name is required", exception.getMessage());
        verify(tradeAgreementRepository, never()).save(any(TradeAgreement.class));
    }

    @Test
    @DisplayName("Should throw exception when agreement code is null")
    void createTradeAgreement_ShouldThrowException_WhenAgreementCodeIsNull() {
        // Arrange
        TradeAgreement newAgreement = new TradeAgreement();
        newAgreement.setAgreementCode(null);
        newAgreement.setAgreementName("New Free Trade Agreement");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            tradeAgreementService.createTradeAgreement(newAgreement));
        
        assertEquals("Agreement code is required", exception.getMessage());
        verify(tradeAgreementRepository, never()).save(any(TradeAgreement.class));
    }

    @Test
    @DisplayName("Should throw exception when agreement code is empty")
    void createTradeAgreement_ShouldThrowException_WhenAgreementCodeIsEmpty() {
        // Arrange
        TradeAgreement newAgreement = new TradeAgreement();
        newAgreement.setAgreementCode("   "); // Empty string
        newAgreement.setAgreementName("New Free Trade Agreement");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            tradeAgreementService.createTradeAgreement(newAgreement));
        
        assertEquals("Agreement code is required", exception.getMessage());
        verify(tradeAgreementRepository, never()).save(any(TradeAgreement.class));
    }

    // ===== UPDATE TRADE AGREEMENT TESTS =====
    
    @Test
    @DisplayName("Should update trade agreement successfully when agreement exists")
    void updateTradeAgreement_ShouldUpdateSuccessfully_WhenAgreementExists() {
        // Arrange
        Long agreementId = 1L;
        TradeAgreement updateData = new TradeAgreement();
        updateData.setAgreementName("Updated USMCA Agreement");
        updateData.setAgreementCode("USMCA");
        updateData.setAgreementType("FTA");
        updateData.setEffectiveDate(LocalDate.of(2021, 1, 1));
        updateData.setExpirationDate(LocalDate.of(2031, 6, 30));
        updateData.setIsMultilateral(true);
        
        when(tradeAgreementRepository.findById(agreementId)).thenReturn(Optional.of(testAgreement1));
        when(tradeAgreementRepository.save(any(TradeAgreement.class))).thenAnswer(invocation -> {
            TradeAgreement agreement = invocation.getArgument(0);
            return agreement;
        });

        // Act
        TradeAgreement result = tradeAgreementService.updateTradeAgreement(agreementId, updateData);

        // Assert
        assertNotNull(result);
        assertEquals("Updated USMCA Agreement", result.getAgreementName());
        assertEquals("USMCA", result.getAgreementCode());
        assertEquals("FTA", result.getAgreementType());
        assertTrue(result.getIsMultilateral());
        verify(tradeAgreementRepository, times(1)).findById(agreementId);
        verify(tradeAgreementRepository, times(1)).save(any(TradeAgreement.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent agreement")
    void updateTradeAgreement_ShouldThrowException_WhenAgreementDoesNotExist() {
        // Arrange
        Long nonExistentId = 999L;
        TradeAgreement updateData = new TradeAgreement();
        updateData.setAgreementName("Updated Agreement");
        
        when(tradeAgreementRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            tradeAgreementService.updateTradeAgreement(nonExistentId, updateData));
        
        assertEquals("Trade agreement not found with ID: " + nonExistentId, exception.getMessage());
        verify(tradeAgreementRepository, times(1)).findById(nonExistentId);
        verify(tradeAgreementRepository, never()).save(any(TradeAgreement.class));
    }

    // ===== UPDATE TRADE AGREEMENT BY CODE TESTS =====
    
    @Test
    @DisplayName("Should update trade agreement by code successfully when agreement exists")
    void updateTradeAgreementByCode_ShouldUpdateSuccessfully_WhenAgreementExists() {
        // Arrange
        String agreementCode = "USMCA";
        TradeAgreement updateData = new TradeAgreement();
        updateData.setAgreementName("Updated USMCA Agreement");
        updateData.setAgreementCode("USMCA");
        updateData.setAgreementType("FTA");
        
        when(tradeAgreementRepository.findByAgreementCode(agreementCode)).thenReturn(Optional.of(testAgreement1));
        when(tradeAgreementRepository.save(any(TradeAgreement.class))).thenAnswer(invocation -> {
            TradeAgreement agreement = invocation.getArgument(0);
            return agreement;
        });

        // Act
        TradeAgreement result = tradeAgreementService.updateTradeAgreementByCode(agreementCode, updateData);

        // Assert
        assertNotNull(result);
        assertEquals("Updated USMCA Agreement", result.getAgreementName());
        assertEquals("USMCA", result.getAgreementCode());
        verify(tradeAgreementRepository, times(1)).findByAgreementCode(agreementCode);
        verify(tradeAgreementRepository, times(1)).save(any(TradeAgreement.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent agreement by code")
    void updateTradeAgreementByCode_ShouldThrowException_WhenAgreementDoesNotExist() {
        // Arrange
        String nonExistentCode = "NONEXISTENT";
        TradeAgreement updateData = new TradeAgreement();
        updateData.setAgreementName("Updated Agreement");
        
        when(tradeAgreementRepository.findByAgreementCode(nonExistentCode)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            tradeAgreementService.updateTradeAgreementByCode(nonExistentCode, updateData));
        
        assertEquals("Trade agreement not found with code: " + nonExistentCode, exception.getMessage());
        verify(tradeAgreementRepository, times(1)).findByAgreementCode(nonExistentCode);
        verify(tradeAgreementRepository, never()).save(any(TradeAgreement.class));
    }

    // ===== DELETE TRADE AGREEMENT BY CODE TESTS =====
    
    @Test
    @DisplayName("Should delete trade agreement by code successfully when agreement exists")
    void deleteTradeAgreementByCode_ShouldDeleteSuccessfully_WhenAgreementExists() {
        // Arrange
        String agreementCode = "USMCA";
        when(tradeAgreementRepository.findByAgreementCode(agreementCode)).thenReturn(Optional.of(testAgreement1));

        // Act
        tradeAgreementService.deleteTradeAgreementByCode(agreementCode);

        // Assert
        verify(tradeAgreementRepository, times(1)).findByAgreementCode(agreementCode);
        verify(tradeAgreementRepository, times(1)).deleteById(testAgreement1.getId());
    }

    // ===== ADDITIONAL BRANCH COVERAGE TESTS =====
    
    @Test
    @DisplayName("Should test branch: getApplicableTradeAgreement with null effective date")
    void getApplicableTradeAgreement_ShouldTestBranchWithNullEffectiveDate() {
        // Arrange
        String countryCode = "US";
        String htsCode = "12345678";
        
        TradeAgreement agreementWithNullEffectiveDate = new TradeAgreement();
        agreementWithNullEffectiveDate.setAgreementCode("TEST");
        agreementWithNullEffectiveDate.setAgreementName("Test Agreement");
        agreementWithNullEffectiveDate.setEffectiveDate(null); // Null effective date
        agreementWithNullEffectiveDate.setExpirationDate(LocalDate.now().plusYears(1));
        
        when(tradeAgreementRepository.findByParticipatingCountry("US")).thenReturn(Arrays.asList(agreementWithNullEffectiveDate));
        
        // Act
        Optional<TradeAgreement> result = tradeAgreementService.getApplicableTradeAgreement(countryCode, htsCode);
        
        // Assert
        assertFalse(result.isPresent()); // Should not be applicable due to null effective date
        verify(tradeAgreementRepository, times(1)).findByParticipatingCountry("US");
    }
    
    @Test
    @DisplayName("Should test branch: getApplicableTradeAgreement with null expiration date")
    void getApplicableTradeAgreement_ShouldTestBranchWithNullExpirationDate() {
        // Arrange
        String countryCode = "US";
        String htsCode = "12345678";
        
        TradeAgreement agreementWithNullExpirationDate = new TradeAgreement();
        agreementWithNullExpirationDate.setAgreementCode("TEST");
        agreementWithNullExpirationDate.setAgreementName("Test Agreement");
        agreementWithNullExpirationDate.setEffectiveDate(LocalDate.now().minusYears(1));
        agreementWithNullExpirationDate.setExpirationDate(null); // Null expiration date
        
        when(tradeAgreementRepository.findByParticipatingCountry("US")).thenReturn(Arrays.asList(agreementWithNullExpirationDate));
        
        // Act
        Optional<TradeAgreement> result = tradeAgreementService.getApplicableTradeAgreement(countryCode, htsCode);
        
        // Assert
        assertTrue(result.isPresent()); // Should be applicable due to null expiration date
        assertEquals("TEST", result.get().getAgreementCode());
        verify(tradeAgreementRepository, times(1)).findByParticipatingCountry("US");
    }
    
    @Test
    @DisplayName("Should test branch: getApplicableTradeAgreement with expired agreement")
    void getApplicableTradeAgreement_ShouldTestBranchWithExpiredAgreement() {
        // Arrange
        String countryCode = "US";
        String htsCode = "12345678";
        
        TradeAgreement expiredAgreement = new TradeAgreement();
        expiredAgreement.setAgreementCode("EXPIRED");
        expiredAgreement.setAgreementName("Expired Agreement");
        expiredAgreement.setEffectiveDate(LocalDate.now().minusYears(2));
        expiredAgreement.setExpirationDate(LocalDate.now().minusDays(1)); // Expired yesterday
        
        when(tradeAgreementRepository.findByParticipatingCountry("US")).thenReturn(Arrays.asList(expiredAgreement));
        
        // Act
        Optional<TradeAgreement> result = tradeAgreementService.getApplicableTradeAgreement(countryCode, htsCode);
        
        // Assert
        assertFalse(result.isPresent()); // Should not be applicable due to expiration
        verify(tradeAgreementRepository, times(1)).findByParticipatingCountry("US");
    }
    
    @Test
    @DisplayName("Should test branch: getApplicableTradeAgreement with future effective date")
    void getApplicableTradeAgreement_ShouldTestBranchWithFutureEffectiveDate() {
        // Arrange
        String countryCode = "US";
        String htsCode = "12345678";
        
        TradeAgreement futureAgreement = new TradeAgreement();
        futureAgreement.setAgreementCode("FUTURE");
        futureAgreement.setAgreementName("Future Agreement");
        futureAgreement.setEffectiveDate(LocalDate.now().plusDays(1)); // Effective tomorrow
        futureAgreement.setExpirationDate(LocalDate.now().plusYears(1));
        
        when(tradeAgreementRepository.findByParticipatingCountry("US")).thenReturn(Arrays.asList(futureAgreement));
        
        // Act
        Optional<TradeAgreement> result = tradeAgreementService.getApplicableTradeAgreement(countryCode, htsCode);
        
        // Assert
        assertFalse(result.isPresent()); // Should not be applicable due to future effective date
        verify(tradeAgreementRepository, times(1)).findByParticipatingCountry("US");
    }
    
    @Test
    @DisplayName("Should test branch: getApplicableTradeAgreement with multiple agreements - should return first applicable")
    void getApplicableTradeAgreement_ShouldTestBranchWithMultipleAgreements() {
        // Arrange
        String countryCode = "US";
        String htsCode = "12345678";
        
        TradeAgreement expiredAgreement = new TradeAgreement();
        expiredAgreement.setAgreementCode("EXPIRED");
        expiredAgreement.setAgreementName("Expired Agreement");
        expiredAgreement.setEffectiveDate(LocalDate.now().minusYears(2));
        expiredAgreement.setExpirationDate(LocalDate.now().minusDays(1));
        
        TradeAgreement activeAgreement1 = new TradeAgreement();
        activeAgreement1.setAgreementCode("ACTIVE1");
        activeAgreement1.setAgreementName("Active Agreement 1");
        activeAgreement1.setEffectiveDate(LocalDate.now().minusYears(1));
        activeAgreement1.setExpirationDate(LocalDate.now().plusYears(1));
        
        TradeAgreement activeAgreement2 = new TradeAgreement();
        activeAgreement2.setAgreementCode("ACTIVE2");
        activeAgreement2.setAgreementName("Active Agreement 2");
        activeAgreement2.setEffectiveDate(LocalDate.now().minusMonths(6));
        activeAgreement2.setExpirationDate(LocalDate.now().plusYears(2));
        
        when(tradeAgreementRepository.findByParticipatingCountry("US"))
            .thenReturn(Arrays.asList(expiredAgreement, activeAgreement1, activeAgreement2));
        
        // Act
        Optional<TradeAgreement> result = tradeAgreementService.getApplicableTradeAgreement(countryCode, htsCode);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("ACTIVE1", result.get().getAgreementCode()); // Should return first applicable
        verify(tradeAgreementRepository, times(1)).findByParticipatingCountry("US");
    }
    
    @Test
    @DisplayName("Should test branch: getApplicableTradeAgreement with no applicable agreements")
    void getApplicableTradeAgreement_ShouldTestBranchWithNoApplicableAgreements() {
        // Arrange
        String countryCode = "US";
        String htsCode = "12345678";
        
        TradeAgreement expiredAgreement = new TradeAgreement();
        expiredAgreement.setAgreementCode("EXPIRED");
        expiredAgreement.setAgreementName("Expired Agreement");
        expiredAgreement.setEffectiveDate(LocalDate.now().minusYears(2));
        expiredAgreement.setExpirationDate(LocalDate.now().minusDays(1));
        
        TradeAgreement futureAgreement = new TradeAgreement();
        futureAgreement.setAgreementCode("FUTURE");
        futureAgreement.setAgreementName("Future Agreement");
        futureAgreement.setEffectiveDate(LocalDate.now().plusDays(1));
        futureAgreement.setExpirationDate(LocalDate.now().plusYears(1));
        
        when(tradeAgreementRepository.findByParticipatingCountry("US"))
            .thenReturn(Arrays.asList(expiredAgreement, futureAgreement));
        
        // Act
        Optional<TradeAgreement> result = tradeAgreementService.getApplicableTradeAgreement(countryCode, htsCode);
        
        // Assert
        assertFalse(result.isPresent()); // No applicable agreements
        verify(tradeAgreementRepository, times(1)).findByParticipatingCountry("US");
    }
    
    @Test
    @DisplayName("Should test branch: getApplicableTradeAgreement with empty agreement list")
    void getApplicableTradeAgreement_ShouldTestBranchWithEmptyAgreementList() {
        // Arrange
        String countryCode = "US";
        String htsCode = "12345678";
        
        when(tradeAgreementRepository.findByParticipatingCountry("US")).thenReturn(Collections.emptyList());
        
        // Act
        Optional<TradeAgreement> result = tradeAgreementService.getApplicableTradeAgreement(countryCode, htsCode);
        
        // Assert
        assertFalse(result.isPresent()); // No agreements found
        verify(tradeAgreementRepository, times(1)).findByParticipatingCountry("US");
    }
}
