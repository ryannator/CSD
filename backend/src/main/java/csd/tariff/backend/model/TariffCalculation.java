package csd.tariff.backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "calculations", schema = "tariff")
public class TariffCalculation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "hts_code", nullable = false, length = 8)
  private String htsCode;

  @Column(name = "country_code", nullable = false, length = 3)
  private String countryCode;

  @Column(name = "origin_country", length = 3)
  private String originCountry;

  @Column(name = "destination_country", length = 3)
  private String destinationCountry;

  @Column(name = "product_value", nullable = false, precision = 15, scale = 2)
  private BigDecimal productValue;

  @Column(name = "quantity", nullable = false, precision = 15, scale = 2)
  private Integer quantity;

  @Column(name = "calculation_type", nullable = false, length = 50)
  private String calculationType;

  @Column(name = "calculation_result", nullable = false)
  private BigDecimal calculationResult;

  @Column(name = "total_tariff_amount", precision = 18, scale = 2)
  private BigDecimal totalTariffAmount;

  @Column(name = "tariff_effective_date")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate tariffEffectiveDate;

  @Column(name = "tariff_expiration_date")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate tariffExpirationDate;

  @Column(name = "currency", length = 3)
  private String currency;

  @Column(name = "created_at")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  // Constructors
  public TariffCalculation() {}

  public TariffCalculation(
      String htsCode,
      String countryCode,
      String originCountry,
      String destinationCountry,
      BigDecimal productValue,
      Integer quantity,
      String calculationType,
      BigDecimal totalTariffAmount,
      BigDecimal calculationResult,
      LocalDate tariffEffectiveDate,
      LocalDate tariffExpirationDate,
      String currency) {
    this.htsCode = htsCode;
    this.countryCode = countryCode;
    this.originCountry = originCountry;
    this.destinationCountry = destinationCountry;
    this.productValue = productValue;
    this.quantity = quantity;
    this.calculationType = calculationType;
    this.totalTariffAmount = totalTariffAmount;
    this.calculationResult = calculationResult;
    this.tariffEffectiveDate = tariffEffectiveDate;
    this.tariffExpirationDate = tariffExpirationDate;
    this.currency = currency;
  }

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public String getHtsCode() {
    return htsCode;
  }

  public void setHtsCode(String htsCode) {
    this.htsCode = htsCode;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public String getOriginCountry() {
    return originCountry;
  }

  public void setOriginCountry(String originCountry) {
    this.originCountry = originCountry;
  }

  public String getDestinationCountry() {
    return destinationCountry;
  }

  public void setDestinationCountry(String destinationCountry) {
    this.destinationCountry = destinationCountry;
  }

  public BigDecimal getProductValue() {
    return productValue;
  }

  public void setProductValue(BigDecimal productValue) {
    this.productValue = productValue;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public String getCalculationType() {
    return calculationType;
  }

  public void setCalculationType(String calculationType) {
    this.calculationType = calculationType;
  }

  public BigDecimal getCalculationResult() {
    return calculationResult;
  }

  public void setCalculationResult(BigDecimal calculationResult) {
    this.calculationResult = calculationResult;
  }

  public BigDecimal getTotalTariffAmount() {
    return totalTariffAmount;
  }

  public void setTotalTariffAmount(BigDecimal totalTariffAmount) {
    this.totalTariffAmount = totalTariffAmount;
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

  public LocalDate getTariffEffectiveDate() {
    return tariffEffectiveDate;
  }

  public void setTariffEffectiveDate(LocalDate tariffEffectiveDate) {
    this.tariffEffectiveDate = tariffEffectiveDate;
  }

  public LocalDate getTariffExpirationDate() {
    return tariffExpirationDate;
  }

  public void setTariffExpirationDate(LocalDate tariffExpirationDate) {
    this.tariffExpirationDate = tariffExpirationDate;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }
}
