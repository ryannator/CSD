package csd.tariff.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import csd.tariff.backend.model.TradeAgreement;

public interface TradeAgreementService {
    
    /**
     * Get all trade agreements
     */
    List<TradeAgreement> getAllTradeAgreements();
    
    /**
     * Get trade agreement by ID
     */
    Optional<TradeAgreement> getTradeAgreementById(Long id);
    
    /**
     * Get trade agreement by agreement code
     */
    Optional<TradeAgreement> getTradeAgreementByCode(String agreementCode);
    
    /**
     * Get trade agreement by agreement name
     */
    Optional<TradeAgreement> getTradeAgreementByName(String agreementName);
    
    /**
     * Get all active trade agreements
     */
    List<TradeAgreement> getActiveTradeAgreements();
    
    /**
     * Get trade agreements for a specific country
     */
    List<TradeAgreement> getTradeAgreementsByCountry(String countryCode);
    
    /**
     * Get trade agreements between two countries
     */
    List<TradeAgreement> getTradeAgreementsBetweenCountries(String country1, String country2);
    
    /**
     * Get trade agreements effective on a specific date
     */
    List<TradeAgreement> getTradeAgreementsEffectiveOnDate(LocalDate date);
    
    /**
     * Get applicable trade agreement for a country and product
     */
    Optional<TradeAgreement> getApplicableTradeAgreement(String countryCode, String htsCode);
    
    /**
     * Get trade agreements expiring on a specific date
     */
    List<TradeAgreement> getTradeAgreementsExpiringOnDate(LocalDate date);
    
    /**
     * Get trade agreements active between effective and expiration date
     */
    List<TradeAgreement> getTradeAgreementsActiveBetweenDates(LocalDate startDate, LocalDate endDate);
    
    /**
     * Create a new trade agreement
     */
    TradeAgreement createTradeAgreement(TradeAgreement tradeAgreement);
    
    /**
     * Update an existing trade agreement by ID
     */
    TradeAgreement updateTradeAgreement(Long id, TradeAgreement tradeAgreement);

    /**
     * Update an existing trade agreement by code
     */
    TradeAgreement updateTradeAgreementByCode(String agreementCode, TradeAgreement tradeAgreement);
    
    /**
     * Delete a trade agreement by code
     */
    void deleteTradeAgreementByCode(String agreementCode);
}
