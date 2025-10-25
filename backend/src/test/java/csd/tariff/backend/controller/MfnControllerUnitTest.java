package csd.tariff.backend.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import static org.mockito.Mockito.never;
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

import csd.tariff.backend.controller.MfnController;
import csd.tariff.backend.model.MfnTariffRate;
import csd.tariff.backend.model.Product;
import csd.tariff.backend.service.MfnService;
import csd.tariff.backend.service.TariffCalculationService;

/**
 * Comprehensive unit tests for MfnController
 * Tests all CRUD operations, error handling, and edge cases
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MfnController Unit Tests")
class MfnControllerUnitTest {

    @Mock
    private TariffCalculationService tariffCalculationService;

    @Mock
    private MfnService mfnService;

    @InjectMocks
    private MfnController mfnController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private MfnTariffRate testMfnRate;
    private Product testProduct;
    private Map<String, Object> testRequestData;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mfnController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Setup test product
        testProduct = new Product();
        setId(testProduct, 1L);
        testProduct.setHts8("12345678");
        testProduct.setBriefDescription("Test Product");

        // Setup test MFN rate
        testMfnRate = new MfnTariffRate();
        setId(testMfnRate, 1L);
        testMfnRate.setProduct(testProduct);
        testMfnRate.setMfnTextRate("10% + $5.00 per unit");
        testMfnRate.setMfnRateTypeCode("ADV");
        testMfnRate.setMfnadValoremRate(new BigDecimal("0.10"));
        testMfnRate.setMfnSpecificRate(new BigDecimal("5.00"));
        testMfnRate.setMfnOtherRate(new BigDecimal("0.00"));
        testMfnRate.setBeginEffectDate(LocalDate.of(2020, 1, 1));
        testMfnRate.setEndEffectiveDate(LocalDate.of(2025, 12, 31));

        // Setup test request data
        testRequestData = new HashMap<>();
        testRequestData.put("mfnTextRate", "10% + $5.00 per unit");
        testRequestData.put("mfnRateTypeCode", "ADV");
        testRequestData.put("mfnadValoremRate", "0.10");
        testRequestData.put("mfnSpecificRate", "5.00");
        testRequestData.put("mfnOtherRate", "0.00");
        testRequestData.put("beginEffectDate", "2020-01-01");
        testRequestData.put("endEffectiveDate", "2025-12-31");
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

    private void setId(MfnTariffRate rate, Long id) {
        try {
            java.lang.reflect.Field idField = MfnTariffRate.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(rate, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID using reflection", e);
        }
    }

    // ===== GET ALL MFN TARIFF RATES TESTS =====

    @Test
    @DisplayName("Should get all MFN tariff rates successfully")
    void getAllMfnTariffRates_ShouldReturnAllRates() throws Exception {
        // Arrange
        List<MfnTariffRate> rates = Arrays.asList(testMfnRate);
        when(mfnService.getAllMfnTariffRates()).thenReturn(rates);

        // Act & Assert
        mockMvc.perform(get("/mfn"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].mfnTextRate").value("10% + $5.00 per unit"))
                .andExpect(jsonPath("$[0].mfnRateTypeCode").value("ADV"))
                .andExpect(jsonPath("$[0].mfnadValoremRate").value(0.10))
                .andExpect(jsonPath("$[0].mfnSpecificRate").value(5.00));

        verify(mfnService, times(1)).getAllMfnTariffRates();
    }

    @Test
    @DisplayName("Should return empty list when no MFN rates exist")
    void getAllMfnTariffRates_ShouldReturnEmptyList_WhenNoRates() throws Exception {
        // Arrange
        when(mfnService.getAllMfnTariffRates()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/mfn"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(mfnService, times(1)).getAllMfnTariffRates();
    }

    // ===== GET MFN TARIFF RATES FOR PRODUCT TESTS =====

    @Test
    @DisplayName("Should get MFN tariff rates for product successfully")
    void getMfnTariffRatesForProduct_ShouldReturnRates_WhenFound() throws Exception {
        // Arrange
        when(mfnService.getMfnTariffRatesForProduct("12345678"))
            .thenReturn(Optional.of(testMfnRate));

        // Act & Assert
        mockMvc.perform(get("/mfn/mfn-rate/{htsCode}", "12345678"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.mfnTextRate").value("10% + $5.00 per unit"));

        verify(mfnService, times(1)).getMfnTariffRatesForProduct("12345678");
    }

    @Test
    @DisplayName("Should return empty optional when MFN rates not found for product")
    void getMfnTariffRatesForProduct_ShouldReturnEmpty_WhenNotFound() throws Exception {
        // Arrange
        when(mfnService.getMfnTariffRatesForProduct("99999999"))
            .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/mfn/mfn-rate/{htsCode}", "99999999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").doesNotExist());

        verify(mfnService, times(1)).getMfnTariffRatesForProduct("99999999");
    }

    // ===== CREATE MFN TARIFF RATE TESTS =====

    @Test
    @DisplayName("Should create MFN tariff rate successfully")
    void createMfnTariffRate_ShouldCreateSuccessfully() throws Exception {
        // Arrange
        when(tariffCalculationService.findByHtsCode("12345678"))
            .thenReturn(Optional.of(testProduct));
        when(mfnService.createMfnTariffRate(any(MfnTariffRate.class)))
            .thenReturn(testMfnRate);

        // Act & Assert
        mockMvc.perform(post("/mfn/mfn-rate/{htsCode}", "12345678")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequestData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.mfnTextRate").value("10% + $5.00 per unit"))
                .andExpect(jsonPath("$.mfnRateTypeCode").value("ADV"));

        verify(tariffCalculationService, times(1)).findByHtsCode("12345678");
        verify(mfnService, times(1)).createMfnTariffRate(any(MfnTariffRate.class));
    }

    @Test
    @DisplayName("Should return bad request when product not found")
    void createMfnTariffRate_ShouldReturnBadRequest_WhenProductNotFound() throws Exception {
        // Arrange
        when(tariffCalculationService.findByHtsCode("99999999"))
            .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/mfn/mfn-rate/{htsCode}", "99999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequestData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Product not found"))
                .andExpect(jsonPath("$.message").value("HTS code not found: 99999999"));

        verify(tariffCalculationService, times(1)).findByHtsCode("99999999");
    }

    @Test
    @DisplayName("Should return bad request when creation fails")
    void createMfnTariffRate_ShouldReturnBadRequest_WhenCreationFails() throws Exception {
        // Arrange
        when(tariffCalculationService.findByHtsCode("12345678"))
            .thenReturn(Optional.of(testProduct));
        when(mfnService.createMfnTariffRate(any(MfnTariffRate.class)))
            .thenThrow(new IllegalArgumentException("Invalid MFN rate data"));

        // Act & Assert
        mockMvc.perform(post("/mfn/mfn-rate/{htsCode}", "12345678")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequestData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Failed to create MFN tariff rate"))
                .andExpect(jsonPath("$.message").value("Invalid MFN rate data"));

        verify(tariffCalculationService, times(1)).findByHtsCode("12345678");
        verify(mfnService, times(1)).createMfnTariffRate(any(MfnTariffRate.class));
    }

    @Test
    @DisplayName("Should handle missing optional fields in request data")
    void createMfnTariffRate_ShouldHandleMissingOptionalFields() throws Exception {
        // Arrange
        Map<String, Object> minimalRequestData = new HashMap<>();
        minimalRequestData.put("mfnTextRate", "10%");
        minimalRequestData.put("mfnRateTypeCode", "ADV");
        minimalRequestData.put("mfnadValoremRate", "0.10");
        minimalRequestData.put("mfnSpecificRate", "5.00");
        minimalRequestData.put("mfnOtherRate", "0.00");
        // No dates provided

        when(tariffCalculationService.findByHtsCode("12345678"))
            .thenReturn(Optional.of(testProduct));
        when(mfnService.createMfnTariffRate(any(MfnTariffRate.class)))
            .thenReturn(testMfnRate);

        // Act & Assert
        mockMvc.perform(post("/mfn/mfn-rate/{htsCode}", "12345678")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minimalRequestData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(tariffCalculationService, times(1)).findByHtsCode("12345678");
        verify(mfnService, times(1)).createMfnTariffRate(any(MfnTariffRate.class));
    }

    // ===== UPDATE MFN TARIFF RATE TESTS =====

    @Test
    @DisplayName("Should update MFN tariff rate successfully")
    void updateMfnTariffRate_ShouldUpdateSuccessfully() throws Exception {
        // Arrange
        MfnTariffRate updatedRate = new MfnTariffRate();
        setId(updatedRate, 1L);
        updatedRate.setMfnTextRate("15% + $8.00 per unit");
        updatedRate.setMfnRateTypeCode("ADV");

        when(mfnService.updateMfnTariffRateByHtsCode(eq("12345678"), any(MfnTariffRate.class)))
            .thenReturn(updatedRate);

        // Act & Assert
        mockMvc.perform(put("/mfn/mfn-rate/{htsCode}", "12345678")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.mfnTextRate").value("15% + $8.00 per unit"))
                .andExpect(jsonPath("$.mfnRateTypeCode").value("ADV"));

        verify(mfnService, times(1)).updateMfnTariffRateByHtsCode(eq("12345678"), any(MfnTariffRate.class));
    }

    @Test
    @DisplayName("Should return bad request when update fails")
    void updateMfnTariffRate_ShouldReturnBadRequest_WhenUpdateFails() throws Exception {
        // Arrange
        when(mfnService.updateMfnTariffRateByHtsCode(eq("99999999"), any(MfnTariffRate.class)))
            .thenThrow(new IllegalArgumentException("MFN rate not found"));

        // Act & Assert
        mockMvc.perform(put("/mfn/mfn-rate/{htsCode}", "99999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMfnRate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Failed to update MFN tariff rate"))
                .andExpect(jsonPath("$.message").value("MFN rate not found"));

        verify(mfnService, times(1)).updateMfnTariffRateByHtsCode(eq("99999999"), any(MfnTariffRate.class));
    }

    // ===== DELETE MFN TARIFF RATE TESTS =====

    @Test
    @DisplayName("Should delete MFN tariff rate successfully")
    void deleteMfnTariffRate_ShouldDeleteSuccessfully() throws Exception {
        // Arrange
        doNothing().when(mfnService).deleteMfnTariffRateByHtsCode("12345678");

        // Act & Assert
        mockMvc.perform(delete("/mfn/mfn-rates/{htsCode}", "12345678"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("MFN tariff rate deleted successfully"));

        verify(mfnService, times(1)).deleteMfnTariffRateByHtsCode("12345678");
    }

    @Test
    @DisplayName("Should return bad request when deletion fails")
    void deleteMfnTariffRate_ShouldReturnBadRequest_WhenDeletionFails() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("MFN rate not found"))
            .when(mfnService).deleteMfnTariffRateByHtsCode("99999999");

        // Act & Assert
        mockMvc.perform(delete("/mfn/mfn-rates/{htsCode}", "99999999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Failed to delete MFN tariff rate"))
                .andExpect(jsonPath("$.message").value("MFN rate not found"));

        verify(mfnService, times(1)).deleteMfnTariffRateByHtsCode("99999999");
    }

    // ===== EDGE CASES AND ERROR HANDLING TESTS =====

    @Test
    @DisplayName("Should handle null request body gracefully")
    void createMfnTariffRate_ShouldHandleNullRequestBody() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/mfn/mfn-rate/{htsCode}", "12345678")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid JSON format")
    void createMfnTariffRate_ShouldHandleInvalidJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/mfn/mfn-rate/{htsCode}", "12345678")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle empty HTS code")
    void getMfnTariffRatesForProduct_ShouldHandleEmptyHtsCode() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/mfn/mfn-rate/{htsCode}", ""))
                .andExpect(status().isNotFound());

        // Verify that the service is not called for empty HTS code
        verify(mfnService, never()).getMfnTariffRatesForProduct("");
    }

    @Test
    @DisplayName("Should handle invalid BigDecimal values in request data")
    void createMfnTariffRate_ShouldHandleInvalidBigDecimalValues() throws Exception {
        // Arrange
        Map<String, Object> invalidRequestData = new HashMap<>();
        invalidRequestData.put("mfnTextRate", "10%");
        invalidRequestData.put("mfnRateTypeCode", "ADV");
        invalidRequestData.put("mfnadValoremRate", "invalid_number");
        invalidRequestData.put("mfnSpecificRate", "5.00");
        invalidRequestData.put("mfnOtherRate", "0.00");

        when(tariffCalculationService.findByHtsCode("12345678"))
            .thenReturn(Optional.of(testProduct));

        // Act & Assert
        mockMvc.perform(post("/mfn/mfn-rate/{htsCode}", "12345678")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequestData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Failed to create MFN tariff rate"));

        verify(tariffCalculationService, times(1)).findByHtsCode("12345678");
    }

    @Test
    @DisplayName("Should handle invalid date format in request data")
    void createMfnTariffRate_ShouldHandleInvalidDateFormat() throws Exception {
        // Arrange
        Map<String, Object> invalidDateRequestData = new HashMap<>();
        invalidDateRequestData.put("mfnTextRate", "10%");
        invalidDateRequestData.put("mfnRateTypeCode", "ADV");
        invalidDateRequestData.put("mfnadValoremRate", "0.10");
        invalidDateRequestData.put("mfnSpecificRate", "5.00");
        invalidDateRequestData.put("mfnOtherRate", "0.00");
        invalidDateRequestData.put("beginEffectDate", "invalid-date");

        when(tariffCalculationService.findByHtsCode("12345678"))
            .thenReturn(Optional.of(testProduct));

        // Act & Assert
        mockMvc.perform(post("/mfn/mfn-rate/{htsCode}", "12345678")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDateRequestData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Failed to create MFN tariff rate"));

        verify(tariffCalculationService, times(1)).findByHtsCode("12345678");
    }
}
