package csd.tariff.backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_indicators", schema = "tariff")
public class ProductIndicator {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "indicator_type", nullable = false, length = 50)
    private String indicatorType;
    
    @Column(name = "indicator_value", length = 10)
    private String indicatorValue;
    
    @Column(name = "excluded_countries")
    private String excludedCountries;
    
    @Column(name = "effective_date")
    private LocalDate effectiveDate;
    
    @Column(name = "expiration_date")
    private LocalDate expirationDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public ProductIndicator() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public ProductIndicator(Product product, String indicatorType, String indicatorValue) {
        this();
        this.product = product;
        this.indicatorType = indicatorType;
        this.indicatorValue = indicatorValue;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public String getIndicatorType() {
        return indicatorType;
    }
    
    public void setIndicatorType(String indicatorType) {
        this.indicatorType = indicatorType;
    }
    
    public String getIndicatorValue() {
        return indicatorValue;
    }
    
    public void setIndicatorValue(String indicatorValue) {
        this.indicatorValue = indicatorValue;
    }
    
    public String getExcludedCountries() {
        return excludedCountries;
    }
    
    public void setExcludedCountries(String excludedCountries) {
        this.excludedCountries = excludedCountries;
    }
    
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }
    
    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    
    public LocalDate getExpirationDate() {
        return expirationDate;
    }
    
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
