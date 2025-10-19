package csd.tariff.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import csd.tariff.backend.model.AgreementRate;
import csd.tariff.backend.model.Product;
import csd.tariff.backend.repository.AgreementRateRepository;
import csd.tariff.backend.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AgreementRateRepository agreementRateRepository;
    
    @Override
    public List<Product> getAllProducts() {
        // Use a custom query to fetch MFN rates
        return productRepository.findAllWithMfnRates();
    }

    @Override
    public List<AgreementRate> getAgreementRates(String htsCode) {
        return agreementRateRepository.findByHts8(htsCode);
    }

    @Override
    public List<AgreementRate> getAgreementRates(String htsCode, String countryCode) {
        return agreementRateRepository.findByHts8AndCountryCode(htsCode, countryCode);
    }
    
    @Override
    public Product createProduct(Product productRequest) {
        if (productRequest == null) {
            throw new IllegalArgumentException("Product payload is required.");
        }

        String htsCode = nullIfBlank(productRequest.getHts8());
        if (htsCode == null) {
            throw new IllegalArgumentException("HTS code is required.");
        }

        String normalizedCode = htsCode.toUpperCase(Locale.ROOT);

        productRepository
            // .findByHts8(htsCode)
            .findByHts8(normalizedCode)
            .ifPresent(existing -> {
                throw new IllegalStateException("A product with this HTS code already exists.");
            });

        String description = nullIfBlank(productRequest.getBriefDescription());
        if (description == null) {
            throw new IllegalArgumentException("Product description is required.");
        }

        Product product = new Product();
        // product.setHts8(htsCode);
        product.setHts8(normalizedCode);
        product.setBriefDescription(description);
        product.setQuantity1Code(nullIfBlank(productRequest.getQuantity1Code()));
        product.setQuantity2Code(nullIfBlank(productRequest.getQuantity2Code()));

        String bindingCode = nullIfBlank(productRequest.getWtoBindingCode());
        product.setWtoBindingCode(bindingCode != null ? bindingCode.toUpperCase(Locale.ROOT) : null);
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product productRequest) {
        if (id == null) {
            throw new IllegalArgumentException("Product ID is required.");
        }

        Product existing = productRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));

        if (productRequest == null) {
            throw new IllegalArgumentException("Product payload is required.");
        }

        String htsCode = nullIfBlank(productRequest.getHts8());
        if (htsCode == null) {
            throw new IllegalArgumentException("HTS code is required.");
        }

        String normalizedCode = htsCode.toUpperCase(Locale.ROOT);
        productRepository
            .findByHts8(normalizedCode)
            .filter(found -> !found.getId().equals(id))
            .ifPresent(found -> {
                throw new IllegalStateException("A product with this HTS code already exists.");
            });

        String description = nullIfBlank(productRequest.getBriefDescription());
        if (description == null) {
            throw new IllegalArgumentException("Product description is required.");
        }

        existing.setHts8(normalizedCode);
        existing.setBriefDescription(description);
        existing.setQuantity1Code(nullIfBlank(productRequest.getQuantity1Code()));
        existing.setQuantity2Code(nullIfBlank(productRequest.getQuantity2Code()));

        String bindingCode = nullIfBlank(productRequest.getWtoBindingCode());
        existing.setWtoBindingCode(bindingCode != null ? bindingCode.toUpperCase(Locale.ROOT) : null);
        existing.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(existing);
    }

    @Override
    public void deleteProduct(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Product ID is required.");
        }

        Product existing = productRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));

        productRepository.delete(existing);
    }

    private String nullIfBlank(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

}
