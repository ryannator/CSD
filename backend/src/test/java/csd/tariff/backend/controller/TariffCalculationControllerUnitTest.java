package csd.tariff.backend.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import csd.tariff.backend.controller.TariffCalculationController;
import csd.tariff.backend.dto.TariffCalculationRequest;
import csd.tariff.backend.dto.TariffCalculationResponse;
import csd.tariff.backend.model.TariffCalculation;
import csd.tariff.backend.service.TariffCalculationService;

/**
 * Comprehensive unit tests for TariffCalculationController
 * Tests all CRUD operations, error handling, and edge cases
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TariffCalculationController Unit Tests")
class TariffCalculationControllerUnitTest {

    @Mock
    private TariffCalculationService tariffCalculationService;

    @InjectMocks
    private TariffCalculationController tariffCalculationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private TariffCalculationRequest testRequest;
    private TariffCalculationResponse testResponse;
    private TariffCalculation testCalculation;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tariffCalculationController).build();
        objectMapper = new ObjectMapper();

        // Setup test request
        testRequest = new TariffCalculationRequest(
            "12345678",
            "US",
            "CA",
            new BigDecimal("1000.00"),
            10,
            "USD"
        );

        // Setup test response
        testResponse = new TariffCalculationResponse(
            "12345678",
            "Test Product",
            "US",
            "CA",
            new BigDecimal("1000.00"),
            10,
            "USD"
        );

        // Setup test calculation
        testCalculation = new TariffCalculation();
        setId(testCalculation, 1L);
        testCalculation.setHtsCode("12345678");
        testCalculation.setOriginCountry("US");
        testCalculation.setDestinationCountry("CA");
        testCalculation.setProductValue(new BigDecimal("1000.00"));
        testCalculation.setQuantity(10);
        testCalculation.setCurrency("USD");
        testCalculation.setCalculationType("MFN");
        // Note: createdAt is likely auto-generated, so we don't set it manually
    }

    private void setId(TariffCalculation calculation, Long id) {
        try {
            java.lang.reflect.Field idField = TariffCalculation.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(calculation, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID using reflection", e);
        }
    }

    // ===== CREATE TARIFF CALCULATION TESTS =====

    @Test
    @DisplayName("Should create tariff calculation successfully")
    void createTariffCalculation_ShouldCreateSuccessfully() throws Exception {
        // Arrange
        when(tariffCalculationService.createTariffCalculation(any(TariffCalculationRequest.class)))
            .thenReturn(testResponse);

        // Act & Assert
        mockMvc.perform(post("/tariff/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.htsCode").value("12345678"))
                .andExpect(jsonPath("$.productDescription").value("Test Product"))
                .andExpect(jsonPath("$.originCountry").value("US"))
                .andExpect(jsonPath("$.destinationCountry").value("CA"))
                .andExpect(jsonPath("$.productValue").value(1000.00))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.currency").value("USD"));

        verify(tariffCalculationService, times(1)).createTariffCalculation(any(TariffCalculationRequest.class));
    }

    @Test
    @DisplayName("Should return bad request when service throws exception")
    void createTariffCalculation_ShouldReturnBadRequest_WhenServiceThrowsException() throws Exception {
        // Arrange
        when(tariffCalculationService.createTariffCalculation(any(TariffCalculationRequest.class)))
            .thenThrow(new IllegalArgumentException("Invalid HTS code"));

        // Act & Assert
        mockMvc.perform(post("/tariff/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Invalid request: Invalid HTS code"));

        verify(tariffCalculationService, times(1)).createTariffCalculation(any(TariffCalculationRequest.class));
    }

    @Test
    @DisplayName("Should return bad request for invalid request body")
    void createTariffCalculation_ShouldReturnBadRequest_ForInvalidRequestBody() throws Exception {
        // Arrange - Create invalid request with null values
        TariffCalculationRequest invalidRequest = new TariffCalculationRequest(
            null, null, null, null, null, null
        );

        // Act & Assert
        mockMvc.perform(post("/tariff/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // ===== GET TARIFF CALCULATION BY ID TESTS =====

    @Test
    @DisplayName("Should get tariff calculation by ID successfully")
    void getTariffCalculationById_ShouldReturnCalculation_WhenFound() throws Exception {
        // Arrange
        when(tariffCalculationService.getTariffCalculationById(1L))
            .thenReturn(Optional.of(testCalculation));

        // Act & Assert
        mockMvc.perform(get("/tariff/calculate/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.htsCode").value("12345678"))
                .andExpect(jsonPath("$.originCountry").value("US"))
                .andExpect(jsonPath("$.destinationCountry").value("CA"))
                .andExpect(jsonPath("$.productValue").value(1000.00))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.currency").value("USD"));

        verify(tariffCalculationService, times(1)).getTariffCalculationById(1L);
    }

    @Test
    @DisplayName("Should return not found when tariff calculation does not exist")
    void getTariffCalculationById_ShouldReturnNotFound_WhenNotExists() throws Exception {
        // Arrange
        when(tariffCalculationService.getTariffCalculationById(999L))
            .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/tariff/calculate/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(tariffCalculationService, times(1)).getTariffCalculationById(999L);
    }

    // ===== GET ALL TARIFF CALCULATIONS TESTS =====

    @Test
    @DisplayName("Should get all tariff calculations successfully")
    void getAllTariffCalculations_ShouldReturnAllCalculations() throws Exception {
        // Arrange
        List<TariffCalculation> calculations = Arrays.asList(testCalculation);
        when(tariffCalculationService.getAllTariffCalculations())
            .thenReturn(calculations);

        // Act & Assert
        mockMvc.perform(get("/tariff/calculate-all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].htsCode").value("12345678"));

        verify(tariffCalculationService, times(1)).getAllTariffCalculations();
    }

    @Test
    @DisplayName("Should return empty list when no calculations exist")
    void getAllTariffCalculations_ShouldReturnEmptyList_WhenNoCalculations() throws Exception {
        // Arrange
        when(tariffCalculationService.getAllTariffCalculations())
            .thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/tariff/calculate-all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(tariffCalculationService, times(1)).getAllTariffCalculations();
    }

    // ===== UPDATE TARIFF CALCULATION TESTS =====

    @Test
    @DisplayName("Should update tariff calculation successfully")
    void updateTariffCalculation_ShouldUpdateSuccessfully() throws Exception {
        // Arrange
        when(tariffCalculationService.updateTariffCalculation(eq(1L), any(TariffCalculationRequest.class)))
            .thenReturn(testResponse);

        // Act & Assert
        mockMvc.perform(put("/tariff/calculate/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.htsCode").value("12345678"))
                .andExpect(jsonPath("$.productDescription").value("Test Product"));

        verify(tariffCalculationService, times(1)).updateTariffCalculation(eq(1L), any(TariffCalculationRequest.class));
    }

    @Test
    @DisplayName("Should return bad request when update fails")
    void updateTariffCalculation_ShouldReturnBadRequest_WhenUpdateFails() throws Exception {
        // Arrange
        when(tariffCalculationService.updateTariffCalculation(eq(1L), any(TariffCalculationRequest.class)))
            .thenThrow(new IllegalArgumentException("Calculation not found"));

        // Act & Assert
        mockMvc.perform(put("/tariff/calculate/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Invalid request: Calculation not found"));

        verify(tariffCalculationService, times(1)).updateTariffCalculation(eq(1L), any(TariffCalculationRequest.class));
    }

    // ===== DELETE TARIFF CALCULATION TESTS =====

    @Test
    @DisplayName("Should delete tariff calculation successfully")
    void deleteTariffCalculation_ShouldDeleteSuccessfully() throws Exception {
        // Arrange
        when(tariffCalculationService.deleteTariffCalculation(1L))
            .thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/tariff/calculate/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Tariff calculation deleted successfully"))
                .andExpect(jsonPath("$.id").value(1));

        verify(tariffCalculationService, times(1)).deleteTariffCalculation(1L);
    }

    @Test
    @DisplayName("Should return not found when deletion fails")
    void deleteTariffCalculation_ShouldReturnNotFound_WhenDeletionFails() throws Exception {
        // Arrange
        when(tariffCalculationService.deleteTariffCalculation(999L))
            .thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/tariff/calculate/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(tariffCalculationService, times(1)).deleteTariffCalculation(999L);
    }

    // ===== EDGE CASES AND ERROR HANDLING TESTS =====

    @Test
    @DisplayName("Should handle null request body gracefully")
    void createTariffCalculation_ShouldHandleNullRequestBody() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/tariff/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid JSON format")
    void createTariffCalculation_ShouldHandleInvalidJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/tariff/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle negative ID in path variable")
    void getTariffCalculationById_ShouldHandleNegativeId() throws Exception {
        // Arrange
        when(tariffCalculationService.getTariffCalculationById(-1L))
            .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/tariff/calculate/{id}", -1L))
                .andExpect(status().isNotFound());

        verify(tariffCalculationService, times(1)).getTariffCalculationById(-1L);
    }

    @Test
    @DisplayName("Should handle zero ID in path variable")
    void getTariffCalculationById_ShouldHandleZeroId() throws Exception {
        // Arrange
        when(tariffCalculationService.getTariffCalculationById(0L))
            .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/tariff/calculate/{id}", 0L))
                .andExpect(status().isNotFound());

        verify(tariffCalculationService, times(1)).getTariffCalculationById(0L);
    }
}
