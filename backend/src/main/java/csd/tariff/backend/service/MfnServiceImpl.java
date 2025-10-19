package csd.tariff.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import csd.tariff.backend.model.MfnTariffRate;
import csd.tariff.backend.model.Product;
import csd.tariff.backend.repository.MfnTariffRateRepository;
import csd.tariff.backend.repository.ProductRepository;

@Service
public class MfnServiceImpl implements MfnService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private MfnTariffRateRepository mfnTariffRateRepository;
    
    @Override
    public List<MfnTariffRate> getAllMfnTariffRates() {
        return mfnTariffRateRepository.findAll();
    }
    
    @Override
    public MfnTariffRate createMfnTariffRate(MfnTariffRate mfnRate) {
        return mfnTariffRateRepository.save(mfnRate);
    }
    
    @Override
    public MfnTariffRate updateMfnTariffRate(Long id, MfnTariffRate mfnRate) {
        Optional<MfnTariffRate> existingRate = mfnTariffRateRepository.findById(id);
        if (existingRate.isPresent()) {
            MfnTariffRate rate = existingRate.get();
            rate.setProduct(mfnRate.getProduct());
            rate.setMfnTextRate(mfnRate.getMfnTextRate());
            rate.setMfnRateTypeCode(mfnRate.getMfnRateTypeCode());
            rate.setMfnadValoremRate(mfnRate.getMfnadValoremRate());
            rate.setMfnSpecificRate(mfnRate.getMfnSpecificRate());
            rate.setMfnOtherRate(mfnRate.getMfnOtherRate());
            rate.setBeginEffectDate(mfnRate.getBeginEffectDate());
            rate.setEndEffectiveDate(mfnRate.getEndEffectiveDate());
            return mfnTariffRateRepository.save(rate);
        }
        return null;
    }
    
    @Override
    public Optional<MfnTariffRate> getMfnTariffRate(String htsCode) {
        return mfnTariffRateRepository.findByHts8(htsCode);
    }

    @Override
    public void deleteMfnTariffRate(Long id) {
        mfnTariffRateRepository.deleteById(id);
    }
    
    @Override
    public Optional<MfnTariffRate> getMfnTariffRatesForProduct(String htsCode) {
        Optional<Product> product = productRepository.findByHts8(htsCode);
        if (product.isPresent()) {
            return mfnTariffRateRepository.findByProductId(product.get().getId());
        }
        return null;
    }

    @Override
    public MfnTariffRate updateMfnTariffRateByHtsCode(String htsCode, MfnTariffRate mfnRate) {
        Optional<Product> product = productRepository.findByHts8(htsCode);
        if (product.isEmpty()) {
            throw new IllegalArgumentException("Product not found with HTS code: " + htsCode);
        }
        
        Optional<MfnTariffRate> existingRate = mfnTariffRateRepository.findByProductId(product.get().getId());
        if (existingRate.isEmpty()) {
            throw new IllegalArgumentException("MFN tariff rate not found for HTS code: " + htsCode);
        }
        
        MfnTariffRate rate = existingRate.get();
        rate.setProduct(mfnRate.getProduct());
        rate.setMfnTextRate(mfnRate.getMfnTextRate());
        rate.setMfnRateTypeCode(mfnRate.getMfnRateTypeCode());
        rate.setMfnadValoremRate(mfnRate.getMfnadValoremRate());
        rate.setMfnSpecificRate(mfnRate.getMfnSpecificRate());
        rate.setMfnOtherRate(mfnRate.getMfnOtherRate());
        rate.setBeginEffectDate(mfnRate.getBeginEffectDate());
        rate.setEndEffectiveDate(mfnRate.getEndEffectiveDate());
        return mfnTariffRateRepository.save(rate);
    }
    
    @Override
    public void deleteMfnTariffRateByHtsCode(String htsCode) {
        Optional<Product> product = productRepository.findByHts8(htsCode);
        if (product.isEmpty()) {
            throw new IllegalArgumentException("Product not found with HTS code: " + htsCode);
        }
        
        Optional<MfnTariffRate> existingRate = mfnTariffRateRepository.findByProductId(product.get().getId());
        if (existingRate.isEmpty()) {
            throw new IllegalArgumentException("MFN tariff rate not found for HTS code: " + htsCode);
        }
        
        mfnTariffRateRepository.deleteById(existingRate.get().getId());
    }

    @Override
    public List<Product> getProductsWithMfnRates() {
        return productRepository.findProductsWithMfnRates();
    }
}
