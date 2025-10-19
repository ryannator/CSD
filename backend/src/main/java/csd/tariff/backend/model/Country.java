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
@Table(name = "countries", schema = "tariff")
public class Country {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "country_name", nullable = false, length = 100)
    private String countryName;
    
    @Column(name = "country_code", length = 3)
    private String countryCode;
    
    @Column(name = "country_name_short", length = 50)
    private String countryNameShort;
    
    @Column(name = "region", length = 50)
    private String region;
    
    @Column(name = "continent", length = 50)
    private String continent;
    
    @Column(name = "currency", length = 3)
    private String currency;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "country", fetch = jakarta.persistence.FetchType.LAZY)
    @JsonIgnore
    private List<AgreementParticipant> agreementParticipants;
    
    @OneToMany(mappedBy = "country", fetch = jakarta.persistence.FetchType.LAZY)
    @JsonIgnore
    private List<AgreementRate> AgreementRates;
    
    // Constructors
    public Country() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Country(String countryCode, String countryName) {
        this();
        this.countryCode = countryCode;
        this.countryName = countryName;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public String getCountryCode() {
        return countryCode;
    }
    
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    
    public String getCountryName() {
        return countryName;
    }
    
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
    
    public String getCountryNameShort() {
        return countryNameShort;
    }
    
    public void setCountryNameShort(String countryNameShort) {
        this.countryNameShort = countryNameShort;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public String getContinent() {
        return continent;
    }
    
    public void setContinent(String continent) {
        this.continent = continent;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @JsonIgnore
    public List<AgreementParticipant> getAgreementParticipants() {
        return agreementParticipants;
    }
    
    public void setAgreementParticipants(List<AgreementParticipant> agreementParticipants) {
        this.agreementParticipants = agreementParticipants;
    }
    
    @JsonIgnore
    public List<AgreementRate> getAgreementRates() {
        return AgreementRates;
    }
    
    public void setAgreementRates(List<AgreementRate> AgreementRates) {
        this.AgreementRates = AgreementRates;
    }
}
