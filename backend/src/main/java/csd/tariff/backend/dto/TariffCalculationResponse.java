package csd.tariff.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TariffCalculationResponse {
    
    private String htsCode;
    private String productDescription;
    private String originCountry;
    private String destinationCountry;
    private BigDecimal productValue;
    private Integer quantity;
    private String currency;
    
    // Tariff calculation results
    private String programType;       // "MFN" or "Preferential"
    private String programName;       // e.g., "MFN" or "USMCA"
    private String appliedRateLabel;  // e.g., "5%" or "Free"

    // Calculated amounts
    private BigDecimal totalTariffAmount; // duty applied based on the chosen program
    private BigDecimal totalImportPrice; // productValue + tariffAmount
    private BigDecimal savingsVsMfn; // if preferential used
    
    // Additional information
    private List<String> applicableAgreements;
    private String effectiveDate;
    private String notes;
    private LocalDateTime calculationTimestamp;
    
    // Constructors
    public TariffCalculationResponse() {
        this.calculationTimestamp = LocalDateTime.now();
    }
    
    public TariffCalculationResponse(String htsCode, String productDescription, String originCountry, 
                           String destinationCountry, BigDecimal productValue, Integer quantity, String currency) {
        this();
        this.htsCode = htsCode;
        this.originCountry = originCountry;
        this.destinationCountry = destinationCountry;
        this.productValue = productValue;
        this.quantity = quantity;
        this.currency = currency;
    }
    
    // Getters and Setters
    public String getHtsCode() {
        return htsCode;
    }
    
    public void setHtsCode(String htsCode) {
        this.htsCode = htsCode;
    }
    
    public String getProductDescription() {
        return productDescription;
    }
    
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
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
    
    public String getProgramType() {
        return programType;
    }
    
    public void setProgramType(String programType) {
        this.programType = programType;
    }
    
    public String getProgramName() {
        return programName;
    }
    
    public void setProgramName(String programName) {
        this.programName = programName;
    }
    
    public String getAppliedRateLabel() {
        return appliedRateLabel;
    }
    
    public void setAppliedRateLabel(String appliedRateLabel) {
        this.appliedRateLabel = appliedRateLabel;
    }
    
    public BigDecimal getTotalTariffAmount() {
        return totalTariffAmount;
    }
    
    public void setTotalTariffAmount(BigDecimal totalTariffAmount) {
        this.totalTariffAmount = totalTariffAmount;
    }
    
    public BigDecimal getSavingsVsMfn() {
        return savingsVsMfn;
    }
    
    public void setSavingsVsMfn(BigDecimal savingsVsMfn) {
        this.savingsVsMfn = savingsVsMfn;
    }
    
    public BigDecimal getTotalImportPrice() {
        return totalImportPrice;
    }
    
    public void setTotalImportPrice(BigDecimal totalImportPrice) {
        this.totalImportPrice = totalImportPrice;
    }
    
    public List<String> getApplicableAgreements() {
        return applicableAgreements;
    }
    
    public void setApplicableAgreements(List<String> applicableAgreements) {
        this.applicableAgreements = applicableAgreements;
    }
    
    public String getEffectiveDate() {
        return effectiveDate;
    }
    
    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCalculationTimestamp() {
        return calculationTimestamp;
    }
    
    public void setCalculationTimestamp(LocalDateTime calculationTimestamp) {
        this.calculationTimestamp = calculationTimestamp;
    }
    
    @Override
    public String toString() {
        return "TariffCalculationResponse{" +
                "htsCode='" + htsCode + '\'' +
                ", originCountry='" + originCountry + '\'' +
                ", destinationCountry='" + destinationCountry + '\'' +
                ", productValue=" + productValue +
                ", quantity=" + quantity +
                ", currency='" + currency + '\'' +
                ", programType='" + programType + '\'' +
                ", programName='" + programName + '\'' +
                ", appliedRateLabel='" + appliedRateLabel + '\'' +
                ", totalTariffAmount=" + totalTariffAmount +
                ", savingsVsMfn=" + savingsVsMfn +
                ", totalImportPrice=" + totalImportPrice +
                ", applicableAgreements=" + applicableAgreements +
                ", effectiveDate='" + effectiveDate + '\'' +
                ", notes='" + notes + '\'' +
                ", calculationTimestamp=" + calculationTimestamp +
                '}';
    }
}


