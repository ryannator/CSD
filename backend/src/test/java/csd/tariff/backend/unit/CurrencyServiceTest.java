package csd.tariff.backend.unit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import csd.tariff.backend.model.CurrencyExchangeRate;
import csd.tariff.backend.repository.CurrencyExchangeRateRepository;
import csd.tariff.backend.service.CurrencyService;

/**
 * Tests for CurrencyService
 * Covers currency conversion operations and exchange rate lookups
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CurrencyService Tests")
class CurrencyServiceTest {

    @Mock
    private CurrencyExchangeRateRepository currencyExchangeRateRepository;

    @InjectMocks
    private CurrencyService currencyService;

    private CurrencyExchangeRate testExchangeRate;
    private CurrencyExchangeRate reverseExchangeRate;

    @BeforeEach
    void setUp() {
        // Setup test exchange rate (USD to EUR)
        testExchangeRate = new CurrencyExchangeRate();
        testExchangeRate.setBaseCurrencyCode("USD");
        testExchangeRate.setTargetCurrencyCode("EUR");
        testExchangeRate.setExchangeRate(new BigDecimal("0.85"));
        testExchangeRate.setEffectiveDate(LocalDate.of(2024, 1, 1));

        // Setup reverse exchange rate (EUR to USD)
        reverseExchangeRate = new CurrencyExchangeRate();
        reverseExchangeRate.setBaseCurrencyCode("EUR");
        reverseExchangeRate.setTargetCurrencyCode("USD");
        reverseExchangeRate.setExchangeRate(new BigDecimal("1.18"));
        reverseExchangeRate.setEffectiveDate(LocalDate.of(2024, 1, 1));
    }

    // ===== Currency Conversion Tests (Latest Rate) =====

    @Test
    @DisplayName("Should convert currency using direct exchange rate")
    void convertCurrency_ShouldConvertUsingDirectRate() {
        // Given
        BigDecimal amount = new BigDecimal("100.00");
        when(currencyExchangeRateRepository.findLatestExchangeRate("USD", "EUR", LocalDate.now()))
                .thenReturn(Optional.of(testExchangeRate));

        // When
        BigDecimal result = currencyService.convertCurrency(amount, "USD", "EUR");

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("85.00"), result);
        verify(currencyExchangeRateRepository, times(1)).findLatestExchangeRate("USD", "EUR", LocalDate.now());
    }

    @Test
    @DisplayName("Should convert currency using reverse exchange rate")
    void convertCurrency_ShouldConvertUsingReverseRate() {
        // Given
        BigDecimal amount = new BigDecimal("100.00");
        when(currencyExchangeRateRepository.findLatestExchangeRate("EUR", "USD", LocalDate.now()))
                .thenReturn(Optional.empty());
        when(currencyExchangeRateRepository.findLatestExchangeRate("USD", "EUR", LocalDate.now()))
                .thenReturn(Optional.of(reverseExchangeRate));

        // When
        BigDecimal result = currencyService.convertCurrency(amount, "EUR", "USD");

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("84.75"), result); // 100 / 1.18 = 84.75
        verify(currencyExchangeRateRepository, times(1)).findLatestExchangeRate("EUR", "USD", LocalDate.now());
        verify(currencyExchangeRateRepository, times(1)).findLatestExchangeRate("USD", "EUR", LocalDate.now());
    }

    @Test
    @DisplayName("Should return same amount when currencies are the same")
    void convertCurrency_ShouldReturnSameAmountForSameCurrency() {
        // Given
        BigDecimal amount = new BigDecimal("100.00");

        // When
        BigDecimal result = currencyService.convertCurrency(amount, "USD", "USD");

        // Then
        assertNotNull(result);
        assertEquals(amount, result);
        verify(currencyExchangeRateRepository, never()).findLatestExchangeRate(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Should return original amount when no exchange rate found")
    void convertCurrency_ShouldReturnOriginalAmountWhenNoRateFound() {
        // Given
        BigDecimal amount = new BigDecimal("100.00");
        when(currencyExchangeRateRepository.findLatestExchangeRate("USD", "JPY", LocalDate.now()))
                .thenReturn(Optional.empty());
        when(currencyExchangeRateRepository.findLatestExchangeRate("JPY", "USD", LocalDate.now()))
                .thenReturn(Optional.empty());

        // When
        BigDecimal result = currencyService.convertCurrency(amount, "USD", "JPY");

        // Then
        assertNotNull(result);
        assertEquals(amount, result);
        verify(currencyExchangeRateRepository, times(1)).findLatestExchangeRate("USD", "JPY", LocalDate.now());
        verify(currencyExchangeRateRepository, times(1)).findLatestExchangeRate("JPY", "USD", LocalDate.now());
    }

    @Test
    @DisplayName("Should handle null amount")
    void convertCurrency_ShouldHandleNullAmount() {
        // When
        BigDecimal result = currencyService.convertCurrency(null, "USD", "EUR");

        // Then
        assertNull(result);
        verify(currencyExchangeRateRepository, never()).findLatestExchangeRate(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Should handle null from currency")
    void convertCurrency_ShouldHandleNullFromCurrency() {
        // Given
        BigDecimal amount = new BigDecimal("100.00");

        // When
        BigDecimal result = currencyService.convertCurrency(amount, null, "EUR");

        // Then
        assertNotNull(result);
        assertEquals(amount, result);
        verify(currencyExchangeRateRepository, never()).findLatestExchangeRate(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Should handle null to currency")
    void convertCurrency_ShouldHandleNullToCurrency() {
        // Given
        BigDecimal amount = new BigDecimal("100.00");

        // When
        BigDecimal result = currencyService.convertCurrency(amount, "USD", null);

        // Then
        assertNotNull(result);
        assertEquals(amount, result);
        verify(currencyExchangeRateRepository, never()).findLatestExchangeRate(anyString(), anyString(), any());
    }

    // ===== Currency Conversion Tests (Specific Date) =====

    @Test
    @DisplayName("Should convert currency using specific date exchange rate")
    void convertCurrencyWithDate_ShouldConvertUsingSpecificDateRate() {
        // Given
        BigDecimal amount = new BigDecimal("100.00");
        LocalDate specificDate = LocalDate.of(2024, 1, 15);
        when(currencyExchangeRateRepository.findExchangeRateByDate("USD", "EUR", specificDate))
                .thenReturn(Optional.of(testExchangeRate));

        // When
        BigDecimal result = currencyService.convertCurrency(amount, "USD", "EUR", specificDate);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("85.00"), result);
        verify(currencyExchangeRateRepository, times(1)).findExchangeRateByDate("USD", "EUR", specificDate);
    }

    @Test
    @DisplayName("Should convert currency using reverse rate for specific date")
    void convertCurrencyWithDate_ShouldConvertUsingReverseRateForSpecificDate() {
        // Given
        BigDecimal amount = new BigDecimal("100.00");
        LocalDate specificDate = LocalDate.of(2024, 1, 15);
        when(currencyExchangeRateRepository.findExchangeRateByDate("EUR", "USD", specificDate))
                .thenReturn(Optional.empty());
        when(currencyExchangeRateRepository.findExchangeRateByDate("USD", "EUR", specificDate))
                .thenReturn(Optional.of(reverseExchangeRate));

        // When
        BigDecimal result = currencyService.convertCurrency(amount, "EUR", "USD", specificDate);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("84.75"), result); // 100 / 1.18 = 84.75
        verify(currencyExchangeRateRepository, times(1)).findExchangeRateByDate("EUR", "USD", specificDate);
        verify(currencyExchangeRateRepository, times(1)).findExchangeRateByDate("USD", "EUR", specificDate);
    }

    @Test
    @DisplayName("Should fallback to latest rate when specific date not found")
    void convertCurrencyWithDate_ShouldFallbackToLatestRate() {
        // Given
        BigDecimal amount = new BigDecimal("100.00");
        LocalDate specificDate = LocalDate.of(2024, 1, 15);
        when(currencyExchangeRateRepository.findExchangeRateByDate("USD", "EUR", specificDate))
                .thenReturn(Optional.empty());
        when(currencyExchangeRateRepository.findExchangeRateByDate("EUR", "USD", specificDate))
                .thenReturn(Optional.empty());
        when(currencyExchangeRateRepository.findLatestExchangeRate("USD", "EUR", LocalDate.now()))
                .thenReturn(Optional.of(testExchangeRate));

        // When
        BigDecimal result = currencyService.convertCurrency(amount, "USD", "EUR", specificDate);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("85.00"), result);
        verify(currencyExchangeRateRepository, times(1)).findExchangeRateByDate("USD", "EUR", specificDate);
        verify(currencyExchangeRateRepository, times(1)).findExchangeRateByDate("EUR", "USD", specificDate);
        verify(currencyExchangeRateRepository, times(1)).findLatestExchangeRate("USD", "EUR", LocalDate.now());
    }

    @Test
    @DisplayName("Should return same amount for same currency with specific date")
    void convertCurrencyWithDate_ShouldReturnSameAmountForSameCurrency() {
        // Given
        BigDecimal amount = new BigDecimal("100.00");
        LocalDate specificDate = LocalDate.of(2024, 1, 15);

        // When
        BigDecimal result = currencyService.convertCurrency(amount, "USD", "USD", specificDate);

        // Then
        assertNotNull(result);
        assertEquals(amount, result);
        verify(currencyExchangeRateRepository, never()).findExchangeRateByDate(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Should handle null date")
    void convertCurrencyWithDate_ShouldHandleNullDate() {
        // Given
        BigDecimal amount = new BigDecimal("100.00");

        // When
        BigDecimal result = currencyService.convertCurrency(amount, "USD", "EUR", null);

        // Then
        assertNotNull(result);
        assertEquals(amount, result);
        verify(currencyExchangeRateRepository, never()).findExchangeRateByDate(anyString(), anyString(), any());
    }

    // ===== Exchange Rate Lookup Tests =====

    @Test
    @DisplayName("Should get exchange rate successfully")
    void getExchangeRate_ShouldReturnRate() {
        // Given
        when(currencyExchangeRateRepository.findLatestExchangeRate("USD", "EUR", LocalDate.now()))
                .thenReturn(Optional.of(testExchangeRate));

        // When
        Optional<BigDecimal> result = currencyService.getExchangeRate("USD", "EUR");

        // Then
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("0.85"), result.get());
        verify(currencyExchangeRateRepository, times(1)).findLatestExchangeRate("USD", "EUR", LocalDate.now());
    }

    @Test
    @DisplayName("Should get exchange rate using reverse rate")
    void getExchangeRate_ShouldReturnReverseRate() {
        // Given
        when(currencyExchangeRateRepository.findLatestExchangeRate("EUR", "USD", LocalDate.now()))
                .thenReturn(Optional.empty());
        when(currencyExchangeRateRepository.findLatestExchangeRate("USD", "EUR", LocalDate.now()))
                .thenReturn(Optional.of(reverseExchangeRate));

        // When
        Optional<BigDecimal> result = currencyService.getExchangeRate("EUR", "USD");

        // Then
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("0.847458"), result.get()); // 1 / 1.18 = 0.847458
        verify(currencyExchangeRateRepository, times(1)).findLatestExchangeRate("EUR", "USD", LocalDate.now());
        verify(currencyExchangeRateRepository, times(1)).findLatestExchangeRate("USD", "EUR", LocalDate.now());
    }

    @Test
    @DisplayName("Should return rate of 1 for same currency")
    void getExchangeRate_ShouldReturnOneForSameCurrency() {
        // When
        Optional<BigDecimal> result = currencyService.getExchangeRate("USD", "USD");

        // Then
        assertTrue(result.isPresent());
        assertEquals(BigDecimal.ONE, result.get());
        verify(currencyExchangeRateRepository, never()).findLatestExchangeRate(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Should return empty when no exchange rate found")
    void getExchangeRate_ShouldReturnEmptyWhenNotFound() {
        // Given
        when(currencyExchangeRateRepository.findLatestExchangeRate("USD", "JPY", LocalDate.now()))
                .thenReturn(Optional.empty());
        when(currencyExchangeRateRepository.findLatestExchangeRate("JPY", "USD", LocalDate.now()))
                .thenReturn(Optional.empty());

        // When
        Optional<BigDecimal> result = currencyService.getExchangeRate("USD", "JPY");

        // Then
        assertFalse(result.isPresent());
        verify(currencyExchangeRateRepository, times(1)).findLatestExchangeRate("USD", "JPY", LocalDate.now());
        verify(currencyExchangeRateRepository, times(1)).findLatestExchangeRate("JPY", "USD", LocalDate.now());
    }

    @Test
    @DisplayName("Should return empty when from currency is null")
    void getExchangeRate_ShouldReturnEmptyWhenFromCurrencyIsNull() {
        // When
        Optional<BigDecimal> result = currencyService.getExchangeRate(null, "EUR");

        // Then
        assertFalse(result.isPresent());
        verify(currencyExchangeRateRepository, never()).findLatestExchangeRate(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Should return empty when to currency is null")
    void getExchangeRate_ShouldReturnEmptyWhenToCurrencyIsNull() {
        // When
        Optional<BigDecimal> result = currencyService.getExchangeRate("USD", null);

        // Then
        assertFalse(result.isPresent());
        verify(currencyExchangeRateRepository, never()).findLatestExchangeRate(anyString(), anyString(), any());
    }

    // ===== Edge Cases and Error Handling =====

    @Test
    @DisplayName("Should handle zero amount")
    void shouldHandleZeroAmount() {
        // Given
        BigDecimal amount = BigDecimal.ZERO;
        when(currencyExchangeRateRepository.findLatestExchangeRate("USD", "EUR", LocalDate.now()))
                .thenReturn(Optional.of(testExchangeRate));

        // When
        BigDecimal result = currencyService.convertCurrency(amount, "USD", "EUR");

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("0.00"), result);
        verify(currencyExchangeRateRepository, times(1)).findLatestExchangeRate("USD", "EUR", LocalDate.now());
    }

    @Test
    @DisplayName("Should handle negative amount")
    void shouldHandleNegativeAmount() {
        // Given
        BigDecimal amount = new BigDecimal("-100.00");
        when(currencyExchangeRateRepository.findLatestExchangeRate("USD", "EUR", LocalDate.now()))
                .thenReturn(Optional.of(testExchangeRate));

        // When
        BigDecimal result = currencyService.convertCurrency(amount, "USD", "EUR");

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("-85.00"), result);
        verify(currencyExchangeRateRepository, times(1)).findLatestExchangeRate("USD", "EUR", LocalDate.now());
    }

    @Test
    @DisplayName("Should handle very small exchange rates")
    void shouldHandleVerySmallExchangeRates() {
        // Given
        BigDecimal amount = new BigDecimal("100.00");
        CurrencyExchangeRate smallRate = new CurrencyExchangeRate();
        smallRate.setBaseCurrencyCode("USD");
        smallRate.setTargetCurrencyCode("JPY");
        smallRate.setExchangeRate(new BigDecimal("0.0001"));
        
        when(currencyExchangeRateRepository.findLatestExchangeRate("USD", "JPY", LocalDate.now()))
                .thenReturn(Optional.of(smallRate));

        // When
        BigDecimal result = currencyService.convertCurrency(amount, "USD", "JPY");

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("0.01"), result);
        verify(currencyExchangeRateRepository, times(1)).findLatestExchangeRate("USD", "JPY", LocalDate.now());
    }

    @Test
    @DisplayName("Should handle very large exchange rates")
    void shouldHandleVeryLargeExchangeRates() {
        // Given
        BigDecimal amount = new BigDecimal("100.00");
        CurrencyExchangeRate largeRate = new CurrencyExchangeRate();
        largeRate.setBaseCurrencyCode("USD");
        largeRate.setTargetCurrencyCode("VND");
        largeRate.setExchangeRate(new BigDecimal("25000.00"));
        
        when(currencyExchangeRateRepository.findLatestExchangeRate("USD", "VND", LocalDate.now()))
                .thenReturn(Optional.of(largeRate));

        // When
        BigDecimal result = currencyService.convertCurrency(amount, "USD", "VND");

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("2500000.00"), result);
        verify(currencyExchangeRateRepository, times(1)).findLatestExchangeRate("USD", "VND", LocalDate.now());
    }

    @Test
    @DisplayName("Should handle precision correctly with rounding")
    void shouldHandlePrecisionCorrectlyWithRounding() {
        // Given
        BigDecimal amount = new BigDecimal("100.00");
        CurrencyExchangeRate preciseRate = new CurrencyExchangeRate();
        preciseRate.setBaseCurrencyCode("USD");
        preciseRate.setTargetCurrencyCode("EUR");
        preciseRate.setExchangeRate(new BigDecimal("0.856789")); // 6 decimal places
        
        when(currencyExchangeRateRepository.findLatestExchangeRate("USD", "EUR", LocalDate.now()))
                .thenReturn(Optional.of(preciseRate));

        // When
        BigDecimal result = currencyService.convertCurrency(amount, "USD", "EUR");

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("85.68"), result); // Rounded to 2 decimal places
        verify(currencyExchangeRateRepository, times(1)).findLatestExchangeRate("USD", "EUR", LocalDate.now());
    }
}