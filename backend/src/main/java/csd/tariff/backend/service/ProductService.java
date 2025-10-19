package csd.tariff.backend.service;

import java.util.List;

import csd.tariff.backend.model.AgreementRate;
import csd.tariff.backend.model.Product;

public interface ProductService {
    
    /**
     * Get all products
     */
    List<Product> getAllProducts();

    /**
     * Get agreement tariff rates for a product
     */
    List<AgreementRate> getAgreementRates(String htsCode, String countryCode);

    /**
     * Get agreement tariff rates for a product
     */
    List<AgreementRate> getAgreementRates(String htsCode);
    
    /**
     * Create a new product entry.
     */
    Product createProduct(Product product);

    /**
     * Update an existing product entry.
     */
    Product updateProduct(Long id, Product product);

    /**
     * Delete a product by its identifier.
     */
    void deleteProduct(Long id);

}
