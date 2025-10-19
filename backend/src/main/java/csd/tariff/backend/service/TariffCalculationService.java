package csd.tariff.backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import csd.tariff.backend.dto.TariffCalculationRequest;
import csd.tariff.backend.dto.TariffCalculationResponse;
import csd.tariff.backend.model.Product;
import csd.tariff.backend.model.TariffCalculation;

public interface TariffCalculationService {

  /** Find product by HTS code */
  Optional<Product> findByHtsCode(String htsCode);

  // ===== TARIFF CALCULATION METHODS =====

  /** Calculate comprehensive tariff for a specific product and country */
  Map<String, Object> calculateTariff(
      String htsCode, String countryCode, Double productValue, Integer quantity);

  /** Calculate comprehensive tariff for a specific product between two countries */
  Map<String, Object> calculateTariff(
      String htsCode,
      String originCountry,
      String destinationCountry,
      Double productValue,
      Integer quantity);

  /** Calculate comprehensive tariff for a specific product and country with tariff date range */
  Map<String, Object> calculateTariffWithDateRange(
      String htsCode,
      String countryCode,
      Double productValue,
      Integer quantity,
      java.time.LocalDate tariffEffectiveDate,
      java.time.LocalDate tariffExpirationDate);

  /**
   * Calculate comprehensive tariff for a specific product between two countries with tariff date
   * range
   */
  Map<String, Object> calculateTariffWithDateRange(
      String htsCode,
      String originCountry,
      String destinationCountry,
      Double productValue,
      Integer quantity,
      java.time.LocalDate tariffEffectiveDate,
      java.time.LocalDate tariffExpirationDate);

  /** Get all applicable trade programs between two countries */
  List<String> getApplicableTradePrograms(String originCountry, String destinationCountry);

  /** Calculate duty amount based on ad valorem and specific rates */
  BigDecimal calculateDuty(
      BigDecimal adValoremRate, BigDecimal specificRate, Double productValue, Integer quantity);

  /** Validate HTS code format and existence */
  Map<String, Object> validateHtsCode(String htsCode);

  /** Get cost breakdown with final import price */
  Map<String, Object> getCostBreakdown(
      String htsCode, String countryCode, Double productValue, Integer quantity);

  // ===== CRUD OPERATIONS FOR TARIFF CALCULATIONS =====

  /** Create a new tariff calculation record using DTO */
  TariffCalculationResponse createTariffCalculation(TariffCalculationRequest request);

  /** Update tariff calculation using DTO */
  TariffCalculationResponse updateTariffCalculation(Long id, TariffCalculationRequest request);

  /** Read/Get tariff calculation by ID */
  Optional<TariffCalculation> getTariffCalculationById(Long id);

  /** Read/Get all tariff calculations */
  List<TariffCalculation> getAllTariffCalculations();

  /** Read/Get tariff calculations by HTS code */
  List<TariffCalculation> getTariffCalculationsByHtsCode(String htsCode);

  /** Read/Get tariff calculations by country code */
  List<TariffCalculation> getTariffCalculationsByCountryCode(String countryCode);

  /** Delete tariff calculation by ID */
  boolean deleteTariffCalculation(Long id);

  /** Delete all tariff calculations */
  boolean deleteAllTariffCalculations();
}
