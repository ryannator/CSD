package csd.tariff.backend.service;

import java.util.List;
import java.util.Optional;

import csd.tariff.backend.model.MfnTariffRate;
import csd.tariff.backend.model.Product;

public interface MfnService {
    
    /**
     * Get MFN tariff rate for a product
     */
    Optional<MfnTariffRate> getMfnTariffRate(String htsCode);

    /**
     * Get all MFN tariff rates
     */
    List<MfnTariffRate> getAllMfnTariffRates();
    
    /**
     * Get products with MFN rates
     */
    List<Product> getProductsWithMfnRates();
    
    /**
     * Get MFN tariff rates for a specific product
     */
    Optional<MfnTariffRate> getMfnTariffRatesForProduct(String htsCode);
    
    /**
     * Create a new MFN tariff rate
     */
    MfnTariffRate createMfnTariffRate(MfnTariffRate mfnRate);
    
    /**
     * Update an existing MFN tariff rate by ID
     */
    MfnTariffRate updateMfnTariffRate(Long id, MfnTariffRate mfnRate);

    /**
     * Update an existing MFN tariff rate by HTS code
     */
    MfnTariffRate updateMfnTariffRateByHtsCode(String htsCode, MfnTariffRate mfnRate);
    
    /**
     * Delete an MFN tariff rate by HTS code
     */
    void deleteMfnTariffRateByHtsCode(String htsCode);

    /**
     * Delete an MFN tariff rate by ID
     */
    void deleteMfnTariffRate(Long id);
}
