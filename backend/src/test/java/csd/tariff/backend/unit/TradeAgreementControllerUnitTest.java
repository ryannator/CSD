package csd.tariff.backend.unit;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import csd.tariff.backend.controller.TradeAgreementController;
import csd.tariff.backend.model.TradeAgreement;
import csd.tariff.backend.service.TradeAgreementService;

@ExtendWith(MockitoExtension.class)
@DisplayName("TradeAgreementController Unit Tests")
class TradeAgreementControllerUnitTest {

    @Mock
    private TradeAgreementService tradeAgreementService;

    private TradeAgreementController tradeAgreementController;

    private TradeAgreement testAgreement1;
    private TradeAgreement testAgreement2;

    @BeforeEach
    void setUp() {
        tradeAgreementController = new TradeAgreementController();
        // Use reflection to inject the mock service
        try {
            java.lang.reflect.Field field = TradeAgreementController.class.getDeclaredField("tradeAgreementService");
            field.setAccessible(true);
            field.set(tradeAgreementController, tradeAgreementService);
        } catch (Exception e) {
            fail("Failed to inject mock service: " + e.getMessage());
        }

        testAgreement1 = new TradeAgreement();
        testAgreement1.setAgreementCode("USMCA");
        testAgreement1.setAgreementName("United States-Mexico-Canada Agreement");
        testAgreement1.setAgreementType("FTA");
        testAgreement1.setIsMultilateral(false);
        testAgreement1.setEffectiveDate(LocalDate.of(2020, 7, 1));
        testAgreement1.setExpirationDate(LocalDate.of(2030, 6, 30));

        testAgreement2 = new TradeAgreement();
        testAgreement2.setAgreementCode("CPTPP");
        testAgreement2.setAgreementName("Comprehensive and Progressive Agreement for Trans-Pacific Partnership");
        testAgreement2.setAgreementType("FTA");
        testAgreement2.setIsMultilateral(true);
        testAgreement2.setEffectiveDate(LocalDate.of(2018, 12, 30));
        testAgreement2.setExpirationDate(null);
    }

    @Test
    @DisplayName("Should get all trade agreements successfully")
    void getAllTradeAgreements_ShouldReturnAllAgreements() {
        // Arrange
        List<TradeAgreement> agreements = Arrays.asList(testAgreement1, testAgreement2);
        when(tradeAgreementService.getAllTradeAgreements()).thenReturn(agreements);

        // Act
        ResponseEntity<List<TradeAgreement>> response = tradeAgreementController.getAllTradeAgreements();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(testAgreement1.getAgreementCode(), response.getBody().get(0).getAgreementCode());
        assertEquals(testAgreement2.getAgreementCode(), response.getBody().get(1).getAgreementCode());

        verify(tradeAgreementService, times(1)).getAllTradeAgreements();
    }

    @Test
    @DisplayName("Should return empty list when no agreements exist")
    void getAllTradeAgreements_ShouldReturnEmptyList_WhenNoAgreements() {
        // Arrange
        when(tradeAgreementService.getAllTradeAgreements()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<List<TradeAgreement>> response = tradeAgreementController.getAllTradeAgreements();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(tradeAgreementService, times(1)).getAllTradeAgreements();
    }

    @Test
    @DisplayName("Should get trade agreement by code successfully")
    void getTradeAgreementByCode_ShouldReturnAgreement() {
        // Arrange
        when(tradeAgreementService.getTradeAgreementByCode("USMCA")).thenReturn(Optional.of(testAgreement1));

        // Act
        ResponseEntity<TradeAgreement> response = tradeAgreementController.getTradeAgreementByCode("USMCA");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testAgreement1.getAgreementCode(), response.getBody().getAgreementCode());
        assertEquals(testAgreement1.getAgreementName(), response.getBody().getAgreementName());
        assertEquals(testAgreement1.getAgreementType(), response.getBody().getAgreementType());

        verify(tradeAgreementService, times(1)).getTradeAgreementByCode("USMCA");
    }

    @Test
    @DisplayName("Should return not found when agreement does not exist")
    void getTradeAgreementByCode_ShouldReturnNotFound_WhenAgreementNotFound() {
        // Arrange
        when(tradeAgreementService.getTradeAgreementByCode("NONEXISTENT")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<TradeAgreement> response = tradeAgreementController.getTradeAgreementByCode("NONEXISTENT");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(tradeAgreementService, times(1)).getTradeAgreementByCode("NONEXISTENT");
    }

    @Test
    @DisplayName("Should create trade agreement successfully")
    void createTradeAgreement_ShouldCreateAgreement() {
        // Arrange
        TradeAgreement newAgreement = new TradeAgreement();
        newAgreement.setAgreementCode("NEWFTA");
        newAgreement.setAgreementName("New Free Trade Agreement");
        newAgreement.setAgreementType("FTA");
        newAgreement.setIsMultilateral(false);
        newAgreement.setEffectiveDate(LocalDate.of(2024, 1, 1));

        when(tradeAgreementService.createTradeAgreement(newAgreement)).thenReturn(newAgreement);

        // Act
        ResponseEntity<TradeAgreement> response = tradeAgreementController.createTradeAgreement(newAgreement);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newAgreement.getAgreementCode(), response.getBody().getAgreementCode());
        assertEquals(newAgreement.getAgreementName(), response.getBody().getAgreementName());

        verify(tradeAgreementService, times(1)).createTradeAgreement(newAgreement);
    }

    @Test
    @DisplayName("Should return bad request when agreement creation fails")
    void createTradeAgreement_ShouldReturnBadRequest_WhenCreationFails() {
        // Arrange
        TradeAgreement invalidAgreement = new TradeAgreement();
        invalidAgreement.setAgreementCode("USMCA"); // Duplicate code
        invalidAgreement.setAgreementName("Duplicate Agreement");

        when(tradeAgreementService.createTradeAgreement(invalidAgreement))
            .thenThrow(new IllegalArgumentException("Agreement code already exists"));

        // Act
        ResponseEntity<TradeAgreement> response = tradeAgreementController.createTradeAgreement(invalidAgreement);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        verify(tradeAgreementService, times(1)).createTradeAgreement(invalidAgreement);
    }

    @Test
    @DisplayName("Should return bad request when unexpected exception occurs during creation")
    void createTradeAgreement_ShouldReturnBadRequest_WhenUnexpectedException() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement();
        agreement.setAgreementCode("TEST");

        when(tradeAgreementService.createTradeAgreement(agreement))
            .thenThrow(new RuntimeException("Database connection failed"));

        // Act
        ResponseEntity<TradeAgreement> response = tradeAgreementController.createTradeAgreement(agreement);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        verify(tradeAgreementService, times(1)).createTradeAgreement(agreement);
    }

    @Test
    @DisplayName("Should update trade agreement successfully")
    void updateTradeAgreement_ShouldUpdateAgreement() {
        // Arrange
        TradeAgreement updatedAgreement = new TradeAgreement();
        updatedAgreement.setAgreementCode("USMCA");
        updatedAgreement.setAgreementName("Updated USMCA Agreement");
        updatedAgreement.setAgreementType("FTA");
        updatedAgreement.setIsMultilateral(false);
        updatedAgreement.setEffectiveDate(LocalDate.of(2020, 7, 1));
        updatedAgreement.setExpirationDate(LocalDate.of(2035, 6, 30)); // Extended expiration

        when(tradeAgreementService.updateTradeAgreementByCode("USMCA", updatedAgreement))
            .thenReturn(updatedAgreement);

        // Act
        ResponseEntity<TradeAgreement> response = tradeAgreementController.updateTradeAgreement("USMCA", updatedAgreement);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedAgreement.getAgreementCode(), response.getBody().getAgreementCode());
        assertEquals(updatedAgreement.getAgreementName(), response.getBody().getAgreementName());
        assertEquals(updatedAgreement.getExpirationDate(), response.getBody().getExpirationDate());

        verify(tradeAgreementService, times(1)).updateTradeAgreementByCode("USMCA", updatedAgreement);
    }

    @Test
    @DisplayName("Should return bad request when agreement update fails")
    void updateTradeAgreement_ShouldReturnBadRequest_WhenUpdateFails() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement();
        agreement.setAgreementCode("NONEXISTENT");
        agreement.setAgreementName("Non-existent Agreement");

        when(tradeAgreementService.updateTradeAgreementByCode("NONEXISTENT", agreement))
            .thenThrow(new IllegalArgumentException("Trade agreement not found"));

        // Act
        ResponseEntity<TradeAgreement> response = tradeAgreementController.updateTradeAgreement("NONEXISTENT", agreement);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        verify(tradeAgreementService, times(1)).updateTradeAgreementByCode("NONEXISTENT", agreement);
    }

    @Test
    @DisplayName("Should return bad request when unexpected exception occurs during update")
    void updateTradeAgreement_ShouldReturnBadRequest_WhenUnexpectedException() {
        // Arrange
        TradeAgreement agreement = new TradeAgreement();
        agreement.setAgreementCode("USMCA");

        when(tradeAgreementService.updateTradeAgreementByCode("USMCA", agreement))
            .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<TradeAgreement> response = tradeAgreementController.updateTradeAgreement("USMCA", agreement);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        verify(tradeAgreementService, times(1)).updateTradeAgreementByCode("USMCA", agreement);
    }

    @Test
    @DisplayName("Should delete trade agreement successfully")
    void deleteTradeAgreement_ShouldDeleteAgreement() {
        // Arrange
        doNothing().when(tradeAgreementService).deleteTradeAgreementByCode("USMCA");

        // Act
        ResponseEntity<Void> response = tradeAgreementController.deleteTradeAgreement("USMCA");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        verify(tradeAgreementService, times(1)).deleteTradeAgreementByCode("USMCA");
    }

    @Test
    @DisplayName("Should return bad request when agreement deletion fails")
    void deleteTradeAgreement_ShouldReturnBadRequest_WhenDeletionFails() {
        // Arrange
        doThrow(new IllegalArgumentException("Trade agreement not found"))
            .when(tradeAgreementService).deleteTradeAgreementByCode("NONEXISTENT");

        // Act
        ResponseEntity<Void> response = tradeAgreementController.deleteTradeAgreement("NONEXISTENT");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        verify(tradeAgreementService, times(1)).deleteTradeAgreementByCode("NONEXISTENT");
    }

    @Test
    @DisplayName("Should return bad request when unexpected exception occurs during deletion")
    void deleteTradeAgreement_ShouldReturnBadRequest_WhenUnexpectedException() {
        // Arrange
        doThrow(new RuntimeException("Database connection failed"))
            .when(tradeAgreementService).deleteTradeAgreementByCode("USMCA");

        // Act
        ResponseEntity<Void> response = tradeAgreementController.deleteTradeAgreement("USMCA");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        verify(tradeAgreementService, times(1)).deleteTradeAgreementByCode("USMCA");
    }

    @Test
    @DisplayName("Should handle null agreement code in get by code")
    void getTradeAgreementByCode_ShouldHandleNullCode() {
        // Arrange
        when(tradeAgreementService.getTradeAgreementByCode(null)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<TradeAgreement> response = tradeAgreementController.getTradeAgreementByCode(null);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(tradeAgreementService, times(1)).getTradeAgreementByCode(null);
    }

    @Test
    @DisplayName("Should handle empty agreement code in get by code")
    void getTradeAgreementByCode_ShouldHandleEmptyCode() {
        // Arrange
        when(tradeAgreementService.getTradeAgreementByCode("")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<TradeAgreement> response = tradeAgreementController.getTradeAgreementByCode("");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(tradeAgreementService, times(1)).getTradeAgreementByCode("");
    }

    @Test
    @DisplayName("Should handle null agreement in create")
    void createTradeAgreement_ShouldHandleNullAgreement() {
        // Arrange
        when(tradeAgreementService.createTradeAgreement(null))
            .thenThrow(new IllegalArgumentException("Trade agreement cannot be null"));

        // Act
        ResponseEntity<TradeAgreement> response = tradeAgreementController.createTradeAgreement(null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        verify(tradeAgreementService, times(1)).createTradeAgreement(null);
    }
}
