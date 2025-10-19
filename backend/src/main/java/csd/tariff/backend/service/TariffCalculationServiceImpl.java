package csd.tariff.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import csd.tariff.backend.dto.TariffCalculationRequest;
import csd.tariff.backend.dto.TariffCalculationResponse;
import csd.tariff.backend.model.AgreementRate;
import csd.tariff.backend.model.MfnTariffRate;
import csd.tariff.backend.model.Product;
import csd.tariff.backend.model.TariffCalculation;
import csd.tariff.backend.model.TradeAgreement;
import csd.tariff.backend.repository.ProductRepository;
import csd.tariff.backend.repository.TariffCalculationRepository;

@Service
public class TariffCalculationServiceImpl implements TariffCalculationService {

  // ===== Dependencies (constructor injection, lowerCamelCase fields) =====
  private final ProductRepository productRepository;
  private final TariffCalculationRepository tariffCalculationRepository;
  private final MfnService mfnService;
  private final ProductService productService;
  private final TradeAgreementService tradeAgreementService;
  private final CurrencyService currencyService;

  public TariffCalculationServiceImpl(
      ProductRepository productRepository,
      TariffCalculationRepository tariffCalculationRepository,
      MfnService mfnService,
      ProductService productService,
      TradeAgreementService tradeAgreementService,
      CurrencyService currencyService) {
    this.productRepository = productRepository;
    this.tariffCalculationRepository = tariffCalculationRepository;
    this.mfnService = mfnService;
    this.productService = productService;
    this.tradeAgreementService = tradeAgreementService;
    this.currencyService = currencyService;
  }

  // ===== Basic lookups =====
  @Override
  public Optional<Product> findByHtsCode(String htsCode) {
    if (htsCode == null) return Optional.empty();
    // Reuse the same normalization used in validate step
    String cleaned = cleanHtsCode(htsCode);
    return cleaned.length() == 8 ? productRepository.findByHts8(cleaned) : Optional.empty();
  }

  // ===== Core calculation =====
  @Override
  public Map<String, Object> calculateTariff(
      String htsCode, String destinationCountry, Double productValue, Integer quantity) {
    // For backward compatibility, treat destinationCountry as destination country
    return calculateTariff(htsCode, null, destinationCountry, productValue, quantity);
  }

  @Override
  public Map<String, Object> calculateTariff(
      String htsCode,
      String originCountry,
      String destinationCountry,
      Double productValue,
      Integer quantity) {
    Map<String, Object> result = new HashMap<>();
    try {
      // Validate & normalize inputs early
      validateInputs(htsCode, productValue, quantity);
      String cleanedHts = cleanHtsCode(htsCode);

      Optional<Product> productOpt = productRepository.findByHts8(cleanedHts);
      if (productOpt.isEmpty()) {
        result.put("error", "HTS code not found");
        return result;
      }

      result.put("htsCode", cleanedHts);
      result.put("countryOfOrigin", originCountry);
      result.put("destinationCountry", destinationCountry);
      result.put("productValue", productValue);
      result.put("quantity", quantity);

      // --- MFN ---
      Optional<MfnTariffRate> mfnRateOpt = mfnService.getMfnTariffRate(cleanedHts);
      Map<String, Object> mfnInfo = new HashMap<>();
      BigDecimal mfnDuty = BigDecimal.ZERO;
      if (mfnRateOpt.isPresent()) {
        MfnTariffRate mfnRate = mfnRateOpt.get();
        mfnInfo.put("adValoremRate", mfnRate.getMfnadValoremRate());
        mfnInfo.put("specificRate", mfnRate.getMfnSpecificRate());
        mfnInfo.put("textRate", mfnRate.getMfnTextRate());
        mfnInfo.put("rateTypeCode", mfnRate.getMfnRateTypeCode());
        mfnDuty =
            calculateDuty(
                mfnRate.getMfnadValoremRate(), mfnRate.getMfnSpecificRate(), productValue, quantity);
        mfnInfo.put("calculatedDuty", mfnDuty);
      }
      result.put("mfnRate", mfnInfo);
      result.put("mfnTariffAmount", mfnDuty);

      // --- Preferential ---
      List<AgreementRate> agreementRates =
          productService.getAgreementRates(cleanedHts, destinationCountry);
      List<Map<String, Object>> preferentialRates = new ArrayList<>();
      BigDecimal lowestDuty = mfnDuty;
      String bestProgram = "MFN";

      for (AgreementRate ar : agreementRates) {
        Map<String, Object> prefInfo = new HashMap<>();
        prefInfo.put("adValoremRate", ar.getadValoremRate());
        prefInfo.put("specificRate", ar.getSpecificRate());
        prefInfo.put("textRate", ar.getTextRate());
        prefInfo.put("rateTypeCode", ar.getRateTypeCode());

        BigDecimal prefDuty =
            calculateDuty(ar.getadValoremRate(), ar.getSpecificRate(), productValue, quantity);
        prefInfo.put("calculatedDuty", prefDuty);
        prefInfo.put("agreementCode", ar.getAgreement().getAgreementCode());
        prefInfo.put("agreementName", ar.getAgreement().getAgreementName());
        prefInfo.put("eligibilityStatus", "Unknown"); // placeholder; implement real checks

        preferentialRates.add(prefInfo);

        if (prefDuty.compareTo(lowestDuty) < 0) {
          lowestDuty = prefDuty;
          bestProgram = ar.getAgreement().getAgreementName();
        }
      }
      result.put("preferentialRates", preferentialRates);

      // --- Recommendation ---
      Map<String, Object> recommended = new HashMap<>();
      boolean isMfnBest = lowestDuty.compareTo(mfnDuty) == 0;
      recommended.put("rateType", isMfnBest ? "MFN" : "Preferential");
      recommended.put("calculatedDuty", lowestDuty);
      recommended.put("savings", mfnDuty.subtract(lowestDuty));
      recommended.put("programName", isMfnBest ? "MFN" : bestProgram);
      recommended.put(
          "recommendation",
          isMfnBest
              ? "MFN rate is the best available option"
              : ("Use " + bestProgram + " for lowest duty rate"));

      result.put("recommendedRate", recommended);
      result.put("bestTariffAmount", lowestDuty);
      result.put("bestProgramName", bestProgram);

      // --- Applicable programs ---
      List<String> applicablePrograms =
          getApplicableTradePrograms(originCountry, destinationCountry);
      result.put("applicablePrograms", applicablePrograms);

      // --- Compliance notes ---
      List<String> notes = new ArrayList<>();
      if (applicablePrograms.isEmpty()) {
        notes.add("No preferential trade programs available for this product/country combination");
      } else {
        notes.add("ELIGIBLE programs (to be verified): " + String.join(", ", applicablePrograms));
      }
      if (applicablePrograms.stream().anyMatch(p -> p.contains("GSP"))) {
        notes.add("GSP: Verify country eligibility and product requirements");
      }
      if (applicablePrograms.stream().anyMatch(p -> p.contains("USMCA"))) {
        notes.add("USMCA: Verify rules of origin requirements");
      }
      notes.add("Ensure proper documentation for preferential treatment");
      notes.add("Verify country of origin certification requirements");
      result.put("complianceNotes", notes);

      // --- Totals ---
      BigDecimal totalImportPrice = BigDecimal.valueOf(productValue).add(lowestDuty);
      result.put("totalImportPrice", totalImportPrice);

      return result;
    } catch (Exception e) {
      result.put("error", "Calculation failed: " + e.getMessage());
      return result;
    }
  }

  @Override
  public Map<String, Object> calculateTariffWithDateRange(
      String htsCode,
      String destinationCountry,
      Double productValue,
      Integer quantity,
      java.time.LocalDate tariffEffectiveDate,
      java.time.LocalDate tariffExpirationDate) {
    // For backward compatibility, treat destinationCountry as destination country
    return calculateTariffWithDateRange(
        htsCode,
        null,
        destinationCountry,
        productValue,
        quantity,
        tariffEffectiveDate,
        tariffExpirationDate);
  }

  @Override
  public Map<String, Object> calculateTariffWithDateRange(
      String htsCode,
      String originCountry,
      String destinationCountry,
      Double productValue,
      Integer quantity,
      java.time.LocalDate tariffEffectiveDate,
      java.time.LocalDate tariffExpirationDate) {
    Map<String, Object> result = new HashMap<>();
    try {
      // Validate & normalize inputs early
      validateInputs(htsCode, productValue, quantity);
      String cleanedHts = cleanHtsCode(htsCode);

      Optional<Product> productOpt = productRepository.findByHts8(cleanedHts);
      if (productOpt.isEmpty()) {
        result.put("error", "HTS code not found");
        return result;
      }

      // Validate tariff date range against MFN rates
      Map<String, Object> mfnDateValidation =
          validateTariffDateRange(cleanedHts, tariffEffectiveDate, tariffExpirationDate);
      if (mfnDateValidation.containsKey("error")) {
        return mfnDateValidation;
      }

      // Store any warnings from date validation
      if (mfnDateValidation.containsKey("warning")) {
        result.put("dateValidationWarning", mfnDateValidation.get("warning"));
      }

      result.put("htsCode", cleanedHts);
      result.put("countryOfOrigin", originCountry);
      result.put("destinationCountry", destinationCountry);
      result.put("productValue", productValue);
      result.put("quantity", quantity);
      result.put("tariffEffectiveDate", tariffEffectiveDate);
      result.put("tariffExpirationDate", tariffExpirationDate);

      // --- MFN ---
      Optional<MfnTariffRate> mfnRateOpt = mfnService.getMfnTariffRate(cleanedHts);
      Map<String, Object> mfnInfo = new HashMap<>();
      BigDecimal mfnDuty = BigDecimal.ZERO;
      if (mfnRateOpt.isPresent()) {
        MfnTariffRate mfnRate = mfnRateOpt.get();
        mfnInfo.put("adValoremRate", mfnRate.getMfnadValoremRate());
        mfnInfo.put("specificRate", mfnRate.getMfnSpecificRate());
        mfnInfo.put("textRate", mfnRate.getMfnTextRate());
        mfnInfo.put("rateTypeCode", mfnRate.getMfnRateTypeCode());
        mfnDuty =
            calculateDuty(
                mfnRate.getMfnadValoremRate(), mfnRate.getMfnSpecificRate(), productValue, quantity);
        mfnInfo.put("calculatedDuty", mfnDuty);
      }
      result.put("mfnRate", mfnInfo);
      result.put("mfnTariffAmount", mfnDuty);

      // --- Preferential ---
      List<AgreementRate> agreementRates =
          productService.getAgreementRates(cleanedHts, destinationCountry);
      List<Map<String, Object>> preferentialRates = new ArrayList<>();
      BigDecimal lowestDuty = mfnDuty;
      String bestProgram = "MFN";

      for (AgreementRate ar : agreementRates) {
        Map<String, Object> prefInfo = new HashMap<>();
        prefInfo.put("adValoremRate", ar.getadValoremRate());
        prefInfo.put("specificRate", ar.getSpecificRate());
        prefInfo.put("textRate", ar.getTextRate());
        prefInfo.put("rateTypeCode", ar.getRateTypeCode());

        BigDecimal prefDuty =
            calculateDuty(ar.getadValoremRate(), ar.getSpecificRate(), productValue, quantity);
        prefInfo.put("calculatedDuty", prefDuty);
        prefInfo.put("agreementCode", ar.getAgreement().getAgreementCode());
        prefInfo.put("agreementName", ar.getAgreement().getAgreementName());
        prefInfo.put("eligibilityStatus", "Unknown"); // placeholder; implement real checks

        preferentialRates.add(prefInfo);

        if (prefDuty.compareTo(lowestDuty) < 0) {
          lowestDuty = prefDuty;
          bestProgram = ar.getAgreement().getAgreementName();
        }
      }
      result.put("preferentialRates", preferentialRates);

      // --- Recommendation ---
      Map<String, Object> recommended = new HashMap<>();
      boolean isMfnBest = lowestDuty.compareTo(mfnDuty) == 0;
      recommended.put("rateType", isMfnBest ? "MFN" : "Preferential");
      recommended.put("calculatedDuty", lowestDuty);
      recommended.put("savings", mfnDuty.subtract(lowestDuty));
      recommended.put("programName", isMfnBest ? "MFN" : bestProgram);
      recommended.put(
          "recommendation",
          isMfnBest
              ? "MFN rate is the best available option"
              : ("Use " + bestProgram + " for lowest duty rate"));

      result.put("recommendedRate", recommended);
      result.put("bestTariffAmount", lowestDuty);
      result.put("bestProgramName", bestProgram);

      // --- Applicable programs ---
      List<String> applicablePrograms =
          getApplicableTradePrograms(originCountry, destinationCountry);
      result.put("applicablePrograms", applicablePrograms);

      // --- Compliance notes ---
      List<String> notes = new ArrayList<>();
      if (applicablePrograms.isEmpty()) {
        notes.add("No preferential trade programs available for this product/country combination");
      } else {
        notes.add("ELIGIBLE programs (to be verified): " + String.join(", ", applicablePrograms));
      }
      if (applicablePrograms.stream().anyMatch(p -> p.contains("GSP"))) {
        notes.add("GSP: Verify country eligibility and product requirements");
      }
      if (applicablePrograms.stream().anyMatch(p -> p.contains("USMCA"))) {
        notes.add("USMCA: Verify rules of origin requirements");
      }
      notes.add("Ensure proper documentation for preferential treatment");
      notes.add("Verify country of origin certification requirements");

      // Add tariff date range notes
      if (tariffEffectiveDate != null && tariffExpirationDate != null) {
        notes.add(
            "Tariff rates effective from " + tariffEffectiveDate + " to " + tariffExpirationDate);
      } else if (tariffEffectiveDate != null) {
        notes.add("Tariff rates effective from " + tariffEffectiveDate);
      } else if (tariffExpirationDate != null) {
        notes.add("Tariff rates effective until " + tariffExpirationDate);
      }

      result.put("complianceNotes", notes);

      // --- Totals ---
      BigDecimal totalImportPrice = BigDecimal.valueOf(productValue).add(lowestDuty);
      result.put("totalImportPrice", totalImportPrice);

      return result;
    } catch (Exception e) {
      result.put("error", "Calculation failed: " + e.getMessage());
      return result;
    }
  }

  // ===== Programs =====
  @Override
  public List<String> getApplicableTradePrograms(String originCountry, String destinationCountry) {
    try {
      // Find trade agreements that apply between the two countries
      List<TradeAgreement> agreements =
          tradeAgreementService.getTradeAgreementsBetweenCountries(
              originCountry, destinationCountry);
      List<String> out = new ArrayList<>();
      for (TradeAgreement agreement : agreements) {
        // Only include active agreements
        if (agreement.getEffectiveDate() != null
            && agreement.getEffectiveDate().isBefore(java.time.LocalDate.now())
            && (agreement.getExpirationDate() == null
                || agreement.getExpirationDate().isAfter(java.time.LocalDate.now()))) {
          out.add(agreement.getAgreementCode() + " - " + agreement.getAgreementName());
        }
      }
      return out;
    } catch (Exception e) {
      return new ArrayList<>();
    }
  }

  // ===== Duty math =====
  @Override
  public BigDecimal calculateDuty(
      BigDecimal adValoremRate, BigDecimal specificRate, Double productValue, Integer quantity) {
    BigDecimal duty = BigDecimal.ZERO;
    if (adValoremRate != null && productValue != null) {
      // Assumes adValoremRate is a fraction (e.g., 0.05 for 5%)
      duty = duty.add(adValoremRate.multiply(BigDecimal.valueOf(productValue)));
    }
    if (specificRate != null && quantity != null) {
      duty = duty.add(specificRate.multiply(BigDecimal.valueOf(quantity)));
    }
    return duty.setScale(2, RoundingMode.HALF_UP);
  }

  // ===== Validation =====
  @Override
  public Map<String, Object> validateHtsCode(String htsCode) {
    Map<String, Object> res = new HashMap<>();
    if (htsCode == null || htsCode.isBlank()) {
      res.put("valid", false);
      res.put("message", "HTS code cannot be empty");
      res.put("errorCode", "EMPTY_HTS_CODE");
      return res;
    }
    String cleaned = cleanHtsCode(htsCode);
    if (cleaned.length() != 8) {
      res.put("valid", false);
      res.put("message", "HTS code must be exactly 8 digits");
      res.put("errorCode", "INVALID_FORMAT");
      res.put("providedCode", htsCode);
      res.put("cleanedCode", cleaned);
      return res;
    }
    Optional<Product> product = productRepository.findByHts8(cleaned);
    if (product.isPresent()) {
      res.put("valid", true);
      res.put("message", "Valid HTS code");
      res.put("htsCode", cleaned);
      res.put("productId", product.get().getId());
      Optional<MfnTariffRate> mfnRate = mfnService.getMfnTariffRate(cleaned);
      res.put("hasMfnRate", mfnRate.isPresent());
      mfnRate.ifPresent(
          r -> {
            res.put("mfnRate", r.getMfnadValoremRate());
            res.put("mfnRateType", r.getMfnRateTypeCode());
          });
    } else {
      res.put("valid", false);
      res.put("message", "HTS code not found in database");
      res.put("errorCode", "HTS_CODE_NOT_FOUND");
      res.put("htsCode", cleaned);
      res.put("suggestion", "Verify the HTS code or check if it exists in the tariff database");
    }
    return res;
  }

  // ===== Cost breakdown (uses best recommended, not MFN-only) =====
  @Override
  public Map<String, Object> getCostBreakdown(
      String htsCode, String destinationCountry, Double productValue, Integer quantity) {
    Map<String, Object> res = new HashMap<>();
    try {
      validateInputs(htsCode, productValue, quantity);
      Map<String, Object> calc =
          calculateTariff(htsCode, destinationCountry, productValue, quantity);
      if (calc.containsKey("error")) return calc;

      BigDecimal bestDuty = (BigDecimal) calc.getOrDefault("bestTariffAmount", BigDecimal.ZERO);
      String program = (String) calc.getOrDefault("bestProgramName", "MFN");
      BigDecimal purchase = BigDecimal.valueOf(productValue);
      BigDecimal total = purchase.add(bestDuty);

      res.put("htsCode", cleanHtsCode(htsCode));
      res.put("countryOfOrigin", destinationCountry);
      res.put("quantity", quantity);
      res.put("appliedProgram", program);
      res.put("tariffAmount", bestDuty);
      res.put("purchasePrice", purchase);
      res.put("totalImportPrice", total);

      Map<String, String> pretty = new HashMap<>();
      pretty.put("Purchase Price", formatMoney(purchase));
      pretty.put("Applied Program", program);
      pretty.put("Tariff Amount", formatMoney(bestDuty));
      pretty.put("Total Import Price", formatMoney(total));
      res.put("breakdown", pretty);
      return res;
    } catch (Exception e) {
      res.put("error", "Cost breakdown calculation failed: " + e.getMessage());
      return res;
    }
  }

  // ===== CRUD over TariffCalculation =====
  @Override
  public Optional<TariffCalculation> getTariffCalculationById(Long id) {
    return tariffCalculationRepository.findById(id);
  }

  @Override
  public List<TariffCalculation> getAllTariffCalculations() {
    return tariffCalculationRepository.findAll();
  }

  @Override
  public List<TariffCalculation> getTariffCalculationsByHtsCode(String htsCode) {
    return tariffCalculationRepository.findByHtsCode(cleanHtsCode(htsCode));
  }

  @Override
  public List<TariffCalculation> getTariffCalculationsByCountryCode(String destinationCountry) {
    return tariffCalculationRepository.findByCountryCode(destinationCountry);
  }

  @Override
  @Transactional
  public boolean deleteTariffCalculation(Long id) {
    if (tariffCalculationRepository.existsById(id)) {
      tariffCalculationRepository.deleteById(id);
      return true;
    }
    return false;
  }

  @Override
  @Transactional
  public boolean deleteAllTariffCalculations() {
    tariffCalculationRepository.deleteAll();
    return true;
  }

  // ===== DTO-based create/update =====
  @Override
  @Transactional
  public TariffCalculationResponse createTariffCalculation(TariffCalculationRequest request) {
    Objects.requireNonNull(request, "request");
    Map<String, Object> validation = validateHtsCode(request.getHtsCode());
    if (!(Boolean) validation.getOrDefault("valid", false)) {
      throw new RuntimeException("Invalid HTS code: " + validation.get("message"));
    }

    Map<String, Object> calc;

    // Use tariff date range calculation if dates are provided
    if (request.getTariffEffectiveDate() != null || request.getTariffExpirationDate() != null) {
      calc =
          calculateTariffWithDateRange(
              request.getHtsCode(),
              request.getOriginCountry(),
              request.getDestinationCountry(),
              request.getProductValue().doubleValue(),
              request.getQuantity().intValue(),
              request.getTariffEffectiveDate(),
              request.getTariffExpirationDate());
    } else {
      calc =
          calculateTariff(
              request.getHtsCode(),
              request.getOriginCountry(),
              request.getDestinationCountry(),
              request.getProductValue().doubleValue(),
              request.getQuantity().intValue());
    }

    if (calc.containsKey("error")) {
      throw new RuntimeException("Tariff calculation failed: " + calc.get("error"));
    }

    TariffCalculationResponse response =
        new TariffCalculationResponse(
            cleanHtsCode(request.getHtsCode()),
            null, // productDescription will be set later from database
            request.getOriginCountry(),
            request.getDestinationCountry(),
            request.getProductValue(),
            request.getQuantity(),
            request.getCurrency());
    populateResponseFromCalculationResult(response, calc);

    BigDecimal totalTariffAmount = response.getTotalTariffAmount();
    BigDecimal totalImportPrice = response.getTotalImportPrice();

    TariffCalculation entity =
        new TariffCalculation(
            cleanHtsCode(request.getHtsCode()),
            request.getDestinationCountry(), // countryCode for backward compatibility
            request.getOriginCountry(),
            request.getDestinationCountry(),
            request.getProductValue(),
            request.getQuantity(),
            "STANDARD",
            totalTariffAmount,
            totalImportPrice,
            request.getTariffEffectiveDate(),
            request.getTariffExpirationDate(),
            request.getCurrency());
    tariffCalculationRepository.save(entity);
    return response;
  }

  @Override
  @Transactional
  public TariffCalculationResponse updateTariffCalculation(
      Long id, TariffCalculationRequest request) {
    TariffCalculation existing =
        tariffCalculationRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Tariff calculation not found with ID: " + id));

    Map<String, Object> validation = validateHtsCode(request.getHtsCode());
    if (!(Boolean) validation.getOrDefault("valid", false)) {
      throw new RuntimeException("Invalid HTS code: " + validation.get("message"));
    }

    Map<String, Object> calc =
        calculateTariff(
            request.getHtsCode(),
            request.getDestinationCountry(),
            request.getProductValue().doubleValue(),
            request.getQuantity().intValue());
    if (calc.containsKey("error")) {
      throw new RuntimeException("Tariff calculation failed: " + calc.get("error"));
    }

    TariffCalculationResponse response =
        new TariffCalculationResponse(
            cleanHtsCode(request.getHtsCode()),
            null, // productDescription will be set later from database
            request.getOriginCountry(),
            request.getDestinationCountry(),
            request.getProductValue(),
            request.getQuantity(),
            request.getCurrency());
    populateResponseFromCalculationResult(response, calc);

    BigDecimal totalTariffAmount = response.getTotalTariffAmount();
    BigDecimal totalImportPrice = response.getTotalImportPrice();

    existing.setHtsCode(cleanHtsCode(request.getHtsCode()));
    existing.setCountryCode(request.getDestinationCountry());
    existing.setOriginCountry(request.getOriginCountry());
    existing.setDestinationCountry(request.getDestinationCountry());
    existing.setProductValue(request.getProductValue());
    existing.setQuantity(request.getQuantity());
    existing.setCalculationType("STANDARD");
    existing.setTotalTariffAmount(totalTariffAmount);
    existing.setCalculationResult(totalImportPrice);
    tariffCalculationRepository.save(existing);

    return response;
  }

  // ===== Helpers =====
  private void populateResponseFromCalculationResult(
      TariffCalculationResponse response, Map<String, Object> tariffResult) {

    // ----- 0) Set product description from database -----
    Optional<Product> product = findByHtsCode(response.getHtsCode());
    if (product.isPresent()) {
      response.setProductDescription(product.get().getBriefDescription());
    }

    // ----- 1) Program & label selection (same as before, but we keep label text for parsing) -----
    @SuppressWarnings("unchecked")
    Map<String, Object> recommendedRate = (Map<String, Object>) tariffResult.get("recommendedRate");

    String appliedTextRate = null;
    String programType = "MFN";
    String programName = "MFN";

    if (recommendedRate != null) {
      // Preferential vs MFN labels
      String rateType = (String) recommendedRate.get("rateType");
      if ("Preferential".equalsIgnoreCase(rateType)) {
        programType = "Preferential";
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> preferentialRates =
            (List<Map<String, Object>>) tariffResult.get("preferentialRates");
        if (preferentialRates != null && !preferentialRates.isEmpty()) {
          Map<String, Object> bestPrefRate = preferentialRates.get(0);
          programName = (String) bestPrefRate.getOrDefault("agreementName", "Preferential");
          appliedTextRate = (String) bestPrefRate.get("textRate");
        }
      } else {
        // MFN path
        programType = "MFN";
        programName = "MFN";
        @SuppressWarnings("unchecked")
        Map<String, Object> mfnRate = (Map<String, Object>) tariffResult.get("mfnRate");
        if (mfnRate != null) {
          appliedTextRate = (String) mfnRate.get("textRate");
        }
      }
    }

    response.setProgramType(programType);
    response.setProgramName(programName);
    if (appliedTextRate != null) {
      response.setAppliedRateLabel(appliedTextRate);
    }

    // ----- 2) Compute base amounts using QUANTITY -----
    BigDecimal unitPrice =
        response.getProductValue() != null ? response.getProductValue() : BigDecimal.ZERO;
    int qtyInt = response.getQuantity() != null ? response.getQuantity() : 1;
    BigDecimal qty = BigDecimal.valueOf(qtyInt);

    // Customs base is value per unit * quantity
    BigDecimal customsBase = unitPrice.multiply(qty);

    // ----- 3) Pull numeric rate components (percent, specific, other) -----
    BigDecimal adValoremPercent = BigDecimal.ZERO; // e.g., 10 means 10%
    BigDecimal specificPerUnit = BigDecimal.ZERO; // per-unit specific duty
    BigDecimal otherFlat = BigDecimal.ZERO; // flat per line

    if (recommendedRate != null) {
      // try common keys (support multiple naming variants)
      adValoremPercent =
          firstDecimal(
              recommendedRate,
              "adValoremRatePercent",
              "adValoremRate",
              "percent",
              "pct",
              "ratePercent");

      specificPerUnit =
          firstDecimal(recommendedRate, "specificRatePerUnit", "specificRate", "specific");

      otherFlat = firstDecimal(recommendedRate, "otherFlatCharge", "otherRate", "flatRate");
    }

    // If percent not provided numerically, try parsing from the text label like "10%"
    if ((adValoremPercent == null || adValoremPercent.compareTo(BigDecimal.ZERO) == 0)
        && appliedTextRate != null) {
      adValoremPercent = parsePercentFromLabel(appliedTextRate);
    }
    if (adValoremPercent == null) adValoremPercent = BigDecimal.ZERO;
    if (specificPerUnit == null) specificPerUnit = BigDecimal.ZERO;
    if (otherFlat == null) otherFlat = BigDecimal.ZERO;

    // ----- 4) Duty math (correct with quantity) -----
    BigDecimal pct = adValoremPercent.movePointLeft(2); // 10 -> 0.10
    BigDecimal adValoremDuty = customsBase.multiply(pct); // base * pct
    BigDecimal specificDuty = specificPerUnit.multiply(qty); // per-unit * qty
    BigDecimal otherDuty = otherFlat; // flat

    BigDecimal totalTariff =
        adValoremDuty.add(specificDuty).add(otherDuty).setScale(2, RoundingMode.HALF_UP);

    // If upstream had a calculatedDuty, we ignore it now since we recomputed correctly
    response.setTotalTariffAmount(totalTariff);

    // Savings if provided (optional)
    BigDecimal savings = firstDecimal(recommendedRate, "savings");
    response.setSavingsVsMfn(
        savings != null ? savings.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

    // ----- 5) Total import price = customs base + total tariff (NOT unit price + duty) -----
    BigDecimal totalImportPrice = customsBase.add(totalTariff).setScale(2, RoundingMode.HALF_UP);
    
    // ----- 5.1) Currency conversion -----
    String targetCurrency = response.getCurrency();
    if (targetCurrency != null && !targetCurrency.isEmpty()) {
      // Convert totalImportPrice to target currency
      BigDecimal convertedTotalImportPrice = currencyService.convertCurrency(
          totalImportPrice, "USD", targetCurrency);
      response.setTotalImportPrice(convertedTotalImportPrice);
      
      // Also convert tariff amount to target currency
      BigDecimal convertedTariffAmount = currencyService.convertCurrency(
          totalTariff, "USD", targetCurrency);
      response.setTotalTariffAmount(convertedTariffAmount);
      
      // Convert savings if any
      if (response.getSavingsVsMfn() != null && response.getSavingsVsMfn().compareTo(BigDecimal.ZERO) > 0) {
        BigDecimal convertedSavings = currencyService.convertCurrency(
            response.getSavingsVsMfn(), "USD", targetCurrency);
        response.setSavingsVsMfn(convertedSavings);
      }
    } else {
      response.setTotalImportPrice(totalImportPrice);
    }

    // ----- 6) Applicable agreements & metadata -----
    @SuppressWarnings("unchecked")
    List<String> applicablePrograms = (List<String>) tariffResult.get("applicablePrograms");
    response.setApplicableAgreements(applicablePrograms != null ? applicablePrograms : List.of());

    // Effective date: from tariff calculation result if present, else today
    Object eff = tariffResult.get("tariffEffectiveDate");
    String effectiveDate =
        (eff instanceof String && !((String) eff).isBlank())
            ? (String) eff
            : LocalDate.now().toString();
    response.setEffectiveDate(effectiveDate);

    // Notes
    @SuppressWarnings("unchecked")
    List<String> complianceNotes = (List<String>) tariffResult.get("complianceNotes");
    if (complianceNotes != null && !complianceNotes.isEmpty()) {
      response.setNotes(String.join("; ", complianceNotes));
    } else if (response.getNotes() == null) {
      response.setNotes(
          "Ensure proper documentation for preferential treatment; Verify country of origin certification requirements");
    }

    // If no applied label was set earlier, synthesize one from numeric parts
    if (response.getAppliedRateLabel() == null || response.getAppliedRateLabel().isBlank()) {
      String label;
      if (adValoremPercent.compareTo(BigDecimal.ZERO) == 0
          && specificPerUnit.compareTo(BigDecimal.ZERO) == 0
          && otherFlat.compareTo(BigDecimal.ZERO) == 0) {
        label = "Free";
      } else {
        List<String> parts = new ArrayList<>();
        if (adValoremPercent.compareTo(BigDecimal.ZERO) > 0)
          parts.add(adValoremPercent.stripTrailingZeros().toPlainString() + "%");
        if (specificPerUnit.compareTo(BigDecimal.ZERO) > 0)
          parts.add(specificPerUnit.stripTrailingZeros().toPlainString() + " per unit");
        if (otherFlat.compareTo(BigDecimal.ZERO) > 0)
          parts.add("$" + otherFlat.stripTrailingZeros().toPlainString() + " flat");
        label = String.join(" + ", parts);
      }
      response.setAppliedRateLabel(label);
    }
  }

  /* ===== helpers (put in the same class) ===== */

  private static BigDecimal firstDecimal(Map<String, Object> m, String... keys) {
    if (m == null) return null;
    for (String k : keys) {
      Object v = m.get(k);
      if (v == null) continue;
      if (v instanceof BigDecimal bd) return bd;
      if (v instanceof Number n) return new BigDecimal(n.toString());
      if (v instanceof String s && !s.isBlank()) {
        try {
          return new BigDecimal(s.trim());
        } catch (NumberFormatException ignored) {
        }
      }
    }
    return null;
  }

  private static BigDecimal parsePercentFromLabel(String label) {
    if (label == null) return BigDecimal.ZERO;
    String s = label.trim();
    // e.g., "10%" or "10 %"
    int pctIdx = s.indexOf('%');
    if (pctIdx >= 0) {
      String num = s.substring(0, pctIdx).trim();
      try {
        return new BigDecimal(num);
      } catch (NumberFormatException ignored) {
      }
    }
    return BigDecimal.ZERO;
  }

  private void validateInputs(String htsCode, Double productValue, Integer quantity) {
    if (htsCode == null || htsCode.isBlank()) {
      throw new IllegalArgumentException("HTS code is required");
    }
    if (productValue == null || productValue < 0) {
      throw new IllegalArgumentException("productValue must be ≥ 0");
    }
    if (quantity == null || quantity < 0) {
      throw new IllegalArgumentException("quantity must be ≥ 0");
    }
  }

  private String cleanHtsCode(String htsCode) {
    if (htsCode == null) return "";
    return htsCode.replaceAll("[^0-9]", "");
  }

  private String formatMoney(BigDecimal v) {
    return String.format("$%.2f", v.doubleValue());
  }

  /** Validates that tariff date range is within the valid MFN and agreement rate periods */
  private Map<String, Object> validateTariffDateRange(
      String htsCode,
      java.time.LocalDate tariffEffectiveDate,
      java.time.LocalDate tariffExpirationDate) {
    Map<String, Object> result = new HashMap<>();

    try {
      // Get MFN rate for this product
      Optional<MfnTariffRate> mfnRateOpt = mfnService.getMfnTariffRate(htsCode);
      if (mfnRateOpt.isPresent()) {
        MfnTariffRate mfnRate = mfnRateOpt.get();

        // Check if tariff dates are within MFN rate validity period
        if (tariffEffectiveDate != null && mfnRate.getBeginEffectDate() != null) {
          if (tariffEffectiveDate.isBefore(mfnRate.getBeginEffectDate())) {
            result.put(
                "error",
                "Tariff effective date ("
                    + tariffEffectiveDate
                    + ") is before MFN rate effective date ("
                    + mfnRate.getBeginEffectDate()
                    + ")");
            return result;
          }
        }

        if (tariffExpirationDate != null && mfnRate.getEndEffectiveDate() != null) {
          if (tariffExpirationDate.isAfter(mfnRate.getEndEffectiveDate())) {
            result.put(
                "error",
                "Tariff expiration date ("
                    + tariffExpirationDate
                    + ") is after MFN rate expiration date ("
                    + mfnRate.getEndEffectiveDate()
                    + ")");
            return result;
          }
        }

        // Check if tariff effective date is after MFN expiration date
        if (tariffEffectiveDate != null && mfnRate.getEndEffectiveDate() != null) {
          if (tariffEffectiveDate.isAfter(mfnRate.getEndEffectiveDate())) {
            result.put(
                "error",
                "Tariff effective date ("
                    + tariffEffectiveDate
                    + ") is after MFN rate expiration date ("
                    + mfnRate.getEndEffectiveDate()
                    + ")");
            return result;
          }
        }
      }

      // Get agreement rates for this product and validate against them
      List<AgreementRate> agreementRates =
          productService.getAgreementRates(htsCode, "US"); // Default to US for validation

      for (AgreementRate agreementRate : agreementRates) {
        // Check if tariff dates overlap with agreement rate validity period
        if (tariffEffectiveDate != null && agreementRate.getEffectiveDate() != null) {
          if (tariffEffectiveDate.isBefore(agreementRate.getEffectiveDate())) {
            result.put(
                "warning",
                "Tariff effective date ("
                    + tariffEffectiveDate
                    + ") is before agreement rate effective date ("
                    + agreementRate.getEffectiveDate()
                    + ") for agreement "
                    + agreementRate.getAgreement().getAgreementCode());
            // This is a warning, not an error, so we continue
          }
        }

        if (tariffExpirationDate != null && agreementRate.getExpirationDate() != null) {
          if (tariffExpirationDate.isAfter(agreementRate.getExpirationDate())) {
            result.put(
                "warning",
                "Tariff expiration date ("
                    + tariffExpirationDate
                    + ") is after agreement rate expiration date ("
                    + agreementRate.getExpirationDate()
                    + ") for agreement "
                    + agreementRate.getAgreement().getAgreementCode());
            // This is a warning, not an error, so we continue
          }
        }
      }

      result.put("valid", true);
      return result;

    } catch (Exception e) {
      result.put("error", "Date validation failed: " + e.getMessage());
      return result;
    }
  }
}
