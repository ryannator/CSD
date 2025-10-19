package csd.tariff.backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "agreement_tariff_rates", schema = "tariff")
public class AgreementRate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;
    
    @ManyToOne
    @JoinColumn(name = "agreement_id", nullable = false)
    private TradeAgreement agreement;
    
    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;
    
    @Column(name = "rate_type_code", length = 10)
    private String rateTypeCode;
    
    @Column(name = "specific_rate", precision = 15, scale = 6)
    private BigDecimal specificRate;
    
    @Column(name = "other_rate", precision = 15, scale = 6)
    private BigDecimal otherRate;
    
    @Column(name = "effective_date")
    private LocalDate effectiveDate;
    
    @Column(name = "expiration_date")
    private LocalDate expirationDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "ad_valorem_rate", precision = 18, scale = 8)
    private BigDecimal adValoremRate;
    
    @Column(name = "indicator", length = 16)
    private String indicator;
    
    @Column(name = "text_rate")
    private String textRate;
    
    // Constructors
    public AgreementRate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public AgreementRate(Product product, TradeAgreement agreement, Country country) {
        this();
        this.product = product;
        this.agreement = agreement;
        this.country = country;
        this.effectiveDate = LocalDate.now();
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
    
    public TradeAgreement getAgreement() {
        return agreement;
    }
    
    public void setAgreement(TradeAgreement agreement) {
        this.agreement = agreement;
    }
    
    public Country getCountry() {
        return country;
    }
    
    public void setCountry(Country country) {
        this.country = country;
    }
    
    public String getRateTypeCode() {
        return rateTypeCode;
    }
    
    public void setRateTypeCode(String rateTypeCode) {
        this.rateTypeCode = rateTypeCode;
    }
    
    public BigDecimal getadValoremRate() {
        return adValoremRate;
    }
    
    public void setadValoremRate(BigDecimal adValoremRate) {
        this.adValoremRate = adValoremRate;
    }
    
    public BigDecimal getSpecificRate() {
        return specificRate;
    }
    
    public void setSpecificRate(BigDecimal specificRate) {
        this.specificRate = specificRate;
    }
    
    public BigDecimal getOtherRate() {
        return otherRate;
    }
    
    public void setOtherRate(BigDecimal otherRate) {
        this.otherRate = otherRate;
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
    public String getIndicator() {
        return indicator;
    }
    
    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }
    public String getTextRate() {
        return textRate;
    }
    
    public void setTextRate(String textRate) {
        this.textRate = textRate;
    }
}
