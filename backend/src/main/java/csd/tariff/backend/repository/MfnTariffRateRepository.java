package csd.tariff.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import csd.tariff.backend.model.MfnTariffRate;

@Repository
public interface MfnTariffRateRepository extends JpaRepository<MfnTariffRate, Long> {
    
    // Find by product ID
    Optional<MfnTariffRate> findByProductId(Long productId);
    
    // Find by HTS code
    @Query("SELECT mfn FROM MfnTariffRate mfn JOIN mfn.product p WHERE p.hts8 = :hts8")
    Optional<MfnTariffRate> findByHts8(@Param("hts8") String hts8);
    
    // Find products with specific MFN rates
    @Query("SELECT mfn FROM MfnTariffRate mfn WHERE mfn.mfnadValoremRate IS NOT NULL AND mfn.mfnadValoremRate > :minRate")
    List<MfnTariffRate> findByMfnadValoremRateGreaterThan(@Param("minRate") Double minRate);
    
    // Find products with free trade rates
    @Query("SELECT mfn FROM MfnTariffRate mfn WHERE " +
           "LOWER(mfn.mfnTextRate) LIKE '%free%' OR mfn.mfnadValoremRate = 0")
    List<MfnTariffRate> findFreeTradeRates();
    
    // Find products with high tariff rates
    @Query("SELECT mfn FROM MfnTariffRate mfn WHERE mfn.mfnadValoremRate IS NOT NULL AND mfn.mfnadValoremRate > 0.1")
    List<MfnTariffRate> findHighTariffRates();
    
    // Find products with medium tariff rates
    @Query("SELECT mfn FROM MfnTariffRate mfn WHERE mfn.mfnadValoremRate IS NOT NULL AND mfn.mfnadValoremRate > 0.05 AND mfn.mfnadValoremRate <= 0.1")
    List<MfnTariffRate> findMediumTariffRates();
    
    // Find products with low tariff rates
    @Query("SELECT mfn FROM MfnTariffRate mfn WHERE mfn.mfnadValoremRate IS NOT NULL AND mfn.mfnadValoremRate > 0 AND mfn.mfnadValoremRate <= 0.05")
    List<MfnTariffRate> findLowTariffRates();
}