package csd.tariff.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "trade_agreements", schema = "tariff")
public class TradeAgreement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "agreement_code", nullable = false, unique = true, length = 20)
  private String agreementCode;

  @Column(name = "agreement_name", nullable = false, length = 100)
  private String agreementName;

  @Column(name = "effective_date")
  private LocalDate effectiveDate;

  @Column(name = "expiration_date")
  private LocalDate expirationDate;

  @Column(name = "agreement_type", nullable = false, length = 50)
  private String agreementType; // 'FTA', 'PTA', 'PREFERENCE', etc.

  @Column(name = "is_multilateral")
  private Boolean isMultilateral = false;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // Relationships
  @OneToMany(mappedBy = "agreement")
  @JsonIgnore
  private List<AgreementParticipant> participants;

  @OneToMany(mappedBy = "agreement")
  @JsonIgnore
  private List<AgreementRate> agreementRates;

  // Constructors
  public TradeAgreement() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public TradeAgreement(String agreementCode, String agreementName, String agreementType) {
    this();
    this.agreementCode = agreementCode;
    this.agreementName = agreementName;
    this.agreementType = agreementType;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public String getAgreementName() {
    return agreementName;
  }

  public void setAgreementName(String agreementName) {
    this.agreementName = agreementName;
  }

  public String getAgreementCode() {
    return agreementCode;
  }

  public void setAgreementCode(String agreementCode) {
    this.agreementCode = agreementCode;
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

  public String getAgreementType() {
    return agreementType;
  }

  public void setAgreementType(String agreementType) {
    this.agreementType = agreementType;
  }

  public Boolean getIsMultilateral() {
    return isMultilateral;
  }

  public void setIsMultilateral(Boolean isMultilateral) {
    this.isMultilateral = isMultilateral;
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

  public List<AgreementParticipant> getParticipants() {
    return participants;
  }

  public void setParticipants(List<AgreementParticipant> participants) {
    this.participants = participants;
  }

  public List<AgreementRate> getAgreementRates() {
    return agreementRates;
  }

  public void setAgreementRates(List<AgreementRate> agreementRates) {
    this.agreementRates = agreementRates;
  }
}
