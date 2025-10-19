package csd.tariff.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import csd.tariff.backend.model.TradeAgreement;
import csd.tariff.backend.repository.TradeAgreementRepository;

@Service
public class TradeAgreementServiceImpl implements TradeAgreementService {
    
    @Autowired
    private TradeAgreementRepository tradeAgreementRepository;
    
    
    @Override
    public List<TradeAgreement> getAllTradeAgreements() {
        return tradeAgreementRepository.findAll();
    }
    
    @Override
    public Optional<TradeAgreement> getTradeAgreementById(Long id) {
        return tradeAgreementRepository.findById(id);
    }
    
    @Override
    public Optional<TradeAgreement> getTradeAgreementByCode(String agreementCode) {
        return tradeAgreementRepository.findByAgreementCode(agreementCode);
    }
    
    @Override
    public Optional<TradeAgreement> getTradeAgreementByName(String agreementName) {
        return tradeAgreementRepository.findByAgreementName(agreementName);
    }
    
    @Override
    public List<TradeAgreement> getActiveTradeAgreements() {
        return tradeAgreementRepository.findActiveAgreements();
    }
    
    @Override
    public List<TradeAgreement> getTradeAgreementsByCountry(String countryCode) {
        return tradeAgreementRepository.findByParticipatingCountry(countryCode.toUpperCase());
    }
    
    @Override
    public List<TradeAgreement> getTradeAgreementsBetweenCountries(String country1, String country2) {
        return tradeAgreementRepository.findBetweenCountries(
            country1.toUpperCase(), 
            country2.toUpperCase()
        );
    }
    
    @Override
    public List<TradeAgreement> getTradeAgreementsEffectiveOnDate(LocalDate date) {
        return tradeAgreementRepository.findEffectiveOnDate(date);
    }
    
    @Override
    public Optional<TradeAgreement> getApplicableTradeAgreement(String countryCode, String htsCode) {
        // Return the first active agreement with the country
        // In a more complex system, this could consider product-specific rules
        List<TradeAgreement> agreements = getTradeAgreementsByCountry(countryCode);
        return agreements.stream()
            .filter(agreement -> agreement.getEffectiveDate() != null && 
                                agreement.getEffectiveDate().isBefore(LocalDate.now()) &&
                                (agreement.getExpirationDate() == null || 
                                 agreement.getExpirationDate().isAfter(LocalDate.now())))
            .findFirst();
    }
    
    /**
     * Get trade agreements expiring on a specific date
     */
    @Override
    public List<TradeAgreement> getTradeAgreementsExpiringOnDate(LocalDate date) {
        return tradeAgreementRepository.findExpiringOnDate(date);
    }
    
    /**
     * Get trade agreements active between effective and expiration date
     */
    @Override
    public List<TradeAgreement> getTradeAgreementsActiveBetweenDates(LocalDate startDate, LocalDate endDate) {
        return tradeAgreementRepository.findActiveBetweenDates(startDate, endDate);
    }
    
    @Override
    public TradeAgreement createTradeAgreement(TradeAgreement tradeAgreement) {
        // Validate required fields
        if (tradeAgreement.getAgreementName() == null || tradeAgreement.getAgreementName().trim().isEmpty()) {
            throw new IllegalArgumentException("Agreement name is required");
        }
        if (tradeAgreement.getAgreementCode() == null || tradeAgreement.getAgreementCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Agreement code is required");
        }
        
        // Set default values if not provided
        if (tradeAgreement.getIsMultilateral() == null) {
            tradeAgreement.setIsMultilateral(false);
        }
        
        return tradeAgreementRepository.save(tradeAgreement);
    }
    
    @Override
    public TradeAgreement updateTradeAgreement(Long id, TradeAgreement tradeAgreement) {
        Optional<TradeAgreement> existingAgreement = tradeAgreementRepository.findById(id);
        if (existingAgreement.isEmpty()) {
            throw new IllegalArgumentException("Trade agreement not found with ID: " + id);
        }
        
        TradeAgreement existing = existingAgreement.get();
        existing.setAgreementName(tradeAgreement.getAgreementName());
        existing.setAgreementCode(tradeAgreement.getAgreementCode());
        existing.setAgreementType(tradeAgreement.getAgreementType());
        existing.setEffectiveDate(tradeAgreement.getEffectiveDate());
        existing.setExpirationDate(tradeAgreement.getExpirationDate());
        existing.setIsMultilateral(tradeAgreement.getIsMultilateral());
        existing.setUpdatedAt(LocalDateTime.now());
        // Note: participatingCountries, tariffReductionSchedule, and notes are now handled through separate entities
        // in the schema (AgreementParticipant, AgreementRate, ProductNote)
        
        return tradeAgreementRepository.save(existing);
    }
    
    @Override
    public TradeAgreement updateTradeAgreementByCode(String agreementCode, TradeAgreement tradeAgreement) {
        Optional<TradeAgreement> existingAgreement = tradeAgreementRepository.findByAgreementCode(agreementCode);
        if (existingAgreement.isEmpty()) {
            throw new IllegalArgumentException("Trade agreement not found with code: " + agreementCode);
        }
        
        TradeAgreement existing = existingAgreement.get();
        existing.setAgreementName(tradeAgreement.getAgreementName());
        existing.setAgreementCode(tradeAgreement.getAgreementCode());
        existing.setAgreementType(tradeAgreement.getAgreementType());
        existing.setEffectiveDate(tradeAgreement.getEffectiveDate());
        existing.setExpirationDate(tradeAgreement.getExpirationDate());
        existing.setIsMultilateral(tradeAgreement.getIsMultilateral());
        existing.setUpdatedAt(LocalDateTime.now());
        
        return tradeAgreementRepository.save(existing);
    }
    
    @Override
    public void deleteTradeAgreementByCode(String agreementCode) {
        Optional<TradeAgreement> existingAgreement = tradeAgreementRepository.findByAgreementCode(agreementCode);
        if (existingAgreement.isEmpty()) {
            throw new IllegalArgumentException("Trade agreement not found with code: " + agreementCode);
        }
        tradeAgreementRepository.deleteById(existingAgreement.get().getId());
    }
}
