package csd.tariff.backend.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "products", schema = "tariff")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "hts8", nullable = false, unique = true, length = 8)
    private String hts8;
    
    @Column(name = "brief_description", nullable = false)
    private String briefDescription;
    
    @Column(name = "quantity_1_code", length = 10)
    private String quantity1Code;
    
    @Column(name = "quantity_2_code", length = 10)
    private String quantity2Code;
    
    @Column(name = "wto_binding_code", length = 1)
    private String wtoBindingCode;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "product", fetch = jakarta.persistence.FetchType.LAZY)
    private List<MfnTariffRate> mfnTariffRates;
    
    @OneToMany(mappedBy = "product", fetch = jakarta.persistence.FetchType.LAZY)
    @JsonIgnore
    private List<AgreementRate> AgreementRates;
    
    @OneToMany(mappedBy = "product", fetch = jakarta.persistence.FetchType.LAZY)
    @JsonIgnore
    private List<ProductIndicator> productIndicators;
    
    @OneToMany(mappedBy = "product", fetch = jakarta.persistence.FetchType.LAZY)
    @JsonIgnore
    private List<ProductNote> productNotes;
    
    // Constructors
    public Product() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Product(String hts8, String briefDescription) {
        this();
        this.hts8 = hts8;
        this.briefDescription = briefDescription;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public String getHts8() {
        return hts8;
    }
    
    public void setHts8(String hts8) {
        this.hts8 = hts8;
    }
    
    public String getBriefDescription() {
        return briefDescription;
    }
    
    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }
    
    public String getQuantity1Code() {
        return quantity1Code;
    }
    
    public void setQuantity1Code(String quantity1Code) {
        this.quantity1Code = quantity1Code;
    }
    
    public String getQuantity2Code() {
        return quantity2Code;
    }
    
    public void setQuantity2Code(String quantity2Code) {
        this.quantity2Code = quantity2Code;
    }
    
    public String getWtoBindingCode() {
        return wtoBindingCode;
    }
    
    public void setWtoBindingCode(String wtoBindingCode) {
        this.wtoBindingCode = wtoBindingCode;
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
    
    @JsonIgnore
    public List<MfnTariffRate> getMfnTariffRates() {
        return mfnTariffRates;
    }
    
    public void setMfnTariffRates(List<MfnTariffRate> mfnTariffRates) {
        this.mfnTariffRates = mfnTariffRates;
    }
    
    @JsonIgnore
    public List<AgreementRate> getAgreementRates() {
        return AgreementRates;
    }
    
    public void setAgreementRates(List<AgreementRate> AgreementRates) {
        this.AgreementRates = AgreementRates;
    }
    
    @JsonIgnore
    public List<ProductIndicator> getProductIndicators() {
        return productIndicators;
    }
    
    public void setProductIndicators(List<ProductIndicator> productIndicators) {
        this.productIndicators = productIndicators;
    }
    
    @JsonIgnore
    public List<ProductNote> getProductNotes() {
        return productNotes;
    }
    
    public void setProductNotes(List<ProductNote> productNotes) {
        this.productNotes = productNotes;
    }
}