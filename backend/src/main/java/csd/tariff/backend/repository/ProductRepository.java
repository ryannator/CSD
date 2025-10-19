package csd.tariff.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import csd.tariff.backend.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Find by HTS code
    Optional<Product> findByHts8(String hts8);
    
    // Search by product description
    @Query("SELECT p FROM Product p WHERE LOWER(p.briefDescription) LIKE LOWER(CONCAT('%', :description, '%'))")
    List<Product> findByBriefDescriptionContainingIgnoreCase(@Param("description") String description);
    
    // Search by HTS code pattern
    @Query("SELECT p FROM Product p WHERE p.hts8 LIKE CONCAT(:pattern, '%')")
    List<Product> findByHts8StartingWith(@Param("pattern") String pattern);
    
    // Find products with MFN rates
    @Query("SELECT DISTINCT p FROM Product p JOIN p.mfnTariffRates mfn WHERE mfn.mfnadValoremRate IS NOT NULL AND mfn.mfnadValoremRate > 0")
    List<Product> findProductsWithMfnRates();
    
    // Find products with free trade rates
    @Query("SELECT DISTINCT p FROM Product p JOIN p.mfnTariffRates mfn WHERE " +
           "LOWER(mfn.mfnTextRate) LIKE '%free%' OR mfn.mfnadValoremRate = 0")
    List<Product> findProductsWithFreeTrade();
    
    // Find products with GSP benefits
    @Query("SELECT DISTINCT p FROM Product p JOIN p.productIndicators pi WHERE pi.indicatorType = 'GSP'")
    List<Product> findProductsWithGspBenefits();
    
    // Find products with specific indicators
    @Query("SELECT DISTINCT p FROM Product p JOIN p.productIndicators pi WHERE pi.indicatorType = :indicatorType")
    List<Product> findProductsByIndicatorType(@Param("indicatorType") String indicatorType);
    
    // Find all products with MFN rates 
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.mfnTariffRates")
    List<Product> findAllWithMfnRates();

    // Count total products
    long count();
}
