package csd.tariff.backend.unit;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import csd.tariff.backend.model.Country;
import csd.tariff.backend.repository.CountryRepository;
import csd.tariff.backend.service.CountryServiceImpl;

/**
 * Tests for CountryServiceImpl
 * Covers all country management operations including CRUD operations
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CountryServiceImpl Tests")
class CountryServiceImplTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryServiceImpl countryService;

    private Country testCountry;
    private Country existingCountry;

    private void setId(Country country, Long id) {
        try {
            Field idField = Country.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(country, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID", e);
        }
    }

    @BeforeEach
    void setUp() {
        // Setup test country
        testCountry = new Country();
        testCountry.setCountryName("United States");
        testCountry.setCountryCode("US");
        testCountry.setCountryNameShort("USA");
        testCountry.setRegion("North America");
        testCountry.setContinent("North America");
        testCountry.setCurrency("USD");

        // Setup existing country
        existingCountry = new Country();
        existingCountry.setCountryName("Canada");
        existingCountry.setCountryCode("CA");
        existingCountry.setCountryNameShort("CAN");
        existingCountry.setRegion("North America");
        existingCountry.setContinent("North America");
        existingCountry.setCurrency("CAD");
        setId(existingCountry, 1L);
    }

    // ===== Get All Countries Tests =====

    @Test
    @DisplayName("Should get all countries successfully")
    void getAllCountries_ShouldReturnAllCountries() {
        // Given
        List<Country> countries = Arrays.asList(testCountry, existingCountry);
        when(countryRepository.findAll()).thenReturn(countries);

        // When
        List<Country> result = countryService.getAllCountries();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testCountry));
        assertTrue(result.contains(existingCountry));
        verify(countryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no countries exist")
    void getAllCountries_ShouldReturnEmptyList() {
        // Given
        when(countryRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Country> result = countryService.getAllCountries();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(countryRepository, times(1)).findAll();
    }

    // ===== Create Country Tests =====

    @Test
    @DisplayName("Should create country successfully")
    void createCountry_ShouldCreateSuccessfully() {
        // Given
        Country newCountry = new Country();
        newCountry.setCountryName("Mexico");
        newCountry.setCountryCode("MX");
        newCountry.setCountryNameShort("MEX");
        newCountry.setRegion("North America");
        newCountry.setContinent("North America");
        newCountry.setCurrency("MXN");

        when(countryRepository.findByCountryCode("MX")).thenReturn(Optional.empty());
        when(countryRepository.save(any(Country.class))).thenReturn(newCountry);

        // When
        Country result = countryService.createCountry(newCountry);

        // Then
        assertNotNull(result);
        assertEquals("Mexico", result.getCountryName());
        assertEquals("MX", result.getCountryCode());
        assertEquals("MEX", result.getCountryNameShort());
        assertEquals("North America", result.getRegion());
        assertEquals("North America", result.getContinent());
        assertEquals("MXN", result.getCurrency());
        verify(countryRepository, times(1)).findByCountryCode("MX");
        verify(countryRepository, times(1)).save(any(Country.class));
    }

    @Test
    @DisplayName("Should normalize country code to uppercase")
    void createCountry_ShouldNormalizeCountryCodeToUppercase() {
        // Given
        Country newCountry = new Country();
        newCountry.setCountryName("Mexico");
        newCountry.setCountryCode("mx"); // lowercase
        newCountry.setCurrency("mxn"); // lowercase

        when(countryRepository.findByCountryCode("MX")).thenReturn(Optional.empty());
        when(countryRepository.save(any(Country.class))).thenAnswer(invocation -> {
            Country saved = invocation.getArgument(0);
            assertEquals("MX", saved.getCountryCode());
            assertEquals("MXN", saved.getCurrency());
            return saved;
        });

        // When
        Country result = countryService.createCountry(newCountry);

        // Then
        assertNotNull(result);
        verify(countryRepository, times(1)).findByCountryCode("MX");
        verify(countryRepository, times(1)).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw exception when country payload is null")
    void createCountry_ShouldThrowExceptionWhenPayloadIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            countryService.createCountry(null);
        });

        assertEquals("Country payload is required.", exception.getMessage());
        verify(countryRepository, never()).findByCountryCode(anyString());
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw exception when country name is null")
    void createCountry_ShouldThrowExceptionWhenNameIsNull() {
        // Given
        Country invalidCountry = new Country();
        invalidCountry.setCountryName(null);
        invalidCountry.setCountryCode("US");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            countryService.createCountry(invalidCountry);
        });

        assertEquals("Country name is required.", exception.getMessage());
        verify(countryRepository, never()).findByCountryCode(anyString());
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw exception when country name is blank")
    void createCountry_ShouldThrowExceptionWhenNameIsBlank() {
        // Given
        Country invalidCountry = new Country();
        invalidCountry.setCountryName("   "); // blank
        invalidCountry.setCountryCode("US");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            countryService.createCountry(invalidCountry);
        });

        assertEquals("Country name is required.", exception.getMessage());
        verify(countryRepository, never()).findByCountryCode(anyString());
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw exception when country code is null")
    void createCountry_ShouldThrowExceptionWhenCodeIsNull() {
        // Given
        Country invalidCountry = new Country();
        invalidCountry.setCountryName("United States");
        invalidCountry.setCountryCode(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            countryService.createCountry(invalidCountry);
        });

        assertEquals("Country code is required.", exception.getMessage());
        verify(countryRepository, never()).findByCountryCode(anyString());
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw exception when country code is blank")
    void createCountry_ShouldThrowExceptionWhenCodeIsBlank() {
        // Given
        Country invalidCountry = new Country();
        invalidCountry.setCountryName("United States");
        invalidCountry.setCountryCode("   "); // blank

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            countryService.createCountry(invalidCountry);
        });

        assertEquals("Country code is required.", exception.getMessage());
        verify(countryRepository, never()).findByCountryCode(anyString());
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw exception when country code already exists")
    void createCountry_ShouldThrowExceptionWhenCodeExists() {
        // Given
        Country newCountry = new Country();
        newCountry.setCountryName("United States");
        newCountry.setCountryCode("US");

        when(countryRepository.findByCountryCode("US")).thenReturn(Optional.of(existingCountry));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            countryService.createCountry(newCountry);
        });

        assertEquals("A country with this code already exists.", exception.getMessage());
        verify(countryRepository, times(1)).findByCountryCode("US");
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    @DisplayName("Should handle null optional fields gracefully")
    void createCountry_ShouldHandleNullOptionalFields() {
        // Given
        Country newCountry = new Country();
        newCountry.setCountryName("Mexico");
        newCountry.setCountryCode("MX");
        newCountry.setCountryNameShort(null);
        newCountry.setRegion(null);
        newCountry.setContinent(null);
        newCountry.setCurrency(null);

        when(countryRepository.findByCountryCode("MX")).thenReturn(Optional.empty());
        when(countryRepository.save(any(Country.class))).thenAnswer(invocation -> {
            Country saved = invocation.getArgument(0);
            assertNull(saved.getCountryNameShort());
            assertNull(saved.getRegion());
            assertNull(saved.getContinent());
            assertNull(saved.getCurrency());
            return saved;
        });

        // When
        Country result = countryService.createCountry(newCountry);

        // Then
        assertNotNull(result);
        verify(countryRepository, times(1)).findByCountryCode("MX");
        verify(countryRepository, times(1)).save(any(Country.class));
    }

    // ===== Update Country Tests =====

    @Test
    @DisplayName("Should update country successfully")
    void updateCountry_ShouldUpdateSuccessfully() {
        // Given
        Country updateRequest = new Country();
        updateRequest.setCountryName("United States of America");
        updateRequest.setCountryCode("US");
        updateRequest.setCountryNameShort("USA");
        updateRequest.setRegion("North America");
        updateRequest.setContinent("North America");
        updateRequest.setCurrency("USD");

        when(countryRepository.findById(1L)).thenReturn(Optional.of(existingCountry));
        when(countryRepository.findByCountryCode("US")).thenReturn(Optional.of(existingCountry));
        when(countryRepository.save(any(Country.class))).thenReturn(existingCountry);

        // When
        Country result = countryService.updateCountry(1L, updateRequest);

        // Then
        assertNotNull(result);
        verify(countryRepository, times(1)).findById(1L);
        verify(countryRepository, times(1)).findByCountryCode("US");
        verify(countryRepository, times(1)).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw exception when updating with null ID")
    void updateCountry_ShouldThrowExceptionWhenIdIsNull() {
        // Given
        Country updateRequest = new Country();
        updateRequest.setCountryName("United States");
        updateRequest.setCountryCode("US");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            countryService.updateCountry(null, updateRequest);
        });

        assertEquals("Country ID is required.", exception.getMessage());
        verify(countryRepository, never()).findById(anyLong());
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent country")
    void updateCountry_ShouldThrowExceptionWhenCountryNotFound() {
        // Given
        Country updateRequest = new Country();
        updateRequest.setCountryName("United States");
        updateRequest.setCountryCode("US");

        when(countryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            countryService.updateCountry(999L, updateRequest);
        });

        assertEquals("Country not found with ID: 999", exception.getMessage());
        verify(countryRepository, times(1)).findById(999L);
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw exception when updating with null payload")
    void updateCountry_ShouldThrowExceptionWhenPayloadIsNull() {
        // Given
        when(countryRepository.findById(1L)).thenReturn(Optional.of(existingCountry));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            countryService.updateCountry(1L, null);
        });

        assertEquals("Country payload is required.", exception.getMessage());
        verify(countryRepository, times(1)).findById(1L);
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw exception when updating with null country name")
    void updateCountry_ShouldThrowExceptionWhenNameIsNull() {
        // Given
        Country updateRequest = new Country();
        updateRequest.setCountryName(null);
        updateRequest.setCountryCode("US");

        when(countryRepository.findById(1L)).thenReturn(Optional.of(existingCountry));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            countryService.updateCountry(1L, updateRequest);
        });

        assertEquals("Country name is required.", exception.getMessage());
        verify(countryRepository, times(1)).findById(1L);
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw exception when updating with null country code")
    void updateCountry_ShouldThrowExceptionWhenCodeIsNull() {
        // Given
        Country updateRequest = new Country();
        updateRequest.setCountryName("United States");
        updateRequest.setCountryCode(null);

        when(countryRepository.findById(1L)).thenReturn(Optional.of(existingCountry));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            countryService.updateCountry(1L, updateRequest);
        });

        assertEquals("Country code is required.", exception.getMessage());
        verify(countryRepository, times(1)).findById(1L);
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw exception when updating with existing country code")
    void updateCountry_ShouldThrowExceptionWhenCodeExists() {
        // Given
        Country updateRequest = new Country();
        updateRequest.setCountryName("United States");
        updateRequest.setCountryCode("US");

        Country otherCountry = new Country();
        otherCountry.setCountryCode("US");
        setId(otherCountry, 2L);

        when(countryRepository.findById(1L)).thenReturn(Optional.of(existingCountry));
        when(countryRepository.findByCountryCode("US")).thenReturn(Optional.of(otherCountry));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            countryService.updateCountry(1L, updateRequest);
        });

        assertEquals("A country with this code already exists.", exception.getMessage());
        verify(countryRepository, times(1)).findById(1L);
        verify(countryRepository, times(1)).findByCountryCode("US");
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    @DisplayName("Should allow updating to same country code")
    void updateCountry_ShouldAllowSameCountryCode() {
        // Given
        Country updateRequest = new Country();
        updateRequest.setCountryName("Updated Canada");
        updateRequest.setCountryCode("CA");

        when(countryRepository.findById(1L)).thenReturn(Optional.of(existingCountry));
        when(countryRepository.findByCountryCode("CA")).thenReturn(Optional.of(existingCountry));
        when(countryRepository.save(any(Country.class))).thenReturn(existingCountry);

        // When
        Country result = countryService.updateCountry(1L, updateRequest);

        // Then
        assertNotNull(result);
        verify(countryRepository, times(1)).findById(1L);
        verify(countryRepository, times(1)).findByCountryCode("CA");
        verify(countryRepository, times(1)).save(any(Country.class));
    }

    // ===== Delete Country Tests =====

    @Test
    @DisplayName("Should delete country successfully")
    void deleteCountry_ShouldDeleteSuccessfully() {
        // Given
        when(countryRepository.findById(1L)).thenReturn(Optional.of(existingCountry));
        doNothing().when(countryRepository).delete(existingCountry);

        // When
        countryService.deleteCountry(1L);

        // Then
        verify(countryRepository, times(1)).findById(1L);
        verify(countryRepository, times(1)).delete(existingCountry);
    }

    @Test
    @DisplayName("Should throw exception when deleting with null ID")
    void deleteCountry_ShouldThrowExceptionWhenIdIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            countryService.deleteCountry(null);
        });

        assertEquals("Country ID is required.", exception.getMessage());
        verify(countryRepository, never()).findById(anyLong());
        verify(countryRepository, never()).delete(any(Country.class));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent country")
    void deleteCountry_ShouldThrowExceptionWhenCountryNotFound() {
        // Given
        when(countryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            countryService.deleteCountry(999L);
        });

        assertEquals("Country not found with ID: 999", exception.getMessage());
        verify(countryRepository, times(1)).findById(999L);
        verify(countryRepository, never()).delete(any(Country.class));
    }

    // ===== Edge Cases and Error Handling =====

    @Test
    @DisplayName("Should handle empty string inputs gracefully")
    void shouldHandleEmptyStringInputs() {
        // Given
        Country countryWithEmptyStrings = new Country();
        countryWithEmptyStrings.setCountryName("Mexico");
        countryWithEmptyStrings.setCountryCode("MX");
        countryWithEmptyStrings.setCountryNameShort("");
        countryWithEmptyStrings.setRegion("");
        countryWithEmptyStrings.setContinent("");
        countryWithEmptyStrings.setCurrency("");

        when(countryRepository.findByCountryCode("MX")).thenReturn(Optional.empty());
        when(countryRepository.save(any(Country.class))).thenAnswer(invocation -> {
            Country saved = invocation.getArgument(0);
            assertNull(saved.getCountryNameShort());
            assertNull(saved.getRegion());
            assertNull(saved.getContinent());
            assertNull(saved.getCurrency());
            return saved;
        });

        // When
        Country result = countryService.createCountry(countryWithEmptyStrings);

        // Then
        assertNotNull(result);
        verify(countryRepository, times(1)).findByCountryCode("MX");
        verify(countryRepository, times(1)).save(any(Country.class));
    }

    @Test
    @DisplayName("Should handle whitespace-only inputs gracefully")
    void shouldHandleWhitespaceOnlyInputs() {
        // Given
        Country countryWithWhitespace = new Country();
        countryWithWhitespace.setCountryName("Mexico");
        countryWithWhitespace.setCountryCode("MX");
        countryWithWhitespace.setCountryNameShort("   ");
        countryWithWhitespace.setRegion("   ");
        countryWithWhitespace.setContinent("   ");
        countryWithWhitespace.setCurrency("   ");

        when(countryRepository.findByCountryCode("MX")).thenReturn(Optional.empty());
        when(countryRepository.save(any(Country.class))).thenAnswer(invocation -> {
            Country saved = invocation.getArgument(0);
            assertNull(saved.getCountryNameShort());
            assertNull(saved.getRegion());
            assertNull(saved.getContinent());
            assertNull(saved.getCurrency());
            return saved;
        });

        // When
        Country result = countryService.createCountry(countryWithWhitespace);

        // Then
        assertNotNull(result);
        verify(countryRepository, times(1)).findByCountryCode("MX");
        verify(countryRepository, times(1)).save(any(Country.class));
    }
}