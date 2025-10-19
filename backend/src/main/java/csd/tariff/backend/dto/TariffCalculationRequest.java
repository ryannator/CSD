package csd.tariff.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TariffCalculationRequest {

  @NotBlank(message = "HTS code is required")
  private String htsCode;

  @NotBlank(message = "Origin country is required")
  private String originCountry;

  @NotBlank(message = "Destination country is required")
  private String destinationCountry;

  @NotNull(message = "Product value is required")
  @Positive(message = "Product value must be positive")
  private BigDecimal productValue;

  @NotNull(message = "Quantity is required")
  @Positive(message = "Quantity must be positive")
  private Integer quantity;

  private String currency = "USD";

  // Optional tariff date range for historical calculations
  private LocalDate tariffEffectiveDate;
  private LocalDate tariffExpirationDate;

  // Constructors
  public TariffCalculationRequest() {}

  public TariffCalculationRequest(
      String htsCode,
      String originCountry,
      String destinationCountry,
      BigDecimal productValue,
      Integer quantity,
      String currency) {
    this.htsCode = htsCode;
    this.originCountry = originCountry;
    this.destinationCountry = destinationCountry;
    this.productValue = productValue;
    this.quantity = quantity;
    this.currency = currency;
  }

  public TariffCalculationRequest(
      String htsCode,
      String originCountry,
      String destinationCountry,
      BigDecimal productValue,
      Integer quantity,
      String currency,
      LocalDate tariffEffectiveDate,
      LocalDate tariffExpirationDate) {
    this.htsCode = htsCode;
    this.originCountry = originCountry;
    this.destinationCountry = destinationCountry;
    this.productValue = productValue;
    this.quantity = quantity;
    this.currency = currency;
    this.tariffEffectiveDate = tariffEffectiveDate;
    this.tariffExpirationDate = tariffExpirationDate;
  }

  // Getters and Setters
  public String getHtsCode() {
    return htsCode;
  }

  public void setHtsCode(String htsCode) {
    this.htsCode = htsCode;
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

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
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

  @Override
  public String toString() {
    return "TariffCalculationRequest{"
        + "htsCode='"
        + htsCode
        + '\''
        + ", originCountry='"
        + originCountry
        + '\''
        + ", destinationCountry='"
        + destinationCountry
        + '\''
        + ", productValue="
        + productValue
        + ", quantity="
        + quantity
        + ", currency='"
        + currency
        + '\''
        + ", tariffEffectiveDate="
        + tariffEffectiveDate
        + ", tariffExpirationDate="
        + tariffExpirationDate
        + '}';
  }
}
