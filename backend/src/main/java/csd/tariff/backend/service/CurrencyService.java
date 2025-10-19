package csd.tariff.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import csd.tariff.backend.model.CurrencyExchangeRate;
import csd.tariff.backend.repository.CurrencyExchangeRateRepository;

@Service
public class CurrencyService {
    
    @Autowired
    private CurrencyExchangeRateRepository currencyExchangeRateRepository;
    
    /**
     * Convert amount from one currency to another using the latest exchange rate
     */
    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (amount == null || fromCurrency == null || toCurrency == null) {
            return amount;
        }
        
        // If same currency, return as is
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        
        // Try to find direct exchange rate
        Optional<CurrencyExchangeRate> directRate = currencyExchangeRateRepository
            .findLatestExchangeRate(fromCurrency, toCurrency, LocalDate.now());
        
        if (directRate.isPresent()) {
            return amount.multiply(directRate.get().getExchangeRate())
                        .setScale(2, RoundingMode.HALF_UP);
        }
        
        // Try reverse exchange rate (1 / rate)
        Optional<CurrencyExchangeRate> reverseRate = currencyExchangeRateRepository
            .findLatestExchangeRate(toCurrency, fromCurrency, LocalDate.now());
        
        if (reverseRate.isPresent()) {
            BigDecimal reverseExchangeRate = BigDecimal.ONE.divide(
                reverseRate.get().getExchangeRate(), 6, RoundingMode.HALF_UP);
            return amount.multiply(reverseExchangeRate)
                        .setScale(2, RoundingMode.HALF_UP);
        }
        
        // If no exchange rate found, return original amount
        return amount;
    }
    
    /**
     * Convert amount from one currency to another using exchange rate for a specific date
     */
    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency, LocalDate date) {
        if (amount == null || fromCurrency == null || toCurrency == null || date == null) {
            return amount;
        }
        
        // If same currency, return as is
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        
        // Try to find direct exchange rate for the specific date
        Optional<CurrencyExchangeRate> directRate = currencyExchangeRateRepository
            .findExchangeRateByDate(fromCurrency, toCurrency, date);
        
        if (directRate.isPresent()) {
            return amount.multiply(directRate.get().getExchangeRate())
                        .setScale(2, RoundingMode.HALF_UP);
        }
        
        // Try reverse exchange rate for the specific date
        Optional<CurrencyExchangeRate> reverseRate = currencyExchangeRateRepository
            .findExchangeRateByDate(toCurrency, fromCurrency, date);
        
        if (reverseRate.isPresent()) {
            BigDecimal reverseExchangeRate = BigDecimal.ONE.divide(
                reverseRate.get().getExchangeRate(), 6, RoundingMode.HALF_UP);
            return amount.multiply(reverseExchangeRate)
                        .setScale(2, RoundingMode.HALF_UP);
        }
        
        // Fallback to latest rate if specific date not found
        return convertCurrency(amount, fromCurrency, toCurrency);
    }
    
    /**
     * Get exchange rate between two currencies
     */
    public Optional<BigDecimal> getExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency == null || toCurrency == null) {
            return Optional.empty();
        }
        
        if (fromCurrency.equals(toCurrency)) {
            return Optional.of(BigDecimal.ONE);
        }
        
        Optional<CurrencyExchangeRate> directRate = currencyExchangeRateRepository
            .findLatestExchangeRate(fromCurrency, toCurrency, LocalDate.now());
        
        if (directRate.isPresent()) {
            return Optional.of(directRate.get().getExchangeRate());
        }
        
        // Try reverse rate
        Optional<CurrencyExchangeRate> reverseRate = currencyExchangeRateRepository
            .findLatestExchangeRate(toCurrency, fromCurrency, LocalDate.now());
        
        if (reverseRate.isPresent()) {
            return Optional.of(BigDecimal.ONE.divide(
                reverseRate.get().getExchangeRate(), 6, RoundingMode.HALF_UP));
        }
        
        return Optional.empty();
    }
}
