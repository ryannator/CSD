package csd.tariff.backend.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import csd.tariff.backend.model.CurrencyExchangeRate;

@Repository
public interface CurrencyExchangeRateRepository extends JpaRepository<CurrencyExchangeRate, Long> {
    
    /**
     * Find the most recent exchange rate between two currencies
     */
    @Query("SELECT cer FROM CurrencyExchangeRate cer " +
           "WHERE cer.baseCurrencyCode = :baseCurrencyCode " +
           "AND cer.targetCurrencyCode = :targetCurrencyCode " +
           "AND cer.effectiveDate <= :date " +
           "ORDER BY cer.effectiveDate DESC")
    Optional<CurrencyExchangeRate> findLatestExchangeRate(
        @Param("baseCurrencyCode") String baseCurrencyCode,
        @Param("targetCurrencyCode") String targetCurrencyCode,
        @Param("date") LocalDate date);
    
    /**
     * Find exchange rate for a specific date
     */
    @Query("SELECT cer FROM CurrencyExchangeRate cer " +
           "WHERE cer.baseCurrencyCode = :baseCurrencyCode " +
           "AND cer.targetCurrencyCode = :targetCurrencyCode " +
           "AND cer.effectiveDate = :date")
    Optional<CurrencyExchangeRate> findExchangeRateByDate(
        @Param("baseCurrencyCode") String baseCurrencyCode,
        @Param("targetCurrencyCode") String targetCurrencyCode,
        @Param("date") LocalDate date);
}
