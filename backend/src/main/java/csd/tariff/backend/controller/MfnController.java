package csd.tariff.backend.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import csd.tariff.backend.model.MfnTariffRate;
import csd.tariff.backend.model.Product;
import csd.tariff.backend.service.MfnService;
import csd.tariff.backend.service.TariffCalculationService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/mfn")
@CrossOrigin(origins = "*")
public class MfnController {
    
    @Autowired
    private TariffCalculationService TariffCalculationService;

    @Autowired
    private MfnService MfnService;
    
    /**
     * Get all MFN tariff rates
     */
    @GetMapping("")
    public ResponseEntity<List<MfnTariffRate>> getAllMfnTariffRates() {
        List<MfnTariffRate> rates = MfnService.getAllMfnTariffRates();
        return ResponseEntity.ok(rates);
    }
    
    /**
     * Get MFN tariff rates for a specific product
     */
    @GetMapping("/mfn-rate/{htsCode}")
    public ResponseEntity<Optional<MfnTariffRate>> getMfnTariffRatesForProduct(@PathVariable String htsCode) {
        Optional<MfnTariffRate> rates = MfnService.getMfnTariffRatesForProduct(htsCode);
        return ResponseEntity.ok(rates);
    }

    /**
     * Create a new MFN tariff rate for a product
     */
    @PostMapping("/mfn-rate/{htsCode}")
    public ResponseEntity<?> createMfnTariffRate(@PathVariable String htsCode, @Valid @RequestBody Map<String, Object> requestData) {
        try {
            // Validate HTS code exists and get the actual Product entity
            Optional<Product> product = TariffCalculationService.findByHtsCode(htsCode);
            if (product.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Product not found",
                    "message", "HTS code not found: " + htsCode
                ));
            }
            
            // Create MFN rate with the actual Product entity
            MfnTariffRate mfnRate = new MfnTariffRate();
            mfnRate.setProduct(product.get());
            mfnRate.setMfnTextRate((String) requestData.get("mfnTextRate"));
            mfnRate.setMfnRateTypeCode((String) requestData.get("mfnRateTypeCode"));
            mfnRate.setMfnadValoremRate(new BigDecimal(requestData.get("mfnadValoremRate").toString()));
            mfnRate.setMfnSpecificRate(new BigDecimal(requestData.get("mfnSpecificRate").toString()));
            mfnRate.setMfnOtherRate(new BigDecimal(requestData.get("mfnOtherRate").toString()));
            
            // Handle dates if provided
            if (requestData.get("beginEffectDate") != null) {
                mfnRate.setBeginEffectDate(java.time.LocalDate.parse(requestData.get("beginEffectDate").toString()));
            }
            if (requestData.get("endEffectiveDate") != null) {
                mfnRate.setEndEffectiveDate(java.time.LocalDate.parse(requestData.get("endEffectiveDate").toString()));
            }
            
            MfnTariffRate created = MfnService.createMfnTariffRate(mfnRate);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to create MFN tariff rate",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Update an existing MFN tariff rate
     */
    @PutMapping("/mfn-rate/{htsCode}")
    public ResponseEntity<?> updateMfnTariffRate(@PathVariable String htsCode, @Valid @RequestBody MfnTariffRate mfnRate) {
        try {
            MfnTariffRate updated = MfnService.updateMfnTariffRateByHtsCode(htsCode, mfnRate);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to update MFN tariff rate",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Delete an MFN tariff rate
     */
    @DeleteMapping("/mfn-rates/{htsCode}")
    public ResponseEntity<?> deleteMfnTariffRate(@PathVariable String htsCode) {
        try {
            MfnService.deleteMfnTariffRateByHtsCode(htsCode);
            return ResponseEntity.ok(Map.of("message", "MFN tariff rate deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to delete MFN tariff rate",
                "message", e.getMessage()
            ));
        }
    }

}