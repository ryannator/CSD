package csd.tariff.backend.unit;

import java.util.Arrays;
import java.util.List;

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

import csd.tariff.backend.controller.CountryController;
import csd.tariff.backend.model.Country;
import csd.tariff.backend.service.CountryService;

/**
 * Comprehensive unit tests for CountryController
 * Tests all CRUD operations, error handling, and edge cases
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CountryController Unit Tests")
class CountryControllerUnitTest {

    @Mock
    private CountryService countryService;

    @InjectMocks
    private CountryController countryController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Country testCountry;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(countryController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Setup test country
        testCountry = new Country();
        setId(testCountry, 1L);
        testCountry.setCountryCode("US");
        testCountry.setCountryName("United States");
        testCountry.setRegion("North America");
    }

    private void setId(Country country, Long id) {
        try {
            java.lang.reflect.Field idField = Country.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(country, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID using reflection", e);
        }
    }

    // ===== GET ALL COUNTRIES TESTS =====

    @Test
    @DisplayName("Should get all countries successfully")
    void getAllCountries_ShouldReturnAllCountries() throws Exception {
        // Arrange
        List<Country> countries = Arrays.asList(testCountry);
        when(countryService.getAllCountries()).thenReturn(countries);

        // Act & Assert
        mockMvc.perform(get("/country"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].countryCode").value("US"))
                .andExpect(jsonPath("$[0].countryName").value("United States"))
                .andExpect(jsonPath("$[0].region").value("North America"));

        verify(countryService, times(1)).getAllCountries();
    }

    @Test
    @DisplayName("Should return empty list when no countries exist")
    void getAllCountries_ShouldReturnEmptyList_WhenNoCountries() throws Exception {
        // Arrange
        when(countryService.getAllCountries()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/country"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(countryService, times(1)).getAllCountries();
    }

    // ===== CREATE COUNTRY TESTS =====

    @Test
    @DisplayName("Should create country successfully")
    void createCountry_ShouldCreateSuccessfully() throws Exception {
        // Arrange
        when(countryService.createCountry(any(Country.class))).thenReturn(testCountry);

        // Act & Assert
        mockMvc.perform(post("/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCountry)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.countryCode").value("US"))
                .andExpect(jsonPath("$.countryName").value("United States"))
                .andExpect(jsonPath("$.region").value("North America"));

        verify(countryService, times(1)).createCountry(any(Country.class));
    }

    @Test
    @DisplayName("Should return bad request when country creation fails with IllegalArgumentException")
    void createCountry_ShouldReturnBadRequest_WhenIllegalArgumentException() throws Exception {
        // Arrange
        when(countryService.createCountry(any(Country.class)))
            .thenThrow(new IllegalArgumentException("Country code already exists"));

        // Act & Assert
        mockMvc.perform(post("/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCountry)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Country code already exists"));

        verify(countryService, times(1)).createCountry(any(Country.class));
    }

    @Test
    @DisplayName("Should return conflict when country creation fails with IllegalStateException")
    void createCountry_ShouldReturnConflict_WhenIllegalStateException() throws Exception {
        // Arrange
        when(countryService.createCountry(any(Country.class)))
            .thenThrow(new IllegalStateException("Country already exists"));

        // Act & Assert
        mockMvc.perform(post("/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCountry)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Country already exists"));

        verify(countryService, times(1)).createCountry(any(Country.class));
    }

    @Test
    @DisplayName("Should return bad request for invalid country data")
    void createCountry_ShouldReturnBadRequest_ForInvalidData() throws Exception {
        // Arrange - Create invalid country with null values
        Country invalidCountry = new Country();
        invalidCountry.setCountryCode(null);
        invalidCountry.setCountryName(null);

        // Mock service to throw exception for invalid data
        when(countryService.createCountry(any(Country.class)))
            .thenThrow(new IllegalArgumentException("Invalid country data"));

        // Act & Assert
        mockMvc.perform(post("/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCountry)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid country data"));

        verify(countryService, times(1)).createCountry(any(Country.class));
    }

    // ===== UPDATE COUNTRY TESTS =====

    @Test
    @DisplayName("Should update country successfully")
    void updateCountry_ShouldUpdateSuccessfully() throws Exception {
        // Arrange
        Country updatedCountry = new Country();
        setId(updatedCountry, 1L);
        updatedCountry.setCountryCode("US");
        updatedCountry.setCountryName("United States of America");
        updatedCountry.setRegion("North America");

        when(countryService.updateCountry(eq(1L), any(Country.class))).thenReturn(updatedCountry);

        // Act & Assert
        mockMvc.perform(put("/country/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCountry)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.countryCode").value("US"))
                .andExpect(jsonPath("$.countryName").value("United States of America"))
                .andExpect(jsonPath("$.region").value("North America"));

        verify(countryService, times(1)).updateCountry(eq(1L), any(Country.class));
    }

    @Test
    @DisplayName("Should return bad request when country update fails with IllegalArgumentException")
    void updateCountry_ShouldReturnBadRequest_WhenIllegalArgumentException() throws Exception {
        // Arrange
        when(countryService.updateCountry(eq(1L), any(Country.class)))
            .thenThrow(new IllegalArgumentException("Country not found"));

        // Act & Assert
        mockMvc.perform(put("/country/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCountry)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Country not found"));

        verify(countryService, times(1)).updateCountry(eq(1L), any(Country.class));
    }

    @Test
    @DisplayName("Should return conflict when country update fails with IllegalStateException")
    void updateCountry_ShouldReturnConflict_WhenIllegalStateException() throws Exception {
        // Arrange
        when(countryService.updateCountry(eq(1L), any(Country.class)))
            .thenThrow(new IllegalStateException("Country code conflict"));

        // Act & Assert
        mockMvc.perform(put("/country/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCountry)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Country code conflict"));

        verify(countryService, times(1)).updateCountry(eq(1L), any(Country.class));
    }

    @Test
    @DisplayName("Should return bad request for invalid country update data")
    void updateCountry_ShouldReturnBadRequest_ForInvalidData() throws Exception {
        // Arrange - Create invalid country with null values
        Country invalidCountry = new Country();
        invalidCountry.setCountryCode(null);
        invalidCountry.setCountryName(null);

        // Mock service to throw exception for invalid data
        when(countryService.updateCountry(eq(1L), any(Country.class)))
            .thenThrow(new IllegalArgumentException("Invalid country data"));

        // Act & Assert
        mockMvc.perform(put("/country/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCountry)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid country data"));

        verify(countryService, times(1)).updateCountry(eq(1L), any(Country.class));
    }

    // ===== DELETE COUNTRY TESTS =====

    @Test
    @DisplayName("Should delete country successfully")
    void deleteCountry_ShouldDeleteSuccessfully() throws Exception {
        // Arrange
        doNothing().when(countryService).deleteCountry(1L);

        // Act & Assert
        mockMvc.perform(delete("/country/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(countryService, times(1)).deleteCountry(1L);
    }

    @Test
    @DisplayName("Should return bad request when country deletion fails")
    void deleteCountry_ShouldReturnBadRequest_WhenDeletionFails() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Country not found"))
            .when(countryService).deleteCountry(999L);

        // Act & Assert
        mockMvc.perform(delete("/country/{id}", 999L))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Country not found"));

        verify(countryService, times(1)).deleteCountry(999L);
    }

    // ===== EDGE CASES AND ERROR HANDLING TESTS =====

    @Test
    @DisplayName("Should handle null request body gracefully")
    void createCountry_ShouldHandleNullRequestBody() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid JSON format")
    void createCountry_ShouldHandleInvalidJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle negative ID in path variable")
    void updateCountry_ShouldHandleNegativeId() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/country/{id}", -1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCountry)))
                .andExpect(status().isOk()); // Controller should still process the request
    }

    @Test
    @DisplayName("Should handle zero ID in path variable")
    void updateCountry_ShouldHandleZeroId() throws Exception {
        // Arrange
        when(countryService.updateCountry(eq(0L), any(Country.class)))
            .thenThrow(new IllegalArgumentException("Invalid ID"));

        // Act & Assert
        mockMvc.perform(put("/country/{id}", 0L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCountry)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid ID"));

        verify(countryService, times(1)).updateCountry(eq(0L), any(Country.class));
    }

    @Test
    @DisplayName("Should handle empty country list")
    void getAllCountries_ShouldHandleEmptyList() throws Exception {
        // Arrange
        when(countryService.getAllCountries()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/country"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(countryService, times(1)).getAllCountries();
    }

    @Test
    @DisplayName("Should handle multiple countries")
    void getAllCountries_ShouldHandleMultipleCountries() throws Exception {
        // Arrange
        Country country2 = new Country();
        setId(country2, 2L);
        country2.setCountryCode("CA");
        country2.setCountryName("Canada");
        country2.setRegion("North America");

        List<Country> countries = Arrays.asList(testCountry, country2);
        when(countryService.getAllCountries()).thenReturn(countries);

        // Act & Assert
        mockMvc.perform(get("/country"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].countryCode").value("US"))
                .andExpect(jsonPath("$[1].countryCode").value("CA"));

        verify(countryService, times(1)).getAllCountries();
    }
}
