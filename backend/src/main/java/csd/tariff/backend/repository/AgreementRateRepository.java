package csd.tariff.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import csd.tariff.backend.model.AgreementRate;

@Repository
public interface AgreementRateRepository extends JpaRepository<AgreementRate, Long> {
    
    // Find by product ID
    List<AgreementRate> findByProductId(Long productId);
    
    // Find by agreement ID
    List<AgreementRate> findByAgreementId(Long agreementId);
    
    // Find by country ID
    List<AgreementRate> findByCountryId(Long countryId);
    
    // Find by HTS code
    @Query("SELECT atr FROM AgreementRate atr JOIN atr.product p WHERE p.hts8 = :hts8")
    List<AgreementRate> findByHts8(@Param("hts8") String hts8);
    
    // Find by agreement code
    @Query("SELECT atr FROM AgreementRate atr JOIN atr.agreement ta WHERE ta.agreementCode = :agreementCode")
    List<AgreementRate> findByAgreementCode(@Param("agreementCode") String agreementCode);
    
    // Find by country code
    @Query("SELECT atr FROM AgreementRate atr JOIN atr.country c WHERE c.countryCode = :countryCode")
    List<AgreementRate> findByCountryCode(@Param("countryCode") String countryCode);
    
    // Find rates for specific product and country
    @Query("SELECT atr FROM AgreementRate atr JOIN atr.product p JOIN atr.country c WHERE p.hts8 = :hts8 AND c.countryCode = :countryCode")
    List<AgreementRate> findByHts8AndCountryCode(@Param("hts8") String hts8, @Param("countryCode") String countryCode);
    
    // Find rates for specific product and agreement
    @Query("SELECT atr FROM AgreementRate atr JOIN atr.product p JOIN atr.agreement ta WHERE p.hts8 = :hts8 AND ta.agreementCode = :agreementCode")
    List<AgreementRate> findByHts8AndAgreementCode(@Param("hts8") String hts8, @Param("agreementCode") String agreementCode);
    
    // Find products with trade agreement benefits
    @Query("SELECT DISTINCT atr FROM AgreementRate atr WHERE atr.adValoremRate IS NOT NULL AND atr.adValoremRate < 0.1")
    List<AgreementRate> findProductsWithTradeAgreementBenefits();
    
    // Count total agreement rates
    long count();
}