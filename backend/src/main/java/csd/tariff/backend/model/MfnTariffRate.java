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
@Table(name = "mfn_tariff_rates", schema = "tariff")
public class MfnTariffRate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;
    
    @Column(name = "mfn_text_rate", length = 500)
    private String mfnTextRate;
    
    @Column(name = "mfn_rate_type_code", length = 10)
    private String mfnRateTypeCode;
    
    @Column(name = "mfn_ave", precision = 15, scale = 6)
    private BigDecimal mfnAve;
    
    @Column(name = "mfn_ad_val_rate", precision = 15, scale = 6)
    private BigDecimal mfnadValoremRate;
    
    @Column(name = "mfn_specific_rate", precision = 15, scale = 6)
    private BigDecimal mfnSpecificRate;
    
    @Column(name = "mfn_other_rate", precision = 15, scale = 6)
    private BigDecimal mfnOtherRate;
    
    @Column(name = "col1_special_text", length = 500)
    private String col1SpecialText;
    
    @Column(name = "col1_special_mod", length = 50)
    private String col1SpecialMod;
    
    @Column(name = "col2_text_rate", length = 500)
    private String col2TextRate;
    
    @Column(name = "col2_rate_type_code", length = 10)
    private String col2RateTypeCode;
    
    @Column(name = "col2_ad_val_rate", precision = 15, scale = 6)
    private BigDecimal col2adValoremRate;
    
    @Column(name = "col2_specific_rate", precision = 15, scale = 6)
    private BigDecimal col2SpecificRate;
    
    @Column(name = "col2_other_rate", precision = 15, scale = 6)
    private BigDecimal col2OtherRate;
    
    @Column(name = "begin_effect_date")
    private LocalDate beginEffectDate;
    
    @Column(name = "end_effective_date")
    private LocalDate endEffectiveDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public MfnTariffRate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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
    
    public String getMfnTextRate() {
        return mfnTextRate;
    }
    
    public void setMfnTextRate(String mfnTextRate) {
        this.mfnTextRate = mfnTextRate;
    }
    
    public String getMfnRateTypeCode() {
        return mfnRateTypeCode;
    }
    
    public void setMfnRateTypeCode(String mfnRateTypeCode) {
        this.mfnRateTypeCode = mfnRateTypeCode;
    }
    
    public BigDecimal getMfnAve() {
        return mfnAve;
    }
    
    public void setMfnAve(BigDecimal mfnAve) {
        this.mfnAve = mfnAve;
    }
    
    public BigDecimal getMfnadValoremRate() {
        return mfnadValoremRate;
    }
    
    public void setMfnadValoremRate(BigDecimal mfnadValoremRate) {
        this.mfnadValoremRate = mfnadValoremRate;
    }
    
    public BigDecimal getMfnSpecificRate() {
        return mfnSpecificRate;
    }
    
    public void setMfnSpecificRate(BigDecimal mfnSpecificRate) {
        this.mfnSpecificRate = mfnSpecificRate;
    }
    
    public BigDecimal getMfnOtherRate() {
        return mfnOtherRate;
    }
    
    public void setMfnOtherRate(BigDecimal mfnOtherRate) {
        this.mfnOtherRate = mfnOtherRate;
    }
    
    public String getCol1SpecialText() {
        return col1SpecialText;
    }
    
    public void setCol1SpecialText(String col1SpecialText) {
        this.col1SpecialText = col1SpecialText;
    }
    
    public String getCol1SpecialMod() {
        return col1SpecialMod;
    }
    
    public void setCol1SpecialMod(String col1SpecialMod) {
        this.col1SpecialMod = col1SpecialMod;
    }
    
    public String getCol2TextRate() {
        return col2TextRate;
    }
    
    public void setCol2TextRate(String col2TextRate) {
        this.col2TextRate = col2TextRate;
    }
    
    public String getCol2RateTypeCode() {
        return col2RateTypeCode;
    }
    
    public void setCol2RateTypeCode(String col2RateTypeCode) {
        this.col2RateTypeCode = col2RateTypeCode;
    }
    
    public BigDecimal getCol2adValoremRate() {
        return col2adValoremRate;
    }
    
    public void setCol2adValoremRate(BigDecimal col2adValoremRate) {
        this.col2adValoremRate = col2adValoremRate;
    }
    
    public BigDecimal getCol2SpecificRate() {
        return col2SpecificRate;
    }
    
    public void setCol2SpecificRate(BigDecimal col2SpecificRate) {
        this.col2SpecificRate = col2SpecificRate;
    }
    
    public BigDecimal getCol2OtherRate() {
        return col2OtherRate;
    }
    
    public void setCol2OtherRate(BigDecimal col2OtherRate) {
        this.col2OtherRate = col2OtherRate;
    }
    
    public LocalDate getBeginEffectDate() {
        return beginEffectDate;
    }
    
    public void setBeginEffectDate(LocalDate beginEffectDate) {
        this.beginEffectDate = beginEffectDate;
    }
    
    public LocalDate getEndEffectiveDate() {
        return endEffectiveDate;
    }
    
    public void setEndEffectiveDate(LocalDate endEffectiveDate) {
        this.endEffectiveDate = endEffectiveDate;
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
